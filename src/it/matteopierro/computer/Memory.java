package it.matteopierro.computer;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private final String[] instructions;
    private final Map<Integer, String> extraMemory = new HashMap<>();
    private int relativeBase = 0;

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

    public void addRelativeOffset(Long offset) {
        relativeBase += (int) (long) offset;
    }

    public String getRelative(int offset) {
        return this.get(relativeBase + offset);
    }

    public void setRelative(int offset, String value) {
        this.set(relativeBase + offset, value);
    }

    public int relativeBase() {
        return relativeBase;
    }
}
