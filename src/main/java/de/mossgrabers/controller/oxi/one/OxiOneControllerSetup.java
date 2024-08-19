// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one;

import de.mossgrabers.controller.oxi.one.command.trigger.OxiOneBackCommand;
import de.mossgrabers.controller.oxi.one.command.trigger.OxiOneCursorCommand;
import de.mossgrabers.controller.oxi.one.command.trigger.OxiOneToggleNoteEditCommand;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.controller.oxi.one.controller.OxiOneDisplay;
import de.mossgrabers.controller.oxi.one.mode.IOxiModeReset;
import de.mossgrabers.controller.oxi.one.mode.OxiOneLayerMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneNoteEditMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneParameterMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOnePlayModeConfigurationMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneRepeatModeConfigurationMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneSeqConfigMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneTrackMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneTransportMode;
import de.mossgrabers.controller.oxi.one.view.OxiOneDrum8View;
import de.mossgrabers.controller.oxi.one.view.OxiOneDrumView128;
import de.mossgrabers.controller.oxi.one.view.OxiOneMixView;
import de.mossgrabers.controller.oxi.one.view.OxiOnePlayView;
import de.mossgrabers.controller.oxi.one.view.OxiOnePolySequencerView;
import de.mossgrabers.controller.oxi.one.view.OxiOneSequencerView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.application.DeleteCommand;
import de.mossgrabers.framework.command.trigger.application.DuplicateCommand;
import de.mossgrabers.framework.command.trigger.application.LoadCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the OXI One controller.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneControllerSetup extends AbstractControllerSetup<OxiOneControlSurface, OxiOneConfiguration>
{
    private OxiOneBackCommand backCommand = null;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public OxiOneControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new OxiOneColorManager ();
        this.valueChanger = new TwosComplementValueChanger (1024, 10);
        this.configuration = new OxiOneConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableDevice (DeviceID.FIRST_INSTRUMENT);
        ms.setNumTracks (16);
        ms.setNumScenes (4);
        ms.setNumSends (6);
        ms.setHasFullFlatTrackList (true);
        ms.setAdditionalDrumDevices (new int []
        {
            128,
            16
        });

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 128, 16, 8, 2);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */);
        final OxiOneControlSurface surface = new OxiOneControlSurface (this.host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.addGraphicsDisplay (new OxiOneDisplay (this.host, output, this.valueChanger.getUpperBound ()));
        surface.getModeManager ().setDefaultID (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final OxiOneControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = surface.getViewManager ();

        modeManager.register (Modes.TRACK, new OxiOneTrackMode (surface, this.model));
        modeManager.register (Modes.DEVICE_LAYER, new OxiOneLayerMode (surface, this.model));
        modeManager.register (Modes.DEVICE_PARAMS, new OxiOneParameterMode (surface, this.model));

        modeManager.register (Modes.TRANSPORT, new OxiOneTransportMode (surface, this.model));

        // Note mode needs the SHIFT button to exist
        this.addButton (ButtonID.SHIFT, "SHIFT", (event, velocity) -> {

            final IMode activeMode = modeManager.getActive ();
            if (activeMode instanceof final IParametersAdjustObserver observer)
                observer.parametersAdjusted ();

            if (event != ButtonEvent.LONG)
                this.getSurface ().updateFunctionButtonLEDs ();

        }, 1, OxiOneControlSurface.BUTTON_SHIFT, () -> viewManager.isActive (Views.SHIFT) || surface.isShiftPressed () ? 2 : 0);

        modeManager.register (Modes.SCALES, new OxiOnePlayModeConfigurationMode (surface, this.model));
        modeManager.register (Modes.REPEAT_NOTE, new OxiOneRepeatModeConfigurationMode (surface, this.model));
        modeManager.register (Modes.NOTE, new OxiOneNoteEditMode (surface, this.model));
        modeManager.register (Modes.SETUP, new OxiOneSeqConfigMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final OxiOneControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.register (Views.MIX, new OxiOneMixView (surface, this.model));
        viewManager.register (Views.PLAY, new OxiOnePlayView (surface, this.model));
        viewManager.register (Views.DRUM64, new OxiOneDrumView128 (surface, this.model));
        viewManager.register (Views.DRUM8, new OxiOneDrum8View (surface, this.model));
        viewManager.register (Views.SEQUENCER, new OxiOneSequencerView (surface, this.model));
        viewManager.register (Views.POLY_SEQUENCER, new OxiOnePolySequencerView (surface, this.model, true));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final OxiOneControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();

        // Transport

        final ITransport t = this.model.getTransport ();
        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface, ButtonID.SHIFT), 1, OxiOneControlSurface.BUTTON_PLAY, () -> t.isPlaying () ? 1 : 0);

        this.addButton (ButtonID.STOP, "STOP", new StopCommand<> (this.model, surface)
        {
            @Override
            public void executeShifted (final ButtonEvent event)
            {
                if (event == ButtonEvent.UP)
                    this.surface.getModeManager ().setTemporary (Modes.TRANSPORT);
            }
        }, 1, OxiOneControlSurface.BUTTON_STOP, () -> !t.isPlaying () ? 1 : 0);

        this.addButton (ButtonID.RECORD, "REC", new ConfiguredRecordCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_REC, () -> {

            int state = 0;
            if (t.isLauncherOverdub ())
                state += 2;
            if (t.isRecording ())
                state += 1;
            return state;

        });

        // Global buttons

        this.addButton (ButtonID.LOAD, "LOAD", new LoadCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_LOAD, () -> 1);
        this.addButton (ButtonID.SAVE, "SAVE", new SaveCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_SAVE, () -> this.model.getProject ().isDirty () ? 1 : 0);

        this.addButton (ButtonID.DUPLICATE, "Duplicate", new DuplicateCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_COPY);
        this.addButton (ButtonID.DELETE, "Delete", new DeleteCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_PASTE);

        this.addButton (ButtonID.SESSION, "MIXER", new ViewMultiSelectCommand<> (this.model, surface, Views.MIX), 1, OxiOneControlSurface.BUTTON_ARRANGER, () -> viewManager.isActive (Views.MIX) ? 1 : 0);

        this.addButton (ButtonID.KEYBOARD, "KEYBOARD", new ViewMultiSelectCommand<> (this.model, surface, true, Views.PLAY, Views.DRUM64)
        {

            /** {@inheritDoc} */
            @Override
            public void executeShifted (final ButtonEvent event)
            {
                if (event == ButtonEvent.DOWN)
                    modeManager.setTemporary (Modes.SCALES);
            }

        }, 1, OxiOneControlSurface.BUTTON_KEYBOARD, () -> {

            if (surface.isShiftPressed ())
                return modeManager.isActive (Modes.SCALES) ? 2 : 0;
            return viewManager.isActive (Views.PLAY, Views.DRUM64) ? 1 : 0;

        });

        this.addButton (ButtonID.SEQUENCER, "SEQUENCE", new ViewMultiSelectCommand<> (this.model, surface, true, Views.DRUM8, Views.SEQUENCER, Views.POLY_SEQUENCER)
        {

            /** {@inheritDoc} */
            @Override
            public void executeShifted (final ButtonEvent event)
            {
                if (event == ButtonEvent.DOWN)
                    modeManager.setTemporary (Modes.REPEAT_NOTE);
            }

        }, 1, OxiOneControlSurface.BUTTON_ARP, () -> {

            if (surface.isShiftPressed ())
                return modeManager.isActive (Modes.REPEAT_NOTE) ? 2 : 0;
            return viewManager.isActive (Views.DRUM8, Views.SEQUENCER, Views.POLY_SEQUENCER) ? 1 : 0;

        });

        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_UNDO, () -> {

            int state = 0;
            final IApplication application = this.model.getApplication ();
            if (application.canUndo ())
                state += 1;
            if (application.canRedo ())
                state += 2;
            return state;

        });

        this.addButton (ButtonID.NOTE, "MOD", new OxiOneToggleNoteEditCommand (this.model, surface), 1, OxiOneControlSurface.BUTTON_MOD, () -> modeManager.isActive (Modes.NOTE));
        this.addButton (ButtonID.REPEAT, "LFO", NopCommand.INSTANCE, 1, OxiOneControlSurface.BUTTON_LFO);
        this.addButton (ButtonID.ACCENT, "Step Chord", NopCommand.INSTANCE, 1, OxiOneControlSurface.BUTTON_STEP_CHORD);
        this.addButton (ButtonID.SETUP, "Y Div", new ModeSelectCommand<> (this.model, surface, Modes.SETUP), 1, OxiOneControlSurface.BUTTON_DIVISION, () -> modeManager.isActive (Modes.SETUP));
        this.addButton (ButtonID.PUNCH_IN, "Init", NopCommand.INSTANCE, 1, OxiOneControlSurface.BUTTON_INIT);
        this.addButton (ButtonID.PUNCH_OUT, "End", NopCommand.INSTANCE, 1, OxiOneControlSurface.BUTTON_END);
        this.addButton (ButtonID.QUANTIZE, "Random", (event, velocity) -> {

            if (event == ButtonEvent.UP)
            {
                if (surface.isShiftPressed ())
                    this.model.getCursorDevice ().toggleWindowOpen ();
                else
                    this.model.getCursorClip ().quantize (this.configuration.getQuantizeAmount () / 100.0);
            }

        }, 1, OxiOneControlSurface.BUTTON_RANDOM);

        this.addButton (ButtonID.SCENE1, "1", new ViewButtonCommand<> (ButtonID.SCENE1, surface), 1, OxiOneControlSurface.BUTTON_SEQUENCER1, () -> this.getSceneButtonColor (0));
        this.addButton (ButtonID.SCENE2, "2", new ViewButtonCommand<> (ButtonID.SCENE2, surface), 1, OxiOneControlSurface.BUTTON_SEQUENCER2, () -> this.getSceneButtonColor (1));
        this.addButton (ButtonID.SCENE3, "3", new ViewButtonCommand<> (ButtonID.SCENE3, surface), 1, OxiOneControlSurface.BUTTON_SEQUENCER3, () -> this.getSceneButtonColor (2));
        this.addButton (ButtonID.SCENE4, "4", new ViewButtonCommand<> (ButtonID.SCENE4, surface), 1, OxiOneControlSurface.BUTTON_SEQUENCER4, () -> this.getSceneButtonColor (3));

        this.addButton (ButtonID.MUTE, "MUTE", (event, velocity) -> {

            if (event != ButtonEvent.DOWN)
                return;
            if (surface.isShiftPressed ())
                this.model.getProject ().clearSolo ();
            else
                this.model.getProject ().clearMute ();

        }, 1, OxiOneControlSurface.BUTTON_MUTE, () -> {

            if (surface.isShiftPressed ())
                return this.model.getProject ().hasSolo () ? 2 : 0;
            return this.model.getProject ().hasMute () ? 1 : 0;

        });

        final OxiOneCursorCommand leftCommand = new OxiOneCursorCommand (Direction.LEFT, this.model, surface);
        final OxiOneCursorCommand rightCommand = new OxiOneCursorCommand (Direction.RIGHT, this.model, surface);
        final OxiOneCursorCommand upCommand = new OxiOneCursorCommand (Direction.UP, this.model, surface);
        final OxiOneCursorCommand downCommand = new OxiOneCursorCommand (Direction.DOWN, this.model, surface);
        this.addButton (ButtonID.ARROW_UP, "Up", upCommand, 1, OxiOneControlSurface.BUTTON_32_UP, () -> this.getCursorLEDState (upCommand));
        this.addButton (ButtonID.ARROW_DOWN, "Down", downCommand, 1, OxiOneControlSurface.BUTTON_48_DOWN, () -> this.getCursorLEDState (downCommand));
        this.addButton (ButtonID.ARROW_LEFT, "Left", leftCommand, 1, OxiOneControlSurface.BUTTON_16_LEFT, () -> this.getCursorLEDState (leftCommand));
        this.addButton (ButtonID.ARROW_RIGHT, "Right", rightCommand, 1, OxiOneControlSurface.BUTTON_64_RIGHT, () -> this.getCursorLEDState (rightCommand));

        this.addButton (ButtonID.KNOB1_TOUCH, "KNOB1_PRESS", (event, velocity) -> this.emulateTouch (event, 0), 1, OxiOneControlSurface.BUTTON_ENCODER1);
        this.addButton (ButtonID.KNOB2_TOUCH, "KNOB2_PRESS", (event, velocity) -> this.emulateTouch (event, 1), 1, OxiOneControlSurface.BUTTON_ENCODER2);
        this.addButton (ButtonID.KNOB3_TOUCH, "KNOB3_PRESS", (event, velocity) -> this.emulateTouch (event, 2), 1, OxiOneControlSurface.BUTTON_ENCODER3);
        this.addButton (ButtonID.KNOB4_TOUCH, "KNOB4_PRESS", (event, velocity) -> this.emulateTouch (event, 3), 1, OxiOneControlSurface.BUTTON_ENCODER4);

        this.backCommand = new OxiOneBackCommand (this.model, surface);
        this.addButton (ButtonID.CONTROL, "BACK", this.backCommand, 1, OxiOneControlSurface.BUTTON_BACK);
    }


    private int getSceneButtonColor (final int i)
    {
        final ISceneBank sceneBank = this.model.getSceneBank ();
        final IScene scene = sceneBank.getItem (i);
        if (!scene.doesExist ())
            return 0;
        return scene.isSelected () ? 3 : 1;
    }


    private int getCursorLEDState (final CursorCommand<OxiOneControlSurface, OxiOneConfiguration> command)
    {
        if (this.getSurface ().isShiftPressed ())
            return command.canScroll () ? 2 : 0;
        return command.canScroll () ? 1 : 0;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final OxiOneControlSurface surface = this.getSurface ();

        for (int i = 0; i < 4; i++)
        {
            final int index = i;
            final IHwRelativeKnob knob = this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + i, new KnobRowModeCommand<> (i, this.model, surface), OxiOneControlSurface.KNOB1_VELOCITY + i);
            knob.setIndexInGroup (i);
            knob.addHasChangedObserver ( (Void) -> this.updateSelectedParameter (index));
        }
    }


    /**
     * Handle pressing the knob buttons. Select the parameter that the knob edits if pressed. If
     * long pressed the parameter is reset.
     *
     * @param event The button event
     * @param knobIndex The index of the knob
     */
    private void emulateTouch (final ButtonEvent event, final int knobIndex)
    {
        final IMode mode = this.getSurface ().getModeManager ().getActive ();
        if (mode == null)
            return;

        if (event == ButtonEvent.LONG)
        {
            if (mode instanceof final IOxiModeReset resetMode)
                resetMode.resetValue (knobIndex);
            else
                mode.getParameterProvider ().get (knobIndex).resetValue ();
        }
        else
            mode.onKnobTouch (knobIndex, event == ButtonEvent.DOWN);
    }


    /**
     * Callback from turning the knobs. Select the parameter that the knob edits by emulating a knob
     * touch event.
     *
     * @param knobIndex The index of the knob
     */
    private void updateSelectedParameter (final int knobIndex)
    {
        final OxiOneControlSurface surface = this.getSurface ();
        final IMode mode = surface.getModeManager ().getActive ();
        if (mode != null && !(mode instanceof OxiOneTransportMode && surface.isShiftPressed ()))
        {
            mode.onKnobTouch (knobIndex, true);
            mode.onKnobTouch (knobIndex, false);

            if (this.backCommand != null && surface.isPressed (ButtonID.CONTROL))
                this.backCommand.setHasKnobBeenUsed (true);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final OxiOneControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (89.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD2).setBounds (101.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD3).setBounds (113.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD4).setBounds (125.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD5).setBounds (137.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD6).setBounds (149.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD7).setBounds (161.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD8).setBounds (173.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD9).setBounds (185.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD10).setBounds (197.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD11).setBounds (209.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD12).setBounds (221.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD13).setBounds (233.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD14).setBounds (245.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD15).setBounds (257.75, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD16).setBounds (270.0, 120.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD17).setBounds (89.75, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD18).setBounds (102.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD19).setBounds (114.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD20).setBounds (126.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD21).setBounds (138.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD22).setBounds (150.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD23).setBounds (162.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD24).setBounds (174.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD25).setBounds (186.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD26).setBounds (198.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD27).setBounds (210.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD28).setBounds (222.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD29).setBounds (234.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD30).setBounds (246.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD31).setBounds (258.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD32).setBounds (270.0, 107.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD33).setBounds (89.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD34).setBounds (102.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD35).setBounds (114.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD36).setBounds (126.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD37).setBounds (138.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD38).setBounds (150.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD39).setBounds (162.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD40).setBounds (174.0, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD41).setBounds (185.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD42).setBounds (197.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD43).setBounds (209.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD44).setBounds (221.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD45).setBounds (233.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD46).setBounds (245.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD47).setBounds (257.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD48).setBounds (269.75, 95.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD49).setBounds (89.75, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD50).setBounds (102.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD51).setBounds (114.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD52).setBounds (126.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD53).setBounds (138.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD54).setBounds (150.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD55).setBounds (162.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD56).setBounds (174.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD57).setBounds (186.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD58).setBounds (198.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD59).setBounds (210.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD60).setBounds (222.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD61).setBounds (234.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD62).setBounds (246.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD63).setBounds (258.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD64).setBounds (270.0, 84.0, 10.0, 10.0);
        surface.getButton (ButtonID.PAD65).setBounds (89.75, 72.5, 10.0, 10.0);
        surface.getButton (ButtonID.PAD66).setBounds (101.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD67).setBounds (113.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD68).setBounds (125.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD69).setBounds (137.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD70).setBounds (149.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD71).setBounds (161.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD72).setBounds (173.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD73).setBounds (185.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD74).setBounds (197.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD75).setBounds (209.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD76).setBounds (221.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD77).setBounds (233.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD78).setBounds (245.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD79).setBounds (257.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD80).setBounds (269.75, 72.25, 10.0, 10.0);
        surface.getButton (ButtonID.PAD81).setBounds (89.75, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD82).setBounds (102.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD83).setBounds (114.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD84).setBounds (126.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD85).setBounds (138.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD86).setBounds (150.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD87).setBounds (162.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.PAD88).setBounds (174.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS1).setBounds (186.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS2).setBounds (198.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS3).setBounds (210.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS4).setBounds (222.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS5).setBounds (234.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS6).setBounds (246.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS7).setBounds (258.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS8).setBounds (270.0, 60.75, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS9).setBounds (89.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS10).setBounds (101.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS11).setBounds (113.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS12).setBounds (125.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS13).setBounds (137.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS14).setBounds (149.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS15).setBounds (161.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS16).setBounds (173.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS17).setBounds (185.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS18).setBounds (197.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS19).setBounds (209.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS20).setBounds (221.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS21).setBounds (233.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS22).setBounds (245.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS23).setBounds (257.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS24).setBounds (269.75, 49.0, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS25).setBounds (89.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS26).setBounds (102.0, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS27).setBounds (114.0, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS28).setBounds (125.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS29).setBounds (137.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS30).setBounds (149.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS31).setBounds (161.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS32).setBounds (173.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS33).setBounds (185.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS34).setBounds (197.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS35).setBounds (209.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS36).setBounds (221.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS37).setBounds (233.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS38).setBounds (245.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS39).setBounds (257.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.MORE_PADS40).setBounds (269.75, 37.5, 10.0, 10.0);
        surface.getButton (ButtonID.SHIFT).setBounds (6.5, 121.75, 10.0, 10.0);
        surface.getButton (ButtonID.PLAY).setBounds (55.25, 121.75, 10.0, 10.0);
        surface.getButton (ButtonID.STOP).setBounds (25.0, 121.75, 10.0, 10.0);
        surface.getButton (ButtonID.RECORD).setBounds (39.75, 121.75, 10.0, 10.0);
        surface.getButton (ButtonID.LOAD).setBounds (22.25, 53.75, 10.0, 10.0);
        surface.getButton (ButtonID.SAVE).setBounds (22.25, 65.25, 10.0, 10.0);
        surface.getButton (ButtonID.DUPLICATE).setBounds (36.25, 53.75, 10.0, 10.0);
        surface.getButton (ButtonID.DELETE).setBounds (36.25, 65.25, 10.0, 10.0);
        surface.getButton (ButtonID.SESSION).setBounds (6.5, 72.75, 10.0, 10.0);
        surface.getButton (ButtonID.KEYBOARD).setBounds (6.5, 89.0, 10.0, 10.0);
        surface.getButton (ButtonID.SEQUENCER).setBounds (6.5, 105.0, 10.0, 10.0);
        surface.getButton (ButtonID.UNDO).setBounds (49.75, 53.75, 10.0, 10.0);
        surface.getButton (ButtonID.NOTE).setBounds (36.25, 78.25, 10.0, 10.0);
        surface.getButton (ButtonID.REPEAT).setBounds (49.75, 78.25, 10.0, 10.0);
        surface.getButton (ButtonID.ACCENT).setBounds (49.75, 90.0, 10.0, 10.0);
        surface.getButton (ButtonID.SETUP).setBounds (36.25, 90.0, 10.0, 10.0);
        surface.getButton (ButtonID.PUNCH_IN).setBounds (22.25, 78.25, 10.0, 10.0);
        surface.getButton (ButtonID.PUNCH_OUT).setBounds (22.25, 90.0, 10.0, 10.0);
        surface.getButton (ButtonID.QUANTIZE).setBounds (49.75, 65.25, 10.0, 10.0);
        surface.getButton (ButtonID.SCENE1).setBounds (70.75, 62.75, 10.0, 10.0);
        surface.getButton (ButtonID.SCENE2).setBounds (70.75, 77.0, 10.0, 10.0);
        surface.getButton (ButtonID.SCENE3).setBounds (70.75, 90.75, 10.0, 10.0);
        surface.getButton (ButtonID.SCENE4).setBounds (70.75, 104.75, 10.0, 10.0);
        surface.getButton (ButtonID.MUTE).setBounds (70.5, 121.75, 10.0, 10.0);
        surface.getButton (ButtonID.ARROW_UP).setBounds (32.5, 104.75, 10.0, 10.0);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (44.5, 104.75, 10.0, 10.0);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (20.5, 104.75, 10.0, 10.0);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (57.25, 104.75, 10.0, 10.0);
        surface.getButton (ButtonID.KNOB1_TOUCH).setBounds (8.0, 39.5, 10.0, 10.0);
        surface.getButton (ButtonID.KNOB2_TOUCH).setBounds (20.0, 39.5, 10.0, 10.0);
        surface.getButton (ButtonID.KNOB3_TOUCH).setBounds (32.0, 39.5, 10.0, 10.0);
        surface.getButton (ButtonID.KNOB4_TOUCH).setBounds (44.0, 39.5, 10.0, 10.0);
        surface.getButton (ButtonID.CONTROL).setBounds (6.5, 57.25, 10.0, 10.0);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (8.0, 28.0, 10.0, 10.0);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (20.0, 28.0, 10.0, 10.0);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (32.0, 28.0, 10.0, 10.0);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (44.0, 28.0, 10.0, 10.0);

        surface.getGraphicsDisplay ().getHardwareDisplay ().setBounds (8.0, 2.0, 44, 22);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final OxiOneControlSurface surface = this.getSurface ();

        surface.getViewManager ().addChangeListener ( (previousViewId, activeViewId) -> this.onViewChange ());

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final OxiOneControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActive (Modes.TRACK);
        surface.getViewManager ().setActive (this.configuration.getStartupView ());

        surface.scheduleTask (surface::enterRemoteMode, 3000);
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
    }


    /**
     * Called when a new view is selected.
     */
    private void onViewChange ()
    {
        this.getSurface ().getDisplay ().cancelNotification ();
    }
}
