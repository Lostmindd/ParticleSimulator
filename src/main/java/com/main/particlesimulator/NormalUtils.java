package com.main.particlesimulator;

import java.util.Vector;

import static java.lang.Math.signum;

public class NormalUtils {
    // Считает нормаль переданной прямой по отношению к переданной точке
    public static double[] calculateLineNormal(double p1, double p2, double x1, double y1, double x2, double y2) {
        double A = y2-y1;
        double B = x1-x2;
        double C = x2 * y1 - x1 * y2;

        double normalSign = signum(A * p1 + B * p2 + C);

        return new double[]{normalSign * A / (Math.sqrt(A * A + B * B)+0.01), normalSign * (-B / (Math.sqrt(A * A + B * B)+0.01))};
    }

    // Вычисляет средней нормали переданных нормалей
    public static double[] calculateAverageNormal(Vector<double[]> normals) {
        if (normals.isEmpty()) return null;

        double sumX = 0, sumY = 0;
        for (double[] normal : normals) {
            sumX += normal[0];
            sumY += normal[1];
        }

        double length = Math.sqrt(sumX * sumX + sumY * sumY);
        return new double[]{sumX / (length+0.01), sumY / (length+0.01)};
    }
}
