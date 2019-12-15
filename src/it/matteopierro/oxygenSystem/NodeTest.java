package it.matteopierro.oxygenSystem;

import it.matteopierro.robot.Direction;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NodeTest {

    @Test
    void rootHasDistanceOne() {
        Node root = new Node();

        assertThat(root.distanceToRoot()).isEqualTo(0);
    }

    @Test
    void firstLayerHasDistanceOne() {
        Node firstLayer = new Node().next();

        assertThat(firstLayer.distanceToRoot()).isEqualTo(1);
    }

    @Test
    void secondLayerHasDistanceTwo() {
        Node secondLayer = new Node().next().next();

        assertThat(secondLayer.distanceToRoot()).isEqualTo(2);
    }

    @Test
    void returnToRootAfterExploringAllTheDirections() {
        Node root = new Node();
        Node firstLayer = root.next();

        firstLayer.next();
        firstLayer.next();
        firstLayer.next();

        assertThat(firstLayer.next()).isEqualTo(root);
    }

    @Test
    void childrenOfRoot() {
        Node root = new Node();

        assertThat(toDirection(root.children())).containsExactlyInAnyOrder(Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST);
    }

    @Test
    void childrenOfNode() {
        Node root = new Node();
        Node northNode = new Node(root, Direction.NORTH);

        assertThat(toDirection(northNode.children())).containsExactlyInAnyOrder(Direction.NORTH, Direction.WEST, Direction.EAST);
    }

    @Test
    void testPriorityQueue() {
        Node root = new Node();
        Queue<Node> nodeQueue = new LinkedList<>();
        nodeQueue.addAll(List.of(new Node(root, Direction.NORTH), new Node(root, Direction.EAST)));

        assertThat(nodeQueue.remove().direction()).isEqualTo(Direction.NORTH);
        nodeQueue.add(new Node(root, Direction.WEST));
        assertThat(nodeQueue.remove().direction()).isEqualTo(Direction.EAST);
        assertThat(nodeQueue.remove().direction()).isEqualTo(Direction.WEST);
    }

    private List<Direction> toDirection(List<Node> nodes) {
        return nodes.stream()
                .map(Node::direction)
                .collect(Collectors.toList());
    }
}
