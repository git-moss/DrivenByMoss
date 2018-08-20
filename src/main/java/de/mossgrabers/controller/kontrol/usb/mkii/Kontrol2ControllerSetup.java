// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii;

import de.mossgrabers.controller.kontrol.usb.mkii.command.continuous.MainEncoderCommand;
import de.mossgrabers.controller.kontrol.usb.mkii.command.trigger.Kontrol2PlayCommand;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2Colors;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2Display;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2UsbDevice;
import de.mossgrabers.controller.kontrol.usb.mkii.mode.Modes;
import de.mossgrabers.controller.kontrol.usb.mkii.mode.track.TrackMode;
import de.mossgrabers.controller.kontrol.usb.mkii.mode.track.VolumeMode;
import de.mossgrabers.controller.kontrol.usb.mkii.view.ControlView;
import de.mossgrabers.controller.kontrol.usb.mkii.view.Views;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.NopCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Setup to support the Native Instruments Komplete Kontrol 2 Sxx controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol2ControllerSetup extends AbstractControllerSetup<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public Kontrol2ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.colorManager = new ColorManager ();
        Kontrol2Colors.addColors (this.colorManager);
        this.configuration = new Kontrol2Configuration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IHost host = this.model.getHost ();

        final Kontrol2UsbDevice usbDevice = new Kontrol2UsbDevice (host);
        usbDevice.init ();

        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("Komplete Kontrol 2",
                "80????" /* Note off */, "90????" /* Note on */, "B040??",
                "B001??" /* Sustainpedal + Modulation */, "D0????" /* Channel Aftertouch */,
                "E0????" /* Pitchbend */);

        final Kontrol2ControlSurface surface = new Kontrol2ControlSurface (host, this.colorManager, this.configuration, input, usbDevice);
        usbDevice.setCallback (surface);
        this.surfaces.add (surface);
        final Kontrol2Display display = new Kontrol2Display (host, this.configuration, usbDevice);
        surface.setDisplay (display);

        surface.getModeManager ().setDefaultMode (Modes.MODE_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final Kontrol2ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.MODE_TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final Kontrol2ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.createScaleObservers (this.configuration);

        this.configuration.addSettingObserver (Kontrol2Configuration.DEBUG_WINDOW, () -> {
            this.getSurface ().getDisplay ().showDebugWindow ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final Kontrol2ControlSurface surface = this.getSurface ();

        this.addTriggerCommand (Commands.COMMAND_METRONOME, Kontrol2ControlSurface.BUTTON_ARP, new MetronomeCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_PLAY, Kontrol2ControlSurface.BUTTON_PLAY, new Kontrol2PlayCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, Kontrol2ControlSurface.BUTTON_REC, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, Kontrol2ControlSurface.BUTTON_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_LOOP, Kontrol2ControlSurface.BUTTON_LOOP, new ToggleLoopCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_PAGE_LEFT, Kontrol2ControlSurface.BUTTON_PAGE_LEFT, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_PARAMS, Modes.MODE_VOLUME, Modes.MODE_TRACK));
        this.addTriggerCommand (Commands.COMMAND_PAGE_RIGHT, Kontrol2ControlSurface.BUTTON_PAGE_RIGHT, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_TRACK, Modes.MODE_VOLUME, Modes.MODE_PARAMS));

        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_1, Kontrol2ControlSurface.TOUCH_ENCODER_1, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_2, Kontrol2ControlSurface.TOUCH_ENCODER_2, new KnobRowTouchModeCommand<> (1, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_3, Kontrol2ControlSurface.TOUCH_ENCODER_3, new KnobRowTouchModeCommand<> (2, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_4, Kontrol2ControlSurface.TOUCH_ENCODER_4, new KnobRowTouchModeCommand<> (3, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_5, Kontrol2ControlSurface.TOUCH_ENCODER_5, new KnobRowTouchModeCommand<> (4, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_6, Kontrol2ControlSurface.TOUCH_ENCODER_6, new KnobRowTouchModeCommand<> (5, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_7, Kontrol2ControlSurface.TOUCH_ENCODER_7, new KnobRowTouchModeCommand<> (6, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_8, Kontrol2ControlSurface.TOUCH_ENCODER_8, new KnobRowTouchModeCommand<> (7, this.model, surface));

        // Block unused knobs and touches
        this.addTriggerCommand (Commands.COMMAND_ROW1_1, Kontrol2ControlSurface.TOUCH_ENCODER_MAIN, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_2, Kontrol2ControlSurface.BUTTON_INSTANCE, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_3, Kontrol2ControlSurface.BUTTON_PRESET_UP, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_4, Kontrol2ControlSurface.BUTTON_PRESET_DOWN, new NopCommand<> (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final Kontrol2ControlSurface surface = this.getSurface ();

        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), Kontrol2ControlSurface.ENCODER_1 + i, new KnobRowModeCommand<> (i, this.model, surface));

        this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB, Kontrol2ControlSurface.MAIN_ENCODER, new MainEncoderCommand (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final Kontrol2ControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
        surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
    }
}
