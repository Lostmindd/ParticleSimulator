package com.main.particlesimulator;

import javafx.geometry.Bounds;
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
                parent.grabParticle(this);
        });
    }

    public void setPos(double x, double y){
        prevPos[0] = getCenterX();
        prevPos[1] = getCenterY();

        if (parent != null) {
            double parentMaxX = parent.getBoundsInParent().getWidth();
            double parentMaxY = parent.getBoundsInParent().getHeight();
            double offset = 15 + getRadius() + 1.8;

            setCenterX(Math.max(offset, Math.min(x, parentMaxX-offset))); // |+15..-15+|
            setCenterY(Math.max(offset, Math.min(y, parentMaxY-offset))); // |+15..-15+|
        } else {
            setCenterX(x);
            setCenterY(y);
        }
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
//        if (abs(momentum[0]) < 0.05 && abs(momentum[1]) < 0.05)
//            return;
        System.out.println(momentum[0] + "  |  " + momentum[1]);
        calculateResistance();

        if(!isMovementStopped) {
            addForce(gravity);
            setCenterY(getCenterY() - Math.max(-getRadius(), Math.min(momentum[1], getRadius())));
            setCenterX(getCenterX() + Math.max(-getRadius(), Math.min(momentum[0], getRadius())));
        }

        Vector<Node> collisions = getCollisions();

        // при столкновении корректирует положение частицы и отражает вектор
        for (Node node : collisions) {
            Line line = (Line)node;
            Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
            Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

            correctPosition(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
            reflectVector(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());

            momentum[0] *= friction;
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

        elasticityCoefficient = Math.min(1, elasticityCoefficient + elasticityCoefficientStep);

    }

    public void setParent(GraphicScene parent) {
        this.parent = parent;
    }

    public void setMovementState(boolean isMovementStopped){
        this.isMovementStopped = isMovementStopped;
    }

    public void reset(){
        elasticityCoefficient = 0.05;
        elasticityCoefficientStep = 0.0005;
        momentum[0] = 0;
        momentum[1] = 1;
    }

    public double[] getPrevPos() {
        return prevPos;
    }

    private boolean isMovementStopped = false;
    private final double[] momentum = {0, 1}; // импульс
    private final double[] prevPos = {0, 0};
    private final double dragCoefficient = 0.03; // сопротивление среды
    double friction = 0.8; // трение
    private double elasticityCoefficient = 0.05; // сила упругости (возрастает при каждом контакте)
    private double elasticityCoefficientStep = 0.1;
    static private final double[] gravity = {0,-2}; // сила гравитации
    private GraphicScene parent;
}