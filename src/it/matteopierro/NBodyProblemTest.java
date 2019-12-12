package it.matteopierro;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Test
    void updateAllMoonsPositionsAccordingToGravity() {
        List<Tuple3<Integer, Integer, Integer>> moonsPositions = List.of(
                tuple(-1, 0, 2),
                tuple(2, -10, -7),
                tuple(4, -8, 8),
                tuple(3, 5, -1)
        );
        List<Tuple3<Integer, Integer, Integer>> newPositions = positionsForGravity(moonsPositions);
        assertThat(newPositions).containsExactly(
                tuple(2, -1, 1),
                tuple(3, -7, -4),
                tuple(1, -7, 5),
                tuple(2, 2, 0)
        );
    }

    private List<Tuple3<Integer, Integer, Integer>> positionsForGravity(List<Tuple3<Integer, Integer, Integer>> moonsPositions) {
        return moonsPositions.stream()
                .map(Tuple3::clone)
                .map(moon -> positionForGravity(moon, moonsPositions))
                .collect(Collectors.toList());
    }

    private Tuple3<Integer, Integer, Integer> positionForGravity(Tuple3<Integer, Integer, Integer> moonPosition, List<Tuple3<Integer, Integer, Integer>> moonsPositions) {
        Tuple3<Integer, Integer, Integer> oldMoonPosition = moonPosition.clone();
        int dX = 0;
        int dY = 0;
        int dZ = 0;
        for (Tuple3<Integer, Integer, Integer> anotherMoonsPosition : moonsPositions) {
            dX += delta(anotherMoonsPosition.v1, moonPosition.v1);
            dY += delta(anotherMoonsPosition.v2, moonPosition.v2);
            dZ += delta(anotherMoonsPosition.v3, moonPosition.v3);
        }

        return tuple(
                oldMoonPosition.v1 + dX,
                oldMoonPosition.v2 + dY,
                oldMoonPosition.v3 + dZ
        );
    }

    private int delta(Integer p1, Integer p2) {
        int delta = 0;
        if (p1 > p2) {
            delta = 1;
        }
        if (p1 < p2) {
            delta = -1;
        }
        return delta;
    }

    @Test
    void moonIsEqualsWhenHasSamePositionAndVelocity() {
        assertThat(new Moon(tuple(1, 0, 1), tuple(1, 5, 6))).isEqualTo(new Moon(tuple(1, 0, 1), tuple(1, 5, 6)));
        assertThat(new Moon(tuple(1, 0, 1), tuple(1, 5, 6))).isNotEqualTo(new Moon(tuple(1, 0, 1), tuple(7, 8, 9)));
    }

    private static class Moon {
        private final Tuple3<Integer, Integer, Integer> position;
        private final Tuple3<Integer, Integer, Integer> velocity;

        public Moon(Tuple3<Integer, Integer, Integer> position) {
            this(position, tuple(0, 0, 0));
        }

        private Moon(Tuple3<Integer, Integer, Integer> position, Tuple3<Integer, Integer, Integer> velocity) {
            this.position = position;
            this.velocity = velocity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Moon)) return false;
            Moon moon = (Moon) o;
            return Objects.equals(position, moon.position) &&
                    Objects.equals(velocity, moon.velocity);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, velocity);
        }
    }
}
