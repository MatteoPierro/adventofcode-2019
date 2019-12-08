package it.matteopierro;

import it.matteopierro.imageDecoder.ImageDecoder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class SpaceImageFormatTest {

    private final ImageDecoder imageDecoder = new ImageDecoder();

    @Test
    void createsGroup() {
        int wide = 3;
        int tall = 2;
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2);

        assertThat(imageDecoder.layersFor(input, wide, tall)).containsExactly(
                List.of(1, 2, 3, 4, 5, 6),
                List.of(7, 8, 9, 0, 1, 2)
        );
    }

    @Test
    void findGroupWithMinimumZeros() {
        int wide = 3;
        int tall = 2;
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2);
        List<List<Integer>> groups = imageDecoder.layersFor(input, wide, tall);
        assertThat(imageDecoder.findLayerWithMinimumZeros(groups)).isEqualTo(List.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    void countNumberOfGivenDigit() {
        assertThat(imageDecoder.countDigits(List.of(1, 2, 3, 4, 5, 6), 1)).isEqualTo(1);
        assertThat(imageDecoder.countDigits(List.of(1, 2, 3, 4, 5, 6), 2)).isEqualTo(1);
    }

    @Test
    void firstPuzzle() throws IOException {
        List<Integer> imageData = imageDecoder.toImageData(Files.readString(Paths.get("./input_day8")));
        List<List<Integer>> layers = imageDecoder.layersFor(imageData, 25, 6);
        List<Integer> groupMinimumZeros = imageDecoder.findLayerWithMinimumZeros(layers);
        assertThat(
                imageDecoder.countDigits(groupMinimumZeros, 1) * imageDecoder.countDigits(groupMinimumZeros, 2)
        ).isEqualTo(1792);
    }

    @Test
    void finalImage() {
        List<List<Integer>> layers = imageDecoder.layersFor(imageDecoder.toImageData("0222112222120000"), 2, 2);
        List<Integer> image = imageDecoder.decodeImage(layers);

        assertThat(image).containsExactly(0, 1, 1, 0);
    }

    @Test
    void decodeImageSecondPuzzle() throws IOException {
        assertThat(imageDecoder.decodeImageDataFile("./input_day8")).containsExactly(
                1,0,0,0,0,0,0,1,1,0,1,1,1,1,0,0,1,1,0,0,1,0,0,1,0,
                1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,1,0,
                1,0,0,0,0,0,0,0,1,0,1,1,1,0,0,1,0,0,0,0,1,1,1,1,0,
                1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,1,0,0,0,0,1,0,0,1,0,
                1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1,0,0,1,0,1,0,0,1,0,
                1,1,1,1,0,0,1,1,0,0,1,1,1,1,0,0,1,1,0,0,1,0,0,1,0
        );
    }
}
