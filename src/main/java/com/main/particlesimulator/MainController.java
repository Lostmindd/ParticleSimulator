package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GraphicScene graphicScene;

    @FXML
    private Slider gravitySlider;
    @FXML
    private Slider resistanceSlider;

    Particle particle = new Particle(200.0f, 150.0f, 100);
    Particle particle2 = new Particle(400.0f, 340.0f, 31.f);
    Particle particle3 = new Particle(400.0f, 340.0f, 31.f);

    // Таймер обновления положения объектов сцены
    protected AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            for (Particle particle : graphicScene.getParticles()) {
                particle.makeMove();
//                System.out.println( particle.getMomentum()[0] + "  |  " +  particle.getMomentum()[1]);
            }
        }
    };

    // Инициализация
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gravitySlider.valueProperty().addListener((changed, oldValue, newValue) -> Particle.gravity[1] = -(double) newValue);
        resistanceSlider.valueProperty().addListener((changed, oldValue, newValue) -> Particle.simulationDragCoefficient = (double) newValue/100);

        graphicScene.addParticle(particle);
        graphicScene.addParticle(particle2);
        graphicScene.addParticle(particle3);
        timer.start();
    }
}
