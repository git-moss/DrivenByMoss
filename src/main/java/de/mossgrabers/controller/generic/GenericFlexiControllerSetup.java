// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.ActionHandler;
import de.mossgrabers.controller.generic.flexihandler.BrowserHandler;
import de.mossgrabers.controller.generic.flexihandler.ClipHandler;
import de.mossgrabers.controller.generic.flexihandler.DeviceHandler;
import de.mossgrabers.controller.generic.flexihandler.EqHandler;
import de.mossgrabers.controller.generic.flexihandler.FxTrackHandler;
import de.mossgrabers.controller.generic.flexihandler.GlobalHandler;
import de.mossgrabers.controller.generic.flexihandler.InstrumentDeviceHandler;
import de.mossgrabers.controller.generic.flexihandler.LayerHandler;
import de.mossgrabers.controller.generic.flexihandler.LayoutHandler;
import de.mossgrabers.controller.generic.flexihandler.MarkerHandler;
import de.mossgrabers.controller.generic.flexihandler.MasterHandler;
import de.mossgrabers.controller.generic.flexihandler.MidiCCHandler;
import de.mossgrabers.controller.generic.flexihandler.ModesHandler;
import de.mossgrabers.controller.generic.flexihandler.NoteInputHandler;
import de.mossgrabers.controller.generic.flexihandler.ProjectRemotesHandler;
import de.mossgrabers.controller.generic.flexihandler.SceneHandler;
import de.mossgrabers.controller.generic.flexihandler.TrackHandler;
import de.mossgrabers.controller.generic.flexihandler.TrackRemotesHandler;
import de.mossgrabers.controller.generic.flexihandler.TransportHandler;
import de.mossgrabers.controller.generic.flexihandler.utils.ProgramBank;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.controller.valuechanger.OffsetBinaryRelativeValueChanger;
import de.mossgrabers.framework.controller.valuechanger.SignedBitRelativeValueChanger;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.AbstractMidiOutput;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.BrowserMode;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FileEx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;


/**
 * Support for generic controllers.
 *
 * @author Jürgen Moßgraber
 */
public class GenericFlexiControllerSetup extends AbstractControllerSetup<GenericFlexiControlSurface, GenericFlexiConfiguration> implements IValueObserver<FlexiCommand>
{
    private static final String     PROGRAM_NONE                     = "None";

    private final IValueChanger     absoluteLowResValueChanger       = new TwosComplementValueChanger (128, 1);
    private final IValueChanger     signedBitRelativeValueChanger    = new SignedBitRelativeValueChanger (16384, 100);
    private final IValueChanger     offsetBinaryRelativeValueChanger = new OffsetBinaryRelativeValueChanger (16384, 100);

    private final List<ProgramBank> banks                            = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public GenericFlexiControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ColorManager ();
        this.valueChanger = new TwosComplementValueChanger (16384, 100);
        this.configuration = new GenericFlexiConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        super.init ();

        // Load program name file and create selection list if present
        final Optional<FileEx> programsFileOpt = this.configuration.getProgramsFile ();
        if (programsFileOpt.isEmpty ())
            return;

        try
        {
            final FileEx programsFile = programsFileOpt.get ();

            final String nameWithoutType = programsFile.getNameWithoutType ();

            this.banks.addAll (ProgramBank.parse (programsFile.readUTF8 ()));
            final IEnumSetting [] bankSettings = new IEnumSetting [this.banks.size ()];

            for (int i = 0; i < this.banks.size (); i++)
            {
                final int bankPos = i;
                final ProgramBank pb = this.banks.get (bankPos);

                final String [] programs = pb.getPrograms ();
                final String [] opts = new String [programs.length + 1];
                System.arraycopy (programs, 0, opts, 1, programs.length);
                opts[0] = PROGRAM_NONE;
                bankSettings[bankPos] = this.documentSettings.getEnumSetting (pb.getName (), nameWithoutType + " Program Banks", opts, opts[0]);
                bankSettings[bankPos].addValueObserver (value -> {

                    final int program = pb.lookupProgram (value);
                    if (program < 0)
                        return;
                    final GenericFlexiControlSurface surface = this.getSurface ();
                    if (surface == null)
                        return;
                    final IMidiOutput midiOutput = surface.getMidiOutput ();
                    if (midiOutput != null)
                        midiOutput.sendProgramChange (pb.getMidiChannel (), pb.getMSB (), pb.getLSB (), program);

                    final int channel = pb.getMidiChannel ();
                    for (int b = 0; b < bankSettings.length; b++)
                    {
                        if (bankPos != b && this.banks.get (b).getMidiChannel () == channel)
                            bankSettings[b].set (PROGRAM_NONE);
                    }

                });
            }
        }
        catch (final IOException ex)
        {
            this.host.error ("Could not load programs file.", ex);
        }
        catch (final ParseException ex)
        {
            this.host.error ("Could not parse programs file.", ex);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 128, 128, 1);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableMainDrumDevice (false);
        ms.setNumMarkers (8);
        ms.enableDevice (DeviceID.EQ);
        ms.enableDevice (DeviceID.FIRST_INSTRUMENT);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();

        final String keyboardInputName = this.configuration.getKeyboardInputName ();
        final String portName = keyboardInputName.isBlank () ? "Generic Flexi" : keyboardInputName;

        final String inputName;
        if (this.configuration.isMPEEndabled ())
            inputName = portName + " (MPE)";
        else
            inputName = this.configuration.getKeyboardChannel () < 0 ? null : portName;

        final List<String> filters = this.getMidiFilters ();
        final IMidiInput input = midiAccess.createInput (inputName, filters.toArray (new String [filters.size ()]));

        final GenericFlexiControlSurface surface = new GenericFlexiControlSurface (this.host, this.configuration, this.colorManager, output, input);
        this.surfaces.add (surface);
        this.registerHandlers (surface);

        this.configuration.setCommandObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final GenericFlexiControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.TRACK, new TrackMode<> (surface, this.model, true));
        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true));
        modeManager.register (Modes.PAN, new TrackPanMode<> (surface, this.model, true));
        for (int i = 0; i < 8; i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new TrackSendMode<> (i, surface, this.model, true));
        modeManager.register (Modes.DEVICE_PARAMS, new ParameterMode<> ("Device", surface, this.model, true, null, surface::isShiftPressed));
        modeManager.register (Modes.BROWSER, new BrowserMode<> (surface, this.model));

        modeManager.setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final GenericFlexiControlSurface surface = this.getSurface ();
        this.configuration.addSettingObserver (GenericFlexiConfiguration.SLOT_CHANGE, surface::updateKeyTranslation);
        this.configuration.addSettingObserver (GenericFlexiConfiguration.SELECTED_MODE, this::selectMode);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, selected) -> this.handleTrackChange (selected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, selected) -> this.handleTrackChange (selected));

        surface.getModeManager ().addChangeListener ( (oldMode, newMode) -> this.updateIndication ());

        // Handle configuration changes
        this.createNoteRepeatObservers (this.configuration, surface);
        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.configuration.addSettingObserver (GenericFlexiConfiguration.ENABLED_MPE_ZONES, () -> surface.scheduleTask ( () -> {

            final INoteInput input = surface.getMidiInput ().getDefaultNoteInput ();
            final IMidiOutput output = surface.getMidiOutput ();

            final boolean mpeEnabled = this.configuration.isMPEEndabled ();
            input.enableMPE (mpeEnabled);
            // Enable MPE zone 1 with all 15 channels
            output.configureMPE (AbstractMidiOutput.ZONE_1, mpeEnabled ? 15 : 0);
            // Disable MPE zone
            output.configureMPE (AbstractMidiOutput.ZONE_2, 0);

        }, 2000));

        this.configuration.addSettingObserver (GenericFlexiConfiguration.MPE_PITCHBEND_RANGE, () -> surface.scheduleTask ( () -> {
            final INoteInput input = surface.getMidiInput ().getDefaultNoteInput ();
            final IMidiOutput output = surface.getMidiOutput ();
            final int mpePitchBendRange = this.configuration.getMPEPitchBendRange ();
            input.setMPEPitchBendSensitivity (mpePitchBendRange);
            output.sendMPEPitchbendRange (AbstractMidiOutput.ZONE_1, mpePitchBendRange);
        }, 2000));

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        this.configuration.clearNoteMap ();

        // Load last configuration
        final GenericFlexiControlSurface surface = this.getSurface ();
        this.host.scheduleTask ( () -> this.host.println (surface.loadFile (this.configuration.getFilename ())), 2000);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateRelativeKnobSensitivity ()
    {
        final int knobSensitivity = this.getSurface ().isKnobSensitivitySlow () ? this.configuration.getKnobSensitivitySlow () : this.configuration.getKnobSensitivityDefault ();
        this.absoluteLowResValueChanger.setSensitivity (knobSensitivity);
        this.signedBitRelativeValueChanger.setSensitivity (knobSensitivity);
        this.offsetBinaryRelativeValueChanger.setSensitivity (knobSensitivity);

        super.updateRelativeKnobSensitivity ();
    }


    /**
     * Registers the different generic flexi modules (handlers).
     *
     * @param surface The surface
     */
    private void registerHandlers (final GenericFlexiControlSurface surface)
    {
        surface.registerHandler (new GlobalHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new TransportHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new LayoutHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new TrackHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new FxTrackHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new MasterHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new DeviceHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new InstrumentDeviceHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new LayerHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new EqHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new BrowserHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new SceneHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new ClipHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new MarkerHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new ModesHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger, this.host));
        surface.registerHandler (new MidiCCHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new NoteInputHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new ProjectRemotesHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new TrackRemotesHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
        surface.registerHandler (new ActionHandler (this.model, surface, this.configuration, this.absoluteLowResValueChanger, this.signedBitRelativeValueChanger, this.offsetBinaryRelativeValueChanger));
    }


    @Override
    protected void handleTrackChange (final boolean isSelected)
    {
        if (isSelected)
            this.update (null);
    }


    /** {@inheritDoc} */
    @Override
    public void update (final FlexiCommand value)
    {
        this.updateIndication ();
    }


    private List<String> getMidiFilters ()
    {
        final boolean isMPEEndabled = this.configuration.isMPEEndabled ();
        final int keyboardChannel = this.configuration.getKeyboardChannel ();

        // Keyboard is off?
        if (keyboardChannel < 0 && !isMPEEndabled)
            return Collections.emptyList ();

        final String midiChannel = isMPEEndabled || keyboardChannel >= 16 ? "?" : Integer.toHexString (keyboardChannel).toUpperCase (Locale.US);

        final List<String> filters = new ArrayList<> ();

        Collections.addAll (filters, "8" + midiChannel + "????", "9" + midiChannel + "????", "A" + midiChannel + "????", "D" + midiChannel + "????");

        if (this.configuration.isKeyboardRouteModulation ())
            filters.add ("B" + midiChannel + "01??");
        if (this.configuration.isKeyboardRouteExpression ())
            filters.add ("B" + midiChannel + "0B??");
        if (this.configuration.isKeyboardRouteSustain ())
            filters.add ("B" + midiChannel + "40??");
        if (this.configuration.isKeyboardRouteTimbre ())
            filters.add ("B" + midiChannel + "4A??");
        if (this.configuration.isKeyboardRoutePitchbend () || isMPEEndabled)
            filters.add ("E" + midiChannel + "????");

        return filters;
    }


    private void selectMode ()
    {
        final String selectedModeName = this.configuration.getSelectedModeName ();
        if (selectedModeName == null)
            return;
        final GenericFlexiControlSurface surface = this.getSurface ();
        final Modes modeID = surface.getModeManager ().get (selectedModeName);
        if (modeID != null)
            surface.activateMode (modeID);
    }


    protected void updateIndication ()
    {
        final Set<FlexiCommand> commands = this.configuration.getMappedCommands ();
        final FlexiCommand [] allCommands = FlexiCommand.values ();

        final ITrackBank trackBank = this.model.getTrackBank ();
        final Optional<ITrack> selectedTrack = trackBank.getSelectedItem ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack.isPresent () && selectedTrack.get ().getIndex () == i;

            final ITrack track = trackBank.getItem (i);
            track.setVolumeIndication (this.testVolumeIndication (commands, allCommands, i, hasTrackSel));
            track.setPanIndication (this.testPanIndication (commands, allCommands, i, hasTrackSel));

            final ISendBank sendBank = track.getSendBank ();
            final int sendPageSize = sendBank.getPageSize ();
            for (int j = 0; j < sendPageSize; j++)
                sendBank.getItem (j).setIndication (this.testSendIndication (commands, allCommands, i, hasTrackSel, sendPageSize, j));
        }
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        masterTrack.setVolumeIndication (commands.contains (FlexiCommand.MASTER_SET_VOLUME));
        masterTrack.setPanIndication (commands.contains (FlexiCommand.MASTER_SET_PANORAMA));

        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
            parameterBank.getItem (i).setIndication (this.testParameterIndication (commands, allCommands, i));
    }


    private boolean testVolumeIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel)
    {
        if (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_VOLUME_TRACK) || commands.contains (allCommands[FlexiCommand.TRACK_1_SET_VOLUME.ordinal () + trackIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + trackIndex]) && this.getSurface ().getModeManager ().isActive (Modes.VOLUME);
    }


    private boolean testPanIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel)
    {
        if (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_PANORAMA) || commands.contains (allCommands[FlexiCommand.TRACK_1_SET_PANORAMA.ordinal () + trackIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + trackIndex]) && this.getSurface ().getModeManager ().isActive (Modes.PAN);
    }


    private boolean testSendIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel, final int sendPageSize, final int sendIndex)
    {
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (hasTrackSel && commands.contains (allCommands[FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal () + sendIndex]) || modeManager.isActive (Modes.TRACK) && sendIndex < 6)
            return true;
        if (commands.contains (allCommands[FlexiCommand.TRACK_1_SET_SEND_1.ordinal () + sendIndex * sendPageSize + trackIndex]))
            return true;
        return modeManager.isActive (Modes.get (Modes.SEND1, sendIndex));

    }


    private boolean testParameterIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int parameterIndex)
    {
        if (commands.contains (allCommands[FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal () + parameterIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + parameterIndex]) && this.getSurface ().getModeManager ().isActive (Modes.DEVICE_PARAMS);
    }
}
