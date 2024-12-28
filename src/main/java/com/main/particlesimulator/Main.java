package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    Particle circle = new Particle(250.0f, 150.0f, 30.f);
    private final double gravity = 0.5;

    protected AnimationTimer timer = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if(circle.getCenterY() < 600-30) {
                if (circle.momentumAngle == 0)
                    circle.setCenterY(circle.getCenterY() + circle.momentum);
                else
                    circle.setCenterY(circle.getCenterY() - circle.momentum);

                if (circle.momentum<20) {
                    if (circle.momentumAngle == 0)
                        circle.momentum += gravity;
                    else
                        circle.momentum -= gravity;
                }

            }
            else {
                circle.momentum *= 1 - circle.momentumLoss;
                circle.momentumLoss -= circle.momentumLoss*0.1;
                circle.momentum += gravity;
                System.out.println(circle.momentum);
                circle.momentumAngle = (circle.momentumAngle+180)%360;
                if (circle.momentumAngle == 0)
                    circle.setCenterY(circle.getCenterY() + circle.momentum);
                else
                    circle.setCenterY(circle.getCenterY() - circle.momentum);
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
