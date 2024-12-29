package com.main.particlesimulator;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

import static java.lang.Math.abs;

public class Particle extends Circle {

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);
    }

    public Bounds checkCollisions() {
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node != this && node.getBoundsInParent().intersects(getBoundsInParent()))
                return node.localToScene(node.getBoundsInLocal());
        }
        return null;
    }

    public void addForce(double[] force) {
        momentum[0] += force[0];
        momentum[1] += force[1];
    }

    public void makeMove() {
        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
            return;
        System.out.println(momentum[1]);
        calculateResistance();

//        double[] currentMomentum = getc;

        setCenterY(getCenterY() - momentum[1]);
        setCenterX(getCenterX() - momentum[0]);

        Bounds bounds = checkCollisions();

        if (bounds != null) {
            if (momentum[1] < 0)
                setCenterY(bounds.getMinY() - getRadius());
            else
                setCenterY(bounds.getMaxY() + getRadius());
        }
    }

    public void makeReboundMove() {
        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
            return;

        momentum[1] = -momentum[1];
        momentum[0] -= momentum[0] * elasticityCoefficient;
        momentum[1] -= momentum[1] * elasticityCoefficient;
        elasticityCoefficient += elasticityCoefficientStep;
        //circle.addForce(gravity);
        makeMove();
    }

    public void calculateResistance() {
            momentum[0] -= momentum[0] * dragCoefficient;
            momentum[1] -= momentum[1] * dragCoefficient;
    }

    private double[] momentum = {0,0};
    private final double dragCoefficient = 0.01;
    private double elasticityCoefficient = 0.01;
    private final double elasticityCoefficientStep = 0.05;



}
