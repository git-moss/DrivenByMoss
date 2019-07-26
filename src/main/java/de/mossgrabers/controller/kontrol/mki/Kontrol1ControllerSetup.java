// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.mki;

import de.mossgrabers.controller.kontrol.mki.command.continuous.MainEncoderCommand;
import de.mossgrabers.controller.kontrol.mki.command.trigger.BackButtonCommand;
import de.mossgrabers.controller.kontrol.mki.command.trigger.EnterButtonCommand;
import de.mossgrabers.controller.kontrol.mki.command.trigger.Kontrol1CursorCommand;
import de.mossgrabers.controller.kontrol.mki.command.trigger.Kontrol1PlayCommand;
import de.mossgrabers.controller.kontrol.mki.command.trigger.MainEncoderButtonCommand;
import de.mossgrabers.controller.kontrol.mki.command.trigger.ScaleButtonCommand;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Colors;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1ControlSurface;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1Display;
import de.mossgrabers.controller.kontrol.mki.controller.Kontrol1UsbDevice;
import de.mossgrabers.controller.kontrol.mki.mode.ScaleMode;
import de.mossgrabers.controller.kontrol.mki.mode.device.BrowseMode;
import de.mossgrabers.controller.kontrol.mki.mode.device.ParamsMode;
import de.mossgrabers.controller.kontrol.mki.mode.track.TrackMode;
import de.mossgrabers.controller.kontrol.mki.mode.track.VolumeMode;
import de.mossgrabers.controller.kontrol.mki.view.ControlView;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.BrowserCommand;
import de.mossgrabers.framework.command.trigger.mode.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
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
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


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
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public Kontrol1ControllerSetup (final int modelIndex, final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);
        this.modelIndex = modelIndex;
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.colorManager = new ColorManager ();
        this.configuration = new Kontrol1Configuration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 88, 88, 1);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setNumDrumPadLayers (128);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, isSelected) -> {
            final View activeView = this.getSurface ().getViewManager ().getActiveView ();
            if (activeView instanceof ControlView)
                ((ControlView) activeView).updateButtons ();
        });

        final ICursorDevice primary = this.model.getInstrumentDevice ();
        primary.getDrumPadBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final Kontrol1UsbDevice usbDevice = new Kontrol1UsbDevice (this.modelIndex, this.host);
        usbDevice.init ();

        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("Komplete Kontrol 1",
                "80????" /* Note off */, "90????" /* Note on */, "B040??",
                "B001??" /* Sustainpedal + Modulation */, "D0????" /* Channel Aftertouch */,
                "E0????" /* Pitchbend */);

        Kontrol1Colors.addColors (this.colorManager);

        final Kontrol1ControlSurface surface = new Kontrol1ControlSurface (this.host, this.colorManager, this.configuration, input, usbDevice);
        usbDevice.setCallback (surface);
        this.surfaces.add (surface);
        final Kontrol1Display display = new Kontrol1Display (this.host, this.valueChanger.getUpperBound (), this.configuration, usbDevice);
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
        modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new ParamsMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_BROWSER, new BrowseMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_SCALES, new ScaleMode (surface, this.model));
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
        this.configuration.addSettingObserver (Kontrol1Configuration.SCALE_IS_ACTIVE, this::updateViewNoteMapping);

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

        this.addTriggerCommand (TriggerCommandID.SCALES, Kontrol1ControlSurface.BUTTON_SCALE, new ScaleButtonCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.METRONOME, Kontrol1ControlSurface.BUTTON_ARP, new MetronomeCommand<> (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.PLAY, Kontrol1ControlSurface.BUTTON_PLAY, new Kontrol1PlayCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.RECORD, Kontrol1ControlSurface.BUTTON_REC, new RecordCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.STOP, Kontrol1ControlSurface.BUTTON_STOP, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.REWIND, Kontrol1ControlSurface.BUTTON_RWD, new WindCommand<> (this.model, surface, false));
        this.addTriggerCommand (TriggerCommandID.FORWARD, Kontrol1ControlSurface.BUTTON_FWD, new WindCommand<> (this.model, surface, true));
        this.addTriggerCommand (TriggerCommandID.LOOP, Kontrol1ControlSurface.BUTTON_LOOP, new ToggleLoopCommand<> (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.PAGE_LEFT, Kontrol1ControlSurface.BUTTON_PAGE_LEFT, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_DEVICE_PARAMS, Modes.MODE_VOLUME, Modes.MODE_TRACK));
        this.addTriggerCommand (TriggerCommandID.PAGE_RIGHT, Kontrol1ControlSurface.BUTTON_PAGE_RIGHT, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_TRACK, Modes.MODE_VOLUME, Modes.MODE_DEVICE_PARAMS));

        this.addTriggerCommand (TriggerCommandID.MASTERTRACK, Kontrol1ControlSurface.BUTTON_MAIN_ENCODER, new MainEncoderButtonCommand (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.ARROW_DOWN, Kontrol1ControlSurface.BUTTON_NAVIGATE_DOWN, new Kontrol1CursorCommand (Direction.DOWN, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_UP, Kontrol1ControlSurface.BUTTON_NAVIGATE_UP, new Kontrol1CursorCommand (Direction.UP, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_LEFT, Kontrol1ControlSurface.BUTTON_NAVIGATE_LEFT, new Kontrol1CursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.ARROW_RIGHT, Kontrol1ControlSurface.BUTTON_NAVIGATE_RIGHT, new Kontrol1CursorCommand (Direction.RIGHT, this.model, surface));

        this.addTriggerCommand (TriggerCommandID.MUTE, Kontrol1ControlSurface.BUTTON_BACK, new BackButtonCommand (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SOLO, Kontrol1ControlSurface.BUTTON_ENTER, new EnterButtonCommand (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.BROWSE, Kontrol1ControlSurface.BUTTON_BROWSE, new BrowserCommand<> (Modes.MODE_BROWSER, this.model, surface));

        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_1, Kontrol1ControlSurface.TOUCH_ENCODER_1, new KnobRowTouchModeCommand<> (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_2, Kontrol1ControlSurface.TOUCH_ENCODER_2, new KnobRowTouchModeCommand<> (1, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_3, Kontrol1ControlSurface.TOUCH_ENCODER_3, new KnobRowTouchModeCommand<> (2, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_4, Kontrol1ControlSurface.TOUCH_ENCODER_4, new KnobRowTouchModeCommand<> (3, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_5, Kontrol1ControlSurface.TOUCH_ENCODER_5, new KnobRowTouchModeCommand<> (4, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_6, Kontrol1ControlSurface.TOUCH_ENCODER_6, new KnobRowTouchModeCommand<> (5, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_7, Kontrol1ControlSurface.TOUCH_ENCODER_7, new KnobRowTouchModeCommand<> (6, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.FADER_TOUCH_8, Kontrol1ControlSurface.TOUCH_ENCODER_8, new KnobRowTouchModeCommand<> (7, this.model, surface));

        // Block unused knobs and touches
        this.addTriggerCommand (TriggerCommandID.ROW1_1, Kontrol1ControlSurface.TOUCH_ENCODER_MAIN, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.ROW1_2, Kontrol1ControlSurface.BUTTON_INSTANCE, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.ROW1_3, Kontrol1ControlSurface.BUTTON_PRESET_UP, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.ROW1_4, Kontrol1ControlSurface.BUTTON_PRESET_DOWN, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.ROW1_5, Kontrol1ControlSurface.BUTTON_OCTAVE_DOWN, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.ROW1_6, Kontrol1ControlSurface.BUTTON_OCTAVE_UP, NopCommand.INSTANCE);
        this.addTriggerCommand (TriggerCommandID.SHIFT, Kontrol1ControlSurface.BUTTON_SHIFT, NopCommand.INSTANCE);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final Kontrol1ControlSurface surface = this.getSurface ();

        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.KNOB1, i), Kontrol1ControlSurface.ENCODER_1 + i, new KnobRowModeCommand<> (i, this.model, surface));

        this.addContinuousCommand (ContinuousCommandID.MASTER_KNOB, Kontrol1ControlSurface.MAIN_ENCODER, new MainEncoderCommand (this.model, surface));
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
        if (!isSelected)
            return;

        this.host.scheduleTask ( () -> {
            final Kontrol1ControlSurface surface = this.getSurface ();
            this.updateIndication (surface.getModeManager ().getActiveModeId ());
            final View activeView = surface.getViewManager ().getActiveView ();
            if (activeView != null)
                activeView.updateNoteMapping ();

            if (this.model.canSelectedTrackHoldNotes ())
            {
                final ICursorDevice primary = this.model.getInstrumentDevice ();
                if (primary.hasDrumPads ())
                    primary.getDrumPadBank ().scrollTo (0);
            }
        }, 100);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode.equals (mode))
            return;
        this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        final boolean isVolume = Modes.MODE_VOLUME.equals (mode);
        final boolean isDevice = Modes.MODE_DEVICE_PARAMS.equals (mode);

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
