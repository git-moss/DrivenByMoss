// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.scale.Scales;

import java.util.Optional;


/**
 * The interface to all data and access to the DAW.
 *
 * @author Jürgen Moßgraber
 */
public interface IModel
{
    /**
     * Get the host.
     *
     * @return The host
     */
    IHost getHost ();


    /**
     * Get the value changer.
     *
     * @return The value changer.
     */
    IValueChanger getValueChanger ();


    /**
     * Get the project.
     *
     * @return The project
     */
    IProject getProject ();


    /**
     * Get the arranger.
     *
     * @return The arranger
     */
    IArranger getArranger ();


    /**
     * Get the marker bank.
     *
     * @return The marker bank
     */
    IMarkerBank getMarkerBank ();


    /**
     * Get the mixer.
     *
     * @return The mixer
     */
    IMixer getMixer ();


    /**
     * Get the transport.
     *
     * @return The transport
     */
    ITransport getTransport ();


    /**
     * Get the groove instance.
     *
     * @return The groove instance
     */
    IGroove getGroove ();


    /**
     * Get the cursor track.
     *
     * @return The cursor track
     */
    ICursorTrack getCursorTrack ();


    /**
     * Get the master track.
     *
     * @return The master track
     */
    IMasterTrack getMasterTrack ();


    /**
     * Get the color manager.
     *
     * @return The color manager
     */
    ColorManager getColorManager ();


    /**
     * Get the scales.
     *
     * @return The scales
     */
    Scales getScales ();


    /**
     * True if there is a selected device.
     *
     * @return True if there is a selected device.
     */
    boolean hasSelectedDevice ();


    /**
     * Get the cursor device.
     *
     * @return The cursor device
     */
    ICursorDevice getCursorDevice ();


    /**
     * Get a specific device.
     *
     * @param deviceID The ID of the device
     * @return The device
     */
    ISpecificDevice getSpecificDevice (DeviceID deviceID);


    /**
     * Get the first drum device of the track.
     *
     * @return The device, never null but needs to checked for existence
     */
    IDrumDevice getDrumDevice ();


    /**
     * Get the first drum device of the track which monitors the given number of drum pads.
     *
     * @param pageSize The size of the drum pad page
     * @return The device
     */
    IDrumDevice getDrumDevice (int pageSize);


    /**
     * Toggles the audio/instrument track bank with the effect track bank.
     */
    void toggleCurrentTrackBank ();


    /**
     * Returns true if the effect track bank is active.
     *
     * @return True if the effect track bank is active
     */
    boolean isEffectTrackBankActive ();


    /**
     * Get the current track bank (audio/instrument track bank or the effect track bank).
     *
     * @return The current track bank
     */
    ITrackBank getCurrentTrackBank ();


    /**
     * Add an observer for changes of the current track bank.
     *
     * @param observer The observer to register
     */
    void addTrackBankObserver (IValueObserver<ITrackBank> observer);


    /**
     * Remove an observer for changes of the current track bank.
     *
     * @param observer The observer to remove
     */
    void removeTrackBankObserver (IValueObserver<ITrackBank> observer);


    /**
     * Get the track bank.
     *
     * @return The track bank
     */
    ITrackBank getTrackBank ();


    /**
     * Get the effect track bank.
     *
     * @return The effect track bank
     */
    ITrackBank getEffectTrackBank ();


    /**
     * Get the application.
     *
     * @return The application
     */
    IApplication getApplication ();


    /**
     * Get the scene bank.
     *
     * @return The scene bank
     */
    ISceneBank getSceneBank ();


    /**
     * Get the browser.
     *
     * @return The browser
     */
    IBrowser getBrowser ();


    /**
     * Returns true if the current track can hold notes. Convenience method.
     *
     * @return True if the current track can hold notes.
     */
    boolean canSelectedTrackHoldNotes ();


    /**
     * Returns true if the cursor device is pointing to a device on the master track.
     *
     * @return True if the cursor device is pointing to a device on the master track
     */
    boolean isCursorDeviceOnMasterTrack ();


    /**
     * Get the selected slot on the selected track, if any.
     *
     * @return The slot or null
     */
    Optional<ISlot> getSelectedSlot ();


    /**
     * Get a new bank for monitoring scenes. Needs to be called once during initialization.
     *
     * @param numScenes The number of scenes in a bank page
     * @return The scene bank
     */
    ISceneBank getSceneBank (final int numScenes);


    /**
     * Get a new bank for monitoring slots on the cursor track. Needs to be called once during
     * initialization.
     *
     * @param numSlots The number of slots in a bank page
     * @return The slot bank
     */
    ISlotBank getSlotBank (int numSlots);


    /**
     * Create or get a new cursor clip.
     *
     * @param cols The columns of the clip
     * @param rows The rows of the clip
     * @return The cursor clip
     */
    INoteClip getNoteClip (int cols, int rows);


    /**
     * Create a new note clip of the given length and activates and starts over-dub.
     *
     * @param track The track which contains the slot
     * @param slot The slot in which to create a clip
     * @param lengthInBeats The length of the new clip
     * @param overdub If true, over-dub is enabled
     */
    void createNoteClip (ITrack track, ISlot slot, int lengthInBeats, boolean overdub);


    /**
     * Record a new note clip.
     *
     * @param track The track which contains the slot
     * @param slot The slot in which to create a clip
     */
    void recordNoteClip (ITrack track, ISlot slot);


    /***
     * Create or get the default cursor clip.
     *
     * @return The cursor clip
     */
    INoteClip getCursorClip ();


    /**
     * If there was no clip for a sequencer created, ensure that there is at least one cursor clip
     * e.g. for quantization and clip modifications.
     */
    void ensureClip ();


    /**
     * Returns true if there is a selected audio clip which can be split.
     *
     * @return True if can be split
     */
    boolean canConvertClip ();


    /**
     * Returns true if session recording is enabled, a clip is recording or overdub is enabled.
     *
     * @return True if recording
     */
    boolean hasRecordingState ();
}