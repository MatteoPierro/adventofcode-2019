package it.matteopierro;

import com.google.common.collect.Sets;
import it.matteopierro.robot.Direction;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class DonutMazeTest {

    @Test
    void shouldFindAllTiles() {
        var input =
                "         A           \n" +
                        "         A           \n" +
                        "  #######.#########  \n" +
                        "  #######.........#  \n" +
                        "  #######.#######.#  \n" +
                        "  #######.#######.#  \n" +
                        "  #######.#######.#  \n" +
                        "  #####  B    ###.#  \n" +
                        "BC...##  C    ###.#  \n" +
                        "  ##.##       ###.#  \n" +
                        "  ##...DE  F  ###.#  \n" +
                        "  #####    G  ###.#  \n" +
                        "  #########.#####.#  \n" +
                        "DE..#######...###.#  \n" +
                        "  #.#########.###.#  \n" +
                        "FG..#########.....#  \n" +
                        "  ###########.#####  \n" +
                        "             Z       \n" +
                        "             Z       ";

        Maze maze = new Maze(input);

        assertThat(maze.tiles).hasSize(47);
        assertThat(maze.portals.keySet()).containsExactlyInAnyOrder("AA", "BC", "FG", "DE", "ZZ");
        assertThat(maze.portals.get("AA")).containsExactly(tuple(9, 2));
        assertThat(maze.portals.get("BC")).containsExactly(tuple(2, 8), tuple(9, 6));
        assertThat(maze.portals.get("DE")).containsExactly(tuple(6, 10), tuple(2, 13));
        assertThat(maze.portals.get("FG")).containsExactly(tuple(11, 12), tuple(2, 15));
        assertThat(maze.portals.get("ZZ")).containsExactly(tuple(13, 16));
        assertThat(maze.reversePortals.keySet()).hasSize(8);
        assertThat(maze.reversePortals.get(tuple(9, 2))).isEqualTo("AA");
        assertThat(maze.reversePortals.get(tuple(2, 8))).isEqualTo("BC");
        assertThat(maze.reversePortals.get(tuple(13, 16))).isEqualTo("ZZ");
        assertThat(maze.adjacentPoints(tuple(9, 2))).containsExactlyInAnyOrder(tuple(9, 3));
        assertThat(maze.adjacentPoints(tuple(9, 3))).containsExactlyInAnyOrder(tuple(9, 2), tuple(9, 4), tuple(10, 3));
        assertThat(maze.adjacentPoints(tuple(9, 6))).containsExactlyInAnyOrder(tuple(9, 5), tuple(2, 8));
        assertThat(maze.adjacentPoints(tuple(2, 8))).containsExactlyInAnyOrder(tuple(3, 8), tuple(9, 6));
        var shortestPath = maze.shortestPath();
        assertThat(shortestPath.getLength()).isEqualTo(23);
        assertThat(maze.outerPortalsPositions).containsExactlyInAnyOrder(tuple(2,15), tuple(2, 13), tuple(2, 8));
        assertThat(maze.innerPortalsPositions).contains(tuple(9, 6));
        assertThat(maze.innerPortalsPositions).hasSize(3);
        assertThat(new InceptionMaze(input).shortestPathLength()).isEqualTo(26);
//        assertThat(shortestPath.getEdgeList()).containsExactly();
    }

    @Test
    void secondExample() {
        var input =
                "                   A               \n" +
                "                   A               \n" +
                "  #################.#############  \n" +
                "  #.#...#...................#.#.#  \n" +
                "  #.#.#.###.###.###.#########.#.#  \n" +
                "  #.#.#.......#...#.....#.#.#...#  \n" +
                "  #.#########.###.#####.#.#.###.#  \n" +
                "  #.............#.#.....#.......#  \n" +
                "  ###.###########.###.#####.#.#.#  \n" +
                "  #.....#        A   C    #.#.#.#  \n" +
                "  #######        S   P    #####.#  \n" +
                "  #.#...#                 #......VT\n" +
                "  #.#.#.#                 #.#####  \n" +
                "  #...#.#               YN....#.#  \n" +
                "  #.###.#                 #####.#  \n" +
                "DI....#.#                 #.....#  \n" +
                "  #####.#                 #.###.#  \n" +
                "ZZ......#               QG....#..AS\n" +
                "  ###.###                 #######  \n" +
                "JO..#.#.#                 #.....#  \n" +
                "  #.#.#.#                 ###.#.#  \n" +
                "  #...#..DI             BU....#..LF\n" +
                "  #####.#                 #.#####  \n" +
                "YN......#               VT..#....QG\n" +
                "  #.###.#                 #.###.#  \n" +
                "  #.#...#                 #.....#  \n" +
                "  ###.###    J L     J    #.#.###  \n" +
                "  #.....#    O F     P    #.#...#  \n" +
                "  #.###.#####.#.#####.#####.###.#  \n" +
                "  #...#.#.#...#.....#.....#.#...#  \n" +
                "  #.#####.###.###.#.#.#########.#  \n" +
                "  #...#.#.....#...#.#.#.#.....#.#  \n" +
                "  #.###.#####.###.###.#.#.#######  \n" +
                "  #.#.........#...#.............#  \n" +
                "  #########.###.###.#############  \n" +
                "           B   J   C               \n" +
                "           U   P   P               ";

        var maze = new Maze(input);

        assertThat(maze.shortestPath().getLength()).isEqualTo(58);
        assertThat(maze.outerPortalsPositions.size()).isEqualTo(10);
        assertThat(maze.outerPortals()).containsExactlyInAnyOrder("DI", "JO", "YN", "BU", "JP", "CP", "VT", "AS", "LF", "QG");
        assertThat(new InceptionMaze(input).shortestPathLength()).isEqualTo(MAX_VALUE);
    }

    @Test
    void firstPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day20"));

        var maze = new Maze(input);

        assertThat(maze.shortestPath().getLength()).isEqualTo(422);
        assertThat(maze.outerPortalsPositions.size()).isEqualTo(27);
        assertThat(maze.innerPortalsPositions.size()).isEqualTo(27);
        assertThat(new InceptionMaze(input).shortestPathLength()).isEqualTo(MAX_VALUE);
    }

    private class Maze {


        public final Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();
        public final Map<String, List<Tuple2<Integer, Integer>>> portals = new HashMap<>();
        public final Map<Tuple2<Integer, Integer>, String> reversePortals = new HashMap<>();
        public final int maxX;
        public final int maxY;
        public final Set<Tuple2<Integer, Integer>> outerPortalsPositions;
        public final Set<Tuple2<Integer, Integer>> innerPortalsPositions;

        public Maze(String input) {
            String[] lines = input.split("\n");
            maxY = lines.length - 3;
            maxX = lines[0].length() - 3;
            for (int y = 0; y < lines.length; y++) {
                char[] line = lines[y].toCharArray();
                for (int x = 0; x < line.length; x++) {
                    char element = line[x];
                    if (element == '.') {
                        tiles.add(tuple(x, y));
                    }
                    if (isLetter(element)) {
                        if (x > 0 && isLetter(line[x - 1])) {
                            if (x+1 < line.length && line[x + 1] == '.') {
                                String mark = line[x - 1] + "" + element;
                                Tuple2<Integer, Integer> position = tuple(x + 1, y);
                                addPortal(mark, position);
                            }
                        }
                        if (x - 1 > 0 && line[x - 1] == '.') {
                            String mark = element + "" + line[x + 1];
                            Tuple2<Integer, Integer> position = tuple(x - 1, y);
                            addPortal(mark, position);
                        }
                        if (y > 0 && isLetter(lines[y - 1].toCharArray()[x])) {
                            if (y + 1 < lines.length && lines[y + 1].toCharArray()[x] == '.') {
                                String mark = lines[y - 1].toCharArray()[x] + "" + element;
                                addPortal(mark, tuple(x, y + 1));
                            }
                            if (y - 2 > 0 && lines[y - 2].toCharArray()[x] == '.') {
                                String mark = lines[y - 1].toCharArray()[x] + "" + element;
                                addPortal(mark, tuple(x, y - 2));
                            }
                        }
                    }
                }
            }
            outerPortalsPositions = outerPortalPositions();
            innerPortalsPositions = new HashSet<>(Sets.difference(reversePortals.keySet(), outerPortalsPositions));
            outerPortalsPositions.remove(portals.get("AA").get(0));
            outerPortalsPositions.remove(portals.get("ZZ").get(0));
        }

        public void addPortal(String mark, Tuple2<Integer, Integer> position) {
            List<Tuple2<Integer, Integer>> elements = portals.getOrDefault(mark, new ArrayList<>());
            elements.add(position);
            portals.put(mark, elements);
            reversePortals.put(position, mark);
        }

        public boolean isLetter(char element) {
            return element >= 'A' && element <= 'Z';
        }

        public Graph<Tuple2<Integer, Integer>, DefaultEdge> graph() {
            Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);
            tiles.forEach(graph::addVertex);
            tiles().forEach(tile ->
                    adjacentPoints(tile).forEach(a -> graph.addEdge(tile, a))
            );
            return graph;
        }

        public Set<Tuple2<Integer, Integer>> tiles() {
            return tiles;
        }

        public Tuple2<Integer, Integer> startingPoint() {
            return portals.get("AA").get(0);
        }

        public Tuple2<Integer, Integer> endPoint() {
            return portals.get("ZZ").get(0);
        }

        public Set<Tuple2<Integer, Integer>> adjacentPoints(Tuple2<Integer, Integer> point) {
            Set<Tuple2<Integer, Integer>> adjacents = Arrays.stream(Direction.values())
                    .map(direction -> direction.move(point))
                    .filter(tiles::contains)
                    .collect(toSet());
            if (reversePortals.containsKey(point)) {
                String letter = reversePortals.get(point);
                List<Tuple2<Integer, Integer>> portal = portals.get(letter);
                if (portal.size() != 1) {
                    Tuple2<Integer, Integer> adjacent = portal.get(0).equals(point) ? portal.get(1) : portal.get(0);
                    adjacents.add(adjacent);
                }
            }
            return adjacents;
        }

        public GraphPath<Tuple2<Integer, Integer>, DefaultEdge> shortestPath() {
            return shortestPath(startingPoint(), endPoint());
        }

        public GraphPath<Tuple2<Integer, Integer>, DefaultEdge> shortestPath(Tuple2<Integer, Integer> source, Tuple2<Integer, Integer> destination) {
            Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = graph();
            return new DijkstraShortestPath<>(graph).getPath(source, destination);
        }

        private Set<Tuple2<Integer, Integer>> outerPortalPositions() {
            var result = new HashSet<Tuple2<Integer, Integer>>();

            for (Tuple2<Integer, Integer> portal : reversePortals.keySet()) {
                if (portal.v1 == 2) {
                    result.add(portal);
                }
                if (portal.v1 == maxX) {
                    result.add(portal);
                }
                if (portal.v2 == 2) {
                    result.add(portal);
                }
                if (portal.v2 == maxY) {
                    result.add(portal);
                }
            }

            return result;
        }

        private List<String> outerPortals() {
            return outerPortalsPositions.stream().map(reversePortals::get).collect(Collectors.toList());
        }
    }

    private class InceptionMaze extends Maze{
        private int level;

        public InceptionMaze(String input) {
            super(input);
            this.level = 0;
        }

        public int shortestPathLength() {
            List<Problem> problems = new ArrayList<>();
            problems.add(new Problem(0, startingPoint(), new ArrayList<>(), new HashSet<>(), new HashSet<>(), new HashSet<>()));

            while (!problems.isEmpty()) {
                var problem = problems.get(0);
                problems.remove(0);//better Linked List?
                level = problem.level;
                var currentPosition = problem.currentPosition;
                var currentPath = problem.path;
                var inDoors = problem.inDoors;
                var outDoors = problem.outDoors;
                if (level == 0) {
                    var path = shortestPath(currentPosition, endPoint());
                    if (path != null) {
                        return currentPath.size() + path.getLength();
                    }
                }
                Set<Tuple2<Integer, Integer>> currentUsed = problem.usedPositions;
                for (Tuple2<Integer, Integer> position : innerPortalsPositions) {
                    if (inDoors.contains(reversePortals.get(position))) continue; //don't reuse ports
                    if (currentPosition.equals(position)) continue;
                    GraphPath<Tuple2<Integer, Integer>, DefaultEdge> path = shortestPath(currentPosition, position);
                    if (path == null) continue;
//                    if (isLoop(currentUsed, path, currentPosition)) continue;
                    if (Sets.difference(innerPortalsPositions, Set.of(currentPosition, position)).stream()
                        .anyMatch(p -> path.getVertexList().contains(p))) continue;

                    if (Sets.difference(outerPortalsPositions, Set.of(currentPosition)).stream() //no outdoors here
                            .anyMatch(p -> path.getVertexList().contains(p))) continue;

                    var newUsedPositions = new HashSet<>(currentUsed);
                    newUsedPositions.addAll(path.getVertexList());
                    newUsedPositions.add(position);
                    var newPath = new ArrayList<>(currentPath);
                    newPath.addAll(path.getVertexList());
                    var symbol = reversePortals.get(position);
                    List<Tuple2<Integer, Integer>> portal = portals.get(symbol);
                    var newPosition = portal.get(0).equals(position) ? portal.get(1) : portal.get(0);
                    HashSet<String> newInDoors = new HashSet<>(inDoors);
                    newInDoors.add(symbol);
                    problems.add(new Problem(level + 1, newPosition, newPath, newUsedPositions, newInDoors, outDoors));
                }

                for (Tuple2<Integer, Integer> position : outerPortalsPositions) {
                    if (currentPosition.equals(position)) continue;
                    if (reversePortals.get(currentPosition).equals(reversePortals.get(position))) continue;
                    GraphPath<Tuple2<Integer, Integer>, DefaultEdge> path = shortestPath(currentPosition, position);
                    if (path == null) continue;
//                    if (isLoop(currentUsed, path, currentPosition)) continue;
                    if (Sets.difference(outerPortalsPositions, Set.of(currentPosition, position)).stream()
                            .anyMatch(p -> path.getVertexList().contains(p))) continue;

                    if (Sets.difference(innerPortalsPositions, Set.of(currentPosition)).stream() //no indoors here
                            .anyMatch(p -> path.getVertexList().contains(p))) continue;

                    var newUsedPositions = new HashSet<>(currentUsed);
                    newUsedPositions.addAll(path.getVertexList());
                    newUsedPositions.add(position);
                    var newPath = new ArrayList<>(currentPath);
                    newPath.addAll(path.getVertexList());
                    var symbol = reversePortals.get(position);
                    List<Tuple2<Integer, Integer>> portal = portals.get(symbol);
                    var newPosition = portal.get(0).equals(position) ? portal.get(1) : portal.get(0);
                    HashSet<String> newOutDoors = new HashSet<>(outDoors);
                    newOutDoors.add(symbol);
                    problems.add(new Problem(level - 1, newPosition, newPath, newUsedPositions, inDoors, newOutDoors));
                }
            }

            return MAX_VALUE;
        }

        public boolean isLoop(Set<Tuple2<Integer, Integer>> currentUsed, GraphPath<Tuple2<Integer, Integer>, DefaultEdge> path, Tuple2<Integer, Integer> currentPosition) {
            return path.getVertexList().stream()
                    .filter(t -> !t.equals(currentPosition))
                    .anyMatch(currentUsed::contains);
        }

        @Override
        public Set<Tuple2<Integer, Integer>> adjacentPoints(Tuple2<Integer, Integer> point) {
            var points = super.adjacentPoints(point);
            if (level == 0) {
                points.removeAll(super.outerPortalsPositions);
            } else {
                points.remove(startingPoint());
                points.remove(endPoint());
            }
            return points;
        }

        @Override
        public Set<Tuple2<Integer, Integer>> tiles() {
            HashSet<Tuple2<Integer, Integer>> ts = new HashSet<>(tiles);
            if (level == 0) {
                ts.removeAll(super.outerPortalsPositions);
            } else {
                ts.remove(startingPoint());
                ts.remove(endPoint());
            }
            return ts;
        }

        private class Problem {

            private final int level;
            private final Tuple2<Integer, Integer> currentPosition;
            private final List<Tuple2<Integer, Integer>> path;
            private final HashSet<Tuple2<Integer, Integer>> usedPositions;
            private final HashSet<String> inDoors;
            private final HashSet<String> outDoors;

            public Problem(int level, Tuple2<Integer, Integer> currentPosition, List<Tuple2<Integer, Integer>> path, HashSet<Tuple2<Integer, Integer>> usedPositions, HashSet<String> inDoors, HashSet<String> outDoors) {
                this.level = level;
                this.currentPosition = currentPosition;
                this.path = path;
                this.usedPositions = usedPositions;
                this.inDoors = inDoors;
                this.outDoors = outDoors;
            }
        }
    }

    @Test
    void exampleSecondPart() {
        var input = "             Z L X W       C                 \n" +
                "             Z P Q B       K                 \n" +
                "  ###########.#.#.#.#######.###############  \n" +
                "  #...#.......#.#.......#.#.......#.#.#...#  \n" +
                "  ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###  \n" +
                "  #.#...#.#.#...#.#.#...#...#...#.#.......#  \n" +
                "  #.###.#######.###.###.#.###.###.#.#######  \n" +
                "  #...#.......#.#...#...#.............#...#  \n" +
                "  #.#########.#######.#.#######.#######.###  \n" +
                "  #...#.#    F       R I       Z    #.#.#.#  \n" +
                "  #.###.#    D       E C       H    #.#.#.#  \n" +
                "  #.#...#                           #...#.#  \n" +
                "  #.###.#                           #.###.#  \n" +
                "  #.#....OA                       WB..#.#..ZH\n" +
                "  #.###.#                           #.#.#.#  \n" +
                "CJ......#                           #.....#  \n" +
                "  #######                           #######  \n" +
                "  #.#....CK                         #......IC\n" +
                "  #.###.#                           #.###.#  \n" +
                "  #.....#                           #...#.#  \n" +
                "  ###.###                           #.#.#.#  \n" +
                "XF....#.#                         RF..#.#.#  \n" +
                "  #####.#                           #######  \n" +
                "  #......CJ                       NM..#...#  \n" +
                "  ###.#.#                           #.###.#  \n" +
                "RE....#.#                           #......RF\n" +
                "  ###.###        X   X       L      #.#.#.#  \n" +
                "  #.....#        F   Q       P      #.#.#.#  \n" +
                "  ###.###########.###.#######.#########.###  \n" +
                "  #.....#...#.....#.......#...#.....#.#...#  \n" +
                "  #####.#.###.#######.#######.###.###.#.#.#  \n" +
                "  #.......#.......#.#.#.#.#...#...#...#.#.#  \n" +
                "  #####.###.#####.#.#.#.#.###.###.#.###.###  \n" +
                "  #.......#.....#.#...#...............#...#  \n" +
                "  #############.#.#.###.###################  \n" +
                "               A O F   N                     \n" +
                "               A A D   M                     ";

        var maze = new InceptionMaze(input);

        assertThat(maze.shortestPathLength()).isEqualTo(-1);
    }
}
