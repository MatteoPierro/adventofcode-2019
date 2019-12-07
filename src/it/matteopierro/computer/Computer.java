package it.matteopierro.computer;

import java.util.ArrayList;
import java.util.List;

public class Computer {
    private static final String SUM_OPERATION = "1";
    private static final String MULTIPLY_OPERATION = "2";
    private static final String SAVE_OPERATION = "3";
    private static final String STOP_OPERATION = "99";
    private static final String READ_OPERATION = "4";
    private static final String JUMP_IF_TRUE = "5";
    private static final String JUMP_IF_FALSE = "6";
    private static final String LESS_OPERATION = "7";
    private static final String EQUAL_OPERATION = "8";

    public List<String> execute(String program, String... inputs) {
        return execute(program.split(","), inputs);
    }

    public List<String> execute(String[] memory, String... inputs) {
        List<String> outputs = new ArrayList<String>();
        for (int memoryIndex = 0; memoryIndex < memory.length; ) {
            String operationCode = memory[memoryIndex];
            Operation operation = operationFor(operationCode, outputs, inputs);
            memoryIndex = operation.execute(memory, memoryIndex);
        }
        return outputs;
    }

    private Operation operationFor(String operationCode, List<String> outputs, String... inputs) {
        if (STOP_OPERATION.equals(operationCode)) {
            return new Stop();
        } else if (operationCode.endsWith(SUM_OPERATION)) {
            return new Sum(operationCode);
        } else if (operationCode.endsWith(MULTIPLY_OPERATION)) {
            return new Multiply(operationCode);
        } else if (operationCode.endsWith(SAVE_OPERATION)) {
            return new Save(inputs);
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

        private final String[] input;
        private int currentInput;

        Save(String... input) {
            this.input = input;
            this.currentInput = 0;
        }

        @Override
        public int execute(String[] memory, int memoryIndex) {
            int savePosition = Integer.parseInt(memory[memoryIndex + 1]);
            memory[savePosition] = input[currentInput++];
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