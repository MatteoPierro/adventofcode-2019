package it.matteopierro;

import it.matteopierro.computer.Computer;
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
    void copyTheInputToTheOutput() {
        String program = "109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99";
        String result = String.join(",", new Computer().execute(program));
        assertThat(result).isEqualTo(program);
    }
}
