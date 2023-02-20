// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.mode.DeviceMode;
import de.mossgrabers.controller.electra.one.mode.EqualizerMode;
import de.mossgrabers.controller.electra.one.mode.MixerMode;
import de.mossgrabers.controller.electra.one.mode.SendsMode;
import de.mossgrabers.controller.electra.one.mode.SessionMode;
import de.mossgrabers.controller.electra.one.mode.TransportMode;
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
import de.mossgrabers.framework.view.DummyView;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Electra.One controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneControllerSetup extends AbstractControllerSetup<ElectraOneControlSurface, ElectraOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public ElectraOneControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ElectraOneColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new ElectraOneConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableMainDrumDevice (false);
        ms.enableDevice (DeviceID.EQ);

        ms.setNumTracks (5);
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);
        ms.setNumSends (6);
        ms.setNumMarkers (8);

        ms.setNumScenes (6);

        // Not used (yet)
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
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

        final ElectraOneControlSurface surface = new ElectraOneControlSurface (this.host, this.colorManager, this.configuration, output, input, ctrlInput, ctrlOutput);
        this.surfaces.add (surface);

        surface.getModeManager ().setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.VOLUME, new MixerMode (surface, this.model));
        modeManager.register (Modes.SEND, new SendsMode (surface, this.model));
        modeManager.register (Modes.DEVICE_PARAMS, new DeviceMode (surface, this.model));
        modeManager.register (Modes.EQ_DEVICE_PARAMS, new EqualizerMode (surface, this.model));
        modeManager.register (Modes.TRANSPORT, new TransportMode (surface, this.model));
        modeManager.register (Modes.SESSION, new SessionMode (surface, this.model));
        modeManager.register (Modes.DUMMY, new DummyMode<> (surface, this.model, ElectraOneControlSurface.KNOB_IDS));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();
        // We need this for triggering updateDisplay in the modes
        surface.getViewManager ().register (Views.CONTROL, new DummyView<> ("Control", surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.registerDeactivatedItemsHandler (this.model);

        final ElectraOneControlSurface surface = this.getSurface ();
        this.configuration.addSettingObserver (ElectraOneConfiguration.LOG_TO_CONSOLE, surface::setLoggingEnabled);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();

        for (int row = 0; row < 6; row++)
        {
            final int rowLabel = row + 1;

            for (int col = 0; col < 6; col++)
            {
                final ButtonID buttonID = ElectraOneControlSurface.getButtonID (row, col);
                final int colLabel = col + 1;
                final int cc = ElectraOneControlSurface.ELECTRA_ROW_1 + 10 * row + col;
                final ButtonRowModeCommand<ElectraOneControlSurface, ElectraOneConfiguration> command = new ButtonRowModeCommand<> (row, col, this.model, surface);
                this.addButton (surface, buttonID, "Row " + rowLabel + ": " + colLabel, command, 15, cc, () -> this.getButtonColor (surface, buttonID));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int row = 0; row < 6; row++)
        {
            final int rowLabel = row + 1;

            for (int col = 0; col < 6; col++)
            {
                final ContinuousID ctrlID = ElectraOneControlSurface.getContinuousID (row, col);
                final int colLabel = col + 1;
                final int cc = ElectraOneControlSurface.ELECTRA_CTRL_1 + 10 * row + col;
                final IHwContinuousControl ctrl;
                if (row > 0 && col == 5)
                    ctrl = this.addRelativeKnob (ctrlID, "Ctrl " + rowLabel + "-" + colLabel, null, BindType.CC, 15, cc);
                else
                    ctrl = this.addAbsoluteKnob (ctrlID, "Ctrl " + rowLabel + "-" + colLabel, null, BindType.CC, 15, cc);
                // Can sadly only be set at startup
                if (col < 6)
                    ctrl.setIndexInGroup (row * 6 + col);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();

        final double width = 12;
        final double height = 10;
        final double padding = 4;
        final double offsetY = 7 * (padding + height);

        for (int row = 0; row < 6; row++)
        {
            for (int col = 0; col < 6; col++)
            {
                final double x = padding + col * (padding + width);
                final double y = padding + row * (padding + height);

                final ContinuousID ctrlID = ElectraOneControlSurface.getContinuousID (row, col);
                surface.getContinuous (ctrlID).setBounds (x, y, width, height);

                final ButtonID buttonID = ElectraOneControlSurface.getButtonID (row, col);
                surface.getButton (buttonID).setBounds (x, offsetY + y, width, height);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final ElectraOneControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.CONTROL);
        surface.requestDeviceInfo ();
    }
}
