package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class MonitoringStationTest {

    @Test
    void findAsteroidsInOneRow() {
        String map = ".#..#";

        assertThat(findAllAsteroids(map)).containsExactly(
                tuple(1, 0),
                tuple(4, 0)
        );
    }

    @Test
    void findAsteroidsInOneColumn() {
        String map =
                ".\n" +
                        "#\n" +
                        ".\n" +
                        ".\n" +
                        "#";

        assertThat(findAllAsteroids(map)).containsExactly(
                tuple(0, 1),
                tuple(0, 4)
        );
    }

    @Test
    void findAsteroidsInTwoColumn() {
        String map =
                "..\n" +
                        ".#\n" +
                        "..\n" +
                        "..\n" +
                        "#.";

        assertThat(findAllAsteroids(map)).containsExactly(
                tuple(1, 1),
                tuple(0, 4)
        );
    }

    @Test
    void findAllAsteroids() {
        String space =
                        ".#..#\n" +
                        ".....\n" +
                        "#####\n" +
                        "....#\n" +
                        "...##";

        assertThat(findAllAsteroids(space)).containsExactly(
                tuple(1, 0), tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2),
                tuple(4, 3),
                tuple(3, 4), tuple(4, 4)
        );
    }

    @Test
    void orderAsteroidsBySlope() {
        String space =
                        ".#..#\n" +
                        ".....\n" +
                        "#####\n" +
                        "....#\n" +
                        "...##";

        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(space);
        Tuple2<Integer, Integer> station = tuple(1, 2);
        List<Tuple2<Integer, Integer>> toVaporize = lineOfSight(station, asteroids)
                .stream()
                .map(a -> tuple(a, slope(station, a).abs(), position(station, a)))
                .sorted( (t1, t2) -> {
                    if (t1.v3.equals(t2.v3)) return t1.v2.compareTo(t2.v2);
                    return t1.v3.compareTo(t2.v3);
                })
                .map( t -> t.v1)
                .collect(toList());

        assertThat(toVaporize).containsExactly(tuple(1,0), tuple(4,0), tuple(2,2), tuple(4,3), tuple(4,4), tuple(3,4), tuple(0,2));
    }

    @Test
    void testPosition() {
        assertThat(position(tuple(1,1), tuple(1,0))).isEqualTo(1);
        assertThat(position(tuple(1,1), tuple(2,0))).isEqualTo(1);
        assertThat(position(tuple(1,1), tuple(2,1))).isEqualTo(2);
        assertThat(position(tuple(1,1), tuple(2,2))).isEqualTo(2);
        assertThat(position(tuple(1,1), tuple(1,2))).isEqualTo(3);
        assertThat(position(tuple(1,1), tuple(0,2))).isEqualTo(3);
        assertThat(position(tuple(1,1), tuple(0,1))).isEqualTo(4);
        assertThat(position(tuple(1,1), tuple(0,0))).isEqualTo(4);
    }

    private int position(Tuple2<Integer, Integer> pointA, Tuple2<Integer, Integer> pointB) {
        int dx = pointB.v1 - pointA.v1;
        int dy = pointB.v2 - pointA.v2;
        if (dx >= 0 && dy < 0) return 1;
        if (dx > 0 && dy >= 0) return 2;
        if (dx <= 0 && dy > 0) return 3;
        return 4;
    }

    @Test
    void asteroidsOnTheLine() {
        List<Tuple2<Integer, Integer>> asteroids = List.of(
                             tuple(1, 0),                           tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2),
                                                                    tuple(4, 3),
                                                       tuple(3, 4), tuple(4, 4));

        assertThat(asteroidsOnTheLine(tuple(1,0), tuple(4,4), asteroids)).isEmpty();
        assertThat(asteroidsOnTheLine(tuple(1,0), tuple(3,4), asteroids)).containsExactly(tuple(2, 2));
        assertThat(asteroidsOnTheLine(tuple(1,0), tuple(2,2), asteroids)).isEmpty();
        assertThat(asteroidsOnTheLine(tuple(0,2), tuple(1,2), asteroids)).isEmpty();
        assertThat(asteroidsOnTheLine(tuple(0,2), tuple(4,2), asteroids)).containsExactly(tuple(1, 2), tuple(2, 2), tuple(3, 2));
        assertThat(asteroidsOnTheLine(tuple(4,0), tuple(4,2), asteroids)).isEmpty();
        assertThat(asteroidsOnTheLine(tuple(4,4), tuple(4,0), asteroids)).containsExactly(tuple(4,2), tuple(4,3));
    }

    @Test
    void lineOfSight() {
        List<Tuple2<Integer, Integer>> asteroids = List.of(
                             tuple(1, 0),                           tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2),
                                                                    tuple(4, 3),
                                                       tuple(3, 4), tuple(4, 4));

        assertThat(lineOfSight(tuple(1, 0), asteroids)).containsExactly(tuple(4, 0), tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2), tuple(4, 4));
        assertThat(lineOfSight(tuple(3, 2), asteroids)).containsExactly(tuple(1, 0), tuple(4, 0), tuple(2, 2), tuple(4, 2), tuple(4, 3), tuple(3, 4), tuple(4, 4));
        assertThat(lineOfSight(tuple(3, 4), asteroids)).containsExactly(tuple(4, 0), tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2), tuple(4, 3), tuple(4, 4));
    }

    @Test
    void maxLineOfSight() {
        List<Tuple2<Integer, Integer>> asteroids = List.of(
                             tuple(1, 0),                           tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2),
                                                                    tuple(4, 3),
                                                       tuple(3, 4), tuple(4, 4));

        Tuple2<Tuple2<Integer, Integer>, Integer> result = maxLightOfSight(asteroids);
        assertThat(asList(result.v1)).containsExactly(tuple(3,4));
        assertThat(result.v2).isEqualTo(8);
    }

    @Test
    void maxLineOfSightFirstExample() {
        String space = "......#.#.\n" +
                "#..#.#....\n" +
                "..#######.\n" +
                ".#.#.###..\n" +
                ".#..#.....\n" +
                "..#....#.#\n" +
                "#..#....#.\n" +
                ".##.#..###\n" +
                "##...#..#.\n" +
                ".#....####";
        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(space);
        Tuple2<Tuple2<Integer, Integer>, Integer> result = maxLightOfSight(asteroids);
        assertThat(asList(result.v1)).containsExactly(tuple(5,8));
        assertThat(result.v2).isEqualTo(33);
    }

    @Test
    void maxLineOfSightSecondExample() {
        String space = ".#..##.###...#######\n" +
                "##.############..##.\n" +
                ".#.######.########.#\n" +
                ".###.#######.####.#.\n" +
                "#####.##.#.##.###.##\n" +
                "..#####..#.#########\n" +
                "####################\n" +
                "#.####....###.#.#.##\n" +
                "##.#################\n" +
                "#####.##.###..####..\n" +
                "..######..##.#######\n" +
                "####.##.####...##..#\n" +
                ".#####..#.######.###\n" +
                "##...#.##########...\n" +
                "#.##########.#######\n" +
                ".####.#.###.###.#.##\n" +
                "....##.##.###..#####\n" +
                ".#.#.###########.###\n" +
                "#.#.#.#####.####.###\n" +
                "###.##.####.##.#..##";
        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(space);
        Tuple2<Tuple2<Integer, Integer>, Integer> result = maxLightOfSight(asteroids);
        assertThat(asList(result.v1)).containsExactly(tuple(11,13));
        assertThat(result.v2).isEqualTo(210);
    }

    @Test
    void maxLineOfSightThirdExample() {
        String space = "#.#...#.#.\n" +
                ".###....#.\n" +
                ".#....#...\n" +
                "##.#.#.#.#\n" +
                "....#.#.#.\n" +
                ".##..###.#\n" +
                "..#...##..\n" +
                "..##....##\n" +
                "......#...\n" +
                ".####.###.";
        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(space);
        Tuple2<Tuple2<Integer, Integer>, Integer> result = maxLightOfSight(asteroids);
        assertThat(asList(result.v1)).containsExactly(tuple(1,2));
        assertThat(result.v2).isEqualTo(35);
    }

    @Test
    void maxLineOfSightForthExample() {
        String space = ".#..#..###\n" +
                "####.###.#\n" +
                "....###.#.\n" +
                "..###.##.#\n" +
                "##.##.#.#.\n" +
                "....###..#\n" +
                "..#.#..#.#\n" +
                "#..#.#.###\n" +
                ".##...##.#\n" +
                ".....#.#..";
        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(space);
        Tuple2<Tuple2<Integer, Integer>, Integer> result = maxLightOfSight(asteroids);
        assertThat(asList(result.v1)).containsExactly(tuple(6,3));
        assertThat(result.v2).isEqualTo(41);
    }

    @Test
    void firstPuzzle() throws IOException {
        String space = Files.readString(Paths.get("./input_day10"));

        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(space);
        Tuple2<Tuple2<Integer, Integer>, Integer> result = maxLightOfSight(asteroids);
        assertThat(asList(result.v1)).containsExactly(tuple(31,20));
        assertThat(result.v2).isEqualTo(319);
    }

    private Tuple2<Tuple2<Integer, Integer>, Integer> maxLightOfSight(List<Tuple2<Integer, Integer>> asteroids) {
        return asteroids.stream()
                .map(asteroid -> tuple(asteroid, lineOfSight(asteroid, asteroids)))
                .map(t -> tuple(t.v1, t.v2.size()))
                .max(Comparator.comparingInt(t -> t.v2))
                .orElseThrow();
    }

    private List<Tuple2<Integer, Integer>> lineOfSight(Tuple2<Integer,Integer> candidate, List<Tuple2<Integer, Integer>> asteroids) {
        return asteroids.stream()
                .map( target -> tuple(target, asteroidsOnTheLine(candidate, target, asteroids).size()))
                .filter(t-> t.v2 == 0)
                .map(t -> t.v1)
                .filter(t -> !t.equals(candidate))
                .collect(toList());
    }

    @Test
    void testSlop() {
        assertThat(slope(tuple(1,0), tuple(4,0))).isEqualTo(slope(tuple(4,0), tuple(1,0)));
        assertThat(slope(tuple(1,0), tuple(1,2))).isEqualTo(slope(tuple(1,2), tuple(1,0)));
        assertThat(slope(tuple(1,0), tuple(0,2))).isEqualTo(slope(tuple(0,2), tuple(1,0)));
        assertThat(slope(tuple(1,0), tuple(3,2))).isEqualTo(slope(tuple(4,3), tuple(1,0)));
        assertThat(slope(tuple(1,0), tuple(2,2))).isEqualTo(slope(tuple(3,4), tuple(1,0)));
    }

    private List<Tuple2<Integer, Integer>> asteroidsOnTheLine(Tuple2<Integer,Integer> pointA, Tuple2<Integer,Integer> pointB, List<Tuple2<Integer, Integer>> asteroids) {
        BigDecimal slope = slope(pointA, pointB);
        return asteroids.stream()
                .filter( asteroid -> !asteroid.equals(pointA))
                .filter( asteroid -> !asteroid.equals(pointB))
                .map( asteroid -> tuple(asteroid, slope(asteroid, pointA)))
                .filter( t -> t.v2.equals(slope))
                .map( t -> t.v1)
                .filter( t -> t.v1 >= min(pointA.v1, pointB.v1))
                .filter( t -> t.v1 <= max(pointA.v1, pointB.v1))
                .filter( t -> t.v2 >= min(pointA.v2, pointB.v2))
                .filter( t -> t.v2 <= max(pointA.v2, pointB.v2))
                .collect(toList());
    }

    private BigDecimal slope(Tuple2<Integer, Integer> pointA, Tuple2<Integer, Integer> pointB) {
        if (pointA.v1.equals(pointB.v1)) return new BigDecimal(BigInteger.ZERO, 4);
        return new BigDecimal(pointB.v2 - pointA.v2).divide(new BigDecimal(pointB.v1 - pointA.v1), 4, HALF_UP);
    }

    private List<Tuple2<Integer, Integer>> findAllAsteroids(String map) {
        List<Tuple2<Integer, Integer>> result = new ArrayList<>();

        String[] rows = map.split("\n");
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            String[] row = rows[rowIndex].split("");
            for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                if (row[columnIndex].equals("#")) {
                    result.add(tuple(columnIndex, rowIndex));
                }
            }
        }

        return result;
    }
}
