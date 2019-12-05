package it.matteopierro;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

public class ChanceOfAsteroidsTest {
    private static final String SUM_OPERATION = "1";
    private static final String MULTIPLY_OPERATION = "2";
    private static final String SAVE_OPERATION = "3";
    private static final String STOP_OPERATION = "99";
    private static final String OUTPUT_OPERATION = "4";
    private static final String INPUT = "1";

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

    @Test
    void outputItsInput() {
        List<String> output = execute("3,0,4,0,99".split(","));
        assertThat(output).containsExactly(INPUT);
    }

    @Test
    void parameterModes() {
        assertThat(execute("1002,4,3,4,33")).isEqualTo("1002,4,3,4,99");
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

    private List<String> execute(String[] memory) {
        List<String> outputs = new ArrayList<>();
        for (int memoryIndex = 0; memoryIndex < memory.length;) {
            String operationCode = memory[memoryIndex];
            if (STOP_OPERATION.equals(operationCode)) break;
            if (SUM_OPERATION.equals(operationCode)) {
                sum(memory, memoryIndex);
                memoryIndex += 4;
            } else if (operationCode.endsWith(MULTIPLY_OPERATION)) {
                Multiply operation = new Multiply(operationCode);
                operation.execute(memory, memoryIndex);
                memoryIndex += operation.size();
            } else if (SAVE_OPERATION.equals(operationCode)) {
                save(memory, memoryIndex);
                memoryIndex += 2;
            } else if (OUTPUT_OPERATION.equals(operationCode)) {
                output(outputs, memory, memoryIndex);
                memoryIndex += 2;
            }
        }
        return outputs;
    }

    @Test
    void testExtractOperation() {
        assertThat(extractOperation("1002")).isEqualTo("2");
    }

    private void computeParameterMode(String[] instructions, int instructionIndex) {
        String instruction = instructions[instructionIndex];
        String operation = extractOperation(instruction);
        Mode firstOperandMode = extractFirsOperandMode(instruction);
        Mode secondOperandMode = extractSecondOperandMode(instruction);
        Mode resultMode = extractResultOperandMode(instruction);
        Operation multiply = new Multiply(firstOperandMode, secondOperandMode, resultMode);
        multiply.execute(instructions, instructionIndex);
    }

    private Mode extractFirsOperandMode(String instruction) {
        return Mode.modeFor(instruction.split("")[instruction.length() - 3]);
    }

    private Mode extractSecondOperandMode(String instruction) {
        return Mode.modeFor(instruction.split("")[instruction.length() - 4]);
    }

    private Mode extractResultOperandMode(String instruction) {
        if (instruction.length() == 4) return new Position();

        return Mode.modeFor(instruction.split("")[instruction.length() - 5]);
    }

    private String extractOperation(String instruction) {
        return instruction.substring(instruction.length() - 1);
    }

    private void output(List<String> outputs, String[] instructions, int instructionIndex) {
        int resultPosition = Integer.parseInt(instructions[instructionIndex + 1]);
        outputs.add(instructions[resultPosition]);
    }

    private void save(String[] instructions, int instructionIndex) {
        int savePosition = Integer.parseInt(instructions[instructionIndex + 1]);
        instructions[savePosition] = INPUT;
    }

    void sum(String[] instructions, int instructionIndex) {
        execute(instructions, instructionIndex, Integer::sum);
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

    private interface Operation {
        void execute(String[] memory, int memoryIndex);
        int size();
    }

    private interface Mode {
        static Mode modeFor(String mode) {
            return mode.equals("0")
                    ? new Position()
                    : new Immediate();
        }

        Integer read(String[] memory, Integer address);
        void write(String[] memory, Integer address, String value);
    }

    private static class Position implements Mode {
        @Override
        public Integer read(String[] memory, Integer address) {
            int position = Integer.parseInt(memory[address]);
            return Integer.parseInt(memory[position]);
        }

        @Override
        public void write(String[] memory, Integer address, String value) {
            int position = Integer.parseInt(memory[address]);
            memory[position] = String.valueOf(value);
        }
    }

    private static class Immediate implements Mode {
        @Override
        public Integer read(String[] memory, Integer address) {
            return Integer.parseInt(memory[address]);
        }

        @Override
        public void write(String[] memory, Integer address, String value) {
            memory[address] = String.valueOf(value);
        }
    }

    private static class Multiply implements Operation {
        private final Mode firstOperandMode;
        private final Mode secondOperandMode;
        private final Mode resultMode;

        Multiply(Mode firstOperandMode, Mode secondOperandMode, Mode resultMode) {
            this.firstOperandMode = firstOperandMode;
            this.secondOperandMode = secondOperandMode;
            this.resultMode = resultMode;
        }

        public Multiply(String operationCode) {
            if (operationCode.length() == 1) {
                this.firstOperandMode = new Position();
                this.secondOperandMode = new Position();
                this.resultMode = new Position();
            } else {
                this.firstOperandMode = extractFirstOperandMode(operationCode);
                this.secondOperandMode = extractSecondOperandMode(operationCode);
                this.resultMode = extractResultOperandMode(operationCode);
            }
        }

        private Mode extractFirstOperandMode(String instruction) {
            return Mode.modeFor(instruction.split("")[instruction.length() - 3]);
        }

        private Mode extractSecondOperandMode(String instruction) {
            return Mode.modeFor(instruction.split("")[instruction.length() - 4]);
        }

        private Mode extractResultOperandMode(String instruction) {
            if (instruction.length() == 4) return new Position();

            return Mode.modeFor(instruction.split("")[instruction.length() - 5]);
        }

        @Override
        public void execute(String[] memory, int memoryIndex) {
            Integer firstOperand = firstOperandMode.read(memory, memoryIndex + 1);
            Integer secondOperand = secondOperandMode.read(memory, memoryIndex + 2);
            String value = String.valueOf(firstOperand * secondOperand);
            resultMode.write(memory, memoryIndex + 3, value);
        }

        @Override
        public int size() {
            return 4;
        }
    }
}
