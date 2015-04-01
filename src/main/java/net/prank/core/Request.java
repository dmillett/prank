package net.prank.core;

import java.util.HashMap;
import java.util.Map;

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
    /** Whether or not to globally ignore scoring for this request */
    private final boolean _disabled;

    public Request(boolean disabled) {
        _disabled = disabled;
        _requestObject = null;
        _options = new HashMap<String, RequestOptions>();
    }

    /** Using this relies on options specified in each ScoreCard implementation */
    public Request(T requestObject) {
        _requestObject = requestObject;
        _options = new HashMap<String, RequestOptions>();
        _disabled = false;
    }

    /** Use specific scoring options that override defaults in ScoreCard implementation */
    public Request(T requestObject, Map<String, RequestOptions> options) {
        _requestObject = requestObject;
        _options = options;
        _disabled = false;
    }

    public boolean isDisabled() {
        return _disabled;
    }

    public T getRequestObject() {
        return _requestObject;
    }

    public Map<String, RequestOptions> getOptions() {
        return _options;
    }

    public RequestOptions getOptionsForScoreCard(String scoreCardName) {

        if (_options != null)
        {
            return _options.get(scoreCardName);
        }

        return null;
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
}

