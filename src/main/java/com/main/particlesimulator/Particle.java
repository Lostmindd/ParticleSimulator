package com.main.particlesimulator;

import javafx.scene.shape.Circle;

import static java.lang.Math.abs;

public class Particle extends Circle {

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);
    }

    public void addForce(double[] force) {
        momentum[0] += force[0];
        momentum[1] += force[1];
    }

    public void makeMove() {
        if (abs(momentum[0]) < 0.05 && abs(momentum[1]) < 0.05)
            return;
        System.out.println(momentum[1]);
        calculateResistance();
        setCenterY(getCenterY() - momentum[1]);
        setCenterX(getCenterX() - momentum[0]);
    }

    public void makeReboundMove() {
        if (abs(momentum[0]) < 0.05 && abs(momentum[1]) < 0.05)
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
    private final double elasticityCoefficientStep = 0.01;



}
