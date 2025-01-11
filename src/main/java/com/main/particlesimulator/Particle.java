package com.main.particlesimulator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.Vector;

import static java.lang.Math.*;
import static javafx.application.Platform.exit;

public class Particle extends Circle {

    public Particle(float v, float v1, float v2) {
        super(v, v1, v2);

        // при нажатии на частицу хватает ее
        setOnMousePressed(event -> {
            if (parent != null)
                parent.grabParticle(this);
        });
    }

    // Устанавливает новую позицию и сохраняет предыдущую
    public void setPos(double x, double y){
        prevPos[0] = getCenterX();
        prevPos[1] = getCenterY();

        // если определена родительская сцена, то не дает переместить частицу за ее пределы
        if (parent != null) {
            double parentMaxX = parent.getBoundsInParent().getWidth();
            double parentMaxY = parent.getBoundsInParent().getHeight();
            double offset = 15 + getRadius() - 2;

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
            if (node != this && node.getBoundsInParent().intersects(getBoundsInParent())) {
                if (!(node instanceof Line line))
                    nodes.add(0, node);
                else{
                    // для линии дополнительно проверяется пересечение по радиусу
                    Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
                    Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());
                    double distance = getDistanceToLine(getCenterX(), getCenterY(),
                            lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
                    if (distance < getRadius())
                        nodes.add(0, node);
                }

            }
        }
        return nodes;
    }

    public static double getDistanceToLine(double px, double py, double x1, double y1, double x2, double y2) {
        double A = y2 - y1;
        double B = x1 - x2;
        double C = x2 * y1 - x1 * y2;
        return abs((A * px + B * py + C) / Math.sqrt(A * A + B * B));
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

    // Двигает частицу по направлению импульса
    public void makeMove() {
//        if (abs(momentum[0]) < 0.05 && abs(momentum[1]) < 0.05)
//            return;
//        System.out.println(momentum[0] + "  |  " + momentum[1]);
        if(isMovementStopped)
            return;
        calculateResistance();
        addForce(gravity);
        setPos(
                getCenterX() + Math.max(-getRadius(), Math.min(momentum[0], getRadius())),
                getCenterY() - Math.max(-getRadius(), Math.min(momentum[1], getRadius()))
        );

        Vector<Node> collisions = getCollisions();
        if (!collisions.isEmpty()) {
            Vector<double[]> normals = new Vector<>();

            // нормали всех пересеченных прямых
            for (Node node : collisions) {
                if (node instanceof Line line) {
                    Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
                    Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

                    double[] normal = NormalUtils.calculateLineNormal(getCenterX(), getCenterY(),
                            lineStartCoords.getX(), lineStartCoords.getY(),
                            lineEndCoords.getX(), lineEndCoords.getY());
                    normals.add(normal);

                    correctPosition(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
                }
            }

            double[] averageNormal = NormalUtils.calculateAverageNormal(normals);

            if (averageNormal != null) {
                reflectVector(averageNormal);
            }

            momentum[0] *= friction;
        }
    }

    // Корректирует положение таким образом, чтобы частица не пересекала переданную прямую=
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

    // Отражает вектор по переданной нормали
    private void reflectVector(double[] normal) {
        double dotProduct = normal[0] * momentum[0] + normal[1] * momentum[1];

        momentum[0] = momentum[0] - 2 * dotProduct * normal[0];
        momentum[1] = momentum[1] - 2 * dotProduct * normal[1];

        momentum[0] -= momentum[0] * elasticityCoefficient;
        momentum[1] -= momentum[1] * elasticityCoefficient;

        elasticityCoefficient = Math.min(1, elasticityCoefficient + elasticityCoefficientStep);
    }

    // Устанавливает родительскую сцену
    public void setParent(GraphicScene parent) {
        this.parent = parent;
    }

    // Устанавливает режим перемещения (вкл/выкл)
    public void setMovementState(boolean isMovementStopped){
        this.isMovementStopped = isMovementStopped;
    }

    // Сбрасывает накапливаемые параметры частицы
    public void reset(){
        elasticityCoefficient = 0.05;
        elasticityCoefficientStep = 0.0005;
        momentum[0] = 0;
        momentum[1] = 1;
    }

    // Возвращает предыдущую позицию
    public double[] getPrevPos() {
        return prevPos;
    }

    private boolean isMovementStopped = false;
    private final double[] momentum = {0, 1}; // импульс
    private final double[] prevPos = {0, 0};
    private final double dragCoefficient = 0.03; // сопротивление среды
    double friction = 0.98; // трение
    private double elasticityCoefficient = 0.05; // сила упругости (возрастает при каждом контакте)
    private double elasticityCoefficientStep = 0.1;
    static private final double[] gravity = {0,-4}; // сила гравитации
    private GraphicScene parent;
}