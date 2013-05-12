package net.prank;

import junit.framework.TestCase;

import java.util.Set;

/**
 *
 */
public class ScoringToolTest
        extends TestCase {


    public void test__scoreSlicesEvenly() {

        ScoringTool tool = new ScoringTool();

        Set<ScoringRange> scores = tool.scoreSlicesEvenlyLowValueAsHighScore(0, 10, 5, 100.0, 575.0);
        assertNotNull(scores);
    }

    public void test__scoreSlicesEvenly_small_range() {

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scores = tool.scoreSlicesEvenlyLowValueAsHighScore(0, 10, 3, 0, 2);
        assertNotNull(scores);
    }
}