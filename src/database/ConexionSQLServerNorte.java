package database;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ConexionSQLServerNorte {
    private static final String URL = "jdbc:sqlserver://25.65.4.242:1433;databaseName=ZonaNorte;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    private Connection conexion;

    /**
     * 🔹 Método para conectar a SQL Server (Zona Norte)
     */
    public boolean conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            conexion.setAutoCommit(false); // 🔹 Se desactiva autoCommit para manejar transacciones manualmente.
            System.out.println("✅ Conexión a SQL Server (Zona Norte) establecida.");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a SQL Server (Zona Norte): " + e.getMessage());
            return false;
        }
    }

    /**
     * 🔹 Método para ejecutar consultas `SELECT`
     */
    public DefaultTableModel ejecutarConsulta(String consulta) {
        DefaultTableModel modelo = new DefaultTableModel();

        if (conexion == null) {
            System.err.println("⚠️ ERROR: No hay conexión con SQL Server (Zona Norte).");
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

            System.out.println("✅ Consulta ejecutada correctamente en SQL Server (Zona Norte).");

        } catch (SQLException e) {
            System.err.println("⚠️ Error al ejecutar consulta en SQL Server (Zona Norte): " + e.getMessage());
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
            System.err.println("⚠️ ERROR: No hay conexión con SQL Server (Zona Norte).");
            return false;
        }

        try (Statement stmt = conexion.createStatement()) {
            int filasAfectadas = stmt.executeUpdate(consulta);
            if (filasAfectadas > 0) {
                conexion.commit(); // 🔹 Se confirma la transacción solo si la operación fue exitosa.
                System.out.println("✅ Transacción confirmada en SQL Server (Zona Norte): " + consulta);
                return true;
            } else {
                System.out.println("⚠️ No se modificaron registros en SQL Server (Zona Norte).");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error en transacción de SQL Server (Zona Norte), ejecutando rollback: " + e.getMessage());
            rollback(); // 🔹 Si hay error, se revierte la transacción.
            return false;
        }
    }

    /**
     * 🔹 Método para ejecutar `ROLLBACK`
     */
    public void rollback() {
        try {
            if (conexion != null) {
                conexion.rollback();
                System.err.println("🔄 Rollback ejecutado en SQL Server (Zona Norte).");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al hacer rollback en SQL Server (Zona Norte): " + e.getMessage());
        }
    }
}
