package net.prank;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulate the request object and specify any ScoreCards that are explicitly enabled
 * or disabled from execution in the Prankster thread pools. By default, all defined
 * ScoreCards are executed for any scoring object.
 * <p/>
 * Scenarios:
 * 1) By default, all ScoreCards are enabled
 * 2) A ScoreCard will not setupScoring if it is disabled
 * 3) Only the enabled ScoreCards are executed
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
public class Request<T> {

    /** Usually a generic Collection of items to be scored */
    private final T _requestObject;
    /** Options to use for each ScoreCard submittal to Prankster */
    private final Map<String, RequestOptions> _options;

    /** Using this relies on options specified in each ScoreCard implementation */
    public Request(T requestObject) {

        _requestObject = requestObject;
        RequestOptions options = new RequestOptions.RequestOptionsBuilder().build();
        Map<String, RequestOptions> optionsMap = new HashMap<String, RequestOptions>();
        _options = optionsMap;
    }

    /** Use specific scoring options that override defaults in ScoreCard implementation */
    public Request(T requestObject, Map<String, RequestOptions> options) {
        _requestObject = requestObject;
        _options = options;
    }

    public T getRequestObject() {
        return _requestObject;
    }

    public Map<String, RequestOptions> getOptions() {
        return _options;
    }

    public RequestOptions getOptionsForScoreCard(String scoreCardName) {
        return _options.get(scoreCardName);
    }

    public void addOption(String scoreCardName, RequestOptions options) {

        if ( scoreCardName != null && scoreCardName.trim().length() > 0 )
        {
            _options.put(scoreCardName, options);
        }
    }

    public void addOptions(Map<String, RequestOptions> otherOptions) {

        for (Map.Entry<String, RequestOptions> entry : otherOptions.entrySet())
        {
            addOption(entry.getKey(), entry.getValue());
        }
    }

//    /**
//     * Will get executed unless it is also disabled
//     */
//    private final Set<ScoreCard> _enabledScoreCards;
//    private final Set<ScoreCard> _disabledScoreCards;
//
//    public Request(T requestObject) {
//        this(requestObject, new HashSet<ScoreCard>(), new HashSet<ScoreCard>());
//    }
//
//    public Request(T requestObject, Set<ScoreCard> enabledScoreCards) {
//        this(requestObject, enabledScoreCards, new HashSet<ScoreCard>());
//    }
//
//    public Request(T requestObject, Set<ScoreCard> enabledScoreCards, Set<ScoreCard> disabledScoreCards) {
//        _requestObject = requestObject;
//        _enabledScoreCards = enabledScoreCards;
//        _disabledScoreCards = disabledScoreCards;
//    }
//
//    public void addEnabled(ScoreCard scoreCard) {
//        _enabledScoreCards.add(scoreCard);
//    }
//
//    public void addEnabled(List<ScoreCard> scoreCards) {
//        _enabledScoreCards.addAll(scoreCards);
//    }
//
//    public void addDisabled(ScoreCard scoreCard) {
//        _disabledScoreCards.add(scoreCard);
//    }
//
//    public void addDisabled(List<ScoreCard> scoreCards) {
//        _disabledScoreCards.addAll(scoreCards);
//    }
//
//
//    public Set<ScoreCard> getEnabledScoreCards() {
//        return _enabledScoreCards;
//    }
//
//    public Set<ScoreCard> getDisabledScoreCards() {
//        return _disabledScoreCards;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//
//        if (this == o)
//        {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass())
//        {
//            return false;
//        }
//
//        Request request = (Request) o;
//
//        if (_disabledScoreCards != null ? !_disabledScoreCards.equals(request._disabledScoreCards) :
//            request._disabledScoreCards != null)
//        {
//            return false;
//        }
//
//        if (_enabledScoreCards != null ? !_enabledScoreCards.equals(request._enabledScoreCards) :
//            request._enabledScoreCards != null)
//        {
//            return false;
//        }
//
//        if (_requestObject != null ? !_requestObject.equals(request._requestObject) : request._requestObject != null)
//        {
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//
//        int result = _requestObject != null ? _requestObject.hashCode() : 0;
//        result = 31 * result + (_enabledScoreCards != null ? _enabledScoreCards.hashCode() : 0);
//        result = 31 * result + (_disabledScoreCards != null ? _disabledScoreCards.hashCode() : 0);
//        return result;
//    }
}

