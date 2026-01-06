// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisDeviceParameterModeSelectCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisProjectParameterModeSelectCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisRepeatCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisSessionCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisTrackModeSelectionCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.command.trigger.ExquisTrackParameterModeSelectCommand;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisColorManager;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisPadGrid;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ISysexCallback;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisArpeggiatorMode;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisParameterMode;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisProjectTrackParameterMode;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisTrackMode;
import de.mossgrabers.controller.intuitiveinstruments.exquis.mode.ExquisVolumeMode;
import de.mossgrabers.controller.intuitiveinstruments.exquis.view.ExquisNoteView;
import de.mossgrabers.controller.intuitiveinstruments.exquis.view.ExquisSelectionView;
import de.mossgrabers.controller.intuitiveinstruments.exquis.view.ExquisSessionView;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.application.RedoCommand;
import de.mossgrabers.framework.command.trigger.application.UndoCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.OffsetBinaryRelativeValueChanger;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Intuitive Instruments Exquis controller.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisControllerSetup extends AbstractControllerSetup<ExquisControlSurface, ExquisConfiguration> implements ISysexCallback
{
    private static final int            MIDI_CHANNEL          = 15;

    protected boolean                   isMoveTracks;
    private IHwAbsoluteKnob             touchstrip;
    private final Map<Integer, byte []> trackSettings         = new HashMap<> ();
    private int                         previousTrackPosition = -1;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public ExquisControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ExquisColorManager ();
        this.valueChanger = new OffsetBinaryRelativeValueChanger (128, 1);
        this.configuration = new ExquisConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();

        ms.enableMainDrumDevice (false);

        ms.setNumTracks (4);
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);
        ms.setNumSends (2);
        ms.setNumScenes (7);

        ms.setNumDevicesInBank (22);
        ms.setNumParamPages (22);

        // Not used
        ms.setNumFilterColumnEntries (0);
        ms.setNumResults (0);
        ms.setNumDeviceLayers (0);
        ms.setNumDrumPadLayers (0);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (index, isSelected));
        trackBank.addPageObserver ( () -> this.handleTrackChange (-1, true));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();

        // Route all MIDI channels directly to the DAW except channel 16 which is used for
        // knob/button communication
        final List<String> filters = new ArrayList<> ();
        for (int i = 0; i < 15; i++)
        {
            final String midiChannel = Integer.toHexString (i).toUpperCase (Locale.US);
            Collections.addAll (filters, "8" + midiChannel + "????", "9" + midiChannel + "????", "B" + midiChannel + "40??", "B" + midiChannel + "4A??", "A" + midiChannel + "????", "D" + midiChannel + "????", "E" + midiChannel + "????");
        }
        final IMidiInput input = midiAccess.createInput ("Exquis", filters.toArray (new String [filters.size ()]));
        final INoteInput noteInput = input.getDefaultNoteInput ();
        noteInput.enableMPE (true);

        final IMidiOutput output = midiAccess.createOutput ();
        final IPadGrid padGrid = new ExquisPadGrid (this.colorManager, output);
        final ExquisControlSurface surface = new ExquisControlSurface (this.host, this.colorManager, this.configuration, output, input, padGrid, this);
        this.surfaces.add (surface);

        surface.getModeManager ().setDefaultID (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final ExquisControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.TRACK, new ExquisTrackMode (surface, this.model));
        modeManager.register (Modes.VOLUME, new ExquisVolumeMode (surface, this.model));
        modeManager.register (Modes.PROJECT_PARAMETERS, new ExquisProjectTrackParameterMode (surface, this.model, true));
        modeManager.register (Modes.TRACK_PARAMETERS, new ExquisProjectTrackParameterMode (surface, this.model, false));
        modeManager.register (Modes.DEVICE_PARAMS, new ExquisParameterMode (surface, this.model));
        modeManager.register (Modes.REPEAT_NOTE, new ExquisArpeggiatorMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ExquisControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.PLAY, new ExquisNoteView (surface, this.model));
        viewManager.register (Views.TRACK_SELECT, new ExquisSelectionView (surface, this.model));
        viewManager.register (Views.SESSION, new ExquisSessionView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final ExquisControlSurface surface = this.getSurface ();
        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.configuration.addSettingObserver (AbstractConfiguration.MPE_PITCHBEND_RANGE, () -> surface.scheduleTask ( () -> {

            final INoteInput input = surface.getMidiInput ().getDefaultNoteInput ();
            final IMidiOutput output = surface.getMidiOutput ();
            if (input == null || output == null)
                return;
            final int mpePitchBendRange = this.configuration.getMPEPitchBendRange ();
            input.setMPEPitchBendSensitivity (mpePitchBendRange);

        }, 2000));

        this.configuration.addSettingObserver (AbstractConfiguration.SCALES_SCALE, () -> {
            final String scaleName = this.configuration.getScale ();
            surface.updateScale (AbstractConfiguration.lookupIndex (ExquisConfiguration.EXQUISE_SCALES, scaleName));
        });

        this.configuration.addSettingObserver (AbstractConfiguration.SCALES_BASE, () -> {
            this.scales.setScaleOffsetByName (this.configuration.getScaleBase ());
            surface.updateRootNote (this.scales.getScaleOffset ());
        });

        this.createNoteRepeatObservers (this.configuration, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final ExquisControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();
        final IApplication application = this.model.getApplication ();
        final ViewManager viewManager = surface.getViewManager ();

        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_PLAY_STOP, () -> t.isPlaying () ? 1 : 0, ExquisColorManager.PLAY_OFF, ExquisColorManager.PLAY_ON);
        this.addButton (ButtonID.RECORD, "Record", new RecordCommand<> (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_RECORD, () -> t.isRecording () ? 1 : 0, ExquisColorManager.RECORD_OFF, ExquisColorManager.RECORD_ON);
        this.addButton (ButtonID.LOOP, "Loop", new ExquisRepeatCommand (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_REPEAT, () -> t.isLoop () ? 1 : 0, ExquisColorManager.LOOP_OFF, ExquisColorManager.LOOP_ON);
        this.addButton (ButtonID.SESSION, "Session", new ExquisSessionCommand (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_SESSION, () -> viewManager.isActive (Views.SESSION) ? 1 : 0, ExquisColorManager.SESSION_OFF, ExquisColorManager.SESSION_ON);

        this.addButton (ButtonID.UNDO, "Undo", new UndoCommand<> (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_LEFT, () -> application.canUndo () ? 1 : 0, ExquisColorManager.DO_OFF, ExquisColorManager.DO_ON);
        this.addButton (ButtonID.REDO, "Redo", new RedoCommand<> (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_RIGHT, () -> application.canRedo () ? 1 : 0, ExquisColorManager.DO_OFF, ExquisColorManager.DO_ON);

        this.addButton (ButtonID.KNOB1_TOUCH, "Knob 1 Press", new ExquisProjectParameterModeSelectCommand (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_KNOB1);
        this.addButton (ButtonID.KNOB2_TOUCH, "Knob 2 Press", new ExquisTrackParameterModeSelectCommand (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_KNOB2);
        this.addButton (ButtonID.KNOB3_TOUCH, "Knob 3 Press", new ExquisDeviceParameterModeSelectCommand (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_KNOB3);
        this.addButton (ButtonID.KNOB4_TOUCH, "Knob 4 Press", new ExquisTrackModeSelectionCommand (this.model, surface), MIDI_CHANNEL, ExquisControlSurface.BUTTON_KNOB4);

        this.addButton (ButtonID.DOWN, "Down", (event, velocity) -> this.setScrollMode (event, false), MIDI_CHANNEL, ExquisControlSurface.BUTTON_DOWN, () -> 0, AbstractSessionView.COLOR_SCENE);
        this.addButton (ButtonID.UP, "Up", (event, velocity) -> this.setScrollMode (event, true), MIDI_CHANNEL, ExquisControlSurface.BUTTON_UP, () -> 0, ExquisColorManager.TRACKS_COLOR);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final ExquisControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        for (int knobIndex = 0; knobIndex < 4; knobIndex++)
        {
            final ContinuousID knobID = ContinuousID.get (ContinuousID.KNOB1, knobIndex);
            final int cc = ExquisControlSurface.FIRST_KNOB + knobIndex;
            final IHwRelativeKnob relativeKnob = this.addRelativeKnob (knobID, "Knob " + (knobIndex + 1), new KnobRowModeCommand<> (knobIndex, this.model, surface), BindType.CC, MIDI_CHANNEL, cc, RelativeEncoding.OFFSET_BINARY);
            relativeKnob.setIndexInGroup (knobIndex);

            final int ki = knobIndex;

            relativeKnob.addOutput ( () -> {

                final IMode mode = modeManager.getActive ();
                return mode == null ? 0 : Math.max (0, mode.getKnobValue (ki));

            }, value -> {

                final IMode mode = modeManager.getActive ();
                if (mode == null)
                    return;
                final ColorEx color = this.colorManager.getColor (mode.getKnobColor (ki), null);
                final double factor = this.valueChanger.toNormalizedValue (value);
                surface.setLED (cc, color.dim (factor));

            });
        }

        this.touchstrip = this.addAbsoluteKnob (ContinuousID.TOUCHSTRIP, "Touchstrip", this::selectBankPage, BindType.CC, MIDI_CHANNEL, ExquisControlSurface.TOUCHSTRIP);
        this.touchstrip.addOutput ( () -> this.isMoveTracks ? this.model.getTrackBank ().getScrollPosition () / 4 : this.model.getSceneBank ().getScrollPosition () / 7, value -> {

            final ColorEx hiColor = this.isMoveTracks ? ColorEx.BLUE : ColorEx.GREEN;
            for (int i = 0; i < 6; i++)
                surface.setLED (ExquisControlSurface.TOUCHSTRIP_P1 + i, i == value ? hiColor : ColorEx.BLACK);

        });
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final ExquisControlSurface surface = this.getSurface ();

        surface.getViewManager ().setActive (Views.PLAY);
        surface.getModeManager ().setActive (Modes.TRACK);

        this.host.scheduleTask (surface::forceFlush, 1000);
    }


    /** {@inheritDoc} */
    @Override
    public void updateTempo (final int tempo)
    {
        final ITransport transport = this.model.getTransport ();
        transport.setTempo (tempo);
        this.host.showNotification ("Tempo: " + transport.formatTempo (tempo));
    }


    /** {@inheritDoc} */
    @Override
    public void storeTrackSettings (final int trackPosition, final byte [] settings)
    {
        final int cursorTrackPosition = this.model.getCursorTrack ().getPosition ();
        final boolean doesntNeedUpdate = trackPosition < 0;
        final int position = doesntNeedUpdate ? cursorTrackPosition : trackPosition;
        if (position >= 0)
            this.trackSettings.put (Integer.valueOf (position), settings);

        if (doesntNeedUpdate)
            return;

        final byte [] trackSettings = this.trackSettings.get (Integer.valueOf (cursorTrackPosition));
        if (trackSettings != null)
            this.getSurface ().sendTrackSettings (trackSettings);
    }


    protected void handleTrackChange (final int index, final boolean isSelected)
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        if (isSelected)
        {
            // The bank page has already changed when the deactivated track is reported, therefore
            // we need the workaround via the previous track position

            if (this.previousTrackPosition >= 0)
                this.getSurface ().requestTrackSettings (this.previousTrackPosition);
            final ITrack item = index >= 0 ? trackBank.getItem (index) : this.model.getCursorTrack ();
            this.previousTrackPosition = item.doesExist () ? item.getPosition () : -1;
        }

        this.handleTrackChange (isSelected);
    }


    /** {@inheritDoc} */
    @Override
    protected void recallLastView ()
    {
        final ExquisControlSurface surface = this.getSurface ();
        if (!surface.getViewManager ().isActive (Views.SESSION, Views.TRACK_SELECT))
            surface.recallPreferredView (this.model.getCursorTrack ());
    }


    private void selectBankPage (final int value)
    {
        if (value < 0 || value > 5)
            return;

        if (this.isMoveTracks)
            this.model.getTrackBank ().scrollTo (4 * value);
        else
            this.model.getSceneBank ().scrollTo (7 * value);
    }


    private void setScrollMode (final ButtonEvent event, final boolean isMoveTracks)
    {
        if (event == ButtonEvent.DOWN)
            return;

        this.isMoveTracks = isMoveTracks;
        this.host.showNotification (this.isMoveTracks ? "Scroll: Tracks" : "Scroll: Scenes");
        this.touchstrip.forceFlush ();
    }
}
