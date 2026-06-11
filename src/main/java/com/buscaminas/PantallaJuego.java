package com.buscaminas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaJuego {
    private int filas, cols, minas, banderasColocadas;
    private Button[][] btns;
    private boolean[][] esMina;
    private int[][] vecinos;
    private int casillasPorRevelar;
    private int idPartida, segundos = 0;
    private Label lblCronometro, lblBanderas;
    private Timeline timeline;

    public PantallaJuego(int idUsuario, int f, int c, int m, String diff) {
        this.filas = f; this.cols = c; this.minas = m;
        this.casillasPorRevelar = (f * c) - m;
        this.banderasColocadas = 0;
        this.btns = new Button[f][c];
        this.esMina = new boolean[f][c];
        this.vecinos = new int[f][c];
        
        // Creamos la partida en BD
        this.idPartida = new PartidaDAO().crearPartida(idUsuario, diff);
    }

    public void mostrar(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        // Panel superior
        HBox topBox = new HBox(30);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 20, 0));
        
        Label lblMinas = new Label("Minas totales: " + minas);
        lblMinas.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        lblBanderas = new Label("Banderas: 🚩 " + minas);
        lblBanderas.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        lblCronometro = new Label("⏱️ 00:00");
        lblCronometro.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        topBox.getChildren().addAll(lblMinas, lblBanderas, lblCronometro);
        root.setTop(topBox);

        // Tablero
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        generarTablero(grid, stage);
        root.setCenter(grid);

        iniciarCronometro();

        // GUARDADO INICIAL EN BD 
        new Thread(() -> {
            PartidaDAO dao = new PartidaDAO();
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < cols; j++) {
                    String tipo = esMina[i][j] ? "MINA" : (vecinos[i][j] > 0 ? "NUMERO" : "VACIO");
                    dao.guardarCelda(idPartida, i, j, tipo, vecinos[i][j]);
                }
            }
        }).start();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene(); // Ajusta la ventana al tamaño de la cuadrícula
        stage.centerOnScreen();
    }

    private void generarTablero(GridPane grid, Stage stage) {
        // 1. Colocar minas
        int mCont = 0;
        while(mCont < minas) {
            int r = (int)(Math.random() * filas), c = (int)(Math.random() * cols);
            if(!esMina[r][c]) { esMina[r][c] = true; mCont++; }
        }

        // 2. Crear botones
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                Button b = new Button();
                b.setPrefSize(40, 40); // Botones cuadrados
                b.setStyle("-fx-background-radius: 0; -fx-border-color: #999; -fx-background-color: #F0F0F0; -fx-font-size: 16px;");
                
                int r = i, c = j;
                b.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) gestionarBandera(r, c);
                    else if (e.getButton() == MouseButton.PRIMARY) gestionarClick(r, c, stage);
                });

                btns[i][j] = b;
                grid.add(b, j, i);
            }
        }
        
        // 3. Calcular números vecinos
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                if (esMina[i][j]) continue;
                int cuenta = 0;
                for (int x = i-1; x <= i+1; x++) 
                    for (int y = j-1; y <= j+1; y++)
                        if (x>=0 && x<filas && y>=0 && y<cols && esMina[x][y]) cuenta++;
                vecinos[i][j] = cuenta;
            }
        }
    }

    private void iniciarCronometro() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            segundos++;
            int m = segundos / 60, s = segundos % 60;
            lblCronometro.setText(String.format("⏱️ %02d:%02d", m, s));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void gestionarBandera(int r, int c) {
        if (btns[r][c].isDisabled()) return;

        PartidaDAO dao = new PartidaDAO();
        if (btns[r][c].getText().equals("🚩")) {
            btns[r][c].setText("");
            banderasColocadas--;
            dao.actualizarVisibilidad(idPartida, r, c, "Tapada");
        } else {
            btns[r][c].setText("🚩");
            banderasColocadas++;
            dao.actualizarVisibilidad(idPartida, r, c, "Bandera");
        }
        lblBanderas.setText("Banderas: 🚩 " + (minas - banderasColocadas));
    }

    private void gestionarClick(int r, int c, Stage stage) {
        if (btns[r][c].isDisabled() || btns[r][c].getText().equals("🚩")) return;

        new PartidaDAO().actualizarVisibilidad(idPartida, r, c, "Descubierta");

        if (esMina[r][c]) {
            // Pisaste una mina
            btns[r][c].setText("💣");
            btns[r][c].setStyle("-fx-background-color: #FF4444; -fx-background-radius: 0; -fx-border-color: #999; -fx-font-size: 18px;");
            revelarTodasLasMinas();
            finalizarJuego(stage, "¡Derrota!");
        } else {
            // Revelar en cascada
            revelarCeldaCascada(r, c);
            if (casillasPorRevelar == 0) {
                finalizarJuego(stage, "¡Victoria!");
            }
        }
    }

    private void revelarCeldaCascada(int r, int c) {
        if (btns[r][c].isDisabled() || btns[r][c].getText().equals("🚩")) return;

        btns[r][c].setDisable(true);
        // Estilo de casilla hundida/revelada
        btns[r][c].setStyle("-fx-background-color: #DDDDDD; -fx-opacity: 1; -fx-border-color: #BBBBBB; -fx-background-radius: 0; -fx-font-weight: bold; -fx-font-size: 18px;");
        casillasPorRevelar--;

        if (vecinos[r][c] > 0) {
            btns[r][c].setText(String.valueOf(vecinos[r][c]));
          
            btns[r][c].setStyle(btns[r][c].getStyle() + "-fx-text-fill: " + obtenerColorNumero(vecinos[r][c]) + ";");
        } else {
            // Si es 0, buscar vecinos recursivamente
            for (int i = r - 1; i <= r + 1; i++) {
                for (int j = c - 1; j <= c + 1; j++) {
                    if (i >= 0 && i < filas && j >= 0 && j < cols && !btns[i][j].isDisabled()) {
                        new PartidaDAO().actualizarVisibilidad(idPartida, i, j, "Descubierta");
                        revelarCeldaCascada(i, j);
                    }
                }
            }
        }
    }

    private void revelarTodasLasMinas() {
        for(int i = 0; i < filas; i++) {
            for(int j = 0; j < cols; j++) {
                if (esMina[i][j] && !btns[i][j].getText().equals("🚩")) {
                    btns[i][j].setText("💣");
                    btns[i][j].setStyle("-fx-background-color: #FFAAAA; -fx-opacity: 1; -fx-border-color: #999; -fx-font-size: 18px;");
                } else if (!esMina[i][j] && btns[i][j].getText().equals("🚩")) {
                    btns[i][j].setText("❌"); 
                }
            }
        }
    }

    private String obtenerColorNumero(int n) {
        switch(n) {
            case 1: return "blue";
            case 2: return "green";
            case 3: return "red";
            case 4: return "darkblue";
            case 5: return "darkred";
            case 6: return "darkcyan";
            case 7: return "black";
            case 8: return "dimgray";
            default: return "black";
        }
    }

    private void finalizarJuego(Stage stage, String resultadoFinal) {
        if (timeline != null) timeline.stop();
        new PartidaDAO().actualizarResultado(idPartida, resultadoFinal.contains("Derrota") ? "Derrota" : "Victoria");
        
// tiempo para resultado final
        new Thread(() -> {
            try { Thread.sleep(4000); } catch (InterruptedException e) {} 
            Platform.runLater(() -> new PantallaResultado(resultadoFinal, segundos).mostrar(stage));
        }).start();
    }
}