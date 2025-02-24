package database;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ConexionSQLServer {
    private static final String URL = "jdbc:sqlserver://25.5.185.106:1433;databaseName=Empresa_Sur;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    private Connection conexion;

    public boolean conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a SQL Server: " + e.getMessage());
            return false;
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    public DefaultTableModel ejecutarConsulta(String consulta) {
        DefaultTableModel modelo = new DefaultTableModel();

        if (conexion == null) {
            System.err.println("⚠️ ERROR: La conexión a SQL Server no está establecida.");
            return modelo;
        }

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnas = metaData.getColumnCount();

            Vector<String> nombresColumnas = new Vector<>();
            for (int i = 1; i <= columnas; i++) {
                nombresColumnas.add(metaData.getColumnName(i));
            }
            modelo.setColumnIdentifiers(nombresColumnas);

            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                for (int i = 1; i <= columnas; i++) {
                    fila.add(rs.getObject(i));
                }
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            System.err.println("⚠️ Error al ejecutar consulta en SQL Server: " + e.getMessage());
        }

        return modelo;
    }
}
