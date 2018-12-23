package net.prank.core;

import net.prank.example.ExampleObject;
import net.prank.example.ExampleScoreCard;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
public class PranksterTest {

    private static final double DELTA = 1e-10;

    @Test
    public void test__execute() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 2, 4.0, 50.0);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));

        RequestOptions options = new RequestOptions.RequestOptionsBuilder().build();
        Map<String, RequestOptions> optionsMap = new HashMap<>();
        optionsMap.put(exampleScoreCard.getName(), options);

        Request<ExampleObject> exampleRequest = new Request<>(exampleObject, optionsMap);
        Set<Future<Result>> futureResult = prankster.setupScoring(exampleRequest);

        for (Future<Result> future : futureResult)
        {
            try
            {
                // Should return quickly, they were already submitted to the pool
                future.get(50,TimeUnit.MILLISECONDS);
            }
            catch (Exception e)
            {
                fail("Should not");
            }
        }

        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);

        assertEquals(new BigDecimal("5"), result.getScoreData().getScore());
        assertEquals(2, result.getPosition().getOriginalIndex());
        assertEquals(9.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(50.0, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    @Test
    public void test__execute_updateObjectsWithScores() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 2, 4.0, 50.0);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));

        RequestOptions options = new RequestOptions.RequestOptionsBuilder().build();
        Map<String, RequestOptions> optionsMap = new HashMap<>();
        optionsMap.put(exampleScoreCard.getName(), options);

        Request<ExampleObject> exampleRequest = new Request<>(exampleObject, optionsMap);
        Set<Prankster.ScoringFuture> futureResult = prankster.buildScoringUpdateFutures(exampleRequest, 50);

        for (Prankster.ScoringFuture scoringFuture : futureResult)
        {
            try
            {
                // Should return quickly, they were already submitted to the pool
                scoringFuture.getFuture().get(50, TimeUnit.MILLISECONDS);
            }
            catch (Exception e)
            {
                fail("Should not");
            }
        }

        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);

        assertEquals(new BigDecimal("5"), result.getScoreData().getScore());
        assertEquals(2, result.getPosition().getOriginalIndex());
        assertEquals(9.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(50.0, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    @Test
    public void test__updateObjectScore() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 2, 4.0, 50.0);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));
        Request<ExampleObject> exampleRequest = new Request<>(exampleObject);
        prankster.updateObjectsWithScores(exampleRequest, 50);
        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);
        assertEquals(5.0, result.getScoreData().getScore() .doubleValue(), DELTA);
        assertEquals(2, result.getPosition().getOriginalIndex());
        assertEquals(9.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(50.0, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    @Test
    public void test__updateObjectsWithScores() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 2, 4.0, 50.0);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));
        Request<ExampleObject> exampleRequest = new Request<>(exampleObject);

        prankster.updateObjectsWithScores(exampleRequest, 50);
        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);
        assertEquals(5.0, result.getScoreData().getScore() .doubleValue(), DELTA);
        assertEquals(2, result.getPosition().getOriginalIndex());
        assertEquals(9.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(50.0, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    @Test
    public void test__execute_disabled() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));

        RequestOptions options = new RequestOptions.RequestOptionsBuilder().setEnabledB(false).build();
        Map<String, RequestOptions> optionsMap = new HashMap<>();
        optionsMap.put(exampleScoreCard.getName(), options);

        Request<ExampleObject> exampleRequest = new Request<>(exampleObject, optionsMap);
        prankster.updateObjectsWithScores(exampleRequest, 50);
        assertEquals(0, exampleObject.getScoreSummary().getResults().size());
    }

    @Test
    public void test__execute_disabled_updateObjectsWithScores() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);

        RequestOptions options = new RequestOptions.RequestOptionsBuilder().setEnabledB(false).build();
        Map<String, RequestOptions> optionsMap = new HashMap<>();
        optionsMap.put(exampleScoreCard.getName(), options);

        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));
        Request<ExampleObject> exampleRequest = new Request<ExampleObject>(exampleObject, optionsMap);
        prankster.updateObjectsWithScores(exampleRequest, 50);
        assertEquals(0, exampleObject.getScoreSummary().getResults().size());
    }

    @Test
    public void test__execute_enabled() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("55.00"));
        Request<ExampleObject> exampleRequest = new Request<>(exampleObject);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        prankster.updateObjectsWithScores(exampleRequest, 50);
        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);
        assertEquals(5.0, result.getScoreData().getScore().doubleValue(), DELTA);
        assertEquals(4, result.getPosition().getOriginalIndex());
        assertEquals(10.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(0.75, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    @Test
    public void test__execute_enabled_updateObjectsWithScores() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("55.00"));

        Request<ExampleObject> exampleRequest = new Request<>(exampleObject);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        prankster.updateObjectsWithScores(exampleRequest, 50);

        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);

        assertEquals(5.0, result.getScoreData().getScore().doubleValue(), DELTA);
        assertEquals(4, result.getPosition().getOriginalIndex());
        assertEquals(10.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(0.75, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    @Test
    public void test__determineTimeout_default() {

        long defaultTimeout = 10;
        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);

        long timeoutNull = prankster.determineTimeout(defaultTimeout, exampleScoreCard.getName(), null);
        assertEquals(10, timeoutNull);

        Map<String, RequestOptions> optionsMap = new HashMap<>();
        long timeoutEmpty = prankster.determineTimeout(defaultTimeout, exampleScoreCard.getName(), optionsMap);
        assertEquals(10, timeoutEmpty);
    }

    @Test
    public void test__determineTimeout_per_request() {

        long defaultTimeout = 10;
        RequestOptions options = new RequestOptions.RequestOptionsBuilder().setTimeoutMillisB(1).build();
        Map<String, RequestOptions> optionsMap = new HashMap<>();
        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        optionsMap.put(exampleScoreCard.getName(), options);

        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        long timeout = prankster.determineTimeout(defaultTimeout, exampleScoreCard.getName(), optionsMap);

        assertEquals(1, timeout);
    }

    @Test
    public void test__determineTimeout_per_request_disabled() {

        long defaultTimeout = 10;
        RequestOptions options = new RequestOptions.RequestOptionsBuilder().setTimeoutMillisB(1).setEnabledB(false).build();
        Map<String, RequestOptions> optionsMap = new HashMap<>();
        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        optionsMap.put(exampleScoreCard.getName(), options);

        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        long timeout = prankster.determineTimeout(defaultTimeout, exampleScoreCard.getName(), optionsMap);

        assertEquals(10, timeout);
    }

    private Prankster<ExampleObject> buildPrankster(ScoreCard... scoreCard) {

        Set<ScoreCard<ExampleObject>> scoreCards = new HashSet<>();
        for (ScoreCard card : scoreCard)
        {
            scoreCards.add(card);
        }

        return new Prankster<>(scoreCards, 1);
    }
}