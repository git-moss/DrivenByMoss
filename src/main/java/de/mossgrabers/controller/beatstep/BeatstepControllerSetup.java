// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep;

import de.mossgrabers.controller.beatstep.command.continuous.KnobRowViewCommand;
import de.mossgrabers.controller.beatstep.command.trigger.StepCommand;
import de.mossgrabers.controller.beatstep.controller.BeatstepColors;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.controller.beatstep.view.BrowserView;
import de.mossgrabers.controller.beatstep.view.DeviceView;
import de.mossgrabers.controller.beatstep.view.DrumView;
import de.mossgrabers.controller.beatstep.view.PlayView;
import de.mossgrabers.controller.beatstep.view.SequencerView;
import de.mossgrabers.controller.beatstep.view.SessionView;
import de.mossgrabers.controller.beatstep.view.ShiftView;
import de.mossgrabers.controller.beatstep.view.TrackView;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractPlayViewCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.Relative3ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Arturia Beatstep and Beatstep Pro controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerSetup extends AbstractControllerSetup<BeatstepControlSurface, BeatstepConfiguration>
{
    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        0,  1,  2,  3,  4,  5,  6,  7,
        8,  9, 10, 11, 12, 13, 14, 15,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on

    private final boolean       isPro;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     * @param isPro True if Beatstep Pro
     */
    public BeatstepControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings, final boolean isPro)
    {
        super (factory, host, settings);

        this.isPro = isPro;
        this.colorManager = new ColorManager ();
        BeatstepColors.addColors (this.colorManager);
        this.valueChanger = new Relative3ValueChanger (128, 1, 0.5);
        this.configuration = new BeatstepConfiguration (host, this.valueChanger, isPro);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
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
        final ModelSetup ms = new ModelSetup ();
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        this.model.getTrackBank ().addSelectionObserver ( (index, value) -> this.handleTrackChange (value));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Control", "82????", "92????", "A2????", "B2????");

        // Sequencer 1 is on channel 1
        final INoteInput seqNoteInput = input.createNoteInput ("Seq. 1", "90????", "80????");
        if (!this.isPro)
        {
            final Integer [] table = new Integer [128];
            for (int i = 0; i < 128; i++)
            {
                // Block the Shift key
                table[i] = Integer.valueOf (i == 7 ? -1 : i);
            }
            seqNoteInput.setKeyTranslationTable (table);
        }

        // Setup the 2 note sequencers and 1 drum sequencer
        if (this.isPro)
        {
            // Sequencer 2 is on channel 2
            input.createNoteInput ("Seq. 2", "91????", "81????");
            // Drum Sequencer is on channel 10
            input.createNoteInput ("Drums", "99????", "89????");
        }

        final BeatstepControlSurface surface = new BeatstepControlSurface (this.host, this.colorManager, this.configuration, output, input, this.isPro);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.getSurface ().getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateIndication (null));
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_TRACK, new TrackView (surface, this.model));
        viewManager.registerView (Views.VIEW_DEVICE, new DeviceView (surface, this.model));
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));

        if (this.host.hasClips ())
        {
            viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
            viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
            viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
        }

        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        if (!this.isPro)
            return;

        final BeatstepControlSurface surface = this.getSurface ();
        for (int i = 0; i < 16; i++)
            this.addTriggerCommand (TriggerCommandID.get (TriggerCommandID.ROW1_1, i), BeatstepControlSurface.BEATSTEP_PRO_STEP1 + i, new StepCommand (i, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        for (int i = 0; i < 8; i++)
        {
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.KNOB1, i), BeatstepControlSurface.BEATSTEP_KNOB_1 + i, new KnobRowViewCommand (i, this.model, surface));
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.DEVICE_KNOB1, i), BeatstepControlSurface.BEATSTEP_KNOB_9 + i, new KnobRowViewCommand (i + 8, this.model, surface));
        }
        this.addContinuousCommand (ContinuousCommandID.MASTER_KNOB, BeatstepControlSurface.BEATSTEP_KNOB_MAIN, new PlayPositionCommand<> (this.model, surface));
        final PlayView playView = (PlayView) viewManager.getView (Views.VIEW_PLAY);
        playView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (playView, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Enable Shift button to send Midi Note 07
        final BeatstepControlSurface surface = this.getSurface ();
        surface.getOutput ().sendSysex ("F0 00 20 6B 7F 42 02 00 01 5E 09 F7");
        surface.getViewManager ().setActiveView (Views.VIEW_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final boolean isTrack = viewManager.isActiveView (Views.VIEW_TRACK);
        final boolean isDevice = viewManager.isActiveView (Views.VIEW_DEVICE);
        final boolean isSession = viewManager.isActiveView (Views.VIEW_SESSION);

        final IMasterTrack mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (!isDevice);

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && hasTrackSel && !isDevice);
            track.setPanIndication (!isEffect && hasTrackSel && !isDevice);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 6; j++)
                sendBank.getItem (j).setIndication (!isEffect && hasTrackSel && isTrack);

            if (tbe != null)
            {
                final ITrack selectedFXTrack = tbe.getSelectedItem ();
                final boolean hasFXTrackSel = selectedFXTrack != null && selectedFXTrack.getIndex () == i;
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect && hasFXTrackSel && isTrack);
                fxTrack.setPanIndication (isEffect && hasFXTrackSel && isTrack);
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

        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
