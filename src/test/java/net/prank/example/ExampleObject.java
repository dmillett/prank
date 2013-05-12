package net.prank.example;

import net.prank.ScoreSummary;

import java.math.BigDecimal;

/**
 *
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
