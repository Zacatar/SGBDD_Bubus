package controller;

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

    public GestorDatos() {
        historialConsultas = new ArrayList<>();
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
            return ejecutarInsertDistribuido(consulta);
        } else if (consulta.toLowerCase().startsWith("update")) {
            return ejecutarUpdateDistribuido(consulta);
        } else if (consulta.toLowerCase().startsWith("delete")) {
            return ejecutarDeleteDistribuido(consulta);
        }

        historialConsultas.add(consulta);
        return new DefaultTableModel();
    }

    private DefaultTableModel ejecutarSelectDistribuido(String consulta) {
        List<DefaultTableModel> resultados = new ArrayList<>();

        DefaultTableModel sqlCentroResult = conexionSQLCentro.ejecutarConsulta(consulta);
        if (sqlCentroResult != null && sqlCentroResult.getRowCount() > 0) {
            resultados.add(sqlCentroResult);
            System.out.println("📌 Registros obtenidos de SQL Server (Zona Centro): " + sqlCentroResult.getRowCount());
        }

        DefaultTableModel sqlNorteResult = conexionSQLNorte.ejecutarConsulta(consulta);
        if (sqlNorteResult != null && sqlNorteResult.getRowCount() > 0) {
            resultados.add(sqlNorteResult);
            System.out.println("📌 Registros obtenidos de SQL Server (Zona Norte): " + sqlNorteResult.getRowCount());
        }

        String consultaCypher = convertirSQLaCypher(consulta);
        if (!consultaCypher.isEmpty()) {
            DefaultTableModel neo4jResult = conexionNeo4j.ejecutarConsulta(consultaCypher);
            if (neo4jResult != null && neo4jResult.getRowCount() > 0) {
                resultados.add(neo4jResult);
                System.out.println("📌 Registros obtenidos de Neo4j: " + neo4jResult.getRowCount());
            }
        }

        return unirResultados(resultados);
    }

    private DefaultTableModel ejecutarInsertDistribuido(String consulta) {
        boolean exitoCentro = conexionSQLCentro.ejecutarInsert(consulta);
        boolean exitoNorte = conexionSQLNorte.ejecutarInsert(consulta);

        String consultaCypher = convertirSQLaCypher(consulta);
        boolean exitoNeo4j = !consultaCypher.isEmpty() && conexionNeo4j.ejecutarInsert(consultaCypher);

        return resultadoModelo(exitoCentro || exitoNorte || exitoNeo4j, "INSERT");
    }

    private DefaultTableModel ejecutarUpdateDistribuido(String consulta) {
        boolean exitoCentro = conexionSQLCentro.ejecutarUpdate(consulta);
        boolean exitoNorte = conexionSQLNorte.ejecutarUpdate(consulta);

        String consultaCypher = convertirSQLaCypher(consulta);
        boolean exitoNeo4j = !consultaCypher.isEmpty() && conexionNeo4j.ejecutarUpdate(consultaCypher);

        return resultadoModelo(exitoCentro || exitoNorte || exitoNeo4j, "UPDATE");
    }

    private DefaultTableModel ejecutarDeleteDistribuido(String consulta) {
        boolean exitoCentro = conexionSQLCentro.ejecutarDelete(consulta);
        boolean exitoNorte = conexionSQLNorte.ejecutarDelete(consulta);

        String consultaCypher = convertirSQLaCypher(consulta);
        boolean exitoNeo4j = !consultaCypher.isEmpty() && conexionNeo4j.ejecutarDelete(consultaCypher);

        return resultadoModelo(exitoCentro || exitoNorte || exitoNeo4j, "DELETE");
    }

    private String convertirSQLaCypher(String consultaSQL) {
        consultaSQL = consultaSQL.trim().toLowerCase(); // Normalizamos a minúsculas

        if (consultaSQL.endsWith(";")) {
            consultaSQL = consultaSQL.substring(0, consultaSQL.length() - 1);
        }

        System.out.println("🔍 Transformando SQL a Cypher: " + consultaSQL);

        // 🔹 Transformar SELECT
        if (consultaSQL.equals("select * from clientes")) {
            return "MATCH (c:Cliente) RETURN c.IdCliente AS IdCliente, c.Nombre AS Nombre, c.Estado AS Estado, c.Credito AS Credito, c.Deuda AS Deuda";
        }

        // 🔹 Transformar INSERT (se debe mejorar para manejar valores dinámicos)
        else if (consultaSQL.startsWith("insert into clientes")) {
            return consultaSQL
                    .replace("insert into clientes", "CREATE (c:Cliente {")
                    .replace("values", "})")
                    .replace("(", "{")
                    .replace(")", "}");
        }

        // 🔹 Transformar UPDATE
        else if (consultaSQL.startsWith("update clientes set")) {
            return consultaSQL
                    .replace("update clientes set", "MATCH (c:Cliente) SET")
                    .replace("where", "WHERE c.");
        }

        // 🔹 Transformar DELETE
        else if (consultaSQL.startsWith("delete from clientes where")) {
            return consultaSQL
                    .replace("delete from clientes where", "MATCH (c:Cliente) WHERE c.")
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
}
