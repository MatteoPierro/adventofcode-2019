package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class SpaceStoichiometryTest {

    private final Stoichiometry stoichiometry = new Stoichiometry();

    //10 ORE => 10 A
    //1 ORE => 1 B
    //7 A, 1 B => 1 C
    //7 A, 1 C => 1 D
    //7 A, 1 D => 1 E
    //7 A, 1 E => 1 FUEL
    @Test
    void numberOfOre() {
        List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions = of(
                tuple(of(tuple(10, "ORE")), tuple(10, "A")),
                tuple(of(tuple(1, "ORE")), tuple(1, "B")),
                tuple(of(tuple(7, "A"), tuple(1, "B")), tuple(1, "C")),
                tuple(of(tuple(7, "A"), tuple(1, "C")), tuple(1, "D")),
                tuple(of(tuple(7, "A"), tuple(1, "D")), tuple(1, "E")),
                tuple(of(tuple(7, "A"), tuple(1, "E")), tuple(1, "FUEL"))
        );

        assertThat(stoichiometry.orePerFuel(reactions)).isEqualTo(31);
    }


    //9 ORE => 2 A
    //8 ORE => 3 B
    //7 ORE => 5 C
    //3 A, 4 B => 1 AB
    //5 B, 7 C => 1 BC
    //4 C, 1 A => 1 CA
    //2 AB, 3 BC, 4 CA => 1 FUEL
    @Test
    void numberOfOreSecondExample() {
        List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions = of(
                tuple(of(tuple(9, "ORE")), tuple(2, "A")),
                tuple(of(tuple(8, "ORE")), tuple(3, "B")),
                tuple(of(tuple(7, "ORE")), tuple(5, "C")),
                tuple(of(tuple(3, "A"), tuple(4, "B")), tuple(1, "AB")),
                tuple(of(tuple(5, "B"), tuple(7, "C")), tuple(1, "BC")),
                tuple(of(tuple(4, "C"), tuple(1, "A")), tuple(1, "CA")),
                tuple(of(tuple(2, "AB"), tuple(3, "BC"), tuple(4, "CA")), tuple(1, "FUEL"))
        );

        assertThat(stoichiometry.orePerFuel(reactions)).isEqualTo(165);
    }

    @Test
    void numberOfOreThirdExample() {
        String rawReactions = "157 ORE => 5 NZVS\n" +
                "165 ORE => 6 DCFZ\n" +
                "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL\n" +
                "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ\n" +
                "179 ORE => 7 PSHF\n" +
                "177 ORE => 5 HKGWZ\n" +
                "7 DCFZ, 7 PSHF => 2 XJWVT\n" +
                "165 ORE => 2 GPVTF\n" +
                "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT";

        assertThat(stoichiometry.orePerFuel(parseReactions(rawReactions))).isEqualTo(13312);
    }

    @Test
    void numberOfOreForthExample() {
        String rawReactions = "2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG\n" +
                "17 NVRVD, 3 JNWZP => 8 VPVL\n" +
                "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL\n" +
                "22 VJHF, 37 MNCFX => 5 FWMGM\n" +
                "139 ORE => 4 NVRVD\n" +
                "144 ORE => 7 JNWZP\n" +
                "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC\n" +
                "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV\n" +
                "145 ORE => 6 MNCFX\n" +
                "1 NVRVD => 8 CXFTF\n" +
                "1 VJHF, 6 MNCFX => 4 RFSQX\n" +
                "176 ORE => 6 VJHF";

        assertThat(stoichiometry.orePerFuel(parseReactions(rawReactions))).isEqualTo(180697);
    }

    @Test
    void numberOfOreFifthExample() {
        String rawReactions = "171 ORE => 8 CNZTR\n" +
                "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL\n" +
                "114 ORE => 4 BHXH\n" +
                "14 VRPVC => 6 BMBT\n" +
                "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL\n" +
                "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT\n" +
                "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW\n" +
                "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW\n" +
                "5 BMBT => 4 WPTQ\n" +
                "189 ORE => 9 KTJDG\n" +
                "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP\n" +
                "12 VRPVC, 27 CNZTR => 2 XDBXC\n" +
                "15 KTJDG, 12 BHXH => 5 XCVML\n" +
                "3 BHXH, 2 VRPVC => 7 MZWV\n" +
                "121 ORE => 7 VRPVC\n" +
                "7 XCVML => 6 RJRHP\n" +
                "5 BHXH, 4 VRPVC => 5 LTCX";

        assertThat(stoichiometry.orePerFuel(parseReactions(rawReactions))).isEqualTo(2210736);
    }

    @Test
    void firstPuzzle() throws IOException {
        String rawReactions = Files.readString(Paths.get("./input_day14"));

        assertThat(stoichiometry.orePerFuel(parseReactions(rawReactions))).isEqualTo(431448);
    }

    @Test
    void findReactionToGenerateOneElement(){
        List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions = of(
                tuple(of(tuple(10, "ORE")), tuple(10, "A")),
                tuple(of(tuple(1, "ORE")), tuple(1, "B")),
                tuple(of(tuple(7, "A"), tuple(1, "B")), tuple(1, "C")),
                tuple(of(tuple(7, "A"), tuple(1, "C")), tuple(1, "D")),
                tuple(of(tuple(7, "A"), tuple(1, "D")), tuple(1, "E")),
                tuple(of(tuple(7, "A"), tuple(1, "B")), tuple(1, "FUEL"))
        );

        assertThat(of(stoichiometry.findReactionFor("FUEL", reactions))).containsExactly(
                tuple(of(tuple(7, "A"), tuple(1, "B")), tuple(1, "FUEL"))
        );
    }

    @Test
    void parseReaction() {
        assertThat(of(parseReaction("3 A, 4 B => 1 AB"))).containsExactly(
          tuple(of(tuple(3, "A"), tuple(4, "B")), tuple(1, "AB"))
        );
    }

    private List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> parseReactions(String rawReactions) {
        List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions = new ArrayList<>();
        for (String rawReaction : rawReactions.split("\n")) {
               reactions.add(parseReaction(rawReaction));
        }
        return reactions;
    }

    private Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>> parseReaction(String input) {
        String[] reaction = input.split(" => ");
        String[] rawReagents = reaction[0].split(", ");
        List<Tuple2<Integer, String>> reagents = new ArrayList<>();
        for (String rawReagent : rawReagents) {
            reagents.add(toElement(rawReagent));
        }

        return tuple(reagents, toElement(reaction[1]));
    }

    private Tuple2<Integer, String> toElement(String s) {
        String[] rawProduct = s.split(" ");
        return tuple(Integer.parseInt(rawProduct[0]), rawProduct[1]);
    }

    public class Stoichiometry {

        long orePerFuel(List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions) {
            List<Tuple2<Integer, String>> elementNeededForFuel = findReactionFor("FUEL", reactions).v1;
            ArrayDeque<Tuple2<Integer, String>> elementsToCreate = new ArrayDeque<Tuple2<Integer, String>>();
            Map<String, Integer> elementsLeft = new HashMap<String, Integer>();
            elementNeededForFuel.forEach(elementsToCreate::push);
            long numberOfOre = 0;
            while (!elementsToCreate.isEmpty()) {
                Tuple2<Integer, String> element = elementsToCreate.pop();
                if (element.v2.equals("ORE")) {
                    numberOfOre += element.v1;
                    continue;
                }

                int quantityRequired = quantityRequired(elementsLeft, element);
                if (quantityRequired == 0) continue;

                Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>> reaction = findReactionFor(element.v2, reactions);
                List<Tuple2<Integer, String>> newElements = reaction.v1;
                if (reaction.v2.v1 >= quantityRequired) {
                    Integer remaining = reaction.v2.v1 - quantityRequired;
                    elementsLeft.put(reaction.v2.v2, elementsLeft.getOrDefault(reaction.v2.v2, 0) + remaining);
                    newElements.forEach(elementsToCreate::push);
                    continue;
                }

                int quantityProduced = 0;
                while (quantityProduced < quantityRequired) {
                    newElements.forEach(elementsToCreate::push);
                    quantityProduced += reaction.v2.v1;
                }

                if (quantityProduced > quantityRequired) {
                    Integer remaining = quantityProduced - quantityRequired;
                    elementsLeft.put(reaction.v2.v2, elementsLeft.getOrDefault(reaction.v2.v2, 0) + remaining);
                }
            }
            return numberOfOre;
        }

        private Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>> findReactionFor(String element, List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions) {
            return reactions.stream()
                    .filter(t-> t.v2.v2.equals(element))
                    .findFirst()
                    .orElseThrow();
        }

        private int quantityRequired(Map<String, Integer> elementsLeft, Tuple2<Integer, String> element) {
            int quantityRequired = element.v1;
            Integer remaining = elementsLeft.getOrDefault(element.v2, 0);
            if (remaining >= quantityRequired) {
                elementsLeft.put(element.v2, remaining - quantityRequired);
                return 0;
            }
            elementsLeft.remove(element.v2);
            return quantityRequired - remaining;
        }
    }
}
