package it.matteopierro;

import org.jooq.lambda.Seq;
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
        assertThat(findLayerWithMinimumZeros(groups)).isEqualTo(List.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    void countNumberOfGivenDigit() {
        assertThat(countDigits(List.of(1, 2, 3, 4, 5, 6), 1)).isEqualTo(1);
        assertThat(countDigits(List.of(1, 2, 3, 4, 5, 6), 2)).isEqualTo(1);
    }

    @Test
    void firstPuzzle() throws IOException {
        List<Integer> imageData = toImageData(Files.readString(Paths.get("./input_day8")));
        List<List<Integer>> layers = layersFor(imageData, 25, 6);
        List<Integer> groupMinimumZeros = findLayerWithMinimumZeros(layers);
        assertThat(
                countDigits(groupMinimumZeros, 1) * countDigits(groupMinimumZeros, 2)
        ).isEqualTo(1792);
    }

    @Test
    void finalImage() {
        List<List<Integer>> layers = layersFor(toImageData("0222112222120000"), 2, 2);
        List<Integer> image = decodeImage(layers);

        assertThat(image).containsExactly(0, 1, 1, 0);
    }

    @Test
    void secondPuzzle() throws IOException {
        List<Integer> imageData = toImageData(Files.readString(Paths.get("./input_day8")));
        List<List<Integer>> layers = layersFor(imageData, 25, 6);
        List<Integer> image = decodeImage(layers);

        assertThat(image).containsExactly(
                1,0,0,0,0,0,0,1,1,0,1,1,1,1,0,0,1,1,0,0,1,0,0,1,0,
                1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,1,0,
                1,0,0,0,0,0,0,0,1,0,1,1,1,0,0,1,0,0,0,0,1,1,1,1,0,
                1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,1,0,
                1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,1,0,
                1,1,1,1,0,0,1,1,0,0,1,1,1,1,0,0,1,1,0,0,1,0,0,1,0
        );
    }

    private List<Integer> decodeImage(List<List<Integer>> layers) {
        return Seq.range(0, layers.get(0).size())
                .map(pixelIndex -> decodePixel(layers, pixelIndex))
                .toList();
    }

    private Integer decodePixel(List<List<Integer>> layers, Integer pixelIndex) {
        return layers.stream()
                .map(layer -> layer.get(pixelIndex))
                .filter(layer -> layer == 1 || layer == 0)
                .findFirst()
                .orElse(2);
    }

    private List<Integer> toImageData(String imageData) {
        return Stream.of(imageData.split(""))
                .map(Integer::parseInt)
                .collect(toList());
    }

    private int countDigits(List<Integer> integers, int digit) {
        return (int) integers.stream()
                .filter(d-> d==digit)
                .count();
    }

    private List<Integer> findLayerWithMinimumZeros(List<List<Integer>> groups) {
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
