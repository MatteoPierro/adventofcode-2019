package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class UniversalOrbitMapTest {

    @Test
    void findDirectAndIndirectOrbitsForAPlanet() {
        Universe universe = new Universe(asList("COM)B", "B)C"));

        assertThat(universe.orbitsFor("C"))
                .containsExactlyInAnyOrder(tuple("COM", "B"), tuple("B","C"));
    }

    @Test
    void findTotalOrbits() {
        String[] orbits = {"COM)B", "B)C", "C)D", "D)E", "E)F", "B)G", "G)H", "D)I", "E)J", "J)K", "K)L"};
        Universe universe = new Universe(asList(orbits));

        assertThat(universe.orbitsFor("D")).hasSize(3);
        assertThat(universe.orbitsFor("L")).hasSize(7);
        assertThat(universe.totalOrbits()).hasSize(42);
    }

    @Test
    void firstPuzzle() throws IOException {
        List<String> orbits = Files.readAllLines(Paths.get("./input_day6"));
        Universe universe = new Universe(orbits);

        assertThat(universe.totalOrbits()).hasSize(402879);
    }

    @Test
    void findAllPaths() {
        Universe universe = new Universe(asList("COM)B", "B)C", "C)D", "D)E", "E)F", "B)G", "G)H", "D)I", "E)J", "J)K", "K)L", "K)YOU", "I)SAN"));

        assertThat(universe.orbitsFor("YOU")).containsExactlyInAnyOrder(
                tuple("K", "YOU"),
                tuple("J", "K"),
                tuple("E", "J"),
                tuple("D", "E"),
                tuple("C", "D"),
                tuple("B", "C"),
                tuple("COM", "B")
        );

        assertThat(universe.orbitsFor("SAN")).containsExactlyInAnyOrder(
                tuple("I", "SAN"),
                tuple("D", "I"),
                tuple("C", "D"),
                tuple("B", "C"),
                tuple("COM", "B")
        );

        assertThat(universe.commonOrbitFor("YOU", "SAN")).isEqualTo("D");
        assertThat(universe.orbitOrderBetween("D", "YOU")).isEqualTo(3);
        assertThat(universe.orbitOrderBetween("D", "SAN")).isEqualTo(1);
        assertThat(universe.orbitalTransfersBetween("YOU", "SAN")).isEqualTo(4);
    }

    @Test
    void secondPuzzle() throws IOException {
        List<String> orbits = Files.readAllLines(Paths.get("./input_day6"));
        Universe universe = new Universe(orbits);

        assertThat(universe.orbitalTransfersBetween("YOU", "SAN")).isEqualTo(484);
    }

    public static class Universe {
        private final Map<String, String> reverseOrbits;

        Universe(List<String> orbits) {
            this.reverseOrbits = new HashMap<>();
            for (String relation : orbits) {
                String[] planets = relation.split("\\)");
                String centerOfMass = planets[0];
                String planet = planets[1];
                this.reverseOrbits.put(planet, centerOfMass);
            }
        }

        List<Tuple2<String, String>> orbitsFor(String planet) {
            ArrayList<Tuple2<String, String>> foundOrbits = new ArrayList<>();
            String currentPlanet = planet;
            while (reverseOrbits.containsKey(currentPlanet)) {
                foundOrbits.add(tuple(reverseOrbits.get(currentPlanet), currentPlanet));
                currentPlanet = reverseOrbits.get(currentPlanet);
            }
            return foundOrbits;
        }

        List<Tuple2<String, String>> totalOrbits() {
            ArrayList<Tuple2<String, String>> totalOrbits = new ArrayList<>();
            for (String planet : reverseOrbits.keySet()) {
                totalOrbits.addAll(orbitsFor(planet));
            }
            return totalOrbits;
        }

        String commonOrbitFor(String planetA, String planetB) {
            List<Tuple2<String, String>> aPaths = orbitsFor(planetA);
            List<Tuple2<String, String>> bPaths = orbitsFor(planetB);
            return aPaths.stream()
                    .filter(bPaths::contains)
                    .findFirst()
                    .orElseThrow()
                    .v2;
        }

        int orbitOrderBetween(String centerOfMass, String planet) {
            return (int) orbitsFor(planet).stream()
                    .takeWhile(orbit -> !orbit.v1.equals(centerOfMass))
                    .count();
        }

        int orbitalTransfersBetween(String planetA, String planetB) {
            String commonCenterOfMass = commonOrbitFor(planetA, planetB);
            return orbitOrderBetween(commonCenterOfMass, planetA) + orbitOrderBetween(commonCenterOfMass, planetB);
        }
    }
}
