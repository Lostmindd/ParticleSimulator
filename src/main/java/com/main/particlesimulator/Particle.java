package com.main.particlesimulator;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.Vector;

import static java.lang.Math.*;

public class Particle extends Circle {

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);
        setOnMousePressed(event -> {
            if (parent != null)
                parent.setCurrentParticle(this);
        });
    }

    public void setPos(double x, double y){
        setCenterX(x);
        setCenterY(y);
        momentum[0] = 0;
        momentum[1] = 1;
    }

    // Возвращает все коллизии для текущей частицы
    public Vector<Node> getCollisions() {
        Vector<Node> nodes = new Vector<>();
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node != this && node.getBoundsInParent().intersects(getBoundsInParent()))
                nodes.add(0, node);
        }
        return nodes;
    }

    // Добавляет переданную силу к импульсу
    public void addForce(double[] force) {
        momentum[0] += force[0];
        momentum[1] += force[1];
    }

    // Добавляет сопротивление к импульсу
    public void calculateResistance() {
        momentum[0] -= momentum[0] * dragCoefficient;
        momentum[1] -= momentum[1] * dragCoefficient;
    }

    // Двигает шар по направлению импульса
    public void makeMove() {
        if(isGravityActive)
            addForce(gravity);
//        if (abs(momentum[0]) < 0.05 && abs(momentum[1]) < 0.05)
//            return;
        //System.out.println(momentum[0] + "  |  " + momentum[1]);
        calculateResistance();

        setCenterY(getCenterY() - Math.max(-getRadius(), Math.min(momentum[1], getRadius())));
        setCenterX(getCenterX() + Math.max(-getRadius(), Math.min(momentum[0], getRadius())));

        Vector<Node> collisions = getCollisions();

        // при столкновении корректирует положение частицы и отражает вектор
        for (Node node : collisions) {
            Line line = (Line)node;
            Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
            Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

            correctPosition(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
            reflectVector(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
            //break;
        }
    }

    // Корректирует положение таким образом, чтобы частица не пересекала тело, с которым столкнулось
    private  void correctPosition(double x1, double y1, double x2, double y2) {
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

    // Отражает вектор
    private void reflectVector(double x1, double y1, double x2, double y2) {
        double A = y2-y1;
        double B = x1-x2;
        double[] lineNormal = {A / Math.sqrt(A * A + B * B), B / Math.sqrt(A * A + B * B)};
        double dotProduct = lineNormal[0] * momentum[0] + lineNormal[1] * momentum[1];

        momentum[0] = momentum[0] - 2 * dotProduct * lineNormal[0];
        momentum[1] = momentum[1] - 2 * dotProduct * lineNormal[1];

        momentum[0] -= momentum[0] * elasticityCoefficient;
        momentum[1] -= momentum[1] * elasticityCoefficient;

        if (elasticityCoefficient + elasticityCoefficientStep < 1) {
            elasticityCoefficient += elasticityCoefficientStep;
            elasticityCoefficientStep *= 1.1;
        }
        else
            elasticityCoefficient = 1;
    }

    public void setParent(GraphicScene parent) {
        this.parent = parent;
    }

    public void setGravityActivity(boolean isGravityActive){
        this.isGravityActive = isGravityActive;
    }

    private boolean isGravityActive = true;
    private double[] momentum = {0, 1};
    private final double dragCoefficient = 0.01;
    private double elasticityCoefficient = 0.0005;
    private double elasticityCoefficientStep = 0.00005;
    static private final double[] gravity = {0,-2};
    private GraphicScene parent;
}