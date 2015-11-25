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
public class ShippingTimeScoreCard
    implements ScoreCard<List<ExampleObject>> {

    public static final String NAME = "ShippingTimeScoreCard";

    /** Max points for best scoring solution */
    private final double _maxPoints;
    /** Min points for best scoring solution */
    private final double _minPoints;
    /** How many slices */
    private final int _pointSlices;

    public ShippingTimeScoreCard(double minPoints, double maxPoints, int pointSliceCount)  {
        _minPoints = minPoints;
        _maxPoints = maxPoints;
        _pointSlices = pointSliceCount;
    }

    @Override
    public ScoreSummary score(List<ExampleObject> examples) {

        if (examples == null || examples.isEmpty())
        {
            return null;
        }

        List<Long> shippingTimes = getShippingTimes(examples);
        double average = NumericTools.averageForLongs(shippingTimes);
        double standardDeviation = NumericTools.standardDeviationForLongs(average, shippingTimes);
        double grossMax = NumericTools.max(shippingTimes);
        double grossMin = NumericTools.min(shippingTimes);

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scoring = tool.scoreBucketsEvenlyLowValueAsHighScore(_minPoints, _maxPoints,
                                                                               _pointSlices, grossMin, grossMax);

        updateSolutionsWithScore(examples, scoring, average, standardDeviation, tool);

        return null;
    }

    @Override
    public ScoreSummary scoreWith(List<ExampleObject> examples, RequestOptions options) {

        if (examples == null || examples.isEmpty())
        {
            return null;
        }

        List<Long> shippingTimes = getShippingTimes(examples);
        double average = NumericTools.averageForLongs(shippingTimes);
        double standardDeviation = NumericTools.standardDeviationForLongs(average, shippingTimes);
        double grossMax = NumericTools.max(shippingTimes);
        double grossMin = NumericTools.min(shippingTimes);

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scoring = tool.scoreBucketsEvenlyLowValueAsHighScore(options.getMinPoints(),
                                                                               options.getMaxPoints(),
                                                                               options.getBucketCount(),
                                                                               grossMin,
                                                                               grossMax);

        updateSolutionsWithScore(examples, scoring, average, standardDeviation, tool);
        return null;
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
            int shippingTime = example.getAverageShippingTime();
            double score = scoringTool.getScoreFromRange(shippingTime, scoringRange);

            // Calculate stats with primitives for performance
            ScoreData.Builder scoreBuilder = new ScoreData.Builder();
            scoreBuilder.setScore(new BigDecimal(String.valueOf(score)));

            Statistics.Builder statsBuilder = new Statistics.Builder();
            statsBuilder.setAverage(new BigDecimal(String.valueOf(average)));
            statsBuilder.setStandardDeviation(new BigDecimal(String.valueOf(standardDeviation)));

            Result.Builder rb = new Result.Builder(NAME, scoreBuilder.build());
            rb.setPosition(new Indices(i));
            rb.setOriginal(shippingTime);
            rb.setStatistics(statsBuilder.build());
            Result result = rb.build();

            example.getScoreSummary().addResult(NAME, result);
            i++;
        }
    }

    private List<Long> getShippingTimes(List<ExampleObject> examples) {

        List<Long> shippingTimes = new ArrayList<Long>(examples.size());

        for ( ExampleObject example : examples )
        {
            shippingTimes.add((long)example.getAverageShippingTime());
        }

        return shippingTimes;
    }
}
