// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.command.trigger;

import de.mossgrabers.controller.launchpad.LaunchpadConfiguration;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.launchpad.view.DrumView;
import de.mossgrabers.controller.launchpad.view.DrumView64;
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
    private final Scales scales;


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
            case SHIFT:
                this.canScrollUp = false;
                this.canScrollDown = false;
                this.canScrollLeft = false;
                this.canScrollRight = false;
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
                final INoteClip drumClip = ((DrumView) viewManager.getView (Views.DRUM)).getClip ();
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
                this.canScrollUp = false;
                this.canScrollDown = false;
                this.canScrollLeft = browser.hasPreviousContentType ();
                this.canScrollRight = browser.hasNextContentType ();
                break;

            case SESSION:
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
                final ITrack sel = tb.getSelectedItem ();
                final int selIndex = sel != null ? sel.getIndex () : -1;
                this.canScrollLeft = selIndex > 0 || tb.canScrollPageBackwards ();
                this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getItem (selIndex + 1).doesExist () || tb.canScrollPageForwards ();
                final ISceneBank sceneBank = tb.getSceneBank ();
                this.canScrollUp = sceneBank.canScrollPageBackwards ();
                this.canScrollDown = sceneBank.canScrollPageForwards ();
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
            case CONTROL:
            case SHIFT:
                // Not used
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
                this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ()), 100);
                break;

            case BROWSER:
                this.model.getBrowser ().previousContentType ();
                break;

            case SESSION:
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
                final Mode mode = this.surface.getModeManager ().getMode (Modes.VOLUME);
                if (mode != null)
                    mode.selectPreviousItem ();
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
            case CONTROL:
            case SHIFT:
                // Not used
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
                this.model.getHost ().scheduleTask ( () -> this.surface.getDisplay ().notify (cursorDevice.getParameterPageBank ().getSelectedItem ()), 100);
                break;

            case BROWSER:
                this.model.getBrowser ().nextContentType ();
                break;

            case SESSION:
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
                final Mode mode = this.surface.getModeManager ().getMode (Modes.VOLUME);
                if (mode != null)
                    mode.selectNextItem ();
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
            case CONTROL:
            case SHIFT:
                // Not Used
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
                break;

            case BROWSER:
                // Not Used
                break;

            case SESSION:
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
                super.scrollUp ();
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
            case CONTROL:
            case SHIFT:
                // Not Used
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
                break;

            case BROWSER:
                // Not Used
                break;

            case SESSION:
            case TRACK_VOLUME:
            case TRACK_PAN:
            case TRACK_SENDS:
                super.scrollDown ();
                break;

            default:
                throw new FrameworkException ("Missing cursor key down handling for view.");
        }
    }
}
