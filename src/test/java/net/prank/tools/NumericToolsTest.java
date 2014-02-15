package net.prank.tools;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 2/12/14.
 */
public class NumericToolsTest
    extends TestCase {

    public void test_average_null() {

        List<Double> values = null;
        assertNull(NumericTools.average(values));

        try
        {
            values = getSmallDoublesList(4);
            values.add(null);
            NumericTools.average(values);

            fail("Should Throw an NPE");
        }
        catch ( NullPointerException e )
        {
            // success
        }
    }

    public void test_average() {

        List<Double> values = getSmallDoublesList(4);
        double average = NumericTools.average(values);

        assertEquals(2.5, average);
    }

    public void test_meanDeviation_null() {

        List<Double> values = null;
        assertNull(NumericTools.meanDeviation(values));

        try
        {
            values = getSmallDoublesList(4);
            values.add(null);
            NumericTools.meanDeviation(values);

            fail("Should Throw an NPE");
        }
        catch ( NullPointerException e )
        {
            // success
        }
    }

    public void test_meanDeviation() {

        List<Double> values = getSmallDoublesList(4);
        double meanDev = NumericTools.meanDeviation(values);

        assertEquals(1.0, meanDev);
    }

    public void test_standardDeviation_null() {

        List<Double> values = null;
        assertNull(NumericTools.standardDeviation(2.5, values));

        try
        {
            values = getSmallDoublesList(4);
            values.add(null);
            NumericTools.standardDeviation(2.5, values);

            fail("Should Throw an NPE");
        }
        catch ( NullPointerException e )
        {
            // success
        }
    }

    public void test_min() {

        assertNull(NumericTools.min(null));
        assertNull(NumericTools.min(new ArrayList<Double>()));

        List<Double> values = getSmallDoublesList(10);
        double min = NumericTools.min(values);

        assertEquals(1.0, min);

        try
        {
            values.add(null);
            NumericTools.min(values);
            fail("Should have puked out on null");
        }
        catch (NullPointerException e)
        {
            // success
        }
    }

    public void test_max() {

        assertNull(NumericTools.max(null));
        assertNull(NumericTools.max(new ArrayList<Double>()));

        List<Double> values = getSmallDoublesList(10);
        double max = NumericTools.max(values);

        assertEquals(10.0, max);

        try
        {
            values.add(null);
            NumericTools.max(values);
            fail("Should have puked out on null");
        }
        catch (NullPointerException e)
        {
            // success
        }
    }

    public void test_standardDeviation() {

        List<Double> values = getSmallDoublesList(4);
        double standardDev = NumericTools.standardDeviation(values);

        BigDecimal standardDeviation = new BigDecimal(String.valueOf(standardDev));
        standardDeviation = standardDeviation.setScale(5, RoundingMode.HALF_DOWN);

        assertEquals(1.29099, standardDeviation.doubleValue());
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
