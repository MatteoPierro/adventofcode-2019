package it.matteopierro;

import com.google.common.collect.Lists;
import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
            ArrayList<Integer> result = new ArrayList<>(deck.subList(size, deck.size()));
            result.addAll(deck.subList(0, size));
            return result;
        }
    }
}
