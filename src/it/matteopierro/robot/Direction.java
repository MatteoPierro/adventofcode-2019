package it.matteopierro.robot;

import org.jooq.lambda.tuple.Tuple2;

import java.util.Map;

import static org.jooq.lambda.tuple.Tuple.tuple;

public enum Direction {
    NORTH(0, 1), EAST(-1, 0), WEST(1, 0), SOUTH(0, -1);

    private static final Map<Direction, Direction> LEFT_ROTATIONS = Map.of(
            NORTH, EAST,
            EAST, SOUTH,
            SOUTH, WEST,
            WEST, NORTH
    );
    private static final Map<Direction, Direction> RIGHT_ROTATIONS = Map.of(
            NORTH, WEST,
            WEST, SOUTH,
            SOUTH, EAST,
            EAST, NORTH
    );
    private final Tuple2<Integer, Integer> step;

    Direction(int x, int y) {
        this.step = tuple(x, y);
    }

    public Direction left() {
        return LEFT_ROTATIONS.get(this);
    }

    public Direction right() {
        return RIGHT_ROTATIONS.get(this);
    }

    public Tuple2<Integer, Integer> move(Tuple2<Integer, Integer> position) {
        return tuple(position.v1 + step.v1, position.v2 + step.v2);
    }
}
