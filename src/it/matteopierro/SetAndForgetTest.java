package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class SetAndForgetTest {

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day17"));
        List<String> results = new Computer().execute(program);
        List<List<String>> lines = findLines(results);
        List<Tuple2<Integer, Integer>> intersections = findIntersections(lines);
        int code = intersections.stream().mapToInt(t -> t.v1 * t.v2).sum();
        assertThat(code).isEqualTo(7584);

        for (List<String> line : lines) {
            printLine(line);
        }
    }

    @Test
    void functionA() {
        assertThat("R,4,R,12,R,10,L,12".length()).isEqualTo(18);
        List<String> routineA = routine("R,4,R,12,R,10,L,12");
        assertThat(routineA).containsExactly("82", "44", "52", "44", "82", "44", "49", "50", "44", "82", "44", "49", "48", "44", "76", "44", "49", "50", "10");
    }

    @Test
    void secondPuzzle() throws IOException {
        List<String> mainRoutine = routine("A,B,B,C,C,A,B,B,C,A");
        List<String> routineA = routine("R,4,R,12,R,10,L,12");
        List<String> routineB = routine("L,12,R,4,R,12");
        List<String> routineC = routine("L,12,L,8,R,10");
        List<String> noFeed = routine("n");
        List<String> input = new ArrayList<>(mainRoutine);
        input.addAll(routineA);
        input.addAll(routineB);
        input.addAll(routineC);
        input.addAll(noFeed);
        String[] program = Files.readString(Paths.get("./input_day17")).split(",");
        program[0] = "2";
        List<String> result = new Computer().execute(program, new ComputerListener(input));
        assertThat(result.get(result.size() -1)).isEqualTo("1016738");
    }

    private List<String> routine(String routine) {
        List<String> f = new ArrayList<>();
        for (char c : routine.toCharArray()) {
            f.add(String.valueOf((int) c));
        }
        f.add("10");
        return f;
    }

    private void printLine(List<String> line) {
        for (String symbol : line) {
            char c = (char) Integer.parseInt(symbol);
            System.out.print(c);
        }
        System.out.println();
    }

    private List<Tuple2<Integer, Integer>> findIntersections(List<List<String>> lines) {
        List<Tuple2<Integer, Integer>> result = new ArrayList<>();

        for (int y = 1; y < lines.size(); y++) {
            List<String> line = lines.get(y);
            for (int x = 1; x < line.size(); x++) {
                String tile = line.get(x);
                if (tile.equals("46")) continue;
                if (line.get(x - 1).equals("46")) continue;
                if (x < line.size() - 1 && line.get(x + 1).equals("46")) continue;
                if (lines.get(y - 1).get(x).equals("46")) continue;
                if (y < lines.size() - 1 && lines.get(y + 1).get(x).equals("46")) continue;

                result.add(tuple(x, y));
            }

        }
        return result;
    }

    private List<List<String>> findLines(List<String> results) {
        ArrayList<List<String>> result = new ArrayList<>();
        List<String> line = new ArrayList<>();
        for (String tile : results) {
            if (tile.equals("10")) {
                if (line.isEmpty()) continue;
                result.add(line);
                line = new ArrayList<>();
                continue;
            }
            line.add(tile);
        }
        return result;
    }
}
