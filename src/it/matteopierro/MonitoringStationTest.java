package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class MonitoringStationTest {

    @Test
    void findAllAsteroids() {
        String map =
                ".#..#\n" +
                ".....\n" +
                "#####\n" +
                "....#\n" +
                "...##";

        assertThat(findAllAsteroids(map)).containsExactly(
                      tuple(1,0),                         tuple(4,0),
          tuple(0,2), tuple(1,2), tuple(2,2), tuple(3,2), tuple(4,2),
                                                          tuple(4,3),
                                              tuple(3,4), tuple(4,4)
        );
    }

    @Test
    void findAsteroidsInOneRow() {
        String map = ".#..#";

        assertThat(findAllAsteroids(map)).containsExactly(
                tuple(1,0),
                tuple(4,0)
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
                tuple(0,1),
                tuple(0,4)
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
                tuple(1,1),
                tuple(0,4)
        );
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
