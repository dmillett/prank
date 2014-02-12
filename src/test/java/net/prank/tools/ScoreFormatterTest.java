package net.prank.tools;

import junit.framework.TestCase;
import net.prank.core.ScoreData;
import net.prank.core.Statistics;

import java.math.BigDecimal;

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
public class ScoreFormatterTest
    extends TestCase {

    public void test_dumpScoreData__with_defaults() {

        ScoreData sd = buildScoreData("20.00", "22.50", "0.00", "30.00", 50);
        ScoreFormatter scf = new ScoreFormatter();

        String defaultDump = scf.dumpScoreData(sd);
        assertEquals("20.00:22.50:0.00:30.00:50", defaultDump);
    }

    public void test_dumpScoreData__with_scale() {

        ScoreData sd = buildScoreData("20.00", "22.55", "0.00", "30.14", 50);
        ScoreFormatter scf = new ScoreFormatter();

        String defaultDump = scf.dumpScoreData(sd, 1);
        assertEquals("20.0:22.6:0.0:30.1:50", defaultDump);
    }

    public void test_dumpStatistics__with_defaults() {

        Statistics stats = buildStatistics("1.36", "0.18", "0.25", "0.40");
        ScoreFormatter scf = new ScoreFormatter();

        String result = scf.dumpStatistics(stats);
        assertEquals("1.36:0.18:0.25:0.40", result);
    }

    private ScoreData buildScoreData(String score, String adjScore, String min, String max, int buckets) {

        ScoreData.Builder sdb = new ScoreData.Builder();
        sdb.setScore(new BigDecimal(score))
                .setAdjustedScore(new BigDecimal(adjScore))
                .setMinPoints(new BigDecimal(min))
                .setMaxPoints(new BigDecimal(max))
                .setBuckets(buckets);

        return sdb.build();
    }

    private Statistics buildStatistics(String avg, String mean_dev, String med_dev, String std_dev) {

        Statistics.Builder sd = new Statistics.Builder();
        sd.setAverage(new BigDecimal(avg))
          .setMeanDeviation(new BigDecimal(mean_dev))
          .setMedianDeviation(new BigDecimal(med_dev))
          .setStandardDeviation(new BigDecimal(std_dev));

        return sd.build();
    }
}
