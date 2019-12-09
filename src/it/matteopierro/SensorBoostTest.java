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
}
