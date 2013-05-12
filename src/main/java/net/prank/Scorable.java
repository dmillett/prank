package net.prank;

/**
 * Use if you are ok updating your scorable object so that
 * you can use a generic comparator on it. Otherwise, just
 * update your model and define a more specific comparator.
 */
public interface Scorable {
    /** Contains any ScoreCard's and their scores */
    public ScoreSummary getScoreSummary();
}

