package it.matteopierro;

import org.jooq.lambda.function.Function3;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class CrossWiresTest {

    @Test
    void parseInstructions() {
        List<Tuple2<String, Integer>> instructions = parseInstructions("R75,D30");
        assertThat(instructions).containsExactly(tuple("R", 75), tuple("D", 30));
    }

    @Test
    void executeInstructions() {
        List<Tuple2<String, Integer>> instructions = Arrays.asList(
                tuple("L", 1),
                tuple("U", 1),
                tuple("R", 2),
                tuple("D", 1)
        );

        assertThat(execute(instructions)).containsExactly(
                tuple(0, 0),
                tuple(-1, 0),
                tuple(-1, 1),
                tuple(0, 1),
                tuple(1, 1),
                tuple(1, 0)
        );
    }

    @Test
    void findWiresCrosses() {
        Set<Tuple2<Integer, Integer>> intersections = wireCrosses(
                "R75,D30,R83,U83,L12,D49,R71,U7,L72",
                "U62,R66,U55,R34,D71,R55,D58,R83"
        );

        assertThat(intersections).containsExactly(
                tuple(155, 11),
                tuple(158, -12),
                tuple(155, 4),
                tuple(146, 46)
        );
    }

    @Test
    void findMinimumWireCross() {
        assertThat(
                minimumWireCrossDistance(
                        "R75,D30,R83,U83,L12,D49,R71,U7,L72",
                        "U62,R66,U55,R34,D71,R55,D58,R83")
        ).isEqualTo(159);

        assertThat(
                minimumWireCrossDistance(
                        "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51",
                        "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")
        ).isEqualTo(135);
    }

    @Test
    void firstPuzzleSolution() throws IOException {
        List<String> instructions = Files.readAllLines(Paths.get("./input_day3"));
        String firstInstructions = instructions.get(0);
        String secondInstructions = instructions.get(1);

        assertThat(
                minimumWireCrossDistance(
                        firstInstructions,
                        secondInstructions)
        ).isEqualTo(232);
    }

    @Test
    void fewestSteps() {
        assertThat(
                fewestSteps(
                        "R75,D30,R83,U83,L12,D49,R71,U7,L72",
                        "U62,R66,U55,R34,D71,R55,D58,R83")
        ).isEqualTo(610);

        assertThat(
                fewestSteps(
                        "R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51",
                        "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")
        ).isEqualTo(410);
    }

    @Test
    void secondPuzzleSolution() throws IOException {
        List<String> instructions = Files.readAllLines(Paths.get("./input_day3"));
        String firstInstructions = instructions.get(0);
        String secondInstructions = instructions.get(1);

        assertThat(
                fewestSteps(
                        firstInstructions,
                        secondInstructions)
        ).isEqualTo(6084);
    }

    private Long fewestSteps(String firstInstructions, String secondInstructions) {
        List<Tuple2<Integer, Integer>> firstPath = execute(firstInstructions);
        List<Tuple2<Integer, Integer>> secondPath = execute(secondInstructions);
        Set<Tuple2<Integer, Integer>> wireCrosses = wireCrosses(firstPath, secondPath);
        return wireCrosses.stream().map(
                wireCross -> stepsUntilWireCross(firstPath, wireCross) + stepsUntilWireCross(secondPath, wireCross)
        ).min(Long::compareTo).orElseThrow();
    }

    private long stepsUntilWireCross(List<Tuple2<Integer, Integer>> firstPath, Tuple2<Integer, Integer> wireCross) {
        return firstPath.stream().takeWhile(step -> !step.equals(wireCross)).count();
    }

    private int minimumWireCrossDistance(String firstInstructions, String secondInstructions) {
        return wireCrosses(firstInstructions, secondInstructions)
                .stream()
                .min(Comparator.comparingInt(this::manhattanDistance))
                .map(this::manhattanDistance)
                .orElseThrow();
    }

    private int manhattanDistance(Tuple2<Integer, Integer> wireCross) {
        return Math.abs(wireCross.v1) + Math.abs(wireCross.v2);
    }

    private Set<Tuple2<Integer, Integer>> wireCrosses(String firstInstructions, String secondInstructions) {
        return wireCrosses(
                execute(firstInstructions),
                execute(secondInstructions));
    }

    private Set<Tuple2<Integer, Integer>> wireCrosses(List<Tuple2<Integer, Integer>> firstInstructions, List<Tuple2<Integer, Integer>> secondInstructions) {
        HashSet<Tuple2<Integer, Integer>> two = new HashSet<>(secondInstructions);
        return firstInstructions.stream()
                .distinct()
                .filter(two::contains)
                .filter(t -> !tuple(0, 0).equals(t))
                .collect(toSet());
    }

    private List<Tuple2<Integer, Integer>> execute(String instructions) {
        return execute(
                parseInstructions(instructions)
        );
    }

    private List<Tuple2<Integer, Integer>> execute(List<Tuple2<String, Integer>> instructions) {
        LinkedList<Tuple2<Integer, Integer>> path = new LinkedList<>();
        path.add(tuple(0, 0));
        for (Tuple2<String, Integer> instruction : instructions) {
            Tuple2<Integer, Integer> initialPosition = path.getLast();
            String direction = instruction.v1;
            Integer steps = instruction.v2;
            path.addAll(move(direction, steps, initialPosition));
        }
        return path;
    }

    private List<Tuple2<Integer, Integer>> move(String direction, Integer steps, Tuple2<Integer, Integer> initialPosition) {
        switch (direction) {
            case "L":
                return moveLeft(steps, initialPosition);
            case "U":
                return moveUp(steps, initialPosition);
            case "R":
                return moveRight(steps, initialPosition);
            case "D":
                return moveDown(steps, initialPosition);
            default:
                throw new RuntimeException();
        }
    }

    private List<Tuple2<Integer, Integer>> moveDown(Integer steps, Tuple2<Integer, Integer> initialPosition) {
        return move(steps, initialPosition, (step, x, y) -> tuple(x, y - step));
    }

    private List<Tuple2<Integer, Integer>> moveRight(Integer steps, Tuple2<Integer, Integer> initialPosition) {
        return move(steps, initialPosition, (step, x, y) -> tuple(x + step, y));
    }

    private List<Tuple2<Integer, Integer>> moveUp(Integer steps, Tuple2<Integer, Integer> initialPosition) {
        return move(steps, initialPosition, (step, x, y) -> tuple(x, y + step));
    }

    private List<Tuple2<Integer, Integer>> moveLeft(Integer steps, Tuple2<Integer, Integer> initialPosition) {
        return move(steps, initialPosition, (step, x, y) -> tuple(x - step, y));
    }

    private List<Tuple2<Integer, Integer>> move(Integer steps, Tuple2<Integer, Integer> initialPosition, Function3<Integer, Integer, Integer, Tuple2<Integer, Integer>> newPositionCalculator) {
        ArrayList<Tuple2<Integer, Integer>> newPositions = new ArrayList<>();
        Integer x = initialPosition.v1;
        Integer y = initialPosition.v2;
        for (int step = 1; step <= steps; step++) {
            newPositions.add(newPositionCalculator.apply(step, x, y));
        }
        return newPositions;
    }

    private List<Tuple2<String, Integer>> parseInstructions(String instructions) {
        return Stream.of(instructions.split(","))
                .map(this::toTuple)
                .collect(Collectors.toList());
    }

    private Tuple2<String, Integer> toTuple(String instruction) {
        String direction = instruction.substring(0, 1);
        int steps = Integer.parseInt(instruction.substring(1));
        return tuple(direction, steps);
    }
}
