package net.prank.tools;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author dmillett
 *
 * Copyright 2012 David Millett
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
public class ScoringToolTest {

    private static final double DELTA = 1e-10;


    @Test
    public void test__scoreSlicesEvenly() {

        ScoringTool tool = new ScoringTool();

        // min, max, bucketCount, grossMin, grossMax
        Set<ScoringRange> scores = tool.scoreBucketsEvenlyLowValueAsHighScore(0, 10, 5, 100.0, 575.0);
        assertNotNull(scores);

        assertEquals(5, scores.size());

        for ( ScoringRange scr : scores )
        {
            if (scr.getScorePoints() == 10.0)
            {
                assertEquals(100.0, scr.getMax(), DELTA);
                assertEquals(100.0, scr.getMin(), DELTA);
            }
            else if (scr.getScorePoints() == 8.0)
            {
                assertEquals(100, (int)scr.getMin(), DELTA);
                assertEquals(218, (int)scr.getMax(), DELTA);
            }
            else if (scr.getScorePoints() == 6.0)
            {
                assertEquals(218, (int)scr.getMin(), DELTA);
                assertEquals(337, (int)scr.getMax(), DELTA);
            }
            else if (scr.getScorePoints() == 4.0)
            {
                assertEquals(337, (int)scr.getMin(), DELTA);
                assertEquals(456, (int)scr.getMax(), DELTA);
            }
            else if (scr.getScorePoints() == 2.0)
            {
                assertEquals(456, (int)scr.getMin(), DELTA);
                assertEquals(575, (int)scr.getMax(), DELTA);
            }
            else if (scr.getScorePoints() == 0.0)
            {
                assertEquals(575.0, scr.getMax(), DELTA);
                assertEquals(575.0, scr.getMin(), DELTA);
            }
        }
    }

    @Test
    public void test__scoreSlicesEvenly_small_range() {

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scores = tool.scoreBucketsEvenlyLowValueAsHighScore(0, 10, 3, 0, 2);
        assertEquals(3, scores.size());

        for ( ScoringRange scr : scores )
        {
            if (scr.getScorePoints() == 10.0)
            {
                assertEquals(0.0, scr.getMax(), DELTA);
                assertEquals(0.0, scr.getMin(), DELTA);
            }
            else if (scr.getScorePoints() == 5.0)
            {
                assertEquals(1.0, scr.getMin(), DELTA);
                assertEquals(1.0, scr.getMax(), DELTA);
            }
            else if (scr.getScorePoints() == 4.0)
            {
                assertEquals(2.0, scr.getMin(), DELTA);
                assertEquals(2.0, scr.getMax(), DELTA);
            }
        }
    }

    @Test
    public void test__scoreBucketsEvenlyHighValueAsHighScore() {

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> ranges = tool.scoreBucketsEvenlyHighValueAsHighScore(0, 10, 10, -1.0, 95.0);
        System.out.println(tool.dumpScoringRanges(ranges, "High value as high score"));
        assertNotNull(ranges);

        for ( ScoringRange scr : ranges )
        {
            if (scr.getScorePoints() == 10.0)
            {
                assertEquals(95.0, scr.getMax(), DELTA);
                assertEquals(95.0, scr.getMin(), DELTA);
            }
            else if (scr.getScorePoints() == 0.0)
            {
                assertEquals(-1.0, scr.getMax(), DELTA);
                assertEquals(-1.0, scr.getMin(), DELTA);
            }
            else if (scr.getScorePoints() == 7.0)
            {
                assertEquals(56.4, scr.getMin(), DELTA);
                assertEquals(65.8, scr.getMax(), DELTA);
            }
        }
    }

    @Test
    public void test__normalize_bad_values() {

        ScoringTool tool = new ScoringTool();

        assertNull(tool.normalize(null, null, null));
        assertNull(tool.normalize(new BigDecimal("1.0"), null, null));

        BigDecimal original = new BigDecimal("1.0");
        BigDecimal max = new BigDecimal("10.0");
        assertEquals(original, tool.normalize(original, max, null));
        assertEquals(original, tool.normalize(original, max, new BigDecimal("10.0")));
    }

    @Test
    public void test__normalize_max_less_than_normalize_target() {

        ScoringTool tool = new ScoringTool();
        BigDecimal value = new BigDecimal("10.0");
        BigDecimal max = new BigDecimal("20.0");
        BigDecimal normalizeTarget = new BigDecimal("100.0");

        BigDecimal normalized = tool.normalize(value, max, normalizeTarget);
        assertEquals(new BigDecimal("50.0"), normalized);
    }

    @Test
    public void test__normalize_max_more_than_normalize_target() {

        ScoringTool tool = new ScoringTool();
        BigDecimal value = new BigDecimal("10.0");
        BigDecimal max = new BigDecimal("110.0");
        BigDecimal normalizeTarget = new BigDecimal("100.0");

        BigDecimal normalized = tool.normalize(value, max, normalizeTarget);
        assertEquals(new BigDecimal("9.1"), normalized);
    }

    @Test
    public void test__normalize_max_more_than_normalize_target2() {

        ScoringTool tool = new ScoringTool();
        BigDecimal value = new BigDecimal("121.0");
        BigDecimal max = new BigDecimal("125.0");
        BigDecimal normalizeTarget = new BigDecimal("100.0");

        BigDecimal normalized = tool.normalize(value, max, normalizeTarget);
        assertEquals(new BigDecimal("96.8"), normalized);
    }
}