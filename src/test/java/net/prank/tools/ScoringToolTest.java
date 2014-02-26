package net.prank.tools;

import junit.framework.TestCase;
import net.prank.tools.ScoringRange;
import net.prank.tools.ScoringTool;

import java.util.Set;

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
public class ScoringToolTest
    extends TestCase {

    public void test__scoreSlicesEvenly() {

        ScoringTool tool = new ScoringTool();

        // min, max, bucketCount, grossMin, grossMax
        Set<ScoringRange> scores = tool.scoreBucketsEvenlyLowValueAsHighScore(0, 10, 5, 100.0, 575.0);
        assertNotNull(scores);

        // 0.0 -- 10.0 (5 buckets + 1 best score)
        assertEquals(6, scores.size());

        for ( ScoringRange scr : scores )
        {
            if (scr.getScorePoints() == 10.0)
            {
                assertEquals(100.0, scr.getMax());
                assertEquals(100.0, scr.getMin());
            }
            else if (scr.getScorePoints() == 8.0)
            {
                assertEquals(101, (int)scr.getMin());
                assertEquals(195, (int)scr.getMax());
            }
            else if (scr.getScorePoints() == 6.0)
            {
                assertEquals(195, (int)scr.getMin());
                assertEquals(290, (int)scr.getMax());
            }
            else if (scr.getScorePoints() == 4.0)
            {
                assertEquals(290, (int)scr.getMin());
                assertEquals(384, (int)scr.getMax());
            }
            else if (scr.getScorePoints() == 2.0)
            {
                assertEquals(384, (int)scr.getMin());
                assertEquals(479, (int)scr.getMax());
            }
            else if (scr.getScorePoints() == 0.0)
            {
                assertEquals(575.0, scr.getMax());
                assertEquals(575.0, scr.getMin());
            }
        }
    }

    public void test__scoreSlicesEvenly_small_range() {

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scores = tool.scoreBucketsEvenlyLowValueAsHighScore(0, 10, 3, 0, 2);
        assertNotNull(scores);
        assertEquals(3, scores.size());

        for ( ScoringRange scr : scores )
        {
            if (scr.getScorePoints() == 10.0)
            {
                assertEquals(0.0, scr.getMax());
                assertEquals(0.0, scr.getMin());
            }
            else if (scr.getScorePoints() == 5.0)
            {
                assertEquals(1.0, scr.getMin());
                assertEquals(1.0, scr.getMax());
            }
            else if (scr.getScorePoints() == 4.0)
            {
                assertEquals(2.0, scr.getMin());
                assertEquals(2.0, scr.getMax());
            }
        }
    }

    public void test__scoreBucketsEvenlyHighValueAsHighScore() {

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> ranges = tool.scoreBucketsEvenlyHighValueAsHighScore(0, 10, 10, -1.0, 95.0);
        System.out.println(tool.dumpScoringRanges(ranges, "High value as high score"));
        assertNotNull(ranges);

        for ( ScoringRange scr : ranges )
        {
            if (scr.getScorePoints() == 10.0)
            {
                assertEquals(95.0, scr.getMax());
                assertEquals(95.0, scr.getMin());
            }
            else if (scr.getScorePoints() == 0.0)
            {
                assertEquals(-1.0, scr.getMax());
                assertEquals(-1.0, scr.getMin());
            }
            else if (scr.getScorePoints() == 7.0)
            {
                assertEquals(56.4, scr.getMin());
                assertEquals(65.8, scr.getMax());
            }
        }
    }
}