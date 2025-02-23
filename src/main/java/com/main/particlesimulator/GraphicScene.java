package com.main.particlesimulator;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class GraphicScene extends Pane{
    private final Vector<Particle> particles = new Vector<>();
    private Particle currentParticle = null;
    private MainController mainController = null;
    public GraphicScene(){
        super();
        // при отпускании мыши отпускает частицу
        setOnMouseReleased(event -> {
            releaseParticle(event.getSceneX(), event.getSceneY());
        });
        // при передвижении зажатой мыши передвигает частицу, если она схвачена
        setOnMouseDragged(event -> {
                    if (currentParticle != null)
                        currentParticle.setPos(event.getSceneX(), event.getSceneY());
        });
        // создает новыю частицу
        setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY && mainController != null){
                addParticle(new Particle((float) event.getSceneX(), (float) event.getSceneY(), (float) mainController.getCurrentParticleSize(),
                        mainController.getCurrentParticleMass(), mainController.getCurrentParticleColor()));
            }
        });
    }

    // Возвращает список частиц сцены
    public List<Particle> getParticles() {
        return Collections.unmodifiableList(particles);
    }

    // Добавляет частицу на сцену
    public void addParticle(Particle particle){
        particles.add(particle);
        particle.setParent(this);
        getChildren().add(particle);
    }

    // Хватает частицу частицу
    public void grabParticle(Particle particle){
        currentParticle = particle;
        currentParticle.reset();
        currentParticle.setMovementState(true);
    }

    // Отпускает частицу и добавляет вектор последнего движения к ее ипульсу
    public void releaseParticle(double lastPosX, double lastPosY){
        if (currentParticle != null) {
            currentParticle.addForce(new double[]{lastPosX - currentParticle.getPrevPos()[0],
                    -lastPosY + currentParticle.getPrevPos()[1]});
            currentParticle.setMovementState(false);
            currentParticle = null;
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}