package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.junit.jupiter.api.Test;
import org.jooq.lambda.tuple.Tuple2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class CarePackageTest {

    @Test
    void firstPuzzle() throws IOException {
        ArcadeCabinet arcadeCabinet = new ArcadeCabinet();
        String program = Files.readString(Paths.get("./input_day13"));
        new Computer().execute(program, arcadeCabinet);

        assertThat(arcadeCabinet.numberOfBlocks).isEqualTo(420);
    }

    @Test
    void secondPuzzle() throws IOException {
        ArcadeCabinet arcadeCabinet = new ArcadeCabinet();
        String[] program = Files.readString(Paths.get("./input_day13")).split(",");
        program[0] = "2";
        new Computer().execute(program, arcadeCabinet);

        assertThat(arcadeCabinet.numberOfBlocks).isEqualTo(420);
        assertThat(arcadeCabinet.score).isEqualTo(420);
    }

    private class ArcadeCabinet extends ComputerListener {

        public static final String BLOCK_TILE = "2";
        public static final String PADDLE_TILE = "3";
        public static final String BALL_POSITION = "4";
        private int instructionIndex = 0;
        private int numberOfBlocks;
        private int currentX;
        private int currentY;
        private Tuple2<Integer, Integer> paddlePosition;
        private Tuple2<Integer, Integer> ballPosition;
        private String score;

        @Override
        public String onReadRequested() {
            if (ballPosition.v1 < paddlePosition.v1) {
                return "-1";
            }
            if (ballPosition.v1 > paddlePosition.v1) {
                return "1";
            }
            return "0";
        }

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            if (instructionIndex == 2) {
                if (BLOCK_TILE.equals(result)) {
                    numberOfBlocks++;
                }
                if (PADDLE_TILE.equals(result)) {
                    paddlePosition = tuple(currentX, currentY);
                }
                if (BALL_POSITION.equals(result)) {
                    ballPosition = tuple(currentX, currentY);
                }
                if (currentX == -1 && currentY == 0) {
                    score = result;
                }
                instructionIndex = 0;
                return;
            }
            if (instructionIndex == 0) {
                currentX = Integer.parseInt(result);
            }

            if (instructionIndex == 1) {
                currentY = Integer.parseInt(result);
            }
            instructionIndex++;
        }
    }
}
