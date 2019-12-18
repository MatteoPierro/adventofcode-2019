package it.matteopierro;

import com.google.common.collect.Sets;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.difference;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
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

        Maze maze = new Maze(input);

        assertThat(maze.keys().keySet()).containsExactlyInAnyOrder("b", "a");
        assertThat(singleton(maze.keys().get("a"))).containsExactly(tuple(7, 1));
        assertThat(singleton(maze.keys().get("b"))).containsExactly(tuple(1, 1));
        assertThat(maze.doors().keySet()).containsExactlyInAnyOrder("A");
        assertThat(singleton(maze.doors().get("A"))).containsExactly(tuple(3, 1));
        assertThat(singleton(maze.keys().get("a"))).containsExactly(tuple(7, 1));
        assertThat(singleton(maze.entrance())).containsExactly(tuple(5, 1));
        assertThat(maze.tiles()).containsExactlyInAnyOrder(
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

        assertThat(calculateSteps(input)).isEqualTo(6);
    }

    @Test
    void calculateStepsLargerExample() {
        String input =
                "########################\n" +
                        "#...............b.C.D.f#\n" +
                        "#.######################\n" +
                        "#.....@.a.B.c.d.A.e.F.g#\n" +
                        "########################";

        assertThat(calculateSteps(input)).isEqualTo(6);
    }

    private int calculateSteps(String input) {
        Maze maze = new Maze(input);
        return maze.stepsToCatchKeys();
    }

    private class Maze {
        private Map<String, Tuple2<Integer, Integer>> keys = new HashMap<>();
        private Map<String, Tuple2<Integer, Integer>> doors = new HashMap<>();
        private Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();
        private Tuple2<Integer, Integer> entrance;

        public Maze(String input) {
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

        public int stepsToCatchKeys(Tuple2<Integer, Integer> location, Set<String> remainingKeys) {
            if (remainingKeys.isEmpty()) {
                return 0;
            }
            var graph = graph(difference(keys.keySet(), remainingKeys));

            var reachableKeys = reachableKeys(remainingKeys);
            var dijkstra = new DijkstraShortestPath<>(graph).getPaths(location);
            return reachableKeys.stream()
                    .mapToInt(key -> distanceToKey(dijkstra, key)
                            + stepsToCatchKeys(keys.get(key), Sets.difference(remainingKeys, Set.of(key))))
                    .min()
                    .orElse(0);
        }

        public int distanceToKey(ShortestPathAlgorithm.SingleSourcePaths<Tuple2<Integer, Integer>, DefaultEdge> dijkstra, String key) {
            return dijkstra.getPath(keys.get(key)).getLength();
        }

        private Set<String> reachableKeys(Set<String> remainingKeys) {
            var graph = graph(difference(keys.keySet(), remainingKeys));
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
                    if (dijkstra.getPath(keys.get(key1)).getVertexList().contains(keys.get(key2))) {
                        it1.remove(); // Key 1 is reachable only through Key 2
                        break;
                    }
                }
            }

            return reachable;
        }

        private Graph<Tuple2<Integer, Integer>, DefaultEdge> graph(Set<String> gotKeys) {
            Set<Tuple2<Integer, Integer>> closedPositions = closedPosition(gotKeys);
            Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
            tiles.forEach(graph::addVertex);
            tiles.stream()
                    .filter(tile -> !closedPositions.contains(tile))
                    .forEach(tile ->
                            adjacentPositions(tile).forEach(adjacent -> graph.addEdge(tile, adjacent))
                    );
            return graph;
        }

        private Set<Tuple2<Integer, Integer>> closedPosition(Set<String> gotKeys) {
            Set<Tuple2<Integer, Integer>> closedPositions = new HashSet<>();
            for (String door : doors.keySet()) {
                if (gotKeys.contains(door.toLowerCase())) continue;

                closedPositions.add(doors.get(door));
            }

            return closedPositions;
        }

        private List<Tuple2<Integer, Integer>> adjacentPositions(Tuple2<Integer, Integer> doorPosition) {
            return Stream.of(tuple(1, 0), tuple(0, 1), tuple(-1, 0), tuple(0, -1))
                    .map(t -> tuple(doorPosition.v1 + t.v1, doorPosition.v2 + t.v2))
                    .filter(tiles::contains)
                    .collect(toList());
        }
    }
}
