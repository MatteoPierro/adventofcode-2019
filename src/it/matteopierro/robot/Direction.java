package it.matteopierro.robot;

import org.jooq.lambda.tuple.Tuple2;

import java.util.Map;

import static org.jooq.lambda.tuple.Tuple.tuple;

public enum Direction {
    NORTH(0, 1), EAST(1, 0), WEST(-1, 0), SOUTH(0, -1);

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

    private static final Map<Direction, Direction> OPPOSITE = Map.of(
            NORTH, SOUTH,
            WEST, EAST,
            SOUTH, NORTH,
            EAST, WEST
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

    public Direction opposite() {
        return OPPOSITE.get(this);
    }

    public Tuple2<Integer, Integer> move(Tuple2<Integer, Integer> position) {
        return tuple(position.v1 + step.v1, position.v2 + step.v2);
    }

    public static Direction fromSegment(Tuple2<Integer, Integer> start, Tuple2<Integer, Integer> end) {
        long dx = end.v1 - start.v1;
        long dy = end.v2 - start.v2;
        if (dx == 0 && dy > 0) return NORTH;
        if (dx == 0 && dy < 0) return SOUTH;
        if (dx > 0 && dy == 0) return EAST;
        if (dx < 0 && dy == 0) return WEST;
        throw new IllegalArgumentException("Line segment is not vertical or horizontal");
    }
}
