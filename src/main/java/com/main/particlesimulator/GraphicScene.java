package com.main.particlesimulator;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class GraphicScene extends Pane{

    public GraphicScene(){
        super();
        // при отпускании мыши добавляет вектор последнего движения и отпускает частицу
        setOnMouseReleased(event -> {
            currentParticle.addForce(new double[]{event.getSceneX() - currentParticle.getPrevPos()[0],
                    -event.getSceneY() + currentParticle.getPrevPos()[1]});
            releaseParticle();
        });
        // при передвижении зажатой мыши передвигает частицу, если она схвачена
        setOnMouseDragged(event -> {
            if (currentParticle != null)
                currentParticle.setPos(event.getSceneX(), event.getSceneY());
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

    // Отпускает частицу
    public void releaseParticle(){
        if (currentParticle != null) {
            currentParticle.setMovementState(false);
            currentParticle = null;
        }
    }

    private final Vector<Particle> particles = new Vector<>();
    private Particle currentParticle = null;
}
