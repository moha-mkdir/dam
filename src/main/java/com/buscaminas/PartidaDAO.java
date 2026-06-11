package com.buscaminas;

import java.sql.*;

public class PartidaDAO {
    public int crearPartida(int idUsuario, String dificultad) {
        String sql = "INSERT INTO PARTIDA (id_usuario, dificultad, resultado) VALUES (?, ?, 'En curso')";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setString(2, dificultad);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void guardarCelda(int idPartida, int fila, int columna, String tipo, int numero) {
        String sqlCelda = "INSERT INTO CELDA (id_partida, fila, columna, estado_visibilidad) VALUES (?, ?, ?, 'Tapada')";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sqlCelda)) {
            pstmt.setInt(1, idPartida);
            pstmt.setInt(2, fila);
            pstmt.setInt(3, columna);
            pstmt.executeUpdate();
            
            String sqlHija = "";
            if (tipo.equals("MINA")) sqlHija = "INSERT INTO MINA (id_partida, fila, columna) VALUES (?, ?, ?)";
            else if (tipo.equals("NUMERO")) sqlHija = "INSERT INTO NUMERO (id_partida, fila, columna, numero) VALUES (?, ?, ?, ?)";
            else if (tipo.equals("VACIO")) sqlHija = "INSERT INTO VACIO (id_partida, fila, columna) VALUES (?, ?, ?)";
            
            if (!sqlHija.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlHija)) {
                    ps.setInt(1, idPartida); ps.setInt(2, fila); ps.setInt(3, columna);
                    if (tipo.equals("NUMERO")) ps.setInt(4, numero);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void actualizarVisibilidad(int idPartida, int fila, int columna, String estado) {
        String sql = "UPDATE CELDA SET estado_visibilidad = ? WHERE id_partida = ? AND fila = ? AND columna = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            pstmt.setInt(2, idPartida);
            pstmt.setInt(3, fila);
            pstmt.setInt(4, columna);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void actualizarResultado(int idPartida, String resultado) {
        String sql = "UPDATE PARTIDA SET resultado = ? WHERE id_partida = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resultado);
            pstmt.setInt(2, idPartida);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}