package net.prank.core;

import net.prank.DELIM;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Encapsulating common scoring items that can be used to differentiate
 * Scored objects after scoring occurs.
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
public class ScoreData
    implements Serializable {

    private static final long serialVersionUID = 42L;
    //public static final String DELIM = ":";

    /** The value given by a ScoreCard */
    private final BigDecimal _score;
    /** The value of adjusting '_score' */
    private final BigDecimal _adjustedScore;
    /** The number of buckets allocated across the range given by '_minPoints' and '_maxPoints' */
    private final int _buckets;
    /** The maximum possible score */
    private final BigDecimal _maxPoints;
    /** The minimum possible score */
    private final BigDecimal _minPoints;

    /** Use this or the builder to create a ScoreData object in your ScoreCard implementation */
    public ScoreData(BigDecimal score, BigDecimal adjustedScore, int buckets, BigDecimal maxPoints,
                     BigDecimal minPoints) {

        _score = score;
        _adjustedScore = adjustedScore;
        _buckets = buckets;
        _maxPoints = maxPoints;
        _minPoints = minPoints;
    }

    @Override
    public boolean equals(Object o) {

        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ScoreData score = (ScoreData) o;

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

        if ( _minPoints != null ? !_minPoints.equals(score._minPoints) : score._minPoints != null )
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
        result = 31 * result + (_minPoints != null ? _minPoints.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScoreData{" +
                "_score=" + _score +
                ", _adjustedScore=" + _adjustedScore +
                ", _buckets=" + _buckets +
                ", _maxPoints=" + _maxPoints +
                ", _minPoints=" + _minPoints +
                '}';
    }

    /**
     * Multiplies the '_score' by a multiplier and returns a new ScoreData object,
     * otherwise returns this
     *
     * @param multiplier A multiplier applied to '_score' if not null
     * @return A new ScoreData object with adjusted score or this
     */
    public ScoreData adjustScore(BigDecimal multiplier) {

        if ( multiplier == null || _score == null )
        {
            return this;
        }

        BigDecimal adjustedScore = _score.multiply(multiplier);
        return new ScoreData(_score, adjustedScore, _buckets, _maxPoints, _minPoints);
    }

    /**
     *
     * @return score:adjustedScore:maxPoints:minPoints:buckets
     */
    public String dump() {

        StringBuilder sb = new StringBuilder();
        sb.append(_score).append(DELIM.COLON).append(_adjustedScore).append(DELIM.COLON)
          .append(_maxPoints).append(DELIM.COLON).append(_minPoints).append(DELIM.COLON)
          .append(_buckets);

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

    public BigDecimal getMinPoints() {
        return _minPoints;
    }

    public static class Builder {

        private BigDecimal _bScore;
        private BigDecimal _bAdjustedScore;
        private int _bBuckets;
        private BigDecimal _bMaxPoints;
        private BigDecimal _bMinPoints;

        public ScoreData build() {
            return new ScoreData(_bScore, _bAdjustedScore, _bBuckets, _bMaxPoints, _bMinPoints);
        }

        public Builder setScore(BigDecimal score) {
            _bScore = score;
            return this;
        }

        public Builder setAdjustedScore(BigDecimal adjustedScore) {
            _bAdjustedScore = adjustedScore;
            return this;
        }

        public Builder setBuckets(int buckets) {
            _bBuckets = buckets;
            return this;
        }

        public Builder setMaxPoints(BigDecimal maxPoints) {
            _bMaxPoints = maxPoints;
            return this;
        }

        public Builder setMinPoints(BigDecimal minPoints) {
            _bMinPoints = minPoints;
            return this;
        }
    }
}
