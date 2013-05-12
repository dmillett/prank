package net.prank;

/**
 * Define a minimum, maximum, and point allotment.
 */
public class ScoringRange {

    private final double _min;
    private final double _max;
    private final double _scorePoints;

    public ScoringRange(double min, double max, double scorePoints) {
        _min = min;
        _max = max;
        _scorePoints = scorePoints;
    }

    /**
     * Max kicks it to the next highest category
     *
     * @param value
     * @return
     */
    public boolean withinRange(double value) {

        if (value >= _min && value <= _max) {
            return true;
        }

        return false;
    }

    public boolean aboveRange(double value) {
        return value > _max;
    }

    public boolean belowRange(double value) {
        return value < _min;
    }

    public double getMin() {
        return _min;
    }

    public double getMax() {
        return _max;
    }

    public double getScorePoints() {
        return _scorePoints;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScoringRange that = (ScoringRange) o;

        if (Double.compare(that._max, _max) != 0) {
            return false;
        }

        if (Double.compare(that._min, _min) != 0) {
            return false;
        }

        if (Double.compare(that._scorePoints, _scorePoints) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result;
        long temp;

        temp = _min != +0.0d ? Double.doubleToLongBits(_min) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = _max != +0.0d ? Double.doubleToLongBits(_max) : 0L;

        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = _scorePoints != +0.0d ? Double.doubleToLongBits(_scorePoints) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));

        return result;
    }

    @Override
    public String toString() {
        return "ScoringRange{" +
                "_min=" + _min +
                ", _max=" + _max +
                ", _scorePoints=" + _scorePoints +
                '}';
    }
}

