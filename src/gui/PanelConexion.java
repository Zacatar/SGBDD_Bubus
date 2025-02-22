package gui;

import javax.swing.*;

import database.ConexionNeo4j;
import database.ConexionSQLServer;

import java.awt.*;

public class PanelConexion extends JFrame {

    private JButton btnConectarSQL, btnConectarMongo, btnConectarNeo4j;
    private JLabel estadoSQL, estadoMongo, estadoNeo4j;
    private ConexionNeo4j conexionNeo4j;
    private ConexionSQLServer conexionSQL;


    public PanelConexion() {
        setTitle("Gestión de Conexiones");
        setSize(400, 300);
        setLayout(new GridLayout(4, 2, 10, 10));
        setLocationRelativeTo(null);

        btnConectarSQL = new JButton("Conectar a SQL Server");
        btnConectarMongo = new JButton("Conectar a MongoDB");
        btnConectarNeo4j = new JButton("Conectar a Neo4j");

        estadoSQL = new JLabel("Desconectado");
        estadoMongo = new JLabel("Desconectado");
        estadoNeo4j = new JLabel("Desconectado");

        add(btnConectarSQL);
        add(estadoSQL);
        add(btnConectarMongo);
        add(estadoMongo);
        add(btnConectarNeo4j);
        add(estadoNeo4j);

        // Eventos (Simulando conexiones)
        btnConectarSQL.addActionListener(e -> conectarSQL());
        btnConectarMongo.addActionListener(e -> conectarMongo());
        btnConectarNeo4j.addActionListener(e -> conectarNeo4j());
    }

    private void conectarSQL() {

        conexionSQL = new ConexionSQLServer();
        if (conexionSQL.conectar()) {
            estadoSQL.setText("Conectado ✔");
        } else {
            estadoSQL.setText("Error ❌");
        }
    }

    private void conectarMongo() {
        estadoMongo.setText("Conectado ✔");
        /*
        conexionMongo = new ConexionMongoDB();
        if (conexionMongo.conectar()) {
            estadoMongo.setText("Conectado ✔");
        } else {
            estadoMongo.setText("Error ❌");
        }
        */
    }
    
    private void conectarNeo4j() {
    
        conexionNeo4j = new ConexionNeo4j("bolt://localhost:7687", "neo4j", "chilaquilesconpollo123");
        if (conexionNeo4j.conectar()) {
            estadoNeo4j.setText("Conectado ✔");
        } else {
            estadoNeo4j.setText("Error ❌");
        }
        
    }

}
