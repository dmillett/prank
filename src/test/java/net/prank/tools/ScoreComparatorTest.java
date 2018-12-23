package net.prank.tools;

import net.prank.core.Result;
import net.prank.example.ExampleObject;

import net.prank.example.PranksterExample;
import net.prank.example.PriceScoreCard;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
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
public class ScoreComparatorTest {

    @Test
    public void test__ScoreComparator_no_args() {

        PranksterExample pranksterExample = new PranksterExample();
        List<ExampleObject> examples = pranksterExample.getExamples();
        pranksterExample.updateObjectsWithScores(examples);
        Collections.sort(examples, new ScoreComparator());

        assertEquals(new BigDecimal("30.0"), examples.get(0).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("22.0"), examples.get(1).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("20.0"), examples.get(2).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("17.0"), examples.get(3).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("13.0"), examples.get(4).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("8.0"), examples.get(5).getScoreSummary().tallyScore());
    }

    @Test
    public void test__ScoreComparator_one_args() {

        PranksterExample pranksterExample = new PranksterExample();
        List<ExampleObject> examples = pranksterExample.getExamples();
        pranksterExample.updateObjectsWithScores(examples);
        Collections.sort(examples, new ScoreComparator(Result.ResultScoreType.ORIGINAL));

        assertEquals(new BigDecimal("30.0"), examples.get(0).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("22.0"), examples.get(1).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("20.0"), examples.get(2).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("17.0"), examples.get(3).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("13.0"), examples.get(4).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("8.0"), examples.get(5).getScoreSummary().tallyScore());
    }

    @Test
    public void test__ScoreComparator_one_card() {

        PranksterExample pranksterExample = new PranksterExample();
        List<ExampleObject> examples = pranksterExample.getExamples();
        pranksterExample.updateObjectsWithScores(examples);
        Set<String> priceCard = new HashSet<>();
        priceCard.add(PriceScoreCard.NAME);
        Collections.sort(examples, new ScoreComparator(priceCard));

        assertEquals(new BigDecimal("9.99"), examples.get(0).getPrice());
        assertEquals(new BigDecimal("10.39"), examples.get(1).getPrice());
        assertEquals(new BigDecimal("11.22"), examples.get(2).getPrice());
        assertEquals(new BigDecimal("12.11"), examples.get(3).getPrice());
        assertEquals(new BigDecimal("13.44"), examples.get(4).getPrice());
        assertEquals(new BigDecimal("13.44"), examples.get(5).getPrice());
    }

    @Test
    public void test__ScoreComparator_no_args_updateObjectsWithScores() {

        PranksterExample pranksterExample = new PranksterExample();
        List<ExampleObject> examples = pranksterExample.getExamples();
        pranksterExample.updateObjectsWithScores(examples);

        examples.sort(new ScoreComparator());

        assertEquals(new BigDecimal("30.0"), examples.get(0).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("22.0"), examples.get(1).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("20.0"), examples.get(2).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("17.0"), examples.get(3).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("13.0"), examples.get(4).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("8.0"), examples.get(5).getScoreSummary().tallyScore());
    }

    @Test
    public void test__ScoreComparator_one_card_updateObjectsWithScores() {

        PranksterExample pranksterExample = new PranksterExample();
        List<ExampleObject> examples = pranksterExample.getExamples();
        pranksterExample.updateObjectsWithScores(examples);

        Set<String> priceCard = new HashSet<>();
        priceCard.add(PriceScoreCard.NAME);
        examples.sort(new ScoreComparator(priceCard));

        assertEquals(new BigDecimal("9.99"), examples.get(0).getPrice());
        assertEquals(new BigDecimal("10.39"), examples.get(1).getPrice());
        assertEquals(new BigDecimal("11.22"), examples.get(2).getPrice());
        assertEquals(new BigDecimal("12.11"), examples.get(3).getPrice());
        assertEquals(new BigDecimal("13.44"), examples.get(4).getPrice());
        assertEquals(new BigDecimal("13.44"), examples.get(5).getPrice());
    }
}
