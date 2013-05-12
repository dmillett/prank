package net.prank.example;

import net.prank.Result;
import net.prank.ScoreCard;
import net.prank.ScoreSummary;

/**
 * A very simple example of a setupScoring card. More complex examples should still be stateless for
 * thread safety. Typically, the higher the setupScoring, the better the result.
 * <p/>
 * The adjustments are just examples of how scoring might be adjusted to make some
 * setupScoring cards more/less important than other setupScoring cards. If machine learning (ML)
 * indicates that price is the most important factor for all customers (or individual),
 * then it should have "heavier" weighting and it's setupScoring should be adjusted (+)
 * <p/>
 * Examples:
 * Price: the lowest price has the highest setupScoring.
 * Shipping cost: how much to ship the item
 * Shipping time: how long it takes an item to ship
 *
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
public class ExampleScoreCard
    implements ScoreCard<ExampleObject> {

    private final String _name = "SolutionPriceScoreCard";

    // Mutable state (single threaded only) -- D
    private final int _scoreAdjustment;
    private final int _positionAdjustment;
    private final double _averageAdjustment;
    private final double _standardDeviationAdjustment;

    public ExampleScoreCard() {

        _scoreAdjustment = 5;
        _positionAdjustment = 3;
        _averageAdjustment = 2;
        _standardDeviationAdjustment = 1.0;
    }

    public ExampleScoreCard(int scoreAdjustment, int positionAdjustment, double averageAdjustment,
                            double standardDeviationAdjustment) {

        _scoreAdjustment = scoreAdjustment;
        _positionAdjustment = positionAdjustment;
        _averageAdjustment = averageAdjustment;
        _standardDeviationAdjustment = standardDeviationAdjustment;
    }

    public ScoreSummary score(ExampleObject scoringObject) {

        int score = scoringObject.getAverageShippingTime() + _scoreAdjustment;
        int position = _positionAdjustment;
        double average = scoringObject.getShippingCost().doubleValue() + _averageAdjustment;
        double standardDeviation = _standardDeviationAdjustment;

        // Simple update
        Result.ResultBuilder resultBuilder = new Result.ResultBuilder(_name, score);
        resultBuilder.position(position).average(average).standardDeviation(standardDeviation);
        Result result = resultBuilder.build();

        scoringObject.getScoreSummary().addResult(_name, result);

        // Ignore the summary for now
        return null;
    }

    public String getName() {
        return _name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExampleScoreCard that = (ExampleScoreCard) o;

        if (!_name.equals(that._name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public String toString() {
        return "ExampleScoreCard{" +
               "_name='" + _name + '\'' +
               '}';
    }
}
