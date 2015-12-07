package net.prank.example;

import net.prank.core.Indices;
import net.prank.core.ScoreData;
import net.prank.core.Statistics;
import net.prank.tools.NumericTools;
import net.prank.core.RequestOptions;
import net.prank.core.Result;
import net.prank.core.ScoreCard;
import net.prank.core.ScoreSummary;
import net.prank.tools.ScoringRange;
import net.prank.tools.ScoringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class PriceScoreCard
    implements ScoreCard<List<ExampleObject>> {

    public static final String NAME = "PriceScoreCard";

    /** Max points for best scoring solution */
     private final double _maxPoints;
    /** Min points for best scoring solution */
    private final double _minPoints;
    /** How many slices */
    private final int _pointSlices;

    public PriceScoreCard(double minPoints, double maxPoints, int pointSliceCount)  {
        _minPoints = minPoints;
        _maxPoints = maxPoints;
        _pointSlices = pointSliceCount;
    }

    // Should use 'updateObjectsWithScore()' instead of this (the name is misleading and dprecated)
    public ScoreSummary score(List<ExampleObject> solutions) {
        updateObjectsWithScore(solutions);
        return null; // Better to throw UnsupportedHere until a better encapsulation comes along
    }

    @Override
    public ScoreSummary scoreWith(List<ExampleObject> solutions, RequestOptions options) {
        updateObjectsWithScore(solutions, options);
        return null; // Better to throw UnsupportedHere until a better encapsulation comes along
    }

    @Override
    public void updateObjectsWithScore(List<ExampleObject> examples) {

        if (examples == null || examples.isEmpty())
        {
            return;
        }

        Map<Data, Double> data = determinePrices(examples);
        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scoring = tool.scoreBucketsEvenlyLowValueAsHighScore(_minPoints,
                                                                               _maxPoints,
                                                                               _pointSlices,
                                                                               data.get(Data.GROSS_MIN),
                                                                               data.get(Data.GROSS_MAX));

        updateSolutionsWithScore(examples, scoring, data.get(Data.AVERAGE), data.get(Data.STD_DEVIATION), tool);
    }

    @Override
    public void updateObjectsWithScore(List<ExampleObject> examples, RequestOptions options) {

        if (examples == null || examples.isEmpty())
        {
            return;
        }

        Map<Data, Double> data = determinePrices(examples);
        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scoring = tool.scoreBucketsEvenlyLowValueAsHighScore(options.getMinPoints(),
                                                                               options.getMaxPoints(),
                                                                               options.getBucketCount(),
                                                                               data.get(Data.GROSS_MIN),
                                                                               data.get(Data.GROSS_MAX));

        updateSolutionsWithScore(examples, scoring, data.get(Data.AVERAGE), data.get(Data.STD_DEVIATION), tool);
    }

    public String getName() {
        return NAME;
    }

    private enum Data {
        AVERAGE,
        STD_DEVIATION,
        GROSS_MAX,
        GROSS_MIN,
    }

    private Map<Data, Double> determinePrices(List<ExampleObject> examples) {

        Map<Data, Double> data = new HashMap<Data, Double>();
        List<Double> totalShippingCosts = getPrices(examples);
        double average = NumericTools.averageForDoubles(totalShippingCosts);

        data.put(Data.AVERAGE, average);
        data.put(Data.STD_DEVIATION, NumericTools.standardDeviationForDoubles(average, totalShippingCosts));
        data.put(Data.GROSS_MAX, NumericTools.max(totalShippingCosts));
        data.put(Data.GROSS_MIN, NumericTools.min(totalShippingCosts));
        return data;
    }

    void updateSolutionsWithScore(List<ExampleObject> solutions, Set<ScoringRange> scoringRange,
                                  double average, double standardDeviation, ScoringTool scoringTool) {

        int i = 0;
        for ( ExampleObject solution : solutions )
        {
            if ( solution.getPrice() == null )
            {
                i++;
                continue;
            }

            double totalPrice = solution.getPrice().doubleValue();
            double score = scoringTool.getScoreFromRange(totalPrice, scoringRange);

            // Calculate stats with primitives for performance
            ScoreData.Builder scoreBuilder = new ScoreData.Builder();
            scoreBuilder.setScore(new BigDecimal(String.valueOf(score)));

            Statistics.Builder statsBuilder = new Statistics.Builder();
            statsBuilder.setAverage(new BigDecimal(String.valueOf(average)));
            statsBuilder.setStandardDeviation(new BigDecimal(String.valueOf(standardDeviation)));

            Result.Builder rb = new Result.Builder(NAME, scoreBuilder.build());
            rb.setPosition(new Indices(i));
            rb.setOriginal(totalPrice);
            rb.setStatistics(statsBuilder.build());
            Result result = rb.build();

            solution.getScoreSummary().addResult(NAME, result);
            i++;
        }
    }


    private List<Double> getPrices(List<ExampleObject> solutions) {

        List<Double> prices = new ArrayList<Double>(solutions.size());

        for ( ExampleObject solution : solutions )
        {
            if ( solution.getPrice() != null )
            {
                double price = solution.getPrice().doubleValue();
                prices.add(price);
            }
        }

        return prices;
    }
}
