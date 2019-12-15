package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import it.matteopierro.robot.Direction;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.matteopierro.SpacePoliceTest.Robot.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class SpacePoliceTest {

    public static final String RANDOM_RESULT = "RANDOM RESULT";
    private Robot robot;

    @BeforeEach
    void setUp() {
        robot = new Robot();
    }

    @Test
    void theFirstTileIsAlwaysBlack() {
        assertThat(robot.onReadRequested()).isEqualTo(BLACK_COLOR);
    }

    @Test
    void robotWaitsAlwaysForTwoInput() {
        step(WHITE_COLOR, TURN_LEFT);

        assertThat(robot.colorAt(tuple(0, 0))).isEqualTo(WHITE_COLOR);
        assertThat(singletonList(robot.currentPosition())).containsExactly(tuple(-1, 0));
    }

    @Test
    void threeConsecutiveStoresAreNotAllowed() {
        assertThatThrownBy(() -> {
            robot.onStoreRequested(WHITE_COLOR);
            robot.onStoreRequested(TURN_LEFT);
            robot.onStoreRequested(RANDOM_RESULT);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("consecutive reads");
    }

    @Test
    void readBeforeTwoStoresIsNotAllowed() {
        assertThatThrownBy(() -> {
            robot.onReadRequested();
            robot.onStoreRequested(WHITE_COLOR);
            robot.onReadRequested();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("read before two stores");
    }

    @Test
    void returnBlackTheFirstTimeVisitNewTile() {
        step(WHITE_COLOR, TURN_LEFT);
        robot.onReadRequested();

        assertThat(robot.colorAt(tuple(-1, 0))).isEqualTo(BLACK_COLOR);
    }

    @Test
    void paintTheSecondTile() {
        step(WHITE_COLOR, TURN_LEFT);
        step(WHITE_COLOR, TURN_LEFT);

        assertThat(robot.colorAt(tuple(-1, 0))).isEqualTo(WHITE_COLOR);
    }

    @Test
    void completePath() {
        step(WHITE_COLOR, TURN_LEFT); //1 0
        step(BLACK_COLOR, TURN_LEFT); //0 0
        step(WHITE_COLOR, TURN_LEFT); //1 0
        step(WHITE_COLOR, TURN_LEFT); //1 0
        step(BLACK_COLOR, TURN_RIGHT);//0 1
        step(WHITE_COLOR, TURN_LEFT); //1 0
        step(WHITE_COLOR, TURN_LEFT); //1 0

        assertThat(robot.tiles).hasSize(6);
        assertThat(robot.tiles.keySet()).containsExactlyInAnyOrder(
                tuple(0, 0),
                tuple(-1, 0),
                tuple(-1, -1),
                tuple(0, -1),
                tuple(1, 0),
                tuple(1, 1)
        );
    }

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day11"));
        new Computer().execute(program, robot);

        assertThat(robot.tiles).hasSize(2883);
    }

    @Test
    void secondPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day11"));
        robot.startingColor = WHITE_COLOR;
        new Computer().execute(program, robot);

        assertThat(robot.tiles).hasSize(249);

        int minX = robot.tiles.keySet()
                .stream()
                .min(Comparator.comparing(t -> t.v1))
                .map(t -> t.v1)
                .orElseThrow();

        int maxX = robot.tiles.keySet()
                .stream()
                .max(Comparator.comparing(t -> t.v1))
                .map(t -> t.v1)
                .orElseThrow();

        int minY = robot.tiles.keySet()
                .stream()
                .min(Comparator.comparing(t -> t.v2))
                .map(t -> t.v2)
                .orElseThrow();

        int maxY = robot.tiles.keySet()
                .stream()
                .max(Comparator.comparing(t -> t.v2))
                .map(t -> t.v2)
                .orElseThrow();


        int gridX = maxX - minX;
        int gridY = maxY - minY;

        assertThat(gridX).isEqualTo(42);
        assertThat(gridY).isEqualTo(5);
        List<Tuple2<Integer, Integer>> centeredFilteredTiles = robot.tiles.keySet()
                .stream()
                .filter(t -> robot.tiles.get(t).equals(WHITE_COLOR))
                .map(t -> tuple(t.v1 - minX, t.v2 - minY))
                .collect(Collectors.toList());

        assertThat(centeredFilteredTiles).hasSize(91);
    }

    private void step(String color, String direction) {
        robot.onReadRequested();
        robot.onStoreRequested(color);
        robot.onStoreRequested(direction);
    }


    public static class Robot extends ComputerListener {
        public static final String BLACK_COLOR = "0";
        public static final String WHITE_COLOR = "1";
        public static final String TURN_LEFT = "0";
        public static final String TURN_RIGHT = "1";

        private final Map<Tuple2<Integer, Integer>, String> tiles = new HashMap<>();
        private String startingColor;
        private Tuple2<Integer, Integer> currentTile = tuple(0, 0);
        private int storeInstructions = 2;
        private Direction currentDirection = Direction.NORTH;

        Robot() {
            this(BLACK_COLOR);
        }

        public Robot(String color) {
            this.startingColor = color;
        }

        @Override
        public String onReadRequested() {
            if (storeInstructions != 2) {
                throw new RuntimeException(storeInstructions + " read before two stores");
            }
            storeInstructions = 0;
            return colorAt(currentTile);
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);

            if (storeInstructions == 0) {
                tiles.put(currentTile, result);
                storeInstructions++;
            } else if (storeInstructions == 1) {
                currentDirection = TURN_LEFT.equals(result) ? currentDirection.left() : currentDirection.right();
                currentTile = currentDirection.move(currentTile);
                storeInstructions++;
            } else {
                throw new RuntimeException(storeInstructions + " consecutive reads");
            }
        }

        public String colorAt(Tuple2<Integer, Integer> tuple) {
            if (startingColor != null) {
                String color = startingColor;
                startingColor = null;
                return color;
            }
            return tiles.getOrDefault(tuple, BLACK_COLOR);
        }

        public Tuple2<Integer, Integer> currentPosition() {
            return currentTile;
        }

        public Map<Tuple2<Integer, Integer>, String> tiles() {
            return tiles;
        }
    }

}
