package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    Particle circle = new Particle(250.0f, 150.0f, 30.f);
    private final double[] gravity = {0,-6};

    protected AnimationTimer timer = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if(circle.getCenterY() < 600-60) {
                circle.addForce(gravity);
                circle.makeMove();

            } else {
                   circle.momentum[1] = -circle.momentum[1];
                circle.momentum[0] -= circle.momentum[0] * circle.elasticityСoefficient;
                circle.momentum[1] -= circle.momentum[1] * circle.elasticityСoefficient;
                circle.elasticityСoefficient += 0.01;
                   //circle.addForce(gravity);
                    circle.makeMove();
            }
        }
    };

    @Override
    public void start(Stage stage) {
        // set title for the stage
        stage.setTitle("particle");

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
