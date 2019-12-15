package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import it.matteopierro.oxygenSystem.Node;
import it.matteopierro.robot.Direction;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

class OxygenSystemTest {

    private Droid droid;

    @BeforeEach
    void setUp() {
        droid = new Droid();
    }

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day15"));

        new Computer().execute(program, droid);
    }

    private static class Droid extends ComputerListener {

        private static final String NORTH = "1";
        private static final String SOUTH = "2";
        private static final String WEST = "3";
        private static final String EAST = "4";
        private static final Map<Direction, String> DIRECTION_TO_COMMAND = Map.of(
                Direction.NORTH, NORTH,
                Direction.SOUTH, SOUTH,
                Direction.WEST, WEST,
                Direction.EAST, EAST
        );

        private static final String WALL = "0";
        private static final String SUCCESS = "1";
        public static final String OXYGEN_SYSTEM = "2";

        private Node currentNode;
        private Queue<Node> nodeQueue = new LinkedList<>();
        public Set<Tuple2<Integer, Integer>> alreadyExploredPosition = new HashSet<>();

        public Droid() {
            Node root = new Node();
            List<Node> children = root.children();
            children.forEach(c -> alreadyExploredPosition.add(c.position()));
            nodeQueue.addAll(children);
        }

        @Override
        public String onReadRequested() {
            currentNode = nodeQueue.remove();
            return DIRECTION_TO_COMMAND.get(currentNode.direction());
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            if (SUCCESS.equals(result)) {
                List<Node> children = currentNode.children()
                                      .stream()
                                      .filter(c -> !alreadyExploredPosition.contains(c.position()))
                                      .collect(Collectors.toList());
                children.forEach(c-> alreadyExploredPosition.add(c.position()));
                nodeQueue.addAll(children);
            }

            if (OXYGEN_SYSTEM.equals(result)) {
                System.out.println("oxygen system");
                System.out.println(currentNode.distanceToRoot());
                System.exit(0);
            }
        }
    }
}
