package database;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
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
    /**
     * 🔹 Ejecutar consultas `SELECT` en Neo4j (Cypher)
     */
    /**
     * 🔹 Ejecutar consultas `SELECT` en Neo4j (Cypher)
     */
    public DefaultTableModel ejecutarConsulta(String consultaSQL) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new String[]{"IdCliente", "Nombre", "Estado", "Credito", "Deuda"});

        // 🔹 Transformar SQL a Cypher
        String consultaCypher = transformarSQLaCypher(consultaSQL);

        // 🔹 Validar consulta vacía
        if (consultaCypher.isEmpty()) {
            System.err.println("⚠️ No se ejecutará una consulta vacía en Neo4j. Verifica la conversión de SQL a Cypher.");
            return modelo; // Retorna un modelo vacío
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


    private String transformarSQLaCypher(String consultaSQL) {
        consultaSQL = consultaSQL.trim().toLowerCase();

        if (consultaSQL.startsWith("select") && consultaSQL.contains("clientes")) {
            return "MATCH (c:Cliente) RETURN c.IdCliente AS IdCliente, c.Nombre AS Nombre, c.Estado AS Estado, c.Credito AS Credito, c.Deuda AS Deuda";
        }

        System.err.println("⚠️ Consulta SQL no reconocida para transformación a Cypher.");
        return "";
    }


    /**
     * 🔹 Ejecutar `INSERT` en Neo4j
     */
    public boolean ejecutarInsert(String consulta) {
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE))) {
            session.writeTransaction(tx -> tx.run(consulta));
            System.out.println("✅ INSERT ejecutado en Neo4j correctamente.");
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error en `INSERT` en Neo4j: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Ejecutar `UPDATE` en Neo4j
     */
    public boolean ejecutarUpdate(String consulta) {
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE))) {
            session.writeTransaction(tx -> tx.run(consulta));
            System.out.println("✅ UPDATE ejecutado en Neo4j correctamente.");
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error en `UPDATE` en Neo4j: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Ejecutar `DELETE` en Neo4j
     */
    public boolean ejecutarDelete(String consulta) {
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE))) {
            session.writeTransaction(tx -> tx.run(consulta));
            System.out.println("✅ DELETE ejecutado en Neo4j correctamente.");
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error en `DELETE` en Neo4j: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Obtener driver
     */
    public Driver getDriver() {
        return driver;
    }
}
