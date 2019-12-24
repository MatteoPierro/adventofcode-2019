package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                        "#....\n";

        var world = new World(input);

        assertThat(world.cells()).containsExactlyInAnyOrder(
                tuple(4, 0),
                tuple(0, 1),
                tuple(3, 1),
                tuple(0, 2),
                tuple(3, 2),
                tuple(4, 2),
                tuple(2, 3),
                tuple(0, 4)
        );
        assertThat(world.toString()).isEqualTo(input);
    }

    @Test
    void shouldEvolve() {
        var input =
                "....#\n" +
                        "#..#.\n" +
                        "#..##\n" +
                        "..#..\n" +
                        "#....\n";

        var world = new World(input);

        world.tick();

        assertThat(world.toString()).isEqualTo(
                "#..#.\n" +
                        "####.\n" +
                        "###.#\n" +
                        "##.##\n" +
                        ".##..\n");
    }

    @Test
    void shouldEvolveTwice() {
        var input =
                "....#\n" +
                        "#..#.\n" +
                        "#..##\n" +
                        "..#..\n" +
                        "#....\n";

        var world = new World(input);

        world.tick();
        world.tick();

        assertThat(world.toString()).isEqualTo(
                "#####\n" +
                        "....#\n" +
                        "....#\n" +
                        "...#.\n" +
                        "#.###\n");
    }

    @Test
    void shouldEvolveSeveralTimes() {
        var input =
                "....#\n" +
                        "#..#.\n" +
                        "#..##\n" +
                        "..#..\n" +
                        "#....\n";

        var world = new World(input);

        world.tick();
        world.tick();
        world.tick();
        world.tick();

        assertThat(world.toString()).isEqualTo(
                "####.\n" +
                        "....#\n" +
                        "##..#\n" +
                        ".....\n" +
                        "##...\n");
    }

    @Test
    void shouldFindRepeatingWorld() {
        var input =
                "....#\n" +
                        "#..#.\n" +
                        "#..##\n" +
                        "..#..\n" +
                        "#....\n";

        var world = new World(input);
        world.evolveUntilRepeating();

        assertThat(world.toString()).isEqualTo(
                ".....\n" +
                        ".....\n" +
                        ".....\n" +
                        "#....\n" +
                        ".#...\n");
        assertThat(world.biodiversity()).isEqualTo(2129920L);
    }

    private class World {
        private Set<Tuple2<Integer, Integer>> livingCells = new HashSet<>();
        private List<Tuple2<Integer, Integer>> NEIGHBOUR_DELTA = List.of(
                tuple(0, -1),
                tuple(-1, 0),
                tuple(+1, 0),
                tuple(0, +1)
        );

        public World(String input) {
            var lines = input.split("\n");
            int y = 0;
            for (String line : lines) {
                int x = 0;
                for (String cell : line.split("")) {
                    if ("#".equals(cell)) {
                        livingCells.add(tuple(x, y));
                    }
                    x++;
                }
                y++;
            }
        }

        public Set<Tuple2<Integer, Integer>> cells() {
            return livingCells;
        }

        public void tick() {
            Set<Tuple2<Integer, Integer>> newGeneration = new HashSet<>();
            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 5; i++) {
                    Tuple2<Integer, Integer> cell = tuple(i, j);
                    if (isAlive(cell)) {
                        newGeneration.add(cell);
                    }
                }
            }
            livingCells = newGeneration;
        }

        private boolean isAlive(Tuple2<Integer, Integer> cell) {
            long livingNeighbour = neighbour(cell)
                    .stream()
                    .filter(c -> livingCells.contains(c))
                    .count();

            if (livingCells.contains(cell)) {
                return livingNeighbour == 1;
            }
            return livingNeighbour == 1 || livingNeighbour == 2;
        }

        private Set<Tuple2<Integer, Integer>> neighbour(Tuple2<Integer, Integer> cell) {
            return NEIGHBOUR_DELTA.stream()
                    .map(d -> tuple(cell.v1 + d.v1, cell.v2 + d.v2))
                    .collect(Collectors.toSet());
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();

            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 5; i++) {
                    Tuple2<Integer, Integer> cell = tuple(i, j);
                    if (livingCells.contains(cell)) {
                        result.append("#");
                    } else {
                        result.append(".");
                    }
                }
                result.append("\n");
            }

            return result.toString();
        }

        public void evolveUntilRepeating() {
            var previousWorlds = new HashSet<Set<Tuple2<Integer, Integer>>>();
            while(!previousWorlds.contains(livingCells)) {
                previousWorlds.add(livingCells);
                tick();
            }
        }

        public long biodiversity() {
            long result = 0;
            int position = 0;
            for (int j = 0; j < 5; j++) {
                for (int i = 0; i < 5; i++) {
                    if (livingCells.contains(tuple(i, j))) {
                        result += Math.pow(2, position);
                    }
                    position++;
                }
            }
            return result;
        }
    }
}
