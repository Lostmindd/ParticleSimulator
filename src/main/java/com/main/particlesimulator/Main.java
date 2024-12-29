package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
    private final double[] gravity = {0,-6};

    protected AnimationTimer timer = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if(circle.getCenterY() < 400-60) {
                circle.addForce(gravity);
                circle.makeMove();

            } else {
                circle.makeReboundMove();
            }
        }
    };

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fmxlLoader = new FXMLLoader(Main.class.getResource("main_window.fxml"));
        Parent root = fmxlLoader.load();
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("particles simulator");
        stage.setScene(scene);
        stage.show();

        ((Pane) root).getChildren().add(circle);
        //root.getChildrenUnmodifiable().add(circle);
        //((Group) scene.getRoot()).getChildren().add(circle);

        timer.start();
    }
}
