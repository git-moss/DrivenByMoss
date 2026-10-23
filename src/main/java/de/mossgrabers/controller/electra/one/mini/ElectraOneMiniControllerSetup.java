// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini;

import java.util.ArrayList;
import java.util.List;

import de.mossgrabers.controller.electra.one.base.AbstractElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.base.command.trigger.TouchCombinationCommand;
import de.mossgrabers.controller.electra.one.base.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.controller.electra.one.mini.mode.ElectraOneMiniMixerMode;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.DummyMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.DummyView;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Electra One Mini controller.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniControllerSetup extends AbstractControllerSetup<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public ElectraOneMiniControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ElectraOneColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new ElectraOneMiniConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableMainDrumDevice (false);
        ms.enableDevice (DeviceID.EQ);
        ms.setWantsFocusedParameter (true);

        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);

        ms.setNumTracks (8);
        ms.setNumScenes (8);
        ms.setNumSends (6);

        ms.setNumMarkers (8);

        // Not used (yet)
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ((index, isSelected) -> this.handleTrackChange (isSelected));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();

        // Communication with the controls
        final IMidiOutput output = midiAccess.createOutput (0);
        final IMidiInput input = midiAccess.createInput (0, null);

        // Raw MIDI input on MIDI channels 1-15 (controls use only channel 16)
        for (int i = 0; i < 15; i++)
            input.createNoteInput ("Channel " + (i + 1), String.format ("?%X????", Integer.valueOf (i)));

        // Sysex control channel
        final IMidiOutput ctrlOutput = midiAccess.createOutput (1);
        final IMidiInput ctrlInput = midiAccess.createInput (1, null);

        final ElectraOneMiniControlSurface surface = new ElectraOneMiniControlSurface (this.host, this.colorManager, this.configuration, output, input, ctrlInput, ctrlOutput);
        this.surfaces.add (surface);

        final int touchPatternSize = surface.getTouchPatternSize ();
        final List<TouchCombinationCommand<?, ?>> touchCombinationCommands = new ArrayList<> (touchPatternSize);
        for (int i = 0; i < touchPatternSize; i++)
            touchCombinationCommands.add (new TouchCombinationCommand<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration> (i, this.model, surface));
        surface.setTouchCombinationCommands (touchCombinationCommands);

        surface.getModeManager ().setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ElectraOneMiniControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.VOLUME, new ElectraOneMiniMixerMode (surface, this.model));
        // TODO
        // modeManager.register (Modes.DEVICE_PARAMS, new DeviceMode (surface, this.model));
        // modeManager.register (Modes.EQ_DEVICE_PARAMS, new EqualizerMode (surface, this.model));
        // modeManager.register (Modes.TRANSPORT, new TransportMode (surface, this.model));
        // modeManager.register (Modes.SESSION, new SessionMode (surface, this.model));
        // modeManager.register (Modes.PROJECT, new ProjectTrackMode (surface, this.model));
        modeManager.register (Modes.DUMMY, new DummyMode<> (surface, this.model, ElectraOneMiniControlSurface.KNOB_IDS));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ElectraOneMiniControlSurface surface = this.getSurface ();
        // We need this for triggering updateDisplay in the modes
        surface.getViewManager ().register (Views.CONTROL, new DummyView<> ("Control", surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final ElectraOneMiniControlSurface surface = this.getSurface ();
        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.configuration.addSettingObserver (AbstractElectraOneConfiguration.KNOB_TOUCH_COMBINATION_COMMANDS, this.getSurface ()::updateTouchCombinationCommandsCache);
        this.configuration.addSettingObserver (AbstractElectraOneConfiguration.LOG_TO_CONSOLE, surface::setLoggingEnabled);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ElectraOneMiniControlSurface surface = this.getSurface ();

        // These are the push buttons of the knobs
        for (int index = 0; index < 8; index++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.ROW1_1, index);
            final int colLabel = index + 1;
            final int cc = ElectraOneMiniControlSurface.ELECTRA_CTRL_PUSH_1 + index;
            final ButtonRowModeCommand<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration> command = new ButtonRowModeCommand<> (0, index, this.model, surface);
            this.addButton (surface, buttonID, Integer.toString (colLabel), command, 15, cc, () -> this.getButtonColor (surface, buttonID));
        }

        // These are the 4 buttons of the Mini
        for (int index = 0; index < 4; index++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.ROW2_1, index);
            final int colLabel = index + 1;
            final int cc = ElectraOneMiniControlSurface.ELECTRA_FUNCTION_BUTTON_1 + index;
            final ButtonRowModeCommand<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration> command = new ButtonRowModeCommand<> (1, index, this.model, surface);
            this.addButton (surface, buttonID, Integer.toString (colLabel), command, 15, cc, () -> this.getButtonColor (surface, buttonID));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final ElectraOneMiniControlSurface surface = this.getSurface ();
        final IMidiInput midiInput = surface.getMidiInput ();
        for (int index = 0; index < 8; index++)
        {
            final ContinuousID ctrlID = ContinuousID.get (ContinuousID.KNOB1, index);
            final int colLabel = index + 1;
            final int cc = ElectraOneMiniControlSurface.ELECTRA_KNOB_1 + index;
            final IHwContinuousControl ctrl = this.addRelativeKnob (ctrlID, "Ctrl " + colLabel, null, BindType.CC, 15, cc);
            // Can sadly only be set at startup
            ctrl.setIndexInGroup (index);
            ctrl.bindTouch (new TriggerCommand ()
            {
                @Override
                public void execute (ButtonEvent event, int velocity)
                {
                    // TODO
                    surface.updateTouchCombinationCommandsCache ();

                }
            }, midiInput, BindType.CC, 15, ElectraOneMiniControlSurface.ELECTRA_KNOB_TOUCH_1);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final ElectraOneMiniControlSurface surface = this.getSurface ();

        final double width = 12;
        final double height = 10;
        final double padding = 4;
        final double offsetY = 7 * (padding + height);

        for (int row = 0; row < 2; row++)
        {
            for (int col = 0; col < 4; col++)
            {
                final double x = padding + col * (padding + width);
                final double y = padding + row * (padding + height);

                final ContinuousID ctrlID = ContinuousID.get (ContinuousID.KNOB1, row * 4 + col);
                surface.getContinuous (ctrlID).setBounds (x, y, width, height);

                final ButtonID buttonID = ButtonID.get (ButtonID.ROW1_1, row * 4 + col);
                surface.getButton (buttonID).setBounds (x, offsetY + y, width, height);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final ElectraOneMiniControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.CONTROL);
        surface.requestDeviceInfo ();
    }
}
