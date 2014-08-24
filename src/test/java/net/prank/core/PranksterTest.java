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
import static org.mockito.Mockito.mock;

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
        Map<String, RequestOptions> optionsMap = new HashMap<String, RequestOptions>();
        optionsMap.put(exampleScoreCard.getName(), options);

        Request<ExampleObject> exampleRequest = new Request<ExampleObject>(exampleObject, optionsMap);
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
    public void test__updateObjectScore() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 2, 4.0, 50.0);
        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("50.00"));
        Request<ExampleObject> exampleRequest = new Request<ExampleObject>(exampleObject);

        prankster.updateObjectScore(exampleRequest, 25);

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
        Map<String, RequestOptions> optionsMap = new HashMap<String, RequestOptions>();
        optionsMap.put(exampleScoreCard.getName(), options);

        Request<ExampleObject> exampleRequest = new Request<ExampleObject>(exampleObject, optionsMap);

        prankster.updateObjectScore(exampleRequest, 10);

        assertEquals(0, exampleObject.getScoreSummary().getResults().size());
    }

    @Test
    public void test__execute_enabled() {

        ScoreCard<ExampleObject> exampleScoreCard = new ExampleScoreCard(2, 4, 5.0, 0.75);
        ScoreCard<Integer> integerScoreCard = mock(ScoreCard.class);

        Prankster<ExampleObject> prankster = buildPrankster(exampleScoreCard);
        ExampleObject exampleObject = new ExampleObject(3, new BigDecimal("5.00"), new BigDecimal("55.00"));

        Request<ExampleObject> exampleRequest = new Request<ExampleObject>(exampleObject);

        prankster.updateObjectScore(exampleRequest, 5);

        assertEquals(1, exampleObject.getScoreSummary().getResults().size());

        Result result = exampleObject.getScoreSummary().getResultByScoreCard(exampleScoreCard.getName());
        assertNotNull(result);

        assertEquals(5.0, result.getScoreData().getScore().doubleValue(), DELTA);
        assertEquals(4, result.getPosition().getOriginalIndex());
        assertEquals(10.0, result.getStatistics().getAverage().doubleValue(), DELTA);
        assertEquals(0.75, result.getStatistics().getStandardDeviation().doubleValue(), DELTA);
    }

    private Prankster<ExampleObject> buildPrankster(ScoreCard... scoreCard) {

        Set<ScoreCard<ExampleObject>> scoreCards = new HashSet<ScoreCard<ExampleObject>>();
        for (ScoreCard card : scoreCard)
        {
            scoreCards.add(card);
        }

        return new Prankster<ExampleObject>(scoreCards, 1);
    }
}