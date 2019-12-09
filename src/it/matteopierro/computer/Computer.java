package it.matteopierro.computer;

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
        return execute(memory, new ComputerListener(inputs));
    }

    public int execute(String program, ComputerListener listener) {
        List<String> results = execute(program.split(","), listener);
        return Integer.parseInt(results.get(results.size() - 1));
    }

    private List<String> execute(String[] memory, ComputerListener listener) {
        for (int memoryIndex = 0; memoryIndex < memory.length; ) {
            String operationCode = memory[memoryIndex];
            Operation operation = operationFor(operationCode, listener);
            memoryIndex = operation.execute(memory, memoryIndex);
        }
        return listener.results();
    }

    private Operation operationFor(String operationCode, ComputerListener listener) {
        if (operationCode.endsWith(STOP_OPERATION)) {
            return new Stop();
        } else if (operationCode.endsWith(SUM_OPERATION)) {
            return new Sum(operationCode);
        } else if (operationCode.endsWith(MULTIPLY_OPERATION)) {
            return new Multiply(operationCode);
        } else if (operationCode.endsWith(SAVE_OPERATION)) {
            return new Read(listener);
        } else if (operationCode.endsWith(READ_OPERATION)) {
            return new Store(operationCode, listener);
        } else if (operationCode.endsWith(JUMP_IF_TRUE)) {
            return new JumpIfTrue(operationCode);
        } else if (operationCode.endsWith(JUMP_IF_FALSE)) {
            return new JumpIfFalse(operationCode);
        } else if (operationCode.endsWith(LESS_OPERATION)) {
            return new Less(operationCode);
        } else if (operationCode.endsWith(EQUAL_OPERATION)) {
            return new Equal(operationCode);
        }
        throw new RuntimeException("Not Supported Operation!" + operationCode);
    }

    private interface Mode {
        static Mode modeFor(String mode) {
            return mode.equals("0")
                    ? new Position()
                    : new Immediate();
        }

        Long read(String[] memory, Integer address);

        void write(String[] memory, Integer address, String value);
    }

    private static class Position implements Mode {
        @Override
        public Long read(String[] memory, Integer address) {
            int position = Integer.parseInt(memory[address]);
            return Long.parseLong(memory[position]);
        }

        @Override
        public void write(String[] memory, Integer address, String value) {
            int position = Integer.parseInt(memory[address]);
            memory[position] = String.valueOf(value);
        }
    }

    private static class Immediate implements Mode {
        @Override
        public Long read(String[] memory, Integer address) {
            return Long.parseLong(memory[address]);
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
            Long firstOperand = firstOperandMode.read(memory, memoryIndex + 1);
            return execute(memory, memoryIndex, firstOperand);
        }

        protected abstract int execute(String[] memory, int memoryIndex, Long firstOperand);
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
        public int execute(String[] memory, int memoryIndex, Long firstOperand) {
            Long secondOperand = secondOperandMode.read(memory, memoryIndex + 2);
            return execute(memory, memoryIndex, firstOperand, secondOperand);
        }

        protected abstract int execute(String[] memory, int memoryIndex, long firstOperand, long secondOperand);
    }

    public static abstract class TwoOperandWithResult extends TwoOperandOperation {
        private static final int INSTRUCTION_SIZE = 4;
        private final Mode resultMode;

        TwoOperandWithResult(String operationCode) {
            super(operationCode);
            this.resultMode = new Position();
        }

        @Override
        protected int execute(String[] memory, int memoryIndex, long firstOperand, long secondOperand) {
            String value = String.valueOf(execute(firstOperand, secondOperand));
            resultMode.write(memory, memoryIndex + 3, value);
            return memoryIndex + INSTRUCTION_SIZE;
        }

        protected abstract long execute(long firstOperand, long secondOperand);
    }

    private static class Multiply extends TwoOperandWithResult {

        Multiply(String operationCode) {
            super(operationCode);
        }

        @Override
        protected long execute(long firstOperand, long secondOperand) {
            return firstOperand * secondOperand;
        }
    }

    private static class Sum extends TwoOperandWithResult {
        Sum(String operationCode) {
            super(operationCode);
        }

        @Override
        protected long execute(long firstOperand, long secondOperand) {
            return firstOperand + secondOperand;
        }
    }

    private static class Read implements Operation {

        private final ComputerListener listener;

        Read(ComputerListener listener) {
            this.listener = listener;
        }

        @Override
        public int execute(String[] memory, int memoryIndex) {
            int savePosition = Integer.parseInt(memory[memoryIndex + 1]);
            memory[savePosition] = listener.onReadRequested();
            return memoryIndex + 2;
        }
    }

    private static class Store extends OneOperandOperation {
        private final ComputerListener listener;

        Store(String operationCode, ComputerListener listener) {
            super(operationCode);
            this.listener = listener;
        }

        @Override
        protected int execute(String[] memory, int memoryIndex, Long firstOperand) {
            listener.onStoreRequested(String.valueOf(firstOperand));
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
        protected int execute(String[] memory, int memoryIndex, long firstOperand, long secondOperand) {
            if (jumpCondition(firstOperand)) return (int) secondOperand;

            return memoryIndex + 3;
        }

        protected abstract boolean jumpCondition(long firstOperand);
    }

    private static class JumpIfTrue extends Jump {
        JumpIfTrue(String operationCode) {
            super(operationCode);
        }

        @Override
        protected boolean jumpCondition(long firstOperand) {
            return firstOperand != 0;
        }
    }

    private static class JumpIfFalse extends Jump {
        JumpIfFalse(String operationCode) {
            super(operationCode);
        }

        @Override
        protected boolean jumpCondition(long firstOperand) {
            return firstOperand == 0;
        }
    }

    private static class Less extends TwoOperandWithResult {

        Less(String operationCode) {
            super(operationCode);
        }

        @Override
        protected long execute(long firstOperand, long secondOperand) {
            return firstOperand < secondOperand ? 1 : 0;
        }
    }

    private static class Equal extends TwoOperandWithResult {

        Equal(String operationCode) {
            super(operationCode);
        }

        @Override
        protected long execute(long firstOperand, long secondOperand) {
            return firstOperand == secondOperand ? 1 : 0;
        }
    }
}