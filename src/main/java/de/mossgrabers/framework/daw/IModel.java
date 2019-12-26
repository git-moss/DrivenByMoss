// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;


/**
 * The interface to all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
     * Get the first instrument device of the track.
     *
     * @return The device
     */
    ICursorDevice getInstrumentDevice ();


    /**
     * Get the drum device. This is the first instrument of the track and monitors 64 layers.
     *
     * @return The device
     */
    ICursorDevice getDrumDevice64 ();


    /**
     * Get the user parameter bank if any.
     *
     * @return The user parameter bank or null if none exists
     */
    IParameterBank getUserParameterBank ();


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
     * Returns true if the cursor track is pinned (aka does not follow the track selection in the
     * DAW).
     *
     * @return True if the cursor track is pinned
     */
    boolean isCursorTrackPinned ();


    /**
     * Toggles if the cursor track is pinned.
     */
    void toggleCursorTrackPinned ();


    /**
     * Returns true if the cursor device is pointing to a device on the master track.
     *
     * @return True if the cursor device is pointing to a device on the master track
     */
    boolean isCursorDeviceOnMasterTrack ();


    /**
     * Get the selected track from the current track bank, if any.
     *
     * @return The selected track or null
     */
    ITrack getSelectedTrack ();


    /**
     * Get the selected slot on the selected track, if any.
     *
     * @return The slot or null
     */
    ISlot getSelectedSlot ();


    /**
     * Creates a new bank for monitoring scenes.
     *
     * @param numScenes The number of scenes in a bank page
     * @return The scene bank
     */
    ISceneBank createSceneBank (final int numScenes);


    /**
     * Check if any of the tracks is soloed.
     *
     * @return True if there is at least one soloed track
     */
    boolean hasSolo ();


    /**
     * Deactivate all solo states of all tracks.
     */
    void clearSolo ();


    /**
     * Deactivate all mute states of all tracks.
     */
    void clearMute ();


    /**
     * Create or get a new cursor clip.
     *
     * @param cols The columns of the clip
     * @param rows The rows of the clip
     * @return The cursor clip
     */
    INoteClip getNoteClip (int cols, int rows);


    /**
     * Create a new note clip of the given length and acivates and starts overdubbing.
     *
     * @param track The track which contains the slot
     * @param slot The slot in which to create a clip
     * @param lengthInBeats The length of the new clip
     * @param overdub If true, overdub is enabled
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
    IClip getClip ();


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