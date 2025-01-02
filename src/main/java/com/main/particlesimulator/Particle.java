package com.main.particlesimulator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.lang.reflect.Array;

import static java.lang.Math.*;

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
            Line line = (Line) node;
            Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
            Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

            correctCirclePosition(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
        }
    }

    private  void correctCirclePosition(double x1, double y1, double x2, double y2) {
        double xCircle = getCenterX();
        double yCircle = getCenterY();

        double A = y2-y1;
        double B = x1-x2;
        double C = x2 * y1 - x1 * y2;

        double t = -(A * xCircle + B * yCircle + C) / (A * A + B * B);
        double xCircleProjection = xCircle + t * A;
        double yCircleProjection = yCircle + t * B;

        double offsetSign = signum(A * xCircle + B * yCircle + C);

        setCenterX(xCircleProjection + offsetSign * getRadius() * (A / Math.sqrt(A * A + B * B)));
        setCenterY(yCircleProjection + offsetSign * getRadius() * (B / Math.sqrt(A * A + B * B)));
    }

    public void makeReboundMove() {
        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
            return;

        double temp = momentum[0];
        momentum[0] = momentum[1];
        momentum[1] = -temp;

//
//        double A = getCenterY();
//        double B = -getCenterX();
//        double C = getCenterX() - getCenterY();
//        double normal0 =  A / Math.sqrt(A * A + B * B);
//        double normal1 =  B / Math.sqrt(A * A + B * B);
//
//        double temp = momentum[0];
//        momentum[0] = momentum[1] * normal0;
//        momentum[1] = temp * normal1;


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

    private double[] momentum = {3, 3};
    private final double dragCoefficient = 0.01;
    private double elasticityCoefficient = 0.01;
    private final double elasticityCoefficientStep = 0.05;



}
