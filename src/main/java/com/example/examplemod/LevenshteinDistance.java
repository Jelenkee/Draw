package com.example.examplemod;

import java.awt.geom.Point2D;
import java.util.List;

//https://www.stephenenright.com/java-levenshtein-distance
public class LevenshteinDistance {
    private final int insertionCost;
    private final int deletionCost;

    public LevenshteinDistance() {
        this(1, 1);
    }

    public LevenshteinDistance(int insertionCost, int deletionCost) {
        this.insertionCost = insertionCost;
        this.deletionCost = deletionCost;
    }

    public double calculateDistance(List<Point2D.Double> source, List<Point2D.Double> target) {
        int sourceLength = source.size();
        int targetLength = target.size();

        double[][] matrix = new double[sourceLength + 1][targetLength + 1];
        matrix[0][0] = 0;

        for (int row = 1; row <= sourceLength; ++row) {
            matrix[row][0] = row;
        }

        for (int col = 1; col <= targetLength; ++col) {
            matrix[0][col] = col;
        }

        for (int row = 1; row <= sourceLength; ++row) {
            for (int col = 1; col <= targetLength; ++col) {
                matrix[row][col] = calcMinCost(source, target, matrix, row, col);
            }
        }

        return matrix[sourceLength][targetLength];
    }

    private double calcMinCost(List<Point2D.Double> source, List<Point2D.Double> target,
                               double[][] matrix, int row, int col) {
        return Math.min(
                calcSubstitutionCost(source, target, matrix, row, col), Math.min(
                        calcDeletionCost(matrix, row, col),
                        calcInsertionCost(matrix, row, col))
        );
    }

    private double calcInsertionCost(double[][] matrix, int row, int col) {
        return matrix[row][col - 1] + insertionCost;
    }

    private double calcDeletionCost(double[][] matrix, int row, int col) {
        return matrix[row - 1][col] + deletionCost;
    }

    private double calcSubstitutionCost(List<Point2D.Double> source, List<Point2D.Double> target,
                                        double[][] matrix, int row, int col) {
        Point2D.Double sourcePoint = source.get(row - 1);
        Point2D.Double targetPoint = target.get(col - 1);
        double cost = sourcePoint.distance(targetPoint);
        return matrix[row - 1][col - 1] + cost;
    }
}
