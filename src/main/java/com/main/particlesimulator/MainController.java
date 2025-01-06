package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public void respawnParticle(MouseEvent event) {
        circle.respawn(event.getSceneX(), event.getSceneY());
    }

    @FXML
    private Pane graphicField;

    Particle circle = new Particle(250.0f, 150.0f, 30.f);

    protected AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
                circle.makeMove();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphicField.getChildren().add(circle);

        timer.start();
    }
}
