package it.matteopierro;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class NBodyProblemTest {

    @Test
    void updateAllMoonsPositionsAccordingToGravity() {
        List<Moon> moonsPositions = List.of(
                new Moon(tuple(-1, 0, 2)),
                new Moon(tuple(2, -10, -7)),
                new Moon(tuple(4, -8, 8)),
                new Moon(tuple(3, 5, -1))
        );
        List<Moon> newMoonsState = step(moonsPositions);
        assertThat(newMoonsState).containsExactly(
                new Moon(tuple(2, -1, 1), tuple(3, -1, -1)),
                new Moon(tuple(3, -7, -4), tuple(1, 3, 3)),
                new Moon(tuple(1, -7, 5), tuple(-3, 1, -3)),
                new Moon(tuple(2, 2, 0), tuple(-1, -3, 1))
        );
    }

    private List<Moon> step(List<Moon> moons) {
        return moons.stream()
                .map(moon -> positionForGravity(moon, moons))
                .collect(Collectors.toList());
    }

    private Moon positionForGravity(Moon moon, List<Moon> moons) {
        int dX = 0;
        int dY = 0;
        int dZ = 0;

        for (Moon anotherMoon : moons) {
            Tuple3<Integer, Integer, Integer> distance = anotherMoon.distanceTo(moon);
            dX += distance.v1;
            dY += distance.v2;
            dZ += distance.v3;
        }

        return moon.move(tuple(dX, dY, dZ));
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

        public Moon(Tuple3<Integer, Integer, Integer> position, Tuple3<Integer, Integer, Integer> velocity) {
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

        public Moon move(Tuple3<Integer, Integer, Integer> gravityContribution) {
            Tuple3<Integer, Integer, Integer> newPosition = tuple(
                    position.v1 + gravityContribution.v1 + velocity.v1,
                    position.v2 + gravityContribution.v2 + velocity.v2,
                    position.v3 + gravityContribution.v3 + velocity.v3
            );

            Tuple3<Integer, Integer, Integer> newVelocity = tuple(
                    gravityContribution.v1 + velocity.v1,
                    gravityContribution.v2 + velocity.v2,
                    gravityContribution.v3 + velocity.v3
            );

            return new Moon(newPosition, newVelocity);
        }

        public Moon clone() {
            return new Moon(position, velocity);
        }

        public Tuple3<Integer, Integer, Integer> distanceTo(Moon moon) {
            return tuple(
                    delta(position.v1, moon.position.v1),
                    delta(position.v2, moon.position.v2),
                    delta(position.v3, moon.position.v3)
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

        @Override
        public String toString() {
            return "position " + position.toString() + " velocity " + velocity.toString();
        }
    }
}
