package net.prank.core;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.Formatter;

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
public class StatisticsTest
    extends TestCase {

    public void test_dump__with_formatter() {

        Statistics stats = new Statistics(new BigDecimal("1.15"), new BigDecimal("1.29"), null, null);
        Formatter formatter = new Formatter();
        String format = "v1:%1s, v2:%2s, v3:%3s, v4:%s";

        String dumped = stats.dump(formatter, format);
        assertEquals("v1:1.15, v2:1.29, v3:null, v4:null", dumped);
    }
}
