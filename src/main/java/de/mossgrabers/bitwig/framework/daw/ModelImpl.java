// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.DataSetup;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FrameworkException;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.util.HashMap;
import java.util.Map;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModelImpl extends AbstractModel
{
    private static final int               ALL_TRACKS = 1000;

    private final ControllerHost           controllerHost;
    private final CursorTrack              cursorTrack;
    private final BooleanValue             masterTrackEqualsValue;
    private final Map<Integer, ISceneBank> sceneBanks = new HashMap<> (1);
    private final TrackBank                muteSoloTrackBank;

    private Track                          rootTrackGroup;


    /**
     * Constructor.
     *
     * @param modelSetup The configuration parameters for the model
     * @param dataSetup Some setup variables
     * @param controllerHost The controller host
     * @param scales The scales object
     */
    public ModelImpl (final ModelSetup modelSetup, final DataSetup dataSetup, final ControllerHost controllerHost, final Scales scales)
    {
        super (modelSetup, dataSetup, scales);

        this.controllerHost = controllerHost;

        final Application app = controllerHost.createApplication ();
        this.application = new ApplicationImpl (app);
        final Project proj = controllerHost.getProject ();
        this.project = new ProjectImpl (proj, app);

        final Arranger bwArranger = controllerHost.createArranger ();
        this.arranger = new ArrangerImpl (bwArranger);
        final int numMarkers = modelSetup.getNumMarkers ();
        if (numMarkers > 0)
            this.markerBank = new MarkerBankImpl (this.host, this.valueChanger, bwArranger.createCueMarkerBank (numMarkers), numMarkers);

        this.mixer = new MixerImpl (controllerHost.createMixer ());
        this.transport = new TransportImpl (controllerHost, this.valueChanger);
        this.groove = new GrooveImpl (controllerHost, this.valueChanger);
        final MasterTrack master = controllerHost.createMasterTrack (0);
        this.masterTrack = new MasterTrackImpl (this.host, this.valueChanger, master);

        this.cursorTrack = controllerHost.createCursorTrack ("MyCursorTrackID", "The Cursor Track", 0, 0, true);
        this.cursorTrack.isPinned ().markInterested ();

        final TrackBank tb;
        final int numTracks = this.modelSetup.getNumTracks ();
        final int numSends = this.modelSetup.getNumSends ();
        final int numScenes = this.modelSetup.getNumScenes ();
        if (this.modelSetup.hasFlatTrackList ())
        {
            if (this.modelSetup.hasFullFlatTrackList ())
                tb = controllerHost.createTrackBank (numTracks, numSends, numScenes, true);
            else
                tb = controllerHost.createMainTrackBank (numTracks, numSends, numScenes);
            tb.followCursorTrack (this.cursorTrack);
        }
        else
            tb = this.cursorTrack.createSiblingsTrackBank (numTracks, numSends, numScenes, false, false);

        this.rootTrackGroup = proj.getRootTrackGroup ();
        this.trackBank = new TrackBankImpl (this.host, this.valueChanger, tb, this.cursorTrack, this.rootTrackGroup, numTracks, numScenes, numSends);
        final TrackBank effectTrackBank = controllerHost.createEffectTrackBank (numTracks, numScenes);
        this.effectTrackBank = new EffectTrackBankImpl (this.host, this.valueChanger, this.cursorTrack, effectTrackBank, numTracks, numScenes, this.trackBank);

        this.muteSoloTrackBank = controllerHost.createTrackBank (ALL_TRACKS, 0, 0, true);
        for (int i = 0; i < ALL_TRACKS; i++)
            this.muteSoloTrackBank.getItemAt (i).solo ().markInterested ();

        final int numParams = this.modelSetup.getNumParams ();
        final int numDeviceLayers = this.modelSetup.getNumDeviceLayers ();
        final int numDrumPadLayers = this.modelSetup.getNumDrumPadLayers ();
        final int numDevicesInBank = this.modelSetup.getNumDevicesInBank ();
        this.instrumentDevice = new CursorDeviceImpl (this.host, this.valueChanger, this.cursorTrack.createCursorDevice ("FIRST_INSTRUMENT", "First Instrument", numSends, CursorDeviceFollowMode.FIRST_INSTRUMENT), numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        PinnableCursorDevice cd = this.cursorTrack.createCursorDevice ("CURSOR_DEVICE", "Cursor device", numSends, CursorDeviceFollowMode.FOLLOW_SELECTION);
        this.cursorDevice = new CursorDeviceImpl (this.host, this.valueChanger, cd, numSends, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
        if (numDrumPadLayers > 0)
        {
            cd = this.cursorTrack.createCursorDevice ("64_DRUM_PADS", "64 Drum Pads", 0, CursorDeviceFollowMode.FIRST_INSTRUMENT);
            this.drumDevice64 = new CursorDeviceImpl (this.host, this.valueChanger, cd, 0, 0, -1, 64, 64);
        }
        final int numResults = this.modelSetup.getNumResults ();
        if (numResults > 0)
            this.browser = new BrowserImpl (controllerHost.createPopupBrowser (), this.cursorTrack, this.cursorDevice, this.modelSetup.getNumFilterColumnEntries (), numResults);

        this.masterTrackEqualsValue = cd.channel ().createEqualsValue (master);
        this.masterTrackEqualsValue.markInterested ();

        this.currentTrackBank = this.trackBank;
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank createSceneBank (final int numScenes)
    {
        return this.sceneBanks.computeIfAbsent (Integer.valueOf (numScenes), key -> {
            final TrackBank tb = this.controllerHost.createMainTrackBank (1, this.modelSetup.getNumSends (), numScenes);
            tb.followCursorTrack (this.cursorTrack);
            return new TrackBankImpl (this.host, this.valueChanger, tb, this.cursorTrack, this.rootTrackGroup, 1, numScenes, 0).getSceneBank ();
        });
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasSolo ()
    {
        for (int i = 0; i < ALL_TRACKS; i++)
        {
            if (this.muteSoloTrackBank.getItemAt (i).solo ().get ())
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void clearSolo ()
    {
        for (int i = 0; i < ALL_TRACKS; i++)
            this.muteSoloTrackBank.getItemAt (i).solo ().set (false);
    }


    /** {@inheritDoc} */
    @Override
    public void clearMute ()
    {
        for (int i = 0; i < ALL_TRACKS; i++)
            this.muteSoloTrackBank.getItemAt (i).mute ().set (false);
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getNoteClip (final int cols, final int rows)
    {
        return (INoteClip) this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.controllerHost, this.valueChanger, cols, rows));
    }


    /** {@inheritDoc} */
    @Override
    public IClip getClip ()
    {
        if (this.cursorClips.isEmpty ())
            throw new FrameworkException ("No cursor clip created!");
        return this.cursorClips.values ().iterator ().next ();
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


    /** {@inheritDoc} */
    @Override
    public void ensureClip ()
    {
        this.getNoteClip (0, 0);
    }
}