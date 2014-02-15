package net.prank.core;

import java.io.Serializable;

/**
 * Keep track of the original index of a solution in a collection and
 * an adjusted index after some preferred sort algorithm. This is useful
 * for identifying differences between 3rd party ordering and localized
 * ordering.
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
public class Indices
    implements Serializable {

    private static final long serialVersionUID = 42L;
    /** The original index of a solution in a collection */
    private final int _originalIndex;
    /** The index after a specific collection sort */
    private final int _adjustedIndex;

    public Indices(int originalIndex) {
        _originalIndex = originalIndex;
        _adjustedIndex = originalIndex;
    }

    public Indices(int original, int adjusted) {
        _originalIndex = original;
        _adjustedIndex = adjusted;
    }

    public int getOriginalIndex() {
        return _originalIndex;
    }

    public int getAdjustedIndex() {
        return _adjustedIndex;
    }

    public Indices updateAdjusted(int updatedAdjusted) {
        return new Indices(_originalIndex, updatedAdjusted);
    }

    @Override
    public String toString() {
        return "Indices{" +
                "_originalIndex=" + _originalIndex +
                ", _adjustedIndex=" + _adjustedIndex +
                '}';
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

        Indices indices = (Indices) o;

        if ( _adjustedIndex != indices._adjustedIndex )
        {
            return false;
        }

        if ( _originalIndex != indices._originalIndex )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _originalIndex;
        result = 31 * result + _adjustedIndex;
        return result;
    }
}
