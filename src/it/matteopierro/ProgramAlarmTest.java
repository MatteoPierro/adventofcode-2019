package it.matteopierro;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

class ProgramAlarmTest {

    private static final String SUM_OPERATION = "1";
    private static final String MULTIPLY_OPERATION = "2";
    private static final String STOP_OPERATION = "99";

    @ParameterizedTest
    @CsvSource(value = {
            "1,0,0,0,99:2,0,0,0,99",
            "2,3,0,3,99:2,3,0,6,99",
            "2,4,4,5,99,0:2,4,4,5,99,9801",
            "1,1,1,4,99,5,6,0,99:30,1,1,4,2,5,6,0,99",
            "1,9,10,3,2,3,11,0,99,30,40,50:3500,9,10,70,2,3,11,0,99,30,40,50",
    }, delimiter = ':')
    void executeSequence(String input, String expected) {
        assertThat(execute(input)).isEqualTo(expected);
    }

    @Test
    void firstPuzzle() {
        int noun = 12;
        int verb = 2;
        String result = compute(noun, verb);

        assertThat(result).isEqualTo("4945026");
    }

    @Test
    void secondPuzzle() {
        Seq<Integer> nouns = Seq.range(0, 100);
        Seq<Integer> verbs = Seq.range(0, 100);
        Tuple2<Integer, Integer> solution = nouns.crossJoin(verbs)
                .findFirst(t -> "19690720".equals(compute(t.v1, t.v2)))
                .orElseThrow();

        int noun = solution.v1;
        int verb = solution.v2;
        assertThat(noun).isEqualTo(52);
        assertThat(verb).isEqualTo(96);
        assertThat(100 * noun + verb).isEqualTo(5296);
    }

    private String compute(int noun, int verb) {
        try {
            String[] instructions = Files.readString(Paths.get("./input_day2")).split(",");
            instructions[1] = String.valueOf(noun);
            instructions[2] = String.valueOf(verb);
            return compute(instructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String compute(String[] instructions) {
        execute(instructions);
        return instructions[0];
    }

    String execute(String program) {
        String[] instructions = program.split(",");
        execute(instructions);
        return String.join(",", instructions);
    }

    private void execute(String[] instructions) {
        for (int instructionIndex = 0; instructionIndex < instructions.length; instructionIndex += 4) {
            String operation = instructions[instructionIndex];
            if (STOP_OPERATION.equals(operation)) break;
            if (SUM_OPERATION.equals(operation)) sum(instructions, instructionIndex);
            if (MULTIPLY_OPERATION.equals(operation)) multiply(instructions, instructionIndex);
        }
    }

    void sum(String[] instructions, int instructionIndex) {
        execute(instructions, instructionIndex, Integer::sum);
    }

    private void multiply(String[] instructions, int instructionIndex) {
        execute(instructions, instructionIndex, (a, b) -> a * b);
    }

    private void execute(String[] instructions, int instructionIndex, BiFunction<Integer, Integer, Integer> operation) {
        int first = operandForOperandPosition(instructions, instructionIndex + 1);
        int second = operandForOperandPosition(instructions, instructionIndex + 2);
        int sum = operation.apply(first, second);
        store(instructions, instructionIndex, sum);
    }

    private void store(String[] instructions, int instructionIndex, int value) {
        int storePosition = Integer.parseInt(instructions[instructionIndex + 3]);
        instructions[storePosition] = String.valueOf(value);
    }

    private int operandForOperandPosition(String[] instructions, int position) {
        int operandPosition = Integer.parseInt(instructions[position]);
        return Integer.parseInt(instructions[operandPosition]);
    }
}
