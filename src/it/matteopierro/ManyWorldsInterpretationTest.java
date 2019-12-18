package it.matteopierro;

import it.matteopierro.robot.Direction;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.google.common.collect.Sets.difference;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class ManyWorldsInterpretationTest {

    @Test
    void findMazeElementPositions() {
        String input =
                "#########\n" +
                        "#b.A.@.a#\n" +
                        "#########";

        Vault vault = new Vault(input);

        assertThat(vault.keys().keySet()).containsExactlyInAnyOrder("b", "a");
        assertThat(singleton(vault.keys().get("a"))).containsExactly(tuple(7, 1));
        assertThat(singleton(vault.keys().get("b"))).containsExactly(tuple(1, 1));
        assertThat(vault.doors().keySet()).containsExactlyInAnyOrder("A");
        assertThat(singleton(vault.doors().get("A"))).containsExactly(tuple(3, 1));
        assertThat(singleton(vault.keys().get("a"))).containsExactly(tuple(7, 1));
        assertThat(singleton(vault.entrance())).containsExactly(tuple(5, 1));
        assertThat(vault.tiles()).containsExactlyInAnyOrder(
                tuple(1, 1),
                tuple(2, 1),
                tuple(3, 1),
                tuple(4, 1),
                tuple(5, 1),
                tuple(6, 1),
                tuple(7, 1));
    }

    @Test
    void catchAllKeysShortExample() {
        String input =
                "#########\n" +
                        "#b.A.@.a#\n" +
                        "#########";

        assertThat(calculateSteps(input)).isEqualTo(8);
    }

    @Test
    void calculateStepsSecondExample() {
        String input =
                "#########\n" +
                        "#b.....@#\n" +
                        "#########";

        assertThat(calculateSteps(input)).isEqualTo(6);
    }

    @Test
    void calculateStepsThirdExample() {
        String input =
                "########################\n" +
                        "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                        "######################.#\n" +
                        "#d.....................#\n" +
                        "########################";

        assertThat(calculateSteps(input)).isEqualTo(86);
    }

    @Test
    void calculateStepsLargerExample() {
        String input =
                "########################\n" +
                        "#...............b.C.D.f#\n" +
                        "#.######################\n" +
                        "#.....@.a.B.c.d.A.e.F.g#\n" +
                        "########################";

        assertThat(calculateSteps(input)).isEqualTo(132);
    }

    @Test
    @Disabled
    void calculateStepsLargerExampleNumberTwo() {
        String input =
                "#################\n" +
                        "#i.G..c...e..H.p#\n" +
                        "########.########\n" +
                        "#j.A..b...f..D.o#\n" +
                        "########@########\n" +
                        "#k.E..a...g..B.n#\n" +
                        "########.########\n" +
                        "#l.F..d...h..C.m#\n" +
                        "#################";

        assertThat(calculateSteps(input)).isEqualTo(136);
    }

    @Test
    void calculateStepsLargerExampleNumberThree() {
        String input =
                "########################\n" +
                        "#@..............ac.GI.b#\n" +
                        "###d#e#f################\n" +
                        "###A#B#C################\n" +
                        "###g#h#i################\n" +
                        "########################";

        assertThat(calculateSteps(input)).isEqualTo(81);
    }

    @Test
    @Disabled
    void firstPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day18"));

        assertThat(calculateSteps(input)).isEqualTo(4520);
    }

    private int calculateSteps(String input) {
        Vault vault = new Vault(input);
        return vault.stepsToCatchKeys();
    }

    private class Vault {
        private final Map<String, Tuple2<Integer, Integer>> keys = new HashMap<>();
        private final Map<String, Tuple2<Integer, Integer>> doors = new HashMap<>();
        private final Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();
        private Tuple2<Integer, Integer> entrance;

        private final Map<Problem, Integer> solvedProblems = new HashMap<>();

        private Vault(String input) {
            String[] lines = input.split("\n");
            for (int y = 0; y < lines.length; y++) {
                char[] line = lines[y].toCharArray();
                for (int x = 0; x < line.length; x++) {
                    if (line[x] >= 'a' && line[x] <= 'z') {
                        keys.put(String.valueOf(line[x]), tuple(x, y));
                    }
                    if (line[x] == '@') {
                        entrance = tuple(x, y);
                    }
                    if (line[x] >= 'A' && line[x] <= 'Z') {
                        doors.put(String.valueOf(line[x]), tuple(x, y));
                    }
                    if (line[x] != '#') {
                        tiles.add(tuple(x, y));
                    }
                }
            }
        }

        public Map<String, Tuple2<Integer, Integer>> keys() {
            return keys;
        }

        public Tuple2<Integer, Integer> entrance() {
            return entrance;
        }

        public Map<String, Tuple2<Integer, Integer>> doors() {
            return doors;
        }

        public Set<Tuple2<Integer, Integer>> tiles() {
            return tiles;
        }

        public int stepsToCatchKeys() {
            return stepsToCatchKeys(entrance, keys.keySet());
        }

        private int stepsToCatchKeys(Tuple2<Integer, Integer> location, Set<String> remainingKeys) {
            if (remainingKeys.isEmpty()) {
                return 0;
            }

            var problem = new Problem(location, remainingKeys);
            if (solvedProblems.containsKey(problem)) {
                return solvedProblems.get(problem);
            }

            Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = graph(remainingKeys);
            var reachableKeys = reachableKeys(remainingKeys, graph);
            var dijkstra = new DijkstraShortestPath<>(graph).getPaths(location);
            var result = reachableKeys.stream()
                    .mapToInt(key -> pathLength(dijkstra, key)
                            + stepsToCatchKeys(keys.get(key), difference(remainingKeys, Set.of(key))))
                    .min()
                    .orElse(0);
            solvedProblems.put(problem, result);
            return result;
        }

        private int pathLength(ShortestPathAlgorithm.SingleSourcePaths<Tuple2<Integer, Integer>, DefaultEdge> dijkstra, String key) {
            return dijkstra.getPath(keys.get(key)).getLength();
        }

        private Set<String> reachableKeys(Set<String> remainingKeys, Graph<Tuple2<Integer, Integer>, DefaultEdge> graph) {
            var connectivity = new ConnectivityInspector<>(graph);
            var reachable = remainingKeys.stream()
                    .filter(key -> connectivity.pathExists(entrance, keys.get(key)))
                    .collect(toSet());

            var dijkstra = new DijkstraShortestPath<>(graph).getPaths(entrance);

            var it1 = reachable.iterator();
            while (it1.hasNext()) {
                var key1 = it1.next();
                for (String key2 : reachable) {
                    if (key1.equals(key2)) {
                        continue;
                    }
                    if (isContainedInThePath(dijkstra, key1, key2)) {
                        it1.remove();
                        break;
                    }
                }
            }

            return reachable;
        }

        private boolean isContainedInThePath(ShortestPathAlgorithm.SingleSourcePaths<Tuple2<Integer, Integer>, DefaultEdge> dijkstra, String key1, String key2) {
            return dijkstra.getPath(keys.get(key1)).getVertexList().contains(keys.get(key2));
        }

        private Graph<Tuple2<Integer, Integer>, DefaultEdge> graph(Set<String> remainingKeys) {
            Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
            tiles.forEach(graph::addVertex);
            tiles.forEach(point ->
                    adjacentPoints(point).forEach(adjacent -> graph.addEdge(point, adjacent))
            );
            unopenedDoors(remainingKeys).forEach(door ->
                    adjacentPoints(door).forEach(adjacent -> graph.removeEdge(door, adjacent))
            );

            return graph;
        }

        private Set<Tuple2<Integer, Integer>> unopenedDoors(Set<String> remainingKeys) {
            Set<String> unopenedDoors = remainingKeys.stream().map(String::toUpperCase).filter(doors::containsKey).collect(toSet());
            return unopenedDoors.stream().map(doors::get).collect(toSet());
        }

        private Set<Tuple2<Integer, Integer>> adjacentPoints(Tuple2<Integer, Integer> point) {
            return Arrays.stream(Direction.values())
                    .map(direction -> direction.move(point))
                    .filter(tiles::contains)
                    .collect(toSet());
        }
    }

    private static class Problem {
        private final Tuple2<Integer, Integer> point;
        private final Set<String> remainingKeys;

        public Problem(Tuple2<Integer, Integer> point, Set<String> remainingKeys) {
            this.point = point;
            this.remainingKeys = remainingKeys;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Problem that = (Problem) o;
            return Objects.equals(point, that.point) &&
                    Objects.equals(remainingKeys, that.remainingKeys);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, remainingKeys);
        }
    }
}
