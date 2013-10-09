package net.prank;

/**
 * Build request options to accompany each ScoreCard evaluation in Prankster.
 * Unless otherwise specified via the builder, the defaults are:
 * 1) _minPoints = 0.0
 * 2) _maxPoints = 10.0
 * 3) _bucketCount = 10
 * 4) _enabled = true
 * 5) _timeoutMillis = 50
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
public class RequestOptions {

    /** Defaults to 0.0 */
    private final double _minPoints;
    /** Defaults to 10.0 */
    private final double _maxPoints;
    /** Defaults to 10 */
    private final int _bucketCount;
    /** Defaults to 'true' */
    private final boolean _enabled;
    /** Defaults to 50 milliseconds */
    private final long _timeoutMillis;

    public RequestOptions(double minPoints, double maxPoints, int bucketCount, boolean enabled, long timeoutMillis) {
        _minPoints = minPoints;
        _maxPoints = maxPoints;
        _bucketCount = bucketCount;
        _enabled = enabled;
        _timeoutMillis = timeoutMillis;
    }

    public double getMinPoints() {
        return _minPoints;
    }

    public double getMaxPoints() {
        return _maxPoints;
    }

    public int getBucketCount() {
        return _bucketCount;
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public long getTimeoutMillis() {
        return _timeoutMillis;
    }

    /**
     * Use this if any of the defaults are acceptable, otherwise specify every value
     * in the RequestOptions constructor.
     */
    public static class RequestOptionsBuilder {

        private double _minPointsB = 0.0;
        private double _maxPointsB = 10.0;
        private int _bucketCountB = 10;
        private boolean _enabledB = true;
        private long _timeoutMillisB = 50;

        public RequestOptionsBuilder setMinPointsB(double minPointsB) {
            _minPointsB = minPointsB;
            return this;
        }

        public RequestOptionsBuilder setMaxPointsB(double maxPointsB) {
            _maxPointsB = maxPointsB;
            return this;
        }

        public RequestOptionsBuilder setBucketCountB(int bucketCountB) {
            _bucketCountB = bucketCountB;
            return this;
        }

        public RequestOptionsBuilder setEnabledB(boolean enabledB) {
            _enabledB = enabledB;
            return this;
        }

        public RequestOptionsBuilder setTimeoutMillisB(long timeoutMillisB) {
            _timeoutMillisB = timeoutMillisB;
            return this;
        }

        public RequestOptions build() {
            return new RequestOptions(_minPointsB, _maxPointsB, _bucketCountB, _enabledB, _timeoutMillisB);
        }
    }
}
