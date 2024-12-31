package com.main.particlesimulator;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Particle extends Circle {

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);
    }

    public Node checkCollisions() {
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node != this && node.getBoundsInParent().intersects(getBoundsInParent()))
                return node;
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
        //System.out.println(momentum[1]);
        calculateResistance();

//        double[] currentMomentum = getc;

        setCenterY(getCenterY() - momentum[1]);
        setCenterX(getCenterX() + momentum[0]);

        Node node = checkCollisions();

        if (node != null) {
//            double k = bounds.getCenterY()/bounds.getCenterX();
//            setCenterX(getCenterX() + getRadius());
//            setCenterY(bounds.getMinY() - getRadius());

//            double k1 = momentum[1]/momentum[0];
//            double k2 = bounds.getMaxY()/bounds.getMaxX();
//
//            if (momentum[1] < 0) {
////                setCenterY(getCenterY() - getRadius() * (k / sqrt(1 + k * k)));
////                setCenterX(getCenterX() - getRadius() * (1 / sqrt(1 + k * k)));
//            }
//            else if (momentum[1] > 0) {
//                setCenterY(bounds.getCenterX()*k + getRadius() * (k / sqrt(1 + k * k)));
//                setCenterX(bounds.getCenterY()/k + getRadius() * (1 / sqrt(1 + k * k)));
//            }
//            setCenterY(getCenterY() + momentum[1]);
//            setCenterX(getCenterX() - momentum[0]);
            Line line = (Line) node;
            correctCirclePosition(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
//            if (momentum[1] < 0)
//                setCenterY(bounds.getMinY() - getRadius());
//            else if (momentum[1] > 0)
//                setCenterY(bounds.getMaxY() + getRadius());
//            if (momentum[0] > 0)
//                setCenterX(bounds.getMinX() - getRadius());
//            else if (momentum[0] < 0)
//                setCenterX(bounds.getMaxX() + getRadius());
        }
    }

    private void correctCirclePosition(double x1, double y1, double x2, double y2) {
        double xC = getCenterX();
        double yC = getCenterY();

        double A = y2-y1;
        double B = x1-x2;
        double C = x2 * y1 - x1 * y2;

        double distance = Math.abs(A * xC + B * yC + C) / Math.sqrt(A * A + B * B);


        System.out.println(distance);
        if (distance <= getRadius()) {
            double t = -(A * xC + B * yC + C) / (A * A + B * B);
            double xP = xC + t * A;
            double yP = yC + t * B;

            System.out.println("+");
            double correction = getRadius() - distance;
            double normalLength = Math.sqrt(A * A + B * B);
            double xCorrection = correction * (A / normalLength);
            double yCorrection = correction * (B / normalLength);

            setCenterX(xP + xCorrection);
            setCenterY(yP + yCorrection);
        }
    }

    public void makeReboundMove() {
        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
            return;

        momentum[0] = -momentum[0];
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

    private double[] momentum = {3,3};
    private final double dragCoefficient = 0.01;
    private double elasticityCoefficient = 0.01;
    private final double elasticityCoefficientStep = 0.05;



}
