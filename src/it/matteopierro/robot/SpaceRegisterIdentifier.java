package it.matteopierro.robot;

import it.matteopierro.SpacePoliceTest;
import it.matteopierro.computer.Computer;
import org.jooq.lambda.tuple.Tuple2;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static it.matteopierro.SpacePoliceTest.Robot.WHITE_COLOR;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class SpaceRegisterIdentifier {

    public static void main(String[] args) throws IOException {
        String program = Files.readString(Paths.get("./input_day11"));
        SpacePoliceTest.Robot robot = new SpacePoliceTest.Robot(WHITE_COLOR);
        new Computer().execute(program, robot);

        int minX = robot.tiles().keySet()
                .stream()
                .min(Comparator.comparing(t -> t.v1))
                .map(t -> t.v1)
                .orElseThrow();

        int maxX = robot.tiles().keySet()
                .stream()
                .max(Comparator.comparing(t -> t.v1))
                .map(t -> t.v1)
                .orElseThrow();

        int minY = robot.tiles().keySet()
                .stream()
                .min(Comparator.comparing(t -> t.v2))
                .map(t -> t.v2)
                .orElseThrow();

        int maxY = robot.tiles().keySet()
                .stream()
                .max(Comparator.comparing(t -> t.v2))
                .map(t -> t.v2)
                .orElseThrow();


        int gridX = maxX - minX;
        int gridY = maxY - minY;

        List<Tuple2<Integer, Integer>> whiteTile = robot.tiles().keySet()
                .stream()
                .filter(t -> robot.tiles().get(t).equals(WHITE_COLOR))
                .map(t -> tuple(t.v1 - minX, maxY - t.v2))
                .collect(Collectors.toList());


        int height = gridY + 1;
        int width = gridX + 1;
        Image panel = new Image(width, height, whiteTile);
        JFrame frame = new JFrame("Image Decoded");
        frame.setSize(width * panel.recWidth, height * panel.recWidth);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    static class Image extends JPanel {

        private final int width;
        private final int height;
        private final List<Tuple2<Integer, Integer>> whiteTile;
        Brick[][] bricks;

        int recWidth = 20;
        int recHeight = 10;

        public Image(int width, int height, List<Tuple2<Integer, Integer>> whiteTile) {
            this.width = width;
            this.height = height;
            this.whiteTile = whiteTile;
            bricks = new Brick[height][width];
            for (int y = 0; y < this.height; y++) {
                for (int x = 0; x < this.width; x++) {
                    bricks[y][x] = new Brick(colorFor(x, y),
                            new Rectangle(x * recWidth, y * recHeight, recWidth, recHeight));
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bricks[y][x].draw(g);
                }
            }
        }

        private Color colorFor(int x, int y) {
            if (whiteTile.contains(tuple(x, y))) return Color.WHITE;

            return Color.BLACK;
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
