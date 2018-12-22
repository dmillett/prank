package net.prank.tools;

import java.util.List;

/**
 * Some simple numeric calculation tools. Feel free to use your
 * own or Apache, etc.
 *
 * @author dmillett
 * <p>
 * Copyright 2012 David Millett
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class NumericTools {

    /**
     * Average a list of numbers by taking the doubleValue()
     *
     * Any null number in 'values' will generate an NPE
     *
     * Ex:
     * (1,2,null,3) -- 6.0 / 3 = 2.0
     *
     * @param values A list of numbers
     * @return The average value for 'values' as a double
     */
    public static Double average(List<? extends Number> values) {

        if (values == null || values.isEmpty())
        {
            return null;
        }

        double total = 0.0;
        for (Number value : values)
        {
            total += value.doubleValue();
        }

        return total / values.size();
    }

    /**
     * Could replace this with commons-math:Mean
     *
     * @param values A list of Long numbers
     * @return The average of 'values' as a Double
     */
    public static Double averageForLongs(List<Long> values) {

        if (values == null || values.isEmpty())
        {
            return null;
        }

        double total = 0.0;
        for (Long time : values)
        {
            total += time;
        }

        return total / values.size();
    }

    /**
     * Average for a list of Double(s), will throw a NPE for any null in 'values'
     *
     * @param values A list of doubles
     * @return The average for a list of doubles
     */
    public static Double averageForDoubles(List<Double> values) {

        if (values == null || values.isEmpty())
        {
            return null;
        }

        double total = 0.0;

        for (Double value : values)
        {
            total += value;
        }

        return total / values.size();
    }

    /**
     * Will throw an NPE for any null value in 'values'
     * @param mean A number representing the average
     * @param values A list of numbers
     * @return The mean deviation as a Double
     */
    public static Double meanDeviation(Number mean, List<? extends Number> values) {

        if (mean == null || values == null || values.isEmpty())
        {
            return null;
        }

        if (values.size() < 2)
        {
            return 0.0;
        }

        double deviationSum = 0.0;
        double average = mean.doubleValue();
        int count = values.size();

        for (Number n : values)
        {
            deviationSum += Math.abs(n.doubleValue() - average);
        }

        return deviationSum / count;
    }

    /**
     * Calculates the mean deviation. Null 'numbers' will throw an error
     * @param values A list of Numbers
     * @return the mean deviation for 'values'
     */
    public static Double meanDeviation(List<? extends Number> values) {

        if (values == null || values.isEmpty())
        {
            return null;
        }

        double average = average(values);
        return meanDeviation(average, values);
    }

    /**
     * Reference Knuth --
     *
     * sum the (square the difference from the average),
     * then take the square root of the squared-sum / sample-size
     *
     * This will throw a NPE for any null in 'values'
     *
     * @param mean The mean/average of a collection of numbers
     * @param values The list of numeric values
     * @return null (for null values)
     *         0.0 (for values.size() = 1)
     *         standard deviation (for normal list of numbers)
     */
    public static Double standardDeviation(double mean, List<? extends Number> values) {

        if (values == null)
        {
            return null;
        }

        if (values.size() < 2)
        {
            return 0.0;
        }

        double squaredSum = 0.0;

        for (Number value : values)
        {
            double delta = value.doubleValue() - mean;
            squaredSum += (delta * delta);
        }

        return Math.sqrt(squaredSum / (values.size() - 1));
    }

    /**
     * Calculate the average here, then hand off to the standard deviations
     * @param values A list of Numbers
     * @return the standard deviation as a double
     */
    public static Double standardDeviation(List<? extends Number> values) {

        double average = NumericTools.average(values);
        return standardDeviation(average, values);
    }

    /**
     * Could replace this with commons-math:StandardDeviation
     * It will throw an NPE for any null in 'values'
     *
     * @param mean The average or mean
     * @param values A list of long numbers
     * @return standard deviation
     */
    public static Double standardDeviationForLongs(double mean, List<Long> values) {

        if (values.size() < 2)
        {
            return 0.0;
        }

        double squaredSum = 0.0;

        for (Long value : values)
        {
            double delta = value - mean;
            squaredSum += (delta * delta);
        }

        return Math.sqrt(squaredSum / (values.size() - 1));
    }

    /**
     * Standard deviation for a list of doubles.
     *
     * @param mean The mean for this collection of doubles
     * @param values A list of doubles
     * @return The standard deviation for collection of doubles and their mean
     */
    public static Double standardDeviationForDoubles(double mean, List<Double> values) {

        if (values.size() < 2)
        {
            return 0.0;
        }

        double squaredSum = 0.0;

        for (Double value : values)
        {
            double delta = value - mean;
            squaredSum += (delta * delta);
        }

        return Math.sqrt(squaredSum / (values.size() - 1));
    }

    /**
     * Calculate the maximum value for a collection of Numbers
     * via each Number's doubleValue().
     *
     * Will throw NPE as it assumes all numbers are not-null.
     * @param values A list of numbers
     * @return null for any invalid List, NPE for any null in List,
     *         or the maximum double value from List
     */
    public static Double max(List<? extends Number> values) {

        if (values == null || values.isEmpty())
        {
            return null;
        }

        double max = values.get(0).doubleValue();

        for (Number value : values)
        {
            max = Math.max(max, value.doubleValue());
        }

        return max;
    }

    /**
     * Calculate the minimum value for a collection of Numbers
     * via each Number's doubleValue().
     *
     * Will throw NPE as it assumes all numbers are not-null.
     * @param values A list of Numbers
     * @return null for any invalid List, NPE for any null in List,
     *         or the minimum double value from List
     */
    public static Double min(List<? extends Number> values) {

        if (values == null || values.isEmpty())
        {
            return null;
        }

        double min = values.get(0).doubleValue();

        for (Number value : values)
        {
            min = Math.min(min, value.doubleValue());
        }

        return min;
    }
}

