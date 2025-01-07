package com.main.particlesimulator;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Vector;

public class GraphicScene extends Pane{

    public GraphicScene(){
        super();
        setOnMouseReleased(event -> {
            clearCurrentParticle();
        });
        setOnMouseDragged(event -> {
            if (currentParticle != null)
                currentParticle.setPos(event.getSceneX(), event.getSceneY());
        });
    }

    public Vector<Particle> getParticles() {
        return particles;
    }

    public void addParticle(Particle particle){
        particles.add(particle);
        particle.setParent(this);
        getChildren().add(particle);
    }

    public void setCurrentParticle(Particle particle){
        currentParticle = particle;
        currentParticle.setGravityActivity(false);
    }

    public void clearCurrentParticle(){
        if (currentParticle != null) {
            currentParticle.setGravityActivity(true);
            currentParticle = null;
        }
    }

    private final Vector<Particle> particles = new Vector<>();
    private Particle currentParticle = null;
}
