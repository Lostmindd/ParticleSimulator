package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

import java.io.IOException;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    Particle circle = new Particle(250.0f, 150.0f, 30.f);

    Pane rootNode;

    protected AnimationTimer timer = new AnimationTimer(){
//        @Override
//        public void handle(long now) {
//                circle.makeMove();
//
//        }
        private int frameCount = 0;

        @Override
        public void handle(long now) {
            frameCount++;
            if (frameCount % 1 == 0) {
                circle.makeMove();
            }
        }
    };

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(Main.class.getResource("main_window.fxml"));
        rootNode = fmxlLoader.load();
        Scene scene = new Scene(rootNode, 700, 400);
        stage.setTitle("particles simulator");
        stage.setScene(scene);
        stage.show();


        for (Node node : rootNode.getChildren()) {
            if (node instanceof Pane)
                ((Pane)node).getChildren().add(circle);
        }

        timer.start();
    }
}
