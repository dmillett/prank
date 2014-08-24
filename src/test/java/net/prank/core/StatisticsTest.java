package net.prank.core;

import org.junit.Test;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author dmillett
 *         <p/>
 *         Copyright 2012 David Millett
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <p/>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <p/>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
public class StatisticsTest {

    @Test
    public void test__dump() {

        BigDecimal min = new BigDecimal("1.0");
        BigDecimal max = new BigDecimal("3.0");
        int sampleSize = 2;
        BigDecimal average = new BigDecimal("2.0");
        BigDecimal meanDev = new BigDecimal("1.0");

        Statistics stats = new Statistics(min, max, sampleSize, average, meanDev, null, null);
        String dumped = stats.dump();
        assertEquals("1.0:3.0:2:2.0:1.0::", dumped);
    }
}
