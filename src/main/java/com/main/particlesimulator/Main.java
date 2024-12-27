package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    Circle circle = new Circle(250.0f, 150.0f, 30.f);

    protected AnimationTimer timer = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if(circle.getCenterY() < 600-30)
                circle.setCenterY(circle.getCenterY()+10);
        }
    };

    @Override
    public void start(Stage stage) {
        // set title for the stage
        stage.setTitle("creating circle");

        // create a Group
        Group group = new Group(circle);

        // create a scene
        Scene scene = new Scene(group, 500, 600);

        // set the scene
        stage.setScene(scene);

        stage.show();

        timer.start();
    }
}
