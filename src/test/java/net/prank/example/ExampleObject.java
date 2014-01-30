package net.prank.example;

import net.prank.core.ScoreSummary;

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
public class ExampleObject {

    private final BigDecimal _price;
    private final BigDecimal _shippingCost;
    private final int _averageShippingTime;
    private final ScoreSummary _scoreSummary;

    public ExampleObject(int averageShippingTime, BigDecimal shippingCost, BigDecimal price) {
        _averageShippingTime = averageShippingTime;
        _shippingCost = shippingCost;
        _price = price;
        _scoreSummary = new ScoreSummary("ExampleObject");
    }

    public BigDecimal getPrice() {
        return _price;
    }

    public BigDecimal getShippingCost() {
        return _shippingCost;
    }

    public int getAverageShippingTime() {
        return _averageShippingTime;
    }

    public ScoreSummary getScoreSummary() {
        return _scoreSummary;
    }
}
