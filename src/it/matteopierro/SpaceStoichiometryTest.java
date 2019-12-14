package it.matteopierro;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class SpaceStoichiometryTest {

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

        assertThat(orePerReactions(reactions)).isEqualTo(31);
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

        assertThat(orePerReactions(reactions)).isEqualTo(165);
    }

    private long orePerReactions(List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions) {
        List<Tuple2<Integer, String>> elementNeededForFuel = findReactionFor("FUEL", reactions).v1;
        ArrayDeque<Tuple2<Integer, String>> elementsToCreate = new ArrayDeque<>();
        Map<String, Integer> elementsLeft = new HashMap<>();
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

        assertThat(of(findReactionFor("FUEL", reactions))).containsExactly(
                tuple(of(tuple(7, "A"), tuple(1, "B")), tuple(1, "FUEL"))
        );
    }

    private Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>> findReactionFor(String element, List<Tuple2<List<Tuple2<Integer, String>>, Tuple2<Integer, String>>> reactions) {
        return reactions.stream()
                .filter(t-> t.v2.v2.equals(element))
                .findFirst()
                .orElseThrow();
    }
}
