package com.main.particlesimulator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.lang.reflect.Array;
import java.util.Vector;

import static java.lang.Math.*;

public class Particle extends Circle {

    static private final double[] gravity = {0,-3};

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);
    }

    public Vector<Node> checkCollisions() {
        Vector<Node> nodes = new Vector<Node>();
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node != this && node.getBoundsInParent().intersects(getBoundsInParent()))
                nodes.add(0, node);
        }
        return nodes;
    }

    public void addForce(double[] force) {
        momentum[0] += force[0];
        momentum[1] += force[1];
    }

    public void makeMove() {
        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
            return;
        addForce(gravity);
        //System.out.println(momentum[0] + "  |  " + momentum[1]);
        calculateResistance();

//        double[] currentMomentum = getc;

        setCenterY(getCenterY() - momentum[1]);
        setCenterX(getCenterX() + momentum[0]);

        Vector<Node> nodes = checkCollisions();

        for (Node node : nodes) {
            Line line = (Line) node;
            Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
            Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

            correctCirclePosition(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
            makeReboundMove(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
        }
    }

    private  void correctCirclePosition(double x1, double y1, double x2, double y2) {
//        System.out.println(momentum[1]);
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

    public void makeReboundMove(double x1, double y1, double x2, double y2) {
//        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
//            return;

        double A = y2-y1;
        double B = x1-x2;
        double[] lineNormal = {A / Math.sqrt(A * A + B * B), B / Math.sqrt(A * A + B * B)};
        double dotProduct = lineNormal[0] * momentum[0] + lineNormal[1] * momentum[1];
        momentum[0] = momentum[0] - 2 * dotProduct * lineNormal[0];
        momentum[1] = momentum[1] - 2 * dotProduct * lineNormal[1];

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

        if (elasticityCoefficient + elasticityCoefficientStep < 1)
            elasticityCoefficient += elasticityCoefficientStep;
        else
            elasticityCoefficient = 1;
        //circle.addForce(gravity);



//        makeMove();
    }

    public void calculateResistance() {
            momentum[0] -= momentum[0] * dragCoefficient;
            momentum[1] -= momentum[1] * dragCoefficient;
    }

    private double[] momentum = {-10, 3};
    private final double dragCoefficient = 0.01;
    private double elasticityCoefficient = 0.01;
    private final double elasticityCoefficientStep = 0.005;



}
