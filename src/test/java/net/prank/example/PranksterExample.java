package net.prank.example;

import net.prank.core.Prankster;
import net.prank.core.Request;
import net.prank.core.ScoreCard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class PranksterExample {

    public void scoreObjects(List<ExampleObject> examples) {

        ScoreCard examplePrice = new PriceScoreCard(0, 20, 10);
        ScoreCard exampleShippingCost = new ShippingCostScoreCard(0, 10, 10);
        ScoreCard exampleShippingTime = new ShippingTimeScoreCard(0, 5, 5);

        Set<ScoreCard> scoreCards = new HashSet<ScoreCard>();
        scoreCards.add(examplePrice);
        scoreCards.add(exampleShippingCost);
        scoreCards.add(exampleShippingTime);

        Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);
        Prankster prankster = new Prankster(scoreCards, 3);
        prankster.updateObjectScore(request, 100);
    }

    public void scoreObjects(List<ExampleObject> examples, Set<ScoreCard> scoreCards) {

        Request<List<ExampleObject>> request = new Request<List<ExampleObject>>(examples);
        Prankster prankster = new Prankster(scoreCards, scoreCards.size());
        prankster.updateObjectScore(request, 20);
    }

    public List<ExampleObject> getExamples() {

        List<ExampleObject> examples = new ArrayList<ExampleObject>();
        examples.add(new ExampleObject(13, new BigDecimal("0.00"), new BigDecimal("13.44")));
        examples.add(new ExampleObject(3, new BigDecimal("4.99"), new BigDecimal("10.39")));
        examples.add(new ExampleObject(9, new BigDecimal("1.99"), new BigDecimal("11.22")));

        examples.add(new ExampleObject(5, new BigDecimal("3.98"), new BigDecimal("13.44")));
        examples.add(new ExampleObject(11, new BigDecimal("0.50"), new BigDecimal("9.99")));
        examples.add(new ExampleObject(8, new BigDecimal("1.99"), new BigDecimal("12.11")));

        return examples;
    }
}
