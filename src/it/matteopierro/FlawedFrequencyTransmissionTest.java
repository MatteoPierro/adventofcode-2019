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
        assertThat(basePattern(1, 8)).containsExactly(1, 0, -1, 0, 1, 0, -1, 0);
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
        assertThat(input).hasSize(650);
        String largeInput = input.repeat(10000);
        assertThat(largeInput).hasSize(6500000);
        assertThat((largeInput.length() - 5978017)).isEqualTo(521983);
        var patternToRepeat = largeInput.substring(5978017);
        assertThat(patternToRepeat).hasSize(521983);
    }

    @Test
    void secondPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day16"));
        assertThat(input).hasSize(650);
        String largeInput = input.repeat(10000);
        assertThat(largeInput).hasSize(6500000);
        assertThat((largeInput.length() - 5978017)).isEqualTo(521983);
        var patternToRepeat = largeInput.substring(5978017);
        assertThat(patternToRepeat).hasSize(521983);
        assertThat(sumEverything(patternToRepeat, 100).subList(0, 8)).containsExactly(5, 2, 5, 4, 1, 0, 2, 6);
    }

    private List<Integer> sumEverything(String input, int times) {
        char[] inputNumbers = input.toCharArray();
        List<Integer> numbers = new ArrayList<>();
        for (char inputNumber : inputNumbers) {
            numbers.add(Integer.parseInt(inputNumber + ""));
        }

        for (int j = 0; j < times; j++) {
            int numToAppend = 0;
            for (int i = inputNumbers.length - 1; i >= 0; i--) {
                numToAppend += numbers.get(i);
                numbers.set(i, Math.abs(numToAppend % 10));
            }
        }

        return numbers;
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
        for (char inputNumber : inputNumbers) {
            numbers.add(Integer.parseInt(inputNumber + ""));
        }

        for (int i = 0; i < inputNumbers.length; i++) {
            List<Integer> sequence = basePattern(i + 1, inputNumbers.length);
            result.append(newDigit(numbers, sequence));
        }

        return result.toString();
    }

    private int newDigit(List<Integer> numbers, List<Integer> sequence) {
        return Math.abs(compute(sequence, numbers) % 10);
    }

    private int compute(List<Integer> sequence, List<Integer> numbers) {
        int result = 0;
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
