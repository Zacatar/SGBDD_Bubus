package controller;

import database.ConexionSQLServer;
import database.ConexionMongoDB;
import database.ConexionNeo4j;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class GestorDatos {
    private ConexionSQLServer conexionSQL;
    private ConexionMongoDB conexionMongo;
    private ConexionNeo4j conexionNeo4j;
    private ArrayList<String> historialConsultas;

    public GestorDatos() {
        historialConsultas = new ArrayList<>();
        this.conexionSQL = new ConexionSQLServer();
        this.conexionMongo = new ConexionMongoDB();
        this.conexionNeo4j = new ConexionNeo4j("bolt://localhost:7687", "neo4j", "chilaquilesconpollo123");

        if (!conexionSQL.conectar()) {
            System.err.println("❌ Error al conectar a SQL Server.");
        }
        if (!conexionMongo.conectar()) {
            System.err.println("❌ Error al conectar a MongoDB.");
        }
        if (!conexionNeo4j.conectar()) {
            System.err.println("❌ Error al conectar a Neo4j.");
        }
    }

    public DefaultTableModel ejecutarConsulta(String consulta) {
        DefaultTableModel modelo = new DefaultTableModel();

        if (consulta.toLowerCase().startsWith("select")) {
            modelo = conexionSQL.ejecutarConsulta(consulta);
        } else if (consulta.toLowerCase().startsWith("insert")) {
            boolean exito = conexionSQL.ejecutarInsert(consulta);
            modelo.setColumnIdentifiers(new String[]{"Resultado"});
            modelo.addRow(new Object[]{exito ? "✅ INSERT realizado" : "⚠️ Error en INSERT"});
        } else if (consulta.toLowerCase().startsWith("update")) {
            boolean exito = conexionSQL.ejecutarUpdate(consulta);
            modelo.setColumnIdentifiers(new String[]{"Resultado"});
            modelo.addRow(new Object[]{exito ? "✅ UPDATE realizado" : "⚠️ Error en UPDATE"});
        } else if (consulta.toLowerCase().startsWith("delete")) {
            boolean exito = conexionSQL.ejecutarDelete(consulta);
            modelo.setColumnIdentifiers(new String[]{"Resultado"});
            modelo.addRow(new Object[]{exito ? "✅ DELETE realizado" : "⚠️ Error en DELETE"});
        }

        historialConsultas.add(consulta);
        return modelo;
    }

    public ArrayList<String> getHistorialConsultas() {
        return historialConsultas;
    }
}
