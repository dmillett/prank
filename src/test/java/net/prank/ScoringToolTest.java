package net.prank;

import junit.framework.TestCase;

import java.util.Set;

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
public class ScoringToolTest
        extends TestCase {


    public void test__scoreSlicesEvenly() {

        ScoringTool tool = new ScoringTool();

        Set<ScoringRange> scores = tool.scoreSlicesEvenlyLowValueAsHighScore(0, 10, 5, 100.0, 575.0);
        assertNotNull(scores);
    }

    public void test__scoreSlicesEvenly_small_range() {

        ScoringTool tool = new ScoringTool();
        Set<ScoringRange> scores = tool.scoreSlicesEvenlyLowValueAsHighScore(0, 10, 3, 0, 2);
        assertNotNull(scores);
    }
}