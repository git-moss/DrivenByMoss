// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.view.DrumView;
import de.mossgrabers.controller.launchpad.view.DrumView64;
import de.mossgrabers.controller.launchpad.view.RaindropsView;
import de.mossgrabers.controller.launchpad.view.SequencerView;
import de.mossgrabers.framework.command.trigger.mode.CursorCommand;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
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
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.PLAY))
        {
            final Scales scales = this.model.getScales ();
            final int octave = scales.getOctave ();
            this.canScrollUp = octave < 3;
            this.canScrollDown = octave > -3;
            final int scale = scales.getScale ().ordinal ();
            this.canScrollLeft = scale > 0;
            this.canScrollRight = scale < Scale.values ().length - 1;
            return;
        }

        if (viewManager.isActiveView (Views.PIANO))
        {
            final Scales scales = this.model.getScales ();
            final int octave = scales.getOctave ();
            this.canScrollUp = octave < 3;
            this.canScrollDown = octave > -3;
            this.canScrollLeft = false;
            this.canScrollRight = false;
            return;
        }

        if (viewManager.isActiveView (Views.DRUM))
        {
            final INoteClip clip = ((DrumView) viewManager.getView (Views.DRUM)).getClip ();
            final Scales scales = this.model.getScales ();
            this.canScrollUp = scales.canScrollDrumOctaveUp ();
            this.canScrollDown = scales.canScrollDrumOctaveDown ();
            this.canScrollLeft = clip.canScrollStepsBackwards ();
            this.canScrollRight = clip.canScrollStepsForwards ();
            return;
        }

        if (viewManager.isActiveView (Views.DRUM64))
        {
            final DrumView64 drumView64 = (DrumView64) viewManager.getView (Views.DRUM64);
            final int octave = drumView64.getDrumOctave ();
            this.canScrollUp = octave < 1;
            this.canScrollDown = octave > -2;
            this.canScrollLeft = false;
            this.canScrollRight = false;
            return;
        }

        if (viewManager.isActiveView (Views.SEQUENCER) || viewManager.isActiveView (Views.RAINDROPS))
        {
            final INoteClip clip = ((AbstractSequencerView<?, ?>) viewManager.getActiveView ()).getClip ();
            final int octave = this.model.getScales ().getOctave ();
            this.canScrollUp = octave < Scales.OCTAVE_RANGE;
            this.canScrollDown = octave > -Scales.OCTAVE_RANGE;
            this.canScrollLeft = clip.canScrollStepsBackwards ();
            this.canScrollRight = clip.canScrollStepsForwards ();
            return;
        }

        if (viewManager.isActiveView (Views.DEVICE))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            this.canScrollUp = cursorDevice.canSelectPreviousFX ();
            this.canScrollDown = cursorDevice.canSelectNextFX ();
            final IParameterBank parameterBank = cursorDevice.getParameterBank ();
            this.canScrollLeft = parameterBank.canScrollPageBackwards ();
            this.canScrollRight = parameterBank.canScrollPageForwards ();
            return;
        }

        if (viewManager.isActiveView (Views.BROWSER))
        {
            final IBrowser browser = this.model.getBrowser ();
            this.canScrollUp = false;
            this.canScrollDown = false;
            this.canScrollLeft = browser.hasPreviousContentType ();
            this.canScrollRight = browser.hasNextContentType ();
            return;
        }

        if (viewManager.isActiveView (Views.SHIFT) || viewManager.isActiveView (Views.DRUM4) || viewManager.isActiveView (Views.DRUM8))
        {
            this.canScrollUp = false;
            this.canScrollDown = false;
            this.canScrollLeft = false;
            this.canScrollRight = false;
            return;
        }

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS

        final ITrack sel = tb.getSelectedItem ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        this.canScrollLeft = selIndex > 0 || tb.canScrollPageBackwards ();
        this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        this.canScrollUp = sceneBank.canScrollPageBackwards ();
        this.canScrollDown = sceneBank.canScrollPageForwards ();
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    protected void scrollLeft ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.PLAY))
        {
            final Scales scales = this.model.getScales ();
            scales.prevScale ();
            final String name = scales.getScale ().getName ();
            this.surface.getConfiguration ().setScale (name);
            this.surface.getDisplay ().notify (name);
            return;
        }

        if (viewManager.isActiveView (Views.DEVICE))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            cursorDevice.getParameterBank ().scrollBackwards ();
            this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ()), 100);
            return;
        }

        if (viewManager.isActiveView (Views.BROWSER))
        {
            this.model.getBrowser ().previousContentType ();
            return;
        }

        if (viewManager.isActiveView (Views.SHIFT) || viewManager.isActiveView (Views.DRUM64))
            return;

        // VIEW_SEQUENCER, VIEW_RAINDROPS, VIEW_DRUM, VIEW_DRUM4, VIEW_DRUM8
        final View activeView = viewManager.getActiveView ();
        if (activeView instanceof AbstractSequencerView)
        {
            ((AbstractSequencerView) activeView).onLeft (ButtonEvent.DOWN);
            return;
        }

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        final Mode mode = this.surface.getModeManager ().getMode (Modes.VOLUME);
        if (mode != null)
            mode.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    protected void scrollRight ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.PLAY))
        {
            final Scales scales = this.model.getScales ();
            scales.nextScale ();
            final String name = scales.getScale ().getName ();
            this.surface.getConfiguration ().setScale (name);
            this.surface.getDisplay ().notify (name);
            return;
        }

        if (viewManager.isActiveView (Views.DEVICE))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            cursorDevice.getParameterBank ().scrollForwards ();
            this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ()), 100);
            return;
        }

        if (viewManager.isActiveView (Views.BROWSER))
        {
            this.model.getBrowser ().nextContentType ();
            return;
        }

        if (viewManager.isActiveView (Views.SHIFT) || viewManager.isActiveView (Views.DRUM64))
            return;

        // VIEW_SEQUENCER, VIEW_RAINDROPS, VIEW_DRUM, VIEW_DRUM4, VIEW_DRUM8
        final View activeView = viewManager.getActiveView ();
        if (activeView instanceof AbstractSequencerView)
        {
            ((AbstractSequencerView) activeView).onRight (ButtonEvent.DOWN);
            return;
        }

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        final Mode mode = this.surface.getModeManager ().getMode (Modes.VOLUME);
        if (mode != null)
            mode.selectNextItem ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.PLAY) || viewManager.isActiveView (Views.PIANO))
        {
            ((TransposeView) viewManager.getActiveView ()).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.DRUM))
        {
            ((DrumView) viewManager.getView (Views.DRUM)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.DRUM64))
        {
            ((DrumView64) viewManager.getView (Views.DRUM64)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.SEQUENCER))
        {
            ((SequencerView) viewManager.getView (Views.SEQUENCER)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.RAINDROPS))
        {
            ((RaindropsView) viewManager.getView (Views.RAINDROPS)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.DEVICE))
        {
            this.model.getCursorDevice ().selectPrevious ();
            return;
        }

        if (viewManager.isActiveView (Views.BROWSER) || viewManager.isActiveView (Views.SHIFT) || viewManager.isActiveView (Views.DRUM4) || viewManager.isActiveView (Views.DRUM8))
            return;

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        super.scrollUp ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.PLAY) || viewManager.isActiveView (Views.PIANO))
        {
            ((TransposeView) viewManager.getActiveView ()).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.DRUM))
        {
            ((DrumView) viewManager.getView (Views.DRUM)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.DRUM64))
        {
            ((DrumView64) viewManager.getView (Views.DRUM64)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.SEQUENCER))
        {
            ((SequencerView) viewManager.getView (Views.SEQUENCER)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.RAINDROPS))
        {
            ((RaindropsView) viewManager.getView (Views.RAINDROPS)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.DEVICE))
        {
            this.model.getCursorDevice ().selectNext ();
            return;
        }

        if (viewManager.isActiveView (Views.BROWSER) || viewManager.isActiveView (Views.SHIFT) || viewManager.isActiveView (Views.DRUM4) || viewManager.isActiveView (Views.DRUM8))
            return;

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        super.scrollDown ();
    }
}
