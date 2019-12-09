package it.matteopierro.computer;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private final String[] instructions;
    private final Map<Integer, String> extraMemory = new HashMap<>();

    public Memory(String[] instructions) {
        this.instructions = instructions;
    }

    public void set(int address, String value) {
        if (address < instructions.length) {
            instructions[address] = value;
            return;
        }
        extraMemory.put(address, value);
    }

    public String get(Integer address) {
        if (address < instructions.length)
            return instructions[address];
        return extraMemory.getOrDefault(address, "0");
    }

    public int instructionLength() {
        return instructions.length;
    }
}
