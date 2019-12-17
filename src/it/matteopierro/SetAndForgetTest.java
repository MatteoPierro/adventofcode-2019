package it.matteopierro;

import it.matteopierro.computer.Computer;
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
