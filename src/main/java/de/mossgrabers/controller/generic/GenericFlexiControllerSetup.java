// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.ActionHandler;
import de.mossgrabers.controller.generic.flexihandler.BrowserHandler;
import de.mossgrabers.controller.generic.flexihandler.ClipHandler;
import de.mossgrabers.controller.generic.flexihandler.DeviceHandler;
import de.mossgrabers.controller.generic.flexihandler.FxTrackHandler;
import de.mossgrabers.controller.generic.flexihandler.GlobalHandler;
import de.mossgrabers.controller.generic.flexihandler.LayoutHandler;
import de.mossgrabers.controller.generic.flexihandler.MarkerHandler;
import de.mossgrabers.controller.generic.flexihandler.MasterHandler;
import de.mossgrabers.controller.generic.flexihandler.MidiCCHandler;
import de.mossgrabers.controller.generic.flexihandler.ModesHandler;
import de.mossgrabers.controller.generic.flexihandler.NoteInputHandler;
import de.mossgrabers.controller.generic.flexihandler.SceneHandler;
import de.mossgrabers.controller.generic.flexihandler.TrackHandler;
import de.mossgrabers.controller.generic.flexihandler.TransportHandler;
import de.mossgrabers.controller.generic.flexihandler.UserHandler;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative2ValueChanger;
import de.mossgrabers.framework.controller.valuechanger.Relative3ValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.BrowserMode;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SendMode;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.FileEx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * Support for generic controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiControllerSetup extends AbstractControllerSetup<GenericFlexiControlSurface, GenericFlexiConfiguration> implements IValueObserver<FlexiCommand>
{
    private static final String PROGRAM_NONE          = "None";

    private final IValueChanger relative2ValueChanger = new Relative2ValueChanger (128, 1);
    private final IValueChanger relative3ValueChanger = new Relative3ValueChanger (128, 1);


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
        this.valueChanger = new DefaultValueChanger (128, 1);
        this.configuration = new GenericFlexiConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    public void init ()
    {
        super.init ();

        // Load program name file and create selection list if present
        final FileEx programsFile = this.configuration.getProgramsFile ();
        if (programsFile == null)
            return;

        try
        {
            final List<ProgramBank> banks = ProgramBank.parse (programsFile.readUTF8 ());
            final IEnumSetting [] bankSettings = new IEnumSetting [banks.size ()];

            for (int i = 0; i < banks.size (); i++)
            {
                final int bankPos = i;
                final ProgramBank pb = banks.get (bankPos);

                final String [] programs = pb.getPrograms ();
                final String [] opts = new String [programs.length + 1];
                System.arraycopy (programs, 0, opts, 1, programs.length);
                opts[0] = PROGRAM_NONE;
                bankSettings[bankPos] = this.documentSettings.getEnumSetting (pb.getName (), "Program Banks", opts, opts[0]);
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
                        if (bankPos != b && banks.get (b).getMidiChannel () == channel)
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
        ms.enableDrumDevice (false);
        ms.setNumMarkers (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();

        final int keyboardChannel = this.configuration.getKeyboardChannel ();
        final IMidiInput input;
        if (keyboardChannel < 0)
        {
            // Does not create a default note input if set to null
            input = midiAccess.createInput (null);
        }
        else
        {
            final String midiChannel;
            if (keyboardChannel >= 16)
                midiChannel = "?";
            else
                midiChannel = Integer.toHexString (keyboardChannel).toUpperCase (Locale.US);

            final List<String> filters = new ArrayList<> ();
            Collections.addAll (filters, "8" + midiChannel + "????", "9" + midiChannel + "????", "A" + midiChannel + "????", "D" + midiChannel + "????");
            if (this.configuration.isKeyboardRouteModulation ())
                filters.add ("B" + midiChannel + "01??");
            if (this.configuration.isKeyboardRouteSustain ())
                filters.add ("B" + midiChannel + "40??");
            if (this.configuration.isKeyboardRoutePitchbend ())
                filters.add ("E" + midiChannel + "????");

            input = midiAccess.createInput ("Generic Flexi", filters.toArray (new String [filters.size ()]));
        }

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
        modeManager.register (Modes.VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.register (Modes.PAN, new PanMode<> (surface, this.model, true));
        for (int i = 0; i < 8; i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new SendMode<> (i, surface, this.model, true));
        modeManager.register (Modes.DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true));
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

        this.createNoteRepeatObservers (this.configuration, surface);

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        this.configuration.clearNoteMap ();

        final GenericFlexiControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (Modes.TRACK);

        // Load last configuration
        this.host.scheduleTask ( () -> this.host.println (surface.loadFile (this.configuration.getFilename ())), 2000);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateRelativeKnobSensitivity ()
    {
        final int knobSensitivity = this.getSurface ().isKnobSensitivitySlow () ? this.configuration.getKnobSensitivitySlow () : this.configuration.getKnobSensitivityDefault ();
        this.relative2ValueChanger.setSensitivity (knobSensitivity);
        this.relative3ValueChanger.setSensitivity (knobSensitivity);

        super.updateRelativeKnobSensitivity ();
    }


    /**
     * Registers the different generic flexi modules (handlers).
     *
     * @param surface The surface
     */
    private void registerHandlers (final GenericFlexiControlSurface surface)
    {
        surface.registerHandler (new GlobalHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new TransportHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new LayoutHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new TrackHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new FxTrackHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new MasterHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new DeviceHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new BrowserHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new SceneHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new ClipHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new MarkerHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new ModesHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger, this.host));
        surface.registerHandler (new MidiCCHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new NoteInputHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new UserHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
        surface.registerHandler (new ActionHandler (this.model, surface, this.configuration, this.relative2ValueChanger, this.relative3ValueChanger));
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
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
        final ITrack selectedTrack = trackBank.getSelectedItem ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;

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
        if (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_VOLUME_TRACK))
            return true;
        if (commands.contains (allCommands[FlexiCommand.TRACK_1_SET_VOLUME.ordinal () + trackIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + trackIndex]) && this.getSurface ().getModeManager ().isActive (Modes.VOLUME);
    }


    private boolean testPanIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel)
    {
        if (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_PANORAMA))
            return true;
        if (commands.contains (allCommands[FlexiCommand.TRACK_1_SET_PANORAMA.ordinal () + trackIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + trackIndex]) && this.getSurface ().getModeManager ().isActive (Modes.PAN);
    }


    private boolean testSendIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel, final int sendPageSize, final int sendIndex)
    {
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (hasTrackSel)
        {
            if (commands.contains (allCommands[FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal () + sendIndex]))
                return true;
            if (modeManager.isActive (Modes.TRACK) && sendIndex < 6)
                return true;
        }
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
