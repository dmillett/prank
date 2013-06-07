package net.prank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provide a mechanism to store and/or process results associated with a given
 * object. In all cases, getTally().. will return a 'null' if there are no matching
 * <p/>
 * <p/>
 * To open up flexibility (in future):
 * 1) Summary (T,V) interface.
 * 2) ScoreTally (V) interface
 *
 * @author dmillett
 *         <p/>
 *         Copyright 2012 David Millett
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <p/>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <p/>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class ScoreSummary {

    private final Map<String, Result> _results;
    private final String _name;

    public ScoreSummary(String name) {
        _name = name;
        _results = new HashMap<String, Result>();
    }

    public void addResult(String key, Result result) {
        _results.put(key, result);
    }

    public Result getResultByScoreCard(String scoreCardName) {
        return _results.get(scoreCardName);
    }

    public Map<String, Result> getResults() {
        return _results;
    }

    public String getName() {
        return _name;
    }

    /**
     * Add up all the scores for each Result.
     *
     * @return The sum of all Result.getScore() or null
     */
    public Double tallyScore() {
        return tallyScore(_results.keySet(), Result.ResultScoreType.ORIGINAL);
    }

    public Double tallyScore(Result.ResultScoreType scoreType) {
        return tallyScore(_results.keySet(), scoreType);
    }

    /**
     * Get the setupScoring for a subset of ScoreCards by name.
     *
     * @param scoreCardNames
     * @return
     */
    public Double tallyScoreFor(Set<String> scoreCardNames) {
        return tallyScoreFor(scoreCardNames, Result.ResultScoreType.ORIGINAL);
    }

    public Double tallyScoreFor(Set<String> scoreCardNames, Result.ResultScoreType scoreType) {

        if (scoreCardNames == null)
        {
            return null;
        }

        Set<String> scoreCards = findScoreCardsByName(scoreCardNames);
        return tallyScore(scoreCards, scoreType);
    }

    /**
     * Tally
     *
     * @param scoreCardNames
     * @return null if there are no matching ScoreCards, otherwise the tally(+)
     */
    public Double tallyScore(Set<String> scoreCardNames, Result.ResultScoreType scoreType) {

        if (scoreCardNames.isEmpty())
        {
            return null;
        }

        Double tally = null;

        for (String scoreCardName : scoreCardNames)
        {
            if (scoreCardName == null)
            {
                continue;
            }

            Result result = _results.get(scoreCardName);

            if (result != null)
            {
                if (tally == null)
                {
                    tally = 0.0;
                }

                if (scoreType.equals(Result.ResultScoreType.ORIGINAL))
                {
                    tally += result.getScore();
                }
                else if (scoreType.equals(Result.ResultScoreType.ADJUSTED))
                {
                    tally += result.getAdjustedScore();
                }
            }
        }

        return tally;
    }

    /**
     * Flexible way of determining which scores to tally
     *
     * @param scoreCards
     * @return
     */
    public Double tallyScoreFor(String... scoreCards) {
        return tallyScoreFor(Result.ResultScoreType.ORIGINAL, scoreCards);
    }

    /**
     * Flexible way of determining which adjusted scores to tally based on setupScoring type
     * (original or adjusted)
     *
     * @param scoreType  Original or Adjusted
     * @param scoreCards
     * @return
     */
    public Double tallyScoreFor(Result.ResultScoreType scoreType, String... scoreCards) {

        if (scoreCards == null || scoreCards.length == 0)
        {
            return null;
        }

        return tallyScore(new HashSet<String>(Arrays.asList(scoreCards)), scoreType);
    }

    /**
     * Find any of these that are currently part of the summary
     */
    private Set<String> findScoreCardsByName(Set<String> scoreCardNames) {

        Set<String> scoreCards = new HashSet<String>();

        for (String scoreCardName : _results.keySet())
        {
            if (scoreCardNames.contains(scoreCardName))
            {
                scoreCards.add(scoreCardName);
            }
        }

        return scoreCards;
    }
}

