package net.prank.example;

import net.prank.core.Indices;
import net.prank.core.RequestOptions;
import net.prank.core.Result;
import net.prank.core.ScoreCard;
import net.prank.core.ScoreData;
import net.prank.core.ScoreSummary;
import net.prank.core.Statistics;
import net.prank.tools.NumericTools;
import net.prank.tools.ScoringRange;
import net.prank.tools.ScoringTool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ShippingCostScoreCard
    implements ScoreCard<List<ExampleObject>> {

    public static final String NAME = "ShippingCostScoreCard";

    /** Max points for best scoring solution */
    private final double _maxPoints;
    /** Min points for best scoring solution */
    private final double _minPoints;
    /** How many slices */
    private final int _pointSlices;

    public ShippingCostScoreCard(double minPoints, double maxPoints, int pointSliceCount)  {
        _minPoints = minPoints;
        _maxPoints = maxPoints;
        _pointSlices = pointSliceCount;
    }

    // Could also return null or just do a misleading update
    @Override
    public ScoreSummary score(List<ExampleObject> examples) {
        updateObjectsWithScore(examples);
        return null; // Better to throw UnsupportedHere until a better encapsulation comes along
    }

    // Could also return null or just do a misleading update
    @Override
    public ScoreSummary scoreWith(List<ExampleObject> examples, RequestOptions options) {
        updateObjectsWithScore(examples, options);
        return null; // Better to throw UnsupportedHere until a better encapsulation comes along
    }

    private enum Data {
        AVERAGE,
        STD_DEVIATION,
        GROSS_MAX,
        GROSS_MIN,
    }

    private Map<Data, Double> determineShippingData(List<ExampleObject> examples) {

        Map<Data, Double> data = new HashMap<Data, Double>();
        List<Double> totalShippingCosts = getShippingCosts(examples);
        double average = NumericTools.averageForDoubles(totalShippingCosts);

        data.put(Data.AVERAGE, average);
        data.put(Data.STD_DEVIATION, NumericTools.standardDeviationForDoubles(average, totalShippingCosts));
        data.put(Data.GROSS_MAX, NumericTools.max(totalShippingCosts));
        data.put(Data.GROSS_MIN, NumericTools.min(totalShippingCosts));
        return data;
    }

    @Override
    public void updateObjectsWithScore(List<ExampleObject> examples) {

        if (examples == null || examples.isEmpty())
        {
            return;
        }

        Map<Data, Double> data = determineShippingData(examples);
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

        Map<Data, Double> data = determineShippingData(examples);
        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scoring = tool.scoreBucketsEvenlyLowValueAsHighScore(options.getMinPoints(),
                                                                               options.getMaxPoints(),
                                                                               options.getBucketCount(),
                                                                               data.get(Data.GROSS_MIN),
                                                                               data.get(Data.GROSS_MAX));

        updateSolutionsWithScore(examples, scoring, data.get(Data.AVERAGE), data.get(Data.STD_DEVIATION), tool);
    }

    @Override
    public String getName() {
        return NAME;
    }

    void updateSolutionsWithScore(List<ExampleObject> examples, Set<ScoringRange> scoringRange,
                                  double average, double standardDeviation, ScoringTool scoringTool) {

        int i = 0;

        for ( ExampleObject example : examples )
        {
            if ( example.getShippingCost() == null )
            {
                i++;
                continue;
            }

            double shippingCost = example.getShippingCost().doubleValue();
            double score = scoringTool.getScoreFromRange(shippingCost, scoringRange);

            // Calculate stats with primitives for performance/immutability
            ScoreData.Builder scoreBuilder = new ScoreData.Builder();
            scoreBuilder.setScore(new BigDecimal(String.valueOf(score)));

            Statistics.Builder statsBuilder = new Statistics.Builder();
            statsBuilder.setAverage(new BigDecimal(String.valueOf(average)));
            statsBuilder.setStandardDeviation(new BigDecimal(String.valueOf(standardDeviation)));

            Result.Builder rb = new Result.Builder(NAME, scoreBuilder.build());
            rb.setPosition(new Indices(i));
            rb.setOriginal(shippingCost);
            rb.setStatistics(statsBuilder.build());
            Result result = rb.build();

            example.getScoreSummary().addResult(NAME, result);
            i++;
        }
    }

    private List<Double> getShippingCosts(List<ExampleObject> examples) {

        List<Double> shippingCosts = new ArrayList<Double>(examples.size());

        for ( ExampleObject example : examples )
        {
            if ( example.getShippingCost() != null )
            {
                double shippingCost = example.getShippingCost().doubleValue();
                shippingCosts.add(shippingCost);
            }
        }

        return shippingCosts;
    }
}
