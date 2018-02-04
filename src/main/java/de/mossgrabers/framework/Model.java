// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.ApplicationProxy;
import de.mossgrabers.framework.daw.ArrangerProxy;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.daw.CursorClipProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.GrooveProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.MixerProxy;
import de.mossgrabers.framework.daw.SceneBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.Project;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Model
{
    private int                    numTracks;
    private int                    numScenes;
    private int                    numSends;
    private int                    numFilterColumnEntries;
    private int                    numResults;
    private boolean                hasFlatTrackList;

    private ControllerHost         host;
    private ValueChanger           valueChanger;

    protected Scales               scales;
    private ApplicationProxy       application;
    private ArrangerProxy          arranger;
    private MixerProxy             mixer;
    private GrooveProxy            groove;
    private Project                project;
    private BrowserProxy           browser;
    private CursorTrack            cursorTrack;
    private AbstractTrackBankProxy currentTrackBank;
    private TrackBankProxy         trackBank;
    private EffectTrackBankProxy   effectTrackBank;
    private TransportProxy         transport;
    private MasterTrackProxy       masterTrack;

    private ColorManager           colorManager;
    private CursorDeviceProxy      primaryDevice;
    private CursorDeviceProxy      cursorDevice;
    private BooleanValue           masterTrackEqualsValue;


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param numTracks The number of track to monitor (per track bank)
     * @param numScenes The number of scenes to monitor (per scene bank)
     * @param numSends The number of sends to monitor
     * @param numFilterColumnEntries The number of entries in one filter column to monitor
     * @param numResults The number of search results in the browser to monitor
     * @param hasFlatTrackList Don't navigate groups, all tracks are flat
     * @param numParams The number of parameter of a device to monitor
     * @param numDevicesInBank The number of devices to monitor
     * @param numDeviceLayers The number of device layers to monitor
     * @param numDrumPadLayers The number of drum pad layers to monitor
     */
    public Model (final ControllerHost host, final ColorManager colorManager, final ValueChanger valueChanger, final Scales scales, final int numTracks, final int numScenes, final int numSends, final int numFilterColumnEntries, final int numResults, final boolean hasFlatTrackList, final int numParams, final int numDevicesInBank, final int numDeviceLayers, final int numDrumPadLayers)
    {
        this.host = host;
        this.colorManager = colorManager;
        this.valueChanger = valueChanger;

        this.numTracks = numTracks < 0 ? 8 : numTracks;
        this.numScenes = numScenes < 0 ? 8 : numScenes;
        this.numSends = numSends < 0 ? 6 : numSends;
        this.numFilterColumnEntries = numFilterColumnEntries < 0 ? 16 : numFilterColumnEntries;
        this.numResults = numResults < 0 ? 16 : numResults;
        this.hasFlatTrackList = hasFlatTrackList ? true : false;

        this.application = new ApplicationProxy (host);
        this.transport = new TransportProxy (host, valueChanger);
        this.groove = new GrooveProxy (host, valueChanger.getUpperBound ());
        final MasterTrack master = host.createMasterTrack (0);
        this.masterTrack = new MasterTrackProxy (master, valueChanger);

        this.cursorTrack = host.createCursorTrack ("MyCursorTrackID", "The Cursor Track", 0, 0, true);
        this.cursorTrack.isPinned ().markInterested ();

        this.trackBank = new TrackBankProxy (host, valueChanger, this.cursorTrack, this.numTracks, this.numScenes, this.numSends, this.hasFlatTrackList);
        this.effectTrackBank = new EffectTrackBankProxy (host, valueChanger, this.cursorTrack, this.numTracks, this.numScenes, this.trackBank);
        this.primaryDevice = new CursorDeviceProxy (host, this.cursorTrack.createCursorDevice ("FIRST_INSTRUMENT", "First Instrument", this.numSends, CursorDeviceFollowMode.FIRST_INSTRUMENT), valueChanger, this.numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        final PinnableCursorDevice cd = this.cursorTrack.createCursorDevice ("CURSOR_DEVICE", "Cursor device", this.numSends, CursorDeviceFollowMode.FOLLOW_SELECTION);
        this.cursorDevice = new CursorDeviceProxy (host, cd, valueChanger, this.numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        this.masterTrackEqualsValue = cd.getChannel ().createEqualsValue (master);
        this.masterTrackEqualsValue.markInterested ();

        this.project = host.getProject ();
        this.arranger = new ArrangerProxy (host);
        this.mixer = new MixerProxy (host);

        this.browser = new BrowserProxy (host, this.cursorTrack, this.cursorDevice, this.numFilterColumnEntries, this.numResults);

        this.currentTrackBank = this.trackBank;
        this.scales = scales;
    }


    /**
     * Get the host.
     *
     * @return The host
     */
    public ControllerHost getHost ()
    {
        return this.host;
    }


    /**
     * Get the value changer.
     *
     * @return The value changer.
     */
    public ValueChanger getValueChanger ()
    {
        return this.valueChanger;
    }


    /**
     * Get the project.
     *
     * @return The project
     */
    public Project getProject ()
    {
        return this.project;
    }


    /**
     * Get the arranger.
     *
     * @return The arranger
     */
    public ArrangerProxy getArranger ()
    {
        return this.arranger;
    }


    /**
     * Get the mixer.
     *
     * @return The mixer
     */
    public MixerProxy getMixer ()
    {
        return this.mixer;
    }


    /**
     * Get the transport.
     *
     * @return The transport
     */
    public TransportProxy getTransport ()
    {
        return this.transport;
    }


    /**
     * Get the groove instance.
     *
     * @return The groove instance
     */
    public GrooveProxy getGroove ()
    {
        return this.groove;
    }


    /**
     * Get the master track.
     *
     * @return The master track
     */
    public MasterTrackProxy getMasterTrack ()
    {
        return this.masterTrack;
    }


    /**
     * Get the color manager.
     *
     * @return The color manager
     */
    public ColorManager getColorManager ()
    {
        return this.colorManager;
    }


    /**
     * Get the scales.
     *
     * @return The scales
     */
    public Scales getScales ()
    {
        return this.scales;
    }


    /**
     * True if there is a selected device.
     *
     * @return True if there is a selected device.
     */
    public boolean hasSelectedDevice ()
    {
        return this.cursorDevice.doesExist ();
    }


    /**
     * Get the cursor device.
     *
     * @return The cursor device
     */
    public CursorDeviceProxy getCursorDevice ()
    {
        return this.cursorDevice;
    }


    /**
     * Get the primary device. This is the first instrument in of the track.
     *
     * @return The device
     */
    public CursorDeviceProxy getPrimaryDevice ()
    {
        return this.primaryDevice;
    }


    /**
     * Toggles the audio/instrument track bank with the effect track bank.
     */
    public void toggleCurrentTrackBank ()
    {
        this.currentTrackBank = this.currentTrackBank == this.trackBank ? this.effectTrackBank : this.trackBank;
    }


    /**
     * Returns true if the effect track bank is active.
     *
     * @return True if the effect track bank is active
     */
    public boolean isEffectTrackBankActive ()
    {
        return this.currentTrackBank == this.effectTrackBank;
    }


    /**
     * Get the current track bank (audio/instrument track bank or the effect track bank).
     *
     * @return The current track bank
     */
    public AbstractTrackBankProxy getCurrentTrackBank ()
    {
        return this.currentTrackBank;
    }


    /**
     * Get the track bank.
     *
     * @return The track bank
     */
    public TrackBankProxy getTrackBank ()
    {
        return this.trackBank;
    }


    /**
     * Get the effect track bank.
     *
     * @return The effect track bank
     */
    public EffectTrackBankProxy getEffectTrackBank ()
    {
        return this.effectTrackBank;
    }


    /**
     * Get the application.
     *
     * @return The application
     */
    public ApplicationProxy getApplication ()
    {
        return this.application;
    }


    /**
     * Get the scene bank.
     *
     * @return The scene bank
     */
    public SceneBankProxy getSceneBank ()
    {
        return this.trackBank.getSceneBank ();
    }


    /**
     * Get the browser.
     *
     * @return The browser
     */
    public BrowserProxy getBrowser ()
    {
        return this.browser;
    }


    /***
     * Create a new cursor clip.
     *
     * @param cols The columns of the clip
     * @param rows The rows of the clip
     * @return The cursor clip
     */
    public CursorClipProxy createCursorClip (final int cols, final int rows)
    {
        return new CursorClipProxy (this.host, this.valueChanger, cols, rows);
    }


    /**
     * Creates a new clip at the given track and slot index.
     *
     * @param trackIndex The index of the track on which to create the clip
     * @param slotIndex The index of the slot (scene) in which to create the clip
     * @param newCLipLength The length of the new clip
     */
    public void createClip (final int trackIndex, final int slotIndex, final int newCLipLength)
    {
        final int quartersPerMeasure = this.getQuartersPerMeasure ();
        final int beats = (int) (newCLipLength < 2 ? Math.pow (2, newCLipLength) : Math.pow (2, newCLipLength - 2.0) * quartersPerMeasure);
        this.getCurrentTrackBank ().createClip (trackIndex, slotIndex, beats);
    }


    /**
     * Returns true if session recording is enabled, a clip is recording or overdub is enabled.
     *
     * @return True if recording
     */
    public boolean hasRecordingState ()
    {
        return this.transport.isRecording () || this.transport.isLauncherOverdub () || this.currentTrackBank.isClipRecording ();
    }


    /**
     * Get the quarters per measure.
     *
     * @return The quarters per measure.
     */
    public int getQuartersPerMeasure ()
    {
        return 4 * this.transport.getNumerator () / this.transport.getDenominator ();
    }


    /**
     * Returns true if the current track can hold notes. Convenience method.
     *
     * @return True if the current track can hold notes.
     */
    public boolean canSelectedTrackHoldNotes ()
    {
        final TrackData t = this.getCurrentTrackBank ().getSelectedTrack ();
        return t != null && t.canHoldNotes ();
    }


    /**
     * Returns true if the cursor track is pinned (aka does not follow the track selection in
     * Bitwig).
     *
     * @return True if the cursor track is pinned
     */
    public boolean isCursorTrackPinned ()
    {
        return this.cursorTrack.isPinned ().get ();
    }


    /**
     * Toggles if the cursor track is pinned.
     */
    public void toggleCursorTrackPinned ()
    {
        this.cursorTrack.isPinned ().toggle ();
    }


    /**
     * Returns true if the cursor device is pointing to a device on the master track.
     *
     * @return True if the cursor device is pointing to a device on the master track
     */
    public boolean isCursorDeviceOnMasterTrack ()
    {
        return this.masterTrackEqualsValue.get ();
    }
}