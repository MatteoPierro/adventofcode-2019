package it.matteopierro;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
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

    @Test
    void firstPuzzle() throws IOException {
        var input = Files.readString(Paths.get("./input_day24"));

        var world = new World(input);
        world.evolveUntilRepeating();

        assertThat(world.biodiversity()).isEqualTo(27777901L);
    }

    @Test
    void secondPuzzle() throws IOException {
        var input = Files.readString(Paths.get("./input_day24"));

        var world = new ComplexWorld(input);

        assertThat(world.neighbours(tuple(0, 0, 0))).containsExactlyInAnyOrder(
                tuple(1, 0, 0),
                tuple(0, 1, 0),
                tuple(1, 2, -1),
                tuple(2, 1, -1)
        );

        assertThat(world.neighbours(tuple(2, 2, 0))).containsExactlyInAnyOrder(
                tuple(3, 2, 0),
                tuple(2, 3, 0),
                tuple(1, 2, 0),
                tuple(2, 1, 0)
        );

        assertThat(world.neighbours(tuple(-1, 0, 0))).containsExactlyInAnyOrder(
                tuple(0, 0, 0),
                tuple(2, 3, -1),
                tuple(1, 2, -1),
                tuple(2, 1, -1)
        );

        assertThat(world.neighbours(tuple(0, 5, 0))).containsExactlyInAnyOrder(
                tuple(3, 2, -1),
                tuple(2, 3, -1),
                tuple(1, 2, -1),
                tuple(0, 4, 0)
        );

        assertThat(world.neighbours(tuple(2, 1, 0))).containsExactlyInAnyOrder(
                tuple(3, 1, 0),
                tuple(0, 0, 1),
                tuple(1, 0, 1),
                tuple(2, 0, 1),
                tuple(3, 0, 1),
                tuple(4, 0, 1),
                tuple(1, 1, 0),
                tuple(2, 0, 0)
        );

        for (int i = 0; i < 200; i++) {
            world.tick();
        }

        assertThat(world.livingCells).hasSize(2047);
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
                    .collect(toSet());
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
            while (!previousWorlds.contains(livingCells)) {
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

    private static class ComplexWorld {

        private static final Tuple2<Integer, Integer> DOWN = tuple(0, +1);
        private static final Tuple2<Integer, Integer> RIGHT = tuple(+1, 0);
        private static final Tuple2<Integer, Integer> LEFT = tuple(-1, 0);
        private static final Tuple2<Integer, Integer> UP = tuple(0, -1);

        private static final Map<Tuple2<Integer, Integer>, Function<Integer, Tuple2<Integer, Integer>>>
                INNER_NEIGHBOUR_MAPPER = Map.of(
                RIGHT, (i) -> tuple(0, i),
                LEFT, (i) -> tuple(4, i),
                UP, (i) -> tuple(i, 4),
                DOWN, (i) -> tuple(i, 0)
        );

        private List<Tuple2<Integer, Integer>> NEIGHBOUR_DELTA = List.of(
                LEFT,
                UP,
                DOWN,
                RIGHT
        );

        private Set<Tuple3<Integer, Integer, Integer>> livingCells = new HashSet<>();

        public ComplexWorld(String input) {
            var lines = input.split("\n");
            int y = 0;
            for (String line : lines) {
                int x = 0;
                for (String cell : line.split("")) {
                    if ("#".equals(cell)) {
                        livingCells.add(tuple(x, y, 0));
                    }
                    x++;
                }
                y++;
            }
        }

        public void tick() {
            livingCells = neighboursCounter().entrySet().stream()
                    .filter(entry -> isAlive(entry.getKey(), entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(toSet());
        }

        public boolean isAlive(Tuple3<Integer, Integer, Integer> neighbour, Integer counter) {
            return livingCells.contains(neighbour) ? counter == 1 : (counter == 1 || counter == 2);
        }

        public HashMap<Tuple3<Integer, Integer, Integer>, Integer> neighboursCounter() {
            var neighboursCounter = new HashMap<Tuple3<Integer, Integer, Integer>, Integer>();

            for (Tuple3<Integer, Integer, Integer> cell : livingCells) {
                for (Tuple3<Integer, Integer, Integer> neighbour : neighbours(cell)) {
                    var counter = neighboursCounter.getOrDefault(neighbour, 0);
                    neighboursCounter.put(neighbour, counter + 1);
                }
            }

            return neighboursCounter;
        }

        private Set<Tuple3<Integer, Integer, Integer>> neighbours(Tuple3<Integer, Integer, Integer> cell) {
            var neighbours = new HashSet<Tuple3<Integer, Integer, Integer>>();
            var level = cell.v3;

            for (Tuple2<Integer, Integer> delta : NEIGHBOUR_DELTA) {
                int x = cell.v1 + delta.v1;
                int y = cell.v2 + delta.v2;

                if (Stream.of(x, y).allMatch(v -> v == 2)) {
                    neighbours.addAll(innerWorldNeighbours(delta, level));
                } else if (Stream.of(x, y).anyMatch(v -> v < 0 || v > 4)) {
                    neighbours.addAll(outWorldNeighbours(delta, level));
                } else {
                    neighbours.add(tuple(x, y, level));
                }
            }

            return neighbours;
        }

        private Set<Tuple3<Integer, Integer, Integer>> outWorldNeighbours(Tuple2<Integer, Integer> delta, Integer level) {
            var center = tuple(2, 2);
            return Set.of(tuple(center.v1 + delta.v1, center.v1 + delta.v2, level - 1));
        }

        private Set<Tuple3<Integer, Integer, Integer>> innerWorldNeighbours(Tuple2<Integer, Integer> delta, Integer level) {
            return Seq.range(0, 5)
                    .map(INNER_NEIGHBOUR_MAPPER.get(delta))
                    .map(t -> tuple(t.v1, t.v2, level + 1))
                    .toSet();
        }
    }
}
