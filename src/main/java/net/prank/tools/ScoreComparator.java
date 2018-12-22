package net.prank.tools;

import net.prank.core.Result;
import net.prank.core.Scorable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * A generic comparator of Scorable types.
 *
 * @author dmillett
 * <p>
 * Copyright 2012 David Millett
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ScoreComparator
    implements Comparator<Scorable>, Serializable {

    private static final long serialVersionUID = 42L;
    /** A subset of score cards (by name) */
    private final Set<String> _scoreCardNames;
    /** The type of score to check: ORIGINAL, ADJUSTED, or NORMALIZED */
    private final Result.ResultScoreType _scoreType;

    public ScoreComparator() {
        this(new HashSet<>(), Result.ResultScoreType.ORIGINAL);
    }

    public ScoreComparator(Result.ResultScoreType scoreType) {
        this(new HashSet<>(), scoreType);
    }

    public ScoreComparator(Set<String> scoreCardNames) {
        this(scoreCardNames, Result.ResultScoreType.ORIGINAL);
    }

    public ScoreComparator(Set<String> scoreCardNames, Result.ResultScoreType scoreType) {
        _scoreCardNames = scoreCardNames;
        _scoreType = scoreType;
    }

    @Override
    public int compare(Scorable one, Scorable two) {

        if (one == null && two == null)
        {
            return 0;
        }
        else if (one != null && two == null)
        {
            return 1;
        }
        else if (one == null)
        {
            return -1;
        }

        BigDecimal score1;
        BigDecimal score2;

        if (_scoreCardNames.isEmpty())
        {
            score1 = one.getScoreSummary().tallyScore(_scoreType);
            score2 = two.getScoreSummary().tallyScore(_scoreType);
        }
        else
        {
            score1 = one.getScoreSummary().tallyScoreFor(_scoreCardNames, _scoreType);
            score2 = two.getScoreSummary().tallyScoreFor(_scoreCardNames, _scoreType);
        }

        if (score1 == null && score2 == null)
        {
            return 0;
        }
        else if (score1 != null && score2 == null)
        {
            return 1;
        }
        else if (score1 == null)
        {
            return -1;
        }

        return score2.compareTo(score1);
    }
}

