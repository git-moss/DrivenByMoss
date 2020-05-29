// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.view.DrumView64;
import de.mossgrabers.controller.launchpad.view.DrumViewBase;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.TransposeView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadCursorCommand extends CursorCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
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
        super (direction, model, surface);

        this.scales = this.model.getScales ();
        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ViewManager viewManager = this.surface.getViewManager ();
        final Views activeViewId = viewManager.getActiveViewId ();
        if (activeViewId == null)
            return;
        switch (activeViewId)
        {
            case CONTROL:
            case USER:
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

            case PLAY:
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
                final DrumView64 drumView64 = (DrumView64) viewManager.getView (Views.DRUM64);
                final int drumOctave = drumView64.getDrumOctave ();
                this.canScrollUp = drumOctave < 1;
                this.canScrollDown = drumOctave > -2;
                this.canScrollLeft = false;
                this.canScrollRight = false;
                break;

            case SEQUENCER:
            case RAINDROPS:
            case POLY_SEQUENCER:
                final INoteClip clip = ((AbstractSequencerView<?, ?>) viewManager.getActiveView ()).getClip ();
                final int seqOctave = this.scales.getOctave ();
                this.canScrollUp = seqOctave < Scales.OCTAVE_RANGE;
                this.canScrollDown = seqOctave > -Scales.OCTAVE_RANGE;
                this.canScrollLeft = clip.canScrollStepsBackwards ();
                this.canScrollRight = clip.canScrollStepsForwards ();
                break;

            case DRUM:
            case DRUM4:
            case DRUM8:
                final INoteClip drumClip = ((DrumViewBase) viewManager.getView (activeViewId)).getClip ();
                this.canScrollUp = this.scales.canScrollDrumOctaveUp ();
                this.canScrollDown = this.scales.canScrollDrumOctaveDown ();
                this.canScrollLeft = drumClip.canScrollStepsBackwards ();
                this.canScrollRight = drumClip.canScrollStepsForwards ();
                break;

            case DEVICE:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                this.canScrollUp = cursorDevice.canSelectPreviousFX ();
                this.canScrollDown = cursorDevice.canSelectNextFX ();
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

            case SESSION:
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
            case MIX:
                final ITrack sel = tb.getSelectedItem ();
                final int selIndex = sel != null ? sel.getIndex () : -1;
                this.canScrollLeft = selIndex > 0 || tb.canScrollPageBackwards ();
                this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ();
                final ISceneBank sceneBank = tb.getSceneBank ();
                this.canScrollUp = sceneBank.canScrollPageBackwards ();
                this.canScrollDown = sceneBank.canScrollPageForwards ();
                break;

            case SHUFFLE:
            case TEMPO:
            case PROJECT:
                this.canScrollLeft = true;
                this.canScrollRight = true;
                this.canScrollUp = true;
                this.canScrollDown = true;
                break;

            default:
                throw new FrameworkException ("Missing cursor key state handling for view.");
        }
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    protected void scrollLeft ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveViewId ())
        {
            case USER:
            case CONTROL:
                // Not used
                break;

            case SHIFT:
                final Views previousViewId = viewManager.getPreviousViewId ();
                viewManager.setActiveView (Views.SHUFFLE);
                viewManager.setPreviousView (previousViewId);
                break;

            case PLAY:
                this.scales.prevScale ();
                final String name = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (name);
                this.surface.getDisplay ().notify (name);
                break;

            case PIANO:
            case DRUM64:
                // Not used
                // Not used
                break;

            case SEQUENCER:
            case RAINDROPS:
            case POLY_SEQUENCER:
            case DRUM:
            case DRUM4:
            case DRUM8:
                final View activeView = viewManager.getActiveView ();
                if (activeView instanceof AbstractSequencerView)
                    ((AbstractSequencerView) activeView).onLeft (ButtonEvent.DOWN);
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
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
            case MIX:
                final Mode mode = this.surface.getModeManager ().getMode (Modes.VOLUME);
                if (mode != null)
                    mode.selectPreviousItem ();
                break;

            case SHUFFLE:
                this.triggerChangeShuffle10 (false);
                break;

            case TEMPO:
                this.triggerChangeTempo10 (false);
                break;

            case PROJECT:
                this.triggerChangeZoom1 (false);
                break;

            default:
                throw new FrameworkException ("Missing cursor key left handling for view.");
        }
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    protected void scrollRight ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        switch (viewManager.getActiveViewId ())
        {
            case USER:
            case CONTROL:
                // Not used
                break;

            case SHIFT:
                final Views previousViewId = viewManager.getPreviousViewId ();
                viewManager.setActiveView (Views.SHUFFLE);
                viewManager.setPreviousView (previousViewId);
                break;

            case PLAY:
                this.scales.nextScale ();
                final String name = this.scales.getScale ().getName ();
                this.surface.getConfiguration ().setScale (name);
                this.surface.getDisplay ().notify (name);
                break;

            case PIANO:
            case DRUM64:
                // Not used
                break;

            case SEQUENCER:
            case RAINDROPS:
            case POLY_SEQUENCER:
            case DRUM:
            case DRUM4:
            case DRUM8:
                final View activeView = viewManager.getActiveView ();
                if (activeView instanceof AbstractSequencerView)
                    ((AbstractSequencerView) activeView).onRight (ButtonEvent.DOWN);
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
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
            case MIX:
                final Mode mode = this.surface.getModeManager ().getMode (Modes.VOLUME);
                if (mode != null)
                    mode.selectNextItem ();
                break;

            case SHUFFLE:
                this.triggerChangeShuffle10 (true);
                break;

            case TEMPO:
                this.triggerChangeTempo10 (true);
                break;

            case PROJECT:
                this.triggerChangeZoom1 (true);
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
        switch (viewManager.getActiveViewId ())
        {
            case USER:
            case CONTROL:
                // Not Used
                break;

            case SHIFT:
                final Views previousViewId = viewManager.getPreviousViewId ();
                viewManager.setActiveView (Views.TEMPO);
                viewManager.setPreviousView (previousViewId);
                break;

            case PLAY:
            case PIANO:
            case DRUM64:
            case DRUM:
            case SEQUENCER:
            case RAINDROPS:
            case POLY_SEQUENCER:
            case DRUM4:
            case DRUM8:
                ((TransposeView) viewManager.getActiveView ()).onOctaveUp (ButtonEvent.DOWN);
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
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
            case MIX:
                super.scrollUp ();
                break;

            case SHUFFLE:
                this.triggerChangeShuffle1 (true);
                break;

            case TEMPO:
                this.triggerChangeTempo1 (true);
                break;

            case PROJECT:
                this.model.getApplication ().incTrackHeight ();
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
        switch (viewManager.getActiveViewId ())
        {
            case USER:
            case CONTROL:
                // Not Used
                break;

            case SHIFT:
                final Views previousViewId = viewManager.getPreviousViewId ();
                viewManager.setActiveView (Views.TEMPO);
                viewManager.setPreviousView (previousViewId);
                break;

            case PLAY:
            case PIANO:
            case DRUM64:
            case DRUM:
            case SEQUENCER:
            case RAINDROPS:
            case POLY_SEQUENCER:
            case DRUM4:
            case DRUM8:
                ((TransposeView) viewManager.getActiveView ()).onOctaveDown (ButtonEvent.DOWN);
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
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
            case MIX:
                super.scrollDown ();
                break;

            case SHUFFLE:
                this.triggerChangeShuffle1 (false);
                break;

            case TEMPO:
                this.triggerChangeTempo1 (false);
                break;

            case PROJECT:
                this.model.getApplication ().decTrackHeight ();
                break;

            default:
                throw new FrameworkException ("Missing cursor key down handling for view.");
        }
    }


    private void triggerChangeTempo1 (final boolean increase)
    {
        if (!this.surface.isPressed (increase ? ButtonID.UP : ButtonID.DOWN))
            return;

        this.model.getTransport ().changeTempo (increase);
        this.surface.scheduleTask ( () -> this.triggerChangeTempo1 (increase), 400);
    }


    private void triggerChangeTempo10 (final boolean increase)
    {
        if (!this.surface.isPressed (increase ? ButtonID.RIGHT : ButtonID.LEFT))
            return;

        this.transport.setTempo (this.transport.getTempo () + (increase ? 10 : -10));
        this.surface.scheduleTask ( () -> this.triggerChangeTempo10 (increase), 400);
    }


    private void triggerChangeShuffle1 (final boolean increase)
    {
        if (!this.surface.isPressed (increase ? ButtonID.UP : ButtonID.DOWN))
            return;

        final IParameter shufflePAram = this.model.getGroove ().getParameters ()[1];
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        shufflePAram.setValue (Math.min (max, shufflePAram.getValue () + (increase ? 1 : -1)));

        this.surface.scheduleTask ( () -> this.triggerChangeShuffle1 (increase), 400);
    }


    private void triggerChangeShuffle10 (final boolean increase)
    {
        if (!this.surface.isPressed (increase ? ButtonID.RIGHT : ButtonID.LEFT))
            return;

        final IParameter shufflePAram = this.model.getGroove ().getParameters ()[1];
        final int max = this.model.getValueChanger ().getUpperBound () - 1;
        shufflePAram.setValue (Math.min (max, shufflePAram.getValue () + (increase ? 10 : -10)));

        this.surface.scheduleTask ( () -> this.triggerChangeShuffle10 (increase), 400);
    }


    private void triggerChangeZoom1 (final boolean in)
    {
        if (!this.surface.isPressed (in ? ButtonID.RIGHT : ButtonID.LEFT))
            return;

        if (in)
            this.model.getApplication ().zoomIn ();
        else
            this.model.getApplication ().zoomOut ();

        this.surface.scheduleTask ( () -> this.triggerChangeZoom1 (in), 300);
    }
}
