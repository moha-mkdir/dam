// Archivo: src/main/java/com/buscaminas/UsuarioDAO.java
package com.buscaminas;

import java.sql.*;

public class UsuarioDAO {
    public int registrarUsuario(String nombre) {
        String sql = "INSERT INTO usuario (nombre) VALUES (?)";
        int idGenerado = -1;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    idGenerado = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
        return idGenerado;
    }
}