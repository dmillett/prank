package net.prank.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Singleton (via Spring),
 * <p/>
 * 1) Spring configured list of ScoreCard implementations
 * 2) Thread pool for each ScoreCard (core = max peak concurrent searches)
 * 3) ScoreData an object T (List of AirPricingSolution)
 * -- update a Map (ScoreCard, Result) for each solution
 * 4) Build a summary of results (discard or not)
 * <p/>
 * If it is necessary to update the setupScoring points (min, max, slices, or strategy),
 * then update and reload with "reload()" -- probably via JMX or something similar.
 * This Prankster could be instantiated for each search service.
 *
 * @author dmillett
 * <p/>
 * Copyright 2012 David Millett
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Prankster<T> {

    private static final Logger LOG = LoggerFactory.getLogger(Prankster.class);

    private final Map<ScoreCard<T>, ExecutorService> _scoring;
    /**
     * Core should target peak concurrent searches per host
     */
    private final int _corePoolSize;
    /**
     * How long should the future wait before stopping
     */
    public static final long MAX_TIME_MILLIS_PER_SCORE = 50;

    public Prankster(Set<ScoreCard<T>> scoreCards, int corePoolSize) {


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
     * Uses a specified factory class to create thread pools for each ScoreCard. Note the number
     * of core threads per thread pool should be higher than expected current traffic.
     *
     * @param scoreCards
     * @param corePoolSize
     * @param threadPoolFactory
     */
    public Prankster(Set<ScoreCard<T>> scoreCards, int corePoolSize, PrankThreadPoolFactory threadPoolFactory) {

        if (scoreCards != null)
        {
            _scoring = initThreadPools(scoreCards, threadPoolFactory);
            _corePoolSize = corePoolSize;
        }
        else
        {
            LOG.info("No Scoring Thread Pool To Initialize!");
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
     * @param defaultTimeoutInMillis The time to wait for scoring to complete.
     */
    public void updateObjectScore(Request<T> objectsToScore, int defaultTimeoutInMillis) {

        if ( objectsToScore == null)
        {
            LOG.warn("Cannot ScoreData Null Objects!");
            return;
        }

        Set<Future<Result>> futures = setupScoring(objectsToScore);
        List<Long> timeouts = getTimeouts(defaultTimeoutInMillis, objectsToScore.getOptions());
        int current = 0;

        for (Future<Result> future : futures)
        {
            try
            {
                future.get(timeouts.get(current), TimeUnit.MILLISECONDS);
            }
            catch (Throwable t)
            {
                LOG.warn("Failed To Complete Scoring For: " + objectsToScore.getRequestObject());
            }

            current++;
        }
    }

    /**
     * Return a Future with Result of type V. Origin is responsible for maintaining
     * a Set of Future responses and interpreting them. This submits each ScoreCard
     * as a Callable and returns a Future.
     *
     * @param scoreIt A request with optional Options
     * @return A Set of Result Futures
     */
    public Set<Future<Result>> setupScoring(Request<T> scoreIt) {

        Set<Future<Result>> futures = new HashSet<Future<Result>>(_scoring.size());

        for (Map.Entry<ScoreCard<T>, ExecutorService> entry : _scoring.entrySet())
        {
            if (!executeWithScoreCard(entry.getKey(), scoreIt))
            {
                continue;
            }

            ScoreCardCallable<T> callable = new ScoreCardCallable<T>(entry.getKey(), scoreIt);
            Future<Result> future = entry.getValue().submit(callable);
            futures.add(future);
        }

        return futures;
    }


    private List<Long> getTimeouts(long defaultTimeoutMilis, Map<String, RequestOptions> options) {

        if ( options == null )
        {
            List<Long> timeouts = new ArrayList<Long>();
            for (ScoreCard<T> card : _scoring.keySet())
            {
                timeouts.add(defaultTimeoutMilis);
            }

            return timeouts;
        }

        List<Long> perRequestTimeouts = new ArrayList<Long>();

        for (Map.Entry<String, RequestOptions> entry : options.entrySet())
        {
            if (entry.getValue().isEnabled())
            {
                perRequestTimeouts.add(entry.getValue().getTimeoutMillis());
            }
        }

        return perRequestTimeouts;
    }

    private boolean executeWithScoreCard(ScoreCard scoreCard, Request request) {

        RequestOptions options = request.getOptionsForScoreCard(scoreCard.getName());

        if (options != null && !options.isEnabled())
        {
            return false;
        }

        return true;
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

    private Map<ScoreCard<T>, ExecutorService> initThreadPools(Set<ScoreCard<T>> scoreCards,
                                                               PrankThreadPoolFactory threadPoolFactory) {

        Map<ScoreCard<T>, ExecutorService> scoring = new HashMap<ScoreCard<T>, ExecutorService>(scoreCards.size());

        for ( ScoreCard<T> scoreCard : scoreCards )
        {
            scoring.put(scoreCard, threadPoolFactory.createThreadPool());
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
        private final Request<T> _request;


        private ScoreCardCallable(ScoreCard<T> scoreCard, Request<T> request) {
            _scoreCard = scoreCard;
            _request = request;
        }

        public ScoreSummary call()
            throws Exception {

            if (_request.getOptions() == null)
            {
                return _scoreCard.score(_request.getRequestObject());
            }

            RequestOptions options = _request.getOptionsForScoreCard(_scoreCard.getName());
            return _scoreCard.scoreWith(_request.getRequestObject(), options);
        }
    }
}

