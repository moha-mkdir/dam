package com.buscaminas;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Arrancamos tu pantalla de inicio
        new PantallaInicio().mostrar(primaryStage);
    }
}