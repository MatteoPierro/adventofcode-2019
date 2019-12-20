package it.matteopierro;

import it.matteopierro.computer.Computer;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class TractorBeamTest {

    private Computer computer;
    private String program;

    @BeforeEach
    void setUp() throws IOException {
        computer = new Computer();
        program = Files.readString(Paths.get("./input_day19"));
    }

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day19"));

        List<Tuple2<Integer, Integer>> points = points();
        Computer computer = new Computer();
        long affectedPoints = 0;
        for (Tuple2<Integer, Integer> point : points) {
            List<String> results = computer.execute(program, String.valueOf(point.v1), String.valueOf(point.v2));
            String result = results.get(results.size() - 1);
            if (result.equals("1")) {
                affectedPoints++;
            }
        }
        assertThat(affectedPoints).isEqualTo(-1L);
    }

    @Test
    void printBeam() {
        for (int y = 900; y <= 1047; y++) {
            println(y, 1000);
            System.out.println();
        }
    }

    @Test
    void findFirstPointWith100() {
        Tuple2<Integer, Integer> firstPoint = firstLineWith100();
        assertThat(firstPoint.v2).isEqualTo(597);
        assertThat(firstPoint.v1).isEqualTo(353);
    }

    @Test
    void secondPuzzle() {
        Tuple2<Integer, Integer> firstPointWith100 = tuple(353, 597);
        String result = execute(firstPointWith100.v1, firstPointWith100.v2 + 99);
        assertThat(result).isEqualTo("0");
        int newY = firstPointWith100.v2 + 99;
        int newX = firstBeam(696);
        assertThat(newX).isEqualTo(412);

        assertThat(checkSquare(newX, newY)).isFalse();

        Tuple2<Integer, Integer> startingPosition = tuple(newX, newY);
        Tuple2<Integer, Integer> leftDown = findLeftDown(startingPosition);
        assertThat(leftDown.v1).isEqualTo(619);
        assertThat(leftDown.v2).isEqualTo(1047);
        assertThat(execute(leftDown.v1, leftDown.v2 - 99)).isEqualTo("1");//number to compute
        assertThat(execute(leftDown.v1 + 99, leftDown.v2)).isEqualTo("1");
        assertThat(execute(leftDown.v1 + 99, leftDown.v2 - 99)).isEqualTo("1");
        assertThat(leftDown.v1 * 10000L + (leftDown.v2 - 99)).isEqualTo(6190948L);
    }

    private Tuple2<Integer, Integer> findLeftDown(Tuple2<Integer, Integer> startingPoint) {
        Tuple2<Integer, Integer> result = startingPoint;
        while (!checkSquare(result.v1, result.v2)) {
            int newY = result.v2 + 1;
            int newX = firstBeam(newY, result.v1);
            result = tuple(newX, newY);
        }
        return result;
    }

    private boolean checkSquare(int x , int y) {
        return execute(x + 99, y - 99).equals("1");
    }

    private Tuple2<Integer, Integer> firstLineWith100() {
        int y = 3;
        while (true) {
            int x = firstBeam(y);
            String result = execute(x + 99, y);
            if (result.equals("1")) {
                return tuple(x, y);
            }
            y++;
        }
    }

    private String execute(int x, int y) {
        List<String> results = computer.execute(program, String.valueOf(x), String.valueOf(y));
        return results.get(results.size() - 1);
    }

    private int firstBeam(int y) {
        int x = 0;
        return firstBeam(y, x);
    }

    private int firstBeam(int y, int x) {
        while (true) {
            List<String> results = computer.execute(program, String.valueOf(x), String.valueOf(y));
            String result = results.get(results.size() - 1);
            if (result.equals("1")) {
                return x;
            }
            x++;
        }
    }

    private void print(String result) {
        if (result.equals("1")) {
            System.out.print("#");
        }
        if (result.equals("0")) {
            System.out.print(".");
        }
    }

    private void println(int y, int maxX) {
        for (int x = 0; x < maxX; x++) {
            List<String> results = computer.execute(program, String.valueOf(x), String.valueOf(y));
            String result = results.get(results.size() - 1);
            print(result);
        }
    }

    @Test
    void combinations() {
        List<Tuple2<Integer, Integer>> points = points();

        assertThat(points).hasSize(2500);
    }

    private List<Tuple2<Integer, Integer>> points() {
        return Seq.range(0, 50)
                .crossJoin(Seq.range(0, 50))
                .toList();
    }
}
