package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_window.fxml"));
        Pane rootNode = fxmlLoader.load();
        Scene scene = new Scene(rootNode, 800, 500);
        stage.setResizable(false);
        stage.setTitle("Particles Simulator");
        stage.setScene(scene);
        stage.show();
    }
}
