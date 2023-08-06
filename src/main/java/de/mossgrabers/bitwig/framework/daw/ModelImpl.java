// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.CursorDeviceImpl;
import de.mossgrabers.bitwig.framework.daw.data.CursorTrackImpl;
import de.mossgrabers.bitwig.framework.daw.data.DrumDeviceImpl;
import de.mossgrabers.bitwig.framework.daw.data.EqualizerDeviceImpl;
import de.mossgrabers.bitwig.framework.daw.data.KompleteDevice;
import de.mossgrabers.bitwig.framework.daw.data.MasterTrackImpl;
import de.mossgrabers.bitwig.framework.daw.data.SpecificDeviceImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.EffectTrackBankImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.MarkerBankImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.SlotBankImpl;
import de.mossgrabers.bitwig.framework.daw.data.bank.TrackBankImpl;
import de.mossgrabers.framework.daw.AbstractModel;
import de.mossgrabers.framework.daw.DataSetup;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FrameworkException;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Arranger;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorDeviceFollowMode;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceMatcher;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.PinnableCursorDevice;
import com.bitwig.extension.controller.api.Project;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * The model which contains all data and access to the DAW.
 *
 * @author Jürgen Moßgraber
 */
public class ModelImpl extends AbstractModel
{
    /** The UUID of the Drum Device. */
    public static final UUID               INSTRUMENT_DRUM_MACHINE = UUID.fromString ("8ea97e45-0255-40fd-bc7e-94419741e9d1");

    private final ControllerHost           controllerHost;
    private final CursorTrack              bwCursorTrack;
    private final Track                    rootTrackGroup;
    private final BooleanValue             masterTrackEqualsValue;
    private final Map<Integer, ISceneBank> sceneBanks              = new HashMap<> (1);
    private final Map<Integer, ISlotBank>  slotBanks               = new HashMap<> (1);
    private final SceneBank                sceneBank;


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

        final int numParamPages = this.modelSetup.getNumParamPages ();
        final int numParams = this.modelSetup.getNumParams ();

        final Application bwApplication = controllerHost.createApplication ();
        final Arranger bwArranger = controllerHost.createArranger ();

        this.application = new ApplicationImpl (bwApplication, bwArranger, this.valueChanger);
        final Project proj = controllerHost.getProject ();
        this.rootTrackGroup = proj.getRootTrackGroup ();
        this.project = new ProjectImpl (this.host, this.valueChanger, proj, bwApplication, numParamPages, numParams);

        this.transport = new TransportImpl (controllerHost, this.application, this.valueChanger);
        this.arranger = new ArrangerImpl (bwArranger);
        final int numMarkers = modelSetup.getNumMarkers ();
        if (numMarkers > 0)
            this.markerBank = new MarkerBankImpl (this.host, this.valueChanger, bwArranger, numMarkers, this.transport);

        this.mixer = new MixerImpl (controllerHost.createMixer ());
        this.groove = new GrooveImpl (controllerHost, this.valueChanger);

        final int numSends = this.modelSetup.getNumSends ();
        final int numScenes = this.modelSetup.getNumScenes ();

        //////////////////////////////////////////////////////////////////////////////
        // Create track banks

        final TrackBank tb;
        this.bwCursorTrack = controllerHost.createCursorTrack ("MyCursorTrackID", "The Cursor Track", numSends, numScenes, true);
        final int numTracks = this.modelSetup.getNumTracks ();
        if (this.modelSetup.hasFlatTrackList ())
        {
            if (this.modelSetup.hasFullFlatTrackList ())
                tb = controllerHost.createTrackBank (numTracks, numSends, numScenes, true);
            else
                tb = controllerHost.createMainTrackBank (numTracks, numSends, numScenes);
            tb.followCursorTrack (this.bwCursorTrack);
        }
        else
            tb = this.bwCursorTrack.createSiblingsTrackBank (numTracks, numSends, numScenes, false, false);

        this.sceneBank = tb.sceneBank ();

        this.cursorTrack = new CursorTrackImpl (this, this.host, this.valueChanger, this.bwCursorTrack, this.rootTrackGroup, this.sceneBank, (ApplicationImpl) this.application, numSends, numScenes, numParamPages, numParams);

        final MasterTrack master = controllerHost.createMasterTrack (0);
        this.masterTrack = new MasterTrackImpl (this.host, this.valueChanger, master, this.bwCursorTrack, this.rootTrackGroup, (ApplicationImpl) this.application, this.sceneBank);

        this.trackBank = new TrackBankImpl (this.host, (ApplicationImpl) this.application, this.valueChanger, tb, (CursorTrackImpl) this.cursorTrack, this.rootTrackGroup, numTracks, numScenes, numSends);

        final int numFxTracks = this.modelSetup.getNumFxTracks ();
        final TrackBank effectTrackBank = controllerHost.createEffectTrackBank (numFxTracks, numSends, numScenes);
        this.effectTrackBank = new EffectTrackBankImpl (this.host, this.valueChanger, effectTrackBank, (CursorTrackImpl) this.cursorTrack, this.rootTrackGroup, (ApplicationImpl) this.application, numFxTracks, numScenes, numSends, numParamPages, numParams, this.trackBank);

        //////////////////////////////////////////////////////////////////////////////
        // Create devices

        final int numDevicesInBank = this.modelSetup.getNumDevicesInBank ();
        final int numDeviceLayers = this.modelSetup.getNumDeviceLayers ();
        final int numDrumPadLayers = this.modelSetup.getNumDrumPadLayers ();

        // Cursor device
        final PinnableCursorDevice mainCursorDevice = this.bwCursorTrack.createCursorDevice ("CURSOR_DEVICE", "Cursor device", numSends, CursorDeviceFollowMode.FOLLOW_SELECTION);
        this.cursorDevice = new CursorDeviceImpl (this.host, this.valueChanger, mainCursorDevice, numSends, numParamPages, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

        // Drum Machine
        if (modelSetup.wantsMainDrumDevice ())
        {
            final DeviceMatcher drumMachineDeviceMatcher = controllerHost.createBitwigDeviceMatcher (INSTRUMENT_DRUM_MACHINE);
            final DeviceBank drumDeviceBank = this.bwCursorTrack.createDeviceBank (1);
            drumDeviceBank.setDeviceMatcher (drumMachineDeviceMatcher);
            final Device drumMachineDevice = drumDeviceBank.getItemAt (0);
            this.drumDevice = new DrumDeviceImpl (this.host, this.valueChanger, drumMachineDevice, numSends, numParamPages, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);

            // Additional drum machines with different drum pad page sizes
            final int [] additionalDrumDevicePageSizes = modelSetup.wantsAdditionalDrumDevices ();
            for (final int pageSize: additionalDrumDevicePageSizes)
            {
                final DrumDeviceImpl addDrumDevice = new DrumDeviceImpl (this.host, this.valueChanger, drumMachineDevice, numSends, 0, 0, -1, pageSize, pageSize);
                this.additionalDrumDevices.put (Integer.valueOf (pageSize), addDrumDevice);
            }
        }

        for (final DeviceID deviceID: modelSetup.getDeviceIDs ())
        {
            final ISpecificDevice specificDevice;
            switch (deviceID)
            {
                case FIRST_INSTRUMENT:
                    final Device instrumentDevice = this.createDevice (controllerHost.createInstrumentMatcher ());
                    specificDevice = new SpecificDeviceImpl (this.host, this.valueChanger, instrumentDevice, numSends, numParamPages, numParams, numDevicesInBank, numDeviceLayers, numDrumPadLayers);
                    break;

                case EQ:
                    final Device eqDevice = this.createDevice (controllerHost.createBitwigDeviceMatcher (UUID.fromString ("e4815188-ba6f-4d14-bcfc-2dcb8f778ccb")));
                    specificDevice = new EqualizerDeviceImpl (this.host, this.valueChanger, eqDevice, numParamPages, numParams);
                    break;

                case NI_KOMPLETE:
                    final DeviceMatcher vst2DeviceMatcher = controllerHost.createVST2DeviceMatcher (KompleteDevice.VST2_KOMPLETE_ID);
                    final DeviceMatcher vst3DeviceMatcher = controllerHost.createVST3DeviceMatcher (KompleteDevice.VST3_KOMPLETE_ID);
                    final Device kompleteDevice = this.createDevice (controllerHost.createOrDeviceMatcher (vst2DeviceMatcher, vst3DeviceMatcher));
                    specificDevice = new KompleteDevice (this.host, this.valueChanger, kompleteDevice);
                    break;

                default:
                    // Impossible to reach
                    throw new FrameworkException ("Unknown device ID.");
            }

            this.specificDevices.put (deviceID, specificDevice);
        }

        final int numResults = this.modelSetup.getNumResults ();
        if (numResults > 0)
            this.browser = new BrowserImpl (this.host, controllerHost.createPopupBrowser (), this.bwCursorTrack, mainCursorDevice, this.modelSetup.getNumFilterColumnEntries (), numResults);

        this.masterTrackEqualsValue = mainCursorDevice.channel ().createEqualsValue (master);
        this.masterTrackEqualsValue.markInterested ();

        this.currentTrackBank = this.trackBank;

        controllerHost.scheduleTask (this::flushWorkaround, 4000);
    }


    /**
     * Create a device bank with the given matcher which contains exactly one device.
     *
     * @param deviceMatcher The device matcher to apply
     * @return The device
     */
    private Device createDevice (final DeviceMatcher deviceMatcher)
    {
        final DeviceBank deviceBank = this.bwCursorTrack.createDeviceBank (1);
        deviceBank.setDeviceMatcher (deviceMatcher);
        return deviceBank.getItemAt (0);
    }


    /**
     * Workaround for flush only happening if state changes since Bitwig 3.1 (which is intended and
     * not a bug).
     */
    private void flushWorkaround ()
    {
        // There are enough flushes happening if playback is active
        if (!this.getTransport ().isPlaying ())
            this.controllerHost.requestFlush ();
        this.controllerHost.scheduleTask (this::flushWorkaround, 100);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isCursorDeviceOnMasterTrack ()
    {
        return this.masterTrackEqualsValue.get ();
    }


    /** {@inheritDoc} */
    @Override
    public ISceneBank getSceneBank (final int numScenes)
    {
        return this.sceneBanks.computeIfAbsent (Integer.valueOf (numScenes), key -> {
            final TrackBank tb = this.controllerHost.createMainTrackBank (1, this.modelSetup.getNumSends (), numScenes);
            tb.followCursorTrack (this.bwCursorTrack);
            return new TrackBankImpl (this.host, (ApplicationImpl) this.application, this.valueChanger, tb, (CursorTrackImpl) this.cursorTrack, this.rootTrackGroup, 1, numScenes, 0).getSceneBank ();
        });
    }


    /** {@inheritDoc} */
    @Override
    public ISlotBank getSlotBank (final int numSlots)
    {
        return this.slotBanks.computeIfAbsent (Integer.valueOf (numSlots), key -> {

            final CursorTrack ct = this.controllerHost.createCursorTrack ("CursorTrackID" + numSlots, "Cursor Track for " + numSlots + "Slots", 0, numSlots, true);
            final ICursorTrack cursorTrack = new CursorTrackImpl (this, this.host, this.valueChanger, ct, this.rootTrackGroup, this.sceneBank, (ApplicationImpl) this.application, 0, numSlots, 0, 0);
            return new SlotBankImpl (this.host, this.valueChanger, cursorTrack, this.sceneBank, ct.clipLauncherSlotBank (), numSlots);

        });
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getNoteClip (final int cols, final int rows)
    {
        return this.cursorClips.computeIfAbsent (cols + "-" + rows, k -> new CursorClipImpl (this.host, this.bwCursorTrack, this.valueChanger, cols, rows));
    }


    /** {@inheritDoc} */
    @Override
    public void createNoteClip (final ITrack track, final ISlot slot, final int lengthInBeats, final boolean overdub)
    {
        track.createClip (slot.getIndex (), lengthInBeats);
        slot.select ();
        slot.launch (true, false);
        if (overdub)
            this.transport.setLauncherOverdub (true);
    }


    /** {@inheritDoc} */
    @Override
    public void recordNoteClip (final ITrack track, final ISlot slot)
    {
        if (!slot.isRecording ())
            slot.startRecording ();
        slot.launch (true, false);
    }


    /** {@inheritDoc} */
    @Override
    public INoteClip getCursorClip ()
    {
        if (this.cursorClips.isEmpty ())
            throw new FrameworkException ("No cursor clip created!");
        return this.cursorClips.values ().iterator ().next ();
    }


    /** {@inheritDoc} */
    @Override
    public void ensureClip ()
    {
        this.getNoteClip (0, 0);
    }
}