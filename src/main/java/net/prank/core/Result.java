package net.prank.core;

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
public class Result<T>
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
    /** The value to score on (price, shipping time, etc) */
    private final T _scoredValue;
    /** Initial position prior to scoring and ranking */
    private final Indices _indices;

    /** Encapsulating score, adjusted score, max points, min points, number of buckets, etc */
    private final ScoreData _score;
    /** Capture average and deviations (mean, median, standard) */
    private final Statistics _statistics;

    /** Instantiate directly or use the Builder */
    public Result(String scoreCardName, T scoredValue, Indices indices, ScoreData score, Statistics stats) {

        _scoreCardName = scoreCardName;
        _scoredValue = scoredValue;
        _indices = indices;
        _score = score;
        _statistics = stats;
    }

    public String getScoreCardName() {
        return _scoreCardName;
    }

    public T getScoredValue() {
        return _scoredValue;
    }

    public Indices getPosition() {
        return _indices;
    }

    public ScoreData getScoreData() {
        return _score;
    }

    public Statistics getStatistics() {
        return _statistics;
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

        if ( _indices != null ? !_indices.equals(result._indices) : result._indices != null )
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

        if ( _scoredValue != null ? !_scoredValue.equals(result._scoredValue) : result._scoredValue != null )
        {
            return false;
        }

        if ( _statistics != null ? !_statistics.equals(result._statistics) : result._statistics != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = _scoreCardName != null ? _scoreCardName.hashCode() : 0;
        result = 31 * result + (_scoredValue != null ? _scoredValue.hashCode() : 0);
        result = 31 * result + (_indices != null ? _indices.hashCode() : 0);
        result = 31 * result + (_score != null ? _score.hashCode() : 0);
        result = 31 * result + (_statistics != null ? _statistics.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "_scoreCardName='" + _scoreCardName + '\'' +
                ", _scoredValue=" + _scoredValue +
                ", _position=" + _indices +
                ", _score=" + _score +
                ", _statistics=" + _statistics +
                '}';
    }

    /**
     * A helper since the constructor is large.
     */
    public static class Builder<T> {

        private String _bCardName;
        private T _bOriginal;
        private Indices _bIndices;
        private ScoreData _bScore;
        private Statistics _bStatistics;

        public Builder() {}

        public Builder(Result<T> original) {

            if (original != null)
            {
                _bCardName = original._scoreCardName;
                _bOriginal = original._scoredValue;
                _bIndices = original._indices;
                _bScore = original._score;
                _bStatistics = original._statistics;
            }
        }

        public Builder(String name, ScoreData score) {
            _bCardName = name;
            _bScore = score;
        }

        public Builder setPosition(Indices bPosition) {
            _bIndices = bPosition;
            return this;
        }

        public Builder setOriginal(T bOriginal) {
            _bOriginal = bOriginal;
            return this;
        }

        public Builder setScore(ScoreData bScore) {
            _bScore = bScore;
            return this;
        }

        public Builder setStatistics(Statistics bStatistics) {
            _bStatistics = bStatistics;
            return this;
        }

        public Builder setCardName(String bCardName) {
            _bCardName = bCardName;
            return this;
        }

        public Result build() {
            return new Result<T>(_bCardName, _bOriginal, _bIndices, _bScore, _bStatistics);
        }
    }
}

