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
    private static final String JUMP_IF_TRUE = "5";
    private static final String JUMP_IF_FALSE = "6";
    private static final String LESS_OPERATION = "7";
    private static final String EQUAL_OPERATION = "8";

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
    void thirdPuzzle() throws IOException {
        String[] program = Files.readString(Paths.get("./input_day5")).split(",");
        List<String> results = execute(program, "1");
        assertThat(results).containsExactly("0", "0", "0", "0", "0", "0", "0", "0", "0", "12234644");
    }

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "123,1"
    })
    void jumpPositionMode(String input, String expectedResult) {
        String program = "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "123,1"
    })
    void jumpImmediateMode(String input, String expectedResult) {
        String program = "3,3,1105,-1,9,1101,0,0,12,4,12,99,1";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "8,1",
            "123,0"
    })
    void equalPositionMode(String input, String expectedResult) {
        String program = "3,9,8,9,10,9,4,9,99,-1,8";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "8,1",
            "123,0"
    })
    void equalImmediateMode(String input, String expectedResult) {
        String program = "3,3,1108,-1,8,3,4,3,99";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "123,0",
            "4,1"
    })
    void lessPositionMode(String input, String expectedResult) {
        String program = "3,9,7,9,10,9,4,9,99,-1,8";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "123,0",
            "4,1"
    })
    void lessImmediateMode(String input, String expectedResult) {
        String program = "3,3,1107,-1,8,3,4,3,99";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "7,999",
            "8,1000",
            "9,1001"
    })
    void largeExample(String input, String expectedResult) {
        String program = "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31," +
                "1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104," +
                "999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99";
        List<String> result = execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @Test
    void forthPuzzle() throws IOException {
        String[] program = Files.readString(Paths.get("./input_day5")).split(",");
        List<String> results = execute(program, "5");
        assertThat(results).containsExactly("3508186");
    }

    @Test
    void outputItsInput() {
        List<String> output = execute(INPUT, "3,0,4,0,99");
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
        execute(instructions, INPUT);
        return instructions[0];
    }

    String execute(String program) {
        String[] instructions = program.split(",");
        execute(instructions, INPUT);
        return String.join(",", instructions);
    }

    private List<String> execute(String input, String program) {
        return execute(program.split(","), input);
    }

    private List<String> execute(String[] memory, String input) {
        List<String> outputs = new ArrayList<>();
        for (int memoryIndex = 0; memoryIndex < memory.length; ) {
            String operationCode = memory[memoryIndex];
            Operation operation = operationFor(operationCode, outputs, input);
            memoryIndex = operation.execute(memory, memoryIndex);
        }
        return outputs;
    }

    private Operation operationFor(String operationCode, List<String> outputs, String input) {
        if (STOP_OPERATION.equals(operationCode)) {
            return new Stop();
        } else if (operationCode.endsWith(SUM_OPERATION)) {
            return new Sum(operationCode);
        } else if (operationCode.endsWith(MULTIPLY_OPERATION)) {
            return new Multiply(operationCode);
        } else if (operationCode.endsWith(SAVE_OPERATION)) {
            return new Save(input);
        } else if (operationCode.endsWith(READ_OPERATION)) {
            return new Read(operationCode, outputs);
        } else if (operationCode.endsWith(JUMP_IF_TRUE)) {
            return new JumpIfTrue(operationCode);
        } else if (operationCode.endsWith(JUMP_IF_FALSE)) {
            return new JumpIfFalse(operationCode);
        } else if (operationCode.endsWith(LESS_OPERATION)) {
            return new Less(operationCode);
        } else if (operationCode.endsWith(EQUAL_OPERATION)) {
            return new Equal(operationCode);
        }
        throw new RuntimeException("Not Supported Operation! " + operationCode);
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

    private interface Operation {
        int execute(String[] memory, int memoryIndex);
    }

    public static abstract class OneOperandOperation implements Operation {
        private final Mode firstOperandMode;

        OneOperandOperation(String operationCode) {
            this.firstOperandMode = extractFirstOperandMode(operationCode);
        }

        private Mode extractFirstOperandMode(String operationCode) {
            if (operationCode.length() < 3) return new Position();

            return Mode.modeFor(operationCode.split("")[operationCode.length() - 3]);
        }

        @Override
        public int execute(String[] memory, int memoryIndex) {
            Integer firstOperand = firstOperandMode.read(memory, memoryIndex + 1);
            return execute(memory, memoryIndex, firstOperand);
        }

        protected abstract int execute(String[] memory, int memoryIndex, Integer firstOperand);
    }

    public static abstract class TwoOperandOperation extends OneOperandOperation {
        private final Mode secondOperandMode;

        TwoOperandOperation(String operationCode) {
            super(operationCode);
            this.secondOperandMode = extractSecondOperandMode(operationCode);
        }

        private Mode extractSecondOperandMode(String operationCode) {
            if (operationCode.length() < 4) return new Position();

            return Mode.modeFor(operationCode.split("")[operationCode.length() - 4]);
        }

        @Override
        public int execute(String[] memory, int memoryIndex, Integer firstOperand) {
            Integer secondOperand = secondOperandMode.read(memory, memoryIndex + 2);
            return execute(memory, memoryIndex, firstOperand, secondOperand);
        }

        protected abstract int execute(String[] memory, int memoryIndex, Integer firstOperand, Integer secondOperand);
    }

    public static abstract class TwoOperandWithResult extends TwoOperandOperation {
        private static final int INSTRUCTION_SIZE = 4;
        private final Mode resultMode;

        TwoOperandWithResult(String operationCode) {
            super(operationCode);
            this.resultMode = new Position();
        }

        @Override
        protected int execute(String[] memory, int memoryIndex, Integer firstOperand, Integer secondOperand) {
            String value = String.valueOf(execute(firstOperand, secondOperand));
            resultMode.write(memory, memoryIndex + 3, value);
            return memoryIndex + INSTRUCTION_SIZE;
        }

        protected abstract int execute(int firstOperand, int secondOperand);
    }

    private static class Multiply extends TwoOperandWithResult {

        Multiply(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(int firstOperand, int secondOperand) {
            return firstOperand * secondOperand;
        }
    }

    private static class Sum extends TwoOperandWithResult {
        Sum(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(int firstOperand, int secondOperand) {
            return firstOperand + secondOperand;
        }
    }

    private static class Save implements Operation {

        private final String input;

        Save(String input) {
            this.input = input;
        }

        @Override
        public int execute(String[] memory, int memoryIndex) {
            int savePosition = Integer.parseInt(memory[memoryIndex + 1]);
            memory[savePosition] = input;
            return memoryIndex + 2;
        }
    }

    private static class Read extends OneOperandOperation {
        private final List<String> outputs;

        Read(String operationCode, List<String> outputs) {
            super(operationCode);
            this.outputs = outputs;
        }

        @Override
        protected int execute(String[] memory, int memoryIndex, Integer firstOperand) {
            outputs.add(String.valueOf(firstOperand));
            return memoryIndex + 2;
        }
    }

    private static class Stop implements Operation {
        @Override
        public int execute(String[] memory, int memoryIndex) {
            return memory.length;
        }
    }

    public static abstract class Jump extends TwoOperandOperation {
        Jump(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(String[] memory, int memoryIndex, Integer firstOperand, Integer secondOperand) {
            if (jumpCondition(firstOperand)) return secondOperand;

            return memoryIndex + 3;
        }

        protected abstract boolean jumpCondition(Integer firstOperand);
    }

    private static class JumpIfTrue extends Jump {
        JumpIfTrue(String operationCode) {
            super(operationCode);
        }

        @Override
        protected boolean jumpCondition(Integer firstOperand) {
            return firstOperand != 0;
        }
    }

    private static class JumpIfFalse extends Jump {
        JumpIfFalse(String operationCode) {
            super(operationCode);
        }

        @Override
        protected boolean jumpCondition(Integer firstOperand) {
            return firstOperand == 0;
        }
    }

    private static class Less extends TwoOperandWithResult {

        Less(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(int firstOperand, int secondOperand) {
            return firstOperand < secondOperand ? 1 : 0;
        }
    }

    private static class Equal extends TwoOperandWithResult {

        Equal(String operationCode) {
            super(operationCode);
        }

        @Override
        protected int execute(int firstOperand, int secondOperand) {
            return firstOperand == secondOperand ? 1 : 0;
        }
    }
}
