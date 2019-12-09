package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import it.matteopierro.computer.Memory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class SensorBoostTest {

    @Test
    void outputTheLargeNumberInTheMiddle() {
        List<String> result = new Computer().execute("104,1125899906842624,99");
        assertThat(result).containsExactlyInAnyOrder("1125899906842624");
    }

    @Test
    void manageRelativeInstruction() {
        Computer computer = new Computer();

        String[] program = "109,1,204,-1,1001,100,1,100,1008,100,16,101,99".split(",");
        Memory memory = new Memory(program);
        List<String> results = computer.execute(program, new ComputerListener(), memory);

        assertThat(memory.relativeBase()).isEqualTo(1);
        assertThat(results).containsExactly("109");
        assertThat(memory.get(100)).contains("1");
        assertThat(memory.get(101)).contains("0");
    }

    @Test
    void output16DigitOutput() {
        String program = "1102,34915192,34915192,7,4,7,99,0";
        String result = String.join(",", new Computer().execute(program));
        assertThat(result.length()).isEqualTo(16);
        assertThat(result).isEqualTo(String.valueOf(34915192L * 34915192L));
    }

    @Test
    void copyTheInputToTheOutput() {
        String program = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99";
        String result = String.join(",", new Computer().execute(program));
        assertThat(result).isEqualTo(program);
    }

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day9"));
        List<String> results = new Computer().execute(program, "1");
        assertThat(results).containsExactly("3906448201");
    }

    @Test
    void secondPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day9"));
        List<String> results = new Computer().execute(program, "2");
        assertThat(results).containsExactly("59785");
    }
}
