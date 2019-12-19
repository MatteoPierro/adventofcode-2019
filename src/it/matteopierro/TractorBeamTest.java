package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TractorBeamTest {

    @Test
    void firstPuzzle() throws IOException {
        String program = Files.readString(Paths.get("./input_day19"));

        List<Tuple2<Integer, Integer>> points = points();
        Computer computer = new Computer();
        long affectedPoints = 0;
        for (Tuple2<Integer, Integer> point : points) {
            List<String> results = computer.execute(program, String.valueOf(point.v1), String.valueOf(point.v2));
            String result = results.get(results.size() - 1);
            if (result.equals("1")) {
                affectedPoints++;
            }
        }
        assertThat(affectedPoints).isEqualTo(-1L);
    }

    @Test
    void combination() {
        List<Tuple2<Integer, Integer>> points = points();

        assertThat(points).hasSize(2500);
    }

    private class TractorBeam extends ComputerListener {

        private final List<String> inputs;
        private int currentIndex;
        private long affectedPoints = 0;

        public TractorBeam() {
            inputs = inputs();
            currentIndex = 0;
        }

        @Override
        public String onReadRequested() {
            return inputs.get(currentIndex++);
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            if (currentIndex == inputs.size()) {
                System.out.println("affected Poitns " + affectedPoints);
            }

            if (result.equals("1")) {
                affectedPoints++;
            }
        }
    }

    List<String> inputs() {
        return points()
                .stream()
                .flatMap(t -> Seq.of(String.valueOf(t.v1), String.valueOf(t.v2)))
                .collect(Collectors.toList());
    }

    private List<Tuple2<Integer, Integer>> points() {
        return Seq.range(0, 50)
                .crossJoin(Seq.range(0, 50))
                .toList();
    }
}
