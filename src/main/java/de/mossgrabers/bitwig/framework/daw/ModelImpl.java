// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.ICursorClip;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.scale.Scales;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.TrackBank;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelImpl extends AbstractModel
{
    private ControllerHost controllerHost;
    private CursorTrack    cursorTrack;
    private BooleanValue   masterTrackEqualsValue;


    /**
     * Constructor.
     *
     * @param controllerHost The host
     * @param colorManager The color manager
     * @param valueChanger The value changer
     * @param scales The scales object
     * @param modelSetup The configuration parameters for the model
     */
    public ModelImpl (final ControllerHost controllerHost, final ColorManager colorManager, final IValueChanger valueChanger, final Scales scales, final ModelSetup modelSetup)
    {
        super (colorManager, valueChanger, scales, modelSetup);

        this.controllerHost = controllerHost;
        this.host = new HostImpl (controllerHost);

        final Application app = controllerHost.createApplication ();
        this.application = new ApplicationImpl (app);
        this.project = new ProjectImpl (controllerHost.getProject (), app);

        final Arranger bwArranger = controllerHost.createArranger ();
        this.arranger = new ArrangerImpl (bwArranger);
        final int numMarkers = modelSetup.getNumMarkers ();
        if (numMarkers > 0)
            this.markerBank = new MarkerBankImpl (this.host, valueChanger, bwArranger.createCueMarkerBank (numMarkers), numMarkers);

        this.mixer = new MixerImpl (controllerHost.createMixer ());
        this.transport = new TransportImpl (controllerHost, valueChanger);
        this.groove = new GrooveImpl (controllerHost, valueChanger);
        final MasterTrack master = controllerHost.createMasterTrack (0);
        this.masterTrack = new MasterTrackImpl (this.host, valueChanger, master);

        this.cursorTrack = controllerHost.createCursorTrack ("MyCursorTrackID", "The Cursor Track", 0, 0, true);
        this.cursorTrack.isPinned ().markInterested ();

        final TrackBank tb;
        final int numTracks = this.modelSetup.getNumTracks ();
        final int numSends = this.modelSetup.getNumSends ();
        final int numScenes = this.modelSetup.getNumScenes ();
        if (this.modelSetup.hasFlatTrackList ())
        {
            tb = controllerHost.createMainTrackBank (numTracks, numSends, numScenes);
            tb.followCursorTrack (this.cursorTrack);
        }
        else
            tb = this.cursorTrack.createSiblingsTrackBank (numTracks, numSends, numScenes, false, false);

        this.trackBank = new TrackBankImpl (this.host, valueChanger, tb, this.cursorTrack, numTracks, numScenes, numSends);
        final TrackBank effectTrackBank = controllerHost.createEffectTrackBank (numTracks, numScenes);
        this.effectTrackBank = new EffectTrackBankImpl (this.host, valueChanger, effectTrackBank, this.cursorTrack, numTracks, numScenes, this.trackBank);

        final int numParams = this.modelSetup.getNumParams ();
        final int numDeviceLayers = this.modelSetup.getNumDeviceLayers ();
        final int numDrumPadLayers = this.modelSetup.getNumDrumPadLayers ();
        final int numDevicesInBank = this.modelSetup.getNumDevicesInBank ();
        this.primaryDevice = new CursorDeviceImpl (this.host, valueChanger, this.cursorTrack.createCursorDevice ("FIRST_INSTRUMENT", "First Instrument", numSends, CursorDeviceFollowMode.FIRST_INSTRUMENT), numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        PinnableCursorDevice cd = this.cursorTrack.createCursorDevice ("CURSOR_DEVICE", "Cursor device", numSends, CursorDeviceFollowMode.FOLLOW_SELECTION);
        this.cursorDevice = new CursorDeviceImpl (this.host, valueChanger, cd, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        if (numDrumPadLayers > 0)
        {
            cd = this.cursorTrack.createCursorDevice ("64_DRUM_PADS", "64 Drum Pads", 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
            this.drumDevice64 = new CursorDeviceImpl (this.host, valueChanger, cd, 0, 0, -1, 64, 64);
        }
        final int numResults = this.modelSetup.getNumResults ();
        if (numResults > 0)
            this.browser = new BrowserImpl (controllerHost.createPopupBrowser (), this.cursorTrack, this.cursorDevice, this.modelSetup.getNumFilterColumnEntries (), numResults);

        this.masterTrackEqualsValue = cd.channel ().createEqualsValue (master);
        this.masterTrackEqualsValue.markInterested ();

        this.currentTrackBank = this.trackBank;

        // Make sure there is at least 1 cursor clip for quantization, even if there are no
        // sequencers
        this.getCursorClip ();
    }


    /** {@inheritDoc} */
    @Override
    public ITrackBank createSceneViewTrackBank (final int numTracks, final int numScenes)
    {
        final TrackBank tb = this.controllerHost.createMainTrackBank (numTracks, this.modelSetup.getNumSends (), numScenes);
        tb.followCursorTrack (this.cursorTrack);
        return new TrackBankImpl (this.host, this.valueChanger, tb, this.cursorTrack, numTracks, numScenes, 0);
    }


    /** {@inheritDoc} */
    @Override
    public ICursorClip getCursorClip (final int cols, final int rows)
    {
        return this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.controllerHost, this.valueChanger, cols, rows));
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