package com.buscaminas;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PantallaResultado {
    private String res;
    private int tiempo;

    public PantallaResultado(String res, int tiempo) {
        this.res = res; this.tiempo = tiempo;
    }

    public void mostrar(Stage stage) {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        Label lblRes = new Label(res);
        lblRes.setFont(new Font(48));
        lblRes.setStyle(res.equals("¡Victoria!") ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        Label lblTime = new Label("Tiempo final: " + (tiempo/60) + ":" + (tiempo%60));

        Button btn = new Button("Volver al inicio");
        btn.setOnAction(e -> new PantallaInicio().mostrar(stage));

        root.getChildren().addAll(lblRes, lblTime, btn);
        stage.setScene(new Scene(root, 400, 450));
    }
}