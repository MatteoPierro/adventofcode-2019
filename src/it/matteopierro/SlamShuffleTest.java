package it.matteopierro;

import com.google.common.collect.Lists;
import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SlamShuffleTest {

    @Test
    void shouldReverseTheDeck() {
        List<Integer> deck = Seq.range(0, 10007).toList();
        var shuffle = new NewStackShuffle();
        assertThat(shuffle.shuffle(deck).get(0)).isEqualTo(10006);
        assertThat(shuffle.positionOf(10004, deck.size())).isEqualTo(2);
        assertThat(shuffle.positionOf(10005, deck.size())).isEqualTo(1);
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
        assertThat(shuffle.positionOf(10004, deck.size())).isEqualTo(find(newDeck, 10004));
        assertThat(shuffle.positionOf(10006, deck.size())).isEqualTo(find(newDeck, 10006));
        assertThat(shuffle.positionOf(0, deck.size())).isEqualTo(find(newDeck, 0));
        assertThat(shuffle.positionOf(1, deck.size())).isEqualTo(find(newDeck, 1));
        assertThat(shuffle.positionOf(2, deck.size())).isEqualTo(find(newDeck, 2));
        assertThat(shuffle.positionOf(3, deck.size())).isEqualTo(find(newDeck, 3));
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
        assertThat(shuffle.positionOf(10003, deck.size())).isEqualTo(find(newDeck, 10003));
        assertThat(shuffle.positionOf(0, deck.size())).isEqualTo(4);
        assertThat(shuffle.positionOf(10002, deck.size())).isEqualTo(10006);
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
        assertThat(shuffle.positionOf(0, deck.size())).isEqualTo(0);
        assertThat(shuffle.positionOf(7, deck.size())).isEqualTo(1);
        assertThat(shuffle.positionOf(4, deck.size())).isEqualTo(2);
        assertThat(shuffle.positionOf(1, deck.size())).isEqualTo(3);
        assertThat(shuffle.positionOf(8, deck.size())).isEqualTo(4);
        assertThat(shuffle.positionOf(5, deck.size())).isEqualTo(5);
        assertThat(shuffle.positionOf(2, deck.size())).isEqualTo(6);
        assertThat(shuffle.positionOf(9, deck.size())).isEqualTo(7);
        assertThat(shuffle.positionOf(6, deck.size())).isEqualTo(8);
        assertThat(shuffle.positionOf(3, deck.size())).isEqualTo(9);
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
        assertThat(shuffle.positionOf(2019, 10007)).isEqualTo(6526);
    }

    @Test
    void secondPuzzle() throws IOException {
        String input = Files.readString(Paths.get("./input_day22"));
        assertThat(solveSecond(2020, 119315717514047L, 101741582076661L, input.split("\n")))
                .isEqualTo(79855812422607L);
    }

    public long solveSecond(long position, long deckSize, long times, String[] instructions) {
        var a = BigInteger.ONE;
        var b = BigInteger.ZERO;

        final var m = BigInteger.valueOf(deckSize);

        for (String instruction : instructions) {
            if (instruction.equals("deal into new stack")) {
                // x → -x - 1; ax + b → -ax - b - 1
                a = a.negate().mod(m);
                b = b.not().mod(m); // b.not() = -b - 1
            }
            if (instruction.contains("cut")) {
                // x → x - i; ax + b → ax + b - i
                String[] tokens = instruction.split(" ");
                int i = Integer.parseInt(tokens[tokens.length - 1]);
                b = b.subtract(BigInteger.valueOf(i)).mod(m);
            }

            if (instruction.contains("increment")) {
                // x → x · i; ax + b → aix + bi
                String[] tokens = instruction.split(" ");
                int increment = Integer.parseInt(tokens[tokens.length - 1]);
                a = a.multiply(BigInteger.valueOf(increment)).mod(m);
                b = b.multiply(BigInteger.valueOf(increment)).mod(m);
            }
        }

        // invert basis function. f^-1(x) = (a^-1)(x - b)
        a = a.modInverse(m);
        b = b.negate().multiply(a).mod(m);

        // start exponentiation for function, f^k(x) = cx + d
        var c = BigInteger.ONE;
        var d = BigInteger.ZERO;
        var e = times;

        // exponentiation by squaring. Equivalent to computing
        // ⌈ a 0 ⌉ k
        // ⌊ b 1 ⌋
        while (e > 0) {
            if ((e & 1) == 1L) {
                // a(cx + d) + b = acx + (ad + b)
                c = a.multiply(c).mod(m);
                d = ((a.multiply(d)).add(b)).mod(m);
            }
            e = e >> 1;
            b = a.multiply(b).add(b).mod(m);
            a = a.multiply(a).mod(m);
        }

        return BigInteger.valueOf(position).multiply(c).add(d).mod(m).longValue();
    }

    private int find(List<Integer> result, int number) {
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).equals(number)) {
                return i;
            }
        }

        throw new RuntimeException("Number " + number + " not found!");
    }

    interface Shuffle {
        List<Integer> shuffle(List<Integer> deck);

        long positionOf(long position, long deckSize);
    }

    class NewStackShuffle implements Shuffle {

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            return Lists.reverse(deck);
        }

        @Override
        public long positionOf(long position, long deckSize) {
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

        @Override
        public long positionOf(long position, long deckSize) {
            if (size >= 0) {
                long newPosition = position - size;
                return newPosition >= 0 ? newPosition : deckSize + newPosition;
            } else {
                return (position + Math.abs(size)) % deckSize;
            }
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

        @Override
        public long positionOf(long position, long size) {
            return position * increment % size;
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

        @Override
        public long positionOf(long position, long deckSize) {
            long result = position;

            for (Shuffle shuffle : shuffles) {
                result = shuffle.positionOf(result, deckSize);
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
