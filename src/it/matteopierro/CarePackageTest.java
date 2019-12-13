package it.matteopierro;

import it.matteopierro.computer.Computer;
import it.matteopierro.computer.ComputerListener;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class CarePackageTest {

    @Test
    void firstPuzzle() throws IOException {
        ArcadeCabinet arcadeCabinet = new ArcadeCabinet();
        String program = Files.readString(Paths.get("./input_day13"));
        new Computer().execute(program, arcadeCabinet);

        assertThat(arcadeCabinet.numberOfBlocks).isEqualTo(420);
    }

    private class ArcadeCabinet extends ComputerListener {

        private int instructionIndex = 0;
        private int numberOfBlocks;

        @Override
        public void onStoreRequested(String result) {
            super.onStoreRequested(result);
            if (instructionIndex == 2) {
                if ("2".equals(result)) {
                    numberOfBlocks++;
                }
                instructionIndex = 0;
                return;
            }
            instructionIndex++;
        }
    }
}
