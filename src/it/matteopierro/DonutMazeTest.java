package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.*;

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
                            if (line[x + 1] == '.') {
                                String mark = line[x - 1] + "" + element;
                                Tuple2<Integer, Integer> position = tuple(x + 1, y);
                                addPortal(mark, position);
                            }
                        }
                        if (x - 1 > 0 && line[x - 1] == '.') {
                            String mark =  element + "" + line[x + 1];
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
    }
}
