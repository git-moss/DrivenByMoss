// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mki;

import de.mossgrabers.controller.kontrol.usb.mki.command.continuous.MainEncoderCommand;
import de.mossgrabers.controller.kontrol.usb.mki.command.trigger.BackButtonCommand;
import de.mossgrabers.controller.kontrol.usb.mki.command.trigger.EnterButtonCommand;
import de.mossgrabers.controller.kontrol.usb.mki.command.trigger.Kontrol1CursorCommand;
import de.mossgrabers.controller.kontrol.usb.mki.command.trigger.Kontrol1PlayCommand;
import de.mossgrabers.controller.kontrol.usb.mki.command.trigger.MainEncoderButtonCommand;
import de.mossgrabers.controller.kontrol.usb.mki.command.trigger.ScaleButtonCommand;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.kontrol.usb.mki.controller.Kontrol1UsbDevice;
import de.mossgrabers.controller.kontrol.usb.mki.mode.Modes;
import de.mossgrabers.controller.kontrol.usb.mki.mode.ScaleMode;
import de.mossgrabers.controller.kontrol.usb.mki.mode.device.BrowseMode;
import de.mossgrabers.controller.kontrol.usb.mki.mode.device.ParamsMode;
import de.mossgrabers.controller.kontrol.usb.mki.mode.track.TrackMode;
import de.mossgrabers.controller.kontrol.usb.mki.mode.track.VolumeMode;
import de.mossgrabers.controller.kontrol.usb.mki.view.ControlView;
import de.mossgrabers.controller.kontrol.usb.mki.view.Views;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.CursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.NopCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;


/**
 * Setup to support the Native Instruments Komplete Kontrol 1 Sxx controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Kontrol1ControllerSetup extends AbstractControllerSetup<Kontrol1ControlSurface, Kontrol1Configuration>
{
    private final int modelIndex;


    /**
     * Constructor.
     *
     * @param modelIndex The index of the model (S25, S49, S61, S88)
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public Kontrol1ControllerSetup (final int modelIndex, final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);
        this.modelIndex = modelIndex;
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.colorManager = new ColorManager ();
        this.configuration = new Kontrol1Configuration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, isSelected) -> {
            final View activeView = this.getSurface ().getViewManager ().getActiveView ();
            if (activeView instanceof ControlView)
                ((ControlView) activeView).updateButtons ();
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IHost host = this.model.getHost ();
        final Kontrol1UsbDevice usbDevice = new Kontrol1UsbDevice (this.modelIndex, host);
        usbDevice.init ();

        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("Komplete Kontrol 1",
                "80????" /* Note off */, "90????" /* Note on */, "B040??",
                "B001??" /* Sustainpedal + Modulation */, "D0????" /* Channel Aftertouch */,
                "E0????" /* Pitchbend */);

        final Kontrol1ControlSurface surface = new Kontrol1ControlSurface (host, this.colorManager, this.configuration, input, usbDevice);
        usbDevice.setCallback (surface);
        this.surfaces.add (surface);
        final Kontrol1Display display = new Kontrol1Display (host, this.valueChanger.getUpperBound (), this.configuration, usbDevice);
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

        this.getSurface ().getModeManager ().addModeListener ( (oldMode, newMode) -> this.updateIndication (newMode));

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
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
        this.addTriggerCommand (Commands.COMMAND_LOOP, Kontrol1ControlSurface.BUTTON_LOOP, new ToggleLoopCommand<> (this.model, surface));

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

        // Block unused knobs and touches
        this.addTriggerCommand (Commands.COMMAND_ROW1_1, Kontrol1ControlSurface.TOUCH_ENCODER_MAIN, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_2, Kontrol1ControlSurface.BUTTON_INSTANCE, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_3, Kontrol1ControlSurface.BUTTON_PRESET_UP, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ROW1_4, Kontrol1ControlSurface.BUTTON_PRESET_DOWN, new NopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_SHIFT, Kontrol1ControlSurface.BUTTON_SHIFT, new NopCommand<> (this.model, surface));
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
    public void startup ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
        surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (isSelected)
            this.updateIndication (this.getSurface ().getModeManager ().getActiveModeId ());
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        if (mode == this.currentMode)
            return;
        this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        final boolean isVolume = Modes.MODE_VOLUME.equals (mode);
        final boolean isDevice = Modes.MODE_PARAMS.equals (mode);

        tb.setIndication (isVolume);
        if (tbe != null)
            tbe.setIndication (isEffect && isVolume);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && Modes.MODE_TRACK.equals (mode);
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && (isVolume || hasTrackSel));
            track.setPanIndication (!isEffect && hasTrackSel);

            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 6; j++)
                sendBank.getItem (j).setIndication (!isEffect && hasTrackSel);

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
            }

            parameterBank.getItem (i).setIndication (isDevice);
        }
    }
}
