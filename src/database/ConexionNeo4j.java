package database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

public class ConexionNeo4j {
    private String uri;
    private String user;
    private String password;
    private static final String DEFAULT_DATABASE = "ZonaSur";

    private Driver driver;

    // Constructor parametrizado
    public ConexionNeo4j(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;
    }

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
    
    public Session getSession() {
        return driver.session(SessionConfig.forDatabase(DEFAULT_DATABASE));
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
