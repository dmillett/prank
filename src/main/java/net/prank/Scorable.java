package net.prank;

import net.prank.core.ScoreSummary;

/**
 * Use if you are ok updating your scorable object so that
 * you can use a generic comparator on it. Otherwise, just
 * update your model and define a more specific comparator.
 *
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
public interface Scorable {
    /** Contains any ScoreCard's and their scores */
    public ScoreSummary getScoreSummary();
}

