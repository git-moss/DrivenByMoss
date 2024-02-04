// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4;

import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.controller.faderfox.ec4.controller.EC4Display;
import de.mossgrabers.controller.faderfox.ec4.mode.EC4ParametersMode;
import de.mossgrabers.controller.faderfox.ec4.mode.EC4TrackMode;
import de.mossgrabers.controller.faderfox.ec4.mode.EC4TwelveMode;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwContinuousControl;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.DummyMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.DummyView;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Faderfox EC4 controller.
 *
 * @author Jürgen Moßgraber
 */
public class EC4ControllerSetup extends AbstractControllerSetup<EC4ControlSurface, EC4Configuration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public EC4ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ColorManager ();
        this.colorManager.registerColor (0, ColorEx.BLACK);
        this.colorManager.registerColor (1, ColorEx.DARK_RED);
        this.colorManager.registerColor (127, ColorEx.RED);
        this.colorManager.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF, 0);
        this.colorManager.registerColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON, 127);
        this.colorManager.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.colorManager.registerColorIndex (ColorManager.BUTTON_STATE_ON, 1);
        this.colorManager.registerColorIndex (ColorManager.BUTTON_STATE_HI, 127);

        this.valueChanger = new TwosComplementValueChanger (1024, 10);
        this.configuration = new EC4Configuration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableMainDrumDevice (false);

        ms.setNumTracks (12);
        ms.setHasFullFlatTrackList (true);

        ms.setNumScenes (12);

        // Not used
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

        final EC4ControlSurface surface = new EC4ControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.addTextDisplay (new EC4Display (this.host, output, EC4Display.DISPLAY_CONTROLS));
        surface.addTextDisplay (new EC4Display (this.host, output, EC4Display.DISPLAY_TOTAL));

        surface.getModeManager ().setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final EC4ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.TRACK, new EC4TrackMode (surface, this.model));
        modeManager.register (Modes.TRACK_DETAILS, new EC4TwelveMode (surface, this.model));
        modeManager.register (Modes.DEVICE_PARAMS, new EC4ParametersMode (surface, this.model));
        modeManager.register (Modes.DUMMY, new DummyMode<> (surface, this.model, EC4ControlSurface.KNOB_IDS));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final EC4ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.DUMMY1, new DummyView<> ("", surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.addSettingObserver (EC4Configuration.SETUP_SLOT, () -> {

            this.getSurface ().setSetupSlot (this.configuration.getSetupSlot ());

        });

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final EC4ControlSurface surface = this.getSurface ();
        for (int i = 0; i < 16; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.PAD1, i);
            final int label = i + 1;
            final int cc = EC4ControlSurface.EC4_BUTTON_1 + i;
            final ButtonRowModeCommand<EC4ControlSurface, EC4Configuration> command = new ButtonRowModeCommand<> (i / 4, i % 4, this.model, surface);
            this.addButton (surface, buttonID, "Button " + label, command, 15, cc);
        }

        for (int i = 0; i < 4; i++)
        {
            final int index = i;
            surface.createButton (ButtonID.get (ButtonID.FOOTSWITCH1, i), "Action " + (i + 1)).bind ( (event, velocity) -> {

                if (event == ButtonEvent.DOWN)
                {
                    final String assignableActionID = this.configuration.getAssignableAction (index);
                    if (assignableActionID != null)
                        this.model.getApplication ().invokeAction (assignableActionID);
                }

            });
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int i = 0; i < 16; i++)
        {
            final ContinuousID knobID = ContinuousID.get (ContinuousID.KNOB1, i);
            final int label = i + 1;
            final int cc = EC4ControlSurface.EC4_KNOB_1 + i;
            final IHwContinuousControl ctrl = this.addRelativeKnob (knobID, "Knob " + label, null, BindType.CC, 15, cc);
            // Can sadly only be set at startup
            if (i < 12)
                ctrl.setIndexInGroup (i);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final EC4ControlSurface surface = this.getSurface ();

        final double padding = 6;
        final double width = 100;
        final double height = 100;
        final double offsetY = 100;

        surface.getTextDisplay ().getHardwareDisplay ().setBounds (padding, padding, 8 * (padding + width), offsetY - padding);

        for (int i = 0; i < 16; i++)
        {
            final int col = i / 4;
            final int row = i % 4;
            final double x = padding + col * (padding + width);
            final double y = offsetY + padding + 2 * row * (padding + height);

            final ContinuousID knobID = ContinuousID.get (ContinuousID.KNOB1, i);
            surface.getContinuous (knobID).setBounds (x, y, width, height);

            final ButtonID buttonID = ButtonID.get (ButtonID.PAD1, i);
            surface.getButton (buttonID).setBounds (x, y + height + padding, width, height);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final EC4ControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.DUMMY1);
        surface.getModeManager ().setActive (Modes.DUMMY);
        surface.requestDeviceInfo ();
    }
}
