package it.matteopierro;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.copyOf;
import static org.assertj.core.api.Assertions.assertThat;

class AmplificationCircuitTest {

    @Test
    void findAllPermutations() {
        int[] inputs = {3,4,7};
        assertThat(permutations(inputs)).containsExactlyInAnyOrder(
                new int[]{3,4,7},
                new int[]{3,7,4},
                new int[]{4,7,3},
                new int[]{4,3,7},
                new int[]{7,3,4},
                new int[]{7,4,3}
        );
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
