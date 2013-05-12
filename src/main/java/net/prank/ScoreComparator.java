package net.prank;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * A generic comparator of Scorable types.
 */
public class ScoreComparator
    implements Comparator<Scorable> {

    private final Set<String> _scoreCardNames;

    public ScoreComparator() {
        _scoreCardNames = new HashSet<String>();
    }

    public ScoreComparator(Set<String> scoreCardNames) {
        _scoreCardNames = scoreCardNames;
    }

    @Override
    public int compare(Scorable one, Scorable two) {

        if (one == null && two == null) {
            return 0;
        } else if (one != null && two == null) {
            return 1;
        } else if (one == null && two != null) {
            return -1;
        }

        Double score1;
        Double score2;

        if (_scoreCardNames.isEmpty()) {
            score1 = one.getScoreSummary().tallyScore();
            score2 = two.getScoreSummary().tallyScore();
        } else {
            score1 = one.getScoreSummary().tallyScoreFor(_scoreCardNames);
            score2 = two.getScoreSummary().tallyScoreFor(_scoreCardNames);
        }

        if (score1 == null && score2 == null) {
            return 0;
        } else if (one != null && two == null) {
            return 1;
        } else if ( one == null && two != null) {
            return -1;
        }

        return score2.compareTo(score1);
    }
}

