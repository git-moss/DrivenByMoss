// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

/**
 * Interface for notifications about feature group changes.
 *
 * @param <E> The specific type of the ID enumeration
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface FeatureGroupChangeListener<E extends Enum<E>>
{
    /**
     * Called when a feature group changes.
     *
     * @param previousID The ID of the previous feature group
     * @param activeID The ID of the newly activated feature group
     */
    void call (E previousID, E activeID);
}
