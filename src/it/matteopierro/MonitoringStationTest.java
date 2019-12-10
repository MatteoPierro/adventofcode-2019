package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String map =
                        ".#..#\n" +
                        ".....\n" +
                        "#####\n" +
                        "....#\n" +
                        "...##";

        assertThat(findAllAsteroids(map)).containsExactly(
                tuple(1, 0), tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2),
                tuple(4, 3),
                tuple(3, 4), tuple(4, 4)
        );
    }

    @Test
    void findVisibleAsteroidsForOneAsteroid() {
        String map =
                        ".#..#\n" +
                        ".....\n" +
                        "#####\n" +
                        "....#\n" +
                        "...##";

        List<Tuple2<Integer, Integer>> asteroids = findAllAsteroids(map);

        assertThat(visibleAsteroids(tuple(1, 0), asteroids)).containsExactlyInAnyOrder(
                             tuple(1, 0),                           tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2)
        );
        assertThat(visibleAsteroids(tuple(0, 2), asteroids)).containsExactlyInAnyOrder(
                            tuple(1, 0),                          tuple(4, 0),
                tuple(0, 2),tuple(1, 2),
                                                      tuple(4, 3),
                                         tuple(3, 4), tuple(4, 4)
        );
        assertThat(visibleAsteroids(tuple(0, 2), asteroids).size()).isEqualTo(6);
        assertThat(visibleAsteroids(tuple(2, 2), asteroids).size()).isEqualTo(7);
    }

    private Set<Tuple2<Integer, Integer>> visibleAsteroids(Tuple2<Integer, Integer> asteroid, List<Tuple2<Integer, Integer>> asteroids) {
        Set<Tuple2<Integer, Integer>> visibleAsteroids = new HashSet<>();

        for (int rowIndex = 0; rowIndex < 5; rowIndex++) {
            for (int columnIndex = 0; columnIndex < 5; columnIndex++) {
                Tuple2<Integer, Integer> coordinates = tuple(columnIndex, rowIndex);
                if (asteroids.contains(coordinates) && isOnLineOfSight(asteroid, coordinates, asteroids)) {
                    visibleAsteroids.add(coordinates);
                }
            }
        }

        return visibleAsteroids;
    }

    @Test
    void lineOfSight() {
        List<Tuple2<Integer, Integer>> asteroids = List.of(
                tuple(1, 0), tuple(4, 0),
                tuple(0, 2), tuple(1, 2), tuple(2, 2), tuple(3, 2), tuple(4, 2),
                tuple(4, 3),
                tuple(3, 4), tuple(4, 4));

        assertThat(isOnLineOfSight(tuple(1, 0), tuple(4, 3), asteroids)).isFalse();
        assertThat(isOnLineOfSight(tuple(1, 0), tuple(4, 0), asteroids)).isTrue();
        assertThat(isOnLineOfSight(tuple(1, 0), tuple(0, 2), asteroids)).isTrue();
    }

    private boolean isOnLineOfSight(Tuple2<Integer, Integer> asteroid, Tuple2<Integer, Integer> target, List<Tuple2<Integer, Integer>> asteroids) {
        if (target.v2.equals(asteroid.v2)) {
            int max = Math.max(target.v1, asteroid.v1);
            int min = Math.min(target.v1, asteroid.v1);
            for (int columnIndex = max - 1; columnIndex > min; columnIndex--) {
                if (asteroids.contains(tuple(columnIndex, asteroid.v2))) {
                    return false;
                }
            }
        }
        if (target.v2 - asteroid.v2 > 0) {
            int columnIndex = target.v1 - 1; //check if they are in the other direction
            for (int rowIndex = target.v2 - 1; rowIndex > asteroid.v2; rowIndex--) {
                if (asteroids.contains(tuple(columnIndex, rowIndex))) {
                    return false;
                }
                columnIndex--;
            }
        }
        return true;
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
