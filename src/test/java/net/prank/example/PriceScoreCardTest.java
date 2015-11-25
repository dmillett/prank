package net.prank.example;

import net.prank.core.Prankster;
import net.prank.core.Request;
import net.prank.core.ScoreCard;
import org.junit.Test;

import java.math.BigDecimal;
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
public class PriceScoreCardTest {

    @Test
    public void test__score() {

        PranksterExample pe = new PranksterExample();
        List<ExampleObject> examples = pe.getExamples();
        Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);

        Set<ScoreCard<List<ExampleObject>>> scoreCards = new HashSet<ScoreCard<List<ExampleObject>>>();
        scoreCards.add(new PriceScoreCard(0, 20, 10));

        Prankster<List<ExampleObject>> prankster = new Prankster<List<ExampleObject>>(scoreCards, 1);
        prankster.updateObjectScore(request, 20);

        assertEquals(new BigDecimal("2.0"), examples.get(0).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("16.0"), examples.get(1).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("12.0"), examples.get(2).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("2.0"), examples.get(3).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("20.0"), examples.get(4).getScoreSummary().tallyScore());
        assertEquals(new BigDecimal("8.0"), examples.get(5).getScoreSummary().tallyScore());
    }
}
