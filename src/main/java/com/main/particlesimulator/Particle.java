package com.main.particlesimulator;

import javafx.scene.shape.Circle;

public class Particle extends Circle {

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);
    }


    public double momentum = 0;
    public int momentumAngle = 0;
    public double momentumLoss = 0.2;


}
