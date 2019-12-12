package it.matteopierro;

import org.jooq.lambda.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
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

        assertThat(steps(moonsPositions, 10)).containsExactly(
                new Moon(tuple(2, 1, -3), tuple(-3, -2, 1)),
                new Moon(tuple(1, -8, 0), tuple(-1, 1, 3)),
                new Moon(tuple(3, -6, 1), tuple(3, 2, -3)),
                new Moon(tuple(2, 0, 4), tuple(1, -1, -1))
        );

        assertThat(totalEnergy(steps(moonsPositions, 10))).isEqualTo(179);
    }

    private int totalEnergy(List<Moon> moonsPositions) {
        return moonsPositions.stream()
                .mapToInt(Moon::energy)
                .sum();
    }

    @Test
    void secondExample() {
        List<Moon> moonsPositions = List.of(
                new Moon(tuple(-8, -10, 0)),
                new Moon(tuple(5, 5, 10)),
                new Moon(tuple(2, -7, 3)),
                new Moon(tuple(9, -8, -3))
        );

        assertThat(steps(moonsPositions, 100)).containsExactly(
                new Moon(tuple(8, -12, -9), tuple(-7, 3, 0)),
                new Moon(tuple(13, 16, -3), tuple(3, -11, -5)),
                new Moon(tuple(-29, -11, -1), tuple(-3, 7, 4)),
                new Moon(tuple(16, -13, 23), tuple(7, 1, 1))
        );

        assertThat(totalEnergy(steps(moonsPositions, 100))).isEqualTo(1940);
    }

    @Test
    void firstPuzzle() {
        List<Moon> moonsPositions = List.of(
                new Moon(tuple(14, 9, 14)),
                new Moon(tuple(9, 11, 6)),
                new Moon(tuple(-6, 14, -4)),
                new Moon(tuple(4, -4, -3))
        );

        assertThat(totalEnergy(steps(moonsPositions, 1000))).isEqualTo(9999);
    }

    private List<Moon> steps(List<Moon> moonsPositions, int numberOfSteps) {
        List<Moon> result = moonsPositions;

        for (int step = 0; step < numberOfSteps; step++) {
            result = step(result);
        }

        return result;
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

        public int energy() {
            return (abs(position.v1) + abs(position.v2) + abs(position.v3)) * (abs(velocity.v1) + abs(velocity.v2) + abs(velocity.v3));
        }
    }
}
