// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IGroove;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.bitwig.data.MasterTrackImpl;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Model implements IModel
{
    private int            numTracks;
    private int            numScenes;
    private int            numSends;
    private int            numFilterColumnEntries;
    private int            numResults;
    private boolean        hasFlatTrackList;

    private IHost          hostProxy;
    private ControllerHost host;
    private ValueChanger   valueChanger;

    protected Scales       scales;
    private IApplication   application;
    private IArranger      arranger;
    private IMixer         mixer;
    private ITransport     transport;
    private IGroove        groove;
    private IProject       project;
    private IBrowser       browser;

    private CursorTrack    cursorTrack;
    private IChannelBank   currentTrackBank;
    private TrackBankProxy trackBank;
    private IChannelBank   effectTrackBank;
    private IMasterTrack   masterTrack;
    private BooleanValue   masterTrackEqualsValue;

    private ColorManager   colorManager;
    private ICursorDevice  primaryDevice;
    private ICursorDevice  cursorDevice;
    private ICursorDevice  drumDevice64;


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
        this.hostProxy = new HostProxy (host);
        this.colorManager = colorManager;
        this.valueChanger = valueChanger;

        this.numTracks = numTracks < 0 ? 8 : numTracks;
        this.numScenes = numScenes < 0 ? 8 : numScenes;
        this.numSends = numSends < 0 ? 6 : numSends;
        this.numFilterColumnEntries = numFilterColumnEntries < 0 ? 16 : numFilterColumnEntries;
        this.numResults = numResults < 0 ? 16 : numResults;
        this.hasFlatTrackList = hasFlatTrackList ? true : false;

        final Application app = host.createApplication ();
        this.application = new ApplicationProxy (app);
        this.transport = new TransportProxy (host, valueChanger);
        this.groove = new GrooveProxy (host, valueChanger.getUpperBound ());
        final MasterTrack master = host.createMasterTrack (0);
        this.masterTrack = new MasterTrackImpl (master, valueChanger);

        this.cursorTrack = host.createCursorTrack ("MyCursorTrackID", "The Cursor Track", 0, 0, true);
        this.cursorTrack.isPinned ().markInterested ();

        this.trackBank = new TrackBankProxy (host, valueChanger, this.cursorTrack, this.numTracks, this.numScenes, this.numSends, this.hasFlatTrackList);
        this.effectTrackBank = new EffectTrackBankProxy (host, valueChanger, this.cursorTrack, this.numTracks, this.numScenes, this.trackBank);

        this.primaryDevice = new CursorDeviceProxy (this.hostProxy, this.cursorTrack.createCursorDevice ("FIRST_INSTRUMENT", "First Instrument", this.numSends, CursorDeviceFollowMode.FIRST_INSTRUMENT), valueChanger, this.numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        PinnableCursorDevice cd = this.cursorTrack.createCursorDevice ("CURSOR_DEVICE", "Cursor device", this.numSends, CursorDeviceFollowMode.FOLLOW_SELECTION);
        this.cursorDevice = new CursorDeviceProxy (this.hostProxy, cd, valueChanger, this.numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        cd = this.cursorTrack.createCursorDevice ("64_DRUM_PADS", "64 Drum Pads", 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
        this.drumDevice64 = new CursorDeviceProxy (this.hostProxy, cd, valueChanger, 0, 0, 0, 64, 64);

        this.masterTrackEqualsValue = cd.channel ().createEqualsValue (master);
        this.masterTrackEqualsValue.markInterested ();

        this.project = new ProjectProxy (host.getProject (), app);
        this.arranger = new ArrangerProxy (host.createArranger ());
        this.mixer = new MixerProxy (host.createMixer ());

        this.browser = new BrowserProxy (host.createPopupBrowser (), this.cursorTrack, this.cursorDevice, this.numFilterColumnEntries, this.numResults);

        this.currentTrackBank = this.trackBank;
        this.scales = scales;
    }


    /** {@inheritDoc} */
    @Override
    public IHost getHost ()
    {
        return this.hostProxy;
    }


    /** {@inheritDoc} */
    @Override
    public ValueChanger getValueChanger ()
    {
        return this.valueChanger;
    }


    /** {@inheritDoc} */
    @Override
    public IProject getProject ()
    {
        return this.project;
    }


    /** {@inheritDoc} */
    @Override
    public IArranger getArranger ()
    {
        return this.arranger;
    }


    /** {@inheritDoc} */
    @Override
    public IMixer getMixer ()
    {
        return this.mixer;
    }


    /** {@inheritDoc} */
    @Override
    public ITransport getTransport ()
    {
        return this.transport;
    }


    /** {@inheritDoc} */
    @Override
    public IGroove getGroove ()
    {
        return this.groove;
    }


    /** {@inheritDoc} */
    @Override
    public IMasterTrack getMasterTrack ()
    {
        return this.masterTrack;
    }


    /** {@inheritDoc} */
    @Override
    public ColorManager getColorManager ()
    {
        return this.colorManager;
    }


    /** {@inheritDoc} */
    @Override
    public Scales getScales ()
    {
        return this.scales;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSelectedDevice ()
    {
        return this.cursorDevice.doesExist ();
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getCursorDevice ()
    {
        return this.cursorDevice;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getPrimaryDevice ()
    {
        return this.primaryDevice;
    }


    /** {@inheritDoc} */
    @Override
    public ICursorDevice getDrumDevice64 ()
    {
        return this.drumDevice64;
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCurrentTrackBank ()
    {
        this.currentTrackBank = this.currentTrackBank == this.trackBank ? this.effectTrackBank : this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isEffectTrackBankActive ()
    {
        return this.currentTrackBank == this.effectTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public IChannelBank getCurrentTrackBank ()
    {
        return this.currentTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public TrackBankProxy getTrackBank ()
    {
        return this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public IChannelBank getEffectTrackBank ()
    {
        return this.effectTrackBank;
    }


    /** {@inheritDoc} */
    @Override
    public IApplication getApplication ()
    {
        return this.application;
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank ()
    {
        return this.trackBank.getSceneBank ();
    }


    /** {@inheritDoc} */
    @Override
    public IBrowser getBrowser ()
    {
        return this.browser;
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank createSceneViewTrackBank (final int numTracks, final int numScenes)
    {
        return new TrackBankProxy (this.host, this.valueChanger, this.cursorTrack, numTracks, numScenes, 0, true);
    }


    /** {@inheritDoc} */
    @Override
    public ICursorClip createCursorClip (final int cols, final int rows)
    {
        return new CursorClipProxy (this.host, this.valueChanger, cols, rows);
    }


    /** {@inheritDoc} */
    @Override
    public void createClip (final int trackIndex, final int slotIndex, final int newCLipLength)
    {
        final int quartersPerMeasure = this.getQuartersPerMeasure ();
        final int beats = (int) (newCLipLength < 2 ? Math.pow (2, newCLipLength) : Math.pow (2, newCLipLength - 2.0) * quartersPerMeasure);
        this.getCurrentTrackBank ().createClip (trackIndex, slotIndex, beats);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRecordingState ()
    {
        return this.transport.isRecording () || this.transport.isLauncherOverdub () || this.currentTrackBank.isClipRecording ();
    }


    /** {@inheritDoc} */
    @Override
    public int getQuartersPerMeasure ()
    {
        return 4 * this.transport.getNumerator () / this.transport.getDenominator ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean canSelectedTrackHoldNotes ()
    {
        final ITrack t = this.getCurrentTrackBank ().getSelectedTrack ();
        return t != null && t.canHoldNotes ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorTrackPinned ()
    {
        return this.cursorTrack.isPinned ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleCursorTrackPinned ()
    {
        this.cursorTrack.isPinned ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorDeviceOnMasterTrack ()
    {
        return this.masterTrackEqualsValue.get ();
    }
}