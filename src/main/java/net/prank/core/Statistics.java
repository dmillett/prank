package net.prank.core;

import net.prank.tools.ScoreFormatter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An encapsulation of statistics to surface in the Results object
 * for ScoreCard evaluation. Use your favorite statistics library
 * or the included NumericTools to populate. Please note it is
 * probably better to perform calculations with primitives where
 * possible and store them as objects in this class.
 *
 * Store calculated values like:
 * average
 * mean absolute deviation
 * median absolute deviation
 * standard deviation
 *
 * Can dump in a simpler abbreviated formats, see dump(). It
 * does not support formatting
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
public class Statistics
    implements Serializable {

    private static final long serialVersionUID = 42L;
    /** The minimum value within a collection */
    private final BigDecimal _min;
    /** The maximum value within a collection */
    private final BigDecimal _max;
    /** The sample size */
    private final int _sampleSize;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _average;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _meanDeviation;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _medianDeviation;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _standardDeviation;

    public Statistics(BigDecimal min, BigDecimal max, int sampleSize, BigDecimal average,
                      BigDecimal meanAbsoluteDeviation,BigDecimal medianAbsoluteDeviation,
                      BigDecimal standardDeviation) {

        _min = min;
        _max = max;
        _sampleSize = sampleSize;
        _average = average;
        _meanDeviation = meanAbsoluteDeviation;
        _medianDeviation = medianAbsoluteDeviation;
        _standardDeviation = standardDeviation;
    }

    public BigDecimal getMin() {
        return _min;
    }

    public BigDecimal getMax() {
        return _max;
    }

    public int getSampleSize() {
        return _sampleSize;
    }

    public BigDecimal getAverage() {
        return _average;
    }

    public BigDecimal getMeanDeviation() {
        return _meanDeviation;
    }

    public BigDecimal getMedianDeviation() {
        return _medianDeviation;
    }

    public BigDecimal getStandardDeviation() {
        return _standardDeviation;
    }

    /**
     *
     * Builds a String representation of these values with their
     * original scale. If value is null, then just appends a ":" delimiter.
     *
     * min:max:sample size:average:mean deviation:median devation:standard deviation
     *
     * Example:
     * ::::                    // all values are null
     * 40.00:5.31::            // average, mean devation not null
     * 5.10:1.144:1.00:1.654   // no null values
     *
     * Option:
     * Use a custom Formatter and the Statistics object
     *
     * @return A formatted score, statistics, etc
     */
    public String dump() {
        return dump(-1, RoundingMode.UNNECESSARY);
    }

    /**
     * Same as dump(), but with a specified scale and RoundingMode.HALF_EVEN
     * @param scale will truncate to value
     * @return A formatted score, statistics, etc
     */
    public String dump(int scale) {
        return dump(scale, null);
    }

    /**
     * Same as dump() but with both scale and rounding mode
     * @param scale The scale to use for score format printer
     * @param roundingMode The rounding mode for the score format printer
     * @return A formatted score, statistics, etc
     */
    public String dump(int scale, RoundingMode roundingMode) {

        ScoreFormatter formatter = new ScoreFormatter();
        return formatter.dumpStatistics(this, scale, roundingMode);
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

        Statistics that = (Statistics) o;

        if ( _sampleSize != that._sampleSize )
        {
            return false;
        }

        if ( _average != null ? !_average.equals(that._average) : that._average != null )
        {
            return false;
        }

        if ( _max != null ? !_max.equals(that._max) : that._max != null )
        {
            return false;
        }

        if ( _meanDeviation != null ? !_meanDeviation.equals(that._meanDeviation) : that._meanDeviation != null )
        {
            return false;
        }

        if ( _medianDeviation != null ? !_medianDeviation.equals(that._medianDeviation) :
                                        that._medianDeviation != null )
        {
            return false;
        }

        if ( _min != null ? !_min.equals(that._min) : that._min != null )
        {
            return false;
        }

        if ( _standardDeviation != null ? !_standardDeviation.equals(that._standardDeviation) :
                                          that._standardDeviation != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = _min != null ? _min.hashCode() : 0;
        result = 31 * result + (_max != null ? _max.hashCode() : 0);
        result = 31 * result + _sampleSize;
        result = 31 * result + (_average != null ? _average.hashCode() : 0);

        result = 31 * result + (_meanDeviation != null ? _meanDeviation.hashCode() : 0);
        result = 31 * result + (_medianDeviation != null ? _medianDeviation.hashCode() : 0);
        result = 31 * result + (_standardDeviation != null ? _standardDeviation.hashCode() : 0);
        return result;
    }

    /** More flexibility for creating a Statistics object. */
    public static class Builder {

        private BigDecimal _bMin;
        private BigDecimal _bMax;
        private int _bSampleSize;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bAverage;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bMeanDeviation;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bMedianDeviation;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bStandardDeviation;

        public Statistics build() {
            return new Statistics(_bMin, _bMax, _bSampleSize, _bAverage, _bMeanDeviation, _bMedianDeviation,
                                  _bStandardDeviation);
        }

        public Builder setMin(BigDecimal bMin) {
            _bMin = bMin;
            return this;
        }

        public Builder setSampleSize(int bSampleSize) {
            _bSampleSize = bSampleSize;
            return this;
        }

        public Builder setMax(BigDecimal bMax) {
            _bMax = bMax;
            return this;
        }

        public Builder setStandardDeviation(BigDecimal bStandardDeviation) {
            _bStandardDeviation = bStandardDeviation;
            return this;
        }

        public Builder setAverage(BigDecimal bAverage) {
            _bAverage = bAverage;
            return this;
        }

        public Builder setMeanDeviation(BigDecimal bMeanDeviation) {
            _bMeanDeviation = bMeanDeviation;
            return this;
        }

        public Builder setMedianDeviation(BigDecimal bMedianDeviation) {
            _bMedianDeviation = bMedianDeviation;
            return this;
        }

    }
}
