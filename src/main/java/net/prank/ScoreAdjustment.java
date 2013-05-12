package net.prank;


/**
 *
 */
public class ScoreAdjustment {

    /**
     * Adjusts the 'adjusted score' as the result of the "score * factor", where 'score'
     * is the original score and is never modified.
     *
     * @param originalResult
     * @param factor
     * @return
     */
    public Result adjustScoreByFactor(Result originalResult, double factor) {

        if (originalResult == null) {
            return originalResult;
        }

        Result.ResultBuilder builder = new Result.ResultBuilder(originalResult);
        double adjusted = originalResult.getScore() * factor;
        builder.adjustScore(adjusted);

        return builder.build();
    }
}

