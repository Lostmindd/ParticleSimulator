package com.main.particlesimulator;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private GraphicScene graphicScene;
    @FXML
    private Slider gravitySlider;
    @FXML
    private Slider resistanceSlider;
    @FXML
    private Slider sizeSlider;
    @FXML
    private Slider massSlider;
    @FXML
    private ColorPicker colorPicker;

    public double getCurrentParticleSize(){
        return sizeSlider.getValue();
    }

    public double getCurrentParticleMass(){
        return massSlider.getValue();
    }

    public Color getCurrentParticleColor(){
        return colorPicker.getValue();
    }

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
        graphicScene.setMainController(this);
        gravitySlider.valueProperty().addListener((changed, oldValue, newValue) -> Particle.gravity[1] = -(double) newValue);
        resistanceSlider.valueProperty().addListener((changed, oldValue, newValue) -> Particle.simulationDragCoefficient = (double) newValue/100);

        timer.start();
    }
}
