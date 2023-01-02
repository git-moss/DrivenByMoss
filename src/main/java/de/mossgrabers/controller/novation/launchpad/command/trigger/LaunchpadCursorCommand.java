// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.command.trigger;

import de.mossgrabers.controller.novation.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.view.Drum64View;
import de.mossgrabers.controller.novation.launchpad.view.SessionView;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;
import de.mossgrabers.framework.view.sequencer.AbstractSequencerView;

import java.util.Optional;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadCursorCommand extends CursorCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    private static final int REPEAT_SPEED = 300;

    private final Scales     scales;
    private final ITransport transport;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public LaunchpadCursorCommand (final Direction direction, final IModel model, final LaunchpadControlSurface surface)
    {
        super (direction, model, surface, false);

        this.scales = this.model.getScales ();
        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ViewManager viewManager = this.surface.getViewManager ();
        final Views activeViewId = viewManager.getActiveID ();
        if (activeViewId == null)
            return;
        switch (activeViewId)
        {
            case CONTROL, USER:
                this.canScrollUp = false;
                this.canScrollDown = false;
                this.canScrollLeft = false;
                this.canScrollRight = false;
                break;

            case SHIFT:
                this.canScrollUp = true;
                this.canScrollDown = true;
                this.canScrollLeft = true;
                this.canScrollRight = true;
                break;

            case PLAY, CHORDS:
                final int octave = this.scales.getOctave ();
                this.canScrollUp = octave < 3;
                this.canScrollDown = octave > -3;
                final int scale = this.scales.getScale ().ordinal ();
                this.canScrollLeft = scale > 0;
                this.canScrollRight = scale < Scale.values ().length - 1;
                break;

            case PIANO:
                final int pianoOctave = this.scales.getOctave ();
                this.canScrollUp = pianoOctave < 3;
                this.canScrollDown = pianoOctave > -3;
                this.canScrollLeft = false;
                this.canScrollRight = false;
                break;

            case DRUM64:
                final Drum64View drumView64 = (Drum64View) viewManager.get (Views.DRUM64);
                final int drumOctave = drumView64.getDrumOctave ();
                this.canScrollUp = drumOctave < 1;
                this.canScrollDown = drumOctave > -2;
                this.canScrollLeft = false;
                this.canScrollRight = false;
                break;

            case SEQUENCER, RAINDROPS, POLY_SEQUENCER:
                final INoteClip clip = AbstractSequencerView.class.cast (viewManager.getActive ()).getClip ();
                final int seqOctave = this.scales.getOctave ();
                this.canScrollUp = seqOctave < Scales.OCTAVE_RANGE;
                this.canScrollDown = seqOctave > -Scales.OCTAVE_RANGE;
                this.canScrollLeft = clip.canScrollStepsBackwards ();
                this.canScrollRight = clip.canScrollStepsForwards ();
                break;

            case DRUM, DRUM4, DRUM8:
                final INoteClip drumClip = AbstractDrumView.class.cast (viewManager.get (activeViewId)).getClip ();
                this.canScrollUp = this.scales.canScrollDrumOctaveUp ();
                this.canScrollDown = this.scales.canScrollDrumOctaveDown ();
                this.canScrollLeft = drumClip.canScrollStepsBackwards ();
                this.canScrollRight = drumClip.canScrollStepsForwards ();
                break;

            case DEVICE:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                this.canScrollUp = cursorDevice.canSelectPrevious ();
                this.canScrollDown = cursorDevice.canSelectNext ();
                final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                this.canScrollLeft = parameterBank.canScrollPageBackwards ();
                this.canScrollRight = parameterBank.canScrollPageForwards ();
                break;

            case BROWSER:
                final IBrowser browser = this.model.getBrowser ();
                this.canScrollUp = true;
                this.canScrollDown = true;
                this.canScrollLeft = browser.hasPreviousContentType ();
                this.canScrollRight = browser.hasNextContentType ();
                break;

            case SESSION, TRACK_VOLUME, TRACK_PAN, TRACK_SENDS, MIX:
                final Optional<ITrack> sel = tb.getSelectedItem ();
                final int selIndex = sel.isPresent () ? sel.get ().getIndex () : -1;
                this.canScrollLeft = selIndex > 0 || tb.canScrollPageBackwards ();
                this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ();
                final ISceneBank sceneBank = tb.getSceneBank ();
                this.canScrollUp = sceneBank.canScrollPageBackwards ();
                this.canScrollDown = sceneBank.canScrollPageForwards ();
                break;

            case SHUFFLE, TEMPO, PROJECT:
                this.canScrollLeft = true;
                this.canScrollRight = true;
                this.canScrollUp = true;
                this.canScrollDown = true;
                break;

            case NOTE_EDIT_VIEW:
                this.canScrollLeft = false;
                this.canScrollRight = false;
                this.canScrollUp = false;
                this.canScrollDown = false;
                break;

            default:
                throw new FrameworkException ("Missing cursor key state handling for view.");
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollLeft ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveID ())
        {
            case USER, CONTROL:
                // Not used
                break;

            case SHIFT:
                viewManager.setTemporary (Views.SHUFFLE);
                break;

            case PLAY, CHORDS:
                this.scales.prevScale ();
                final String name = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (name);
                this.surface.getDisplay ().notify (name);
                break;

            case PIANO, DRUM64:
                // Not used
                break;

            case SEQUENCER, RAINDROPS, POLY_SEQUENCER, DRUM, DRUM4, DRUM8:
                final IView activeView = viewManager.getActive ();
                if (activeView instanceof final AbstractSequencerView<?, ?> sequencerView)
                {
                    sequencerView.onLeft (ButtonEvent.DOWN);
                    this.mvHelper.notifyEditPage (sequencerView.getClip ());
                }
                break;

            case DEVICE:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                cursorDevice.getParameterBank ().scrollBackwards ();
                this.mvHelper.notifySelectedParameterPage ();
                break;

            case BROWSER:
                this.model.getBrowser ().previousContentType ();
                break;

            case SESSION:
                final IMode volumeMode = this.surface.getModeManager ().get (Modes.VOLUME);
                if (volumeMode == null)
                    return;
                final SessionView sessionView = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
                if (sessionView.isBirdsEyeActive ())
                    volumeMode.selectPreviousItemPage ();
                else
                    volumeMode.selectPreviousItem ();
                this.mvHelper.notifySelectedTrack ();
                break;

            case TRACK_VOLUME, TRACK_PAN, TRACK_SENDS, MIX:
                final IMode mode = this.surface.getModeManager ().get (Modes.VOLUME);
                if (mode != null)
                    mode.selectPreviousItem ();
                break;

            case SHUFFLE:
                if (!this.surface.isPressed (ButtonID.RIGHT))
                    this.triggerChangeShuffle (-10, ButtonID.LEFT);
                break;

            case TEMPO:
                if (!this.surface.isPressed (ButtonID.RIGHT))
                    this.triggerChangeTempo (-10, ButtonID.LEFT);
                break;

            case PROJECT:
                this.triggerChangeZoom1 (false);
                break;

            case NOTE_EDIT_VIEW:
                // Not used
                break;

            default:
                throw new FrameworkException ("Missing cursor key left handling for view.");
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollRight ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveID ())
        {
            case USER, CONTROL:
                // Not used
                break;

            case SHIFT:
                viewManager.setTemporary (Views.SHUFFLE);
                break;

            case PLAY, CHORDS:
                this.scales.nextScale ();
                final String name = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (name);
                this.surface.getDisplay ().notify (name);
                break;

            case PIANO, DRUM64:
                // Not used
                break;

            case SEQUENCER, RAINDROPS, POLY_SEQUENCER, DRUM, DRUM4, DRUM8:
                final IView activeView = viewManager.getActive ();
                if (activeView instanceof final AbstractSequencerView<?, ?> sequencerView)
                {
                    sequencerView.onRight (ButtonEvent.DOWN);
                    this.mvHelper.notifyEditPage (sequencerView.getClip ());
                }
                break;

            case DEVICE:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                cursorDevice.getParameterBank ().scrollForwards ();
                this.mvHelper.notifySelectedParameterPage ();
                break;

            case BROWSER:
                this.model.getBrowser ().nextContentType ();
                break;

            case SESSION:
                final IMode volumeMode = this.surface.getModeManager ().get (Modes.VOLUME);
                if (volumeMode == null)
                    return;
                final SessionView sessionView = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
                if (sessionView.isBirdsEyeActive ())
                    volumeMode.selectNextItemPage ();
                else
                    volumeMode.selectNextItem ();
                this.mvHelper.notifySelectedTrack ();
                break;

            case TRACK_VOLUME, TRACK_PAN, TRACK_SENDS, MIX:
                final IMode mode = this.surface.getModeManager ().get (Modes.VOLUME);
                if (mode != null)
                    mode.selectNextItem ();
                break;

            case SHUFFLE:
                if (!this.surface.isPressed (ButtonID.LEFT))
                    this.triggerChangeShuffle (10, ButtonID.RIGHT);
                break;

            case TEMPO:
                if (!this.surface.isPressed (ButtonID.LEFT))
                    this.triggerChangeTempo (10, ButtonID.RIGHT);
                break;

            case PROJECT:
                this.triggerChangeZoom1 (true);
                break;

            case NOTE_EDIT_VIEW:
                // Not used
                break;

            default:
                throw new FrameworkException ("Missing cursor key right handling for view.");
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveID ())
        {
            case USER, CONTROL:
                // Not Used
                break;

            case SHIFT:
                viewManager.setTemporary (Views.TEMPO);
                break;

            case PLAY, CHORDS, PIANO, DRUM64, DRUM, SEQUENCER, RAINDROPS, POLY_SEQUENCER, DRUM4, DRUM8:
                ((TransposeView) viewManager.getActive ()).onOctaveUp (ButtonEvent.DOWN);
                break;

            case DEVICE:
                this.model.getCursorDevice ().selectPrevious ();
                this.mvHelper.notifySelectedDevice ();
                break;

            case BROWSER:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                if (cursorDevice.doesExist ())
                    this.model.getBrowser ().insertBeforeCursorDevice ();
                break;

            case SESSION:
                final SessionView sessionView = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
                if (sessionView.isBirdsEyeActive ())
                    this.getSceneBank ().selectPreviousPage ();
                else
                    super.scrollUp ();
                break;

            case TRACK_VOLUME, TRACK_PAN, TRACK_SENDS, MIX:
                super.scrollUp ();
                break;

            case SHUFFLE:
                if (!this.surface.isPressed (ButtonID.DOWN))
                    this.triggerChangeShuffle (1, ButtonID.UP);
                break;

            case TEMPO:
                if (!this.surface.isPressed (ButtonID.DOWN))
                    this.triggerChangeTempo (1, ButtonID.UP);
                break;

            case PROJECT:
                this.model.getApplication ().incTrackHeight ();
                break;

            case NOTE_EDIT_VIEW:
                // Not used
                break;

            default:
                throw new FrameworkException ("Missing cursor key up handling for view.");
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveID ())
        {
            case USER, CONTROL:
                // Not Used
                break;

            case SHIFT:
                viewManager.setTemporary (Views.TEMPO);
                break;

            case PLAY, CHORDS, PIANO, DRUM64, DRUM, SEQUENCER, RAINDROPS, POLY_SEQUENCER, DRUM4, DRUM8:
                ((TransposeView) viewManager.getActive ()).onOctaveDown (ButtonEvent.DOWN);
                break;

            case DEVICE:
                this.model.getCursorDevice ().selectNext ();
                this.mvHelper.notifySelectedDevice ();
                break;

            case BROWSER:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                if (cursorDevice.doesExist ())
                    this.model.getBrowser ().insertAfterCursorDevice ();
                break;

            case SESSION:
                final SessionView sessionView = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
                if (sessionView.isBirdsEyeActive ())
                    this.getSceneBank ().selectNextPage ();
                else
                    super.scrollDown ();
                break;

            case TRACK_VOLUME, TRACK_PAN, TRACK_SENDS, MIX:
                super.scrollDown ();
                break;

            case SHUFFLE:
                if (!this.surface.isPressed (ButtonID.UP))
                    this.triggerChangeShuffle (-1, ButtonID.DOWN);
                break;

            case TEMPO:
                if (!this.surface.isPressed (ButtonID.UP))
                    this.triggerChangeTempo (-1, ButtonID.DOWN);
                break;

            case PROJECT:
                this.model.getApplication ().decTrackHeight ();
                break;

            case NOTE_EDIT_VIEW:
                // Not used
                break;

            default:
                throw new FrameworkException ("Missing cursor key down handling for view.");
        }
    }


    private void triggerChangeTempo (final int amount, final ButtonID buttonID)
    {
        if (!this.surface.isPressed (buttonID))
            return;

        this.transport.setTempo (this.transport.getTempo () + amount);
        this.surface.scheduleTask ( () -> this.triggerChangeTempo (amount, buttonID), REPEAT_SPEED);
    }


    private void triggerChangeShuffle (final int amount, final ButtonID buttonID)
    {
        if (!this.surface.isPressed (buttonID))
            return;

        final IParameter shuffleParam = this.model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT);
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        final int a = (int) Math.round (amount * max / 100.0);
        shuffleParam.setValue (Math.min (max, shuffleParam.getValue () + a));

        this.surface.scheduleTask ( () -> this.triggerChangeShuffle (amount, buttonID), REPEAT_SPEED);
    }


    private void triggerChangeZoom1 (final boolean in)
    {
        if (!this.surface.isPressed (in ? ButtonID.RIGHT : ButtonID.LEFT))
            return;

        if (in)
            this.model.getApplication ().zoomIn ();
        else
            this.model.getApplication ().zoomOut ();

        this.surface.scheduleTask ( () -> this.triggerChangeZoom1 (in), REPEAT_SPEED);
    }


    /** {@inheritDoc} */
    @Override
    protected ButtonEvent getTriggerEvent ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveID ())
        {
            case DRUM, SEQUENCER, POLY_SEQUENCER, DRUM4, DRUM8:
                return ButtonEvent.UP;

            default:
                return ButtonEvent.DOWN;
        }
    }
}
