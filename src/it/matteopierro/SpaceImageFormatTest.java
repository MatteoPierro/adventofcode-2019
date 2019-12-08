package it.matteopierro;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;


class SpaceImageFormatTest {

    @Test
    void createsGroup() {
        int wide = 3;
        int tall = 2;
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2);

        assertThat(layersFor(input, wide, tall)).containsExactly(
                List.of(1, 2, 3, 4, 5, 6),
                List.of(7, 8, 9, 0, 1, 2)
        );
    }

    @Test
    void findGroupWithMinimumZeros() {
        int wide = 3;
        int tall = 2;
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2);
        List<List<Integer>> groups = layersFor(input, wide, tall);
        assertThat(findWithMinimumZeros(groups)).isEqualTo(List.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    void countNumberOfGivenDigit() {
        assertThat(countDigits(List.of(1, 2, 3, 4, 5, 6), 1)).isEqualTo(1);
        assertThat(countDigits(List.of(1, 2, 3, 4, 5, 6), 2)).isEqualTo(1);
    }

    @Test
    void firstPuzzle() throws IOException {
        List<Integer> input = Stream.of(Files.readString(Paths.get("./input_day8")).split(""))
                .map(Integer::parseInt)
                .collect(toList());

        List<List<Integer>> groups = layersFor(input, 25, 6);
        List<Integer> groupMinimumZeros = findWithMinimumZeros(groups);
        assertThat(
                countDigits(groupMinimumZeros, 1) * countDigits(groupMinimumZeros, 2)
        ).isEqualTo(1792);
    }

    private int countDigits(List<Integer> integers, int digit) {
        return (int) integers.stream()
                .filter(d-> d==digit)
                .count();
    }

    private List<Integer> findWithMinimumZeros(List<List<Integer>> groups) {
        return groups.stream()
                .map(group -> {
                    return tuple(numberOfZeros(group), group);
                })
                .min((o1, o2) -> (int) (o1.v1 - o2.v1))
                .map(t -> t.v2)
                .orElseThrow();
    }

    private long numberOfZeros(List<Integer> group) {
        return group.stream()
                .filter(number -> number == 0)
                .count();
    }

    private List<List<Integer>> layersFor(List<Integer> input, int wide, int tall) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> group = new ArrayList<>();
        for (int value : input) {
            if (group.size() == wide * tall) {
                result.add(group);
                group = new ArrayList<>();
            }
            group.add(value);
        }
        result.add(group);
        return result;
    }
}
