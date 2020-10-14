// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.FeatureGroupManager;
import de.mossgrabers.framework.featuregroup.Mode;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.ArrayList;
import java.util.List;


/**
 * Manages all modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeManager extends FeatureGroupManager<Modes, Mode>
{
    private final List<ModeChangeListener> modeChangeListeners   = new ArrayList<> ();
    private final List<ModeManager>        connectedModeManagers = new ArrayList<> ();

    private Modes                          temporaryModeId       = null;
    private Modes                          defaultModeId         = null;


    /**
     * Constructor.
     */
    public ModeManager ()
    {
        super (Modes.class);
    }


    /**
     * Set the active mode. If the mode to activate is only temporary, calling restoreMode sets back
     * the previous active one.
     *
     * @param modeId The ID of the mode to activate
     */
    public void setActive (final Modes modeId)
    {
        this.setActive (modeId, true);
    }


    /**
     * Set the active mode. If the mode to activate is only temporary, calling restoreMode sets back
     * the previous active one.
     *
     * @param modeId The ID of the mode to activate
     * @param syncSiblings Sync changes to siblings if true
     */
    private void setActive (final Modes modeId, final boolean syncSiblings)
    {
        final Modes id = modeId == null ? this.defaultModeId : modeId;
        if (id == null)
            throw new FrameworkException ("Attempt to set the active mode to null and no default mode is registered.");

        // Do nothing if already active
        if (this.isActiveOrTemp (id))
            return;

        // Deactivate the current temporary or active mode
        final Modes deactivate = this.temporaryModeId != null ? this.temporaryModeId : this.activeID;
        if (deactivate != null)
            this.get (deactivate).onDeactivate ();
        this.temporaryModeId = null;

        // Activate the new temporary or active mode
        final Mode newMode = this.get (id);
        if (newMode.isTemporary ())
            this.temporaryModeId = id;
        else
        {
            this.previousID = this.activeID;
            this.activeID = id;
        }
        newMode.onActivate ();

        if (syncSiblings)
            this.connectedModeManagers.forEach (sibling -> sibling.setActive (modeId, false));

        this.notifyObservers (this.previousID, this.getActiveOrTempId ());
    }


    /**
     * Set the previous mode. Will be used for restoreMode.
     *
     * @param mode The previous mode
     */
    public void setPrevious (final Modes mode)
    {
        this.setPrevious (mode, true);
    }


    /**
     * Set the previous mode. Will be used for restoreMode.
     *
     * @param mode The previous mode
     * @param syncSiblings Sync changes to siblings if true
     */
    private void setPrevious (final Modes mode, final boolean syncSiblings)
    {
        this.previousID = mode;
        if (syncSiblings)
            this.connectedModeManagers.forEach (sibling -> sibling.setPrevious (mode, false));
    }


    /**
     * Checks if the mode with the given ID is the active mode.
     *
     * @param modeId An ID
     * @return True if active
     */
    public boolean isActive (final Modes modeId)
    {
        return this.getActiveId () == modeId;
    }


    /**
     * Get the active or temporary mode if active.
     *
     * @return The mode
     */
    public Mode getActiveOrTemp ()
    {
        return this.featureGroups.get (this.getActiveOrTempId ());
    }


    /**
     * Get the ID of the active mode or the temporary mode if active.
     *
     * @return The ID
     */
    public Modes getActiveOrTempId ()
    {
        return this.temporaryModeId == null ? this.activeID : this.temporaryModeId;
    }


    /**
     * Checks if one of the mode IDs is the active mode or the temporary mode.
     *
     * @param modeIds Several IDs
     * @return True if active
     */
    public boolean isActiveOrTemp (final Modes... modeIds)
    {
        for (final Modes modeID: modeIds)
        {
            if (this.isActiveOrTemp (modeID))
                return true;
        }
        return false;
    }


    /**
     * Checks if the mode with the given ID is the active mode or the temporary mode if active.
     *
     * @param modeId An ID
     * @return True if active
     */
    public boolean isActiveOrTemp (final Modes modeId)
    {
        return this.getActiveOrTempId () == modeId;
    }


    /**
     * Get the ID of the previous mode.
     *
     * @return The ID of the previous mode
     */
    public Modes getPreviousId ()
    {
        return this.previousID;
    }


    /**
     * Set the previous mode as the active one.
     */
    public void restore ()
    {
        this.restore (true);
    }


    /**
     * Set the previous mode as the active one.
     *
     * @param syncSiblings Sync changes to siblings if true
     */
    private void restore (final boolean syncSiblings)
    {
        // Deactivate the current temporary or active mode
        Modes oldModeId = null;
        if (this.temporaryModeId != null)
        {
            oldModeId = this.temporaryModeId;
            this.get (this.temporaryModeId).onDeactivate ();
            this.temporaryModeId = null;
            Mode mode = this.get (this.activeID);
            if (mode == null)
            {
                this.activeID = this.defaultModeId;
                mode = this.get (this.activeID);
            }
            mode.onActivate ();
        }
        else if (this.previousID != null)
        {
            oldModeId = this.activeID;
            this.get (this.activeID).onDeactivate ();
            this.activeID = this.previousID;
            Mode mode = this.get (this.activeID);
            if (mode == null)
            {
                this.activeID = this.defaultModeId;
                mode = this.get (this.activeID);
            }
            mode.onActivate ();
        }

        if (syncSiblings)
            this.connectedModeManagers.forEach (sibling -> sibling.restore (false));

        if (oldModeId != null)
            this.notifyObservers (oldModeId, this.activeID);
    }


    /**
     * Sets the default mode.
     *
     * @param modeId The ID of the default mode
     */
    public void setDefault (final Modes modeId)
    {
        this.defaultModeId = modeId;
    }


    /**
     * Register a listener which gets notified if the active mode has changed.
     *
     * @param listener The listener to register
     */
    public void addChangeListener (final ModeChangeListener listener)
    {
        this.modeChangeListeners.add (listener);
    }


    /**
     * Register another mode manager. If a mode changes all mode states are synchronized to the
     * registered sibling.
     *
     * @param sibling Another mode manager to keep in sync
     */
    public void addConnectedManagerListener (final ModeManager sibling)
    {
        this.connectedModeManagers.add (sibling);
    }


    /**
     * Notify all mode change observers.
     *
     * @param oldMode The old mode
     * @param newMode The new mode
     */
    private void notifyObservers (final Modes oldMode, final Modes newMode)
    {
        this.modeChangeListeners.forEach (l -> l.call (oldMode, newMode));
    }
}
