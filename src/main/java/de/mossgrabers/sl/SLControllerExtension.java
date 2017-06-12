// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.AbstractControllerExtension;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.sl.command.continuous.DeviceKnobRowCommand;
import de.mossgrabers.sl.command.continuous.FaderCommand;
import de.mossgrabers.sl.command.continuous.TapTempoInitMkICommand;
import de.mossgrabers.sl.command.continuous.TapTempoMkICommand;
import de.mossgrabers.sl.command.continuous.TouchpadCommand;
import de.mossgrabers.sl.command.continuous.TrackKnobRowCommand;
import de.mossgrabers.sl.command.trigger.ButtonRowSelectCommand;
import de.mossgrabers.sl.command.trigger.ButtonRowViewCommand;
import de.mossgrabers.sl.command.trigger.P1ButtonCommand;
import de.mossgrabers.sl.command.trigger.P2ButtonCommand;
import de.mossgrabers.sl.command.trigger.TransportButtonCommand;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.controller.SLDisplay;
import de.mossgrabers.sl.controller.SLKeysMidiInput;
import de.mossgrabers.sl.controller.SLMidiInput;
import de.mossgrabers.sl.mode.FixedMode;
import de.mossgrabers.sl.mode.FrameMode;
import de.mossgrabers.sl.mode.FunctionMode;
import de.mossgrabers.sl.mode.MasterMode;
import de.mossgrabers.sl.mode.Modes;
import de.mossgrabers.sl.mode.PlayOptionsMode;
import de.mossgrabers.sl.mode.SessionMode;
import de.mossgrabers.sl.mode.TrackMode;
import de.mossgrabers.sl.mode.TrackTogglesMode;
import de.mossgrabers.sl.mode.ViewSelectMode;
import de.mossgrabers.sl.mode.VolumeMode;
import de.mossgrabers.sl.mode.device.DeviceParamsMode;
import de.mossgrabers.sl.mode.device.DevicePresetsMode;
import de.mossgrabers.sl.view.ControlView;
import de.mossgrabers.sl.view.PlayView;
import de.mossgrabers.sl.view.Views;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the Novation SLMkII Pro and SLMkII MkII controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLControllerExtension extends AbstractControllerExtension<SLControlSurface, SLConfiguration>
{
    private static final int [] DRUM_MATRIX =
    {
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1
    };

    private final boolean       isMkII;


    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     * @param isMkII True if SLMkII
     */
    protected SLControllerExtension (final SLControllerExtensionDefinition extensionDefinition, final ControllerHost host, final boolean isMkII)
    {
        super (extensionDefinition, host);
        this.isMkII = isMkII;
        this.colorManager = new ColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new SLConfiguration (this.valueChanger, isMkII);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.surface.flush ();
        this.updateIndication ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 52, 8, 2);
        this.scales.setDrumMatrix (DRUM_MATRIX);
        this.scales.setDrumNoteEnd (52);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        this.model = new Model (this.getHost (), this.colorManager, this.valueChanger, this.scales, 8, 8, 6, 16, 16, true, -1, -1, -1, -1);

        this.model.getTrackBank ().addTrackSelectionObserver (this::handleTrackChange);
        this.model.getMasterTrack ().addTrackSelectionObserver ( (index, isSelected) -> {
            final ModeManager modeManager = this.surface.getModeManager ();
            modeManager.setActiveMode (isSelected ? Modes.MODE_MASTER : modeManager.getPreviousModeId ());
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final ControllerHost host = this.getHost ();
        final MidiOutput output = new MidiOutput (host);
        final MidiInput input = new SLMidiInput (this.isMkII);
        final MidiInput keysInput = new SLKeysMidiInput (this.isMkII);
        keysInput.init (host);
        keysInput.createNoteInput ();

        this.surface = new SLControlSurface (host, this.colorManager, this.configuration, output, input, this.isMkII);
        this.surface.setDisplay (new SLDisplay (host, output));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.registerMode (Modes.MODE_FIXED, new FixedMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_FRAME, new FrameMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_FUNCTIONS, new FunctionMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_MASTER, new MasterMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_PLAY_OPTIONS, new PlayOptionsMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_SESSION, new SessionMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_TRACK, new TrackMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_TRACK_TOGGLES, new TrackTogglesMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_VIEW_SELECT, new ViewSelectMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_PARAMS, new DeviceParamsMode (this.surface, this.model));
        modeManager.registerMode (Modes.MODE_BROWSER, new DevicePresetsMode (this.surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_CONTROL, new ControlView (this.surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        for (int i = 0; i < 8; i++)
        {
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW1_1.intValue () + i), SLControlSurface.MKII_BUTTON_ROW1_1 + i, new ButtonRowViewCommand<> (0, i, this.model, this.surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW2_1.intValue () + i), SLControlSurface.MKII_BUTTON_ROW2_1 + i, new ButtonRowViewCommand<> (1, i, this.model, this.surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW3_1.intValue () + i), SLControlSurface.MKII_BUTTON_ROW3_1 + i, new ButtonRowViewCommand<> (2, i, this.model, this.surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW4_1.intValue () + i), SLControlSurface.MKII_BUTTON_ROW4_1 + i, new ButtonRowViewCommand<> (3, i, this.model, this.surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW_SELECT_1.intValue () + i), SLControlSurface.MKII_BUTTON_ROWSEL1 + i, new ButtonRowSelectCommand<> (i, this.model, this.surface));
        }

        this.addTriggerCommand (Commands.COMMAND_REWIND, SLControlSurface.MKII_BUTTON_REWIND, new ButtonRowViewCommand<> (4, 0, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_FORWARD, SLControlSurface.MKII_BUTTON_FORWARD, new ButtonRowViewCommand<> (4, 1, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, SLControlSurface.MKII_BUTTON_STOP, new ButtonRowViewCommand<> (4, 2, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_PLAY, SLControlSurface.MKII_BUTTON_PLAY, new ButtonRowViewCommand<> (4, 3, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_LOOP, SLControlSurface.MKII_BUTTON_LOOP, new ButtonRowViewCommand<> (4, 4, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, SLControlSurface.MKII_BUTTON_RECORD, new ButtonRowViewCommand<> (4, 6, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_LEFT, SLControlSurface.MKII_BUTTON_P1_UP, new P1ButtonCommand (true, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_RIGHT, SLControlSurface.MKII_BUTTON_P1_DOWN, new P1ButtonCommand (false, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_UP, SLControlSurface.MKII_BUTTON_P2_UP, new P2ButtonCommand (true, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_DOWN, SLControlSurface.MKII_BUTTON_P2_DOWN, new P2ButtonCommand (false, this.model, this.surface));
        this.addTriggerCommand (Commands.COMMAND_SELECT_PLAY_VIEW, SLControlSurface.MKII_BUTTON_TRANSPORT, new TransportButtonCommand (this.model, this.surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        for (int i = 0; i < 8; i++)
        {
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_FADER1.intValue () + i), SLControlSurface.MKII_SLIDER1 + i, new FaderCommand (i, this.model, this.surface));
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_DEVICE_KNOB1.intValue () + i), SLControlSurface.MKII_KNOB_ROW1_1 + i, new DeviceKnobRowCommand (i, this.model, this.surface));
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), SLControlSurface.MKII_KNOB_ROW2_1 + i, new TrackKnobRowCommand (i, this.model, this.surface));
        }
        this.addContinuousCommand (Commands.CONT_COMMAND_TOUCHPAD_X, SLControlSurface.MKII_TOUCHPAD_X, new TouchpadCommand (true, this.model, this.surface));
        this.addContinuousCommand (Commands.CONT_COMMAND_TOUCHPAD_Y, SLControlSurface.MKII_TOUCHPAD_Y, new TouchpadCommand (false, this.model, this.surface));
        this.addContinuousCommand (Commands.CONT_COMMAND_TEMPO_TOUCH, SLControlSurface.MKI_BUTTON_TAP_TEMPO, new TapTempoInitMkICommand (this.model, this.surface));
        this.addContinuousCommand (Commands.CONT_COMMAND_TEMPO, SLControlSurface.MKI_BUTTON_TAP_TEMPO_VALUE, new TapTempoMkICommand (this.model, this.surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        // Initialise 2nd display
        this.surface.getModeManager ().getMode (Modes.MODE_VOLUME).updateDisplay ();

        this.getHost ().scheduleTask ( () -> {
            this.surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);
            this.surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
        }, 200);
    }


    private void updateIndication ()
    {
        final Integer mode = this.surface.getModeManager ().getActiveModeId ();

        final MasterTrackProxy mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (Modes.MODE_MASTER.equals (mode));
        mt.setPanIndication (Modes.MODE_MASTER.equals (mode));

        final TrackBankProxy tb = this.model.getTrackBank ();
        final EffectTrackBankProxy tbe = this.model.getEffectTrackBank ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isVolume = Modes.MODE_VOLUME.equals (mode);

        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        final TrackData selectedTrack = tb.getSelectedTrack ();
        for (int i = 0; i < 8; i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i && Modes.MODE_TRACK.equals (mode);
            tb.setVolumeIndication (i, !isEffect && (isVolume || hasTrackSel));
            tb.setPanIndication (i, !isEffect && hasTrackSel);

            for (int j = 0; j < 6; j++)
                tb.setSendIndication (i, j, !isEffect && hasTrackSel);

            tbe.setVolumeIndication (i, isEffect);
            tbe.setPanIndication (i, isEffect);

            cursorDevice.getParameter (i).setIndication (true);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (isSelected && modeManager.isActiveMode (Modes.MODE_MASTER))
            modeManager.setActiveMode (Modes.MODE_TRACK);
    }
}
