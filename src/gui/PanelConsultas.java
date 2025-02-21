package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import controller.GestorDatos;
import java.awt.*;

public class PanelConsultas extends JFrame {

    private JTextArea txtConsulta;
    private JButton btnEjecutar, btnLimpiar;
    private JTable tablaResultados;
    private JScrollPane scrollResultados;
    private DefaultListModel<String> modeloHistorial;
    private JList<String> listaHistorial;
    private GestorDatos gestorDatos;

    public PanelConsultas() {
        setTitle("Ejecutar Consultas");
        setSize(700, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        gestorDatos = new GestorDatos();

        txtConsulta = new JTextArea(3, 40);
        btnEjecutar = new JButton("Ejecutar Consulta");
        btnLimpiar = new JButton("Limpiar");

        // Historial de consultas
        modeloHistorial = new DefaultListModel<>();
        listaHistorial = new JList<>(modeloHistorial);
        JScrollPane scrollHistorial = new JScrollPane(listaHistorial);

        // Tabla de resultados (inicialmente vacía)
        tablaResultados = new JTable();
        scrollResultados = new JScrollPane(tablaResultados);

        JPanel panelInput = new JPanel();
        panelInput.setLayout(new BorderLayout());
        panelInput.add(new JScrollPane(txtConsulta), BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnEjecutar);
        panelBotones.add(btnLimpiar);
        
        panelInput.add(panelBotones, BorderLayout.SOUTH);

        add(panelInput, BorderLayout.NORTH);
        add(scrollResultados, BorderLayout.CENTER);
        add(scrollHistorial, BorderLayout.EAST);

        // Eventos
        btnEjecutar.addActionListener(e -> ejecutarConsulta());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        listaHistorial.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                txtConsulta.setText(listaHistorial.getSelectedValue());
            }
        });
    }

    private void ejecutarConsulta() {
        String consulta = txtConsulta.getText().trim();
        if (consulta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La consulta no puede estar vacía", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ahora el controlador maneja la consulta y devuelve el modelo de la tabla
        DefaultTableModel modelo = gestorDatos.ejecutarConsulta(consulta);
        tablaResultados.setModel(modelo);

        // Guardar en historial
        modeloHistorial.addElement(consulta);
    }

    private void limpiarCampos() {
        txtConsulta.setText("");
        tablaResultados.setModel(new DefaultTableModel());
    }
}
