package javaslang.collection;

import javaslang.*;
import javaslang.WrappedString;
import javaslang.control.None;
import javaslang.control.Option;
import javaslang.control.Some;
import org.assertj.core.api.*;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static javaslang.Serializables.deserialize;
import static javaslang.Serializables.serialize;

// TODO java.lang.StringTest, AbstractValueTest
public class StringTest {

    protected <T> IterableAssert<T> assertThat(Iterable<T> actual) {
        return new IterableAssert<T>(actual) {
        };
    }

    protected <T> ObjectAssert<T> assertThat(T actual) {
        return new ObjectAssert<T>(actual) {
        };
    }

    protected BooleanAssert assertThat(Boolean actual) {
        return new BooleanAssert(actual) {
        };
    }

    protected DoubleAssert assertThat(Double actual) {
        return new DoubleAssert(actual) {
        };
    }

    protected IntegerAssert assertThat(Integer actual) {
        return new IntegerAssert(actual) {
        };
    }

    protected LongAssert assertThat(Long actual) {
        return new LongAssert(actual) {
        };
    }

    protected StringAssert assertThat(java.lang.String actual) {
        return new StringAssert(actual) {
        };
    }
    
    private WrappedString empty() {
        return WrappedString.empty();
    }

    // -- exists

    @Test
    public void shouldBeAwareOfExistingElement() {
        assertThat(WrappedString.of('1', '2').exists(i -> i == '2')).isTrue();
    }

    @Test
    public void shouldBeAwareOfNonExistingElement() {
        assertThat(empty().exists(i -> i == 1)).isFalse();
    }

    // -- forAll

    @Test
    public void shouldBeAwareOfPropertyThatHoldsForAll() {
        assertThat(WrappedString.of('2', '4').forAll(i -> i % 2 == 0)).isTrue();
    }

    @Test
    public void shouldBeAwareOfPropertyThatNotHoldsForAll() {
        assertThat(WrappedString.of('2', '3').forAll(i -> i % 2 == 0)).isFalse();
    }

    // -- peek

    @Test
    public void shouldPeekNil() {
        assertThat(empty().peek(t -> {
        })).isEqualTo(empty());
    }

    @Test
    public void shouldPeekNonNilPerformingNoAction() {
        assertThat(WrappedString.of('1').peek(t -> {
        })).isEqualTo(WrappedString.of('1'));
    }

    @Test
    public void shouldPeekSingleValuePerformingAnAction() {
        final char[] effect = { 0 };
        final WrappedString actual = WrappedString.of('1').peek(i -> effect[0] = i);
        assertThat(actual).isEqualTo(WrappedString.of('1'));
        assertThat(effect[0]).isEqualTo('1');
    }

    // -- average

    @Test
    public void shouldReturnNoneWhenComputingAverageOfNil() {
        assertThat(empty().average()).isEqualTo(None.instance());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenComputingAverageOfStrings() {
        WrappedString.of('1', '2', '3').average();
    }

    // -- clear

    @Test

    public void shouldClearNil() {
        assertThat(empty().clear()).isEqualTo(empty());
    }

    @Test
    public void shouldClearNonNil() {
        assertThat(WrappedString.of('1', '2', '3').clear()).isEqualTo(empty());
    }

    // -- contains

    @Test
    public void shouldRecognizeNilContainsNoElement() {
        final boolean actual = empty().contains((Character) null);
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesNotContainElement() {
        final boolean actual = WrappedString.of('1', '2', '3').contains('0');
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesContainElement() {
        final boolean actual = WrappedString.of('1', '2', '3').contains('2');
        assertThat(actual).isTrue();
    }

    // -- containsAll

    @Test
    public void shouldRecognizeNilNotContainsAllElements() {
        final boolean actual = empty().containsAll(WrappedString.of('1', '2', '3'));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilNotContainsAllOverlappingElements() {
        final boolean actual = WrappedString.of('1', '2', '3').containsAll(WrappedString.of('2', '3', '4'));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilContainsAllOnSelf() {
        final boolean actual = WrappedString.of('1', '2', '3').containsAll(WrappedString.of('1', '2', '3'));
        assertThat(actual).isTrue();
    }

    // -- distinct

    @Test
    public void shouldComputeDistinctOfEmptyTraversable() {
        assertThat(empty().distinct()).isEqualTo(empty());
    }

    @Test
    public void shouldComputeDistinctOfNonEmptyTraversable() {
        assertThat(WrappedString.of('1', '1', '2', '2', '3', '3').distinct()).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    // -- distinct(Comparator)

    @Test
    public void shouldComputeDistinctByOfEmptyTraversableUsingComparator() {
        final Comparator<Character> comparator = (i1, i2) -> i1 - i2;
        assertThat(WrappedString.empty().distinctBy(comparator)).isEqualTo(empty());
    }

    @Test
    public void shouldComputeDistinctByOfNonEmptyTraversableUsingComparator() {
        final Comparator<Character> comparator = (s1, s2) -> (s1 - s2);
        assertThat(WrappedString.of('1', '2', '3', '3', '4', '5').distinctBy(comparator)).isEqualTo(WrappedString.of('1', '2', '3', '4', '5'));
    }

    // -- distinct(Function)

    @Test
    public void shouldComputeDistinctByOfEmptyTraversableUsingKeyExtractor() {
        assertThat(empty().distinctBy(Function.identity())).isEqualTo(empty());
    }

    @Test
    public void shouldComputeDistinctByOfNonEmptyTraversableUsingKeyExtractor() {
        assertThat(WrappedString.of('1', '2', '3', '3', '4', '5').distinctBy(c -> c)).isEqualTo(WrappedString.of('1', '2', '3', '4', '5'));
    }

    // -- drop

    @Test
    public void shouldDropNoneOnNil() {
        assertThat(empty().drop(1)).isEqualTo(empty());
    }

    @Test
    public void shouldDropNoneIfCountIsNegative() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        assertThat(t.drop(-1)).isSameAs(t);
    }

    @Test
    public void shouldDropAsExpectedIfCountIsLessThanSize() {
        assertThat(WrappedString.of('1', '2', '3').drop(2)).isEqualTo(WrappedString.of('3'));
    }

    @Test
    public void shouldDropAllIfCountExceedsSize() {
        assertThat(WrappedString.of('1', '2', '3').drop('4')).isEqualTo(empty());
    }

    // -- dropRight

    @Test
    public void shouldDropRightNoneOnNil() {
        assertThat(empty().dropRight(1)).isEqualTo(empty());
    }

    @Test
    public void shouldDropRightNoneIfCountIsNegative() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        assertThat(t.dropRight(-1)).isSameAs(t);
    }

    @Test
    public void shouldDropRightAsExpectedIfCountIsLessThanSize() {
        assertThat(WrappedString.of('1', '2', '3').dropRight(2)).isEqualTo(WrappedString.of('1'));
    }

    @Test
    public void shouldDropRightAllIfCountExceedsSize() {
        assertThat(WrappedString.of('1', '2', '3').dropRight(4)).isEqualTo(empty());
    }

    // -- dropWhile

    @Test
    public void shouldDropWhileNoneOnNil() {
        assertThat(empty().dropWhile(ignored -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldDropWhileNoneIfPredicateIsFalse() {
        WrappedString t = WrappedString.of('1', '2', '3');
        assertThat(t.dropWhile(ignored -> false)).isSameAs(t);
    }

    @Test
    public void shouldDropWhileAllIfPredicateIsTrue() {
        assertThat(WrappedString.of('1', '2', '3').dropWhile(ignored -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldDropWhileCorrect() {
        assertThat(WrappedString.of('1', '2', '3').dropWhile(i -> i == '1')).isEqualTo(WrappedString.of('2', '3'));
    }


    // -- existsUnique

    @Test
    public void shouldBeAwareOfExistingUniqueElement() {
        assertThat(WrappedString.of('1', '2').existsUnique(i -> i == '1')).isTrue();
    }

    @Test
    public void shouldBeAwareOfNonExistingUniqueElement() {
        assertThat(WrappedString.empty().existsUnique(i -> i == '1')).isFalse();
    }

    @Test
    public void shouldBeAwareOfExistingNonUniqueElement() {
        assertThat(WrappedString.of('1', '1', '2').existsUnique(i -> i == '1')).isFalse();
    }

    // -- filter

    @Test
    public void shouldFilterEmptyTraversable() {
        assertThat(empty().filter(ignored -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldFilterNonEmptyTraversable() {
        assertThat(WrappedString.of('1', '2', '3', '4').filter(i -> i == '2' || i == '4')).isEqualTo(WrappedString.of('2', '4'));
    }

    @Test
    public void shouldFilterNonEmptyTraversableAllMatch() {
        final WrappedString t = WrappedString.of('1', '2', '3', '4');
        if(isThisLazyCollection()) {
            assertThat(t.filter(i -> true)).isEqualTo(t);
        } else {
            assertThat(t.filter(i -> true)).isSameAs(t);
        }
    }

    // -- findAll

    @Test
    public void shouldFindAllOfNil() {
        assertThat(empty().findAll(ignored -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldFindAllOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3', '4').findAll(i -> i % 2 == 0)).isEqualTo(WrappedString.of('2', '4'));
    }

    // -- findFirst

    @Test
    public void shouldFindFirstOfNil() {
        assertThat(empty().findFirst(ignored -> true)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindFirstOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3', '4').findFirst(i -> i % 2 == 0)).isEqualTo(Option.of('2'));
    }

    // -- findLast

    @Test
    public void shouldFindLastOfNil() {
        assertThat(empty().findLast(ignored -> true)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindLastOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3', '4').findLast(i -> i % 2 == 0)).isEqualTo(Option.of('4'));
    }

    // -- flatMap

    @Test
    public void shouldFlatMapEmptyTraversable() {
        assertThat(empty().flatMap(WrappedString::of)).isEqualTo(empty());
    }

    @Test
    public void shouldFlatMapNonEmptyTraversable() {
        assertThat(WrappedString.of('1', '2', '3').flatMap(WrappedString::of)).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    @Test
    public void shouldFlatMapTraversableByExpandingElements() {
        assertThat(WrappedString.of('1', '2', '3').flatMap(i -> {
            if (i == '1') {
                return WrappedString.of('1', '2', '3');
            } else if (i == '2') {
                return WrappedString.of('4', '5');
            } else {
                return WrappedString.of('6');
            }
        })).isEqualTo(WrappedString.of('1', '2', '3', '4', '5', '6'));
    }

    @Test
    public void shouldFlatMapElementsToSequentialValuesInTheRightOrder() {
        final AtomicInteger seq = new AtomicInteger('0');
        final Vector<Character> actualInts = WrappedString.of('0', '1', '2')
                .flatMap(ignored -> Vector.of((char) seq.getAndIncrement(), (char) seq.getAndIncrement()));
        final WrappedString expectedInts = WrappedString.of('0', '1', '2', '3', '4', '5');
        assertThat(actualInts).isEqualTo(expectedInts);
    }

    // -- flatten()

    @Test
    public void shouldFlattenEmptyTraversable() {
        assertThat(empty().flatten()).isEqualTo(empty());
    }

    @Test
    public void shouldFlattenTraversableOfPlainElements() {
        assertThat(WrappedString.of('1', '2', '3').flatten()).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    // -- fold

    @Test
    public void shouldFoldNil() {
        assertThat(empty().fold('0', (a, b) -> b)).isEqualTo('0');
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldNullOperator() {
        empty().fold(null, null);
    }

    @Test
    public void shouldFoldNonNil() {
        assertThat(WrappedString.of('1', '2', '3').fold('0', (a, b) -> b)).isEqualTo('3');
    }

    // -- foldLeft

    @Test
    public void shouldFoldLeftNil() {
        assertThat(this.<String> empty().foldLeft("", (xs, x) -> xs + x)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldLeftNullOperator() {
        this.<String> empty().foldLeft(null, null);
    }

    @Test
    public void shouldFoldLeftNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').foldLeft("", (xs, x) -> xs + x)).isEqualTo("abc");
    }

    // -- foldRight

    @Test
    public void shouldFoldRightNil() {
        assertThat(empty().foldRight("", (x, xs) -> x + xs)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldRightNullOperator() {
        this.<String> empty().foldRight(null, null);
    }

    @Test
    public void shouldFoldRightNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').foldRight("", (x, xs) -> x + xs)).isEqualTo("abc");
    }

    // -- head

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenHeadOnNil() {
        empty().head();
    }

    @Test
    public void shouldReturnHeadOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3').head()).isEqualTo('1');
    }

    // -- headOption

    @Test
    public void shouldReturnNoneWhenCallingHeadOptionOnNil() {
        assertThat(empty().headOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeHeadWhenCallingHeadOptionOnNonNil() {
        assertThat(WrappedString.of('1', '2', '3').headOption()).isEqualTo(new Some<>('1'));
    }

    // -- init

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenInitOfNil() {
        empty().init();
    }

    @Test
    public void shouldGetInitOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3').init()).isEqualTo(WrappedString.of('1', '2'));
    }

    // -- initOption

    @Test
    public void shouldReturnNoneWhenCallingInitOptionOnNil() {
        assertThat(empty().initOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeInitWhenCallingInitOptionOnNonNil() {
        assertThat(WrappedString.of('1', '2', '3').initOption()).isEqualTo(new Some<>(WrappedString.of('1', '2')));
    }

    // -- isEmpty

    @Test
    public void shouldRecognizeNil() {
        assertThat(empty().isEmpty()).isTrue();
    }

    @Test
    public void shouldRecognizeNonNil() {
        assertThat(WrappedString.of('1').isEmpty()).isFalse();
    }

    // -- iterator

    @Test
    public void shouldNotHasNextWhenNilIterator() {
        assertThat(empty().iterator().hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowOnNextWhenNilIterator() {
        empty().iterator().next();
    }

    @Test
    public void shouldIterateFirstElementOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3').iterator().next()).isEqualTo('1');
    }

    @Test
    public void shouldFullyIterateNonNil() {
        final Iterator<Character> iterator = WrappedString.of('1', '2', '3').iterator();
        int actual;
        for (int i = 1; i <= 3; i++) {
            actual = iterator.next();
            assertThat(actual).isEqualTo('0' + i);
        }
        assertThat(iterator.hasNext()).isFalse();
    }

    // -- mkString()

    @Test
    public void shouldMkStringNil() {
        assertThat(empty().mkString()).isEqualTo("");
    }

    @Test
    public void shouldMkStringNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').mkString()).isEqualTo("abc");
    }

    // -- mkString(delimiter)

    @Test
    public void shouldMkStringWithDelimiterNil() {
        assertThat(empty().mkString(",")).isEqualTo("");
    }

    @Test
    public void shouldMkStringWithDelimiterNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').mkString(",")).isEqualTo("a,b,c");
    }

    // -- mkString(delimiter, prefix, suffix)

    @Test
    public void shouldMkStringWithDelimiterAndPrefixAndSuffixNil() {
        assertThat(empty().mkString(",", "[", "]")).isEqualTo("[]");
    }

    @Test
    public void shouldMkStringWithDelimiterAndPrefixAndSuffixNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').mkString(",", "[", "]")).isEqualTo("[a,b,c]");
    }

    // -- last

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenLastOnNil() {
        empty().last();
    }

    @Test
    public void shouldReturnLastOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3').last()).isEqualTo('3');
    }

    // -- lastOption

    @Test
    public void shouldReturnNoneWhenCallingLastOptionOnNil() {
        assertThat(empty().lastOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeLastWhenCallingLastOptionOnNonNil() {
        assertThat(WrappedString.of('1', '2', '3').lastOption()).isEqualTo(new Some<>('3'));
    }

    // -- length

    @Test
    public void shouldComputeLengthOfNil() {
        assertThat(empty().length()).isEqualTo(0);
    }

    @Test
    public void shouldComputeLengthOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3').length()).isEqualTo(3);
    }

    // -- map

    @Test
    public void shouldMapNil() {
        assertThat(this.<Integer> empty().map(i -> i + 1)).isEqualTo(empty());
    }

    @Test
    public void shouldMapNonNil() {
        assertThat(WrappedString.of('1', '2', '3').map(i -> (char)(i + 1))).isEqualTo(WrappedString.of('2', '3', '4'));
    }

    @Test
    public void shouldMapElementsToSequentialValuesInTheRightOrder() {
        final AtomicInteger seq = new AtomicInteger('0');
        final WrappedString expectedInts = WrappedString.of('0', '1', '2', '3', '4');
        final Vector<Character> actualInts = expectedInts.map(ignored -> (char)seq.getAndIncrement());
        assertThat(actualInts).isEqualTo(expectedInts);
    }

    // -- partition

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenPartitionNilAndPredicateIsNull() {
        empty().partition(null);
    }

    @Test
    public void shouldPartitionNil() {
        assertThat(empty().partition(e -> true)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldPartitionIntsInOddAndEvenHavingOddAndEventNumbers() {
        assertThat(WrappedString.of('1', '2', '3', '4').partition(i -> i % 2 != 0)).isEqualTo(Tuple.of(WrappedString.of('1', '3'), WrappedString.of('2', '4')));
    }

    @Test
    public void shouldPartitionIntsInOddAndEvenHavingOnlyOddNumbers() {
        assertThat(WrappedString.of('1', '3').partition(i -> i % 2 != 0)).isEqualTo(Tuple.of(WrappedString.of('1', '3'), empty()));
    }

    @Test
    public void shouldPartitionIntsInOddAndEvenHavingOnlyEvenNumbers() {
        assertThat(WrappedString.of('2', '4').partition(i -> i % 2 != 0)).isEqualTo(Tuple.of(empty(), WrappedString.of('2', '4')));
    }

    // -- max

    @Test
    public void shouldReturnNoneWhenComputingMaxOfNil() {
        assertThat(empty().max()).isEqualTo(None.instance());
    }

    @Test
    public void shouldComputeMaxOfChar() {
        assertThat(WrappedString.of('a', 'b', 'c').max()).isEqualTo(new Some<>('c'));
    }

    // -- maxBy(Comparator)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMaxByWithNullComparator() {
        WrappedString.of('1').maxBy((Comparator<Character>) null);
    }

    @Test
    public void shouldThrowWhenMaxByOfNil() {
        assertThat(empty().maxBy((o1, o2) -> 0)).isEqualTo(None.instance());
    }

    @Test
    public void shouldCalculateMaxByOfInts() {
        assertThat(WrappedString.of('1', '2', '3').maxBy((i1, i2) -> i1 - i2)).isEqualTo(new Some<>('3'));
    }

    @Test
    public void shouldCalculateInverseMaxByOfInts() {
        assertThat(WrappedString.of('1', '2', '3').maxBy((i1, i2) -> i2 - i1)).isEqualTo(new Some<>('1'));
    }

    // -- maxBy(Function)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMaxByWithNullFunction() {
        WrappedString.of('1').maxBy((Function<Character, Character>) null);
    }

    @Test
    public void shouldThrowWhenMaxByFunctionOfNil() {
        assertThat(this.<Integer> empty().maxBy(i -> i)).isEqualTo(None.instance());
    }

    @Test
    public void shouldCalculateMaxByFunctionOfInts() {
        assertThat(WrappedString.of('1', '2', '3').maxBy(i -> i)).isEqualTo(new Some<>('3'));
    }

    @Test
    public void shouldCalculateInverseMaxByFunctionOfInts() {
        assertThat(WrappedString.of('1', '2', '3').maxBy(i -> -i)).isEqualTo(new Some<>('1'));
    }

    // -- min

    @Test
    public void shouldReturnNoneWhenComputingMinOfNil() {
        assertThat(empty().min()).isEqualTo(None.instance());
    }

    @Test
    public void shouldComputeMinOfChar() {
        assertThat(WrappedString.of('a', 'b', 'c').min()).isEqualTo(new Some<>('a'));
    }

    // -- minBy(Comparator)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMinByWithNullComparator() {
        WrappedString.of('1').minBy((Comparator<Character>) null);
    }

    @Test
    public void shouldThrowWhenMinByOfNil() {
        assertThat(empty().minBy((o1, o2) -> 0)).isEqualTo(None.instance());
    }

    @Test
    public void shouldCalculateMinByOfInts() {
        assertThat(WrappedString.of('1', '2', '3').minBy((i1, i2) -> i1 - i2)).isEqualTo(new Some<>('1'));
    }

    @Test
    public void shouldCalculateInverseMinByOfInts() {
        assertThat(WrappedString.of('1', '2', '3').minBy((i1, i2) -> i2 - i1)).isEqualTo(new Some<>('3'));
    }

    // -- minBy(Function)

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMinByWithNullFunction() {
        WrappedString.of('1').minBy((Function<Character, Character>) null);
    }

    @Test
    public void shouldThrowWhenMinByFunctionOfNil() {
        assertThat(empty().minBy(i -> i)).isEqualTo(None.instance());
    }

    @Test
    public void shouldCalculateMinByFunctionOfInts() {
        assertThat(WrappedString.of('1', '2', '3').minBy(i -> i)).isEqualTo(new Some<>('1'));
    }

    @Test
    public void shouldCalculateInverseMinByFunctionOfInts() {
        assertThat(WrappedString.of('1', '2', '3').minBy(i -> -i)).isEqualTo(new Some<>('3'));
    }

    // -- peek

    @Test
    public void shouldPeekNonNilPerformingAnAction() {
        final char[] effect = { 0 };
        final WrappedString actual = WrappedString.of('1', '2', '3').peek(i -> effect[0] = i);
        assertThat(actual).isEqualTo(WrappedString.of('1', '2', '3')); // traverses all elements in the lazy case
        assertThat(effect[0]).isEqualTo('1');
    }

    // -- product

    @Test
    public void shouldComputeProductOfNil() {
        assertThat(empty().product()).isEqualTo(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenComputingProductOfStrings() {
        WrappedString.of('1', '2', '3').product();
    }

    // -- reduce

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenReduceNil() {
        empty().reduce((a, b) -> a);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceNullOperator() {
        this.<String> empty().reduce(null);
    }

    @Test
    public void shouldReduceNonNil() {
        assertThat(WrappedString.of('1', '2', '3').reduce((a, b) -> b)).isEqualTo('3');
    }

    // -- reduceLeft

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenReduceLeftNil() {
        empty().reduceLeft((a, b) -> a);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceLeftNullOperator() {
        this.<String> empty().reduceLeft(null);
    }

    @Test
    public void shouldReduceLeftNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').reduceLeft((xs, x) -> x)).isEqualTo('c');
    }

    // -- reduceRight

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowWhenReduceRightNil() {
        empty().reduceRight((a, b) -> a);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceRightNullOperator() {
        empty().reduceRight(null);
    }

    @Test
    public void shouldReduceRightNonNil() {
        assertThat(WrappedString.of('a', 'b', 'c').reduceRight((x, xs) -> x)).isEqualTo('a');
    }

    // -- replace(curr, new)

    @Test
    public void shouldReplaceElementOfNilUsingCurrNew() {
        assertThat(empty().replace('1', '2')).isEqualTo(empty());
    }

    @Test
    public void shouldReplaceElementOfNonNilUsingCurrNew() {
        assertThat(WrappedString.of('0', '1', '2', '1').replace('1', '3')).isEqualTo(WrappedString.of('0', '3', '2', '1'));
    }

    // -- replaceAll(curr, new)

    @Test
    public void shouldReplaceAllElementsOfNilUsingCurrNew() {
        assertThat(empty().replaceAll('1', '2')).isEqualTo(empty());
    }

    @Test
    public void shouldReplaceAllElementsOfNonNilUsingCurrNew() {
        assertThat(WrappedString.of('0', '1', '2', '1').replaceAll('1', '3')).isEqualTo(WrappedString.of('0', '3', '2', '3'));
    }

    // -- replaceAll(UnaryOp)

    @Test
    public void shouldReplaceAllElementsOfNilUsingUnaryOp() {
        assertThat(empty().replaceAll(i -> i)).isEqualTo(empty());
    }

    @Test
    public void shouldReplaceAllElementsOfNonNilUsingUnaryOp() {
        assertThat(WrappedString.of('1', '2', '3').replaceAll(i -> i)).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    // -- retainAll

    @Test
    public void shouldRetainAllElementsFromNil() {
        assertThat(empty().retainAll(WrappedString.of('1', '2', '3'))).isEqualTo(empty());
    }

    @Test
    public void shouldRetainAllExistingElementsFromNonNil() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').retainAll(WrappedString.of('1', '2'))).isEqualTo(WrappedString.of('1', '2', '1', '2'));
    }

    @Test
    public void shouldNotRetainAllNonExistingElementsFromNonNil() {
        assertThat(WrappedString.of('1', '2', '3').retainAll(WrappedString.of('4', '5'))).isEqualTo(empty());
    }

    // -- sliding(size)

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNilByZeroSize() {
        empty().sliding(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNilByNegativeSize() {
        empty().sliding(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNonNilByZeroSize() {
        WrappedString.of('1').sliding(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNonNilByNegativeSize() {
        WrappedString.of('1').sliding(-1);
    }

    @Test
    public void shouldSlideNilBySize() {
        assertThat(empty().sliding(1)).isEqualTo(empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlideNonNilBySize1() {
        assertThat(WrappedString.of('1', '2', '3').sliding(1)).isEqualTo(Vector.of(WrappedString.of('1'), WrappedString.of('2'), WrappedString.of('3')));
    }

    @SuppressWarnings("unchecked")
    @Test // #201
    public void shouldSlideNonNilBySize2() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').sliding(2))
                .isEqualTo(Vector.of(WrappedString.of('1', '2'), WrappedString.of('2', '3'), WrappedString.of('3', '4'), WrappedString.of('4', '5')));
    }

    // -- sliding(size, step)

    @Test
    public void shouldSlideNilBySizeAndStep() {
        assertThat(empty().sliding(1, 1)).isEqualTo(empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide5ElementsBySize2AndStep3() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').sliding(2, 3)).isEqualTo(Vector.of(WrappedString.of('1', '2'), WrappedString.of('4', '5')));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide5ElementsBySize2AndStep4() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').sliding(2, 4)).isEqualTo(Vector.of(WrappedString.of('1', '2'), WrappedString.of('5')));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide5ElementsBySize2AndStep5() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').sliding(2, 5)).isEqualTo(Vector.of(WrappedString.of('1', '2')));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide4ElementsBySize5AndStep3() {
        assertThat(WrappedString.of('1', '2', '3', '4').sliding(5, 3)).isEqualTo(Vector.of(WrappedString.of('1', '2', '3', '4')));
    }

    // -- span

    @Test
    public void shouldSpanNil() {
        assertThat(this.<Integer> empty().span(i -> i < 2)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSpanNonNil() {
        assertThat(WrappedString.of('0', '1', '2', '3').span(i -> i == '0' || i == '1')).isEqualTo(Tuple.of(WrappedString.of('0', '1'), WrappedString.of('2', '3')));
    }

    // -- spliterator

    @Test
    public void shouldSplitNil() {
        final java.util.List<Character> actual = new java.util.ArrayList<>();
        WrappedString.empty().spliterator().forEachRemaining(actual::add);
        assertThat(actual).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldSplitNonNil() {
        final java.util.List<Character> actual = new java.util.ArrayList<>();
        WrappedString.of('1', '2', '3').spliterator().forEachRemaining(actual::add);
        assertThat(actual).isEqualTo(Arrays.asList('1', '2', '3'));
    }

    @Test
    public void shouldHaveImmutableSpliterator() {
        assertThat(WrappedString.of('1', '2', '3').spliterator().characteristics() & Spliterator.IMMUTABLE).isNotZero();
    }

    @Test
    public void shouldHaveOrderedSpliterator() {
        assertThat(WrappedString.of('1', '2', '3').spliterator().characteristics() & Spliterator.ORDERED).isNotZero();
    }

    @Test
    public void shouldHaveSizedSpliterator() {
        assertThat(WrappedString.of('1', '2', '3').spliterator().characteristics() & Spliterator.SIZED).isNotZero();
    }

    @Test
    public void shouldReturnSizeWhenSpliterator() {
        assertThat(WrappedString.of('1', '2', '3').spliterator().getExactSizeIfKnown()).isEqualTo(3);
    }

    // -- stderr

    @Test
    public void shouldWriteToStderr() {
        WrappedString.of('1', '2', '3').stderr();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleStderrIOException() {
        final PrintStream originalErr = System.err;
        try (PrintStream failingPrintStream = failingPrintStream()) {
            System.setErr(failingPrintStream);
            WrappedString.of('0').stderr();
        } finally {
            System.setErr(originalErr);
        }
    }

    // -- stdout

    @Test
    public void shouldWriteToStdout() {
        WrappedString.of('1', '2', '3').stdout();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleStdoutIOException() {
        final PrintStream originalOut = System.out;
        try (PrintStream failingPrintStream = failingPrintStream()) {
            System.setOut(failingPrintStream);
            WrappedString.of('0').stdout();
        } finally {
            System.setOut(originalOut);
        }
    }

    // -- sum

    @Test
    public void shouldComputeSumOfNil() {
        assertThat(empty().sum()).isEqualTo(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenComputingSumOfStrings() {
        WrappedString.of('1', '2', '3').sum();
    }

    // -- take

    @Test
    public void shouldTakeNoneOnNil() {
        assertThat(empty().take(1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeNoneIfCountIsNegative() {
        assertThat(WrappedString.of('1', '2', '3').take(-1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeAsExpectedIfCountIsLessThanSize() {
        assertThat(WrappedString.of('1', '2', '3').take(2)).isEqualTo(WrappedString.of('1', '2'));
    }

    @Test
    public void shouldTakeAllIfCountExceedsSize() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        if(isThisLazyCollection()) {
            assertThat(t.take(4)).isEqualTo(t);
        } else {
            assertThat(t.take(4)).isSameAs(t);
        }
    }

    // -- takeRight

    @Test
    public void shouldTakeRightNoneOnNil() {
        assertThat(empty().takeRight(1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeRightNoneIfCountIsNegative() {
        assertThat(WrappedString.of('1', '2', '3').takeRight(-1)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeRightAsExpectedIfCountIsLessThanSize() {
        assertThat(WrappedString.of('1', '2', '3').takeRight(2)).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldTakeRightAllIfCountExceedsSize() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        assertThat(t.takeRight(4)).isSameAs(t);
    }

    // -- takeWhile

    @Test
    public void shouldTakeWhileNoneOnNil() {
        assertThat(empty().takeWhile(x -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeWhileAllOnFalseCondition() {
        assertThat(WrappedString.of('1', '2', '3').takeWhile(x -> false)).isEqualTo(empty());
    }

    @Test
    public void shouldTakeWhileAllOnTrueCondition() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        if(isThisLazyCollection()) {
            assertThat(t.takeWhile(x -> true)).isEqualTo(t);
        } else {
            assertThat(t.takeWhile(x -> true)).isSameAs(t);
        }
    }

    @Test
    public void shouldTakeWhileAsExpected() {
        assertThat(WrappedString.of('2', '4', '5', '6').takeWhile(x -> x % 2 == 0)).isEqualTo(WrappedString.of('2', '4'));
    }

    // -- tail

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenTailOnNil() {
        empty().tail();
    }

    @Test
    public void shouldReturnTailOfNonNil() {
        assertThat(WrappedString.of('1', '2', '3').tail()).isEqualTo(WrappedString.of('2', '3'));
    }

    // -- tailOption

    @Test
    public void shouldReturnNoneWhenCallingTailOptionOnNil() {
        assertThat(empty().tailOption().isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnSomeTailWhenCallingTailOptionOnNonNil() {
        assertThat(WrappedString.of('1', '2', '3').tailOption()).isEqualTo(new Some<>(WrappedString.of('2', '3')));
    }

    // -- toJavaArray(Class)

    @Test
    public void shouldConvertNilToJavaArray() {
        final Character[] actual = WrappedString.empty().toJavaArray(Character.class);
        final Character[] expected = new Character[] {};
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConvertNonNilToJavaArray() {
        final Character[] array = WrappedString.of('1', '2').toJavaArray(Character.class);
        final Character[] expected = new Character[]{ '1', '2' };
        assertThat(array).isEqualTo(expected);
    }

    // -- toJavaList

    @Test
    public void shouldConvertNilToArrayList() {
        assertThat(this.<Integer> empty().toJavaList()).isEqualTo(new ArrayList<Integer>());
    }

    @Test
    public void shouldConvertNonNilToArrayList() {
        assertThat(WrappedString.of('1', '2', '3').toJavaList()).isEqualTo(Arrays.asList('1', '2', '3'));
    }

    // -- toJavaMap(Function)

    @Test
    public void shouldConvertNilToHashMap() {
        assertThat(this.<Integer> empty().toJavaMap(x -> Tuple.of(x, x))).isEqualTo(new java.util.HashMap<>());
    }

    @Test
    public void shouldConvertNonNilToHashMap() {
        final java.util.Map<Character, Character> expected = new java.util.HashMap<>();
        expected.put('1', '1');
        expected.put('2', '2');
        assertThat(WrappedString.of('1', '2').toJavaMap(x -> Tuple.of(x, x))).isEqualTo(expected);
    }

    // -- toJavaSet

    @Test
    public void shouldConvertNilToHashSet() {
        assertThat(WrappedString.empty().toJavaSet()).isEqualTo(new java.util.HashSet<>());
    }

    @Test
    public void shouldConvertNonNilToHashSet() {
        final java.util.Set<Character> expected = new java.util.HashSet<>();
        expected.add('2');
        expected.add('1');
        expected.add('3');
        assertThat(WrappedString.of('1', '2', '2', '3').toJavaSet()).isEqualTo(expected);
    }

    // ++++++ OBJECT ++++++

    // -- equals

    @Test
    public void shouldEqualSameTraversableInstance() {
        final Traversable<?> traversable = empty();
        assertThat(traversable).isEqualTo(traversable);
    }

    @Test
    public void shouldNilNotEqualsNull() {
        assertThat(empty()).isNotNull();
    }

    @Test
    public void shouldNonNilNotEqualsNull() {
        assertThat(WrappedString.of('1')).isNotNull();
    }

    @Test
    public void shouldEmptyNotEqualsDifferentType() {
        assertThat(empty()).isNotEqualTo("");
    }

    @Test
    public void shouldNonEmptyNotEqualsDifferentType() {
        assertThat(WrappedString.of('1')).isNotEqualTo("");
    }

    @Test
    public void shouldRecognizeEqualityOfNils() {
        assertThat(empty()).isEqualTo(empty());
    }

    @Test
    public void shouldRecognizeEqualityOfNonNils() {
        assertThat(WrappedString.of('1', '2', '3').equals(WrappedString.of('1', '2', '3'))).isTrue();
    }

    @Test
    public void shouldRecognizeNonEqualityOfTraversablesOfSameSize() {
        assertThat(WrappedString.of('1', '2', '3').equals(WrappedString.of('1', '2', '4'))).isFalse();
    }

    @Test
    public void shouldRecognizeNonEqualityOfTraversablesOfDifferentSize() {
        assertThat(WrappedString.of('1', '2', '3').equals(WrappedString.of('1', '2'))).isFalse();
    }

    // -- hashCode

    @Test
    public void shouldCalculateHashCodeOfNil() {
        assertThat(empty().hashCode() == empty().hashCode()).isTrue();
    }

    @Test
    public void shouldCalculateHashCodeOfNonNil() {
        assertThat(WrappedString.of('1', '2').hashCode() == WrappedString.of('1', '2').hashCode()).isTrue();
    }

    @Test
    public void shouldCalculateDifferentHashCodesForDifferentTraversables() {
        assertThat(WrappedString.of('1', '2').hashCode() != WrappedString.of('2', '3').hashCode()).isTrue();
    }

    // -- Serializable interface

    @Test
    public void shouldSerializeDeserializeNil() {
        final Object actual = deserialize(serialize(empty()));
        final Object expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        final boolean actual = deserialize(serialize(empty())) == empty();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldSerializeDeserializeNonNil() {
        final Object actual = deserialize(serialize(WrappedString.of('1', '2', '3')));
        final Object expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    // helpers

    static PrintStream failingPrintStream() {
        return new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        });
    }
    // -- append

    @Test
    public void shouldAppendElementToNil() {
        final WrappedString actual = empty().append('1');
        final WrappedString expected = WrappedString.of('1');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendElementToNonNil() {
        final WrappedString actual = WrappedString.of('1', '2').append('3');
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    // -- appendAll

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnAppendAllOfNull() {
        empty().appendAll(null);
    }

    @Test
    public void shouldAppendAllNilToNil() {
        final WrappedString actual = empty().appendAll(empty());
        final WrappedString expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendAllNonNilToNil() {
        final WrappedString actual = this.<Integer> empty().appendAll(WrappedString.of('1', '2', '3'));
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendAllNilToNonNil() {
        final WrappedString actual = WrappedString.of('1', '2', '3').appendAll(empty());
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldAppendAllNonNilToNonNil() {
        final WrappedString actual = WrappedString.of('1', '2', '3').appendAll(WrappedString.of('4', '5', '6'));
        final WrappedString expected = WrappedString.of('1', '2', '3', '4', '5', '6');
        assertThat(actual).isEqualTo(expected);
    }

    // -- apply

    @Test
    public void shouldUseSeqAsPartialFunction() {
        assertThat(WrappedString.of('1', '2', '3').apply(1)).isEqualTo('2');
    }

    // -- containsSlice

    @Test
    public void shouldRecognizeNilNotContainsSlice() {
        final boolean actual = empty().containsSlice(WrappedString.of('1', '2', '3'));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesContainSlice() {
        final boolean actual = WrappedString.of('1', '2', '3', '4', '5').containsSlice(WrappedString.of('2', '3'));
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldRecognizeNonNilDoesNotContainSlice() {
        final boolean actual = WrappedString.of('1', '2', '3', '4', '5').containsSlice(WrappedString.of('2', '1', '4'));
        assertThat(actual).isFalse();
    }

    // -- crossProduct()

    @Test
    public void shouldCalculateCrossProductOfNil() {
        final Vector<Tuple2<Character, Character>> actual = empty().crossProduct();
        assertThat(actual).isEqualTo(empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCalculateCrossProductOfNonNil() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').crossProduct();
        final Vector<Tuple2<Character, Character>> expected = Vector.of(
                Tuple.of('1', '1'), Tuple.of('1', '2'), Tuple.of('1', '3'),
                Tuple.of('2', '1'), Tuple.of('2', '2'), Tuple.of('2', '3'),
                Tuple.of('3', '1'), Tuple.of('3', '2'), Tuple.of('3', '3'));
        assertThat(actual).isEqualTo(expected);
    }

    // -- crossProduct(Iterable)

    @Test
    public void shouldCalculateCrossProductOfNilAndNil() {
        final Traversable<Tuple2<Character, Object>> actual = empty().crossProduct(empty());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldCalculateCrossProductOfNilAndNonNil() {
        final Traversable<Tuple2<Character, Object>> actual = empty().crossProduct(WrappedString.of('1', '2', '3'));
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldCalculateCrossProductOfNonNilAndNil() {
        final Traversable<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').crossProduct(WrappedString.empty());
        assertThat(actual).isEqualTo(empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCalculateCrossProductOfNonNilAndNonNil() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').crossProduct(WrappedString.of('a', 'b'));
        final Vector<Tuple2<Character, Character>> expected = Vector.of(
                Tuple.of('1', 'a'), Tuple.of('1', 'b'),
                Tuple.of('2', 'a'), Tuple.of('2', 'b'),
                Tuple.of('3', 'a'), Tuple.of('3', 'b'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenCalculatingCrossProductAndThatIsNull() {
        empty().crossProduct(null);
    }

    // -- get

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetWithNegativeIndexOnNil() {
        empty().get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetWithNegativeIndexOnNonNil() {
        WrappedString.of('1').get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetOnNil() {
        empty().get(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenGetWithTooBigIndexOnNonNil() {
        WrappedString.of('1').get(1);
    }

    @Test
    public void shouldGetFirstElement() {
        assertThat(WrappedString.of('1', '2', '3').get(0)).isEqualTo('1');
    }

    @Test
    public void shouldGetLastElement() {
        assertThat(WrappedString.of('1', '2', '3').get(2)).isEqualTo('3');
    }

    // -- grouped

    @Test
    public void shouldGroupedNil() {
        assertThat(empty().grouped(1)).isEqualTo(empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenGroupedWithSizeZero() {
        empty().grouped(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenGroupedWithNegativeSize() {
        empty().grouped(-1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGroupedTraversableWithEqualSizedBlocks() {
        assertThat(WrappedString.of('1', '2', '3', '4').grouped(2)).isEqualTo(Vector.of(WrappedString.of('1', '2'), WrappedString.of('3', '4')));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGroupedTraversableWithRemainder() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').grouped(2)).isEqualTo(Vector.of(WrappedString.of('1', '2'), WrappedString.of('3', '4'), WrappedString.of('5')));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGroupedWhenTraversableLengthIsSmallerThanBlockSize() {
        assertThat(WrappedString.of('1', '2', '3', '4').grouped(5)).isEqualTo(Vector.of(WrappedString.of('1', '2', '3', '4')));
    }

    // -- indexOf

    @Test
    public void shouldNotFindIndexOfElementWhenSeqIsEmpty() {
        assertThat(empty().indexOf(1)).isEqualTo(-1);
    }

    @Test
    public void shouldNotFindIndexOfElementWhenStartIsGreater() {
        assertThat(WrappedString.of('1', '2', '3', '4').indexOf(2, 2)).isEqualTo(-1);
    }

    @Test
    public void shouldFindIndexOfFirstElement() {
        assertThat(WrappedString.of('1', '2', '3').indexOf('1')).isEqualTo(0);
    }

    @Test
    public void shouldFindIndexOfInnerElement() {
        assertThat(WrappedString.of('1', '2', '3').indexOf('2')).isEqualTo(1);
    }

    @Test
    public void shouldFindIndexOfLastElement() {
        assertThat(WrappedString.of('1', '2', '3').indexOf('3')).isEqualTo(2);
    }

    // -- indexOfSlice

    @Test
    public void shouldNotFindIndexOfSliceWhenSeqIsEmpty() {
        assertThat(empty().indexOfSlice(WrappedString.of('2', '3'))).isEqualTo(-1);
    }

    @Test
    public void shouldNotFindIndexOfSliceWhenStartIsGreater() {
        assertThat(WrappedString.of('1', '2', '3', '4').indexOfSlice(WrappedString.of('2', '3'), 2)).isEqualTo(-1);
    }

    @Test
    public void shouldFindIndexOfFirstSlice() {
        assertThat(WrappedString.of('1', '2', '3', '4').indexOfSlice(WrappedString.of('1', '2'))).isEqualTo(0);
    }

    @Test
    public void shouldFindIndexOfInnerSlice() {
        assertThat(WrappedString.of('1', '2', '3', '4').indexOfSlice(WrappedString.of('2', '3'))).isEqualTo(1);
    }

    @Test
    public void shouldFindIndexOfLastSlice() {
        assertThat(WrappedString.of('1', '2', '3').indexOfSlice(WrappedString.of('2', '3'))).isEqualTo(1);
    }

    // -- lastIndexOf

    @Test
    public void shouldNotFindLastIndexOfElementWhenSeqIsEmpty() {
        assertThat(empty().lastIndexOf(1)).isEqualTo(-1);
    }

    @Test
    public void shouldNotFindLastIndexOfElementWhenEndIdLess() {
        assertThat(WrappedString.of('1', '2', '3', '4').lastIndexOf(3, 1)).isEqualTo(-1);
    }

    @Test
    public void shouldFindLastIndexOfElement() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').lastIndexOf('1')).isEqualTo(3);
    }

    @Test
    public void shouldFindLastIndexOfElementWithEnd() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').lastIndexOf('1', 1)).isEqualTo(0);
    }

    // -- lastIndexOfSlice

    @Test
    public void shouldNotFindLastIndexOfSliceWhenSeqIsEmpty() {
        assertThat(empty().lastIndexOfSlice(WrappedString.of('2', '3'))).isEqualTo(-1);
    }

    @Test
    public void shouldNotFindLastIndexOfSliceWhenEndIdLess() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').lastIndexOfSlice(WrappedString.of('3', '4'), 1)).isEqualTo(-1);
    }

    @Test
    public void shouldFindLastIndexOfSlice() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2').lastIndexOfSlice(empty())).isEqualTo(5);
        assertThat(WrappedString.of('1', '2', '3', '1', '2').lastIndexOfSlice(WrappedString.of('2'))).isEqualTo(4);
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3', '4').lastIndexOfSlice(WrappedString.of('2', '3'))).isEqualTo(4);
    }

    @Test
    public void shouldFindLastIndexOfSliceWithEnd() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').lastIndexOfSlice(empty(), 2)).isEqualTo(2);
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').lastIndexOfSlice(WrappedString.of('2'), 2)).isEqualTo(1);
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').lastIndexOfSlice(WrappedString.of('2', '3'), 2)).isEqualTo(1);
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3', '4').lastIndexOfSlice(WrappedString.of('2', '3'), 2)).isEqualTo(1);
    }

    // -- insert

    @Test
    public void shouldInsertIntoNil() {
        final WrappedString actual = this.<Integer> empty().insert(0, '1');
        final WrappedString expected = WrappedString.of('1');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertInFrontOfElement() {
        final WrappedString actual = WrappedString.of('4').insert(0, '1');
        final WrappedString expected = WrappedString.of('1', '4');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertBehindOfElement() {
        final WrappedString actual = WrappedString.of('4').insert(1, '1');
        final WrappedString expected = WrappedString.of('4', '1');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertIntoSeq() {
        final WrappedString actual = WrappedString.of('1', '2', '3').insert(2, '4');
        final WrappedString expected = WrappedString.of('1', '2', '4', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNonNilWithNegativeIndex() {
        WrappedString.of('1').insert(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNilWithNegativeIndex() {
        empty().insert(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnInsertWhenExceedingUpperBound() {
        empty().insert(1, null);
    }

    // -- insertAll

    @Test
    public void shouldInserAlltIntoNil() {
        final WrappedString actual = this.<Integer> empty().insertAll(0, WrappedString.of('1', '2', '3'));
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertAllInFrontOfElement() {
        final WrappedString actual = WrappedString.of('4').insertAll(0, WrappedString.of('1', '2', '3'));
        final WrappedString expected = WrappedString.of('1', '2', '3', '4');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertAllBehindOfElement() {
        final WrappedString actual = WrappedString.of('4').insertAll(1, WrappedString.of('1', '2', '3'));
        final WrappedString expected = WrappedString.of('4', '1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldInsertAllIntoSeq() {
        final WrappedString actual = WrappedString.of('1', '2', '3').insertAll(2, WrappedString.of('4', '5'));
        final WrappedString expected = WrappedString.of('1', '2', '4', '5', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnInsertAllWithNil() {
        empty().insertAll(0, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNonNilAllWithNegativeIndex() {
        WrappedString.of('1').insertAll(-1, empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenInsertOnNilAllWithNegativeIndex() {
        empty().insertAll(-1, empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnInsertAllWhenExceedingUpperBound() {
        empty().insertAll(1, empty());
    }


    // -- intersperse

    @Test
    public void shouldIntersperseNil() {
        assertThat(this.<Character> empty().intersperse(',')).isEqualTo(empty());
    }

    @Test
    public void shouldIntersperseSingleton() {
        assertThat(WrappedString.of('a').intersperse(',')).isEqualTo(WrappedString.of('a'));
    }

    @Test
    public void shouldIntersperseMultipleElements() {
        assertThat(WrappedString.of('a', 'b').intersperse(',')).isEqualTo(WrappedString.of('a', ',', 'b'));
    }

    // -- iterator(int)

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenNilIteratorStartingAtIndex() {
        empty().iterator(1);
    }

    @Test
    public void shouldIterateFirstElementOfNonNilStartingAtIndex() {
        assertThat(WrappedString.of('1', '2', '3').iterator(1).next()).isEqualTo('2');
    }

    @Test
    public void shouldFullyIterateNonNilStartingAtIndex() {
        int actual = -1;
        for (java.util.Iterator<Character> iter = WrappedString.of('1', '2', '3').iterator(1); iter.hasNext(); ) {
            actual = iter.next();
        }
        assertThat(actual).isEqualTo('3');
    }

    // -- prepend

    @Test
    public void shouldPrependElementToNil() {
        final WrappedString actual = this.<Integer> empty().prepend('1');
        final WrappedString expected = WrappedString.of('1');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependElementToNonNil() {
        final WrappedString actual = WrappedString.of('2', '3').prepend('1');
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    // -- prependAll

    @Test(expected = NullPointerException.class)
    public void shouldThrowOnPrependAllOfNull() {
        empty().prependAll(null);
    }

    @Test
    public void shouldPrependAllNilToNil() {
        final WrappedString actual = this.<Integer> empty().prependAll(empty());
        final WrappedString expected = empty();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependAllNilToNonNil() {
        final WrappedString actual = WrappedString.of('1', '2', '3').prependAll(empty());
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependAllNonNilToNil() {
        final WrappedString actual = this.<Integer> empty().prependAll(WrappedString.of('1', '2', '3'));
        final WrappedString expected = WrappedString.of('1', '2', '3');
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPrependAllNonNilToNonNil() {
        final WrappedString actual = WrappedString.of('4', '5', '6').prependAll(WrappedString.of('1', '2', '3'));
        final WrappedString expected = WrappedString.of('1', '2', '3', '4', '5', '6');
        assertThat(actual).isEqualTo(expected);
    }

    // -- remove

    @Test
    public void shouldRemoveElementFromNil() {
        assertThat(empty().remove(null)).isEqualTo(empty());
    }

    @Test
    public void shouldRemoveFirstElement() {
        assertThat(WrappedString.of('1', '2', '3').remove('1')).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldRemoveLastElement() {
        assertThat(WrappedString.of('1', '2', '3').remove('3')).isEqualTo(WrappedString.of('1', '2'));
    }

    @Test
    public void shouldRemoveInnerElement() {
        assertThat(WrappedString.of('1', '2', '3').remove('2')).isEqualTo(WrappedString.of('1', '3'));
    }

    @Test
    public void shouldRemoveNonExistingElement() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        if (isThisLazyCollection()) {
            assertThat(t.remove('4')).isEqualTo(t);
        } else {
            assertThat(t.remove('4')).isSameAs(t);
        }
    }

    boolean isThisLazyCollection() {
        return false;
    }

    // -- removeFirst(Predicate)

    @Test
    public void shouldRemoveFirstElementByPredicateFromNil() {
        assertThat(empty().removeFirst(v -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldRemoveFirstElementByPredicateBegin() {
        assertThat(WrappedString.of('1', '2', '3').removeFirst(v -> v == '1')).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateBeginM() {
        assertThat(WrappedString.of('1', '2', '1', '3').removeFirst(v -> v == '1')).isEqualTo(WrappedString.of('2', '1', '3'));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateEnd() {
        assertThat(WrappedString.of('1', '2', '3').removeFirst(v -> v == '3')).isEqualTo(WrappedString.of('1', '2'));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateInner() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').removeFirst(v -> v == '3')).isEqualTo(WrappedString.of('1', '2', '4', '5'));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateInnerM() {
        assertThat(WrappedString.of('1', '2', '3', '2', '5').removeFirst(v -> v == '2')).isEqualTo(WrappedString.of('1', '3', '2', '5'));
    }

    @Test
    public void shouldRemoveFirstElementByPredicateNonExisting() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        if (isThisLazyCollection()) {
            assertThat(t.removeFirst(v -> v == 4)).isEqualTo(t);
        } else {
            assertThat(t.removeFirst(v -> v == 4)).isSameAs(t);
        }
    }

    // -- removeLast(Predicate)

    @Test
    public void shouldRemoveLastElementByPredicateFromNil() {
        assertThat(empty().removeLast(v -> true)).isEqualTo(empty());
    }

    @Test
    public void shouldRemoveLastElementByPredicateBegin() {
        assertThat(WrappedString.of('1', '2', '3').removeLast(v -> v == '1')).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldRemoveLastElementByPredicateEnd() {
        assertThat(WrappedString.of('1', '2', '3').removeLast(v -> v == '3')).isEqualTo(WrappedString.of('1', '2'));
    }

    @Test
    public void shouldRemoveLastElementByPredicateEndM() {
        assertThat(WrappedString.of('1', '3', '2', '3').removeLast(v -> v == '3')).isEqualTo(WrappedString.of('1', '3', '2'));
    }

    @Test
    public void shouldRemoveLastElementByPredicateInner() {
        assertThat(WrappedString.of('1', '2', '3', '4', '5').removeLast(v -> v == '3')).isEqualTo(WrappedString.of('1', '2', '4', '5'));
    }

    @Test
    public void shouldRemoveLastElementByPredicateInnerM() {
        assertThat(WrappedString.of('1', '2', '3', '2', '5').removeLast(v -> v == '2')).isEqualTo(WrappedString.of('1', '2', '3', '5'));
    }

    @Test
    public void shouldRemoveLastElementByPredicateNonExisting() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        assertThat(t.removeLast(v -> v == 4)).isSameAs(t);
    }

    // -- removeAll(Iterable)

    @Test
    public void shouldRemoveAllElementsFromNil() {
        assertThat(empty().removeAll(WrappedString.of('1', '2', '3'))).isEqualTo(empty());
    }

    @Test
    public void shouldRemoveAllExistingElementsFromNonNil() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').removeAll(WrappedString.of('1', '2'))).isEqualTo(WrappedString.of('3', '3'));
    }

    @Test
    public void shouldNotRemoveAllNonExistingElementsFromNonNil() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        if (isThisLazyCollection()) {
            assertThat(t.removeAll(WrappedString.of('4', '5'))).isEqualTo(t);
        } else {
            assertThat(t.removeAll(WrappedString.of('4', '5'))).isSameAs(t);
        }
    }

    // -- removeAll(Object)

    @Test
    public void shouldRemoveAllObjectsFromNil() {
        assertThat(empty().removeAll('1')).isEqualTo(empty());
    }

    @Test
    public void shouldRemoveAllExistingObjectsFromNonNil() {
        assertThat(WrappedString.of('1', '2', '3', '1', '2', '3').removeAll('1')).isEqualTo(WrappedString.of('2', '3', '2', '3'));
    }

    @Test
    public void shouldNotRemoveAllNonObjectsElementsFromNonNil() {
        final WrappedString t = WrappedString.of('1', '2', '3');
        if (isThisLazyCollection()) {
            assertThat(t.removeAll('4')).isEqualTo(t);
        } else {
            assertThat(t.removeAll('4')).isSameAs(t);
        }
    }

    // -- reverse

    @Test
    public void shouldReverseNil() {
        assertThat(empty().reverse()).isEqualTo(empty());
    }

    @Test
    public void shouldReverseNonNil() {
        assertThat(WrappedString.of('1', '2', '3').reverse()).isEqualTo(WrappedString.of('3', '2', '1'));
    }

    // -- set

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithNegativeIndexOnNil() {
        empty().set(-1, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithNegativeIndexOnNonNil() {
        WrappedString.of('1').set(-1, '2');
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetOnNil() {
        empty().set(0, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithIndexExceedingByOneOnNonNil() {
        WrappedString.of('1').set(1, '2');
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSetWithIndexExceedingByTwoOnNonNil() {
        WrappedString.of('1').set(2, '2');
    }

    @Test
    public void shouldSetFirstElement() {
        assertThat(WrappedString.of('1', '2', '3').set(0, '4')).isEqualTo(WrappedString.of('4', '2', '3'));
    }

    @Test
    public void shouldSetLastElement() {
        assertThat(WrappedString.of('1', '2', '3').set(2, '4')).isEqualTo(WrappedString.of('1', '2', '4'));
    }

    // -- sort()

    @Test
    public void shouldSortNil() {
        assertThat(empty().sort()).isEqualTo(empty());
    }

    @Test
    public void shouldSortNonNil() {
        assertThat(WrappedString.of('3', '4', '1', '2').sort()).isEqualTo(WrappedString.of('1', '2', '3', '4'));
    }

    // -- sort(Comparator)

    @Test
    public void shouldSortNilUsingComparator() {
        assertThat(this.<Integer> empty().sort((i, j) -> j - i)).isEqualTo(empty());
    }

    @Test
    public void shouldSortNonNilUsingComparator() {
        assertThat(WrappedString.of('3', '4', '1', '2').sort((i, j) -> j - i)).isEqualTo(WrappedString.of('4', '3', '2', '1'));
    }

    // -- splitAt(index)

    @Test
    public void shouldSplitAtNil() {
        assertThat(empty().splitAt(1)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSplitAtNonNil() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(1)).isEqualTo(Tuple.of(WrappedString.of('1'), WrappedString.of('2', '3')));
    }

    @Test
    public void shouldSplitAtBegin() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(0)).isEqualTo(Tuple.of(empty(), WrappedString.of('1', '2', '3')));
    }

    @Test
    public void shouldSplitAtEnd() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(3)).isEqualTo(Tuple.of(WrappedString.of('1', '2', '3'), empty()));
    }

    @Test
    public void shouldSplitAtOutOfBounds() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(5)).isEqualTo(Tuple.of(WrappedString.of('1', '2', '3'), empty()));
        assertThat(WrappedString.of('1', '2', '3').splitAt(-1)).isEqualTo(Tuple.of(empty(), WrappedString.of('1', '2', '3')));
    }

    // -- splitAt(predicate)

    @Test
    public void shouldSplitPredicateAtNil() {
        assertThat(empty().splitAt(e -> true)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSplitPredicateAtNonNil() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(e -> e == '2')).isEqualTo(Tuple.of(WrappedString.of('1'), WrappedString.of('2', '3')));
    }

    @Test
    public void shouldSplitAtPredicateBegin() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(e -> e == '1')).isEqualTo(Tuple.of(empty(), WrappedString.of('1', '2', '3')));
    }

    @Test
    public void shouldSplitAtPredicateEnd() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(e -> e == '3')).isEqualTo(Tuple.of(WrappedString.of('1', '2'), WrappedString.of('3')));
    }

    @Test
    public void shouldSplitAtPredicateNotFound() {
        assertThat(WrappedString.of('1', '2', '3').splitAt(e -> e == '5')).isEqualTo(Tuple.of(WrappedString.of('1', '2', '3'), empty()));
    }

    // -- splitAtInclusive(predicate)

    @Test
    public void shouldSplitInclusivePredicateAtNil() {
        assertThat(empty().splitAtInclusive(e -> true)).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldSplitInclusivePredicateAtNonNil() {
        assertThat(WrappedString.of('1', '2', '3').splitAtInclusive(e -> e == '2')).isEqualTo(Tuple.of(WrappedString.of('1', '2'), WrappedString.of('3')));
    }

    @Test
    public void shouldSplitAtInclusivePredicateBegin() {
        assertThat(WrappedString.of('1', '2', '3').splitAtInclusive(e -> e == '1')).isEqualTo(Tuple.of(WrappedString.of('1'), WrappedString.of('2', '3')));
    }

    @Test
    public void shouldSplitAtInclusivePredicateEnd() {
        assertThat(WrappedString.of('1', '2', '3').splitAtInclusive(e -> e == '3')).isEqualTo(Tuple.of(WrappedString.of('1', '2', '3'), empty()));
    }

    @Test
    public void shouldSplitAtInclusivePredicateNotFound() {
        assertThat(WrappedString.of('1', '2', '3').splitAtInclusive(e -> e == '5')).isEqualTo(Tuple.of(WrappedString.of('1', '2', '3'), empty()));
    }

    // -- removeAt(index)

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldRemoveIndxAtNil() {
        assertThat(empty().removeAt(1)).isEqualTo(empty());
    }

    @Test
    public void shouldRemoveIndxAtNonNil() {
        assertThat(WrappedString.of('1', '2', '3').removeAt(1)).isEqualTo(WrappedString.of('1', '3'));
    }

    @Test
    public void shouldRemoveIndxAtBegin() {
        assertThat(WrappedString.of('1', '2', '3').removeAt(0)).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldRemoveIndxAtEnd() {
        assertThat(WrappedString.of('1', '2', '3').removeAt(2)).isEqualTo(WrappedString.of('1', '2'));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldRemoveIndxOutOfBoundsLeft() {
        assertThat(WrappedString.of('1', '2', '3').removeAt(-1)).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldRemoveIndxOutOfBoundsRight() {
        assertThat(WrappedString.of('1', '2', '3').removeAt(5)).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    // -- subsequence(beginIndex)

    @Test
    public void shouldReturnNilWhenSubsequenceFrom0OnNil() {
        final WrappedString actual = this.<Integer> empty().subsequence(0);
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldReturnIdentityWhenSubsequenceFrom0OnNonNil() {
        final WrappedString actual = WrappedString.of('1').subsequence(0);
        assertThat(actual).isEqualTo(WrappedString.of('1'));
    }

    @Test
    public void shouldReturnNilWhenSubsequenceFrom1OnSeqOf1() {
        final WrappedString actual = WrappedString.of('1').subsequence(1);
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldReturnSubsequenceWhenIndexIsWithinRange() {
        final WrappedString actual = WrappedString.of('1', '2', '3').subsequence(1);
        assertThat(actual).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldReturnNilWhenSubsequenceBeginningWithSize() {
        final WrappedString actual = WrappedString.of('1', '2', '3').subsequence(3);
        assertThat(actual).isEqualTo(empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubsequenceOnNil() {
        empty().subsequence(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubsequenceWithOutOfLowerBound() {
        WrappedString.of('1', '2', '3').subsequence(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubsequenceWithOutOfUpperBound() {
        WrappedString.of('1', '2', '3').subsequence(4);
    }

    // -- subsequence(beginIndex, endIndex)

    @Test
    public void shouldReturnNilWhenSubsequenceFrom0To0OnNil() {
        final WrappedString actual = this.<Integer> empty().subsequence(0, 0);
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldReturnNilWhenSubsequenceFrom0To0OnNonNil() {
        final WrappedString actual = WrappedString.of('1').subsequence(0, 0);
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldReturnSeqWithFirstElementWhenSubsequenceFrom0To1OnNonNil() {
        final WrappedString actual = WrappedString.of('1').subsequence(0, 1);
        assertThat(actual).isEqualTo(WrappedString.of('1'));
    }

    @Test
    public void shouldReturnNilWhenSubsequenceFrom1To1OnNonNil() {
        final WrappedString actual = WrappedString.of('1').subsequence(1, 1);
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldReturnSubsequenceWhenIndicesAreWithinRange() {
        final WrappedString actual = WrappedString.of('1', '2', '3').subsequence(1, 3);
        assertThat(actual).isEqualTo(WrappedString.of('2', '3'));
    }

    @Test
    public void shouldReturnNilWhenIndicesBothAreUpperBound() {
        final WrappedString actual = WrappedString.of('1', '2', '3').subsequence(3, 3);
        assertThat(actual).isEqualTo(empty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubsequenceOnNonNilWhenBeginIndexIsGreaterThanEndIndex() {
        WrappedString.of('1', '2', '3').subsequence(1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubsequenceOnNilWhenBeginIndexIsGreaterThanEndIndex() {
        empty().subsequence(1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubsequenceOnNonNilWhenBeginIndexExceedsLowerBound() {
        WrappedString.of('1', '2', '3').subsequence(-'1', '2');
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubsequenceOnNilWhenBeginIndexExceedsLowerBound() {
        empty().subsequence(-'1', '2');
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowWhenSubsequence2OnNil() {
        empty().subsequence(0, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowOnSubsequenceWhenEndIndexExceedsUpperBound() {
        WrappedString.of('1', '2', '3').subsequence(1, 4).mkString(); // force computation of last element, e.g. because Stream is lazy
    }

    // -- unzip

    @Test
    public void shouldUnzipNil() {
        assertThat(empty().unzip(x -> Tuple.of(x, x))).isEqualTo(Tuple.of(empty(), empty()));
    }

    @Test
    public void shouldUnzipNonNil() {
        final Tuple actual = WrappedString.of('0', '1').unzip(i -> Tuple.of(i, i == '0' ? 'a' : 'b'));
        final Tuple expected = Tuple.of(Vector.of('0', '1'), Vector.of('a', 'b'));
        assertThat(actual).isEqualTo(expected);
    }

    // -- zip

    @Test
    public void shouldZipNils() {
        final Seq<?> actual = empty().zip(empty());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipEmptyAndNonNil() {
        final Seq<?> actual = empty().zip(WrappedString.of('1'));
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipNonEmptyAndNil() {
        final Seq<?> actual = WrappedString.of('1').zip(empty());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipNonNilsIfThisIsSmaller() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2').zip(WrappedString.of('a', 'b', 'c'));
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Character>> expected = Vector.of(Tuple.of('1', 'a'), Tuple.of('2', 'b'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipNonNilsIfThatIsSmaller() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').zip(WrappedString.of('a', 'b'));
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Character>> expected = Vector.of(Tuple.of('1', 'a'), Tuple.of('2', 'b'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipNonNilsOfSameSize() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').zip(WrappedString.of('a', 'b', 'c'));
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Character>> expected = Vector.of(Tuple.of('1', 'a'), Tuple.of('2', 'b'), Tuple.of('3', 'c'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipWithThatIsNull() {
        empty().zip(null);
    }

    // -- zipAll

    @Test
    public void shouldZipAllNils() {
        final Seq<?> actual = empty().zipAll(empty(), null, null);
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldZipAllEmptyAndNonNil() {
        final Vector<?> actual = empty().zipAll(WrappedString.of('1'), null, null);
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Object, Character>> expected = Vector.of(Tuple.of(null, '1'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonEmptyAndNil() {
        final Vector<?> actual = WrappedString.of('1').zipAll(empty(), null, null);
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Object>> expected = Vector.of(Tuple.of('1', null));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThisIsSmaller() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2').zipAll(WrappedString.of('a', 'b', 'c'), '9', 'z');
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Character>> expected = Vector.of(Tuple.of('1', 'a'), Tuple.of('2', 'b'), Tuple.of('9', 'c'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThatIsSmaller() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').zipAll(WrappedString.of('a', 'b'), '9', 'z');
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Character>> expected = Vector.of(Tuple.of('1', 'a'), Tuple.of('2', 'b'), Tuple.of('3', 'z'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsOfSameSize() {
        final Vector<Tuple2<Character, Character>> actual = WrappedString.of('1', '2', '3').zipAll(WrappedString.of('a', 'b', 'c'), '9', 'z');
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Character>> expected = Vector.of(Tuple.of('1', 'a'), Tuple.of('2', 'b'), Tuple.of('3', 'c'));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipAllWithThatIsNull() {
        empty().zipAll(null, null, null);
    }

    // -- zipWithIndex

    @Test
    public void shouldZipNilWithIndex() {
        assertThat(this.<WrappedString> empty().zipWithIndex()).isEqualTo(this.<Tuple2<WrappedString, Integer>> empty());
    }

    @Test
    public void shouldZipNonNilWithIndex() {
        final Vector<Tuple2<Character, Integer>> actual = WrappedString.of("abc").zipWithIndex();
        @SuppressWarnings("unchecked")
        final Vector<Tuple2<Character, Integer>> expected = Vector.of(Tuple.of('a', 0), Tuple.of('b', 1), Tuple.of('c', 2));
        assertThat(actual).isEqualTo(expected);
    }

    // -- static collector()

    @Test
    public void shouldStreamAndCollectNil() {
        final Seq<?> actual = java.util.stream.Stream.<Character>empty().collect(WrappedString.collector());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldStreamAndCollectNonNil() {
        final Seq<?> actual = java.util.stream.Stream.of('1', '2', '3').collect(WrappedString.collector());
        assertThat(actual).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    @Test
    public void shouldParallelStreamAndCollectNil() {
        final Seq<?> actual = java.util.stream.Stream.<Character>empty().parallel().collect(WrappedString.collector());
        assertThat(actual).isEqualTo(empty());
    }

    @Test
    public void shouldParallelStreamAndCollectNonNil() {
        final Seq<?> actual = java.util.stream.Stream.of('1', '2', '3').parallel().collect(WrappedString.collector());
        assertThat(actual).isEqualTo(WrappedString.of('1', '2', '3'));
    }

    // -- static empty()

    @Test
    public void shouldCreateNil() {
        final Seq<?> actual = empty();
        assertThat(actual.length()).isEqualTo(0);
    }

    // -- static javaslang.String.of(T...)

    @Test
    public void shouldCreateSeqOfElements() {
        final WrappedString actual = WrappedString.of('1', '2');
        assertThat(actual.length()).isEqualTo(2);
        assertThat(actual.get(0)).isEqualTo('1');
        assertThat(actual.get(1)).isEqualTo('2');
    }

    // -- static ofAll(Iterable)

    @Test
    public void shouldCreateListOfIterable() {
        final java.util.List<Character> arrayList = Arrays.asList('1', '2');
        final WrappedString actual = WrappedString.ofAll(arrayList);
        assertThat(actual.length()).isEqualTo(2);
        assertThat(actual.get(0)).isEqualTo('1');
        assertThat(actual.get(1)).isEqualTo('2');
    }

}