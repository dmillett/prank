package net.prank.tools;

import net.prank.core.Result;

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
public class ScoreAdjustment {

    /**
     * Adjusts the 'adjusted setupScoring' as the result of the "setupScoring * factor", where 'setupScoring'
     * is the original setupScoring and is never modified.
     *
     * @param originalResult
     * @param factor
     * @return
     */
    public Result adjustScoreByFactor(Result originalResult, double factor) {

        if (originalResult == null)
        {
            return originalResult;
        }

        Result.ResultBuilder builder = new Result.ResultBuilder(originalResult);
        double adjusted = originalResult.getScore().doubleValue() * factor;
        builder.adjustScore(new BigDecimal(String.valueOf(adjusted)));

        return builder.build();
    }
}

