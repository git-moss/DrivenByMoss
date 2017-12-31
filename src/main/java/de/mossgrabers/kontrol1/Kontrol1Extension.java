// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.kontrol1;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.CursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.LoopCommand;
import de.mossgrabers.framework.command.trigger.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.RecordCommand;
import de.mossgrabers.framework.command.trigger.StopCommand;
import de.mossgrabers.framework.command.trigger.WindCommand;
import de.mossgrabers.framework.controller.AbstractControllerExtension;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.kontrol1.command.continuous.MainEncoderCommand;
import de.mossgrabers.kontrol1.command.trigger.BackButtonCommand;
import de.mossgrabers.kontrol1.command.trigger.EnterButtonCommand;
import de.mossgrabers.kontrol1.command.trigger.Kontrol1CursorCommand;
import de.mossgrabers.kontrol1.command.trigger.Kontrol1PlayCommand;
import de.mossgrabers.kontrol1.command.trigger.MainEncoderButtonCommand;
import de.mossgrabers.kontrol1.command.trigger.ScaleButtonCommand;
import de.mossgrabers.kontrol1.controller.Kontrol1ControlSurface;
import de.mossgrabers.kontrol1.controller.Kontrol1Display;
import de.mossgrabers.kontrol1.controller.Kontrol1MidiInput;
import de.mossgrabers.kontrol1.controller.Kontrol1USBDevice;
import de.mossgrabers.kontrol1.mode.Modes;
import de.mossgrabers.kontrol1.mode.ScaleMode;
import de.mossgrabers.kontrol1.mode.device.BrowseMode;
import de.mossgrabers.kontrol1.mode.device.ParamsMode;
import de.mossgrabers.kontrol1.mode.track.TrackMode;
import de.mossgrabers.kontrol1.mode.track.VolumeMode;
import de.mossgrabers.kontrol1.view.ControlView;
import de.mossgrabers.kontrol1.view.Views;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the Native Instruments Komplete Kontrol 1 Sxx controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1Extension extends AbstractControllerExtension<Kontrol1ControlSurface, Kontrol1Configuration>
{
    private Kontrol1USBDevice usbDevice;


    /**
     * Constructor.
     *
     * @param definition The extension definition
     * @param host The Bitwig host
     */
    protected Kontrol1Extension (final AbstractKontrol1ExtensionDefinition definition, final ControllerHost host)
    {
        super (definition, host);
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.configuration = new Kontrol1Configuration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        this.model = new Model (this.getHost (), this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final ControllerHost host = this.getHost ();

        this.usbDevice = new Kontrol1USBDevice (host);
        this.usbDevice.init ();

        final MidiInput input = new Kontrol1MidiInput ();
        input.init (host);
        input.createNoteInput ();

        final Kontrol1ControlSurface surface = new Kontrol1ControlSurface (host, this.configuration, null, this.usbDevice);
        this.usbDevice.setCallback (surface);
        this.surfaces.add (surface);
        final Kontrol1Display display = new Kontrol1Display (host, this.valueChanger.getUpperBound (), this.configuration, this.usbDevice);
        surface.setDisplay (display);

        surface.getModeManager ().setDefaultMode (Modes.MODE_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.MODE_TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PARAMS, new ParamsMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_BROWSER, new BrowseMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_SCALE, new ScaleMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();

        this.addTriggerCommand (Commands.COMMAND_SCALES, Kontrol1ControlSurface.BUTTON_SCALE, new ScaleButtonCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_METRONOME, Kontrol1ControlSurface.BUTTON_ARP, new MetronomeCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_PLAY, Kontrol1ControlSurface.BUTTON_PLAY, new Kontrol1PlayCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, Kontrol1ControlSurface.BUTTON_REC, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, Kontrol1ControlSurface.BUTTON_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_REWIND, Kontrol1ControlSurface.BUTTON_RWD, new WindCommand<> (this.model, surface, false));
        this.addTriggerCommand (Commands.COMMAND_FORWARD, Kontrol1ControlSurface.BUTTON_FWD, new WindCommand<> (this.model, surface, true));
        this.addTriggerCommand (Commands.COMMAND_LOOP, Kontrol1ControlSurface.BUTTON_LOOP, new LoopCommand<> (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_PAGE_LEFT, Kontrol1ControlSurface.BUTTON_PAGE_LEFT, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_PARAMS, Modes.MODE_VOLUME, Modes.MODE_TRACK));
        this.addTriggerCommand (Commands.COMMAND_PAGE_RIGHT, Kontrol1ControlSurface.BUTTON_PAGE_RIGHT, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_TRACK, Modes.MODE_VOLUME, Modes.MODE_PARAMS));

        this.addTriggerCommand (Commands.COMMAND_MASTERTRACK, Kontrol1ControlSurface.BUTTON_MAIN_ENCODER, new MainEncoderButtonCommand (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_ARROW_DOWN, Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN, new Kontrol1CursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_UP, Kontrol1ControlSurface.BUTTON_NAVIGATE_UP, new Kontrol1CursorCommand (Direction.UP, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_LEFT, Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT, new Kontrol1CursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_RIGHT, Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT, new Kontrol1CursorCommand (Direction.RIGHT, this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_MUTE, Kontrol1ControlSurface.BUTTON_BACK, new BackButtonCommand (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SOLO, Kontrol1ControlSurface.BUTTON_ENTER, new EnterButtonCommand (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_BROWSE, Kontrol1ControlSurface.BUTTON_BROWSE, new BrowserCommand<> (Modes.MODE_BROWSER, this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_1, Kontrol1ControlSurface.TOUCH_ENCODER_1, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_2, Kontrol1ControlSurface.TOUCH_ENCODER_2, new KnobRowTouchModeCommand<> (1, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_3, Kontrol1ControlSurface.TOUCH_ENCODER_3, new KnobRowTouchModeCommand<> (2, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_4, Kontrol1ControlSurface.TOUCH_ENCODER_4, new KnobRowTouchModeCommand<> (3, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_5, Kontrol1ControlSurface.TOUCH_ENCODER_5, new KnobRowTouchModeCommand<> (4, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_6, Kontrol1ControlSurface.TOUCH_ENCODER_6, new KnobRowTouchModeCommand<> (5, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_7, Kontrol1ControlSurface.TOUCH_ENCODER_7, new KnobRowTouchModeCommand<> (6, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_FADER_TOUCH_8, Kontrol1ControlSurface.TOUCH_ENCODER_8, new KnobRowTouchModeCommand<> (7, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();

        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), Kontrol1ControlSurface.ENCODER_1 + i, new KnobRowModeCommand<> (i, this.model, surface));

        this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB, Kontrol1ControlSurface.MAIN_ENCODER, new MainEncoderCommand (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();
        this.getHost ().scheduleTask ( () -> {
            surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
            surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
        }, 200);

        this.usbDevice.pollUI ();
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
    }
}
