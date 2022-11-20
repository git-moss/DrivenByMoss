// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire;

import de.mossgrabers.controller.akai.fire.command.continuous.SelectKnobCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.DrumSequencerSelectCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.FireBrowserCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.FireModeCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.FireRecordCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.FireSelectButtonCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.FireStopCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.FireViewButtonCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.PlaySelectCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.SessionSelectCommand;
import de.mossgrabers.controller.akai.fire.command.trigger.StepSequencerSelectCommand;
import de.mossgrabers.controller.akai.fire.controller.FireColorManager;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.controller.FireDisplay;
import de.mossgrabers.controller.akai.fire.controller.FireScales;
import de.mossgrabers.controller.akai.fire.mode.BrowserMode;
import de.mossgrabers.controller.akai.fire.mode.FireLayerMixerMode;
import de.mossgrabers.controller.akai.fire.mode.FireLayerMode;
import de.mossgrabers.controller.akai.fire.mode.FireParameterMode;
import de.mossgrabers.controller.akai.fire.mode.FireTrackMixerMode;
import de.mossgrabers.controller.akai.fire.mode.FireTrackMode;
import de.mossgrabers.controller.akai.fire.mode.FireUserMode;
import de.mossgrabers.controller.akai.fire.mode.NoteMode;
import de.mossgrabers.controller.akai.fire.view.Drum4View;
import de.mossgrabers.controller.akai.fire.view.DrumView64;
import de.mossgrabers.controller.akai.fire.view.DrumXoXView;
import de.mossgrabers.controller.akai.fire.view.IFireView;
import de.mossgrabers.controller.akai.fire.view.MixView;
import de.mossgrabers.controller.akai.fire.view.PianoView;
import de.mossgrabers.controller.akai.fire.view.PlayView;
import de.mossgrabers.controller.akai.fire.view.PolySequencerView;
import de.mossgrabers.controller.akai.fire.view.SequencerView;
import de.mossgrabers.controller.akai.fire.view.SessionView;
import de.mossgrabers.controller.akai.fire.view.ShiftView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.mode.KnobRowTouchModeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeMultiSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.observer.IParametersAdjustObserver;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Akai Fire controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireControllerSetup extends AbstractControllerSetup<FireControlSurface, FireConfiguration>
{
    private static final Modes []                                         MODES =
    {
        Modes.DEVICE_LAYER,
        Modes.TRACK,
        Modes.DEVICE_PARAMS,
        Modes.USER
    };

    private ModeMultiSelectCommand<FireControlSurface, FireConfiguration> modeSelectCommand;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public FireControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new FireColorManager ();
        this.valueChanger = new TwosComplementValueChanger (1024, 10);
        this.configuration = new FireConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
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
        ms.setNumDrumPadLayers (4);
        ms.setNumDeviceLayers (4);
        ms.setNumUserPages (8);
        ms.setNumUserPageSize (4);
        ms.setNumFilterColumnEntries (3);
        ms.setNumResults (3);
        ms.setHasFullFlatTrackList (true);
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
        this.scales = new FireScales (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */);
        final FireControlSurface surface = new FireControlSurface (this.host, this.colorManager, this.configuration, output, input);
        surface.configureLEDs ();
        this.surfaces.add (surface);

        surface.addGraphicsDisplay (new FireDisplay (this.host, output, this.valueChanger.getUpperBound ()));

        surface.getModeManager ().setDefaultID (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final FireControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.DEVICE_LAYER, new FireLayerMode (surface, this.model));
        modeManager.register (Modes.DEVICE_LAYER_VOLUME, new FireLayerMixerMode (surface, this.model));
        modeManager.register (Modes.TRACK, new FireTrackMode (surface, this.model));
        modeManager.register (Modes.VOLUME, new FireTrackMixerMode (surface, this.model));
        modeManager.register (Modes.DEVICE_PARAMS, new FireParameterMode (surface, this.model));
        modeManager.register (Modes.USER, new FireUserMode (surface, this.model));
        modeManager.register (Modes.BROWSER, new BrowserMode (surface, this.model));

        // Note mode needs the ALT button to exist
        this.addButton (ButtonID.ALT, "ALT", (event, velocity) -> {

            final IMode activeMode = modeManager.getActive ();
            if (activeMode instanceof final IParametersAdjustObserver observer)
                observer.parametersAdjusted ();

        }, FireControlSurface.FIRE_ALT);
        modeManager.register (Modes.NOTE, new NoteMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final FireControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.register (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model, true));

        viewManager.register (Views.PLAY, new PlayView (surface, this.model));
        viewManager.register (Views.PIANO, new PianoView (surface, this.model));

        viewManager.register (Views.DRUM, new DrumXoXView (surface, this.model));
        viewManager.register (Views.DRUM4, new Drum4View (surface, this.model));
        viewManager.register (Views.DRUM64, new DrumView64 (surface, this.model));

        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.MIX, new MixView (surface, this.model));

        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final FireControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ModeManager modeManager = surface.getModeManager ();

        // Modes

        this.modeSelectCommand = new FireModeCommand (this.model, surface, MODES);
        this.addButton (ButtonID.BANK_RIGHT, "BANK", this.modeSelectCommand, FireControlSurface.FIRE_BANK);

        for (int i = 0; i < MODES.length; i++)
        {
            final int index = i;
            surface.createLight (OutputID.get (OutputID.LED1, i), () -> modeManager.getActiveID () == MODES[index] ? index : 5, color -> {
                if (color < 5)
                    surface.setTrigger (0, 0x1B, color);
            }, state -> state < 5 ? ColorEx.RED : ColorEx.GRAY, null);
        }

        // Transport

        final ITransport t = this.model.getTransport ();
        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface, ButtonID.ALT), FireControlSurface.FIRE_PLAY, t::isPlaying, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.STOP, "STOP", new FireStopCommand (this.model, surface), FireControlSurface.FIRE_STOP, () -> !t.isPlaying (), ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);
        this.addButton (ButtonID.RECORD, "REC", new FireRecordCommand (this.model, surface), FireControlSurface.FIRE_REC, () -> {

            if (this.isRecordShifted (surface))
                return t.isLauncherOverdub () ? 3 : 2;
            return t.isRecording () ? 1 : 0;

        }, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2);

        this.addButton (ButtonID.METRONOME, "PATTERN/\nSONG", new MetronomeCommand<> (this.model, surface, false), FireControlSurface.FIRE_PATTERN, () -> {

            if (surface.isShiftPressed ())
                return t.isMetronomeTicksOn ();
            return t.isMetronomeOn ();

        }, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2);

        // Views + state toggles

        this.addButton (ButtonID.SEQUENCER, "STEP", new StepSequencerSelectCommand (this.model, surface), FireControlSurface.FIRE_STEP, () -> {
            if (viewManager.isActive (Views.SEQUENCER))
                return 1;
            if (viewManager.isActive (Views.POLY_SEQUENCER))
                return 2;
            return surface.isShiftPressed () && surface.getConfiguration ().isAccentActive () ? 1 : 0;
        }, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2, ColorManager.BUTTON_STATE_ON);

        this.addButton (ButtonID.NOTE, "NOTE", new PlaySelectCommand (this.model, surface), FireControlSurface.FIRE_NOTE, () -> {
            if (viewManager.isActive (Views.PLAY))
                return 1;
            if (viewManager.isActive (Views.PIANO))
                return 2;
            return 0;
        }, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2, ColorManager.BUTTON_STATE_ON);

        this.addButton (ButtonID.DRUM, "DRUM", new DrumSequencerSelectCommand (this.model, surface), FireControlSurface.FIRE_DRUM, () -> {
            if (viewManager.isActive (Views.DRUM))
                return 1;
            if (viewManager.isActive (Views.DRUM4))
                return 2;
            if (viewManager.isActive (Views.DRUM64))
                return 3;
            return 0;
        }, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2, ColorManager.BUTTON_STATE_ON, ColorManager.BUTTON_STATE_HI);

        this.addButton (ButtonID.SESSION, "PERFORM", new SessionSelectCommand (this.model, surface), FireControlSurface.FIRE_PERFORM, () -> {
            if (viewManager.isActive (Views.SESSION))
                return 1;
            if (viewManager.isActive (Views.MIX))
                return 2;
            return 0;
        }, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2, ColorManager.BUTTON_STATE_ON);

        this.addButton (ButtonID.SHIFT, "SHIFT", new ToggleShiftViewCommand<> (this.model, surface), FireControlSurface.FIRE_SHIFT, () -> viewManager.isActive (Views.SHIFT) || surface.isShiftPressed () ? 1 : 0, FireColorManager.BUTTON_STATE_ON2, FireColorManager.BUTTON_STATE_HI2);

        this.addButton (ButtonID.SELECT, "SELECT", new FireSelectButtonCommand (this.model, surface), FireControlSurface.SELECT);
        this.addButton (ButtonID.BROWSE, "BROWSER", new FireBrowserCommand (this.model, surface), FireControlSurface.FIRE_BROWSER, () -> modeManager.isActive (Modes.BROWSER));

        // Navigation

        this.addButton (ButtonID.ARROW_LEFT, "GRID\nLEFT", new FireViewButtonCommand (ButtonID.ARROW_LEFT, this.model, surface), FireControlSurface.FIRE_GRID_LEFT);
        this.addButton (ButtonID.ARROW_RIGHT, "GRID\nRIGHT", new FireViewButtonCommand (ButtonID.ARROW_RIGHT, this.model, surface), FireControlSurface.FIRE_GRID_RIGHT);

        this.addButton (ButtonID.ARROW_UP, "PATTERN\nUP", (event, velocity) -> {
            if (event != ButtonEvent.UP)
                return;
            if (surface.isPressed (ButtonID.ALT))
            {
                this.model.getApplication ().redo ();
                surface.getDisplay ().notify ("Redo");
                return;
            }
            this.model.getCursorTrack ().getSlotBank ().selectNextItem ();
        }, FireControlSurface.FIRE_PAT_UP);
        this.addButton (ButtonID.ARROW_DOWN, "PATTERN\nDOWN", (event, velocity) -> {
            if (event != ButtonEvent.UP)
                return;
            if (surface.isPressed (ButtonID.ALT))
            {
                this.model.getApplication ().undo ();
                surface.getDisplay ().notify ("Undo");
                return;
            }
            this.model.getCursorTrack ().getSlotBank ().selectPreviousItem ();
        }, FireControlSurface.FIRE_PAT_DOWN);

        for (int i = 0; i < 4; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (buttonID, "SOLO" + (i + 1), new ViewButtonCommand<> (buttonID, surface), FireControlSurface.FIRE_SOLO_1 + i, () -> {
                final IView activeView = viewManager.getActive ();
                return activeView != null ? activeView.getButtonColor (buttonID) : 0;
            });

            final int index = i;
            surface.createLight (OutputID.get (OutputID.LED5, i), () -> {
                final IView activeView = viewManager.getActive ();
                return activeView instanceof final IFireView fireView ? fireView.getSoloButtonColor (index) : 0;
            }, color -> surface.setTrigger (0, 0x28 + index, color), state -> {
                switch (state)
                {
                    case 0:
                        return ColorEx.GRAY;
                    case 1:
                        return ColorEx.DARK_RED;
                    case 2:
                        return ColorEx.DARK_GREEN;
                    case 3:
                        return ColorEx.RED;
                    case 4:
                        return ColorEx.GREEN;
                    default:
                        return ColorEx.GRAY;
                }
            }, null);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final FireControlSurface surface = this.getSurface ();
        final IMidiInput input = surface.getMidiInput ();

        for (int i = 0; i < 4; i++)
        {
            final IHwRelativeKnob knob = this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + i, new KnobRowModeCommand<> (i, this.model, surface), FireControlSurface.CC_VOLUME + i);
            knob.bindTouch (new KnobRowTouchModeCommand<> (i, this.model, surface), input, BindType.NOTE, 0, FireControlSurface.TOUCH_VOLUME + i);
            knob.setIndexInGroup (i);
        }

        this.addRelativeKnob (ContinuousID.VIEW_SELECTION, "Select", new SelectKnobCommand (this.model, surface), FireControlSurface.CC_SELECT).setShouldAdaptSensitivity (false);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final FireControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (27.0, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD2).setBounds (44.0, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD3).setBounds (61.25, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD4).setBounds (78.25, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD5).setBounds (95.25, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD6).setBounds (112.5, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD7).setBounds (129.75, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD8).setBounds (146.5, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD9).setBounds (163.75, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD10).setBounds (180.75, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD11).setBounds (198.0, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD12).setBounds (214.5, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD13).setBounds (232.0, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD14).setBounds (249.25, 107.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD15).setBounds (266.25, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD16).setBounds (283.0, 107.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD17).setBounds (27.0, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD18).setBounds (44.0, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD19).setBounds (61.25, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD20).setBounds (78.25, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD21).setBounds (95.25, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD22).setBounds (112.5, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD23).setBounds (129.75, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD24).setBounds (146.5, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD25).setBounds (163.75, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD26).setBounds (180.75, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD27).setBounds (198.0, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD28).setBounds (214.5, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD29).setBounds (232.0, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD30).setBounds (249.25, 85.0, 12.75, 18.25);
        surface.getButton (ButtonID.PAD31).setBounds (266.25, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD32).setBounds (283.0, 85.25, 12.75, 18.25);
        surface.getButton (ButtonID.PAD33).setBounds (27.0, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD34).setBounds (44.0, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD35).setBounds (61.25, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD36).setBounds (78.25, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD37).setBounds (95.25, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD38).setBounds (112.5, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD39).setBounds (129.75, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD40).setBounds (146.5, 63.75, 12.0, 18.25);
        surface.getButton (ButtonID.PAD41).setBounds (163.75, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD42).setBounds (180.75, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD43).setBounds (198.0, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD44).setBounds (214.5, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD45).setBounds (232.0, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD46).setBounds (249.25, 63.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD47).setBounds (266.25, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD48).setBounds (283.0, 63.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD49).setBounds (27.0, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD50).setBounds (44.0, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD51).setBounds (61.25, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD52).setBounds (78.25, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD53).setBounds (95.25, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD54).setBounds (112.5, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD55).setBounds (129.75, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD56).setBounds (146.5, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD57).setBounds (163.75, 42.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD58).setBounds (180.75, 42.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD59).setBounds (198.0, 42.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD60).setBounds (214.5, 42.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD61).setBounds (232.0, 42.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD62).setBounds (249.25, 42.5, 12.75, 18.25);
        surface.getButton (ButtonID.PAD63).setBounds (266.25, 42.75, 12.75, 18.25);
        surface.getButton (ButtonID.PAD64).setBounds (283.0, 42.75, 12.75, 18.25);

        surface.getButton (ButtonID.BANK_RIGHT).setBounds (4.75, 29.0, 10.0, 10.0);
        surface.getButton (ButtonID.PLAY).setBounds (236.5, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.STOP).setBounds (258.5, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.RECORD).setBounds (280.75, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.METRONOME).setBounds (214.25, 134.0, 17.25, 11.75);

        surface.getButton (ButtonID.SELECT).setBounds (238.25, 2.5, 18.25, 10.0);

        surface.getButton (ButtonID.SEQUENCER).setBounds (4.5, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.NOTE).setBounds (27.0, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.DRUM).setBounds (49.25, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.SESSION).setBounds (71.75, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.SHIFT).setBounds (94.25, 134.0, 17.25, 11.75);
        surface.getButton (ButtonID.ALT).setBounds (116.5, 134.0, 17.25, 11.75);

        surface.getButton (ButtonID.ARROW_LEFT).setBounds (261.0, 18.0, 18.0, 10.0);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (281.75, 18.0, 18.0, 10.0);
        surface.getButton (ButtonID.ARROW_UP).setBounds (138.25, 12.25, 18.5, 9.5);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (138.25, 23.75, 18.5, 9.5);

        surface.getButton (ButtonID.SCENE1).setBounds (4.75, 42.75, 10.0, 17.25);
        surface.getButton (ButtonID.SCENE2).setBounds (4.75, 63.75, 10.0, 17.25);
        surface.getButton (ButtonID.SCENE3).setBounds (4.75, 86.0, 10.0, 17.25);
        surface.getButton (ButtonID.SCENE4).setBounds (4.75, 107.75, 10.0, 17.25);

        surface.getButton (ButtonID.BROWSE).setBounds (214.25, 17.0, 17.25, 11.75);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (27.5, 12.75, 22.0, 19.25);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (55.0, 12.75, 22.0, 19.25);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (82.75, 12.75, 22.0, 19.25);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (110.25, 12.75, 22.0, 19.25);
        surface.getContinuous (ContinuousID.VIEW_SELECTION).setBounds (238.25, 13.5, 17.75, 17.25);

        surface.getLight (OutputID.LED1).setBounds (4.75, 4.0, 5.5, 4.25);
        surface.getLight (OutputID.LED2).setBounds (4.75, 10.0, 5.5, 4.25);
        surface.getLight (OutputID.LED3).setBounds (4.75, 16.25, 5.5, 4.25);
        surface.getLight (OutputID.LED4).setBounds (4.75, 22.25, 5.5, 4.25);
        surface.getLight (OutputID.LED5).setBounds (18.0, 42.75, 5.0, 17.25);
        surface.getLight (OutputID.LED6).setBounds (18.0, 63.75, 5.0, 17.25);
        surface.getLight (OutputID.LED7).setBounds (18.0, 86.0, 5.0, 17.25);
        surface.getLight (OutputID.LED8).setBounds (18.0, 107.75, 5.0, 17.25);

        surface.getGraphicsDisplay ().getHardwareDisplay ().setBounds (165.25, 11.75, 40.75, 20.75);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final FireControlSurface surface = this.getSurface ();

        surface.getViewManager ().addChangeListener ( (previousViewId, activeViewId) -> this.onViewChange ());

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.configuration.addSettingObserver (FireConfiguration.PAD_BRIGHTNESS, surface::configureLEDs);
        this.configuration.addSettingObserver (FireConfiguration.PAD_SATURATION, surface::configureLEDs);

        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);

        this.activateBrowserObserver (Modes.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final FireControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (this.configuration.shouldStartWithSessionView () ? Views.SESSION : this.configuration.getPreferredNoteView ());
        surface.getModeManager ().setActive (Modes.TRACK);
        this.modeSelectCommand.activateMode (Modes.TRACK);
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
