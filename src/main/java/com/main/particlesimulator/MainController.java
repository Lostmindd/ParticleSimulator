package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GraphicScene graphicScene;

    Particle particle = new Particle(250.0f, 150.0f, 30.f);
    Particle particle2 = new Particle(350.0f, 300.0f, 31.f);

    // Таймер обновления положения объектов сцены
    protected AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (Particle particle : graphicScene.getParticles()) {
                particle.makeMove();
            }
        }
    };

    // Инициализация
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphicScene.addParticle(particle);
        graphicScene.addParticle(particle2);
        timer.start();
    }
}
