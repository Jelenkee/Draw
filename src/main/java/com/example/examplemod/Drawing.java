package com.example.examplemod;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.ints.IntDoublePair;
import net.minecraft.nbt.LongArrayTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Drawing implements INBTSerializable<LongArrayTag> {

    private List<Point2D.Double> points = new ArrayList<>();

    public void addPoint(double x, double y) {
        addPoint(new Point2D.Double(x, y));
    }

    public void addPoint(Point2D.Double point) {
        Point2D.Double last = Iterables.getLast(points, null);
        if (last == null || !tooClose(point, last, 1.9)) {
            points.add(point);
        }
    }

    private boolean tooClose(Point2D.Double p1, Point2D.Double p2, double distance) {
        return p1.equals(p2) || p1.distanceSq(p2) < distance * distance;
    }

    public boolean valid() {
        return getPoints().size() >= 10;
    }

    public void finish() {
        Validate.isTrue(valid());
        Point2D.Double start = getPoints().get(0);
        points = points.stream().map(p -> new Point2D.Double(p.getX() - start.getX(), p.getY() - start.getY())).collect(Collectors.toList());
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
        double wh = Math.max(bottom - top, right - left);
        points = points.stream()
                .map(p -> new Point2D.Double(p.getX() / wh, p.getY() / wh))
                .collect(Collectors.toList());
        createImage();

    }

    private byte[] createImage() {
        final int wh = 120;
        BufferedImage image = new BufferedImage(wh + 20, wh + 20, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.YELLOW);
        double minX = Math.abs(points.stream().mapToDouble(Point2D.Double::getX).min().getAsDouble());
        double minY = Math.abs(points.stream().mapToDouble(Point2D.Double::getY).min().getAsDouble());
        for (Point2D.Double point : points) {
            graphics.fillRect(
                    (int) ((point.getX() + minX) * wh) - 2 + 10,
                    (int) ((point.getY() + minY) * wh) - 2 + 10,
                    4, 4);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            //ImageIO.write(image, "png", new File("/home/gaus/Bilder/", "lolcat" + System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public double compare(Drawing other, boolean fill) {
        List<Point2D.Double> thisPoints = this.points;
        List<Point2D.Double> otherPoints = other.points;
        int thisLength = thisPoints.size();
        int otherLength = otherPoints.size();
        List<Point2D.Double> smallList = thisLength <= otherLength ? thisPoints : otherPoints;
        List<Point2D.Double> largeList = thisLength > otherLength ? thisPoints : otherPoints;
        while (fill && largeList.size() > smallList.size()) {
            int diff = largeList.size() - smallList.size();
            List<IntDoublePair> distances = new ArrayList<>();
            smallList = new ArrayList<>(smallList);
            for (int i = 0; i < smallList.size() - 1; i++) {
                distances.add(IntDoublePair.of(i, smallList.get(i).distanceSq(smallList.get(i + 1))));
            }
            distances = distances.stream()
                    .sorted(Comparator.comparingDouble(IntDoublePair::rightDouble).reversed())
                    .limit(diff)
                    .sorted(Comparator.comparingInt(IntDoublePair::leftInt).reversed())
                    .collect(Collectors.toList());
            smallList = new LinkedList<>(smallList);
            for (IntDoublePair distance : distances) {
                Point2D.Double first = smallList.get(distance.leftInt());
                Point2D.Double second = smallList.get(distance.leftInt() + 1);
                Point2D.Double newPoint = new Point2D.Double((first.getX() + second.getX()) / 2, (first.getY() + second.getY()) / 2);
                smallList.add(distance.leftInt() + 1, newPoint);
            }
        }
        return new LevenshteinDistance().calculateDistance(smallList, largeList);
    }

    public List<Point2D.Double> getPoints() {
        return points;
    }

    @Override
    public LongArrayTag serializeNBT() {
        return new LongArrayTag(points.stream()
                .flatMapToLong(p -> LongStream.of(Double.doubleToLongBits(p.getX()), Double.doubleToLongBits(p.getY())))
                .toArray());
    }

    @Override
    public void deserializeNBT(LongArrayTag arrayTag) {
        if (arrayTag.size() % 2 != 0) {
            System.out.println("ERROR");
        } else {
            points = new ArrayList<>();
            long[] array = arrayTag.getAsLongArray();
            for (int i = 0; i < array.length; i += 2) {
                points.add(new Point2D.Double(Double.longBitsToDouble(array[i]), Double.longBitsToDouble(array[i + 1])));
            }
        }
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
