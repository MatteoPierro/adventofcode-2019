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

import static org.assertj.core.api.Assertions.assertThat;

class ChanceOfAsteroidsTest {
    private static final String SUM_OPERATION = "1";
    private static final String MULTIPLY_OPERATION = "2";
    private static final String SAVE_OPERATION = "3";
    private static final String STOP_OPERATION = "99";
    private static final String READ_OPERATION = "4";
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
        for (int memoryIndex = 0; memoryIndex < memory.length; ) {
            String operationCode = memory[memoryIndex];
            Operation operation = operationFor(operationCode, outputs);
            operation.execute(memory, memoryIndex);
            memoryIndex += operation.size();
        }
        return outputs;
    }

    private Operation operationFor(String operationCode, List<String> outputs) {
        if (STOP_OPERATION.equals(operationCode)) {
            return new Stop();
        } else if (SUM_OPERATION.equals(operationCode)) {
            return new Sum(operationCode);
        } else if (operationCode.endsWith(MULTIPLY_OPERATION)) {
            return new Multiply(operationCode);
        } else if (SAVE_OPERATION.equals(operationCode)) {
            return new Save();
        } else if (READ_OPERATION.equals(operationCode)) {
            return new Read(outputs);
        }
        throw new RuntimeException("Not Supported Operation!");
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

    public static abstract class TwoOperandsOperation implements Operation {
        private final Mode firstOperandMode;
        private final Mode secondOperandMode;
        private final Mode resultMode;

        TwoOperandsOperation(String operationCode) {
            this.firstOperandMode = extractFirstOperandMode(operationCode);
            this.secondOperandMode = extractSecondOperandMode(operationCode);
            this.resultMode = extractResultOperandMode(operationCode);
        }

        private Mode extractFirstOperandMode(String operationCode) {
            if (operationCode.length() == 1) return new Position();

            return Mode.modeFor(operationCode.split("")[operationCode.length() - 3]);
        }

        private Mode extractSecondOperandMode(String operationCode) {
            if (operationCode.length() == 1) return new Position();

            return Mode.modeFor(operationCode.split("")[operationCode.length() - 4]);
        }

        private Mode extractResultOperandMode(String operationCode) {
            if (operationCode.length() == 1 || operationCode.length() == 4) return new Position();

            return Mode.modeFor(operationCode.split("")[operationCode.length() - 5]);
        }

        @Override
        public void execute(String[] memory, int memoryIndex) {
            Integer firstOperand = firstOperandMode.read(memory, memoryIndex + 1);
            Integer secondOperand = secondOperandMode.read(memory, memoryIndex + 2);
            String value = String.valueOf(execute(firstOperand, secondOperand));
            resultMode.write(memory, memoryIndex + 3, value);
        }

        @Override
        public int size() {
            return 4;
        }

        protected abstract int execute(int firstOperand, int secondOperand);
    }

    private static class Multiply extends TwoOperandsOperation {

        Multiply(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(int firstOperand, int secondOperand) {
            return firstOperand * secondOperand;
        }
    }

    private static class Sum extends TwoOperandsOperation {
        Sum(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(int firstOperand, int secondOperand) {
            return firstOperand + secondOperand;
        }
    }

    private static class Save implements Operation {

        @Override
        public void execute(String[] memory, int memoryIndex) {
            int savePosition = Integer.parseInt(memory[memoryIndex + 1]);
            memory[savePosition] = INPUT;
        }

        @Override
        public int size() {
            return 2;
        }
    }

    private static class Read implements Operation {

        private final List<String> outputs;

        Read(List<String> outputs) {
            this.outputs = outputs;
        }

        @Override
        public void execute(String[] memory, int memoryIndex) {
            int resultPosition = Integer.parseInt(memory[memoryIndex + 1]);
            outputs.add(memory[resultPosition]);
        }

        @Override
        public int size() {
            return 2;
        }
    }

    private static class Stop implements Operation {
        private int memorySize = Integer.MAX_VALUE;
        private int memoryIndex = 0;

        @Override
        public void execute(String[] memory, int memoryIndex) {
            this.memorySize = memory.length;
            this.memoryIndex = memoryIndex;
        }

        @Override
        public int size() {
            return memorySize - memoryIndex;
        }
    }
}
