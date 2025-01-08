package com.main.particlesimulator;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.Vector;

public class GraphicScene extends Pane{

    public GraphicScene(){
        super();
        setOnMouseReleased(event -> {
            releaseParticle();
        });
        setOnMouseDragged(event -> {
            if (currentParticle != null) {
                currentParticle.setPos(event.getSceneX(), event.getSceneY());
            }
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

    public void grabParticle(Particle particle){
        currentParticle = particle;
        currentParticle.reset();
        currentParticle.setMovementState(true);
    }

    public void releaseParticle(){
        if (currentParticle != null) {
            currentParticle.addForce(new double[]{currentParticle.getCenterX() - currentParticle.getPrevPos()[0],
                    -currentParticle.getCenterY() + currentParticle.getPrevPos()[1]});
            currentParticle.setMovementState(false);
            currentParticle = null;
        }
    }

    private final Vector<Particle> particles = new Vector<>();
    private Particle currentParticle = null;
}
