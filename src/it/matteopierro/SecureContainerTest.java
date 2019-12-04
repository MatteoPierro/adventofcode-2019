package it.matteopierro;

import org.jooq.lambda.Seq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.jooq.lambda.tuple.Tuple.tuple;

class SecureContainerTest {

    @ParameterizedTest
    @CsvSource( {
            "111111",
            "122345",
            "112233",
            "111122"
    })
    void validPasswordFirstPuzzle(int validPassword) {
        assertThat(isValidPasswordFirstPuzzle(validPassword)).isTrue();
        assertThat(isValidPasswordFirstPuzzle(223450)).isFalse();
        assertThat(isValidPasswordFirstPuzzle(123789)).isFalse();
    }

    @ParameterizedTest
    @CsvSource( {
            "223450",
            "123789"
    })
    void invalidPasswordFirstPuzzle(int invalidPassword) {
        assertThat(isValidPasswordFirstPuzzle(invalidPassword)).isFalse();
    }

    @ParameterizedTest
    @CsvSource( {
            "122345",
            "112233",
            "111122"
    })
    void validPasswordSecondPuzzle(int validPassword) {
        assertThat(isValidPasswordSecondPuzzle(validPassword)).isTrue();
        assertThat(isValidPasswordSecondPuzzle(111111)).isFalse();
        assertThat(isValidPasswordSecondPuzzle(223450)).isFalse();
        assertThat(isValidPasswordSecondPuzzle(123789)).isFalse();
        assertThat(isValidPasswordSecondPuzzle(123444)).isFalse();
        assertThat(isValidPasswordSecondPuzzle(444123)).isFalse();
        assertThat(isValidPasswordSecondPuzzle(124443)).isFalse();
    }

    @ParameterizedTest
    @CsvSource( {
            "111111",
            "223450",
            "123789",
            "123444",
            "444123",
            "124443"
    })
    void invalidPasswordSecondPuzzle(int invalidPassword) {
        assertThat(isValidPasswordSecondPuzzle(invalidPassword)).isFalse();
    }

    @Test
    void groups() {
        assertThat(
                groups(112233)
        ).containsExactly(
                List.of(1, 1),
                List.of(2, 2),
                List.of(3, 3)
        );

        assertThat(
                groups(123444)
        ).containsExactlyInAnyOrder(
                List.of(1),
                List.of(2),
                List.of(3),
                List.of(4, 4, 4)
        );

        assertThat(
                groups(444123)
        ).containsExactlyInAnyOrder(
                List.of(1),
                List.of(2),
                List.of(3),
                List.of(4, 4, 4)
        );

        assertThat(
                groups(124443)
        ).containsExactlyInAnyOrder(
                List.of(1),
                List.of(2),
                List.of(3),
                List.of(4, 4, 4)
        );
    }

    @Test
    void firstPuzzle() {
        long validPasswords = IntStream.range(236491, 713787 + 1)
                .filter(this::isValidPasswordFirstPuzzle)
                .count();

        assertThat(validPasswords).isEqualTo(1169);
    }

    @Test
    void secondPuzzle() {
        long validPasswords = IntStream.range(236491, 713787 + 1)
                .filter(this::isValidPasswordSecondPuzzle)
                .count();

        assertThat(validPasswords).isEqualTo(757);
    }

    private boolean isValidPasswordFirstPuzzle(int password) {
        return isNeverDecreasing(password) && hasDoubles(password);
    }

    private boolean isValidPasswordSecondPuzzle(int password) {
        return isNeverDecreasing(password) && hasDoublesNeverPartOfLargeGroups(password);
    }

    private boolean isNeverDecreasing(int password) {
        List<Integer> digits = passwordToDigits(password);
        return Seq.range(0, 5)
                .zip(Seq.range(1, 6))
                .map(t -> tuple(digits.get(t.v1), digits.get(t.v2)))
                .allMatch(t1 -> t1.v2 >= t1.v1);
    }

    private boolean hasDoubles(int password) {
        return hasDoublesWithCondition(password, group -> group.size() > 1);
    }

    private boolean hasDoublesNeverPartOfLargeGroups(int password) {
        return hasDoublesWithCondition(password, group -> group.size() == 2);
    }

    private boolean hasDoublesWithCondition(int password, Predicate<List<Integer>> condition) {
        return groups(password).stream().anyMatch(condition);
    }

    private List<List<Integer>> groups(int password) {
        return groups(passwordToDigits(password), new ArrayList<>());
    }

    private List<List<Integer>> groups(List<Integer> remainingDigits, List<List<Integer>> groups) {
        if (remainingDigits.isEmpty()) return groups;

        Integer digit = remainingDigits.get(0);
        int groupLength = (int) remainingDigits.stream().takeWhile(digit::equals).count();
        List<Integer> newGroup = remainingDigits.subList(0, groupLength);
        groups.add(newGroup);
        List<Integer> newRemainingDigits = remainingDigits.subList(newGroup.size(), remainingDigits.size());
        return groups(newRemainingDigits, groups);
    }

    private List<Integer> passwordToDigits(int password) {
        return Stream.of(String.valueOf(password).split(""))
                .map(Integer::parseInt)
                .collect(toList());
    }
}
