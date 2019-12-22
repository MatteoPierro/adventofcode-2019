package it.matteopierro;

import com.google.common.collect.Lists;
import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class SlamShuffleTest {

    @Test
    void shouldReverseTheDeck() {
        List<Integer> deck = Seq.range(0, 10007).toList();
        var shuffle = new NewStackShuffle();
        assertThat(shuffle.shuffle(deck).get(0)).isEqualTo(10006);
        assertThat(shuffle.positionsOf(10004, deck.size())).isEqualTo(2);
        assertThat(shuffle.positionsOf(10005, deck.size())).isEqualTo(1);
    }

    @Test
    void shouldCutTheDeck() {
        List<Integer> deck = Seq.range(0, 10007).toList();
        var shuffle = new CutShuffle(3);
        List<Integer> newDeck = shuffle.shuffle(deck);
        assertThat(newDeck.get(0)).isEqualTo(3);
        assertThat(newDeck.get(newDeck.size() - 4)).isEqualTo(10006);
        assertThat(newDeck.get(newDeck.size() - 3)).isEqualTo(0);
        assertThat(newDeck.get(newDeck.size() - 2)).isEqualTo(1);
        assertThat(newDeck.get(newDeck.size() - 1)).isEqualTo(2);
        assertThat(newDeck.size()).isEqualTo(10007);
    }

    @Test
    void shouldNegativelyCutTheDeck() {
        List<Integer> deck = Seq.range(0, 10007).toList();
        var shuffle = new CutShuffle(-4);
        List<Integer> newDeck = shuffle.shuffle(deck);
        assertThat(newDeck.get(0)).isEqualTo(10003);
        assertThat(newDeck.get(1)).isEqualTo(10004);
        assertThat(newDeck.get(2)).isEqualTo(10005);
        assertThat(newDeck.get(3)).isEqualTo(10006);
        assertThat(newDeck.get(4)).isEqualTo(0);
        assertThat(newDeck.get(newDeck.size() - 2)).isEqualTo(10001);
        assertThat(newDeck.get(newDeck.size() - 1)).isEqualTo(10002);
        assertThat(newDeck.size()).isEqualTo(10007);
    }

    @Test
    void shouldNegativelyCutTheDeck2() {
        List<Integer> deck = Seq.range(0, 10).toList();
        var shuffle = new CutShuffle(-4);
        List<Integer> newDeck = shuffle.shuffle(deck);
        assertThat(newDeck).containsExactly(6, 7, 8, 9, 0, 1, 2, 3, 4, 5);
    }

    @Test
    void shouldIncrementCutTheDeck() {
        List<Integer> deck = Seq.range(0, 10).toList();
        var shuffle = new IncrementShuffle(3);
        List<Integer> newDeck = shuffle.shuffle(deck);
        assertThat(newDeck).containsExactly(0, 7, 4, 1, 8, 5, 2, 9, 6, 3);
    }

    @Test
    void shouldComposeShuffle() {
        var composeShuffle = new CompositeShuffle();
        composeShuffle.add(new IncrementShuffle(7));
        composeShuffle.add(new NewStackShuffle());
        composeShuffle.add(new NewStackShuffle());

        List<Integer> deck = Seq.range(0, 10).toList();

        assertThat(composeShuffle.shuffle(deck)).containsExactly(0, 3, 6, 9, 2, 5, 8, 1, 4, 7);
    }

    @Test
    void shouldComposeShuffle2() {
        var composeShuffle = new CompositeShuffle();
        composeShuffle.add(new CutShuffle(6));
        composeShuffle.add(new IncrementShuffle(7));
        composeShuffle.add(new NewStackShuffle());

        List<Integer> deck = Seq.range(0, 10).toList();

        assertThat(composeShuffle.shuffle(deck)).containsExactly(3, 0, 7, 4, 1, 8, 5, 2, 9, 6);
    }

    @Test
    void shouldComposeShuffle3() {
        var composeShuffle = new CompositeShuffle();
        composeShuffle.add(new IncrementShuffle(7));
        composeShuffle.add(new IncrementShuffle(9));
        composeShuffle.add(new CutShuffle(-2));

        List<Integer> deck = Seq.range(0, 10).toList();

        assertThat(composeShuffle.shuffle(deck)).containsExactly(6, 3, 0, 7, 4, 1, 8, 5, 2, 9);
    }

    @Test
    void shouldParseShuffles() {
        var shuffle = new ShuffleParser().parse(
                "deal into new stack\n" +
                        "cut -2\n" +
                        "deal with increment 7\n" +
                        "cut 8\n" +
                        "cut -4\n" +
                        "deal with increment 7\n" +
                        "cut 3\n" +
                        "deal with increment 9\n" +
                        "deal with increment 3\n" +
                        "cut -1");

        List<Integer> deck = Seq.range(0, 10).toList();

        assertThat(shuffle.shuffle(deck)).containsExactly(9, 2, 5, 8, 1, 4, 7, 0, 3, 6);
    }

    @Test
    void shouldReturnTheOriginalOrderWithDoubleShuffle() {
        var composeShuffle = new CompositeShuffle();
        composeShuffle.add(new NewStackShuffle());
        composeShuffle.add(new NewStackShuffle());

        List<Integer> deck = Seq.range(0, 10).toList();

        assertThat(composeShuffle.shuffle(deck)).isEqualTo(deck);
    }

    @Test
    void firstPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day22"));
        Shuffle shuffle = new ShuffleParser().parse(input);
        List<Integer> deck = Seq.range(0, 10007).toList();
        List<Integer> result = shuffle.shuffle(deck);
        assertThat(find(result, 2019)).isEqualTo(6526);
    }

    @Test
    @Disabled
    void secondPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day22"));
        Shuffle shuffle = new ShuffleParser().parse(input);
    }

    private int find(List<Integer> result, int number) {
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).equals(2019)) {
                return i;
            }
        }

        throw new RuntimeException("Number " + number + " not found!");
    }

    interface Shuffle {
        List<Integer> shuffle(List<Integer> deck);
    }

    class NewStackShuffle implements Shuffle {

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            return Lists.reverse(deck);
        }

        public int positionsOf(int position, int deckSize) {
            return (deckSize - 1) - position;
        }
    }

    private class CutShuffle implements Shuffle {
        private final int size;

        public CutShuffle(int size) {
            this.size = size;
        }

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            int size = this.size >= 0 ? this.size : (deck.size() + this.size);
            return shuffle(deck, size);
        }

        public List<Integer> shuffle(List<Integer> deck, int size) {
            ArrayList<Integer> result = new ArrayList<>(deck.subList(size, deck.size()));
            result.addAll(deck.subList(0, size));
            return result;
        }
    }

    private class IncrementShuffle implements Shuffle {
        private final int increment;

        public IncrementShuffle(int increment) {
            this.increment = increment;
        }

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            ArrayList<Integer> newDeck = new ArrayList<>(Collections.nCopies(deck.size(), -1));

            var i = 0;
            for (Integer card : deck) {
                newDeck.set(i % deck.size(), card);
                i += increment;
            }

            return newDeck;
        }
    }

    private class CompositeShuffle implements Shuffle {
        private final List<Shuffle> shuffles = new ArrayList<>();

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            List<Integer> result = new ArrayList<>(deck);

            for (Shuffle shuffle : shuffles) {
                result = shuffle.shuffle(result);
            }

            return result;
        }

        public void add(Shuffle shuffle) {
            shuffles.add(shuffle);
        }
    }

    private class ShuffleParser {
        public Shuffle parse(String shuffles) {
            var result = new CompositeShuffle();

            for (String shuffle : shuffles.split("\n")) {
                if (shuffle.contains("new stack")) {
                    result.add(new NewStackShuffle());
                }
                if (shuffle.contains("cut")) {
                    String[] tokens = shuffle.split(" ");
                    int cut = Integer.parseInt(tokens[tokens.length - 1]);
                    result.add(new CutShuffle(cut));
                }
                if (shuffle.contains("increment")) {
                    String[] tokens = shuffle.split(" ");
                    int increment = Integer.parseInt(tokens[tokens.length - 1]);
                    result.add(new IncrementShuffle(increment));
                }
            }

            return result;
        }
    }
}
