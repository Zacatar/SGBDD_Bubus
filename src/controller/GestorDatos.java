package controller;

import java.util.concurrent.*;
import database.ConexionSQLServer;
import database.ConexionSQLServerNorte;
import database.ConexionNeo4j;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class GestorDatos {
    private final ConexionSQLServer conexionSQLCentro;
    private final ConexionSQLServerNorte conexionSQLNorte;
    private final ConexionNeo4j conexionNeo4j;
    private final ArrayList<String> historialConsultas;
    private final ExecutorService executor;

    public GestorDatos() {
        historialConsultas = new ArrayList<>();
        executor = Executors.newFixedThreadPool(3); // Pool de hilos para manejar transacciones concurrentes.

        this.conexionSQLCentro = new ConexionSQLServer();
        this.conexionSQLNorte = new ConexionSQLServerNorte();
        this.conexionNeo4j = new ConexionNeo4j("bolt://25.3.62.48:7687", "neo4j", "chilaquilesconpollo123");

        if (!conexionSQLCentro.conectar()) {
            System.err.println("❌ Error al conectar a SQL Server (Zona Centro).");
        }
        if (!conexionSQLNorte.conectar()) {
            System.err.println("❌ Error al conectar a SQL Server (Zona Norte).");
        }
        if (!conexionNeo4j.conectar()) {
            System.err.println("❌ Error al conectar a Neo4j.");
        }
    }

    public DefaultTableModel ejecutarConsulta(String consulta) {
        if (consulta.toLowerCase().startsWith("select")) {
            return ejecutarSelectDistribuido(consulta);
        } else if (consulta.toLowerCase().startsWith("insert")) {
            return ejecutarTransaccionDistribuida(consulta, "INSERT");
        } else if (consulta.toLowerCase().startsWith("update")) {
            return ejecutarTransaccionDistribuida(consulta, "UPDATE");
        } else if (consulta.toLowerCase().startsWith("delete")) {
            return ejecutarTransaccionDistribuida(consulta, "DELETE");
        }

        historialConsultas.add(consulta);
        return new DefaultTableModel();
    }

    private DefaultTableModel ejecutarSelectDistribuido(String consulta) {
        List<Future<DefaultTableModel>> resultadosFuturos = new ArrayList<>();

        resultadosFuturos.add(executor.submit(() -> conexionSQLCentro.ejecutarConsulta(consulta)));
        resultadosFuturos.add(executor.submit(() -> conexionSQLNorte.ejecutarConsulta(consulta)));
        resultadosFuturos.add(executor.submit(() -> {
            String consultaCypher = convertirSQLaCypher(consulta);
            return !consultaCypher.isEmpty() ? conexionNeo4j.ejecutarConsulta(consultaCypher) : new DefaultTableModel();
        }));

        return unirResultados(obtenerResultados(resultadosFuturos));
    }

    private DefaultTableModel ejecutarTransaccionDistribuida(String consulta, String tipoOperacion) {
        List<Future<Boolean>> resultadosFuturos = new ArrayList<>();

        resultadosFuturos.add(executor.submit(() -> conexionSQLCentro.ejecutarTransaccion(consulta)));
        resultadosFuturos.add(executor.submit(() -> conexionSQLNorte.ejecutarTransaccion(consulta)));
        resultadosFuturos.add(executor.submit(() -> {
            String consultaCypher = convertirSQLaCypher(consulta);
            return !consultaCypher.isEmpty() && conexionNeo4j.ejecutarTransaccion(consultaCypher);
        }));

        boolean exito = validarTransaccion(resultadosFuturos);
        return resultadoModelo(exito, tipoOperacion);
    }

    private boolean validarTransaccion(List<Future<Boolean>> resultadosFuturos) {
        boolean exito = true;
        for (Future<Boolean> future : resultadosFuturos) {
            try {
                if (!future.get()) { // Si alguna transacción falla, se revierte todo.
                    exito = false;
                }
            } catch (InterruptedException | ExecutionException e) {
                exito = false;
                System.err.println("❌ Error en transacción distribuida: " + e.getMessage());
            }
        }
        return exito;
    }

    private List<DefaultTableModel> obtenerResultados(List<Future<DefaultTableModel>> resultadosFuturos) {
        List<DefaultTableModel> resultados = new ArrayList<>();
        for (Future<DefaultTableModel> future : resultadosFuturos) {
            try {
                DefaultTableModel resultado = future.get();
                if (resultado != null && resultado.getRowCount() > 0) {
                    resultados.add(resultado);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("❌ Error al obtener resultados de consulta distribuida: " + e.getMessage());
            }
        }
        return resultados;
    }

    private String convertirSQLaCypher(String consultaSQL) {
        consultaSQL = consultaSQL.trim().toLowerCase();

        if (consultaSQL.endsWith(";")) {
            consultaSQL = consultaSQL.substring(0, consultaSQL.length() - 1);
        }

        System.out.println("🔍 Transformando SQL a Cypher: " + consultaSQL);

        if (consultaSQL.equals("select * from clientes")) {
            return "MATCH (c:Cliente) RETURN c.IdCliente AS IdCliente, c.Nombre AS Nombre, c.Estado AS Estado, c.Credito AS Credito, c.Deuda AS Deuda";
        } else if (consultaSQL.startsWith("insert into clientes")) {
            return consultaSQL.replace("insert into clientes", "CREATE (c:Cliente {")
                              .replace("values", "})")
                              .replace("(", "{")
                              .replace(")", "}");
        } else if (consultaSQL.startsWith("update clientes set")) {
            return consultaSQL.replace("update clientes set", "MATCH (c:Cliente) SET")
                              .replace("where", "WHERE c.");
        } else if (consultaSQL.startsWith("delete from clientes where")) {
            return consultaSQL.replace("delete from clientes where", "MATCH (c:Cliente) WHERE c.")
                              .concat(" DETACH DELETE c");
        }

        System.err.println("⚠️ Consulta SQL no reconocida para transformación a Cypher: " + consultaSQL);
        return "";
    }

    private DefaultTableModel unirResultados(List<DefaultTableModel> resultados) {
        DefaultTableModel modeloUnificado = new DefaultTableModel();
        modeloUnificado.setColumnIdentifiers(new String[]{"IdCliente", "Nombre", "Estado", "Credito", "Deuda"});

        for (DefaultTableModel resultado : resultados) {
            for (int i = 0; i < resultado.getRowCount(); i++) {
                modeloUnificado.addRow(resultado.getDataVector().elementAt(i));
            }
        }
        return modeloUnificado;
    }

    private DefaultTableModel resultadoModelo(boolean exito, String operacion) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new String[]{"Resultado"});
        modelo.addRow(new Object[]{exito ? "✅ " + operacion + " realizado" : "⚠️ Error en " + operacion});
        return modelo;
    }

    public void cerrar() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
