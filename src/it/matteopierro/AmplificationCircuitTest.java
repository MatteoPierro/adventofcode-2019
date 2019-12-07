package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.junit.jupiter.api.Disabled;
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

    @Test
    @Disabled
    void feedbackLoop() {
        String program = "3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5";
        int[] inputs = {9,8,7,6,5};
        int signal = calculateThrusterSignal(program, inputs);
        assertThat(signal).isEqualTo(123);
    }

    private int maxThrusterSignal(String program) {
        return permutations(new int[]{0, 1, 2, 3, 4})
                .stream()
                .mapToInt(inputs -> calculateThrusterSignal(program, inputs))
                .max()
                .orElseThrow();
    }

    private int calculateThrusterSignal(String program, int[] inputs) {
        ChainableComputerListener fifthAmplifierListener = new ChainableComputerListener(inputs[4]);
        ComputerListener fourthAmplifierListener = new ChainableComputerListener(inputs[3], fifthAmplifierListener);
        ComputerListener thirdAmplifierListener = new ChainableComputerListener(inputs[2], fourthAmplifierListener);
        ComputerListener secondAmplifierListener =  new ChainableComputerListener(inputs[1], thirdAmplifierListener);
        ComputerListener firstAmplifierListener = new ChainableComputerListener(inputs[0], secondAmplifierListener);
        firstAmplifierListener.addInput("0");
        fifthAmplifierListener.setListener(firstAmplifierListener);

        int firstAmplifier = new Computer().execute(program, firstAmplifierListener);
        int secondAmplifier = new Computer().execute(program, secondAmplifierListener);
        int thirdAmplifier = new Computer().execute(program, thirdAmplifierListener);
        int fourthAmplifier = new Computer().execute(program, fourthAmplifierListener);
        int fifthAmplifier = new Computer().execute(program, fifthAmplifierListener);

        return fifthAmplifier;
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

    private static class ChainableComputerListener extends ComputerListener {
        private ComputerListener listener;

        public ChainableComputerListener(int input, ComputerListener listener) {
            super(input);
            this.listener = listener;
        }

        public ChainableComputerListener(int input) {
            super(input);
        }

        @Override
        public void addResult(String result) {
            super.addResult(result);
            listener.addInput(result);
        }

        public void setListener(ComputerListener listener) {
            this.listener = listener;
        }
    }
}
