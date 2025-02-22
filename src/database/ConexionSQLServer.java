package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQLServer {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=ZonaSur;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";  
    private static final String PASSWORD = "123456789";

    private Connection conexion;

    public boolean conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión a SQL Server exitosa.");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a SQL Server: " + e.getMessage());
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("🔴 Conexión a SQL Server cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
        }
    }

    public Connection getConexion() {
        return conexion;
    }
}
