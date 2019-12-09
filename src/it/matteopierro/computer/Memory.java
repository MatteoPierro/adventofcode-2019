package it.matteopierro.computer;

public class Memory {
    private final String[] instructions;

    public Memory(String[] instructions) {
        this.instructions = instructions;
    }

    public void set(int address, String value) {
        if (address < instructions.length) {
            instructions[address] = value;
            return;
        }
        throw new UnsupportedOperationException();
    }

    public String get(Integer address) {
        if (address < instructions.length)
            return instructions[address];
        throw new UnsupportedOperationException();
    }

    public int instructionLength() {
        return instructions.length;
    }
}
