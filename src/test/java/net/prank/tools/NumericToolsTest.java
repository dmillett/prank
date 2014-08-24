package net.prank.tools;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
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
public class NumericToolsTest {

    private static final double DELTA = 1e-10;

    @Test(expected = NullPointerException.class)
    public void test_average_null() {

        List<Double> values = null;
        assertNull(NumericTools.average(values));

        values = getSmallDoublesList(4);
        values.add(null);
        NumericTools.average(values);
    }

    @Test
    public void test_average() {

        List<Double> values = getSmallDoublesList(4);
        double average = NumericTools.average(values);

        assertEquals(2.5, average, DELTA);
    }

    @Test(expected = NullPointerException.class)
    public void test_meanDeviation_null() {

        List<Double> values = null;
        assertNull(NumericTools.meanDeviation(values));

        values = getSmallDoublesList(4);
        values.add(null);
        NumericTools.meanDeviation(values);
    }

    @Test
    public void test_meanDeviation() {

        List<Double> values = getSmallDoublesList(4);
        double meanDev = NumericTools.meanDeviation(values);

        assertEquals(1.0, meanDev, DELTA);
    }

    @Test(expected = NullPointerException.class)
    public void test_standardDeviation_null() {

        List<Double> values = null;
        assertNull(NumericTools.standardDeviation(2.5, values));

        values = getSmallDoublesList(4);
        values.add(null);
        NumericTools.standardDeviation(2.5, values);
    }

    @Test(expected = NullPointerException.class)
    public void test_min() {

        assertNull(NumericTools.min(null));
        assertNull(NumericTools.min(new ArrayList<Double>()));

        List<Double> values = getSmallDoublesList(10);
        double min = NumericTools.min(values);

        assertEquals(1.0, min, DELTA);

        values.add(null);
        NumericTools.min(values);
    }

    @Test(expected = NullPointerException.class)
    public void test_max() {

        assertNull(NumericTools.max(null));
        assertNull(NumericTools.max(new ArrayList<Double>()));

        List<Double> values = getSmallDoublesList(10);
        double max = NumericTools.max(values);

        assertEquals(10.0, max, DELTA);

        values.add(null);
        NumericTools.max(values);
    }

    @Test
    public void test_standardDeviation() {

        List<Double> values = getSmallDoublesList(4);
        double standardDev = NumericTools.standardDeviation(values);

        BigDecimal standardDeviation = new BigDecimal(String.valueOf(standardDev));
        standardDeviation = standardDeviation.setScale(5, RoundingMode.HALF_DOWN);

        assertEquals(1.29099, standardDeviation.doubleValue(), DELTA);
    }

    private List<Double> getSmallDoublesList(int size) {

        List<Double> values = new ArrayList<Double>();

        for (int i = 1; i <= size; i++)
        {
            values.add(Double.valueOf(i));
        }

        return values;
    }
}
