package com.buscaminas;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PantallaInicio {

    public void mostrar(Stage stage) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: white;");

        Label titulo = new Label("DAMmines");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 48));

        VBox userBox = new VBox(10);
        userBox.setAlignment(Pos.CENTER);
        Label userLabel = new Label("Nombre de usuario");
        userLabel.setTextFill(Color.GRAY);
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Introduce tu nombre");
        txtNombre.setMaxWidth(250);
        txtNombre.setStyle("-fx-background-radius: 0; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
        userBox.getChildren().addAll(userLabel, txtNombre);

        HBox diffBox = new HBox(20);
        diffBox.setAlignment(Pos.CENTER);
        ToggleGroup group = new ToggleGroup();
        RadioButton rbFacil = new RadioButton("Fácil"); rbFacil.setToggleGroup(group); rbFacil.setSelected(true);
        RadioButton rbMedio = new RadioButton("Medio"); rbMedio.setToggleGroup(group);
        RadioButton rbDificil = new RadioButton("Difícil"); rbDificil.setToggleGroup(group);
        diffBox.getChildren().addAll(rbFacil, rbMedio, rbDificil);

        Button btnJugar = new Button("Jugar");
        btnJugar.setPrefSize(120, 50);
        btnJugar.setStyle("-fx-background-color: #E6E6E6; -fx-border-color: black; -fx-font-weight: bold;");

        btnJugar.setOnAction(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) return;

            int idUsuario = new UsuarioDAO().registrarUsuario(nombre);
            int f = 12, c = 10, m = 10;
            String diff = "Fácil";
            
            if (rbMedio.isSelected()) { f = 16; c = 16; m = 40; diff = "Medio"; }
            else if (rbDificil.isSelected()) { f = 16; c = 30; m = 99; diff = "Difícil"; }

            new PantallaJuego(idUsuario, f, c, m, diff).mostrar(stage);
        });

        root.getChildren().addAll(titulo, userBox, diffBox, btnJugar);
        Scene scene = new Scene(root, 500, 600);
        stage.setTitle("DAMmines - Inicio");
        stage.setScene(scene);
        stage.show();
    }
}