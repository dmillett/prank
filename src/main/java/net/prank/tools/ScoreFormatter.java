package net.prank.tools;

import net.prank.core.Indices;
import net.prank.core.Result;
import net.prank.core.ScoreData;
import net.prank.core.ScoreSummary;
import net.prank.core.Statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;

/**
 * Tools to format Results, ScoreData, and Statistics --
 * feed this back to logs/elsewhere for analysis.
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
public class ScoreFormatter {

    public static final String DEFAULT_INTERNAL_DELIM = DELIM.COLON.get();
    public static final String DEFAULT_SEPARATOR = DELIM.COMMA.get();
    public static final String DEFAULT_CARD_SEPARATOR = DELIM.SEMI_COLON.get();
    public static final int DEFAULT_SCALE = 4;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /**
     * Dumps the ScoreSummary name followed by each ScoreCard result (see dumpResults()).
     * Example: BookSellersScoring:scoreResult 1;scoreResult2;scoreResult3
     */
    public String dumpScoreSummary(ScoreSummary summary) {

        StringBuilder sb = new StringBuilder(summary.getName());
        sb.append(DEFAULT_INTERNAL_DELIM);

        for ( Map.Entry<String, Result> entry : summary.getResults().entrySet() )
        {
            sb.append(dumpResult(entry.getValue())).append(DEFAULT_CARD_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * Dumps a result with the following format:
     * scoreCardName,value that was scored, sort indices, score data, statistics
     * Where indices, score data, and statistics have internal delimiters (default ":")
     */
    public String dumpResult(Result result) {

        if (result == null)
        {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(result.getScoreCardName()).append(DEFAULT_SEPARATOR)
          .append(result.getScoredValue()).append(DEFAULT_SEPARATOR)
          .append(dumpIndices(result.getPosition())).append(DEFAULT_SEPARATOR)
          .append(dumpScoreData(result.getScoreData())).append(DEFAULT_SEPARATOR)
          .append(dumpStatistics(result.getStatistics()));

        return sb.toString();
    }

    /** originalIndex:adjustedIndex */
    public String dumpIndices(Indices indices) {
        return dumpIndices(indices, DEFAULT_INTERNAL_DELIM);
    }

    /** Similar to dumpIndices(indices), except with specific delimiter */
    public String dumpIndices(Indices indices, String delimiter) {

        if (indices == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Iterator<Integer> it = indices.getIndices().iterator();
        while ( it.hasNext() )
        {
            int current = it.next();
            sb.append(current);

            if ( it.hasNext() )
            {
                sb.append(delimiter);
            }
        }

        return sb.toString();
    }

    /** Uses DEFAULT_INTERNAL_DELIM, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE */
    public String dumpScoreData(ScoreData scoreData) {
        return dumpScoreData(scoreData, DEFAULT_INTERNAL_DELIM, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /** Uses DEFAULT_INTERNAL_DELIM, DEFAULT_ROUNDING_MODE */
    public String dumpScoreData(ScoreData scoreData, int scale) {
        return dumpScoreData(scoreData, DEFAULT_INTERNAL_DELIM, scale, DEFAULT_ROUNDING_MODE);
    }

    /** Uses DEFAULT_INTERNAL_DELIM */
    public String dumpScoreData(ScoreData scoreData, int scale, RoundingMode roundingMode) {
        return dumpScoreData(scoreData, DEFAULT_INTERNAL_DELIM, scale, roundingMode);
    }

    /**
     * Colon delimited output of ScoreData using DEFAULT_SCALE and DEFAULT_ROUNDING_MODE
     *
     * @param scoreData
     * @return "null" for null 'scoreData', otherwise returns score:adj_score:min_pts:max_pts:buckets
     */
    public String dumpScoreData(ScoreData scoreData, String delimiter, int scale, RoundingMode roundingMode) {

        if (scoreData == null)
        {
            return "null";
        }

        String delim = delimiter != null ? delimiter : DEFAULT_INTERNAL_DELIM;

        StringBuilder sb = new StringBuilder();
        addToBuilder(sb, scoreData.getScore(), delim, scale, roundingMode);
        addToBuilder(sb, scoreData.getAdjustedScore(), delim, scale, roundingMode);
        addToBuilder(sb, scoreData.getMinPoints(), delim, scale, roundingMode);
        addToBuilder(sb, scoreData.getMaxPoints(), delim, scale, roundingMode);
        sb.append(scoreData.getBuckets());

        return sb.toString();
    }

    /** Uses DEFAULT_INTERNAL_DELIM, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE */
    public String dumpStatistics(Statistics statistics) {
        return dumpStatistics(statistics, DEFAULT_INTERNAL_DELIM, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    /** Uses DEFAULT_INTERNAL_DELIM and DEFAULT_ROUNDING_MODE */
    public String dumpStatistics(Statistics statistics, int scale) {
        return dumpStatistics(statistics, DEFAULT_INTERNAL_DELIM, scale, DEFAULT_ROUNDING_MODE);
    }

    /** Uses DEFAULT_INTERNAL_DELIM */
    public String dumpStatistics(Statistics statistics, int scale, RoundingMode rm) {
        return dumpStatistics(statistics, DEFAULT_INTERNAL_DELIM, scale, rm);
    }

    /**
     * Colon delimited output of ScoreData
     *
     * Sample output using DEFAULT_SCALE and DEFAULT_ROUNDING_MODE:
     * 4.50:1.50:1.70:1.90
     *
     * Any null Statistics value is represented by an empty String.
     * 4.50:1.50:::  // null median_dev, std_dev
     *
     * @param statistics
     * @return "null" for null 'statistics', otherwise returns avg:mean_dev:median_dev:std_dev
     */
    public String dumpStatistics(Statistics statistics, String delimiter, int scale, RoundingMode rm) {

        if (statistics == null)
        {
            return "null";
        }

        String delim = delimiter != null ? delimiter : DEFAULT_INTERNAL_DELIM;

        StringBuilder sb = new StringBuilder();
        addToBuilder(sb, statistics.getAverage(), delim, scale, rm);
        addToBuilder(sb, statistics.getMeanDeviation(), delim, scale, rm);
        addToBuilder(sb, statistics.getMedianDeviation(), delim, scale, rm);
        addToBuilder(sb, statistics.getStandardDeviation(), DELIM.EMPTY.get(), scale, rm);

        return sb.toString();
    }

    /**  */
    private void addToBuilder(StringBuilder sb, BigDecimal value, String delim, Integer scale, RoundingMode mode) {

        RoundingMode rm = mode != null ? mode : RoundingMode.HALF_EVEN;

        if (value != null)
        {
            if (scale != null)
            {
                sb.append(value.setScale(scale, rm)).append(delim);
            }
            else
            {
                sb.append(value).append(delim);
            }
        }
        else
        {
            sb.append(delim);
        }
    }
}
