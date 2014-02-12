package net.prank.core;

import net.prank.DELIM;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Formatter;

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
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _average;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _meanDeviation;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _medianDeviation;
    /** Use your favorite stats library or NumericTools */
    private final BigDecimal _standardDeviation;

    public Statistics(BigDecimal average, BigDecimal meanAbsoluteDeviation, BigDecimal medianAbsoluteDeviation,
                      BigDecimal standardDeviation) {

        _average = average;
        _meanDeviation = meanAbsoluteDeviation;
        _medianDeviation = medianAbsoluteDeviation;
        _standardDeviation = standardDeviation;
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
     * original scale. If null, then just appends a ":" delimiter.
     *
     * average:mean deviation:median devation:standard deviation
     *
     * Example:
     * ::::                    // all values are null
     * 40.00:5.31::            // average, mean devation not null
     * 5.10:1.144:1.00:1.654   // no null values
     *
     * @return
     */
    public String dump() {
        return dump(null, RoundingMode.UNNECESSARY);
    }

    /**
     * Same as dump(), but with a specified scale and RoundingMode.HALF_EVEN
     * @param scale will truncate to value
     * @return
     */
    public String dump(int scale) {
        return dump(scale, null);
    }

    /**
     * Same as dump() but with both scale and rounding mode
     * @param scale
     * @param roundingMode
     * @return
     */
    public String dump(Integer scale, RoundingMode roundingMode) {

        String delim = DELIM.COLON.get();

        StringBuilder sb = new StringBuilder();
        addToBuilder(sb, _average, delim, scale, roundingMode);
        addToBuilder(sb, _meanDeviation, delim, scale, roundingMode);
        addToBuilder(sb, _medianDeviation, delim, scale, roundingMode);
        addToBuilder(sb, _standardDeviation, delim, scale, roundingMode);

        return sb.toString();
    }

    /**
     * Applies any non-null formatter/format combination with objects in the
     * following order: _average, _meanDeviation, _medianDeviation, _standardDeviation
     *
     * @param formatter
     * @param format
     *
     * @return null if formatter or format are null, otherwise a formatted String
     */
    public String dump(Formatter formatter, String format) {

        if (formatter == null || format == null)
        {
            return null;
        }

        return formatter.format(format, _average, _meanDeviation, _medianDeviation, _standardDeviation).toString();
    }

    /**  */
    private void addToBuilder(StringBuilder sb, BigDecimal value, String delim, Integer scale, RoundingMode mode) {

        RoundingMode rm = mode != null ? mode : RoundingMode.HALF_EVEN;

        if (value != null)
        {
            if (scale != null)
            {
                sb.append(value.setScale(scale, rm)).append(delim);
            }
            else
            {
                sb.append(value).append(delim);
            }
        }
        else
        {
            sb.append(delim);
        }
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

        if ( _average != null ? !_average.equals(that._average) : that._average != null )
        {
            return false;
        }

        if ( _meanDeviation != null ? !_meanDeviation.equals(that._meanDeviation)
                                            : that._meanDeviation != null )
        {
            return false;
        }

        if ( _medianDeviation != null ? !_medianDeviation.equals(that._medianDeviation)
                                              : that._medianDeviation != null )
        {
            return false;
        }

        if ( _standardDeviation != null ? !_standardDeviation.equals(that._standardDeviation)
                                        : that._standardDeviation != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = _average != null ? _average.hashCode() : 0;
        result = 31 * result + (_meanDeviation != null ? _meanDeviation.hashCode() : 0);
        result = 31 * result + (_medianDeviation != null ? _medianDeviation.hashCode() : 0);
        result = 31 * result + (_standardDeviation != null ? _standardDeviation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "_average=" + _average +
                ", _meanDeviation=" + _meanDeviation +
                ", _medianDeviation=" + _medianDeviation +
                ", _standardDeviation=" + _standardDeviation +
                '}';
    }

    /** More flexibility for creating a Statistics object. */
    public static class Builder {

        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bAverage;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bMeanDeviation;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bMedianDeviation;
        /** Use your favorite stats library or NumericTools */
        private BigDecimal _bStandardDeviation;

        public Statistics build() {
            return new Statistics(_bAverage, _bMeanDeviation, _bMedianDeviation, _bStandardDeviation);
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
