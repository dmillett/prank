package net.prank.core;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Each solution should have a Result where 'V' might typically be an integer
 * in a simple point system.
 * <p/>
 * TODO: Generic expansion to accommodate more flexibility in the framework.
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
public class Result
    implements Serializable {

    private static final long serialVersionUID = 42L;
    /**
     * ORIGINAL - setupScoring per original scoring
     * ADJUSTED - indicates that an adjustment (+/-/*) may have occurred
     */
    public static enum ResultScoreType {

        ORIGINAL,
        ADJUSTED;
    }

    /** Name of the ScoreCard for this result, corresponds to Prankster 'key'  */
    private final String _scoreCardName;
    /** An overall grade mechanism */
    private final BigDecimal _score;
    /** An overall grade after 'weighting' or 'adjustments' have been applied */
    private final BigDecimal _adjustedScore;

    /** The value to score on (price, shipping time, etc) */
    private final Object _valueToScore;
    /** Initial position prior to scoring and ranking */
    private final Long _position;
    /** Standard deviation from other results */
    private final BigDecimal _standardDeviation;
    /** Average across all results */
    private final BigDecimal _average;

    /**
     * If this seems like a
     *
     * @param scoreCardName
     * @param score
     * @param adjustedScore
     * @param position
     * @param standardDeviation
     * @param average
     */
    public Result(String scoreCardName, BigDecimal score, BigDecimal adjustedScore, Long position, Object original,
                  BigDecimal average, BigDecimal standardDeviation) {

        _scoreCardName = scoreCardName;
        _score = score;
        _adjustedScore = adjustedScore;
        _position = position;
        _valueToScore = original;
        _standardDeviation = standardDeviation;
        _average = average;
    }

    public BigDecimal getScore() {
        return _score;
    }

    public BigDecimal getAdjustedScore() {
        return _adjustedScore;
    }

    public Long getPosition() {
        return _position;
    }

    public BigDecimal getStandardDeviation() {
        return _standardDeviation;
    }

    public BigDecimal getAverage() {
        return _average;
    }

    public String getScoreCardName() {
        return _scoreCardName;
    }

    public Object getValueToScore() {
        return _valueToScore;
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

        Result result = (Result) o;

        if ( _adjustedScore != null ? !_adjustedScore.equals(result._adjustedScore) : result._adjustedScore != null )
        {
            return false;
        }

        if ( _average != null ? !_average.equals(result._average) : result._average != null )
        {
            return false;
        }

        if ( _position != null ? !_position.equals(result._position) : result._position != null )
        {
            return false;
        }

        if ( _score != null ? !_score.equals(result._score) : result._score != null )
        {
            return false;
        }

        if ( _scoreCardName != null ? !_scoreCardName.equals(result._scoreCardName) : result._scoreCardName != null )
        {
            return false;
        }

        if ( _standardDeviation != null ? !_standardDeviation.equals(result._standardDeviation)
                                        : result._standardDeviation != null )
        {
            return false;
        }

        if ( _valueToScore != null ? !_valueToScore.equals(result._valueToScore) : result._valueToScore != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _scoreCardName != null ? _scoreCardName.hashCode() : 0;
        result = 31 * result + (_score != null ? _score.hashCode() : 0);
        result = 31 * result + (_adjustedScore != null ? _adjustedScore.hashCode() : 0);
        result = 31 * result + (_valueToScore != null ? _valueToScore.hashCode() : 0);
        result = 31 * result + (_position != null ? _position.hashCode() : 0);
        result = 31 * result + (_standardDeviation != null ? _standardDeviation.hashCode() : 0);
        result = 31 * result + (_average != null ? _average.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "_scoreCardName='" + _scoreCardName + '\'' +
                ", _score=" + _score +
                ", _adjustedScore=" + _adjustedScore +
                ", _valueToScore=" + _valueToScore +
                ", _position=" + _position +
                ", _standardDeviation=" + _standardDeviation +
                ", _average=" + _average +
                '}';
    }

    /**
     * name, setupScoring, adjusted setupScoring, position, original, average, std dev
     */
    public String dump() {

        StringBuilder sb = new StringBuilder();
        sb.append(",").append(_score).append(",").append(_adjustedScore).append(",").append(_position).append(",")
                .append(_valueToScore).append(",").append(_average).append(",").append(_standardDeviation);

        return sb.toString();
    }

    /**
     * A helper since the constructor is large.
     */
    public static class ResultBuilder {

        private String _rbCardName;
        private BigDecimal _rbScore;
        private BigDecimal _rbAdjustedScore;

        private Long _rbPosition = null;
        private Object _rbOriginal = null;
        private BigDecimal _rbAverage = null;
        private BigDecimal _rbStandardDeviation = null;

        public ResultBuilder(Result originalResult) {

            if (originalResult != null) {
                _rbCardName = originalResult.getScoreCardName();
                _rbScore = originalResult.getScore();
                _rbAdjustedScore = originalResult.getAdjustedScore();

                _rbPosition = originalResult.getPosition();
                _rbOriginal = originalResult.getValueToScore();
                _rbAverage = originalResult.getAverage();
                _rbStandardDeviation = originalResult.getStandardDeviation();
            }
        }

        public ResultBuilder(String name, BigDecimal score) {
            _rbCardName = name;
            _rbScore = score;
            _rbAdjustedScore = score;
        }

        public ResultBuilder adjustScore(BigDecimal adjustedScore) {
            _rbAdjustedScore = adjustedScore;
            return this;
        }

        public ResultBuilder position(long position) {
            _rbPosition = position;
            return this;
        }

        public ResultBuilder standardDeviation(BigDecimal standardDeviation) {
            _rbStandardDeviation = standardDeviation;
            return this;
        }

        public ResultBuilder average(BigDecimal average) {
            _rbAverage = average;
            return this;
        }

        public ResultBuilder original(double original) {
            _rbOriginal = original;
            return this;
        }

        public Result build() {
            return new Result(_rbCardName, _rbScore, _rbAdjustedScore, _rbPosition, _rbOriginal, _rbAverage,
                              _rbStandardDeviation);
        }
    }
}

