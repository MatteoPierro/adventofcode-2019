package it.matteopierro;

import it.matteopierro.computer.Computer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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

        List<String> results = computer.execute("109,1,204,-1,99");

        assertThat(computer.relativeBase).isEqualTo(1);
        assertThat(results).containsExactly("109");
    }

    @Test
    void output16DigitOutput() {
        String program = "1102,34915192,34915192,7,4,7,99,0";
        String result = String.join(",", new Computer().execute(program));
        assertThat(result.length()).isEqualTo(16);
        assertThat(result).isEqualTo(String.valueOf(34915192L * 34915192L));
    }

    @Test
    @Disabled
    void copyTheInputToTheOutput() {
        String program = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99";
        String result = String.join(",", new Computer().execute(program));
        assertThat(result).isEqualTo(program);
    }
}
