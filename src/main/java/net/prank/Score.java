package net.prank;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 */
public class Score
    implements Serializable {

    private static final long serialVersionUID = 42L;
    public static final String DELIM = ":";

    private final BigDecimal _score;
    private final BigDecimal _adjustedScore;
    private final int _buckets;
    private final BigDecimal _maxPoints;

    public Score(BigDecimal score, BigDecimal adjustedScore, int buckets, BigDecimal maxPoints) {
        _score = score;
        _adjustedScore = adjustedScore;
        _buckets = buckets;
        _maxPoints = maxPoints;
    }

    @Override
    public boolean equals(Object o) {

        if ( this == o ) { return true; }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Score score = (Score) o;

        if ( _buckets != score._buckets )
        {
            return false;
        }

        if ( _adjustedScore != null ? !_adjustedScore.equals(score._adjustedScore) : score._adjustedScore != null )
        {
            return false;
        }

        if ( _maxPoints != null ? !_maxPoints.equals(score._maxPoints) : score._maxPoints != null )
        {
            return false;
        }

        if ( _score != null ? !_score.equals(score._score) : score._score != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = _score != null ? _score.hashCode() : 0;
        result = 31 * result + (_adjustedScore != null ? _adjustedScore.hashCode() : 0);
        result = 31 * result + _buckets;
        result = 31 * result + (_maxPoints != null ? _maxPoints.hashCode() : 0);
        return result;
    }

    /**
     * Multiplies the '_score' by a multiplier and returns a new Score object,
     * otherwise returns this
     *
     * @param multiplier A multiplier applied to '_score' if not null
     * @return A new Score object with adjusted score or this
     */
    public Score adjustScore(BigDecimal multiplier) {

        if ( multiplier == null || _score == null )
        {
            return this;
        }

        BigDecimal adjustedScore = _score.multiply(multiplier);
        return new Score(_score, adjustedScore, _buckets, _maxPoints);
    }

    /**
     *
     * @return score:adjustedScore:maxPoints:buckets
     */
    public String dump() {

        StringBuilder sb = new StringBuilder();
        sb.append(_score).append(DELIM).append(_adjustedScore).append(DELIM)
          .append(_maxPoints).append(DELIM).append(_buckets);

        return sb.toString();
    }

    public BigDecimal getScore() {
        return _score;
    }

    public BigDecimal getAdjustedScore() {
        return _adjustedScore;
    }

    public int getBuckets() {
        return _buckets;
    }

    public BigDecimal getMaxPoints() {
        return _maxPoints;
    }
}
