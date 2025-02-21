package database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class ConexionNeo4j {
    private static final String URI = "bolt://<IP_HAMACHI>:7687";
    private static final String USER = "neo4j";
    private static final String PASSWORD = "tu_contraseña";

    private Driver driver;

    public boolean conectar() {
        try {
            driver = GraphDatabase.driver(URI, AuthTokens.basic(USER, PASSWORD));
            System.out.println("✅ Conexión a Neo4j exitosa.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al conectar a Neo4j: " + e.getMessage());
            return false;
        }
    }

    public void cerrarConexion() {
        if (driver != null) {
            driver.close();
            System.out.println("🔴 Conexión a Neo4j cerrada.");
        }
    }

    public Driver getDriver() {
        return driver;
    }
}
