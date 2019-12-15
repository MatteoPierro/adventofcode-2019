package it.matteopierro;

import it.matteopierro.computer.ComputerListener;
import it.matteopierro.robot.Direction;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class OxygenSystemTest {

    private Droid droid;

    @BeforeEach
    void setUp() {
        droid = new Droid();
    }

    @Test
    void sendMovementCommand() {
        assertThat(droid.onReadRequested()).isEqualTo(Droid.NORTH);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(0, 0));
    }

    @Test
    void doNotChangePositionWhenHitsWalls() {
        move(Droid.NORTH, Droid.WALL);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(0, 0));
    }

    @Test
    void tryToMoveEastWhenNorthHasWall() {
        move(Droid.NORTH, Droid.WALL);
        assertThat(droid.onReadRequested()).isEqualTo(Droid.EAST);
    }

    @Test
    void moveEastWhenThereAreNoWall() {
        move(Droid.NORTH, Droid.WALL);
        move(Droid.EAST, Droid.SUCCESS);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(-1, 0));
    }

    @Test
    void findAnotherWallInNorth() {
        move(Droid.NORTH, Droid.WALL);
        move(Droid.EAST, Droid.SUCCESS);
        move(Droid.NORTH, Droid.WALL);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(-1, 0));
    }

    @Test
    void findAnotherWallInSouth() {
        move(Droid.NORTH, Droid.WALL);
        move(Droid.EAST, Droid.SUCCESS);
        move(Droid.NORTH, Droid.WALL);
        move(Droid.EAST, Droid.WALL);
        move(Droid.SOUTH, Droid.WALL);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(-1, 0));
    }

    private void move(String direction, String found) {
        assertThat(droid.onReadRequested()).isEqualTo(direction);
        droid.onStoreRequested(found);
    }

    private static class Droid extends ComputerListener {

        private static final String NORTH = "1";
        private static final String SOUTH = "2";
        private static final String WEST = "3";
        private static final String EAST = "4";
        private static final Map<Direction, String> DIRECTION_TO_COMMAND = Map.of(
                Direction.NORTH, NORTH,
                Direction.SOUTH, SOUTH,
                Direction.WEST, WEST,
                Direction.EAST, EAST
        );

        private static final String WALL = "0";
        private static final String SUCCESS = "1";

        private Tuple2<Integer, Integer> currentPosition = tuple(0, 0);
        private Direction currentDirection = Direction.NORTH;
        private List<Direction> alreadyTriedDirections = new ArrayList<>();

        @Override
        public String onReadRequested() {
            return DIRECTION_TO_COMMAND.get(currentDirection);
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            if (WALL.equals(result)) {
                alreadyTriedDirections.add(currentDirection);
                currentDirection = Stream.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
                .filter(d -> !alreadyTriedDirections.contains(d))
                .findFirst()
                .get();
            }

            if (SUCCESS.equals(result)) {
                alreadyTriedDirections = new ArrayList<>();
                currentPosition = currentDirection.move(currentPosition);
                currentDirection = Direction.NORTH;
            }
        }

        public Tuple2<Integer, Integer> currentPosition() {
            return currentPosition;
        }
    }
}
