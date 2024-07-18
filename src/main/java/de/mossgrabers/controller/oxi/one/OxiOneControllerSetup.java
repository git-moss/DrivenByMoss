// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one;

import de.mossgrabers.controller.oxi.one.command.trigger.OxiOneBackCommand;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.controller.oxi.one.controller.OxiOneDisplay;
import de.mossgrabers.controller.oxi.one.controller.OxiOneScales;
import de.mossgrabers.controller.oxi.one.mode.OxiOneLayerMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneParameterMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneTrackMode;
import de.mossgrabers.controller.oxi.one.mode.OxiOneTransportMode;
import de.mossgrabers.controller.oxi.one.view.OxiOneMixView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.application.LoadCommand;
import de.mossgrabers.framework.command.trigger.application.SaveCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
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
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
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
        // TODO
        ms.setAdditionalDrumDevices (new int []
        {
            64,
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
        this.scales = new OxiOneScales (this.valueChanger);
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
        // modeManager.register (Modes.USER, new OxiOneUserMode (surface, this.model));
        // modeManager.register (Modes.BROWSER, new BrowserMode (surface, this.model));

        modeManager.register (Modes.TRANSPORT, new OxiOneTransportMode (surface, this.model));

        // Note mode needs the SHIFT button to exist
        this.addButton (ButtonID.SHIFT, "SHIFT", (event, velocity) -> {

            final IMode activeMode = modeManager.getActive ();
            if (activeMode instanceof final IParametersAdjustObserver observer)
                observer.parametersAdjusted ();

            if (event != ButtonEvent.LONG)
                this.getSurface ().updateFunctionButtonLEDs ();

        }, 1, OxiOneControlSurface.BUTTON_SHIFT, () -> viewManager.isActive (Views.SHIFT) || surface.isShiftPressed () ? 2 : 0);

        // modeManager.register (Modes.NOTE, new NoteMode (surface, this.model));

    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final OxiOneControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        // viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        // viewManager.register (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model,
        // true));
        //
        // viewManager.register (Views.PLAY, new PlayView (surface, this.model));
        // viewManager.register (Views.PIANO, new PianoView (surface, this.model));
        //
        // viewManager.register (Views.DRUM, new DrumXoXView (surface, this.model));
        // viewManager.register (Views.DRUM4, new Drum4View (surface, this.model));
        // viewManager.register (Views.DRUM64, new DrumView64 (surface, this.model));
        //
        // viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.MIX, new OxiOneMixView (surface, this.model));

        // viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final OxiOneControlSurface surface = this.getSurface ();

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

        this.addButton (ButtonID.RECORD, "REC", new RecordCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_REC, () -> {

            int state = 0;
            if (t.isLauncherOverdub ())
                state += 2;
            if (t.isRecording ())
                state += 1;
            return state;

        });

        // this.addButton (ButtonID.METRONOME, "PATTERN/\nSONG", new MetronomeCommand<> (this.model,
        // surface, false), OxiOneControlSurface.OXI_ONE_PATTERN, () -> {
        //
        // if (surface.isShiftPressed ())
        // return t.isMetronomeTicksOn ();
        // return t.isMetronomeOn ();
        //
        // }, OxiOneColorManager.BUTTON_STATE_ON2, OxiOneColorManager.BUTTON_STATE_HI2);

        this.addButton (ButtonID.LOAD, "LOAD", new LoadCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_LOAD);
        this.addButton (ButtonID.SAVE, "SAVE", new SaveCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_SAVE);

        // // Views + state toggles
        //
        // this.addButton (ButtonID.SEQUENCER, "STEP", new StepSequencerSelectCommand (this.model,
        // surface), OxiOneControlSurface.OXI_ONE_STEP, () -> {
        // if (viewManager.isActive (Views.SEQUENCER))
        // return 1;
        // if (viewManager.isActive (Views.POLY_SEQUENCER))
        // return 2;
        // return surface.isShiftPressed () && surface.getConfiguration ().isAccentActive () ? 1 :
        // 0;
        // }, OxiOneColorManager.BUTTON_STATE_ON2, OxiOneColorManager.BUTTON_STATE_HI2,
        // ColorManager.BUTTON_STATE_ON);
        //
        // this.addButton (ButtonID.NOTE, "NOTE", new PlaySelectCommand (this.model, surface),
        // OxiOneControlSurface.OXI_ONE_NOTE, () -> {
        // if (viewManager.isActive (Views.PLAY))
        // return 1;
        // if (viewManager.isActive (Views.PIANO))
        // return 2;
        // return 0;
        // }, OxiOneColorManager.BUTTON_STATE_ON2, OxiOneColorManager.BUTTON_STATE_HI2,
        // ColorManager.BUTTON_STATE_ON);
        //
        // this.addButton (ButtonID.DRUM, "DRUM", new DrumSequencerSelectCommand (this.model,
        // surface), OxiOneControlSurface.OXI_ONE_DRUM, () -> {
        // if (viewManager.isActive (Views.DRUM))
        // return 1;
        // if (viewManager.isActive (Views.DRUM4))
        // return 2;
        // if (viewManager.isActive (Views.DRUM64))
        // return 3;
        // return 0;
        // }, OxiOneColorManager.BUTTON_STATE_ON2, OxiOneColorManager.BUTTON_STATE_HI2,
        // ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
        //
        // this.addButton (ButtonID.SESSION, "PERFORM", new SessionSelectCommand (this.model,
        // surface), OxiOneControlSurface.OXI_ONE_PERFORM, () -> {
        // if (viewManager.isActive (Views.SESSION))
        // return 1;
        // if (viewManager.isActive (Views.MIX))
        // return 2;
        // return 0;
        // }, OxiOneColorManager.BUTTON_STATE_ON2, OxiOneColorManager.BUTTON_STATE_HI2,
        // ColorManager.BUTTON_STATE_ON);
        //

        // TODO new ToggleShiftViewCommand<> (this.model, surface)
        // this.addButton (ButtonID.SHIFT, "SHIFT", NopCommand.INSTANCE, 1,
        // OxiOneControlSurface.BUTTON_SHIFT, () -> viewManager.isActive (Views.SHIFT) ||
        // surface.isShiftPressed () ? 1 : 0, ColorManager.BUTTON_STATE_ON,
        // ColorManager.BUTTON_STATE_HI);
        // this.addButton (ButtonID.SELECT, "SELECT", new OxiOneSelectButtonCommand (this.model,
        // surface), OxiOneControlSurface.SELECT);
        // this.addButton (ButtonID.BROWSE, "BROWSER", new OxiOneBrowserCommand (this.model,
        // surface),
        // OxiOneControlSurface.OXI_ONE_BROWSER, () -> modeManager.isActive (Modes.BROWSER));
        //
        // // Navigation
        //
        // this.addButton (ButtonID.ARROW_LEFT, "GRID\nLEFT", new OxiOneViewButtonCommand
        // (ButtonID.ARROW_LEFT, this.model, surface), OxiOneControlSurface.OXI_ONE_GRID_LEFT);
        // this.addButton (ButtonID.ARROW_RIGHT, "GRID\nRIGHT", new OxiOneViewButtonCommand
        // (ButtonID.ARROW_RIGHT, this.model, surface), OxiOneControlSurface.OXI_ONE_GRID_RIGHT);

        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), 1, OxiOneControlSurface.BUTTON_UNDO, () -> {

            int state = 0;
            final IApplication application = this.model.getApplication ();
            if (application.canUndo ())
                state += 1;
            if (application.canRedo ())
                state += 2;
            return state;

        });

        // for (int i = 0; i < 4; i++)
        // {
        // final ButtonID buttonID = ButtonID.get (ButtonID.SCENE1, i);
        // this.addButton (buttonID, "SOLO" + (i + 1), new ViewButtonCommand<> (buttonID, surface),
        // OxiOneControlSurface.OXI_ONE_SOLO_1 + i, () -> {
        // final IView activeView = viewManager.getActive ();
        // return activeView != null ? activeView.getButtonColor (buttonID) : 0;
        // });
        //
        // final int index = i;
        // surface.createLight (OutputID.get (OutputID.LED5, i), () -> {
        // final IView activeView = viewManager.getActive ();
        // return activeView instanceof final IOxiOneView fireView ? fireView.getSoloButtonColor
        // (index) : 0;
        // }, color -> surface.setTrigger (0, 0x28 + index, color), state -> {
        // switch (state)
        // {
        // case 0:
        // return ColorEx.GRAY;
        // case 1:
        // return ColorEx.DARK_RED;
        // case 2:
        // return ColorEx.DARK_GREEN;
        // case 3:
        // return ColorEx.RED;
        // case 4:
        // return ColorEx.GREEN;
        // default:
        // return ColorEx.GRAY;
        // }
        // }, null);
        // }

        this.addButton (ButtonID.KNOB1_TOUCH, "KNOB1_PRESS", (event, velocity) -> emulateTouch (event, 0), 1, OxiOneControlSurface.BUTTON_ENCODER1);
        this.addButton (ButtonID.KNOB2_TOUCH, "KNOB2_PRESS", (event, velocity) -> emulateTouch (event, 1), 1, OxiOneControlSurface.BUTTON_ENCODER2);
        this.addButton (ButtonID.KNOB3_TOUCH, "KNOB3_PRESS", (event, velocity) -> emulateTouch (event, 2), 1, OxiOneControlSurface.BUTTON_ENCODER3);
        this.addButton (ButtonID.KNOB4_TOUCH, "KNOB4_PRESS", (event, velocity) -> emulateTouch (event, 3), 1, OxiOneControlSurface.BUTTON_ENCODER4);

        this.backCommand = new OxiOneBackCommand (this.model, surface);
        this.addButton (ButtonID.CONTROL, "BACK", this.backCommand, 1, OxiOneControlSurface.BUTTON_BACK);
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
            knob.addHasChangedObserver ( (Void) -> updateSelectedParameter (index));
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
            mode.getParameterProvider ().get (knobIndex).resetValue ();
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
        if (mode != null)
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

        // surface.getButton (ButtonID.PAD1).setBounds (27.0, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD2).setBounds (44.0, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD3).setBounds (61.25, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD4).setBounds (78.25, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD5).setBounds (95.25, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD6).setBounds (112.5, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD7).setBounds (129.75, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD8).setBounds (146.5, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD9).setBounds (163.75, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD10).setBounds (180.75, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD11).setBounds (198.0, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD12).setBounds (214.5, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD13).setBounds (232.0, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD14).setBounds (249.25, 107.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD15).setBounds (266.25, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD16).setBounds (283.0, 107.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD17).setBounds (27.0, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD18).setBounds (44.0, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD19).setBounds (61.25, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD20).setBounds (78.25, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD21).setBounds (95.25, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD22).setBounds (112.5, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD23).setBounds (129.75, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD24).setBounds (146.5, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD25).setBounds (163.75, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD26).setBounds (180.75, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD27).setBounds (198.0, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD28).setBounds (214.5, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD29).setBounds (232.0, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD30).setBounds (249.25, 85.0, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD31).setBounds (266.25, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD32).setBounds (283.0, 85.25, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD33).setBounds (27.0, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD34).setBounds (44.0, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD35).setBounds (61.25, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD36).setBounds (78.25, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD37).setBounds (95.25, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD38).setBounds (112.5, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD39).setBounds (129.75, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD40).setBounds (146.5, 63.75, 12.0, 18.25);
        // surface.getButton (ButtonID.PAD41).setBounds (163.75, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD42).setBounds (180.75, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD43).setBounds (198.0, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD44).setBounds (214.5, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD45).setBounds (232.0, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD46).setBounds (249.25, 63.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD47).setBounds (266.25, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD48).setBounds (283.0, 63.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD49).setBounds (27.0, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD50).setBounds (44.0, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD51).setBounds (61.25, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD52).setBounds (78.25, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD53).setBounds (95.25, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD54).setBounds (112.5, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD55).setBounds (129.75, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD56).setBounds (146.5, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD57).setBounds (163.75, 42.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD58).setBounds (180.75, 42.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD59).setBounds (198.0, 42.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD60).setBounds (214.5, 42.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD61).setBounds (232.0, 42.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD62).setBounds (249.25, 42.5, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD63).setBounds (266.25, 42.75, 12.75, 18.25);
        // surface.getButton (ButtonID.PAD64).setBounds (283.0, 42.75, 12.75, 18.25);
        //
        // surface.getButton (ButtonID.BANK_RIGHT).setBounds (4.75, 29.0, 10.0, 10.0);
        // surface.getButton (ButtonID.PLAY).setBounds (236.5, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.STOP).setBounds (258.5, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.RECORD).setBounds (280.75, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.METRONOME).setBounds (214.25, 134.0, 17.25, 11.75);
        //
        // surface.getButton (ButtonID.SELECT).setBounds (238.25, 2.5, 18.25, 10.0);
        //
        // surface.getButton (ButtonID.SEQUENCER).setBounds (4.5, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.NOTE).setBounds (27.0, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.DRUM).setBounds (49.25, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.SESSION).setBounds (71.75, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.SHIFT).setBounds (94.25, 134.0, 17.25, 11.75);
        // surface.getButton (ButtonID.ALT).setBounds (116.5, 134.0, 17.25, 11.75);
        //
        // surface.getButton (ButtonID.ARROW_LEFT).setBounds (261.0, 18.0, 18.0, 10.0);
        // surface.getButton (ButtonID.ARROW_RIGHT).setBounds (281.75, 18.0, 18.0, 10.0);
        // surface.getButton (ButtonID.ARROW_UP).setBounds (138.25, 12.25, 18.5, 9.5);
        // surface.getButton (ButtonID.ARROW_DOWN).setBounds (138.25, 23.75, 18.5, 9.5);
        //
        // surface.getButton (ButtonID.SCENE1).setBounds (4.75, 42.75, 10.0, 17.25);
        // surface.getButton (ButtonID.SCENE2).setBounds (4.75, 63.75, 10.0, 17.25);
        // surface.getButton (ButtonID.SCENE3).setBounds (4.75, 86.0, 10.0, 17.25);
        // surface.getButton (ButtonID.SCENE4).setBounds (4.75, 107.75, 10.0, 17.25);
        //
        // surface.getButton (ButtonID.BROWSE).setBounds (214.25, 17.0, 17.25, 11.75);
        //
        // surface.getContinuous (ContinuousID.KNOB1).setBounds (27.5, 12.75, 22.0, 19.25);
        // surface.getContinuous (ContinuousID.KNOB2).setBounds (55.0, 12.75, 22.0, 19.25);
        // surface.getContinuous (ContinuousID.KNOB3).setBounds (82.75, 12.75, 22.0, 19.25);
        // surface.getContinuous (ContinuousID.KNOB4).setBounds (110.25, 12.75, 22.0, 19.25);
        // surface.getContinuous (ContinuousID.VIEW_SELECTION).setBounds (238.25, 13.5, 17.75,
        // 17.25);
        //
        // surface.getLight (OutputID.LED1).setBounds (4.75, 4.0, 5.5, 4.25);
        // surface.getLight (OutputID.LED2).setBounds (4.75, 10.0, 5.5, 4.25);
        // surface.getLight (OutputID.LED3).setBounds (4.75, 16.25, 5.5, 4.25);
        // surface.getLight (OutputID.LED4).setBounds (4.75, 22.25, 5.5, 4.25);
        // surface.getLight (OutputID.LED5).setBounds (18.0, 42.75, 5.0, 17.25);
        // surface.getLight (OutputID.LED6).setBounds (18.0, 63.75, 5.0, 17.25);
        // surface.getLight (OutputID.LED7).setBounds (18.0, 86.0, 5.0, 17.25);
        // surface.getLight (OutputID.LED8).setBounds (18.0, 107.75, 5.0, 17.25);
        //
        // surface.getGraphicsDisplay ().getHardwareDisplay ().setBounds (165.25, 11.75, 40.75,
        // 20.75);
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

        // this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final OxiOneControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.MIX);
        surface.getModeManager ().setActive (Modes.TRACK);
        // this.modeSelectCommand.activateMode (Modes.TRACK);

        surface.enterRemoteMode ();
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
