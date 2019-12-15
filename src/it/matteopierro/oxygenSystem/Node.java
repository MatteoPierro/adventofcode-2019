package it.matteopierro.oxygenSystem;

import it.matteopierro.robot.Direction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Node {

    private final static Map<Direction, Direction> OPPOSITE_DIRECTION = Map.of(
            Direction.NORTH, Direction.SOUTH,
            Direction.SOUTH, Direction.NORTH,
            Direction.EAST, Direction.WEST,
            Direction.WEST, Direction.EAST
    );

    private Node parent;
    private Set<Direction> visitedDirection = new HashSet<>();

    public Node() {

    }

    public Node(Node parent, Direction cameFrom) {
        this.parent = parent;
        this.visitedDirection.add(OPPOSITE_DIRECTION.get(cameFrom));
    }

    public Node next() {
        if (visitedDirection.size() == 4) {
            return parent;
        }
        Direction nextDirection = Stream.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
                .filter(d -> !visitedDirection.contains(d))
                .findFirst()
                .get();
        visitedDirection.add(nextDirection);
        return new Node(this, nextDirection);
    }

    public int distanceToRoot() {
        Node currentParent = parent;
        int distance = 0;
        while (currentParent != null) {
            distance++;
            currentParent = currentParent.parent;
        }
        return distance;
    }
}
