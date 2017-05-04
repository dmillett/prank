package net.prank.tools;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by dave on 5/2/17.
 */
public class ScoringRangeTest {

    @Test
    public void test_aboveRange() {

        ScoringRange range = new ScoringRange(0.0, 5.0, 1.0);
        assertTrue(range.aboveRange(6.0));
        assertFalse(range.aboveRange(4.0));
    }

    @Test
    public void test_belowRange() {

        ScoringRange range = new ScoringRange(0.0, 5.0, 1.0);
        assertTrue(range.belowRange(-1.0));
        assertFalse(range.belowRange(4.0));
    }

    @Test
    public void test_equals() {

        ScoringRange rangeOne = new ScoringRange(0.0, 1.0, 5.0);
        ScoringRange rangeTwo = new ScoringRange(0.0, 1.0, 5.0);
        assertEquals(rangeOne, rangeTwo);
        assertEquals(rangeOne, rangeOne);
    }

    @Test
    public void test_equals_not() {

        ScoringRange rangeOne = new ScoringRange(0.0, 1.0, 5.0);
        ScoringRange rangeTwo = new ScoringRange(0.0, 2.0, 4.0);
        ScoringRange rangeThree = new ScoringRange(-1.0, 3.0, 6.0);
        ScoringRange rangeFour = new ScoringRange(1.0, 1.0, 5.0);
        assertNotEquals(rangeOne, rangeTwo);
        assertNotEquals(rangeOne, rangeThree);
        assertNotEquals(rangeOne, rangeFour);
        assertNotEquals(rangeOne, "foo");
        assertNotEquals(rangeOne, null);
    }

    @Test
    public void test_hashCode() {

        ScoringRange rangeOne = new ScoringRange(0.0, 1.0, 5.0);
        ScoringRange rangeTwo = new ScoringRange(0.0, 1.0, 4.0);
        Set<ScoringRange> ranges = new HashSet<>();
        ranges.add(rangeOne);
        ranges.add(rangeTwo);
        assertEquals(2, ranges.size());
    }

    @Test
    public void test_toString() {

        ScoringRange rangeOne = new ScoringRange(0.0, 1.0, 5.0);
        assertEquals("ScoringRange{_min=0.0, _max=1.0, _scorePoints=5.0}", rangeOne.toString());
    }
}
