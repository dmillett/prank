package net.prank;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Singleton (via Spring),
 * <p/>
 * 1) Spring configured list of ScoreCard implementations
 * 2) Thread pool for each ScoreCard (core = max peak concurrent searches)
 * 3) Score an object T (List of AirPricingSolution)
 * -- update a Map (ScoreCard, Result) for each solution
 * 4) Build a summary of results (discard or not)
 * <p/>
 * If it is necessary to update the setupScoring points (min, max, slices, or strategy),
 * then update and reload with "reload()" -- probably via JMX or something similar.
 * This ScoreKeeper could be instantiated for each search service.
 *
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
public class ScoreKeeper<T> {

    private static final Logger LOG = Logger.getLogger(ScoreKeeper.class);

    private final Map<ScoreCard<T>, ExecutorService> _scoring;
    /**
     * Core should target peak concurrent searches per host
     */
    private final int _corePoolSize;
    /**
     * How long should the future wait before stopping
     */
    private final long _maxTimeMillisPerScore;

    public ScoreKeeper(Set<ScoreCard<T>> scoreCards, int corePoolSize) {

        _maxTimeMillisPerScore = 50;

        if (scoreCards != null)
        {
            LOG.info("Initializing " + scoreCards.size() + " Scoring Thread Pools");
            _scoring = initFixedThreadPools(scoreCards, corePoolSize);
            _corePoolSize = corePoolSize;
        }
        else
        {
            LOG.info("No Scoring Thread Pools To Initialize");
            _scoring = new HashMap<ScoreCard<T>, ExecutorService>(0);
            _corePoolSize = 0;
        }
    }

    /**
     * Stop all existing thread pools and clear out the scoring map.
     * Reinitialize a new map of setupScoring cards.
     *
     * @param scoreCards
     */
    public synchronized void reload(Set<ScoreCard<T>> scoreCards) {

        gameOver();
        Map<ScoreCard<T>, ExecutorService> rematch = initFixedThreadPools(scoreCards, _corePoolSize);
        _scoring.putAll(rematch);
    }

    public synchronized void reload(Set<ScoreCard<T>> scoreCards, int corePoolSize) {

        gameOver();
        Map<ScoreCard<T>, ExecutorService> rematch = initFixedThreadPools(scoreCards, corePoolSize);
        _scoring.putAll(rematch);
    }

    /**
     * Shut down each of teh setupScoring card thread pools.
     */
    public synchronized void gameOver() {

        if (_scoring != null && !_scoring.isEmpty())
        {
            for (Map.Entry<ScoreCard<T>, ExecutorService> entry : _scoring.entrySet())
            {
                if (entry.getValue().isShutdown() || entry.getValue().isTerminated())
                {
                    continue;
                }

                entry.getValue().shutdown();
            }

            _scoring.clear();
        }
    }

    /**
     * Submit request and scorables to the work queues and wait the specified time for
     * them to complete scoring. This eats all exceptions, but there is a chance some
     * or all of the objects will not be scored.
     *
     * @param objectsToScore A single object or collection of objects with type T
     * @param timeoutInMillis The time to wait for scoring to complete.
     */
    public void updateObjectScore(Request<T> objectsToScore, int timeoutInMillis) {

        if ( objectsToScore == null)
        {
            LOG.warn("Cannot Score Null Objects!");
            return;
        }

        Set<Future<Result>> futures = setupScoring(objectsToScore);

        for (Future<Result> future : futures)
        {
            try
            {
                // Many of the ScoreCard futures were already submitted and should be in progress
                future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
            }
            catch (Exception e)
            {
                LOG.warn("Failed To Complete Scoring For: " + objectsToScore.getRequestObject());
            }
        }
    }

    /**
     * Return a Future with Result of type V. Origin is responsible for maintaining
     * a Set of Future responses and interpreting them.
     *
     * @param scoreIt
     * @return
     */
    public Set<Future<Result>> setupScoring(Request<T> scoreIt) {

        Set<Future<Result>> futures = new HashSet<Future<Result>>(_scoring.size());

        for (Map.Entry<ScoreCard<T>, ExecutorService> entry : _scoring.entrySet())
        {
            if (!executeScoreCard(entry.getKey(), scoreIt.getEnabledScoreCards(), scoreIt.getDisabledScoreCards()))
            {
                continue;
            }

            ScoreCardCallable<T> callable = new ScoreCardCallable<T>(entry.getKey(), scoreIt.getRequestObject());
            Future<Result> future = entry.getValue().submit(callable);
            futures.add(future);
        }

        return futures;
    }

    /**
     * Determines if a ScoreCard and request object should be submitted to the executor pool.
     * <p/>
     * true if enabled & disabled are empty
     * false if disabled contains scoreCard
     * true if enabled contains scoreCard and disabled does not
     * otherwise false
     *
     * @param scoreCard
     * @param enabled
     * @param disabled
     * @return
     */
    private boolean executeScoreCard(ScoreCard scoreCard, Set<ScoreCard> enabled, Set<ScoreCard> disabled) {

        if (enabled.isEmpty() && disabled.isEmpty())
        {
            return true;
        }

        if (containsScoreCard(scoreCard, disabled))
        {
            return false;
        }

        if (containsScoreCard(scoreCard, enabled))
        {
            return true;
        }

        return false;
    }

    /**
     * If a set of ScoreCard's contains a specific ScoreCard (check by equals(), check by name).
     *
     * @param scoreCard
     * @param scoreCards
     * @return
     */
    private boolean containsScoreCard(ScoreCard scoreCard, Set<ScoreCard> scoreCards) {

        if (scoreCards.contains(scoreCard))
        {
            return true;
        }

        for (ScoreCard card : scoreCards)
        {
            if (scoreCard.getName().equals(card.getName()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Create fixed thread pool for each ScoreCard where the max threads = 2 * corePoolSize.
     *
     * @param scoreCards   ScoreCards to use for request object T
     * @param corePoolSize the core pool size
     * @return The ScoreCard : Fixed Thread Pool map
     */
    private Map<ScoreCard<T>, ExecutorService> initFixedThreadPools(Set<ScoreCard<T>> scoreCards, int corePoolSize) {

        int maxThreads = corePoolSize * 2;
        Map<ScoreCard<T>, ExecutorService> scoring = new HashMap<ScoreCard<T>, ExecutorService>(scoreCards.size());

        for (ScoreCard scoreCard : scoreCards)
        {
            scoring.put(scoreCard, Executors.newFixedThreadPool(maxThreads));
        }

        return scoring;
    }

    /**
     * Create a Callable from a stateless setupScoring card execution and some object to setupScoring (T).
     *
     * @param <T> The object to setupScoring.
     */
    private class ScoreCardCallable<T>
        implements Callable {

        private final ScoreCard<T> _scoreCard;
        private final T _objectToScore;

        private ScoreCardCallable(ScoreCard<T> scoreCard, T objectToScore) {

            _scoreCard = scoreCard;
            _objectToScore = objectToScore;
        }

        public ScoreSummary call()
            throws Exception {

            return _scoreCard.score(_objectToScore);
        }
    }
}

