package it.matteopierro;

import com.google.common.collect.Lists;
import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SlamShuffleTest {

    @Test
    void shouldReverseTheDeck() {
        List<Integer> deck = Seq.range(0, 10007).toList();
        var shuffle = new ReverseShuffle();
        assertThat(shuffle.shuffle(deck).get(0)).isEqualTo(10006);
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
    void shouldIncrementCutTheDeck() {
        List<Integer> deck = Seq.range(0, 10).toList();
        var shuffle = new IncrementShuffle(3);
        List<Integer> newDeck = shuffle.shuffle(deck);
        assertThat(newDeck).containsExactly(0,7,4,1,8,5,2,9,6,3);
    }

    interface Shuffle {
        List<Integer> shuffle(List<Integer> deck);
    }

    class ReverseShuffle implements Shuffle {

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            return Lists.reverse(deck);
        }
    }

    private class CutShuffle implements Shuffle {
        private final int size;

        public CutShuffle(int size) {
            this.size = size;
        }

        @Override
        public List<Integer> shuffle(List<Integer> deck) {
            int size = this.size >= 0 ? this.size : (deck.size() + this.size) ;
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

            var inc = increment;
            var i = 0;
            var offset = 0;
            for (Integer card : deck) {
                if ((inc * i) + offset >= deck.size()) {
                    i = 0;
                    if (offset == 0) {
                        offset = increment;
                    }
                    offset--;
                }
                newDeck.set((inc * i) + offset, card);
                i++;
            }

            return newDeck;
        }
    }
}
