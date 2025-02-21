package gui;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private JButton btnConexion, btnConsultas, btnSalir;

    public VentanaPrincipal() {
        setTitle("SGBDD - Sistema de Gestión de Bases de Datos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        btnConexion = new JButton("Gestión de Conexiones");
        btnConsultas = new JButton("Ejecutar Consultas");
        btnSalir = new JButton("Salir");

        add(btnConexion);
        add(btnConsultas);
        add(btnSalir);

        // Eventos
        btnConexion.addActionListener(e -> abrirPanelConexion());
        btnConsultas.addActionListener(e -> abrirPanelConsultas());
        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void abrirPanelConexion() {
        PanelConexion conexion = new PanelConexion();
        conexion.setVisible(true);
    }

    private void abrirPanelConsultas() {
        PanelConsultas consultas = new PanelConsultas();
        consultas.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
