// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad;

import de.mossgrabers.controller.novation.launchpad.command.trigger.DeleteCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.LaunchpadCursorCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.LaunchpadDuplicateCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.MuteCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.PanCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.PlayAndNewCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.ProjectCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.RecordArmCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.SelectDeviceViewCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.SelectMainViewCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.SendsCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.SoloCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.StopClipCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.TrackSelectCommand;
import de.mossgrabers.controller.novation.launchpad.command.trigger.VolumeCommand;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadScales;
import de.mossgrabers.controller.novation.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.controller.novation.launchpad.definition.button.ButtonSetup;
import de.mossgrabers.controller.novation.launchpad.definition.button.LaunchpadButton;
import de.mossgrabers.controller.novation.launchpad.definition.button.LaunchpadButtonInfo;
import de.mossgrabers.controller.novation.launchpad.view.ChordsView;
import de.mossgrabers.controller.novation.launchpad.view.DeviceView;
import de.mossgrabers.controller.novation.launchpad.view.Drum4View;
import de.mossgrabers.controller.novation.launchpad.view.Drum64View;
import de.mossgrabers.controller.novation.launchpad.view.Drum8View;
import de.mossgrabers.controller.novation.launchpad.view.DrumView;
import de.mossgrabers.controller.novation.launchpad.view.LaunchpadBrowserView;
import de.mossgrabers.controller.novation.launchpad.view.LaunchpadShuffleView;
import de.mossgrabers.controller.novation.launchpad.view.MixView;
import de.mossgrabers.controller.novation.launchpad.view.NoteEditView;
import de.mossgrabers.controller.novation.launchpad.view.NoteViewSelectView;
import de.mossgrabers.controller.novation.launchpad.view.PanView;
import de.mossgrabers.controller.novation.launchpad.view.PianoView;
import de.mossgrabers.controller.novation.launchpad.view.PlayView;
import de.mossgrabers.controller.novation.launchpad.view.PolySequencerView;
import de.mossgrabers.controller.novation.launchpad.view.ProjectView;
import de.mossgrabers.controller.novation.launchpad.view.RaindropsView;
import de.mossgrabers.controller.novation.launchpad.view.SendsView;
import de.mossgrabers.controller.novation.launchpad.view.SequencerView;
import de.mossgrabers.controller.novation.launchpad.view.SessionView;
import de.mossgrabers.controller.novation.launchpad.view.ShiftView;
import de.mossgrabers.controller.novation.launchpad.view.UserView;
import de.mossgrabers.controller.novation.launchpad.view.VolumeView;
import de.mossgrabers.framework.command.aftertouch.AftertouchViewCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.MetronomeCommand;
import de.mossgrabers.framework.command.trigger.view.SelectPlayViewCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackMuteMode;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.mode.track.TrackRecArmMode;
import de.mossgrabers.framework.mode.track.TrackSelectMode;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.mode.track.TrackSoloMode;
import de.mossgrabers.framework.mode.track.TrackStopClipMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.TempoView;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.ClipLengthView;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Support for several Novation Launchpad controllers.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchpadControllerSetup extends AbstractControllerSetup<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final ILaunchpadControllerDefinition definition;

    private static final Views []                PLAY_VIEWS      =
    {
        Views.PLAY,
        Views.PIANO,
        Views.DRUM64
    };

    private static final Views []                SEQUENCER_VIEWS =
    {
        Views.SEQUENCER,
        Views.POLY_SEQUENCER,
        Views.RAINDROPS
    };

    private static final Views []                DRUM_VIEWS      =
    {
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8
    };

    private static final Set<Views>              MAIN_VIEWS      = new HashSet<> ();

    static
    {
        MAIN_VIEWS.add (Views.PLAY);
        MAIN_VIEWS.add (Views.CHORDS);
        MAIN_VIEWS.add (Views.PIANO);
        MAIN_VIEWS.add (Views.DRUM64);
        MAIN_VIEWS.add (Views.SEQUENCER);
        MAIN_VIEWS.add (Views.POLY_SEQUENCER);
        MAIN_VIEWS.add (Views.RAINDROPS);
        MAIN_VIEWS.add (Views.DRUM);
        MAIN_VIEWS.add (Views.DRUM4);
        MAIN_VIEWS.add (Views.DRUM8);
        MAIN_VIEWS.add (Views.CLIP_LENGTH);
    }


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param definition The Launchpad definition
     */
    public LaunchpadControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final ILaunchpadControllerDefinition definition)
    {
        super (factory, host, globalSettings, documentSettings);

        this.definition = definition;
        this.colorManager = new LaunchpadColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new LaunchpadConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), definition);
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new LaunchpadScales (this.valueChanger, 36, 100, 8, 8);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.enableDrum64Device ();
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */);
        final LaunchpadControlSurface surface = new LaunchpadControlSurface (this.host, this.colorManager, this.configuration, output, input, this.definition);
        this.surfaces.add (surface);
        surface.setLaunchpadToStandalone ();
        surface.setLaunchpadToPrgMode ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.REC_ARM, new TrackRecArmMode<> (surface, this.model));
        modeManager.register (Modes.TRACK_SELECT, new TrackSelectMode<> (surface, this.model));
        modeManager.register (Modes.MUTE, new TrackMuteMode<> (surface, this.model));
        modeManager.register (Modes.SOLO, new TrackSoloMode<> (surface, this.model));
        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true));
        modeManager.register (Modes.PAN, new TrackPanMode<> (surface, this.model, true));
        modeManager.register (Modes.SEND, new TrackSendMode<> (-1, surface, this.model, true));
        modeManager.register (Modes.STOP_CLIP, new TrackStopClipMode<> (surface, this.model));
        modeManager.register (Modes.DUMMY, new TrackSelectMode<> (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.register (Views.SESSION, new SessionView ("Session", surface, this.model));
        viewManager.register (Views.PLAY, new PlayView (surface, this.model));
        viewManager.register (Views.CHORDS, new ChordsView (surface, this.model));
        viewManager.register (Views.PIANO, new PianoView (surface, this.model));
        viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.register (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model, true));
        viewManager.register (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.DRUM4, new Drum4View (surface, this.model));
        viewManager.register (Views.DRUM8, new Drum8View (surface, this.model));
        viewManager.register (Views.DRUM64, new Drum64View (surface, this.model));
        viewManager.register (Views.DEVICE, new DeviceView (surface, this.model));
        viewManager.register (Views.USER, new UserView (surface, this.model));
        viewManager.register (Views.BROWSER, new LaunchpadBrowserView (surface, this.model));
        viewManager.register (Views.TRACK_PAN, new PanView (surface, this.model));
        viewManager.register (Views.TRACK_VOLUME, new VolumeView (surface, this.model));
        viewManager.register (Views.TRACK_SENDS, new SendsView (surface, this.model));
        viewManager.register (Views.MIX, new MixView (surface, this.model));
        viewManager.register (Views.CONTROL, new NoteViewSelectView (surface, this.model));
        viewManager.register (Views.NOTE_EDIT_VIEW, new NoteEditView (surface, this.model));
        viewManager.register (Views.CLIP_LENGTH, new ClipLengthView<> (surface, this.model, true));
        viewManager.register (Views.TEMPO, new TempoView<> (surface, this.model, LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI, LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK));
        viewManager.register (Views.SHUFFLE, new LaunchpadShuffleView (surface, this.model, LaunchpadColorManager.LAUNCHPAD_COLOR_PINK_HI, LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE, LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK));
        viewManager.register (Views.PROJECT, new ProjectView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final LaunchpadControlSurface surface = this.getSurface ();

        surface.getViewManager ().addChangeListener ( (previousViewId, activeViewId) -> {

            surface.getLight (OutputID.LED1).forceFlush ();
            this.updateIndication ();

        });

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));

        if (this.configuration.canSettingBeObserved (LaunchpadConfiguration.PAD_BRIGHTNESS))
            this.configuration.addSettingObserver (LaunchpadConfiguration.PAD_BRIGHTNESS, surface::updateBrightness);

        this.configuration.registerDeactivatedItemsHandler (this.model);
        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);

        this.activateBrowserObserver (Views.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ITransport transport = this.model.getTransport ();

        final ButtonSetup buttonSetup = this.definition.getButtonSetup ();

        this.addButton (ButtonID.SHIFT, "Shift", new ToggleShiftViewCommand<> (this.model, surface), buttonSetup.get (LaunchpadButton.SHIFT).getControl (), () -> viewManager.isActive (Views.SHIFT) || surface.isShiftPressed () ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);

        final LaunchpadCursorCommand commandUp = new LaunchpadCursorCommand (Direction.UP, this.model, surface);
        final LaunchpadCursorCommand commandDown = new LaunchpadCursorCommand (Direction.DOWN, this.model, surface);
        final LaunchpadCursorCommand commandLeft = new LaunchpadCursorCommand (Direction.LEFT, this.model, surface);
        final LaunchpadCursorCommand commandRight = new LaunchpadCursorCommand (Direction.RIGHT, this.model, surface);
        this.addButton (ButtonID.UP, "Up", commandUp, buttonSetup.get (LaunchpadButton.ARROW_UP).getControl (), () -> commandUp.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.addButton (ButtonID.DOWN, "Down", commandDown, buttonSetup.get (LaunchpadButton.ARROW_DOWN).getControl (), () -> commandDown.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.addButton (ButtonID.LEFT, "Left", commandLeft, buttonSetup.get (LaunchpadButton.ARROW_LEFT).getControl (), () -> commandLeft.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        this.addButton (ButtonID.RIGHT, "Right", commandRight, buttonSetup.get (LaunchpadButton.ARROW_RIGHT).getControl (), () -> commandRight.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        this.addButton (ButtonID.SESSION, "Session", new SelectSessionViewCommand (this.model, surface), buttonSetup.get (LaunchpadButton.SESSION).getControl (), () -> this.getViewStateColor (LaunchpadColorManager.LAUNCHPAD_COLOR_LIME, Views.SESSION, Views.MIX));
        final SelectDeviceViewCommand selectDeviceViewCommand = new SelectDeviceViewCommand (this.model, surface);
        this.addButton (ButtonID.DEVICE, "Device", selectDeviceViewCommand, buttonSetup.get (LaunchpadButton.DEVICE).getControl (), selectDeviceViewCommand::getButtonColor);

        final LaunchpadButtonInfo userInfo = buttonSetup.get (LaunchpadButton.USER);
        if (!userInfo.isVirtual ())
            this.addButton (ButtonID.USER, "User", new ViewMultiSelectCommand<> (this.model, surface, Views.CLIP_LENGTH), userInfo.getControl (), () -> viewManager.isActive (Views.CLIP_LENGTH) ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);

        final LaunchpadButtonInfo projectInfo = buttonSetup.get (LaunchpadButton.PROJECT);
        if (!projectInfo.isVirtual ())
            this.addButton (ButtonID.PROJECT, "Project", new ProjectCommand (this.model, surface), projectInfo.getControl (), () -> viewManager.isActive (Views.PROJECT) ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);

        // The following buttons are only available on the Pro but the commands are used by all
        // Launchpad models!
        final LaunchpadButtonInfo clickInfo = buttonSetup.get (LaunchpadButton.CLICK);
        this.addButton (ButtonID.METRONOME, "Metronome", new MetronomeCommand<> (this.model, surface, true), clickInfo.isShifted () ? -1 : clickInfo.getControl (), () -> {
            if (surface.isShiftPressed ())
                return surface.isPressed (ButtonID.METRONOME) ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING;
            return transport.isMetronomeOn () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
        });

        final LaunchpadButtonInfo shiftInfo = buttonSetup.get (LaunchpadButton.UNDO);
        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), shiftInfo.isShifted () ? -1 : shiftInfo.getControl (), () -> get2StateColor (surface, ButtonID.UNDO));

        this.addButton (ButtonID.DELETE, "Delete", new DeleteCommand (this.model, surface), buttonSetup.get (LaunchpadButton.DELETE).getControl (), () -> this.getDeleteStateColor (surface));
        this.addButton (ButtonID.QUANTIZE, "Quantize", new QuantizeCommand<> (this.model, surface), buttonSetup.get (LaunchpadButton.QUANTIZE).getControl (), () -> getStateColor (surface, ButtonID.QUANTIZE));
        this.addButton (ButtonID.DUPLICATE, "Duplicate", new LaunchpadDuplicateCommand (this.model, surface), buttonSetup.get (LaunchpadButton.DUPLICATE).getControl (), () -> getDuplicateStateColor (surface));
        this.addButton (ButtonID.PLAY, "Play", new PlayAndNewCommand (this.model, surface), buttonSetup.get (LaunchpadButton.PLAY).getControl (), () -> this.getPlayStateColor (surface));

        if (this.definition.isPro ())
        {
            final ConfiguredRecordCommand<LaunchpadControlSurface, LaunchpadConfiguration> configuredRecordCommand = new ConfiguredRecordCommand<> (this.model, surface);
            this.addButton (ButtonID.RECORD, "Record", configuredRecordCommand, buttonSetup.get (LaunchpadButton.RECORD).getControl (), () -> {

                final boolean isShift = surface.isShiftPressed ();
                final boolean isLit = configuredRecordCommand.isLit (isShift);
                if (isShift)
                    return isLit ? LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_AMBER;
                return isLit ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO;

            });
        }

        // Mode selection buttons

        this.addButton (ButtonID.REC_ARM, "Rec Arm", new RecordArmCommand (this.model, surface), buttonSetup.get (LaunchpadButton.REC_ARM).getControl (), () -> this.getModeColorIndex (ButtonID.REC_ARM));
        this.addButton (ButtonID.TRACK, "Track", new TrackSelectCommand (this.model, surface), buttonSetup.get (LaunchpadButton.TRACK_SELECT).getControl (), () -> this.getModeColorIndex (ButtonID.TRACK));
        this.addButton (ButtonID.MUTE, "Mute", new MuteCommand (this.model, surface), buttonSetup.get (LaunchpadButton.MUTE).getControl (), () -> this.getModeColorIndex (ButtonID.MUTE));
        this.addButton (ButtonID.SOLO, "Solo", new SoloCommand (this.model, surface), buttonSetup.get (LaunchpadButton.SOLO).getControl (), () -> this.getModeColorIndex (ButtonID.SOLO));
        this.addButton (ButtonID.VOLUME, "Volume", new VolumeCommand (this.model, surface), buttonSetup.get (LaunchpadButton.VOLUME).getControl (), () -> this.getModeColorIndex (ButtonID.VOLUME));
        this.addButton (ButtonID.PAN_SEND, "Panorama", new PanCommand (this.model, surface), buttonSetup.get (LaunchpadButton.PAN).getControl (), () -> this.getModeColorIndex (ButtonID.PAN_SEND));
        this.addButton (ButtonID.SENDS, "Sends", new SendsCommand (this.model, surface), buttonSetup.get (LaunchpadButton.SENDS).getControl (), () -> this.getModeColorIndex (ButtonID.SENDS));
        this.addButton (ButtonID.STOP_CLIP, "Stop Clip", new StopClipCommand (this.model, surface), buttonSetup.get (LaunchpadButton.STOP_CLIP).getControl (), () -> this.getModeColorIndex (ButtonID.STOP_CLIP));

        // Scene buttons

        for (int i = 0; i < 8; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (buttonID, "Scene " + (i + 1), new ViewButtonCommand<> (buttonID, surface), buttonSetup.get (LaunchpadButton.SCENES.get (i)).getControl (), () -> this.getButtonColorFromActiveView (buttonID));
        }

        // Pro Mk3
        if (this.definition.hasTrackSelectionButtons ())
        {
            // Additional Track selection buttons
            for (int i = 0; i < 8; i++)
            {
                final int index = i;
                this.addButton (ButtonID.get (ButtonID.ROW1_1, i), "Track Select " + (i + 1), (event, velocity) -> this.handleTrackSelection (event, index), LaunchpadControlSurface.LAUNCHPAD_TRACK1 + i, () -> this.getTrackModeColorIndex (index));
            }

            this.addButton (ButtonID.NOTE, "Note", new SelectPlayViewCommand<> (this.model, surface, PLAY_VIEWS, MAIN_VIEWS), buttonSetup.get (LaunchpadButton.NOTE).getControl (), () -> this.getViewStateColor (LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_HI, PLAY_VIEWS));
            this.addButton (ButtonID.DRUM, "Drum Seq", new SelectPlayViewCommand<> (this.model, surface, DRUM_VIEWS, MAIN_VIEWS), 95, () -> this.getViewStateColor (LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI, DRUM_VIEWS));
            this.addButton (ButtonID.SEQUENCER, "Sequencer", new SelectPlayViewCommand<> (this.model, surface, SEQUENCER_VIEWS, MAIN_VIEWS), 97, () -> this.getViewStateColor (LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI, SEQUENCER_VIEWS));
            this.addButton (ButtonID.NEW, "New", new NewCommand<> (this.model, surface), LaunchpadControlSurface.PRO3_LAUNCHPAD_FIXED_LENGTH, () -> surface.isPressed (ButtonID.NEW) ? LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER_LO);
        }
        else
            this.addButton (ButtonID.NOTE, "Note", new SelectMainViewCommand (this.model, surface, MAIN_VIEWS), buttonSetup.get (LaunchpadButton.NOTE).getControl (), this::getNoteButtonColor);

        // Update the front or logo LED with the color of the current track

        surface.createLight (OutputID.LED1, () -> {

            final ITrack cursorTrack = this.model.getCursorTrack ();
            return cursorTrack.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorID (cursorTrack.getColor ())) : 0;

        }, color -> this.definition.setLogoColor (surface, color), state -> this.colorManager.getColor (state, null), null);

        // Workaround for some pads not redrawn all the time
        for (final Entry<ButtonID, IHwButton> entry: surface.getButtons ().entrySet ())
        {
            final ButtonID key = entry.getKey ();
            final int keyValue = key.ordinal ();
            if (ButtonID.PAD1.ordinal () < keyValue || ButtonID.PAD64.ordinal () > keyValue)
            {
                entry.getValue ().addEventHandler (ButtonEvent.UP, event -> {
                    final IHwLight light = surface.getButton (key).getLight ();
                    if (light != null)
                        light.forceFlush ();
                });
            }
        }
    }


    private int getNoteButtonColor ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        final Views activeID = viewManager.getActiveID ();
        if (activeID == Views.CONTROL)
            return LaunchpadColorManager.COLOR_VIEW_SELECTED;
        return MAIN_VIEWS.contains (activeID) ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        if (ButtonID.isSceneButton (buttonID))
            return this.definition.sceneButtonsUseCC () ? BindType.CC : BindType.NOTE;
        return super.getTriggerBindType (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        final Views [] views =
        {
            Views.PLAY,
            Views.PIANO,
            Views.DRUM,
            Views.DRUM64
        };
        for (final Views viewID: views)
        {
            final IView view = viewManager.get (viewID);
            view.registerAftertouchCommand (new AftertouchViewCommand<> (view, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();

        final double offset = this.definition.isPro () ? 0 : 100;

        surface.getButton (ButtonID.PAD1).setBounds (114.0 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD2).setBounds (188.5 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD3).setBounds (262.25 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD4).setBounds (335.75 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD5).setBounds (410.25 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD6).setBounds (483.0 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD7).setBounds (559.25 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD8).setBounds (632.75 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD9).setBounds (114.0 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD10).setBounds (188.5 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD11).setBounds (262.25 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD12).setBounds (335.75 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD13).setBounds (410.25 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD14).setBounds (483.0 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD15).setBounds (559.25 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD16).setBounds (632.75 - offset, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD17).setBounds (114.0 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD18).setBounds (188.5 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD19).setBounds (262.25 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD20).setBounds (335.75 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD21).setBounds (410.25 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD22).setBounds (483.0 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD23).setBounds (559.25 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD24).setBounds (632.75 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD25).setBounds (114.0 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD26).setBounds (188.5 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD27).setBounds (262.25 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD28).setBounds (335.75 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD29).setBounds (410.25 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD30).setBounds (483.0 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD31).setBounds (559.25 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD32).setBounds (632.75 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD33).setBounds (114.0 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD34).setBounds (188.5 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD35).setBounds (262.25 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD36).setBounds (335.75 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD37).setBounds (410.25 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD38).setBounds (483.0 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD39).setBounds (559.25 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD40).setBounds (632.75 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD41).setBounds (114.0 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD42).setBounds (188.5 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD43).setBounds (262.25 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD44).setBounds (335.75 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD45).setBounds (410.25 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD46).setBounds (483.0 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD47).setBounds (559.25 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD48).setBounds (632.75 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD49).setBounds (114.0 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD50).setBounds (188.5 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD51).setBounds (262.25 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD52).setBounds (335.75 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD53).setBounds (410.25 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD54).setBounds (483.0 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD55).setBounds (559.25 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD56).setBounds (632.75 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD57).setBounds (114.0 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD58).setBounds (188.5 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD59).setBounds (262.25 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD60).setBounds (335.75 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD61).setBounds (410.25 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD62).setBounds (483.0 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD63).setBounds (559.25 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD64).setBounds (632.75 - offset, 79.0, 61.0, 60.0);

        surface.getButton (ButtonID.SCENE1).setBounds (707.0 - offset, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE2).setBounds (707.0 - offset, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE3).setBounds (707.0 - offset, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE4).setBounds (707.0 - offset, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE5).setBounds (707.0 - offset, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE6).setBounds (707.0 - offset, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE7).setBounds (707.0 - offset, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE8).setBounds (707.0 - offset, 522.25, 61.0, 60.0);

        if (this.definition.isPro ())
        {
            surface.getButton (ButtonID.QUANTIZE).setBounds (41.0, 373.0, 61.0, 60.0);
            surface.getButton (ButtonID.PLAY).setBounds (41.0, 522.25, 61.0, 60.0);
            surface.getButton (ButtonID.RECORD).setBounds (41.0, 596.0, 61.0, 60.0);

            if (this.definition.hasTrackSelectionButtons ())
            {
                // Pro MK3

                surface.getButton (ButtonID.SHIFT).setBounds (41.0, 8.5, 61.0, 60.0);

                surface.getButton (ButtonID.LEFT).setBounds (114.0, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.RIGHT).setBounds (188.5, 8.5, 61.0, 60.0);

                surface.getButton (ButtonID.SESSION).setBounds (262.25, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.NOTE).setBounds (335.75, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.DRUM).setBounds (410.25, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.USER).setBounds (483.0, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.SEQUENCER).setBounds (559.25, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.PROJECT).setBounds (632.75, 8.5, 61.0, 60.0);

                surface.getButton (ButtonID.UP).setBounds (41.0, 79.0, 61.0, 60.0);
                surface.getButton (ButtonID.DOWN).setBounds (41.0, 150.75, 61.0, 60.0);
                surface.getButton (ButtonID.DELETE).setBounds (41.0, 226.75, 61.0, 60.0);
                surface.getButton (ButtonID.DUPLICATE).setBounds (41.0, 299.75, 61.0, 60.0);
                surface.getButton (ButtonID.NEW).setBounds (41.0, 450.5, 61.0, 60.0);

                surface.getButton (ButtonID.REC_ARM).setBounds (113.25, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.MUTE).setBounds (188.5, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.SOLO).setBounds (262.25, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.VOLUME).setBounds (335.75, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.PAN_SEND).setBounds (410.25, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.SENDS).setBounds (483.0, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.DEVICE).setBounds (559.25, 698.0, 61.0, 30.0);
                surface.getButton (ButtonID.STOP_CLIP).setBounds (632.75, 698.0, 61.0, 30.0);

                surface.getButton (ButtonID.ROW1_1).setBounds (113.25, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_2).setBounds (188.5, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_3).setBounds (262.25, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_4).setBounds (335.75, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_5).setBounds (410.25, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_6).setBounds (483.0, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_7).setBounds (559.25, 666.0, 61.0, 30.0);
                surface.getButton (ButtonID.ROW1_8).setBounds (632.75, 666.0, 61.0, 30.0);
            }
            else
            {
                // Pro Mk1
                surface.getButton (ButtonID.SHIFT).setBounds (41.0, 79.0, 61.0, 60.0);
                surface.getButton (ButtonID.USER).setBounds (632.75, 8.5, 61.0, 60.0);

                surface.getButton (ButtonID.METRONOME).setBounds (41.0, 150.75, 61.0, 60.0);
                surface.getButton (ButtonID.UNDO).setBounds (41.0, 226.75, 61.0, 60.0);
                surface.getButton (ButtonID.DELETE).setBounds (41.0, 299.75, 61.0, 60.0);
                surface.getButton (ButtonID.DUPLICATE).setBounds (41.0, 450.5, 61.0, 60.0);

                surface.getButton (ButtonID.REC_ARM).setBounds (113.25, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.TRACK).setBounds (188.5, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.MUTE).setBounds (262.25, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.SOLO).setBounds (335.75, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.VOLUME).setBounds (410.25, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.PAN_SEND).setBounds (483.0, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.SENDS).setBounds (559.25, 668.0, 61.0, 60.0);
                surface.getButton (ButtonID.STOP_CLIP).setBounds (632.75, 668.0, 61.0, 60.0);

                surface.getButton (ButtonID.SESSION).setBounds (410.25, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.NOTE).setBounds (483.0, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.DEVICE).setBounds (559.25, 8.5, 61.0, 60.0);

                surface.getButton (ButtonID.UP).setBounds (114.0, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.DOWN).setBounds (188.5, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.LEFT).setBounds (262.25, 8.5, 61.0, 60.0);
                surface.getButton (ButtonID.RIGHT).setBounds (335.75, 8.5, 61.0, 60.0);
            }
        }
        else
        {
            surface.getButton (ButtonID.SHIFT).setBounds (632.75 - offset, 8.5, 61.0, 60.0);

            surface.getButton (ButtonID.SESSION).setBounds (410.25 - offset, 8.5, 61.0, 60.0);
            surface.getButton (ButtonID.NOTE).setBounds (483.0 - offset, 8.5, 61.0, 60.0);
            surface.getButton (ButtonID.DEVICE).setBounds (559.25 - offset, 8.5, 61.0, 60.0);

            surface.getButton (ButtonID.UP).setBounds (114.0 - offset, 8.5, 61.0, 60.0);
            surface.getButton (ButtonID.DOWN).setBounds (188.5 - offset, 8.5, 61.0, 60.0);
            surface.getButton (ButtonID.LEFT).setBounds (262.25 - offset, 8.5, 61.0, 60.0);
            surface.getButton (ButtonID.RIGHT).setBounds (335.75 - offset, 8.5, 61.0, 60.0);
        }

        surface.getLight (OutputID.LED1).setBounds (707.0 - offset, 8.5, 61.0, 60.0);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (this.configuration.shouldStartWithSessionView () ? Views.SESSION : this.configuration.getPreferredNoteView ());
        surface.getModeManager ().setActive (Modes.DUMMY);
    }


    protected void updateIndication ()
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        final boolean isVolume = viewManager.isActive (Views.TRACK_VOLUME);
        final boolean isPan = viewManager.isActive (Views.TRACK_PAN);
        final boolean isSends = viewManager.isActive (Views.TRACK_SENDS);
        final boolean isDevice = viewManager.isActive (Views.DEVICE);

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IView view = viewManager.getActive ();
        final int selSend = view instanceof final SendsView sendsView ? sendsView.getSelectedSend () : -1;
        final boolean isSession = view instanceof SessionView && !isVolume && !isPan && !isSends;

        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && isVolume);
            track.setPanIndication (!isEffect && isPan);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 8; j++)
                sendBank.getItem (j).setIndication (!isEffect && isSends && selSend == j);

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect && isVolume);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            parameterBank.getItem (i).setIndication (isDevice);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected)
            return;

        // Do not restore preferred play view view if certain views are active
        if (!this.getSurface ().getViewManager ().isActive (Views.MIX, Views.USER, Views.TRACK_PAN, Views.TRACK_VOLUME, Views.TRACK_SENDS, Views.DEVICE))
            super.handleTrackChange (isSelected);
    }


    private static int getStateColor (final LaunchpadControlSurface surface, final ButtonID buttonID)
    {
        if (surface.isShiftPressed ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING;
        return surface.isPressed (buttonID) ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
    }


    private static int get2StateColor (final LaunchpadControlSurface surface, final ButtonID buttonID)
    {
        final boolean isPressed = surface.isPressed (buttonID);
        if (surface.isShiftPressed ())
            return isPressed ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING;
        return isPressed ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
    }


    private int getDeleteStateColor (final LaunchpadControlSurface surface)
    {
        if (surface.isShiftPressed ())
            return this.model.getTransport ().isLoop () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO;
        return surface.isPressed (ButtonID.DELETE) ? LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_MAGENTA_LO;
    }


    private static int getDuplicateStateColor (final LaunchpadControlSurface surface)
    {
        if (surface.isShiftPressed ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_ORCHID;
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE;
    }


    private int getPlayStateColor (final LaunchpadControlSurface surface)
    {
        if (surface.isShiftPressed ())
            return surface.isPressed (ButtonID.PLAY) ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING;
        return this.model.getTransport ().isPlaying () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
    }


    private int getViewStateColor (final int viewColor, final Views... views)
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        for (final Views view: views)
        {
            if (viewManager.isActive (view))
                return viewColor;
        }
        return LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
    }


    private int getTrackModeColorIndex (final int index)
    {
        final LaunchpadControlSurface surface = this.getSurface ();

        if (surface.isPressed (ButtonID.NEW))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ModeManager modeManager = surface.getModeManager ();

        final ITrack track = tb.getItem (index);

        if (!track.doesExist ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;

        final Modes activeID = modeManager.getActiveID ();
        if (activeID == null)
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        switch (activeID)
        {
            case REC_ARM:
                return track.isRecArm () ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO;

            case TRACK_SELECT:
                return track.isSelected () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;

            case MUTE:
                return track.isMute () ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW_LO;

            case SOLO:
                return track.isSolo () ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_LO;

            case STOP_CLIP:
                return surface.isPressed (ButtonID.get (ButtonID.PAD1, index)) ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED : LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE;

            default:
                return this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ()));
        }
    }


    private int getModeColorIndex (final ButtonID buttonID)
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = surface.getViewManager ();

        switch (buttonID)
        {
            case REC_ARM:
                return modeManager.isActive (Modes.REC_ARM) ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case TRACK:
                return modeManager.isActive (Modes.TRACK_SELECT) ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case MUTE:
                return modeManager.isActive (Modes.MUTE) ? LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case SOLO:
                // Pro Mk3 -> color Click
                if (this.definition.hasTrackSelectionButtons () && surface.isShiftPressed ())
                    return this.model.getTransport ().isMetronomeOn () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
                return modeManager.isActive (Modes.SOLO) ? LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case VOLUME:
                return viewManager.isActive (Views.TRACK_VOLUME) ? LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case PAN_SEND:
                return viewManager.isActive (Views.TRACK_PAN) ? LaunchpadColorManager.LAUNCHPAD_COLOR_SKY : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case SENDS:
                return viewManager.isActive (Views.TRACK_SENDS) ? LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case STOP_CLIP:
                return modeManager.isActive (Modes.STOP_CLIP) ? LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            default:
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        }
    }


    private int getViewColor ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        switch (surface.getViewManager ().getActiveID ())
        {
            case SESSION, TRACK_VOLUME, TRACK_PAN, TRACK_SENDS, MIX:
                return LaunchpadColorManager.COLOR_VIEW_MIXER;

            case PLAY, CHORDS, PIANO, DRUM64:
                return LaunchpadColorManager.COLOR_VIEW_PLAY;

            case SEQUENCER, POLY_SEQUENCER, RAINDROPS:
                return LaunchpadColorManager.COLOR_VIEW_NOTE_SEQUENCER;

            case DRUM, DRUM4, DRUM8:
                return LaunchpadColorManager.COLOR_VIEW_DRUM_SEQUENCER;

            case DEVICE, BROWSER:
                return LaunchpadColorManager.COLOR_VIEW_DEVICE;

            case CLIP_LENGTH:
                return LaunchpadColorManager.COLOR_VIEW_CLIP;

            case PROJECT:
                return LaunchpadColorManager.COLOR_VIEW_PROJECT;

            case SHIFT:
                return LaunchpadColorManager.COLOR_VIEW_SHIFT;

            case TEMPO:
                return LaunchpadColorManager.COLOR_VIEW_TEMPO;

            case SHUFFLE:
                return LaunchpadColorManager.COLOR_VIEW_SHUFFLE;

            case USER:
                return LaunchpadColorManager.COLOR_VIEW_USER;

            default:
                return LaunchpadColorManager.COLOR_VIEW_OFF;
        }
    }


    private void handleTrackSelection (final ButtonEvent event, final int index)
    {
        final IMode active = this.getSurface ().getModeManager ().getActive ();
        if (active != null)
            active.onButton (0, index, event);
    }
}
