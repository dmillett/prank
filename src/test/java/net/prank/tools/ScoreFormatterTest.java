package net.prank.tools;

import junit.framework.TestCase;
import net.prank.core.Indices;
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

    public void test_dumpIndices() {

        Indices indices = new Indices(10);
        ScoreFormatter scf = new ScoreFormatter();

        assertEquals("10:10", scf.dumpIndices(indices));

        Indices indices2 = new Indices(5, 2);
        assertEquals("5:2", scf.dumpIndices(indices2));
    }

    public void test_dumpScoreData__with_defaults() {

        ScoreData sd = buildScoreData("20.00", "22.50", "0.00", "30.00", 50);
        ScoreFormatter scf = new ScoreFormatter();

        String defaultDump = scf.dumpScoreData(sd);
        assertEquals("20.0000:22.5000:0.0000:30.0000:50", defaultDump);
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
        assertEquals("1.3600:0.1800:0.2500:0.4000", result);
    }

    public void test_dumpStatistics__with_null() {

        Statistics stats = buildStatistics("1.36", "0.18", null, null);
        ScoreFormatter scf = new ScoreFormatter();

        String result = scf.dumpStatistics(stats);
        assertEquals("1.3600:0.1800::", result);
    }

    private ScoreData buildScoreData(String score, String adjScore, String min, String max, int buckets) {

        BigDecimal scoreBd = score != null ? new BigDecimal(score) : null;
        BigDecimal adjScoreBd = adjScore != null ? new BigDecimal(adjScore) : null;
        BigDecimal minBd = min != null ? new BigDecimal(min) : null;
        BigDecimal maxBd = max != null ? new BigDecimal(max) : null;

        ScoreData.Builder sdb = new ScoreData.Builder();
        sdb.setScore(scoreBd)
           .setAdjustedScore(adjScoreBd)
           .setMinPoints(minBd)
           .setMaxPoints(maxBd)
           .setBuckets(buckets);

        return sdb.build();
    }

    private Statistics buildStatistics(String avg, String meanDev, String medDev, String stdDev) {

        BigDecimal avgBd = avg != null ? new BigDecimal(avg) : null;
        BigDecimal meanDevBd = meanDev != null ? new BigDecimal(meanDev) : null;
        BigDecimal medianDevBd = medDev != null ? new BigDecimal(medDev) : null;
        BigDecimal stdDevBd = stdDev != null ? new BigDecimal(stdDev) : null;

        Statistics.Builder sd = new Statistics.Builder();
        sd.setAverage(avgBd)
          .setMeanDeviation(meanDevBd)
          .setMedianDeviation(medianDevBd)
          .setStandardDeviation(stdDevBd);

        return sd.build();
    }
}
