package net.prank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Encapsulate the request object and specify any ScoreCards that are explicitly enabled
 * or disabled from execution in the ScoreKeeper thread pools. By default, all defined
 * ScoreCards are executed for any scoring object.
 * <p/>
 * Scenarios:
 * 1) By default, all ScoreCards are enabled
 * 2) A ScoreCard will not score if it is disabled
 * 3) Only the enabled ScoreCards are executed
 */
public class Request<T> {

    private final T _requestObject;
    /**
     * Will get executed unless it is also disabled
     */
    private final Set<ScoreCard> _enabledScoreCards;
    private final Set<ScoreCard> _disabledScoreCards;

    public Request(T requestObject) {
        this(requestObject, new HashSet<ScoreCard>(), new HashSet<ScoreCard>());
    }

    public Request(T requestObject, Set<ScoreCard> enabledScoreCards) {
        this(requestObject, enabledScoreCards, new HashSet<ScoreCard>());
    }

    public Request(T requestObject, Set<ScoreCard> enabledScoreCards, Set<ScoreCard> disabledScoreCards) {
        _requestObject = requestObject;
        _enabledScoreCards = enabledScoreCards;
        _disabledScoreCards = disabledScoreCards;
    }

    public void addEnabled(ScoreCard scoreCard) {
        _enabledScoreCards.add(scoreCard);
    }

    public void addEnabled(List<ScoreCard> scoreCards) {
        _enabledScoreCards.addAll(scoreCards);
    }

    public void addDisabled(ScoreCard scoreCard) {
        _disabledScoreCards.add(scoreCard);
    }

    public void addDisabled(List<ScoreCard> scoreCards) {
        _disabledScoreCards.addAll(scoreCards);
    }

    public T getRequestObject() {
        return _requestObject;
    }

    public Set<ScoreCard> getEnabledScoreCards() {
        return _enabledScoreCards;
    }

    public Set<ScoreCard> getDisabledScoreCards() {
        return _disabledScoreCards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Request request = (Request) o;

        if (_disabledScoreCards != null ? !_disabledScoreCards.equals(request._disabledScoreCards) :
                request._disabledScoreCards != null) {
            return false;
        }

        if (_enabledScoreCards != null ? !_enabledScoreCards.equals(request._enabledScoreCards) :
                request._enabledScoreCards != null) {
            return false;
        }

        if (_requestObject != null ? !_requestObject.equals(request._requestObject) : request._requestObject != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _requestObject != null ? _requestObject.hashCode() : 0;
        result = 31 * result + (_enabledScoreCards != null ? _enabledScoreCards.hashCode() : 0);
        result = 31 * result + (_disabledScoreCards != null ? _disabledScoreCards.hashCode() : 0);
        return result;
    }
}

