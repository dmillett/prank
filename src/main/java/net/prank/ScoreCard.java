package net.prank;

/**
 * A thread pool exists for each configured (spring) ScoreCard where it will examine all
 */
public interface ScoreCard<T> {
    /**
     * A stateless method to prevent cross thread corruption.
     */
    public ScoreSummary score(T scoringObject);
    /** A score card name to use as a key in ScoreSummary */
    public String getName();
}

