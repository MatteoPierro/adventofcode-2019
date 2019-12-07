package it.matteopierro;

import it.matteopierro.computer.Computer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.copyOf;
import static org.assertj.core.api.Assertions.assertThat;

class AmplificationCircuitTest {

    @Test
    void findAllPermutations() {
        int[] inputs = {3, 4, 7};
        assertThat(permutations(inputs)).containsExactlyInAnyOrder(
                new int[]{3, 4, 7},
                new int[]{3, 7, 4},
                new int[]{4, 7, 3},
                new int[]{4, 3, 7},
                new int[]{7, 3, 4},
                new int[]{7, 4, 3}
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0:43210",
            "3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0:54321",
            "3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0:65210"
    }, delimiter = ':')
    void maxThrusterSignal(String program, int expectedMaxThrusterSignal) {
        assertThat(maxThrusterSignal(program)).isEqualTo(expectedMaxThrusterSignal);
    }

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day7"));
        assertThat(maxThrusterSignal(program)).isEqualTo(45730);
    }

    private int maxThrusterSignal(String program) {
        return permutations(new int[]{0, 1, 2, 3, 4})
                .stream()
                .mapToInt(inputs -> calculateThrusterSignal(program, inputs))
                .max()
                .orElseThrow();
    }

    private int calculateThrusterSignal(String program, int[] inputs) {
        int result = 0;

        for (int input : inputs) {
            result = new Computer().execute(program, input, result);
        }

        return result;
    }

    private List<int[]> permutations(int[] inputs) {
        return permutations(inputs.length, inputs, new ArrayList<>());
    }

    private static List<int[]> permutations(int n, int[] elements, List<int[]> permutations) {
        if (n == 1) {
            permutations.add(copyOf(elements, elements.length));
            return permutations;
        }

        for (int i = 0; i < n - 1; i++) {
            permutations(n - 1, elements, permutations);
            if (n % 2 == 0) {
                swap(elements, i, n - 1);
            } else {
                swap(elements, 0, n - 1);
            }
        }
        return permutations(n - 1, elements, permutations);
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }
}