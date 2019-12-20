package it.matteopierro;

import it.matteopierro.robot.Direction;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.*;

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
    }

    private class Maze {


        public final Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();
        public final Map<String, List<Tuple2<Integer, Integer>>> portals = new HashMap<>();
        public final Map<Tuple2<Integer, Integer>, String> reversePortals = new HashMap<>();

        public Maze(String input) {
            String[] lines = input.split("\n");
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
            tiles.forEach(tile ->
                    adjacentPoints(tile).forEach(a -> graph.addEdge(tile, a))
            );
            return graph;
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
            Graph<Tuple2<Integer, Integer>, DefaultEdge> graph = graph();
            return new DijkstraShortestPath<>(graph).getPath(startingPoint(), endPoint());
        }
    }
}
