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
    }

    // Возвращает все коллизии для текущей частицы
    public Vector<Node> getCollisions() {
        Vector<Node> nodes = new Vector<Node>();
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
        if (abs(momentum[0]) < 0.5 && abs(momentum[1]) < 0.5)
            return;

        addForce(gravity);
        //System.out.println(momentum[0] + "  |  " + momentum[1]);
        calculateResistance();

        setCenterY(getCenterY() - momentum[1]);
        setCenterX(getCenterX() + momentum[0]);
        //System.out.println(getCenterX() + "  |  " + (getCenterY()));

        //        Vector<Node> collisions = getCollisions();
        Vector<Line> crossP = getCross();
        if(crossP.size() > 0)
            System.out.println(crossP);

//        System.out.println(getCenterY());
//        System.out.println(getCenterY() - (momentum[1] + gravity[1]));
//        System.out.println("-----");
        // при столкновении корректирует положение частицы и отражает вектор
        for (Line line : crossP) {
            Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
            Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

            correctPosition(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
            reflectVector(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
            //break;
        }


    }

    private Vector<Line> getCross() {
        Vector<Line> nodes = new Vector<>();
        for (Node node : getParent().getChildrenUnmodifiable()) {
            if (node == this || !(node instanceof Line)) {
                continue;
            }
            Line line = (Line) node;
            Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
            Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

            double x2 = getCenterX() + momentum[0] + gravity[0];
            double y2 = getCenterY() - momentum[1] - gravity[1]; // Учитываем направление оси Y
//            System.out.println(getCenterX() + "  |  " + getCenterY());
//            System.out.println(x2 + "  |  " + y2);
//            System.out.println(lineStartCoords.getX() + "  |  " + lineStartCoords.getY());
//            System.out.println(lineEndCoords.getX() + "  |  " + lineEndCoords.getY());
//            System.out.println("-----");

            double[] point = cross(
                    getCenterX(), getCenterY(), x2, y2,
                    lineStartCoords.getX(), lineStartCoords.getY(),
                    lineEndCoords.getX(), lineEndCoords.getY()
            );

            if (point != null) {
                nodes.add(line);
            }
        }
        return nodes;
    }

    private double[] cross(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        // Вычисляем определитель
        double det = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (det == 0) {
            // Отрезки параллельны или совпадают
            return null;
        }

        // Вычисляем параметры t и u
        double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / det;
        double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / det;

        // Проверяем, лежат ли t и u в пределах [0, 1]
        if (t < 0 || t > 1 || u < 0 || u > 1) {
            // Точка пересечения вне отрезков
            return null;
        }

        // Вычисляем координаты точки пересечения
        double px = x1 + t * (x2 - x1);
        double py = y1 + t * (y2 - y1);

        return new double[]{px, py};
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

        if (elasticityCoefficient + elasticityCoefficientStep < 1)
            elasticityCoefficient += elasticityCoefficientStep;
        else
            elasticityCoefficient = 1;
        System.out.println(elasticityCoefficient);
    }

    private double[] momentum = {-5, 0.5};
    private final double dragCoefficient = 0.01;
    private double elasticityCoefficient = 0.005;
    private final double elasticityCoefficientStep = 0.0005;
    static private final double[] gravity = {0,-10};
}