package it.matteopierro.oxygenSystem;

import it.matteopierro.robot.Direction;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jooq.lambda.tuple.Tuple.tuple;

public class Node {

    private final static Map<Direction, Direction> OPPOSITE_DIRECTION = Map.of(
            Direction.NORTH, Direction.SOUTH,
            Direction.SOUTH, Direction.NORTH,
            Direction.EAST, Direction.WEST,
            Direction.WEST, Direction.EAST
    );

    private Node parent;
    private Direction cameFrom;
    private Set<Direction> visitedDirection = new HashSet<>();
    private Tuple2<Integer, Integer> position;

    public Node() {
        position = tuple(0, 0);
    }

    public Node(Node parent, Direction cameFrom) {
        this.parent = parent;
        this.cameFrom = cameFrom;
        this.position = cameFrom.move(parent.position);
        this.visitedDirection.add(OPPOSITE_DIRECTION.get(cameFrom));
    }

    public Direction direction() {
        return cameFrom;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(parent, node.parent) &&
                Objects.equals(visitedDirection, node.visitedDirection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, visitedDirection);
    }

    @Override
    public String toString() {
        return "Node{" +
                "parent=" + parent +
                ", visitedDirection=" + visitedDirection +
                '}';
    }

    public Node parent() {
        return parent;
    }

    public List<Node> children() {
        return Stream.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
                .filter(d -> !visitedDirection.contains(d))
                .map(d -> new Node(this, d))
                .collect(Collectors.toList());
    }

    public Tuple2<Integer, Integer> position() {
        return this.position;
    }
}
