package net.prank.core;

import junit.framework.TestCase;
import net.prank.core.Result;
import net.prank.core.ScoreSummary;

import java.math.BigDecimal;
import java.util.HashSet;
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
public class ScoreSummaryTest
    extends TestCase {

    public void test__tallyScore_null() {

        ScoreSummary foo = new ScoreSummary("Foo");
        assertNull(foo.tallyScore());

        Set<String> example = new HashSet<String>();
        example.add("ExampleScoreCard");
        assertNull(foo.tallyScore());
    }

    public void test__tallyScore_simple_unadjusted_score() {

        ScoreSummary simple = new ScoreSummary("Simple");

        String scoreCardName = "ExampleScoreCard";
        Result result = new Result.ResultBuilder(scoreCardName, new BigDecimal("1.0")).build();
        simple.addResult(scoreCardName, result);

        assertEquals(1.0, simple.tallyScore());
        assertEquals(1.0, simple.tallyScore(Result.ResultScoreType.ORIGINAL));
        assertEquals(1.0, simple.tallyScore(Result.ResultScoreType.ADJUSTED));
    }

    public void test__tallyScoreFor() {

        String scoreCardName = "ExampleScoreCard";
        ScoreSummary simple = new ScoreSummary("Simple");
        assertNull(simple.tallyScoreFor(scoreCardName));

        Result result = new Result.ResultBuilder(scoreCardName, new BigDecimal("1.0")).build();
        simple.addResult(scoreCardName, result);

        assertEquals(1.0, simple.tallyScoreFor(scoreCardName));
        assertEquals(1.0, simple.tallyScoreFor(Result.ResultScoreType.ORIGINAL, scoreCardName));
    }
}