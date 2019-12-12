package it.matteopierro;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class NBodyProblemTest {

    @Test
    void updateMoonPositionAccordingToGravity() {
        Tuple3<Integer, Integer, Integer> moonPosition = tuple(-1, 0, 2);
        List<Tuple3<Integer, Integer, Integer>> moonsPositions = List.of(
                moonPosition.clone(),
                tuple(2, -10, -7),
                tuple(4, -8, 8),
                tuple(3, 5, -1)
        );
        Tuple3<Integer, Integer, Integer> newPosition = positionForGravity(moonPosition, moonsPositions);
        assertThat(asList(newPosition)).containsExactly(tuple(2, -1, 1));
    }

    private Tuple3<Integer, Integer, Integer> positionForGravity(Tuple3<Integer, Integer, Integer> moonPosition, List<Tuple3<Integer, Integer, Integer>> moonsPositions) {
        Tuple3<Integer, Integer, Integer> newMoonPosition = moonPosition.clone();
        int dX = 0;
        int dY = 0;
        int dZ = 0;
        for (Tuple3<Integer, Integer, Integer> anotherMoonsPosition : moonsPositions) {
            if (anotherMoonsPosition.v1 > moonPosition.v1) {
                dX++;
            }
            if (anotherMoonsPosition.v1 < moonPosition.v1) {
                dX--;
            }
            if (anotherMoonsPosition.v2 > moonPosition.v2) {
                dY++;
            }
            if (anotherMoonsPosition.v2 < moonPosition.v2) {
                dY--;
            }
            if (anotherMoonsPosition.v3 > moonPosition.v3) {
                dZ++;
            }
            if (anotherMoonsPosition.v3 < moonPosition.v3) {
                dZ--;
            }
        }

        newMoonPosition = tuple(
                newMoonPosition.v1 + dX,
                newMoonPosition.v2 + dY,
                newMoonPosition.v3 + dZ
        );
        return newMoonPosition;
    }
}
