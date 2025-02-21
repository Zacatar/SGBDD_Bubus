package controller;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class GestorDatos {

    private ArrayList<String> historialConsultas;

    public GestorDatos() {
        historialConsultas = new ArrayList<>();
    }

    public DefaultTableModel ejecutarConsulta(String consulta) {
        // Simulación de estructura dinámica de la tabla según la consulta
        String[] columnas;
        Object[][] datos;

        if (consulta.toLowerCase().contains("select * from productos")) {
            columnas = new String[]{"ID", "Nombre", "Precio"};
            datos = new Object[][]{
                    {1, "Producto A", 100.0},
                    {2, "Producto B", 200.0},
                    {3, "Producto C", 150.5}
            };
        } else if (consulta.toLowerCase().contains("select * from clientes")) {
            columnas = new String[]{"ID", "Nombre", "Email"};
            datos = new Object[][]{
                    {1, "Juan Pérez", "juan@example.com"},
                    {2, "María López", "maria@example.com"},
                    {3, "Carlos Díaz", "carlos@example.com"}
            };
        } else {
            columnas = new String[]{"Resultado"};
            datos = new Object[][]{
                    {"Consulta ejecutada correctamente"}
            };
        }

        // Guardar en historial
        historialConsultas.add(consulta);

        return new DefaultTableModel(datos, columnas);
    }

    public ArrayList<String> getHistorialConsultas() {
        return historialConsultas;
    }
}
