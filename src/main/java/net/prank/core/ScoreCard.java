package net.prank.core;

/**
 * A thread pool exists for each configured (spring) ScoreCard where it will examine all
 * the relevant ScoreCards and evaluate them. Implement a separate ScoreCard for ranking
 * for any/all discrete ranking topics.
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
public interface ScoreCard<T> {

    /**
     * Score a single object with default options
     * @param scoringObject The object to score
     * @return The score summary after scoring
     */
    public ScoreSummary score(T scoringObject);
    /**
     * Score a single object with specified options, otherwise use defaults
     * @param scoringObject The object to score
     * @param options The request specific options to use for scoring
     * @return The score summary
     */
    public ScoreSummary scoreWith(T scoringObject, RequestOptions options);

    /**
     * Score and update an object or collection of objects with default options
     * @param scoringObject The object to score
     */
    public void updateObjectsWithScore(T scoringObject);
    /**
     * Score and update an object or collection of objects with specific options
     * @param scoringObject The object to score
     * @param options The request specific scoring parameters
     */
    public void updateObjectsWithScore(T scoringObject, RequestOptions options);

    /**
     * A setupScoring card name to use as a key in ScoreSummary
     * The name of the ScoreCard for reporting, applying, etc
     *
     * @return The name of the ScoreCard
     */
    public String getName();
}

