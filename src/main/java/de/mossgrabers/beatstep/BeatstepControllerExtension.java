// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep;

import de.mossgrabers.beatstep.command.continuous.BeatstepPlayPositionCommand;
import de.mossgrabers.beatstep.command.continuous.KnobRowViewCommand;
import de.mossgrabers.beatstep.command.trigger.StepCommand;
import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.beatstep.controller.BeatstepMidiInput;
import de.mossgrabers.beatstep.controller.BeatstepValueChanger;
import de.mossgrabers.beatstep.view.BrowserView;
import de.mossgrabers.beatstep.view.DeviceView;
import de.mossgrabers.beatstep.view.DrumView;
import de.mossgrabers.beatstep.view.PlayView;
import de.mossgrabers.beatstep.view.SequencerView;
import de.mossgrabers.beatstep.view.SessionView;
import de.mossgrabers.beatstep.view.ShiftView;
import de.mossgrabers.beatstep.view.TrackView;
import de.mossgrabers.beatstep.view.Views;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractPlayViewCommand;
import de.mossgrabers.framework.controller.AbstractControllerExtension;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ViewManager;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the Arturia Beatstep and Beatstep Pro controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerExtension extends AbstractControllerExtension<BeatstepControlSurface, BeatstepConfiguration>
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

    private final boolean       isPro;


    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     * @param isPro True if Beatstep Pro
     */
    protected BeatstepControllerExtension (final BaseBeatstepControllerExtensionDefinition extensionDefinition, final ControllerHost host, final boolean isPro)
    {
        super (extensionDefinition, host);
        this.isPro = isPro;
        this.colorManager = new ColorManager ();
        BeatstepColors.addColors (this.colorManager);
        this.valueChanger = new BeatstepValueChanger (128, 1, 0.5);
        this.configuration = new BeatstepConfiguration (this.valueChanger, isPro);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.surface.flush ();
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
        this.model = new Model (this.getHost (), this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1);

        final TrackBankProxy trackBank = this.model.getTrackBank ();
        trackBank.addTrackSelectionObserver (this::handleTrackChange);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final ControllerHost host = this.getHost ();
        final MidiOutput output = new MidiOutput (host);
        final MidiInput input = new BeatstepMidiInput (this.isPro);
        this.surface = new BeatstepControlSurface (host, this.colorManager, this.configuration, output, input, this.isPro);
        this.surface.setDisplay (new DummyDisplay (host));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateIndication ());
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.registerView (Views.VIEW_TRACK, new TrackView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DEVICE, new DeviceView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_SESSION, new SessionView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (this.surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (this.surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        if (!this.isPro)
            return;

        for (int i = 0; i < 16; i++)
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW1_1.intValue () + i), BeatstepControlSurface.BEATSTEP_PRO_STEP1 + i, new StepCommand (i, this.model, this.surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < 8; i++)
        {
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), BeatstepControlSurface.BEATSTEP_KNOB_1 + i, new KnobRowViewCommand (i, this.model, this.surface));
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_DEVICE_KNOB1.intValue () + i), BeatstepControlSurface.BEATSTEP_KNOB_9 + i, new KnobRowViewCommand (i + 8, this.model, this.surface));
        }
        this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB, BeatstepControlSurface.BEATSTEP_KNOB_MAIN, new BeatstepPlayPositionCommand (this.model, this.surface));
        final PlayView playView = (PlayView) viewManager.getView (Views.VIEW_PLAY);
        playView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (playView, this.model, this.surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        // Enable Shift button to send Midi Note 07
        this.surface.getOutput ().sendSysex ("F0 00 20 6B 7F 42 02 00 01 5E 09 F7");

        this.surface.scheduleTask ( () -> this.surface.getViewManager ().setActiveView (Views.VIEW_TRACK), 100);
    }


    private void updateIndication ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        final boolean isTrack = viewManager.isActiveView (Views.VIEW_TRACK);
        final boolean isDevice = viewManager.isActiveView (Views.VIEW_DEVICE);
        final boolean isSession = viewManager.isActiveView (Views.VIEW_SESSION);

        final MasterTrackProxy mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (!isDevice);

        final TrackBankProxy tb = this.model.getTrackBank ();
        final TrackData selectedTrack = tb.getSelectedTrack ();
        final EffectTrackBankProxy tbe = this.model.getEffectTrackBank ();
        final TrackData selectedFXTrack = tbe.getSelectedTrack ();
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        tbe.setIndication (isEffect && isSession);

        for (int i = 0; i < 8; i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;
            tb.setVolumeIndication (i, !isEffect && hasTrackSel && !isDevice);
            tb.setPanIndication (i, !isEffect && hasTrackSel && !isDevice);
            for (int j = 0; j < 6; j++)
                tb.setSendIndication (i, j, !isEffect && hasTrackSel && isTrack);

            final boolean hasFXTrackSel = selectedFXTrack != null && selectedFXTrack.getIndex () == i;
            tbe.setVolumeIndication (i, isEffect && hasFXTrackSel && isTrack);
            tbe.setPanIndication (i, isEffect && hasFXTrackSel && isTrack);

            cursorDevice.getParameter (i).setIndication (isDevice);
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
        if (!isSelected)
            return;

        final ViewManager viewManager = this.surface.getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.setDrumOctave (0);
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
