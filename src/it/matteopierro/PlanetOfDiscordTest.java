package it.matteopierro;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class PlanetOfDiscordTest {

    @Test
    void shouldCreateWorld() {
        var input =
                "....#\n" +
                "#..#.\n" +
                "#..##\n" +
                "..#..\n" +
                "#....";

        var world = new World(input);

        assertThat(world.cells()).containsExactlyInAnyOrder(
                tuple(4, 0, 4),
                tuple(0, 1, 5),
                tuple(3, 1, 8),
                tuple(0, 2, 10),
                tuple(3, 2, 13),
                tuple(4, 2, 14),
                tuple(2, 3, 17),
                tuple(0, 4, 20)
        );
    }

    private class World {
        private Set<Tuple3<Integer, Integer, Integer>> cells = new HashSet<>();

        public World(String input) {
            var lines = input.split("\n");
            int position = 0;
            int y = 0;
            for (String line : lines) {
                int x = 0;
                for (String cell : line.split("")) {
                    if ("#".equals(cell)) {
                        cells.add(tuple(x, y, position));
                    }
                    x++;
                    position++;
                }
                y++;
            }
        }

        public Set<Tuple3<Integer, Integer, Integer>> cells() {
            return cells;
        }
    }
}
