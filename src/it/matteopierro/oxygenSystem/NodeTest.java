package it.matteopierro.oxygenSystem;

import org.junit.jupiter.api.Test;

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
}
