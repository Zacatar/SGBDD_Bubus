package database;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ConexionSQLServer {
    private static final String URL = "jdbc:sqlserver://25.5.185.106:1433;databaseName=Empresa_Sur;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    private Connection conexion;

    /**
     * 🔹 Método para conectar a SQL Server
     */
    public boolean conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            conexion.setAutoCommit(true); // 🔹 Asegura que todas las consultas se confirmen automáticamente
            System.out.println("✅ Conexión a SQL Server establecida.");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a SQL Server: " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Método para ejecutar consultas `SELECT`
     */
    public DefaultTableModel ejecutarConsulta(String consulta) {
        DefaultTableModel modelo = new DefaultTableModel();

        if (conexion == null) {
            System.err.println("⚠️ ERROR: No hay conexión con SQL Server.");
            return modelo;
        }

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            // Obtener metadatos de la consulta (nombres de columnas)
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

            System.out.println("✅ Consulta ejecutada correctamente en SQL Server.");

        } catch (SQLException e) {
            System.err.println("⚠️ Error al ejecutar consulta en SQL Server: " + e.getMessage());
        }

        return modelo;
    }

    /**
     * 🔹 Método para ejecutar `INSERT`
     */
    public boolean ejecutarInsert(String consulta) {
        return ejecutarModificacion(consulta);
    }

    /**
     * 🔹 Método para ejecutar `UPDATE`
     */
    public boolean ejecutarUpdate(String consulta) {
        if (!consulta.toLowerCase().contains("where")) {
            System.err.println("⚠️ ERROR: `UPDATE` sin `WHERE` no está permitido.");
            return false;
        }
        return ejecutarModificacion(consulta);
    }

    /**
     * 🔹 Método para ejecutar `DELETE`
     */
    public boolean ejecutarDelete(String consulta) {
        if (!consulta.toLowerCase().contains("where")) {
            System.err.println("⚠️ ERROR: `DELETE` sin `WHERE` no está permitido.");
            return false;
        }
        return ejecutarModificacion(consulta);
    }

    /**
     * 🔹 Método privado para `INSERT`, `UPDATE`, `DELETE`
     */
    private boolean ejecutarModificacion(String consulta) {
        if (conexion == null) {
            System.err.println("⚠️ ERROR: No hay conexión con SQL Server.");
            return false;
        }

        try (Statement stmt = conexion.createStatement()) {
            int filasAfectadas = stmt.executeUpdate(consulta);
            if (filasAfectadas > 0) {
                System.out.println("✅ Modificación realizada correctamente: " + consulta);
                return true;
            } else {
                System.out.println("⚠️ No se modificaron registros.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al ejecutar modificación en SQL Server: " + e.getMessage());
            return false;
        }
    }
}
