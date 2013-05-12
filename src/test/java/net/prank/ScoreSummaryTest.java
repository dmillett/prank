package net.prank;

import junit.framework.TestCase;
import java.util.HashSet;
import java.util.Set;

/**
 *
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
        Result result = new Result.ResultBuilder(scoreCardName, 1.0).build();
        simple.addResult(scoreCardName, result);

        assertEquals(1.0, simple.tallyScore());
        assertEquals(1.0, simple.tallyScore(Result.ResultScoreType.ORIGINAL));
        assertEquals(1.0, simple.tallyScore(Result.ResultScoreType.ADJUSTED));
    }

    public void test__tallyScoreFor() {

        String scoreCardName = "ExampleScoreCard";
        ScoreSummary simple = new ScoreSummary("Simple");
        assertNull(simple.tallyScoreFor(scoreCardName));

        Result result = new Result.ResultBuilder(scoreCardName, 1.0).build();
        simple.addResult(scoreCardName, result);

        assertEquals(1.0, simple.tallyScoreFor(scoreCardName));
        assertEquals(1.0, simple.tallyScoreFor(Result.ResultScoreType.ORIGINAL, scoreCardName));
    }
}