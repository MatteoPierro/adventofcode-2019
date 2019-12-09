package it.matteopierro;

import it.matteopierro.computer.Computer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class SensorBoostTest {

    public static final String INPUT = "123";

    @Test
    void outputTheLargeNumberInTheMiddle() {
        List<String> result = new Computer().execute("104,1125899906842624,99");
        assertThat(result).containsExactlyInAnyOrder("1125899906842624");
    }

    @Test
    @Disabled
    void copyTheInputToTheOutput() {
        List<String> result = new Computer().execute("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99", INPUT);
        assertThat(result).containsExactlyInAnyOrder(INPUT);
    }
}
