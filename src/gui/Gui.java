package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui extends JFrame {

    private JButton btnConectarSQL, btnConectarMongo, btnConectarNeo4j, btnSalir;

    public Gui() {
        setTitle("SGBDD - Gestor de Bases de Datos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 10, 10));

        btnConectarSQL = new JButton("Conectar a SQL Server");
        btnConectarMongo = new JButton("Conectar a MongoDB");
        btnConectarNeo4j = new JButton("Conectar a Neo4j");
        btnSalir = new JButton("Salir");

        add(btnConectarSQL);
        add(btnConectarMongo);
        add(btnConectarNeo4j);
        add(btnSalir);

        // Acción del botón salir
        btnSalir.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Gui ventana = new Gui();
            ventana.setVisible(true);
        });
    }
}