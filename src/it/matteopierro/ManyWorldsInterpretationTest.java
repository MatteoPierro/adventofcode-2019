package it.matteopierro;

import it.matteopierro.robot.Direction;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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

    @Test
    @Disabled
    void secondPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day18"));

        assertThat(calculateStepsComplex(input)).isEqualTo(1540);
    }

    private int calculateSteps(String input) {
        Vault vault = new Vault(input);
        return vault.stepsToCatchKeys();
    }

    private int calculateStepsComplex(String input) {
        Vault vault = new ComplexVault(input);
        return vault.stepsToCatchKeys();
    }

    private class Vault {
        protected final Map<String, Tuple2<Integer, Integer>> keys = new HashMap<>();
        private final Map<String, Tuple2<Integer, Integer>> doors = new HashMap<>();
        protected final Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();
        protected Tuple2<Integer, Integer> entrance;

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
            var reachableKeys = reachableKeys(location, remainingKeys, graph);
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

        protected Set<String> reachableKeys(Tuple2<Integer, Integer> location, Set<String> remainingKeys, Graph<Tuple2<Integer, Integer>, DefaultEdge> graph) {
            var connectivity = new ConnectivityInspector<>(graph);
            var reachable = remainingKeys.stream()
                    .filter(key -> connectivity.pathExists(location, keys.get(key)))
                    .collect(toSet());

            var dijkstra = new DijkstraShortestPath<>(graph).getPaths(location);

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
            GraphPath<Tuple2<Integer, Integer>, DefaultEdge> path = dijkstra.getPath(keys.get(key1));
            List<Tuple2<Integer, Integer>> vertexList = path.getVertexList();
            return vertexList.contains(keys.get(key2));
        }

        protected Graph<Tuple2<Integer, Integer>, DefaultEdge> graph(Set<String> remainingKeys) {
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

    public class ComplexVault extends Vault {

        private final Tuple2<Integer, Integer> entrance1;
        private final Tuple2<Integer, Integer> entrance2;
        private final Tuple2<Integer, Integer> entrance3;
        private final Tuple2<Integer, Integer> entrance4;

        private ComplexVault(String input) {
            super(input);
            tiles.remove(Direction.NORTH.move(entrance));
            tiles.remove(Direction.SOUTH.move(entrance));
            tiles.remove(Direction.EAST.move(entrance));
            tiles.remove(Direction.WEST.move(entrance));
            tiles.remove(entrance);
            entrance1 = tuple(entrance.v1 - 1, entrance.v2 - 1);
            entrance2 = tuple(entrance.v1 - 1, entrance.v2 + 1);
            entrance3 = tuple(entrance.v1 + 1, entrance.v2 - 1);
            entrance4 = tuple(entrance.v1 + 1, entrance.v2 + 1);
        }

        private final Map<ComplexProblem, Long> resolvedProblems = new ConcurrentHashMap<>();

        @Override
        public int stepsToCatchKeys() {
            ComplexProblem problem = new ComplexProblem(entrance1, entrance2,
                    entrance3, entrance4, keys.keySet());
            return (int) stepsToCatchKeys(problem);
        }

        private long stepsToCatchKeys(ComplexProblem problem) {
            if (problem.remainingKeys.isEmpty()) {
                return 0;
            }
            if (resolvedProblems.containsKey(problem)) {
                return resolvedProblems.get(problem);
            }

            var graph = graph(problem.remainingKeys);

            var reachable1 = reachableKeys(problem.robot1, problem.remainingKeys, graph);
            var reachable2 = reachableKeys(problem.robot2, problem.remainingKeys, graph);
            var reachable3 = reachableKeys(problem.robot3, problem.remainingKeys, graph);
            var reachable4 = reachableKeys(problem.robot4, problem.remainingKeys, graph);

            var dijkstra1 = new DijkstraShortestPath<>(graph).getPaths(problem.robot1);
            var dijkstra2 = new DijkstraShortestPath<>(graph).getPaths(problem.robot2);
            var dijkstra3 = new DijkstraShortestPath<>(graph).getPaths(problem.robot3);
            var dijkstra4 = new DijkstraShortestPath<>(graph).getPaths(problem.robot4);

            Function<String, ComplexProblem> input1 = key ->
                    new ComplexProblem(keys.get(key), problem.robot2, problem.robot3, problem.robot4, difference(problem.remainingKeys, Set.of(key)));
            Function<String, ComplexProblem> input2 = key ->
                    new ComplexProblem(problem.robot1, keys.get(key), problem.robot3, problem.robot4, difference(problem.remainingKeys, Set.of(key)));
            Function<String, ComplexProblem> input3 = key ->
                    new ComplexProblem(problem.robot1, problem.robot2, keys.get(key), problem.robot4, difference(problem.remainingKeys, Set.of(key)));
            Function<String, ComplexProblem> input4 = key ->
                    new ComplexProblem(problem.robot1, problem.robot2, problem.robot3, keys.get(key), difference(problem.remainingKeys, Set.of(key)));

            var result1 = reachable1.stream()
                    .mapToLong(key -> dijkstra1.getPath(keys.get(key)).getLength() + stepsToCatchKeys(input1.apply(key)))
                    .min()
                    .orElse(Integer.MAX_VALUE);
            var result2 = reachable2.stream()
                    .mapToLong(key -> dijkstra2.getPath(keys.get(key)).getLength() + stepsToCatchKeys(input2.apply(key)))
                    .min()
                    .orElse(Integer.MAX_VALUE);
            var result3 = reachable3.stream()
                    .mapToLong(key -> dijkstra3.getPath(keys.get(key)).getLength() + stepsToCatchKeys(input3.apply(key)))
                    .min()
                    .orElse(Integer.MAX_VALUE);
            var result4 = reachable4.stream()
                    .mapToLong(key -> dijkstra4.getPath(keys.get(key)).getLength() + stepsToCatchKeys(input4.apply(key)))
                    .min()
                    .orElse(Integer.MAX_VALUE);

            var result = Math.min(Math.min(Math.min(result1, result2), result3), result4);
            resolvedProblems.put(problem, result);
            return result;
        }

        private class ComplexProblem {
            private final Tuple2<Integer, Integer> robot1;
            private final Tuple2<Integer, Integer> robot2;
            private final Tuple2<Integer, Integer> robot3;
            private final Tuple2<Integer, Integer> robot4;
            private final Set<String> remainingKeys;

            public ComplexProblem(Tuple2<Integer, Integer> robot1,
                                  Tuple2<Integer, Integer> robot2,
                                  Tuple2<Integer, Integer> robot3,
                                  Tuple2<Integer, Integer> robot4,
                                  Set<String> remainingKeys) {
                this.robot1 = robot1;
                this.robot2 = robot2;
                this.robot3 = robot3;
                this.robot4 = robot4;
                this.remainingKeys = remainingKeys;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ComplexProblem that = (ComplexProblem) o;
                return Objects.equals(robot1, that.robot1) &&
                        Objects.equals(robot2, that.robot2) &&
                        Objects.equals(robot3, that.robot3) &&
                        Objects.equals(robot4, that.robot4) &&
                        Objects.equals(remainingKeys, that.remainingKeys);
            }

            @Override
            public int hashCode() {
                return Objects.hash(robot1, robot2, robot3, robot4, remainingKeys);
            }
        }
    }
}
