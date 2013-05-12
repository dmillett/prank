package net.prank;


import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Tools to build a Set of ScoreRange objects.
 */
public class ScoringTool {

    private static final Logger LOG = Logger.getLogger(ScoringTool.class);

    /**
     * Split the range of grossMax - grossMin and divide it evenly by the sliceCount.
     * Use for larger ranges.
     *
     * @param minPoints
     * @param maxPoints
     * @param sliceCount
     * @param grossMin
     * @param grossMax
     * @return
     */
    public Set<ScoringRange> scoreSlicesEvenlyLowValueAsHighScore(double minPoints, double maxPoints, int sliceCount,
                                                                  double grossMin, double grossMax) {

        Set<ScoringRange> scores = new HashSet<ScoringRange>();

        double pointsPerSlice = (maxPoints - minPoints) / sliceCount;
        double range = (grossMax - 1) - (grossMin + 1);
        double sliceRange = range / sliceCount;

        scores.add(new ScoringRange(grossMin, grossMin, maxPoints));
        scores.add(new ScoringRange(grossMax, grossMax, minPoints));

        if (sliceCount < 3) {
            double averagePoints = minPoints + maxPoints / 2;
            scores.add(new ScoringRange(grossMin + 1, grossMax - 1, averagePoints));
            return scores;
        }

        double minRange = grossMin + 1;
        double maxRange = minRange + sliceRange;
        int maxSlice = sliceCount - 1;

        for (int i = maxSlice; i > 0; i--) {
            double points = i * pointsPerSlice;
            scores.add(new ScoringRange(minRange, maxRange, points));
            minRange = maxRange;
            maxRange += sliceRange;
        }

        return scores;
    }

    /**
     * Create a String dump from a Set of ScoringRange where each is separated by a newline.
     *
     * @param scoringRanges
     * @param contextInfo
     * @return
     */
    public String dumpScoringRanges(Set<ScoringRange> scoringRanges, String contextInfo) {

        String msg = contextInfo != null ? contextInfo : "";
        StringBuilder sb = new StringBuilder(msg);
        sb.append(" ");

        for (ScoringRange range : scoringRanges) {
            String output =
                    String.format("min: %1f, max: %2f, score: %3f", range.getMin(), range.getMax(), range.getScorePoints());

            sb.append(output).append("\n");
        }

        return sb.toString();
    }

    public double getScoreFromRange(double value, Set<ScoringRange> scoringRange) {

        for (ScoringRange range : scoringRange) {
            if (range.withinRange(value)) {
                return range.getScorePoints();
            }
        }

        return 0.0;
    }
}

