package it.matteopierro;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static org.assertj.core.api.Assertions.assertThat;

class UniversalOrbitMapTest {

    @Test
    void findAnOrbit() {
        Universe universe = new Universe(asList("COM)B", "A)B"));

        assertThat(universe.directOrbits()).hasSize(2);
    }

    @Test
    void findDirectAndIndirectOrbitsForAPlanet() {
        Universe universe = new Universe(asList("COM)B", "B)C"));

        assertThat(universe.orbitsFor("C"))
                .containsExactlyInAnyOrder("COM)C", "B)C");
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

    public static class Universe {
        private final Map<String, String> orbits;
        private final Map<String, String> reverseOrbits;


        Universe(List<String> orbits) {
            this.orbits = new HashMap<>();
            this.reverseOrbits = new HashMap<>();
            for (String relation : orbits) {
                String[] planets = relation.split("\\)");
                String centerOfMass = planets[0];
                String planet = planets[1];
                this.orbits.put(centerOfMass, planet);
                this.reverseOrbits.put(planet, centerOfMass);
            }
        }

        Map<String, String> directOrbits() {
            return unmodifiableMap(orbits);
        }

        List<String> orbitsFor(String planet) {
            ArrayList<String> foundOrbits = new ArrayList<>();
            String currentPlanet = planet;
            while (reverseOrbits.containsKey(currentPlanet)) {
                foundOrbits.add(reverseOrbits.get(currentPlanet) + ")" + planet);
                currentPlanet = reverseOrbits.get(currentPlanet);
            }
            return foundOrbits;
        }

        List<String> totalOrbits() {
            ArrayList<String> totalOrbits = new ArrayList<>();
            for (String planet : reverseOrbits.keySet()) {
                totalOrbits.addAll(orbitsFor(planet));
            }
            return totalOrbits;
        }
    }
}
