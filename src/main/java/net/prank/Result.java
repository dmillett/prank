package net.prank;

import java.io.Serializable;

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
    private final double _score;
    /** An overall grade after 'weighting' or 'adjustments' have been applied */
    private final double _adjustedScore;

    /** The value to score on (price, shipping time, etc) */
    private final double _original;
    /** Initial position prior to scoring and ranking */
    private final int _position;
    /** Standard deviation from other results */
    private final double _standardDeviation;
    /** Average across all results */
    private final double _average;

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
    public Result(String scoreCardName, double score, double adjustedScore, int position, double original,
                  double average, double standardDeviation) {

        _scoreCardName = scoreCardName;
        _score = score;
        _adjustedScore = adjustedScore;
        _position = position;
        _original = original;
        _standardDeviation = standardDeviation;
        _average = average;
    }

    public double getScore() {
        return _score;
    }

    public double getAdjustedScore() {
        return _adjustedScore;
    }

    public int getPosition() {
        return _position;
    }

    public double getStandardDeviation() {
        return _standardDeviation;
    }

    public double getAverage() {
        return _average;
    }

    public String getScoreCardName() {
        return _scoreCardName;
    }

    public double getOriginal() {
        return _original;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Result result = (Result) o;

        if (Double.compare(result._adjustedScore, _adjustedScore) != 0)
        {
            return false;
        }

        if (Double.compare(result._average, _average) != 0)
        {
            return false;
        }

        if (Double.compare(result._original, _original) != 0)
        {
            return false;
        }

        if (_position != result._position)
        {
            return false;
        }

        if (Double.compare(result._score, _score) != 0)
        {
            return false;
        }

        if (Double.compare(result._standardDeviation, _standardDeviation) != 0)
        {
            return false;
        }

        if (_scoreCardName != null ? !_scoreCardName.equals(result._scoreCardName) : result._scoreCardName != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result;
        long temp;

        result = _scoreCardName != null ? _scoreCardName.hashCode() : 0;
        temp = _score != +0.0d ? Double.doubleToLongBits(_score) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = _adjustedScore != +0.0d ? Double.doubleToLongBits(_adjustedScore) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = _original != +0.0d ? Double.doubleToLongBits(_original) : 0L;

        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + _position;
        temp = _standardDeviation != +0.0d ? Double.doubleToLongBits(_standardDeviation) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = _average != +0.0d ? Double.doubleToLongBits(_average) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));

        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "_scoreCardName='" + _scoreCardName + '\'' +
                ", _score=" + _score +
                ", _adjustedScore=" + _adjustedScore +
                ", _original=" + _original +
                ", _position=" + _position +
                ", _standardDeviation=" + _standardDeviation +
                ", _average=" + _average +
                '}';
    }

    /**
     * name, setupScoring, adjusted setupScoring, position, original, average, std dev
     */
    public String dump() {

        StringBuilder sb = new StringBuilder(_scoreCardName);
        sb.append(",").append(_score).append(",").append(_adjustedScore).append(",").append(_position).append(",")
                .append(_original).append(",").append(_average).append(",").append(_standardDeviation);

        return sb.toString();
    }

    /**
     * A helper since the constructor is large.
     */
    public static class ResultBuilder {

        private String _rbCardName;
        private double _rbScore;
        private double _rbAdjustedScore;

        private int _rbPosition = -1;
        private double _rbOriginal = -1.0;
        private double _rbAverage = -1;
        private double _rbStandardDeviation = 0.0;

        public ResultBuilder(Result originalResult) {

            if (originalResult != null) {
                _rbCardName = originalResult.getScoreCardName();
                _rbScore = originalResult.getScore();
                _rbAdjustedScore = originalResult.getAdjustedScore();

                _rbPosition = originalResult.getPosition();
                _rbOriginal = originalResult.getOriginal();
                _rbAverage = originalResult.getAverage();
                _rbStandardDeviation = originalResult.getStandardDeviation();
            }
        }

        public ResultBuilder(String name, double score) {
            _rbCardName = name;
            _rbScore = score;
            _rbAdjustedScore = score;
        }

        public ResultBuilder adjustScore(double adjustedScore) {
            _rbAdjustedScore = adjustedScore;
            return this;
        }

        public ResultBuilder position(int position) {
            _rbPosition = position;
            return this;
        }

        public ResultBuilder standardDeviation(double standardDeviation) {
            _rbStandardDeviation = standardDeviation;
            return this;
        }

        public ResultBuilder average(double average) {
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

