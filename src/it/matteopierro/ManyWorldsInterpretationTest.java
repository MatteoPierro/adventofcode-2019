package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;
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

        assertThat(maze.keys().keySet()).containsExactlyInAnyOrder('b', 'a');
        assertThat(singleton(maze.keys().get('a'))).containsExactly(tuple(7, 1));
        assertThat(singleton(maze.keys().get('b'))).containsExactly(tuple(1, 1));
        assertThat(maze.doors().keySet()).containsExactlyInAnyOrder('A');
        assertThat(singleton(maze.doors().get('A'))).containsExactly(tuple(3, 1));
        assertThat(singleton(maze.keys().get('a'))).containsExactly(tuple(7, 1));
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

    private class Maze {
        private Map<Character, Tuple2<Integer, Integer>> keys = new HashMap<>();
        private Map<Character, Tuple2<Integer, Integer>> doors = new HashMap<>();
        private Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();
        private Tuple2<Integer, Integer> entrance;

        public Maze(String input) {
            String[] lines = input.split("\n");
            for (int y = 0; y < lines.length; y++) {
                char[] line = lines[y].toCharArray();
                for (int x = 0; x < line.length; x++) {
                    if (line[x] >= 'a' && line[x] <= 'z') {
                        keys.put(line[x], tuple(x, y));
                    }
                    if (line[x] == '@') {
                        entrance = tuple(x, y);
                    }
                    if (line[x] >= 'A' && line[x] <= 'Z') {
                        doors.put(line[x], tuple(x, y));
                    }
                    if (line[x] != '#') {
                        tiles.add(tuple(x, y));
                    }
                }
            }
        }

        public Map<Character, Tuple2<Integer, Integer>> keys() {
            return keys;
        }

        public Tuple2<Integer, Integer> entrance() {
            return entrance;
        }

        public Map<Character, Tuple2<Integer, Integer>> doors() {
            return doors;
        }

        public Set<Tuple2<Integer, Integer>> tiles() {
            return tiles;
        }
    }
}
