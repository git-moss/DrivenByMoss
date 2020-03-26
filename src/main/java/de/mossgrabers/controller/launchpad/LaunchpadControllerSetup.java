// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad;

import de.mossgrabers.controller.launchpad.command.continuous.FaderCommand;
import de.mossgrabers.controller.launchpad.command.trigger.ClickCommand;
import de.mossgrabers.controller.launchpad.command.trigger.LaunchpadCursorCommand;
import de.mossgrabers.controller.launchpad.command.trigger.LaunchpadDuplicateCommand;
import de.mossgrabers.controller.launchpad.command.trigger.MuteCommand;
import de.mossgrabers.controller.launchpad.command.trigger.PanCommand;
import de.mossgrabers.controller.launchpad.command.trigger.PlayAndNewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.RecordArmCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SelectDeviceViewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SelectNoteViewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SelectSessionViewCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SendsCommand;
import de.mossgrabers.controller.launchpad.command.trigger.ShiftCommand;
import de.mossgrabers.controller.launchpad.command.trigger.SoloCommand;
import de.mossgrabers.controller.launchpad.command.trigger.StopClipCommand;
import de.mossgrabers.controller.launchpad.command.trigger.TrackSelectCommand;
import de.mossgrabers.controller.launchpad.command.trigger.VolumeCommand;
import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.controller.LaunchpadScales;
import de.mossgrabers.controller.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.controller.launchpad.definition.LaunchpadProControllerDefinition;
import de.mossgrabers.controller.launchpad.mode.RecArmMode;
import de.mossgrabers.controller.launchpad.mode.SendMode;
import de.mossgrabers.controller.launchpad.mode.StopClipMode;
import de.mossgrabers.controller.launchpad.mode.TrackMode;
import de.mossgrabers.controller.launchpad.view.BrowserView;
import de.mossgrabers.controller.launchpad.view.DeviceView;
import de.mossgrabers.controller.launchpad.view.DrumView;
import de.mossgrabers.controller.launchpad.view.DrumView4;
import de.mossgrabers.controller.launchpad.view.DrumView64;
import de.mossgrabers.controller.launchpad.view.DrumView8;
import de.mossgrabers.controller.launchpad.view.NoteViewSelectView;
import de.mossgrabers.controller.launchpad.view.PanView;
import de.mossgrabers.controller.launchpad.view.PianoView;
import de.mossgrabers.controller.launchpad.view.PlayView;
import de.mossgrabers.controller.launchpad.view.PolySequencerView;
import de.mossgrabers.controller.launchpad.view.RaindropsView;
import de.mossgrabers.controller.launchpad.view.SendsView;
import de.mossgrabers.controller.launchpad.view.SequencerView;
import de.mossgrabers.controller.launchpad.view.SessionView;
import de.mossgrabers.controller.launchpad.view.ShiftView;
import de.mossgrabers.controller.launchpad.view.UserView;
import de.mossgrabers.controller.launchpad.view.VolumeView;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractViewCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.MuteMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SoloMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;

import java.util.Map;
import java.util.Map.Entry;


/**
 * Support for several Novation Launchpad controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadControllerSetup extends AbstractControllerSetup<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private final ILaunchpadControllerDefinition definition;


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
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
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
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
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
    protected void createObservers ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();

        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> {

            surface.getLight (OutputID.LED1).clearCache ();
            this.updateIndication (null);

        });

        this.createScaleObservers (this.configuration);
        this.createNoteRepeatObservers (this.configuration, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.registerMode (Modes.REC_ARM, new RecArmMode (surface, this.model));
        modeManager.registerMode (Modes.TRACK_SELECT, new TrackMode (surface, this.model));
        modeManager.registerMode (Modes.MUTE, new MuteMode<> (surface, this.model));
        modeManager.registerMode (Modes.SOLO, new SoloMode<> (surface, this.model));
        modeManager.registerMode (Modes.VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.PAN, new PanMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.SEND, new SendMode (surface, this.model));
        modeManager.registerMode (Modes.STOP_CLIP, new StopClipMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.BROWSER, new BrowserView (surface, this.model));
        viewManager.registerView (Views.DEVICE, new DeviceView (surface, this.model));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.DRUM4, new DrumView4 (surface, this.model));
        viewManager.registerView (Views.DRUM8, new DrumView8 (surface, this.model));
        viewManager.registerView (Views.TRACK_PAN, new PanView (surface, this.model));
        viewManager.registerView (Views.DRUM64, new DrumView64 (surface, this.model));
        viewManager.registerView (Views.PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.PIANO, new PianoView (surface, this.model));
        viewManager.registerView (Views.RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.TRACK_SENDS, new SendsView (surface, this.model));
        viewManager.registerView (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.POLY_SEQUENCER, new PolySequencerView (surface, this.model, true));
        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.TRACK_VOLUME, new VolumeView (surface, this.model));
        viewManager.registerView (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.registerView (Views.CONTROL, new NoteViewSelectView (surface, this.model));
        if (this.definition.isPro () && this.host.hasUserParameters ())
            viewManager.registerView (Views.USER, new UserView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final ITransport transport = this.model.getTransport ();

        final Map<ButtonID, Integer> buttonIDs = this.definition.getButtonIDs ();

        this.addButton (ButtonID.SHIFT, "Shift", new ShiftCommand (this.model, surface), buttonIDs.get (ButtonID.SHIFT).intValue (), () -> surface.isShiftPressed () ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);

        final LaunchpadCursorCommand commandUp = new LaunchpadCursorCommand (Direction.UP, this.model, surface);
        this.addButton (ButtonID.UP, "Up", commandUp, buttonIDs.get (ButtonID.UP).intValue (), () -> commandUp.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        final LaunchpadCursorCommand commandDown = new LaunchpadCursorCommand (Direction.DOWN, this.model, surface);
        this.addButton (ButtonID.DOWN, "Down", commandDown, buttonIDs.get (ButtonID.DOWN).intValue (), () -> commandDown.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        final LaunchpadCursorCommand commandLeft = new LaunchpadCursorCommand (Direction.LEFT, this.model, surface);
        this.addButton (ButtonID.LEFT, "Left", commandLeft, buttonIDs.get (ButtonID.LEFT).intValue (), () -> commandLeft.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        final LaunchpadCursorCommand commandRight = new LaunchpadCursorCommand (Direction.RIGHT, this.model, surface);
        this.addButton (ButtonID.RIGHT, "Right", commandRight, buttonIDs.get (ButtonID.RIGHT).intValue (), () -> commandRight.canScroll () ? this.getViewColor () : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);

        this.addButton (ButtonID.SESSION, "Session", new SelectSessionViewCommand (this.model, surface), buttonIDs.get (ButtonID.SESSION).intValue (), () -> {
            if (viewManager.isActiveView (Views.SESSION))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_LIME;
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
        });
        this.addButton (ButtonID.NOTE, "Note", new SelectNoteViewCommand (this.model, surface), buttonIDs.get (ButtonID.NOTE).intValue (), () -> {

            if (viewManager.isActiveView (Views.DRUM))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW;

            if (viewManager.isActiveView (Views.SEQUENCER))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE;

            if (viewManager.isActiveView (Views.POLY_SEQUENCER))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_ORCHID;

            if (viewManager.isActiveView (Views.RAINDROPS))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;

            if (Views.isNoteView (viewManager.getActiveViewId ()))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI;

            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;

        });
        this.addButton (ButtonID.DEVICE, "Device", new SelectDeviceViewCommand (this.model, surface), buttonIDs.get (ButtonID.DEVICE).intValue (), () -> {

            if (viewManager.isActiveView (Views.BROWSER))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE;
            if (viewManager.isActiveView (Views.DEVICE))
                return LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER;
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;

        });
        if (this.definition.isPro ())
        {
            if (this.host.hasUserParameters ())
                this.addButton (ButtonID.USER, "User", new ViewMultiSelectCommand<> (this.model, surface, true, Views.USER), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_USER, () -> viewManager.isActiveView (Views.USER) ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO);
            else
                this.addButton (ButtonID.USER, "User", NopCommand.INSTANCE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_USER, () -> LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
        }

        // The following buttons are only available on the Pro but the commands are used by all
        // Launchpad models!
        this.addButton (ButtonID.METRONOME, "Metronome", new ClickCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_CLICK, () -> {
            if (surface.isShiftPressed ())
                return surface.isPressed (ButtonID.METRONOME) ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING;
            return transport.isMetronomeOn () ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
        });
        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_UNDO, () -> get2StateColor (surface, ButtonID.UNDO));
        this.addButton (ButtonID.DELETE, "Delete", NopCommand.INSTANCE, LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DELETE, () -> getStateColor (surface, ButtonID.DELETE));
        this.addButton (ButtonID.QUANTIZE, "Quantize", new QuantizeCommand<> (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_QUANTIZE, () -> getStateColor (surface, ButtonID.QUANTIZE));
        this.addButton (ButtonID.DUPLICATE, "Duplicate", new LaunchpadDuplicateCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DUPLICATE, () -> get2StateColor (surface, ButtonID.DUPLICATE));
        this.addButton (ButtonID.DOUBLE, "Double", new PlayAndNewCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_DOUBLE, () -> get2StateColor (surface, ButtonID.DOUBLE));
        this.addButton (ButtonID.RECORD, "Record", new RecordCommand<> (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_RECORD, () -> {
            final boolean isShift = surface.isShiftPressed ();
            final boolean flipRecord = surface.getConfiguration ().isFlipRecord ();
            if (isShift && !flipRecord || !isShift && flipRecord)
                return transport.isLauncherOverdub () ? LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_AMBER;
            return transport.isRecording () ? LaunchpadColorManager.LAUNCHPAD_COLOR_RED_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_RED_LO;
        });
        this.addButton (ButtonID.REC_ARM, "Rec Arm", new RecordArmCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_REC_ARM, () -> this.getModeColorIndex (ButtonID.REC_ARM));
        this.addButton (ButtonID.TRACK, "Track", new TrackSelectCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_TRACK, () -> this.getModeColorIndex (ButtonID.TRACK));
        this.addButton (ButtonID.MUTE, "Mute", new MuteCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_MUTE, () -> this.getModeColorIndex (ButtonID.MUTE));
        this.addButton (ButtonID.SOLO, "Solo", new SoloCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_SOLO, () -> this.getModeColorIndex (ButtonID.SOLO));
        this.addButton (ButtonID.VOLUME, "Volume", new VolumeCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_VOLUME, () -> this.getModeColorIndex (ButtonID.VOLUME));
        this.addButton (ButtonID.PAN_SEND, "Panorama", new PanCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_PAN, () -> this.getModeColorIndex (ButtonID.PAN_SEND));
        this.addButton (ButtonID.SENDS, "Sends", new SendsCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_SENDS, () -> this.getModeColorIndex (ButtonID.SENDS));
        this.addButton (ButtonID.STOP_CLIP, "Stop Clip", new StopClipCommand (this.model, surface), LaunchpadProControllerDefinition.LAUNCHPAD_BUTTON_STOP_CLIP, () -> this.getModeColorIndex (ButtonID.STOP_CLIP));

        for (int i = 0; i < 8; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (buttonID, "Scene " + (i + 1), new ViewButtonCommand<> (buttonID, this.model, surface), buttonIDs.get (buttonID).intValue (), () -> this.getViewColor (buttonID));
        }

        // Update the front or logo LED with the color of the current track

        surface.createLight (OutputID.LED1, () -> {

            final ITrack track = this.model.getSelectedTrack ();
            return track != null && track.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorIndex (track.getColor ())) : 0;

        }, color -> this.definition.setLogoColor (surface, color), state -> this.colorManager.getColor (state, null), null);

        for (final Entry<ButtonID, IHwButton> entry: surface.getButtons ().entrySet ())
        {
            final ButtonID key = entry.getKey ();
            final int keyValue = key.ordinal ();
            if (ButtonID.PAD1.ordinal () < keyValue || ButtonID.PAD64.ordinal () > keyValue)
                entry.getValue ().addEventHandler (ButtonEvent.UP, event -> surface.getButton (key).getLight ().clearCache ());
        }
    }


    private static int getStateColor (final LaunchpadControlSurface surface, final ButtonID buttonID)
    {
        if (surface.isShiftPressed ())
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
        return surface.isPressed (buttonID) ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
    }


    private static int get2StateColor (final LaunchpadControlSurface surface, final ButtonID buttonID)
    {
        if (surface.isShiftPressed ())
            return surface.isPressed (buttonID) ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_SPRING;
        return surface.isPressed (buttonID) ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN : LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO;
    }


    private int getModeColorIndex (final ButtonID buttonID)
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final ViewManager viewManager = surface.getViewManager ();

        final ITrack selTrack = this.model.getSelectedTrack ();
        final int index = selTrack == null ? -1 : selTrack.getIndex ();

        switch (buttonID)
        {
            case REC_ARM:
                if (modeManager.isActiveOrTempMode (Modes.REC_ARM))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_RED;
                return index == 0 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case TRACK:
                if (modeManager.isActiveOrTempMode (Modes.TRACK_SELECT))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
                return index == 1 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case MUTE:
                if (modeManager.isActiveOrTempMode (Modes.MUTE))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW;
                return index == 2 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case SOLO:
                if (modeManager.isActiveOrTempMode (Modes.SOLO))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE;
                return index == 3 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case VOLUME:
                if (viewManager.isActiveView (Views.TRACK_VOLUME))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_CYAN;
                return index == 4 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case PAN_SEND:
                if (viewManager.isActiveView (Views.TRACK_PAN))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_SKY;
                return index == 5 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case SENDS:
                if (viewManager.isActiveView (Views.TRACK_SENDS))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID;
                return index == 6 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            case STOP_CLIP:
                if (modeManager.isActiveOrTempMode (Modes.STOP_CLIP))
                    return LaunchpadColorManager.LAUNCHPAD_COLOR_ROSE;
                return index == 7 ? LaunchpadColorManager.LAUNCHPAD_COLOR_WHITE : LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;
            default:
                return 0;
        }
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
    @SuppressWarnings(
    {
        "rawtypes",
        "unchecked"
    })
    @Override
    protected void registerContinuousCommands ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new FaderCommand (i, this.model, surface), BindType.CC, LaunchpadControlSurface.LAUNCHPAD_FADER_1 + i);
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
            final AbstractView view = (AbstractView) viewManager.getView (viewID);
            view.registerAftertouchCommand (new AftertouchAbstractViewCommand (view, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (114.0, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD2).setBounds (188.5, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD3).setBounds (262.25, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD4).setBounds (335.75, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD5).setBounds (410.25, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD6).setBounds (483.0, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD7).setBounds (559.25, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD8).setBounds (632.75, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD9).setBounds (114.0, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD10).setBounds (188.5, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD11).setBounds (262.25, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD12).setBounds (335.75, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD13).setBounds (410.25, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD14).setBounds (483.0, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD15).setBounds (559.25, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD16).setBounds (632.75, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.PAD17).setBounds (114.0, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD18).setBounds (188.5, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD19).setBounds (262.25, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD20).setBounds (335.75, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD21).setBounds (410.25, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD22).setBounds (483.0, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD23).setBounds (559.25, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD24).setBounds (632.75, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.PAD25).setBounds (114.0, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD26).setBounds (188.5, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD27).setBounds (262.25, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD28).setBounds (335.75, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD29).setBounds (410.25, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD30).setBounds (483.0, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD31).setBounds (559.25, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD32).setBounds (632.75, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD33).setBounds (114.0, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD34).setBounds (188.5, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD35).setBounds (262.25, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD36).setBounds (335.75, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD37).setBounds (410.25, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD38).setBounds (483.0, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD39).setBounds (559.25, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD40).setBounds (632.75, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD41).setBounds (114.0, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD42).setBounds (188.5, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD43).setBounds (262.25, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD44).setBounds (335.75, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD45).setBounds (410.25, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD46).setBounds (483.0, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD47).setBounds (559.25, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD48).setBounds (632.75, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD49).setBounds (114.0, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD50).setBounds (188.5, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD51).setBounds (262.25, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD52).setBounds (335.75, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD53).setBounds (410.25, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD54).setBounds (483.0, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD55).setBounds (559.25, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD56).setBounds (632.75, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.PAD57).setBounds (114.0, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD58).setBounds (188.5, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD59).setBounds (262.25, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD60).setBounds (335.75, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD61).setBounds (410.25, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD62).setBounds (483.0, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD63).setBounds (559.25, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAD64).setBounds (632.75, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.SHIFT).setBounds (41.0, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.UP).setBounds (114.0, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.DOWN).setBounds (188.5, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.LEFT).setBounds (262.25, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.RIGHT).setBounds (335.75, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.SESSION).setBounds (410.25, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.NOTE).setBounds (483.0, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.DEVICE).setBounds (559.25, 8.5, 61.0, 60.0);
        if (this.definition.isPro ())
            surface.getButton (ButtonID.USER).setBounds (632.75, 8.5, 61.0, 60.0);
        surface.getButton (ButtonID.METRONOME).setBounds (41.0, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.UNDO).setBounds (41.0, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.DELETE).setBounds (41.0, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.QUANTIZE).setBounds (41.0, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.DUPLICATE).setBounds (41.0, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.DOUBLE).setBounds (41.0, 522.25, 61.0, 60.0);
        surface.getButton (ButtonID.RECORD).setBounds (41.0, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.REC_ARM).setBounds (113.25, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.TRACK).setBounds (188.5, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.MUTE).setBounds (262.25, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.SOLO).setBounds (335.75, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.VOLUME).setBounds (410.25, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.PAN_SEND).setBounds (483.0, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.SENDS).setBounds (559.25, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.STOP_CLIP).setBounds (632.75, 668.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE1).setBounds (707.0, 79.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE2).setBounds (707.0, 150.75, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE3).setBounds (707.0, 226.75, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE4).setBounds (707.0, 299.75, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE5).setBounds (707.0, 373.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE6).setBounds (707.0, 450.5, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE7).setBounds (707.0, 596.0, 61.0, 60.0);
        surface.getButton (ButtonID.SCENE8).setBounds (707.0, 522.25, 61.0, 60.0);

        surface.getContinuous (ContinuousID.FADER1).setBounds (113.25, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER2).setBounds (188.5, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER3).setBounds (262.25, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER4).setBounds (335.75, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER5).setBounds (410.25, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER6).setBounds (483.0, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER7).setBounds (559.25, 732.75, 60.0, 57.75);
        surface.getContinuous (ContinuousID.FADER8).setBounds (632.75, 732.75, 60.0, 57.75);

        surface.getLight (OutputID.LED1).setBounds (707.0, 8.5, 61.0, 60.0);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        this.getSurface ().getViewManager ().setActiveView (Views.PLAY);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        final boolean isVolume = viewManager.isActiveView (Views.TRACK_VOLUME);
        final boolean isPan = viewManager.isActiveView (Views.TRACK_PAN);
        final boolean isSends = viewManager.isActiveView (Views.TRACK_SENDS);
        final boolean isDevice = viewManager.isActiveView (Views.DEVICE);

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final View view = viewManager.getActiveView ();
        final int selSend = view instanceof SendsView ? ((SendsView) view).getSelectedSend () : -1;
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


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected)
            return;

        // Recall last used view (if we are not in session mode)
        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (!viewManager.isActiveView (Views.SESSION))
        {
            final ITrack selectedTrack = this.model.getSelectedTrack ();
            if (selectedTrack != null)
            {
                final Views preferredView = viewManager.getPreferredView (selectedTrack.getPosition ());
                viewManager.setActiveView (preferredView == null ? Views.PLAY : preferredView);
            }
        }

        if (viewManager.isActiveView (Views.PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.DRUM))
            viewManager.getView (Views.DRUM).updateNoteMapping ();
    }


    private int getViewColor ()
    {
        final LaunchpadControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        if (viewManager.isActiveView (Views.SESSION) || viewManager.isActiveView (Views.TRACK_VOLUME) || viewManager.isActiveView (Views.TRACK_PAN) || viewManager.isActiveView (Views.TRACK_SENDS))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_LIME;

        if (viewManager.isActiveView (Views.RAINDROPS))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;

        if (viewManager.isActiveView (Views.SEQUENCER))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE;

        if (viewManager.isActiveView (Views.POLY_SEQUENCER))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_ORCHID;

        if (viewManager.isActiveView (Views.DEVICE))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_AMBER;

        if (viewManager.isActiveView (Views.DRUM) || viewManager.isActiveView (Views.DRUM4) || viewManager.isActiveView (Views.DRUM8) || viewManager.isActiveView (Views.DRUM64))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW;

        if (viewManager.isActiveView (Views.BROWSER))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_TURQUOISE;

        // VIEW_PLAY, VIEW_PIANO, VIEW_SHIFT
        return LaunchpadColorManager.LAUNCHPAD_COLOR_OCEAN_HI;
    }
}
