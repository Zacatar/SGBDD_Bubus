package database;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class ConexionNeo4j {
    private String uri;
    private String user;
    private String password;
    private static final String DEFAULT_DATABASE = "zonacentro"; // Nombre de la BD en Neo4j
    private Driver driver;

    // Constructor
    public ConexionNeo4j(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
    }

    /**
     * 🔹 Conectar a Neo4j
     */
    public boolean conectar() {
        try {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
            System.out.println("✅ Conexión a Neo4j exitosa.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al conectar a Neo4j: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Cerrar conexión
     */
    public void cerrarConexion() {
        if (driver != null) {
            driver.close();
            System.out.println("🔴 Conexión a Neo4j cerrada.");
        }
    }

    /**
     * 🔹 Ejecutar consultas `SELECT` en Neo4j (Cypher)
     */
    public DefaultTableModel ejecutarConsulta(String consultaSQL) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new String[]{"IdCliente", "Nombre", "Estado", "Credito", "Deuda"});

        String consultaCypher = transformarSQLaCypher(consultaSQL);
        if (consultaCypher.isEmpty()) {
            System.err.println("⚠️ No se ejecutará una consulta vacía en Neo4j.");
            return modelo;
        }

        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE))) {
            Result result = session.run(consultaCypher);

            if (!result.hasNext()) {
                System.out.println("⚠️ No se obtuvieron registros de Neo4j.");
            } else {
                System.out.println("✅ Se obtuvieron registros de Neo4j.");
            }

            while (result.hasNext()) {
                Record row = result.next();
                modelo.addRow(new Object[]{
                        row.get("IdCliente").asInt(),
                        row.get("Nombre").asString(),
                        row.get("Estado").asString(),
                        row.get("Credito").asDouble(),
                        row.get("Deuda").asDouble()
                });
            }

            System.out.println("✅ Consulta ejecutada correctamente en Neo4j.");

        } catch (Exception e) {
            System.err.println("⚠️ Error al ejecutar consulta en Neo4j: " + e.getMessage());
        }

        return modelo;
    }

    /**
     * 🔹 Transformar SQL a Cypher
     */
    private String transformarSQLaCypher(String consultaSQL) {
        consultaSQL = consultaSQL.trim().toLowerCase();

        if (consultaSQL.startsWith("select") && consultaSQL.contains("clientes")) {
            return "MATCH (c:Cliente) RETURN c.IdCliente AS IdCliente, c.Nombre AS Nombre, c.Estado AS Estado, c.Credito AS Credito, c.Deuda AS Deuda";
        }

        System.err.println("⚠️ Consulta SQL no reconocida para transformación a Cypher.");
        return "";
    }

    /**
     * 🔹 Ejecutar `INSERT` en Neo4j con transacción
     */
    public boolean ejecutarInsert(String consulta) {
        return ejecutarTransaccion(consulta, "INSERT");
    }

    /**
     * 🔹 Ejecutar `UPDATE` en Neo4j con transacción
     */
    public boolean ejecutarUpdate(String consulta) {
        return ejecutarTransaccion(consulta, "UPDATE");
    }

    /**
     * 🔹 Ejecutar `DELETE` en Neo4j con transacción
     */
    public boolean ejecutarDelete(String consulta) {
        return ejecutarTransaccion(consulta, "DELETE");
    }

    /**
     * 🔹 Ejecutar `INSERT`, `UPDATE`, `DELETE` con control de `COMMIT` y `ROLLBACK`
     */
    private boolean ejecutarTransaccion(String consulta, String operacion) {
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE))) {
            return session.writeTransaction(tx -> {
                try {
                    tx.run(consulta);
                    tx.commit(); // 🔹 Confirmar transacción
                    System.out.println("✅ " + operacion + " ejecutado en Neo4j correctamente.");
                    return true;
                } catch (Exception e) {
                    tx.rollback(); // 🔹 Si hay error, revertir la transacción
                    System.err.println("⚠️ Error en `" + operacion + "` en Neo4j, ejecutando rollback: " + e.getMessage());
                    return false;
                }
            });
        } catch (Exception e) {
            System.err.println("❌ Error en transacción de Neo4j: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Método para ejecutar `ROLLBACK` manualmente
     */
    public void rollback() {
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE))) {
            session.writeTransaction(tx -> {
                tx.rollback();
                System.err.println("🔄 Rollback ejecutado en Neo4j.");
                return null;
            });
        } catch (Exception e) {
            System.err.println("❌ Error al hacer rollback en Neo4j: " + e.getMessage());
        }
    }

    /**
     * 🔹 Obtener driver
     */
    public Driver getDriver() {
        return driver;
    }
}
