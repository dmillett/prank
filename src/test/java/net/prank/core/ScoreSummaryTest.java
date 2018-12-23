package net.prank.core;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
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
public class ScoreSummaryTest {

    @Test
    public void test__tallyScore_null() {

        ScoreSummary foo = new ScoreSummary("Foo");
        assertNull(foo.tallyScore());
    }

    @Test
    public void test__tallyScore_simple_unadjusted_score() {

        ScoreSummary simple = new ScoreSummary("Simple");

        String scoreCardName = "ExampleScoreCard";
        ScoreData.Builder scoreBuilder = new ScoreData.Builder();
        scoreBuilder.setScore(new BigDecimal("1.0"));
        scoreBuilder.setAdjustedScore(new BigDecimal("1.0"));

        Result result = new Result.Builder(scoreCardName, scoreBuilder.build()).build();
        simple.addResult(scoreCardName, result);

        assertEquals(new BigDecimal("1.0"), simple.tallyScore());
        assertEquals(new BigDecimal("1.0"), simple.tallyScore(Result.ResultScoreType.ORIGINAL));
        assertEquals(new BigDecimal("1.0"), simple.tallyScore(Result.ResultScoreType.ADJUSTED));
    }

    @Test
    public void test__tallyScoreFor() {

        String scoreCardName = "ExampleScoreCard";
        ScoreSummary simple = new ScoreSummary("Simple");
        assertNull(simple.tallyScoreFor(scoreCardName));

        ScoreData.Builder scoreBuilder = new ScoreData.Builder();
        scoreBuilder.setScore(new BigDecimal("1.0"));

        Result result = new Result.Builder(scoreCardName, scoreBuilder.build()).build();
        simple.addResult(scoreCardName, result);

        assertEquals(new BigDecimal("1.0"), simple.tallyScoreFor(scoreCardName));
        assertEquals(new BigDecimal("1.0"), simple.tallyScoreFor(Result.ResultScoreType.ORIGINAL, scoreCardName));
    }
}