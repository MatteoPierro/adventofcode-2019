package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import it.matteopierro.robot.Direction;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class OxygenSystemTest {


    @Test
    void firstPuzzle() throws Exception {
        String program = Files.readString(Paths.get("./input_day15"));
        var droid = new Droid(program);
        var ship = new ShipState(droid);
        ship.exploreShip();

        assertThat(ship.distanceToOxygen()).isEqualTo(208);
    }

    private static class Droid extends ComputerListener {
        private static final Tuple2<Integer, Integer> STARTING_POSITION = tuple(0, 0);

        private final BlockingQueue<String> input = new LinkedBlockingQueue<>();
        private final BlockingQueue<String> output = new LinkedBlockingQueue<>();
        private Tuple2<Integer, Integer> location = STARTING_POSITION;

        public Droid(String program) {
            new Thread(() -> {
                var computer = new Computer();
                computer.execute(program, Droid.this);
            }).start();
        }

        public State move(Direction direction) throws InterruptedException {
            input.add(ordinal(direction));
            var state = State.of(output.take());
            if (state != State.WALL) {
                location = direction.move(location);
            }
            return state;
        }

        public Tuple2<Integer, Integer> getLocation() {
            return location;
        }

        @Override
        public String onReadRequested() {
            try {
                return input.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            output.add(result);
        }
    }

    private static class ShipState {
        private final Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        private final Stack<Tuple2<Integer, Integer>> tilesToVisit = new Stack<>();
        private final Set<Tuple2<Integer, Integer>> tilesVisited = new HashSet<>();
        private final Droid droid;
        private Tuple2<Integer, Integer> oxygen = null;

        ShipState(Droid droid) {
            this.droid = droid;
            graph.addVertex(droid.getLocation());
            tilesToVisit.addAll(adjacentPoints(droid.getLocation()));
        }

        private Set<Tuple2<Integer, Integer>> adjacentPoints(Tuple2<Integer, Integer> location) {
            return Arrays.stream(Direction.values())
                    .map( d -> d.move(location))
                    .collect(toSet());
        }

        private synchronized void exploreShip() throws InterruptedException {
            while (!tilesToVisit.isEmpty()) {
                // Find the next point we need to explore
                var tile = tilesToVisit.pop();

                // Don't explore previously explored locations
                if (!tilesVisited.add(tile)) {
                    continue;
                }

                // Travel to our exploration point
                var directions = getDirections(tile).iterator();
                State state = null;
                Tuple2<Integer, Integer> previousLocation = null;
                while (directions.hasNext()) {
                    var direction = directions.next();
                    previousLocation = droid.getLocation();
                    state = droid.move(direction);
                }

                if (state == State.WALL) {
                    continue; // Node is not reachable
                }

                // Place the node into our graph
                graph.addVertex(droid.getLocation());
                graph.addEdge(droid.getLocation(), previousLocation);

                // Mark the oxygen system if we've found it
                if (state == State.OXYGEN_SYSTEM) {
                    oxygen = droid.getLocation();
                }

                // Enqueue children
                for (var direction : Direction.values()) {
                    tilesToVisit.push(direction.move(droid.getLocation()));
                }
            }
        }

        synchronized int distanceToOxygen() throws InterruptedException {
            if (oxygen == null) {
                exploreShip();
            }
            return new DijkstraShortestPath<>(graph)
                    .getPaths(Droid.STARTING_POSITION)
                    .getPath(oxygen)
                    .getLength();
        }

        private List<Direction> getDirections(Tuple2<Integer, Integer> tile) {
            // If we're 1 move away, then just go there
            for (var direction : Direction.values()) {
                if (tile.equals(direction.move(droid.getLocation()))) {
                    return List.of(direction);
                }
            }

            // If we are more than 1 move away, then find the shortest path to get to any known-good square that's 1 square
            // away, and then just make the last move.
            var djikstra = new DijkstraShortestPath<>(graph).getPaths(droid.getLocation());
            return Arrays.stream(Direction.values())
                    .filter(direction -> graph.containsVertex(direction.move(tile)))
                    .map(direction -> Optional.ofNullable(djikstra.getPath(direction.move(tile)))
                            .map(GraphPath::getVertexList)
                            .map(pointsInPath -> {
                                var path = new ArrayList<Direction>();
                                for (var i = 0; i < pointsInPath.size() - 1; i++) {
                                    path.add(Direction.fromSegment(pointsInPath.get(i), pointsInPath.get(i + 1)));
                                }
                                path.add(direction.opposite());
                                return path;
                            })
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .min(Comparator.comparingInt(List::size))
                    .orElseThrow(() -> new IllegalStateException(String.format("Couldn't find path to %s", tile)));
        }
    }


    enum State {
        WALL,
        EMPTY,
        OXYGEN_SYSTEM;

        private static State of(String value) {
            switch (value) {
                case "0": return WALL;
                case "1": return EMPTY;
                case "2": return OXYGEN_SYSTEM;
            }
            throw new IllegalArgumentException("Unknown state value: " + value);
        }
    }

    private static String ordinal(Direction direction) {
        switch (direction) {
            case NORTH: return "1";
            case SOUTH: return "2";
            case WEST: return "3";
            case EAST: return "4";
        }
        throw new UnsupportedOperationException("Unknown direction: " + direction);
    }
}
