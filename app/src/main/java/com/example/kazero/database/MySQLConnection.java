package com.example.kazero.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    // IMPORTANTE: Reemplaza estos valores con tus credenciales de Railway
    private static final String HOST = "tramway.proxy.rlwy.net"; // ejemplo: containers-us-west-123.railway.app
    private static final String PORT = "3306"; // puerto por defecto de MySQL
    private static final String DATABASE = "kazero_db";
    private static final String USER = "root"; // usuario de Railway
    private static final String PASSWORD = "TjaZprBUqjXhNXihY"; // contraseña de Railway

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión exitosa a MySQL");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de conexión a MySQL: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}