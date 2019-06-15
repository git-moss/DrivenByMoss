// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii;

import de.mossgrabers.controller.slmkiii.command.continuous.VolumeFaderCommand;
import de.mossgrabers.controller.slmkiii.command.trigger.ButtonAreaCommand;
import de.mossgrabers.controller.slmkiii.command.trigger.OptionsCommand;
import de.mossgrabers.controller.slmkiii.command.trigger.SLMkIIICursorCommand;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.slmkiii.mode.OptionsMode;
import de.mossgrabers.controller.slmkiii.mode.device.ParametersMode;
import de.mossgrabers.controller.slmkiii.mode.track.PanMode;
import de.mossgrabers.controller.slmkiii.mode.track.SendMode;
import de.mossgrabers.controller.slmkiii.mode.track.TrackMode;
import de.mossgrabers.controller.slmkiii.mode.track.VolumeMode;
import de.mossgrabers.controller.slmkiii.view.ControlView;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.ShiftCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Novation SLMkIII controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIIControllerSetup extends AbstractControllerSetup<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    private ButtonArea buttonArea;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public SLMkIIIControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        this.colorManager = new ColorManager ();
        SLMkIIIColors.addColors (this.colorManager);

        this.valueChanger = new DefaultValueChanger (1024, 8, 1);
        this.configuration = new SLMkIIIConfiguration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
        this.updateButtons ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        // TODO
        this.scales = new Scales (this.valueChanger, 36, 52, 8, 2);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);
        ms.setNumScenes (2);
        ms.setNumSends (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        midiAccess.createInput (1, "Novation SL MkIII", "80????", "90????", "B0????", "D0????", "E0????");
        final IHost hostProxy = this.model.getHost ();
        final SLMkIIIControlSurface surface = new SLMkIIIControlSurface (hostProxy, this.colorManager, this.configuration, output, midiAccess.createInput (null));
        this.surfaces.add (surface);
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
        final SLMkIIIControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.MODE_TRACK, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + i), new SendMode (i, surface, this.model));
        modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new ParametersMode (surface, this.model));

        modeManager.registerMode (Modes.MODE_FUNCTIONS, new OptionsMode (surface, this.model));

        this.buttonArea = new ButtonArea (surface, this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        final ControlView view = new ControlView (surface, this.model);
        view.registerTriggerCommand (Commands.COMMAND_SCENE1, new SceneCommand<> (0, this.model, surface));
        view.registerTriggerCommand (Commands.COMMAND_SCENE2, new SceneCommand<> (1, this.model, surface));
        surface.assignTriggerCommand (SLMkIIIControlSurface.MKIII_SCENE_1, 15, Commands.COMMAND_SCENE1);
        surface.assignTriggerCommand (SLMkIIIControlSurface.MKIII_SCENE_2, 15, Commands.COMMAND_SCENE2);

        viewManager.registerView (Views.VIEW_CONTROL, view);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();

        this.addTriggerCommand (Commands.COMMAND_REWIND, SLMkIIIControlSurface.MKIII_TRANSPORT_REWIND, 15, new WindCommand<> (this.model, surface, false));
        this.addTriggerCommand (Commands.COMMAND_FORWARD, SLMkIIIControlSurface.MKIII_TRANSPORT_FORWARD, 15, new WindCommand<> (this.model, surface, true));
        this.addTriggerCommand (Commands.COMMAND_LOOP, SLMkIIIControlSurface.MKIII_TRANSPORT_LOOP, 15, new ToggleLoopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_STOP, SLMkIIIControlSurface.MKIII_TRANSPORT_STOP, 15, new StopCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_PLAY, SLMkIIIControlSurface.MKIII_TRANSPORT_PLAY, 15, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_RECORD, SLMkIIIControlSurface.MKIII_TRANSPORT_RECORD, 15, new RecordCommand<> (this.model, surface));

        for (int i = 0; i < 8; i++)
        {
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW1_1.intValue () + i), SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, 15, new ButtonRowModeCommand<> (0, i, this.model, surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW2_1.intValue () + i), SLMkIIIControlSurface.MKIII_BUTTON_ROW1_1 + i, 15, new ButtonAreaCommand (this.buttonArea, 0, i, this.model, surface));
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW3_1.intValue () + i), SLMkIIIControlSurface.MKIII_BUTTON_ROW2_1 + i, 15, new ButtonAreaCommand (this.buttonArea, 1, i, this.model, surface));
        }

        final ModeSelectCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> deviceModeSelectCommand = new ModeSelectCommand<> (Modes.MODE_DEVICE_PARAMS, this.model, surface);
        this.addTriggerCommand (Commands.COMMAND_ARROW_UP, SLMkIIIControlSurface.MKIII_DISPLAY_UP, 15, event -> {
            if (event != ButtonEvent.DOWN)
                return;
            final ModeManager modeManager = this.getSurface ().getModeManager ();
            if (modeManager.isActiveMode (Modes.MODE_DEVICE_PARAMS))
                ((ParametersMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).toggleShowDevices ();
            else
                deviceModeSelectCommand.execute (event);
        });

        this.addTriggerCommand (Commands.COMMAND_ARROW_DOWN, SLMkIIIControlSurface.MKIII_DISPLAY_DOWN, 15, new ModeMultiSelectCommand<> (this.model, surface, Modes.MODE_TRACK, Modes.MODE_VOLUME, Modes.MODE_PAN, Modes.MODE_SEND1, Modes.MODE_SEND2, Modes.MODE_SEND3, Modes.MODE_SEND4, Modes.MODE_SEND5, Modes.MODE_SEND6, Modes.MODE_SEND7, Modes.MODE_SEND8));

        this.addTriggerCommand (Commands.COMMAND_SHIFT, SLMkIIIControlSurface.MKIII_SHIFT, 15, new ShiftCommand<> (this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_USER, SLMkIIIControlSurface.MKIII_OPTIONS, 15, new OptionsCommand (this.model, surface));

        this.addTriggerCommand (Commands.COMMAND_OCTAVE_UP, SLMkIIIControlSurface.MKIII_BUTTONS_UP, 15, event -> {
            if (event == ButtonEvent.UP)
                this.buttonArea.toggleMuteSolo ();
        });
        this.addTriggerCommand (Commands.COMMAND_OCTAVE_DOWN, SLMkIIIControlSurface.MKIII_BUTTONS_DOWN, 15, event -> {
            if (event == ButtonEvent.UP)
                this.buttonArea.toggleMuteSolo ();
        });

        this.addTriggerCommand (Commands.COMMAND_ARROW_LEFT, SLMkIIIControlSurface.MKIII_TRACK_LEFT, 15, new SLMkIIICursorCommand (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (Commands.COMMAND_ARROW_RIGHT, SLMkIIIControlSurface.MKIII_TRACK_RIGHT, 15, new SLMkIIICursorCommand (Direction.RIGHT, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
        {
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), SLMkIIIControlSurface.MKIII_KNOB_1 + i, 15, new KnobRowModeCommand<> (i, this.model, surface));
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_FADER1.intValue () + i), SLMkIIIControlSurface.MKIII_FADER_1 + i, 15, new VolumeFaderCommand (i, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActiveView (Views.VIEW_CONTROL);

        final ModeManager modeManager = surface.getModeManager ();
        modeManager.setActiveMode (Modes.MODE_TRACK);
    }


    /**
     * Update all buttons, except the ones controlled by the views. Refreshed on flush.
     */
    @SuppressWarnings("unchecked")
    private void updateButtons ()
    {
        final SLMkIIIControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();
        final boolean isShift = surface.isShiftPressed ();

        final View view = surface.getViewManager ().getView (Views.VIEW_CONTROL);
        surface.updateButton (SLMkIIIControlSurface.MKIII_TRANSPORT_REWIND, ((WindCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>) view.getTriggerCommand (Commands.COMMAND_REWIND)).isRewinding () ? SLMkIIIColors.SLMKIII_YELLOW : SLMkIIIColors.SLMKIII_YELLOW_HALF);
        surface.updateButton (SLMkIIIControlSurface.MKIII_TRANSPORT_FORWARD, ((WindCommand<SLMkIIIControlSurface, SLMkIIIConfiguration>) view.getTriggerCommand (Commands.COMMAND_FORWARD)).isForwarding () ? SLMkIIIColors.SLMKIII_YELLOW : SLMkIIIColors.SLMKIII_YELLOW_HALF);
        surface.updateButton (SLMkIIIControlSurface.MKIII_TRANSPORT_LOOP, t.isLoop () ? SLMkIIIColors.SLMKIII_BLUE : SLMkIIIColors.SLMKIII_BLUE_HALF);
        surface.updateButton (SLMkIIIControlSurface.MKIII_TRANSPORT_STOP, !t.isPlaying () ? SLMkIIIColors.SLMKIII_GREY : SLMkIIIColors.SLMKIII_DARK_GREY);
        surface.updateButton (SLMkIIIControlSurface.MKIII_TRANSPORT_PLAY, t.isPlaying () ? SLMkIIIColors.SLMKIII_GREEN : SLMkIIIColors.SLMKIII_GREEN_HALF);
        surface.updateButton (SLMkIIIControlSurface.MKIII_TRANSPORT_RECORD, isShift ? t.isLauncherOverdub () ? SLMkIIIColors.SLMKIII_AMBER : SLMkIIIColors.SLMKIII_AMBER_HALF : t.isRecording () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);

        final ModeManager modeManager = surface.getModeManager ();

        surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_UP, getDeviceModeColor (modeManager));
        surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_DOWN, getTrackModeColor (modeManager));

        surface.updateButton (SLMkIIIControlSurface.MKIII_BUTTONS_UP, this.buttonArea.isMuteSolo () ? SLMkIIIColors.SLMKIII_ORANGE : SLMkIIIColors.SLMKIII_ORANGE_HALF);
        surface.updateButton (SLMkIIIControlSurface.MKIII_BUTTONS_DOWN, !this.buttonArea.isMuteSolo () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);

        surface.updateButton (SLMkIIIControlSurface.MKIII_DUPLICATE, surface.isPressed (SLMkIIIControlSurface.MKIII_DUPLICATE) ? SLMkIIIColors.SLMKIII_AMBER : SLMkIIIColors.SLMKIII_AMBER_HALF);
        surface.updateButton (SLMkIIIControlSurface.MKIII_CLEAR, surface.isPressed (SLMkIIIControlSurface.MKIII_CLEAR) ? SLMkIIIColors.SLMKIII_AMBER : SLMkIIIColors.SLMKIII_AMBER_HALF);

        final SLMkIIIDisplay display = surface.getDisplay ();
        final ITrackBank tb = this.model.getTrackBank ();
        final double max = this.valueChanger.getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final double [] color = track.getColor ();
            display.setFaderLEDColor (SLMkIIIControlSurface.MKIII_FADER_LED_1 + i, track.getVolume () / max, color);
        }

        final ViewManager viewManager = surface.getViewManager ();
        final View activeView = viewManager.getActiveView ();
        if (activeView != null)
        {
            ((CursorCommand<?, ?>) activeView.getTriggerCommand (Commands.COMMAND_ARROW_LEFT)).updateArrows ();
            // TODO ((SceneView) activeView).updateSceneButtons ();
        }

        this.buttonArea.updateButtons ();
    }


    private static int getDeviceModeColor (final ModeManager modeManager)
    {
        if (modeManager.isActiveMode (Modes.MODE_DEVICE_PARAMS))
        {
            if (((ParametersMode) modeManager.getMode (Modes.MODE_DEVICE_PARAMS)).isShowDevices ())
                return SLMkIIIColors.SLMKIII_MINT;
            return SLMkIIIColors.SLMKIII_PURPLE;
        }
        return SLMkIIIColors.SLMKIII_WHITE_HALF;
    }


    private static int getTrackModeColor (final ModeManager modeManager)
    {
        if (modeManager.isActiveMode (Modes.MODE_TRACK))
            return SLMkIIIColors.SLMKIII_GREEN;
        if (modeManager.isActiveMode (Modes.MODE_VOLUME))
            return SLMkIIIColors.SLMKIII_BLUE;
        if (modeManager.isActiveMode (Modes.MODE_PAN))
            return SLMkIIIColors.SLMKIII_ORANGE;
        if (Modes.isSendMode (modeManager.getActiveModeId ()))
            return SLMkIIIColors.SLMKIII_YELLOW;

        return SLMkIIIColors.SLMKIII_WHITE_HALF;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        // TODO Auto-generated method stub

    }
}
