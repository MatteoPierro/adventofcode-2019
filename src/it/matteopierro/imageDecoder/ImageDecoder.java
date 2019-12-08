package it.matteopierro.imageDecoder;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageDecoder {

    public List<Integer> decodeImage(List<List<Integer>> layers) {
        return Seq.range(0, layers.get(0).size())
                .map(pixelIndex -> decodePixel(layers, pixelIndex))
                .toList();
    }

    private Integer decodePixel(List<List<Integer>> layers, Integer pixelIndex) {
        return layers.stream()
                .map(layer -> layer.get(pixelIndex))
                .filter(layer -> layer == 1 || layer == 0)
                .findFirst()
                .orElse(2);
    }

    public List<Integer> toImageData(String imageData) {
        return Stream.of(imageData.split(""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public int countDigits(List<Integer> integers, int digit) {
        return (int) integers.stream()
                .filter(d -> d == digit)
                .count();
    }

    public List<Integer> findLayerWithMinimumZeros(List<List<Integer>> groups) {
        return groups.stream()
                .map(group -> {
                    return Tuple.tuple(numberOfZeros(group), group);
                })
                .min((o1, o2) -> (int) (o1.v1 - o2.v1))
                .map(t -> t.v2)
                .orElseThrow();
    }

    private long numberOfZeros(List<Integer> group) {
        return group.stream()
                .filter(number -> number == 0)
                .count();
    }

    public List<List<Integer>> layersFor(List<Integer> input, int wide, int tall) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> group = new ArrayList<>();
        for (int value : input) {
            if (group.size() == wide * tall) {
                result.add(group);
                group = new ArrayList<>();
            }
            group.add(value);
        }
        result.add(group);
        return result;
    }

    public List<Integer> decodeImageDataFile(String imageDataFile) throws IOException {
        List<Integer> imageData = toImageData(Files.readString(Paths.get(imageDataFile)));
        List<List<Integer>> layers = layersFor(imageData, 25, 6);
        return decodeImage(layers);
    }

    public static void main(String[] args) throws IOException {
        ImageDecoder imageDecoder = new ImageDecoder();
        List<Integer> pixels = imageDecoder.decodeImageDataFile("./input_day8");
        Image panel = new Image(pixels.iterator());
        JFrame frame = new JFrame("Image Decoded");
        frame.setSize(500, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    static class Image extends JPanel {

        private static final int WEIGHT = 25;
        private static final int HEIGHT = 6;
        int recwidth = 20;
        int recheight = 10;

        Brick[][] bricks = new Brick[HEIGHT][WEIGHT];

        Image(Iterator<Integer> pixels) {
            super();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WEIGHT; x++) {
                    bricks[y][x] = new Brick(colorFor(pixels.next()),
                            new Rectangle(x * recwidth, y * recheight, recwidth, recheight));
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WEIGHT; x++) {
                    bricks[y][x].draw(g);
                }
            }
        }

        Color colorFor(Integer pixel) {
            return pixel == 1 ? Color.WHITE : Color.BLACK;
        }
    }

    static class Brick {

        Color col;
        Rectangle rec;

        Brick(Color col, Rectangle rec) {
            this.col = col;
            this.rec = rec;
        }

        void draw(Graphics g) {
            g.setColor(col);
            g.fillRect(rec.x, rec.y, rec.width, rec.height);
        }
    }
}