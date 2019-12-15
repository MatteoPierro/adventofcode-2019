package it.matteopierro;

import it.matteopierro.computer.ComputerListener;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertThat(droid.onReadRequested()).isEqualTo(Droid.NORTH);
        droid.onStoreRequested(Droid.WALL);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(0, 0));
    }

    @Test
    void tryToMoveEastWhenNorthHasWall() {
        assertThat(droid.onReadRequested()).isEqualTo(Droid.NORTH);
        droid.onStoreRequested(Droid.WALL);
        assertThat(droid.onReadRequested()).isEqualTo(Droid.EAST);
    }

    @Test
    void moveEastWhenThereAreNoWall() {
        assertThat(droid.onReadRequested()).isEqualTo(Droid.NORTH);
        droid.onStoreRequested(Droid.WALL);
        assertThat(droid.onReadRequested()).isEqualTo(Droid.EAST);
        droid.onStoreRequested(Droid.SUCCESS);
        assertThat((Iterable<?>) droid.currentPosition()).isEqualTo(tuple(1, 0));
    }

    private class Droid extends ComputerListener {

        private static final String WALL = "0";
        private static final String SUCCESS = "1";

        private static final String NORTH = "1";
        private static final String EAST = "4";
        private Tuple2<Integer, Integer> currentPosition = tuple(0, 0);
        private String currentDirection = NORTH;

        @Override
        public String onReadRequested() {
            return currentDirection;
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            if (WALL.equals(result)) {
                currentDirection = EAST;
            }

            if (SUCCESS.equals(result)) {
                currentPosition = tuple(1, 0);
            }
        }

        public Tuple2<Integer, Integer> currentPosition() {
            return currentPosition;
        }
    }
}
