package it.matteopierro;

import it.matteopierro.computer.ComputerListener;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static it.matteopierro.SpacePoliceTest.Robot.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jooq.lambda.tuple.Tuple.tuple;

class SpacePoliceTest {

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
        robot.onReadRequested();
        robot.onStoreRequested(WHITE_COLOR);
        robot.onStoreRequested(TURN_LEFT);

        assertThat(robot.tileAt(tuple(0,0))).isEqualTo(WHITE_COLOR);
        assertThat(singletonList(robot.currentPosition())).containsExactly(tuple(-1, 0));
    }

    @Test
    void threeConsecutiveStoresAreNotAllowed() {
        assertThatThrownBy( () -> {
            robot.onStoreRequested(WHITE_COLOR);
            robot.onStoreRequested(TURN_LEFT);
            robot.onStoreRequested(RANDOM_RESULT);
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("consecutive reads");
    }

    @Test
    void readBeforeTwoStoresIsNotAllowed() {
        assertThatThrownBy( () -> {
            robot.onReadRequested();
            robot.onStoreRequested(WHITE_COLOR);
            robot.onReadRequested();
        }).isInstanceOf(RuntimeException.class).hasMessageContaining("read before two stores");
    }

    @Test
    void returnBlackTheFirstTimeVisitNewTile() {
        robot.onReadRequested();
        robot.onStoreRequested(WHITE_COLOR);
        robot.onStoreRequested(TURN_LEFT);
        robot.onReadRequested();

        assertThat(robot.tileAt(tuple(-1, 0))).isEqualTo(BLACK_COLOR);
    }

    @Test
    void paintTheSecondTile() {
        robot.onReadRequested();
        robot.onStoreRequested(WHITE_COLOR);
        robot.onStoreRequested(TURN_LEFT);
        robot.onReadRequested();
        robot.onStoreRequested(WHITE_COLOR);
        robot.onStoreRequested(TURN_LEFT);

        assertThat(robot.tileAt(tuple(-1, 0))).isEqualTo(WHITE_COLOR);
    }

    static class Robot extends ComputerListener {
        public static final String BLACK_COLOR = "0";
        public static final String WHITE_COLOR = "1";
        public static final String TURN_LEFT = "0";
        public static final String TURN_RIGHT = "1";

        private final Map<Tuple2<Integer, Integer>, String> tiles = new HashMap<>();
        private Tuple2<Integer, Integer> currentTile = tuple(0,0);
        private int storeInstructions = 2;

        @Override
        public String onReadRequested() {
            if (storeInstructions != 2) {
                throw new RuntimeException(storeInstructions + " read before two stores");
            }
            storeInstructions = 0;
            return tileAt(currentTile);
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);

            if (storeInstructions == 0) {
                tiles.put(currentTile, result);
                storeInstructions++;
            } else if (storeInstructions == 1) {
                currentTile = tuple(-1, 0);
                storeInstructions++;
            } else {
                throw new RuntimeException(storeInstructions + " consecutive reads");
            }
        }

        public String tileAt(Tuple2<Integer, Integer> tuple) {
            return tiles.getOrDefault(tuple, BLACK_COLOR);
        }

        public Tuple2<Integer, Integer> currentPosition() {
            return currentTile;
        }
    }
}
