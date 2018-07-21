// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages all modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeManager
{
    private final Map<Integer, Mode>       modes               = new HashMap<> ();
    private final List<ModeChangeListener> modeChangeListeners = new ArrayList<> ();

    private Integer                        activeModeId        = null;
    private Integer                        previousModeId      = null;
    private Integer                        temporaryModeId     = null;
    private Integer                        defaultModeId       = null;


    /**
     * Register a mode.
     *
     * @param modeId The ID of the mode to register
     * @param mode The mode to register
     */
    public void registerMode (final Integer modeId, final Mode mode)
    {
        this.modes.put (modeId, mode);
    }


    /**
     * Get the mode with the given ID.
     *
     * @param modeId An ID
     * @return The mode or null if no mode with that ID is registered
     */
    public Mode getMode (final Integer modeId)
    {
        return this.modes.get (modeId);
    }


    /**
     * Set the active mode. If the mode to activate is only temporary, calling restoreMode sets back
     * the previous active one.
     *
     * @param modeId The ID of the mode to activate
     */
    public void setActiveMode (final Integer modeId)
    {
        final Integer id = modeId == null ? this.defaultModeId : modeId;

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
     * Get the ID of the active mode.
     *
     * @return The ID
     */
    public Integer getActiveModeId ()
    {
        return this.activeModeId;
    }


    /**
     * Checks if the mode with the given ID is the active mode.
     *
     * @param modeId An ID
     * @return True if active
     */
    public boolean isActiveMode (final Integer modeId)
    {
        final Integer mode = this.getActiveModeId ();
        return mode != null && mode.equals (modeId);
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
    public Integer getActiveOrTempModeId ()
    {
        return this.temporaryModeId == null ? this.activeModeId : this.temporaryModeId;
    }


    /**
     * Checks if the mode with the given ID is the active mode or the temporary mode if active.
     *
     * @param modeId An ID
     * @return True if active
     */
    public boolean isActiveOrTempMode (final Integer modeId)
    {
        final Integer mode = this.getActiveOrTempModeId ();
        return mode != null && mode.equals (modeId);
    }


    /**
     * Get the ID of the previous mode.
     *
     * @return The ID of the previous mode
     */
    public Integer getPreviousModeId ()
    {
        return this.previousModeId;
    }


    /**
     * Set the previous mode as the active one.
     */
    public void restoreMode ()
    {
        // Deactivate the current temporary or active mode
        Integer oldModeId = null;
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
    public void setDefaultMode (final Integer modeId)
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
    private void notifyObservers (final Integer oldMode, final Integer newMode)
    {
        for (final ModeChangeListener listener: this.modeChangeListeners)
            listener.call (oldMode, newMode);
    }
}
