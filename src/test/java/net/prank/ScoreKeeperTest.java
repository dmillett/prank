package net.prank;

import junit.framework.TestCase;
import net.prank.example.ExampleObject;
import net.prank.example.ExampleScoreCard;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

/**
 *
 */
public class ScoreKeeperTest
        extends TestCase {

    public void test__execute() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 2, 4.0, 50.0);
        ScoreKeeper<ExampleObject> scoreKeeper = buildScoreKeeper(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));
        Request<ExampleObject> example = new Request<ExampleObject>(exampleObject);

        Set<Future<Result>> futureResult = scoreKeeper.score(example);

        for (Future<Result> future : futureResult) {
            try {
                // Should return quickly, they were already submitted to the pool
                future.get(25, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fail("Should not");
            }
        }

        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);

        assertEquals(5.0, result.getScore());
        assertEquals(2, result.getPosition());
        assertEquals(9.0, result.getAverage());
        assertEquals(50.0, result.getStandardDeviation());
    }

    public void test__execute_disabled() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        ScoreKeeper<ExampleObject> scoreKeeper = buildScoreKeeper(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));

        Request<ExampleObject> example = new Request<ExampleObject>(exampleObject);
        example.addDisabled(exampleScoreCard);

        Set<Future<Result>> futureResult = scoreKeeper.score(example);

        for (Future<Result> future : futureResult) {
            try {
                // Should return quickly, they were already submitted to the pool
                future.get(5, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fail("Should not");
            }
        }

        assertEquals(0, exampleObject.getScoreSummary().getResults().size());
    }

    public void test__execute_enabled_disabled() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        ScoreKeeper<ExampleObject> scoreKeeper = buildScoreKeeper(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));

        Request<ExampleObject> example = new Request<ExampleObject>(exampleObject);
        example.addEnabled(exampleScoreCard);
        example.addDisabled(exampleScoreCard);

        Set<Future<Result>> futureResult = scoreKeeper.score(example);

        for (Future<Result> future : futureResult) {
            try {
                // Should return quickly, they were already submitted to the pool
                future.get(5, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fail("Should not");
            }
        }

        assertEquals(0, exampleObject.getScoreSummary().getResults().size());
    }

    public void test__execute_enabled() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        ScoreCard<Integer> integerScoreCard = mock(ScoreCard.class);

        ScoreKeeper<ExampleObject> scoreKeeper = buildScoreKeeper(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("55.00"));

        Request<ExampleObject> example = new Request<ExampleObject>(exampleObject);
        example.addEnabled(exampleScoreCard);

        Set<Future<Result>> futureResult = scoreKeeper.score(example);

        for (Future<Result> future : futureResult) {
            try {
                // Should return quickly, they were already submitted to the pool
                future.get(5, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fail("Should not");
            }
        }

        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);

        assertEquals(5.0, result.getScore());
        assertEquals(4, result.getPosition());
        assertEquals(10.0, result.getAverage());
        assertEquals(0.75, result.getStandardDeviation());
    }

    private ScoreKeeper<ExampleObject> buildScoreKeeper(ScoreCard... scoreCard) {

        Set<ScoreCard<ExampleObject>> scoreCards = new HashSet<ScoreCard<ExampleObject>>();
        for (ScoreCard card : scoreCard) {
            scoreCards.add(card);
        }

        return new ScoreKeeper<ExampleObject>(scoreCards, 1);
    }
}