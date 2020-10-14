// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

import de.mossgrabers.framework.featuregroup.FeatureGroup;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Manages a number of feature groups.
 *
 * @param <E> The specific type of the ID enum
 * @param <F> The specific type of the feature group
 * 
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FeatureGroupManager<E extends Enum<E>, F extends FeatureGroup>
{
    protected final Map<E, F> featureGroups;

    protected E               activeID   = null;
    protected E               previousID = null;


    /**
     * Construcor
     *
     * @param clazz The specific class of the feature group
     */
    public FeatureGroupManager (final Class<E> clazz)
    {
        this.featureGroups = new EnumMap<> (clazz);
    }


    /**
     * Register a feature group.
     *
     * @param featureGroupId The ID of the feature group to register
     * @param featureGroup The feature group to register
     */
    public void register (final E featureGroupId, final F featureGroup)
    {
        this.featureGroups.put (featureGroupId, featureGroup);
    }


    /**
     * Get the feature group with the given ID.
     *
     * @param featureGroupId An ID
     * @return The feature group or null if no feature group with that ID is registered
     */
    public F get (final E featureGroupId)
    {
        return this.featureGroups.get (featureGroupId);
    }


    /**
     * Get the feature group with the given name.
     *
     * @param featureGroupName The name of a feature group
     * @return The feature group or null if no feature group with that name is registered
     */
    public E get (final String featureGroupName)
    {
        for (final Entry<E, F> entry: this.featureGroups.entrySet ())
            if (featureGroupName.equals (entry.getValue ().getName ()))
                return entry.getKey ();
        return null;
    }


    /**
     * Get the active feature group ID.
     *
     * @return The ID of the active feature group
     */
    public E getActiveId ()
    {
        return this.activeID;
    }
}
