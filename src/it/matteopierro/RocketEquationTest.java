package it.matteopierro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.IntUnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

class RocketEquationTest {

    @ParameterizedTest
    @CsvSource({
            "12,2",
            "14,2",
            "1969,654",
            "100756,33583"
    })
    void testFuel(int mass, int expectedFuel) {
        assertThat(fuelFor(mass)).isEqualTo(expectedFuel);
    }

    @ParameterizedTest
    @CsvSource({
            "14,2",
            "1969,966",
            "100756,50346"
    })
    void testTotalFuel(int mass, int expectedFuel) {
        assertThat(totalFuelFor(mass)).isEqualTo(expectedFuel);
    }

    @Test
    void firstPuzzle() throws IOException {
        assertThat(
                fuelForSpacecraft(this::fuelFor)
        ).isEqualTo(3324332);
    }

    @Test
    void secondPuzzle() throws IOException {
        assertThat(
                fuelForSpacecraft(this::totalFuelFor)
        ).isEqualTo(4983626);
    }

    private int fuelForSpacecraft(IntUnaryOperator fuelCalculator) throws IOException {
        return Files.readAllLines(Paths.get("./input_day1"))
                .stream()
                .mapToInt(Integer::parseInt)
                .map(fuelCalculator)
                .sum();
    }

    int totalFuelFor(int mass) {
        int totalFuel = fuelFor(mass);
        return totalFuel + fuelForFuel(totalFuel, 0);
    }

    private int fuelForFuel(int fuel, int fuelForFuel) {
        if (fuel == 0) return fuelForFuel;
        int additionalFuel = fuelFor(fuel);
        return fuelForFuel(additionalFuel, fuelForFuel + additionalFuel);
    }

    int fuelFor(int mass) {
        return Math.max((mass / 3) - 2, 0);
    }
}