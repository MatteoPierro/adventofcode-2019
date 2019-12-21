package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class SpringdroidAdventureTest {

    private String program;

    @BeforeEach
    void setUp() throws IOException {
        program = Files.readString(Paths.get("./input_day21"));
    }

    @Test
    void firstPuzzle() {
        var program = List.of(
                "OR A J",  // J = A
                "AND B J", // J = A && B
                "AND C J", // J = A && B && C
                "NOT J J", // J = !(A && B && C)
                "AND D J", // J = !(A && B && C) && D
                "WALK"
        );

        assertThat(execute(program)).isEqualTo(19350938);
    }

    @Test
    void secondPuzzle() {
        var program = List.of(
                "OR A J",  // J = A
                "AND B J", // J = A && B
                "AND C J", // J = A && B && C
                "NOT J J", // J = !(A && B && C)
                "AND D J", // J = !(A && B && C) && D
                "OR E T", // T = E
                "OR H T", // T = E || H
                "AND T J", // J = !(A && B && C) && D && (E || H)
                "RUN"
        );

        assertThat(execute(program)).isEqualTo(1142986901);
    }

    private int execute(List<String> program) {
        SpringdroidAdventure listener = new SpringdroidAdventure(program);
        return new Computer().execute(this.program, listener);
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
