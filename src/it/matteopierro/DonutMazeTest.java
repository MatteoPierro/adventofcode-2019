package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

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
    }

    private class Maze {


        public final Set<Tuple2<Integer, Integer>> tiles = new HashSet<>();

        public Maze(String input) {
            String[] lines = input.split("\n");
            for (int y = 0; y < lines.length; y++) {
                char[] line = lines[y].toCharArray();
                for (int x = 0; x < line.length; x++) {
                    if (line[x] == '.') {
                        tiles.add(tuple(x, y));
                    }
                }
            }
        }
    }
}
