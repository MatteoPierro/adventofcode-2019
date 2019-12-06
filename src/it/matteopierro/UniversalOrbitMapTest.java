package it.matteopierro;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static org.assertj.core.api.Assertions.assertThat;

class UniversalOrbitMapTest {

    @Test
    void findAnOrbit() {
        Universe universe = new Universe(new String[]{"COM)B", "A)B"});

        assertThat(universe.directOrbits()).hasSize(2);
    }

    @Test
    void findDirectAndIndirectOrbitsForAPlanet() {
        Universe universe = new Universe(new String[]{"COM)B", "B)C"});

        assertThat(universe.orbitsFor("C"))
                .containsExactlyInAnyOrder("COM)B", "B)C");
    }

    public static class Universe {
        private final Map<String, String> orbits;
        private final Map<String, String> reverseOrbits;


        Universe(String[] orbits) {
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
                foundOrbits.add(reverseOrbits.get(currentPlanet) + ")" + currentPlanet);
                currentPlanet = reverseOrbits.get(currentPlanet);
            }
            return foundOrbits;
        }
    }
}
