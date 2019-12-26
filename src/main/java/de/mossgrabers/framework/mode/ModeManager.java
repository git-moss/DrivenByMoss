// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Manages all modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeManager
{
    private final Map<Modes, Mode>         modes               = new EnumMap<> (Modes.class);
    private final List<ModeChangeListener> modeChangeListeners = new ArrayList<> ();

    private Modes                          activeModeId        = null;
    private Modes                          previousModeId      = null;
    private Modes                          temporaryModeId     = null;
    private Modes                          defaultModeId       = null;


    /**
     * Register a mode.
     *
     * @param modeId The ID of the mode to register
     * @param mode The mode to register
     */
    public void registerMode (final Modes modeId, final Mode mode)
    {
        this.modes.put (modeId, mode);
    }


    /**
     * Get the mode with the given ID.
     *
     * @param modeId An ID
     * @return The mode or null if no mode with that ID is registered
     */
    public Mode getMode (final Modes modeId)
    {
        return this.modes.get (modeId);
    }


    /**
     * Get the mode with the given name.
     *
     * @param modeName The name of a mode
     * @return The mode or null if no mode with that name is registered
     */
    public Modes getMode (final String modeName)
    {
        for (final Entry<Modes, Mode> entry: this.modes.entrySet ())
            if (modeName.equals (entry.getValue ().getName ()))
                return entry.getKey ();
        return null;
    }


    /**
     * Set the active mode. If the mode to activate is only temporary, calling restoreMode sets back
     * the previous active one.
     *
     * @param modeId The ID of the mode to activate
     */
    public void setActiveMode (final Modes modeId)
    {
        final Modes id = modeId == null ? this.defaultModeId : modeId;

        // Do nothing if already active
        if (this.isActiveOrTempMode (id))
            return;

        // Deactivate the current temporary or active mode
        if (this.temporaryModeId != null)
        {
            this.getMode (this.temporaryModeId).onDeactivate ();
            this.temporaryModeId = null;
        }
        else if (this.activeModeId != null)
            this.getMode (this.activeModeId).onDeactivate ();

        // Activate the new temporary or active mode
        if (id == null)
        {
            this.previousModeId = this.activeModeId;
            this.activeModeId = null;
        }
        else
        {
            final Mode newMode = this.getMode (id);
            if (newMode.isTemporary ())
                this.temporaryModeId = id;
            else
            {
                this.previousModeId = this.activeModeId;
                this.activeModeId = id;
            }
            newMode.onActivate ();
        }

        this.notifyObservers (this.previousModeId, this.getActiveOrTempModeId ());
    }


    /**
     * Set the previous mode. Will be used for restoreMode.
     *
     * @param mode The previous mode
     */
    public void setPreviousMode (final Modes mode)
    {
        this.previousModeId = mode;
    }


    /**
     * Get the ID of the active mode.
     *
     * @return The ID
     */
    public Modes getActiveModeId ()
    {
        return this.activeModeId;
    }


    /**
     * Checks if the mode with the given ID is the active mode.
     *
     * @param modeId An ID
     * @return True if active
     */
    public boolean isActiveMode (final Modes modeId)
    {
        return this.getActiveModeId () == modeId;
    }


    /**
     * Get the active or temporary mode if active.
     *
     * @return The mode
     */
    public Mode getActiveOrTempMode ()
    {
        return this.modes.get (this.getActiveOrTempModeId ());
    }


    /**
     * Get the ID of the active mode or the temporary mode if active.
     *
     * @return The ID
     */
    public Modes getActiveOrTempModeId ()
    {
        return this.temporaryModeId == null ? this.activeModeId : this.temporaryModeId;
    }


    /**
     * Checks if one of the mode IDs is the active mode or the temporary mode.
     *
     * @param modeIds Several IDs
     * @return True if active
     */
    public boolean isActiveOrTempMode (final Modes... modeIds)
    {
        for (final Modes modeID: modeIds)
        {
            if (this.isActiveOrTempMode (modeID))
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
    public boolean isActiveOrTempMode (final Modes modeId)
    {
        return this.getActiveOrTempModeId () == modeId;
    }


    /**
     * Get the ID of the previous mode.
     *
     * @return The ID of the previous mode
     */
    public Modes getPreviousModeId ()
    {
        return this.previousModeId;
    }


    /**
     * Set the previous mode as the active one.
     */
    public void restoreMode ()
    {
        // Deactivate the current temporary or active mode
        Modes oldModeId = null;
        if (this.temporaryModeId != null)
        {
            oldModeId = this.temporaryModeId;
            this.getMode (this.temporaryModeId).onDeactivate ();
            this.temporaryModeId = null;
            Mode mode = this.getMode (this.activeModeId);
            if (mode == null)
            {
                this.activeModeId = this.defaultModeId;
                mode = this.getMode (this.activeModeId);
            }
            mode.onActivate ();
        }
        else if (this.previousModeId != null)
        {
            oldModeId = this.activeModeId;
            this.getMode (this.activeModeId).onDeactivate ();
            this.activeModeId = this.previousModeId;
            Mode mode = this.getMode (this.activeModeId);
            if (mode == null)
            {
                this.activeModeId = this.defaultModeId;
                mode = this.getMode (this.activeModeId);
            }
            mode.onActivate ();
        }

        if (oldModeId != null)
            this.notifyObservers (oldModeId, this.activeModeId);
    }


    /**
     * Sets the default mode.
     *
     * @param modeId The ID of the default mode
     */
    public void setDefaultMode (final Modes modeId)
    {
        this.defaultModeId = modeId;
    }


    /**
     * Register a listener which gets notified if the active mode has changed.
     *
     * @param listener The listener to register
     */
    public void addModeListener (final ModeChangeListener listener)
    {
        this.modeChangeListeners.add (listener);
    }


    /**
     * Notify all mode change observers.
     *
     * @param oldMode The old mode
     * @param newMode The new mode
     */
    private void notifyObservers (final Modes oldMode, final Modes newMode)
    {
        for (final ModeChangeListener listener: this.modeChangeListeners)
            listener.call (oldMode, newMode);
    }
}
