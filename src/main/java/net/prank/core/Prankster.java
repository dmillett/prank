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
            _scoring = new HashMap<>(0);
            _corePoolSize = 0;
        }
    }

    /**
     * Uses a specified factory class to create thread pools for each ScoreCard. Note the number
     * of core threads per thread pool should be higher than expected current traffic.
     *
     * @param scoreCards The score cards to apply
     * @param corePoolSize The number of threads to use in the thread pool, think of concurrent request load
     * @param threadPoolFactory To create a thread pool for the score cards.
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
            _scoring = new HashMap<>(0);
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
    public void updateObjectsWithScores(Request<T> objectsToScore, int defaultTimeoutInMillis) {

        if ( objectsToScore == null || objectsToScore.isDisabled() )
        {
            LOG.warn("Cannot ScoreData Null Objects OR Scoring Is Disabled");
            return;
        }

        Set<ScoringFuture> scoringFutures = buildScoringUpdateFutures(objectsToScore, defaultTimeoutInMillis);
        for (ScoringFuture scoringFuture : scoringFutures)
        {
            long timeout = scoringFuture._timeout;
            try
            {
                scoringFuture._future.get(timeout, TimeUnit.MILLISECONDS);
            }
            catch (Throwable t)
            {
                LOG.warn("Failed To Complete Scoring In time: %s, For: %s", timeout,
                         objectsToScore.getRequestObject(), t);
            }
        }
    }

    /** Create and add Future Runnables to their appropriate executor pool. A generic request and timeout */
    @SuppressWarnings("unchecked")
    public Set<ScoringFuture> buildScoringUpdateFutures(Request<T> request, long defaultTimeoutMillis) {

        if (request == null || request.isDisabled())
        {
            LOG.info("Scoring Request Is Null Or Disabled");
            return new HashSet<>();
        }

        int futuresCount = determineFuturesCount(request);
        Set<ScoringFuture> scoringFutures = new HashSet<>(futuresCount);

        for (Map.Entry<ScoreCard<T>, ExecutorService> entry : _scoring.entrySet())
        {
            if ( !isScoreCardEnableForRequest(entry.getKey(), request) )
            {
                continue;
            }

            long timeout = determineTimeout(defaultTimeoutMillis, entry.getKey().getName(), request.getOptions());
            ScoreRunnable<T> runnable = new ScoreRunnable<>(entry.getKey(), request);
            Future future = entry.getValue().submit(runnable);
            ScoringFuture scoringFuture = new ScoringFuture(future, timeout);
            scoringFutures.add(scoringFuture);
        }

        return scoringFutures;
    }

    /** Use the default timeout or per-request timeout from RequestOptions */
    long determineTimeout(long defaultTimeoutMillis, String cardName, Map<String, RequestOptions> requestOptions) {

        if (requestOptions == null || requestOptions.isEmpty())
        {
            return defaultTimeoutMillis;
        }

        RequestOptions options = requestOptions.get(cardName);
        if (options != null && options.isEnabled())
        {
            return options.getTimeoutMillis();
        }

        return defaultTimeoutMillis;
    }

    /** Check per request options to see if this ScoreCard is enabled */
    private boolean isScoreCardEnableForRequest(ScoreCard scoreCard, Request<T> request) {

        if (request.getOptions().isEmpty())
        {
            return true;
        }

        RequestOptions options = request.getOptionsForScoreCard(scoreCard.getName());
        return options != null && options.isEnabled();

    }

    /** How many score cards will be used? */
    private int determineFuturesCount(Request<T> scoreIt) {
        return scoreIt.getOptions().isEmpty() ? _scoring.size() : scoreIt.getOptions().size();
    }

    /**
     * Create fixed thread pool for each ScoreCard where the max threads = 2 * corePoolSize.
     *
     * @param scoreCards   ScoreCards to use for request object T
     * @param corePoolSize the core pool size
     * @return The ScoreCard : Fixed Thread Pool map
     */
    @SuppressWarnings("unchecked")
    private Map<ScoreCard<T>, ExecutorService> initFixedThreadPools(Set<ScoreCard<T>> scoreCards, int corePoolSize) {

        int maxThreads = corePoolSize * 2;
        Map<ScoreCard<T>, ExecutorService> scoring = new HashMap<>(scoreCards.size());

        for ( ScoreCard scoreCard : scoreCards )
        {
            LOG.info("Initializing ScoreCard: %s, With Max Threads %s", scoreCard, maxThreads);
            scoring.put(scoreCard, Executors.newFixedThreadPool(maxThreads));
        }

        return scoring;
    }

    /** Setup a thread pool executor service for each ScoreCard */
    private Map<ScoreCard<T>, ExecutorService> initThreadPools(Set<ScoreCard<T>> scoreCards,
                                                               PrankThreadPoolFactory threadPoolFactory) {

        Map<ScoreCard<T>, ExecutorService> scoring = new HashMap<>(scoreCards.size());
        for ( ScoreCard<T> scoreCard : scoreCards )
        {
            scoring.put(scoreCard, threadPoolFactory.createThreadPool());
        }

        return scoring;
    }

    /** Encapsulates a Future and a Timeout */
    public static class ScoringFuture<T> {

        private final Future<T> _future;
        private final long _timeout;

        private ScoringFuture(Future<T> future, long timeout) {
            _future = future;
            _timeout = timeout;
        }

        public Future<T> getFuture() {
            return _future;
        }

        public long getTimeout() {
            return _timeout;
        }

        @Override
        public boolean equals(Object o) {

            if ( this == o )
            {
                return true;
            }

            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            ScoringFuture that = (ScoringFuture) o;

            if ( _timeout != that._timeout )
            {
                return false;
            }
            return !(_future != null ? !_future.equals(that._future) : that._future != null);

        }

        @Override
        public int hashCode() {
            int result = _future != null ? _future.hashCode() : 0;
            result = 31 * result + (int) (_timeout ^ (_timeout >>> 32));
            return result;
        }
    }

    /**
     * Create a Runnable from a stateless setupScoring card execution and some object to setupScoring (T).
     * Will use 'RequestOptions' if that object is not null.
     *
     * @param <T> The object to setupScoring.
     */
    private class ScoreRunnable<T>
        implements Runnable {

        private final ScoreCard<T> _scoreCard;
        private final Request<T> _request;

        private ScoreRunnable(ScoreCard<T> scoreCard, Request<T> request) {
            _scoreCard = scoreCard;
            _request = request;
        }

        @Override
        public void run() {

            if (_request.getOptions() != null)
            {
                RequestOptions options = _request.getOptionsForScoreCard(_scoreCard.getName());
                if (options != null)
                {
                    _scoreCard.updateObjectsWithScore(_request.getRequestObject(), options);
                    return;
                }
            }

            _scoreCard.updateObjectsWithScore(_request.getRequestObject());
        }
    }

    /**
     * Create a Callable from a stateless setupScoring card execution and some object to setupScoring (T).
     * Will use 'RequestOptions' if that object is not null.
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

            if (_request.getOptions() != null)
            {
                RequestOptions options = _request.getOptionsForScoreCard(_scoreCard.getName());
                if ( options != null )
                {
                    return _scoreCard.scoreWith(_request.getRequestObject(), options);
                }
            }

            return _scoreCard.score(_request.getRequestObject());
        }
    }

    /**
     * Submit request and scorables to the work queues and wait the specified time for
     * them to complete scoring. This eats all exceptions, but there is a chance some
     * or all of the objects will not be scored.
     *
     * @param objectsToScore A single object or collection of objects with type T
     * @param defaultTimeoutInMillis The time to wait for scoring to complete.
     * @deprecated (Targeted removal 2.0), Use 'updateObjectsWithScores()' instead, rename to scoreObject()
     */
    @Deprecated
    public void updateObjectScore(Request<T> objectsToScore, int defaultTimeoutInMillis) {

        if ( objectsToScore == null || objectsToScore.isDisabled() )
        {
            LOG.warn("Cannot ScoreData Null Objects OR Scoring Is Disabled");
            return;
        }

        // todo: Make another internal class that has a Future and Timeout (settings)
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
                LOG.warn("Failed To Complete Scoring In time: %s, For: %s", timeouts.get(current),
                         objectsToScore.getRequestObject());
            }

            current++;
        }
    }

    /** Intended replacement for 'setupScoring()' */
    private Set<ScoringFuture<Result>> buildScoringFutures(Request<T> request, long defaultTimeoutMillis) {

        if (request == null || request.isDisabled())
        {
            LOG.info("Scoring Request Is Null Or Disabled");
            return new HashSet<ScoringFuture<Result>>();
        }

        int futuresCount = determineFuturesCount(request);
        Set<ScoringFuture<Result>> scoringFutures = new HashSet<ScoringFuture<Result>>(futuresCount);

        for (Map.Entry<ScoreCard<T>, ExecutorService> entry : _scoring.entrySet())
        {
            if ( !isScoreCardEnableForRequest(entry.getKey(), request) )
            {
                continue;
            }

            long timeout = determineTimeout(defaultTimeoutMillis, entry.getKey().getName(), request.getOptions());
            ScoreCardCallable<T> callable = new ScoreCardCallable<>(entry.getKey(), request);
            Future future = entry.getValue().submit(callable);
            ScoringFuture<Result> scoringFuture = new ScoringFuture<Result>(future, timeout);
            scoringFutures.add(scoringFuture);
        }

        return scoringFutures;
    }

    /**
     * Return a Future with Result of type V. Origin is responsible for maintaining
     * a Set of Future responses and interpreting them. This submits each ScoreCard
     * as a Callable and returns a Future.
     *
     * @param scoreIt A request with optional Options
     * @return A Set of Result Futures
     * @deprecated (Targeted removal: 2.0) Use 'setupScoringObjects' or 'setupScoringUpdates()' instead
     */
    @Deprecated
    public Set<Future<Result>> setupScoring(Request<T> scoreIt) {

        if ( scoreIt == null || scoreIt.isDisabled() )
        {
            LOG.info("Scoring Request Is Null Or Disabled");
            return new HashSet<>();
        }

        int futuresCount = determineFuturesCount(scoreIt);
        Set<Future<Result>> futures = new HashSet<>(futuresCount);

        for (Map.Entry<ScoreCard<T>, ExecutorService> entry : _scoring.entrySet())
        {
            if ( !isScoreCardEnableForRequest(entry.getKey(), scoreIt) )
            {
                continue;
            }

            ScoreCardCallable<T> callable = new ScoreCardCallable<T>(entry.getKey(), scoreIt);
            Future<Result> future = entry.getValue().submit(callable);
            futures.add(future);
        }

        return futures;
    }

    /** Empty or null options indicate default card use.  */
    @Deprecated
    private List<Long> getTimeouts(long defaultTimeoutMilis, Map<String, RequestOptions> requestOptions) {

        if ( requestOptions == null || requestOptions.isEmpty() )
        {
            List<Long> timeouts = new ArrayList<>();
            for (ScoreCard<T> card : _scoring.keySet())
            {
                timeouts.add(defaultTimeoutMilis);
            }

            return timeouts;
        }

        return getPerRequestTimeouts(requestOptions);
    }

    /** Update ScoreCard timeout from request options */
    @Deprecated
    private List<Long> getPerRequestTimeouts(Map<String, RequestOptions> requestOptions) {

        List<Long> perRequestTimeouts = new ArrayList<>();
        for ( Map.Entry<String, RequestOptions> entry : requestOptions.entrySet() )
        {
            if ( entry.getValue().isEnabled() )
            {
                perRequestTimeouts.add(entry.getValue().getTimeoutMillis());
            }
        }

        return perRequestTimeouts;
    }
}

