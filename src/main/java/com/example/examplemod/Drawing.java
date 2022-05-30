package com.example.examplemod;

import com.google.common.collect.Iterables;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Drawing implements INBTSerializable<CompoundTag> {

    private List<Point2D.Double> points = new ArrayList<>();

    public void addPoint(Point2D.Double point) {
        Point2D.Double last = Iterables.getLast(points, null);
        if (last == null || !tooClose(point, last, 1.9)) {
            points.add(point);
        }
    }

    private boolean tooClose(Point2D.Double p1, Point2D.Double p2, double distance) {
        return p1.equals(p2) || p1.distanceSq(p2) < distance * distance;
    }

    public void addPoint(double x, double y) {
        addPoint(new Point2D.Double(x, y));
    }

    public boolean valid() {
        return getPoints().size() >= 10;
    }

    public void finish() {
        Validate.isTrue(valid());
        double top = getPoints().stream()
                .min(Comparator.comparing(Point2D.Double::getY))
                .map(Point2D.Double::getY)
                .get();
        double bottom = getPoints().stream()
                .max(Comparator.comparing(Point2D.Double::getY))
                .map(Point2D.Double::getY)
                .get();
        double left = getPoints().stream()
                .min(Comparator.comparing(Point2D.Double::getX))
                .map(Point2D.Double::getX)
                .get();
        double right = getPoints().stream()
                .max(Comparator.comparing(Point2D.Double::getX))
                .map(Point2D.Double::getX)
                .get();
        Point2D.Double start = getPoints().get(0);
        // TODO does this work?
        points = points.stream().map(p -> new Point2D.Double(p.getX() - start.getX(), p.getY() - start.getY())).collect(Collectors.toList());
        double wh = Math.max(bottom - top, right - left);
        points = points.stream()
                .map(p -> new Point2D.Double(p.getX() - left, p.getY() - top))
                .map(p -> new Point2D.Double(p.getX() / wh, p.getY() / wh))
                .collect(Collectors.toList());
        createImage();

    }

    private byte[] createImage() {
        final int wh = 120;
        BufferedImage image = new BufferedImage(wh + 20, wh + 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.CYAN);
        for (Point2D.Double point : getPoints()) {
            graphics.fillRect((int) (point.getX() * wh) - 2 + 10, (int) (point.getY() * wh) - 2 + 10, 4, 4);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            //ImageIO.write(image, "png", new File("/home/gaus/", "pussy" + System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public double compare(Drawing other) {
        List<Point2D.Double> thisPoints = this.points;
        List<Point2D.Double> otherPoints = other.points;
        int thisLength = thisPoints.size();
        int otherLength = otherPoints.size();
        int minLength = Math.min(thisLength, otherLength);
        int diff = Math.abs(thisLength - otherLength);
        double growthPercentage = Math.max(thisLength / (double) otherLength, otherLength / (double) thisLength);
        /*if(thisLength>otherLength){

        }else if(thisLength<otherLength){

        }*/
        //reverse list when comparing
        return comparePoints(thisPoints.subList(0, minLength), otherPoints.subList(0, minLength));
    }

    private double comparePoints(List<Point2D.Double> l1, List<Point2D.Double> l2) {
        Validate.isTrue(l1.size() == l2.size());
        double res = 1;
        for (int i = 0; i < l1.size(); i++) {
            Point2D.Double p1 = l1.get(i);
            Point2D.Double p2 = l2.get(i);
            res *= p1.distance(p2);
        }
        return res;
    }

    public List<Point2D.Double> getPoints() {
        return points;
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drawing drawing = (Drawing) o;
        return Objects.equals(points, drawing.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }
}
