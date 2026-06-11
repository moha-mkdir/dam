package com.buscaminas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // URL corregida con las dos barras '//' y parámetros de compatibilidad
private static final String URL = "jdbc:mysql://localhost:3306/dammines";    private static final String USUARIO = "root";
    private static final String PASS = ""; 

    public static Connection conectar() throws SQLException {
        try {
            // Carga explícita del driver para MySQL 8
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver de MySQL en el proyecto.");
            throw new SQLException("Driver no disponible", e);
        }
        return DriverManager.getConnection(URL, USUARIO, PASS);
    }
}