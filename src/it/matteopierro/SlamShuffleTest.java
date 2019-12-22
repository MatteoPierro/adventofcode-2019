package it.matteopierro;

import com.google.common.collect.Lists;
import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SlamShuffleTest {

    @Test
    void shouldReverseTheDeck() {
        List<Integer> deck = Seq.range(0, 10007).toList();
        var shuffle = new ReverseShuffle();
        assertThat(shuffle.shuffle(deck).get(0)).isEqualTo(10006);
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
}
