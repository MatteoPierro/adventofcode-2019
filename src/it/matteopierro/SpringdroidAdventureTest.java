package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class SpringdroidAdventureTest {

    @Test
    void firstPuzzle() throws IOException {
        var program = Files.readString(Paths.get("./input_day21"));
        var springDroidProgram = List.of(
                "OR A J",  // J = A
                "AND B J", // J = A && B
                "AND C J", // J = A && B && C
                "NOT J J", // J = !(A && B && C)
                "AND D J", // J = !(A && B && C) && D
                "WALK"
        );

        SpringdroidAdventure listener = new SpringdroidAdventure(springDroidProgram);
        int result = new Computer().execute(program, listener);

        assertThat(result).isEqualTo(19350938);
    }

    private class SpringdroidAdventure extends ComputerListener {

        private final List<Integer> input;
        private int currentIndex;

        public SpringdroidAdventure(List<String> springDroidProgram) {
            input = springDroidProgram.stream()
                    .map(i -> i + "\n")
                    .flatMapToInt(String::chars)
                    .boxed()
                    .collect(toList());
            currentIndex = 0;
        }

        @Override
        public String onReadRequested() {
            return String.valueOf(input.get(currentIndex++));
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
        }
    }
}
