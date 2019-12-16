package it.matteopierro;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlawedFrequencyTransmissionTest {

    @Test
    void repeatSequenceSkippingTheFirst() {
        assertThat(basePattern(1, 8)).containsExactly(1,0,-1,0,1,0,-1,0);
        assertThat(basePattern(650, 650)).hasSize(650);
    }

    @Test
    void createNewSignal() {
        var input = "12345678";
        assertThat(newSignal(input)).isEqualTo("48226158");
        assertThat(newSignal(input, 2)).isEqualTo("34040438");
        assertThat(newSignal(input, 3)).isEqualTo("03415518");
        assertThat(newSignal(input, 4)).isEqualTo("01029498");
        assertThat(newSignal(input, 100)).isEqualTo("23845678");
    }

    @Test
    void largeExamples() {
        assertThat(newSignal("80871224585914546619083218645595", 100).substring(0, 8)).isEqualTo("24176176");
        assertThat(newSignal("19617804207202209144916044189917", 100).substring(0, 8)).isEqualTo("73745418");
        assertThat(newSignal("69317163492948606335995924319873", 100).substring(0, 8)).isEqualTo("52432133");
    }

    @Test
    void firstPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day16"));
        assertThat(newSignal(input, 100).substring(0, 8)).isEqualTo("52611030");
    }

    private String newSignal(String input, int iterations) {
        var result = input;
        for (int i = 0; i < iterations; i++) {
            result = newSignal(result);
        }
        return result;
    }

    private String newSignal(String input) {
        var result = new StringBuffer();
        char[] inputNumbers = input.toCharArray();
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < inputNumbers.length; i++) {
            numbers.add(Integer.parseInt(inputNumbers[i] + ""));
        }

        for (int i = 0; i < inputNumbers.length; i++) {
            List<Integer> sequence = basePattern(i + 1, inputNumbers.length);
            result.append(newDigit(numbers, sequence));
        }

        return result.toString();
    }

    private String newDigit(List<Integer> numbers, List<Integer> sequence) {
        String result = String.valueOf(compute(sequence, numbers));
        return result.substring(result.length() - 1);
    }

    private long compute(List<Integer> sequence, List<Integer> numbers) {
        long result = 0;
        for (int i = 0; i < sequence.size(); i++) {
            Integer digit = sequence.get(i);
            Integer number = numbers.get(i);
            result += digit * number;
        }
        return result;
    }

    private List<Integer> basePattern(int position, int length) {
        Integer[] basePattern = {0, 1, 0, -1};
        int first = basePattern[0];
        List<Integer> result = repeatBase(first, position - 1);
        int baseIndex = 1;
        while (result.size() < length) {
            int base = basePattern[baseIndex];
            int repeat = Math.min(position, length - result.size());
            result.addAll(repeatBase(base, repeat));
            baseIndex = (baseIndex + 1) % basePattern.length;
        }
        return result;
    }

    private List<Integer> repeatBase(int element, int numberOfTime) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < numberOfTime; i++) {
            result.add(element);
        }
        return result;
    }


}
