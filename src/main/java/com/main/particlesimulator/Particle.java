package com.main.particlesimulator;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.Vector;

import static java.lang.Math.*;

public class Particle extends Circle {
    private static boolean isMovementStopped = false;
    static public final double[] gravity = {0,-2}; // сила гравитации для конкретной частицы
    static public double simulationDragCoefficient = 0.5; // сопротивление среды в симуляции

    private double mass = 30;  // масса
    private final double[] momentum = {0, 0}; // импульс
    private final double[] prevPos = {0, 0};  // последняя позиция
    public double dragCoefficient = ((getRadius() / mass) - 0.034)/149.966; // сопротивление конкретной частицы
    private double elasticityCoefficient = 0.001; // сила упругости (возрастает при каждом контакте)
    private double elasticityCoefficientStep = 0.005; // шаг возрастания силы упругости
    private GraphicScene parent;


    // Стандартное создание частицы
    public Particle(float x, float y, float radius) {
        super(x, y, radius);

        // при нажатии на частицу хватает ее
        setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && parent != null)
                parent.grabParticle(this);
        });
    }

    // Создание частицы с учетом массы и цвета
    public Particle(float x, float y, float radius, double mass, Color color) {
        this(x, y, radius);
        this.mass = mass;
        setFill(color);
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

    // Находит кратчайшее расстояние от точки до линии
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
        double resistanceFactor = Math.min(1, dragCoefficient * simulationDragCoefficient);
        momentum[0] -= momentum[0] * resistanceFactor;
        momentum[1] -= momentum[1] * resistanceFactor;
    }

    // Двигает частицу по направлению импульса
    public void makeMove() {
        if(isMovementStopped)
            return;
        addForce(gravity);
        calculateResistance();
            setPos(
                    getCenterX() + Math.max(-getRadius(), Math.min(momentum[0], getRadius())),
                    getCenterY() - Math.max(-getRadius(), Math.min(momentum[1], getRadius()))
            );

            Vector<Node> collisions = getCollisions();
            if (!collisions.isEmpty()) {
                Vector<double[]> lineNormals = new Vector<>();

                // нормали всех пересеченных прямых
                for (Node node : collisions) {
                    if (node instanceof Line line) {
                        Point2D lineStartCoords = line.localToScene(line.getStartX(), line.getStartY());
                        Point2D lineEndCoords = line.localToScene(line.getEndX(), line.getEndY());

                        double[] normal = NormalUtils.calculateLineNormal(getCenterX(), getCenterY(),
                                lineStartCoords.getX(), lineStartCoords.getY(),
                                lineEndCoords.getX(), lineEndCoords.getY());
                        lineNormals.add(normal);

                        resolveCollisionWithLine(lineStartCoords.getX(), lineStartCoords.getY(), lineEndCoords.getX(), lineEndCoords.getY());
                    } else if (node instanceof Particle particle) {
                        resolveCollisionWithOtherParticle(particle);
                    }
                }

                double[] averageLineNormals = NormalUtils.calculateAverageNormal(lineNormals);

                if (averageLineNormals != null) {
                    reflectVector(averageLineNormals);
                    momentum[0] *= 1 + (gravity[1] / 60);
            }
        }
    }

    // Корректирует положение таким образом, чтобы частица не пересекала переданную прямую
    private  void resolveCollisionWithLine(double x1, double y1, double x2, double y2) {
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

        takeElasticityCoefficientStep();
    }

    // Корректирует положение текущей частицы с переданной если они пересекаются
    private void resolveCollisionWithOtherParticle(Particle other) {
        double dx = other.getCenterX() - this.getCenterX();
        double dy = other.getCenterY() - this.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double normalX = dx / distance;
        double normalY = dy / distance;

        // поправка положения частиц
        double minDistance = this.getRadius() + other.getRadius();
        if (distance < minDistance) {
            double overlap = minDistance - distance;
            double correctionX = overlap * normalX / 2;
            double correctionY = overlap * normalY / 2;

            this.setPos(this.getCenterX() - correctionX, this.getCenterY() - correctionY);
            other.setPos(other.getCenterX() + correctionX, other.getCenterY() + correctionY);

            calculateCollisionImpulse(other, new double[]{normalX,normalY});
        }
    }

    // Расчитывает новые импульсы для текущей частицы и переданной, если они сталкиваются.
    // Раситывает на основе переданой частицы и переданной нормали
    private void calculateCollisionImpulse(Particle other, double[] normal){

        // относительная скорость
        double[] relativeVelocity = {
                other.momentum[0] - this.momentum[0],
                other.momentum[1] - this.momentum[1]
        };

        // относительная скорость по нормали между частицами
        double velocityAlongNormal = relativeVelocity[0] * normal[0] + relativeVelocity[1] * normal[1];
        if (velocityAlongNormal > 0) return; // частицы уже расходятся

        // импульс текущей частицы
        double impulse = -velocityAlongNormal;
        impulse /= 1 / this.mass + 1 / other.mass;
        double impulseX = impulse * normal[0];
        double impulseY = impulse * normal[1];

        // изменение импульсов
        this.momentum[0] -= impulseX / this.mass;
        this.momentum[1] -= impulseY / this.mass;
        other.momentum[0] += impulseX / other.mass;
        other.momentum[1] += impulseY / other.mass;

        // снижение испульса
        takeElasticityCoefficientStep();
    }

    // Увеличивает эластичность
    private void takeElasticityCoefficientStep(){
        momentum[0] -= momentum[0] * elasticityCoefficient * -gravity[1];
        momentum[1] -= momentum[1] * elasticityCoefficient * -gravity[1];
        elasticityCoefficient = Math.min(0.2, elasticityCoefficient + elasticityCoefficientStep);
    }

    // Устанавливает родительскую сцену
    public void setParent(GraphicScene parent) {
        this.parent = parent;
    }

    // Устанавливает режим перемещения (вкл/выкл)
    public void setMovementState(boolean isMovementStopped){
        Particle.isMovementStopped = isMovementStopped;
    }

    // Сбрасывает накапливаемые параметры частицы
    public void reset(){
        elasticityCoefficient = 0.001;
        elasticityCoefficientStep = 0.00005;
        momentum[0] = 0;
        momentum[1] = 0;
    }

    // Возвращает предыдущую позицию
    public double[] getPrevPos() {
        return prevPos;
    }

    public double[] getMomentum() {
        return momentum;
    }
}