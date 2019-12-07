package it.matteopierro;

import it.matteopierro.computer.Computer;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChanceOfAsteroidsTest {
    private static final String INPUT = "1";
    private final Computer computer = new Computer();

    @ParameterizedTest
    @CsvSource(value = {
            "1,0,0,0,99:2,0,0,0,99",
            "2,3,0,3,99:2,3,0,6,99",
            "2,4,4,5,99,0:2,4,4,5,99,9801",
            "1,1,1,4,99,5,6,0,99:30,1,1,4,2,5,6,0,99",
            "1,9,10,3,2,3,11,0,99,30,40,50:3500,9,10,70,2,3,11,0,99,30,40,50",
    }, delimiter = ':')
    void executeSequence(String input, String expected) {
        assertThat(execute(input)).isEqualTo(expected);
    }

    @Test
    void firstPuzzle() {
        int noun = 12;
        int verb = 2;
        String result = compute(noun, verb);

        assertThat(result).isEqualTo("4945026");
    }

    @Test
    void secondPuzzle() {
        Seq<Integer> nouns = Seq.range(0, 100);
        Seq<Integer> verbs = Seq.range(0, 100);
        Tuple2<Integer, Integer> solution = nouns.crossJoin(verbs)
                .findFirst(t -> "19690720".equals(compute(t.v1, t.v2)))
                .orElseThrow();

        int noun = solution.v1;
        int verb = solution.v2;
        assertThat(noun).isEqualTo(52);
        assertThat(verb).isEqualTo(96);
        assertThat(100 * noun + verb).isEqualTo(5296);
    }

    @Test
    void thirdPuzzle() throws IOException {
        String[] program = Files.readString(Paths.get("./input_day5")).split(",");
        List<String> results = computer.execute(program, "1");
        assertThat(results).containsExactly("0", "0", "0", "0", "0", "0", "0", "0", "0", "12234644");
    }

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "123,1"
    })
    void jumpPositionMode(String input, String expectedResult) {
        String program = "3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "123,1"
    })
    void jumpImmediateMode(String input, String expectedResult) {
        String program = "3,3,1105,-1,9,1101,0,0,12,4,12,99,1";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "8,1",
            "123,0"
    })
    void equalPositionMode(String input, String expectedResult) {
        String program = "3,9,8,9,10,9,4,9,99,-1,8";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "8,1",
            "123,0"
    })
    void equalImmediateMode(String input, String expectedResult) {
        String program = "3,3,1108,-1,8,3,4,3,99";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "123,0",
            "4,1"
    })
    void lessPositionMode(String input, String expectedResult) {
        String program = "3,9,7,9,10,9,4,9,99,-1,8";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "123,0",
            "4,1"
    })
    void lessImmediateMode(String input, String expectedResult) {
        String program = "3,3,1107,-1,8,3,4,3,99";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @ParameterizedTest
    @CsvSource({
            "7,999",
            "8,1000",
            "9,1001"
    })
    void largeExample(String input, String expectedResult) {
        String program = "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31," +
                "1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104," +
                "999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99";
        List<String> result = computer.execute(input, program);
        assertThat(result).containsExactly(expectedResult);
    }

    @Test
    void forthPuzzle() throws IOException {
        String[] program = Files.readString(Paths.get("./input_day5")).split(",");
        List<String> results = computer.execute(program, "5");
        assertThat(results).containsExactly("3508186");
    }

    @Test
    void outputItsInput() {
        List<String> output = computer.execute(INPUT, "3,0,4,0,99");
        assertThat(output).containsExactly(INPUT);
    }

    @Test
    void parameterModes() {
        assertThat(execute("1002,4,3,4,33")).isEqualTo("1002,4,3,4,99");
    }

    private String compute(int noun, int verb) {
        try {
            String[] instructions = Files.readString(Paths.get("./input_day2")).split(",");
            instructions[1] = String.valueOf(noun);
            instructions[2] = String.valueOf(verb);
            return compute(instructions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String compute(String[] instructions) {
        computer.execute(instructions, INPUT);
        return instructions[0];
    }

    String execute(String program) {
        String[] instructions = program.split(",");
        computer.execute(instructions, INPUT);
        return String.join(",", instructions);
    }
}
