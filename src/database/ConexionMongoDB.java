package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConexionMongoDB {
    private static final String URI = "mongodb://<IP_HAMACHI>:27017";
    private static final String DATABASE_NAME = "Empresa";

    private MongoClient mongoClient;
    private MongoDatabase database;

    public boolean conectar() {
        try {
            mongoClient = MongoClients.create(URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("✅ Conexión a MongoDB exitosa.");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al conectar a MongoDB: " + e.getMessage());
            return false;
        }
    }

    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("🔴 Conexión a MongoDB cerrada.");
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
