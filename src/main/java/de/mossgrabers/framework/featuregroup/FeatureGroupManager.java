// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.utils.FrameworkException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Manages a number of feature groups.
 *
 * @param <E> The specific type of the ID enumeration
 * @param <F> The specific type of the feature group
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FeatureGroupManager<E extends Enum<E>, F extends IFeatureGroup>
{
    protected final Map<E, F>                         featureGroups;

    protected E                                       activeID          = null;
    protected E                                       previousID        = null;
    protected E                                       temporaryID       = null;
    protected E                                       defaultID         = null;

    private final List<FeatureGroupChangeListener<E>> changeListeners   = new ArrayList<> ();
    private final List<FeatureGroupManager<E, F>>     connectedManagers = new ArrayList<> ();


    /**
     * Constructor.
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
     * Check if the currently active feature group is temporary.
     *
     * @return True if temporary
     */
    public boolean isTemporary ()
    {
        return this.temporaryID != null;
    }


    /**
     * Set the active feature group.
     *
     * @param featureGroupID The ID of the feature group to activate
     */
    public void setActive (final E featureGroupID)
    {
        this.setActive (featureGroupID, true);
    }


    /**
     * Set the active feature group.
     *
     * @param featureGroupID The ID of the feature group to activate
     * @param syncSiblings Changes sibling feature group managers as well if true
     */
    private void setActive (final E featureGroupID, final boolean syncSiblings)
    {
        final E id = featureGroupID == null ? this.defaultID : featureGroupID;
        if (id == null)
            throw new FrameworkException ("Attempt to set the active feature group to null and no default feature group is registered.");

        // Do nothing if already active
        if (this.isActive (id))
            return;

        // Deactivate the current temporary or active feature group
        final F deactivate = this.getActive ();
        if (deactivate != null)
            deactivate.onDeactivate ();
        this.temporaryID = null;

        // Activate the feature group
        this.previousID = this.activeID;
        this.activeID = id;
        this.get (this.activeID).onActivate ();

        if (syncSiblings)
            this.connectedManagers.forEach (sibling -> sibling.setActive (featureGroupID, false));

        this.notifyObservers (this.previousID, this.activeID);
    }


    /**
     * Set the active feature group temporarily. Calling restore activates the previous active one
     * even if setTemporary was called multiple times.
     *
     * @param featureGroupID The ID of the feature group to activate
     */
    public void setTemporary (final E featureGroupID)
    {
        this.setTemporary (featureGroupID, true);
    }


    /**
     * Set the active feature group. If the feature group to activate is only temporary, calling
     * restore sets back the previous active one.
     *
     * @param featureGroupID The ID of the feature group to activate
     * @param syncSiblings Sync changes to siblings if true
     */
    private void setTemporary (final E featureGroupID, final boolean syncSiblings)
    {
        if (featureGroupID == null)
            throw new FrameworkException ("Attempt to set the temporary feature group to null.");

        // Do nothing if already active
        if (this.isActive (featureGroupID))
            return;

        // Deactivate the current temporary or active feature group
        final F deactivate = this.getActive ();
        if (deactivate != null)
            deactivate.onDeactivate ();

        // Activate the new temporary feature group
        this.temporaryID = featureGroupID;
        final F featureGroup = this.get (this.temporaryID);
        if (featureGroup == null)
            throw new FrameworkException ("Attempt to set the temporary feature group to non-existing: " + featureGroupID);
        featureGroup.onActivate ();

        if (syncSiblings)
            this.connectedManagers.forEach (sibling -> sibling.setActive (featureGroupID, false));

        this.notifyObservers (this.activeID, this.temporaryID);
    }


    /**
     * Check if one of the feature group IDs is the active or temporary feature group.
     *
     * @param featureGroupIDs Several feature group IDs
     * @return True if active
     */
    @SafeVarargs
    public final boolean isActive (final E... featureGroupIDs)
    {
        final E id = this.getActiveID ();
        for (final E featureGroupID: featureGroupIDs)
        {
            if (id == featureGroupID)
                return true;
        }
        return false;
    }


    /**
     * Get the active or temporary feature group ID.
     *
     * @return The ID of the active feature group
     */
    public E getActiveID ()
    {
        return this.temporaryID == null ? this.activeID : this.temporaryID;
    }


    /**
     * Get the active feature group ID.
     *
     * @return The ID of the active feature group
     */
    public E getActiveIDIgnoreTemporary ()
    {
        return this.activeID;
    }


    /**
     * Get the active or temporary feature group if active.
     *
     * @return The feature group
     */
    public F getActive ()
    {
        final E featureGroupID = this.getActiveID ();
        return featureGroupID == null ? null : this.featureGroups.get (featureGroupID);
    }


    /**
     * Sets the default feature group.
     *
     * @param featureGroupID The ID of the default feature group
     */
    public void setDefaultID (final E featureGroupID)
    {
        this.defaultID = featureGroupID;
    }


    /**
     * Get the previous view ID.
     *
     * @return The ID of the previous view
     */
    public E getPreviousID ()
    {
        return this.previousID;
    }


    /**
     * Set the previous feature group.
     *
     * @param featureGroupID The ID of the previous feature group
     */
    public void setPreviousID (final E featureGroupID)
    {
        this.setPrevious (featureGroupID, true);
    }


    /**
     * Set the previous feature group.
     *
     * @param featureGroupID The ID of the previous feature group
     * @param syncSiblings Sync changes to siblings if true
     */
    private void setPrevious (final E featureGroupID, final boolean syncSiblings)
    {
        this.previousID = featureGroupID;

        if (syncSiblings)
            this.connectedManagers.forEach (sibling -> sibling.setPrevious (featureGroupID, false));
    }


    /**
     * Get the previous view.
     *
     * @return The previous view, might be null if not set
     */
    public F getPrevious ()
    {
        return this.previousID == null ? null : this.get (this.previousID);
    }


    /**
     * Set the previous feature group as the active one.
     */
    public void restore ()
    {
        this.restore (true);
    }


    /**
     * Set the previous feature group as the active one.
     *
     * @param syncSiblings Sync changes to siblings if true
     */
    private void restore (final boolean syncSiblings)
    {
        // Deactivate the current temporary or active feature group
        E oldID = null;
        if (this.temporaryID != null)
        {
            oldID = this.temporaryID;
            this.get (this.temporaryID).onDeactivate ();
            this.temporaryID = null;
            F featureGroup = this.get (this.activeID);
            if (featureGroup == null)
            {
                this.activeID = this.defaultID;
                featureGroup = this.get (this.activeID);
            }
            featureGroup.onActivate ();
        }
        else if (this.previousID != null)
        {
            oldID = this.activeID;
            this.get (this.activeID).onDeactivate ();
            this.activeID = this.previousID;
            F featureGroup = this.get (this.activeID);
            if (featureGroup == null)
            {
                this.activeID = this.defaultID;
                featureGroup = this.get (this.activeID);
            }
            featureGroup.onActivate ();
        }

        if (syncSiblings)
            this.connectedManagers.forEach (sibling -> sibling.restore (false));

        if (oldID != null)
            this.notifyObservers (oldID, this.activeID);
    }


    /**
     * Register another manager. If a feature group changes all states are synchronized to the
     * registered siblings.
     *
     * @param sibling Another manager to keep in sync
     */
    public void addConnectedManagerListener (final FeatureGroupManager<E, F> sibling)
    {
        this.connectedManagers.add (sibling);
    }


    /**
     * Register a listener which gets notified if the active feature group has changed.
     *
     * @param listener The listener to register
     */
    public void addChangeListener (final FeatureGroupChangeListener<E> listener)
    {
        this.changeListeners.add (listener);
    }


    /**
     * Notify all feature group change observers.
     *
     * @param oldFeatureGroup The old feature group
     * @param newFeatureGroup The new feature group
     */
    protected void notifyObservers (final E oldFeatureGroup, final E newFeatureGroup)
    {
        this.changeListeners.forEach (l -> l.call (oldFeatureGroup, newFeatureGroup));
    }
}
