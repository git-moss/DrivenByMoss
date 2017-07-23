// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.CursorCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.view.DrumView;
import de.mossgrabers.launchpad.view.DrumView64;
import de.mossgrabers.launchpad.view.PlayView;
import de.mossgrabers.launchpad.view.RaindropsView;
import de.mossgrabers.launchpad.view.SequencerView;
import de.mossgrabers.launchpad.view.Views;


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
    public LaunchpadCursorCommand (final Direction direction, final Model model, final LaunchpadControlSurface surface)
    {
        super (direction, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateArrowStates ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.VIEW_PLAY))
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

        if (viewManager.isActiveView (Views.VIEW_DRUM))
        {
            final int octave = this.model.getScales ().getDrumOctave ();
            this.canScrollUp = octave < 5;
            this.canScrollDown = octave > -3;
            this.canScrollLeft = ((DrumView) viewManager.getView (Views.VIEW_DRUM)).getClip ().getEditPage () > 0;
            // TODO API extension required - We do not know the number of steps
            this.canScrollRight = true;
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DRUM64))
        {
            final DrumView64 drumView64 = (DrumView64) viewManager.getView (Views.VIEW_DRUM64);
            final int octave = drumView64.getDrumOctave ();
            this.canScrollUp = octave < 1;
            this.canScrollDown = octave > -2;
            this.canScrollLeft = false;
            this.canScrollRight = false;
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_SEQUENCER) || viewManager.isActiveView (Views.VIEW_RAINDROPS))
        {
            final int octave = this.model.getScales ().getOctave ();
            this.canScrollUp = octave < Scales.OCTAVE_RANGE;
            this.canScrollDown = octave > -Scales.OCTAVE_RANGE;
            this.canScrollLeft = ((SequencerView) viewManager.getView (Views.VIEW_SEQUENCER)).getClip ().getEditPage () > 0;
            // TODO API extension required - We do not know the number of steps
            this.canScrollRight = true;
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
        {
            final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
            this.canScrollUp = cursorDevice.canSelectNextFX ();
            this.canScrollDown = cursorDevice.canSelectPreviousFX ();
            this.canScrollLeft = cursorDevice.hasPreviousParameterPage ();
            this.canScrollRight = cursorDevice.hasNextParameterPage ();
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_BROWSER))
        {
            final BrowserProxy browser = this.model.getBrowser ();
            final int index = browser.getSelectedContentTypeIndex ();
            this.canScrollUp = false;
            this.canScrollDown = false;
            this.canScrollLeft = index > 0;
            this.canScrollRight = index < browser.getContentTypeNames ().length - 1;
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_SHIFT) || viewManager.isActiveView (Views.VIEW_DRUM4) || viewManager.isActiveView (Views.VIEW_DRUM8))
        {
            this.canScrollUp = false;
            this.canScrollDown = false;
            this.canScrollLeft = false;
            this.canScrollRight = false;
            return;
        }

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS

        final TrackData sel = tb.getSelectedTrack ();
        final int selIndex = sel != null ? sel.getIndex () : -1;
        this.canScrollLeft = selIndex > 0 || tb.canScrollTracksUp ();
        this.canScrollRight = selIndex >= 0 && selIndex < 7 && tb.getTrack (selIndex + 1).doesExist () || tb.canScrollTracksDown ();
        this.canScrollUp = tb.canScrollScenesUp ();
        this.canScrollDown = tb.canScrollScenesDown ();
    }


    /** {@inheritDoc} */
    @Override
    protected int getButtonOnColor ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.VIEW_SESSION) || viewManager.isActiveView (Views.VIEW_VOLUME) || viewManager.isActiveView (Views.VIEW_PAN) || viewManager.isActiveView (Views.VIEW_SENDS))
            return LaunchpadColors.LAUNCHPAD_COLOR_LIME;

        if (viewManager.isActiveView (Views.VIEW_RAINDROPS))
            return LaunchpadColors.LAUNCHPAD_COLOR_GREEN;

        if (viewManager.isActiveView (Views.VIEW_SEQUENCER))
            return LaunchpadColors.LAUNCHPAD_COLOR_BLUE;

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
            return LaunchpadColors.LAUNCHPAD_COLOR_AMBER;

        if (viewManager.isActiveView (Views.VIEW_DRUM) || viewManager.isActiveView (Views.VIEW_DRUM4) || viewManager.isActiveView (Views.VIEW_DRUM8) || viewManager.isActiveView (Views.VIEW_DRUM64))
            return LaunchpadColors.LAUNCHPAD_COLOR_YELLOW;

        if (viewManager.isActiveView (Views.VIEW_BROWSER))
            return LaunchpadColors.LAUNCHPAD_COLOR_TURQUOISE;

        // VIEW_PLAY, VIEW_SHIFT
        return LaunchpadColors.LAUNCHPAD_COLOR_OCEAN_HI;
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    protected void scrollLeft ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.VIEW_PLAY))
        {
            final Scales scales = this.model.getScales ();
            scales.prevScale ();
            final String name = scales.getScale ().getName ();
            this.surface.getConfiguration ().setScale (name);
            this.surface.getDisplay ().notify (name);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
        {
            final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
            cursorDevice.previousParameterPage ();
            this.surface.getDisplay ().notify (cursorDevice.getSelectedParameterPageName ());
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_BROWSER))
        {
            this.model.getBrowser ().previousContentType ();
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_SHIFT) || viewManager.isActiveView (Views.VIEW_DRUM64))
            return;

        // VIEW_SEQUENCER, VIEW_RAINDROPS, VIEW_DRUM, VIEW_DRUM4, VIEW_DRUM8
        final View activeView = viewManager.getActiveView ();
        if (activeView instanceof AbstractSequencerView)
        {
            ((AbstractSequencerView) activeView).onLeft (ButtonEvent.DOWN);
            return;
        }

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        this.scrollTracksLeft ();
    }


    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    protected void scrollRight ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.VIEW_PLAY))
        {
            final Scales scales = this.model.getScales ();
            scales.nextScale ();
            final String name = scales.getScale ().getName ();
            this.surface.getConfiguration ().setScale (name);
            this.surface.getDisplay ().notify (name);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
        {
            final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
            cursorDevice.nextParameterPage ();
            this.surface.getDisplay ().notify (cursorDevice.getSelectedParameterPageName ());
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_BROWSER))
        {
            this.model.getBrowser ().nextContentType ();
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_SHIFT) || viewManager.isActiveView (Views.VIEW_DRUM64))
            return;

        // VIEW_SEQUENCER, VIEW_RAINDROPS, VIEW_DRUM, VIEW_DRUM4, VIEW_DRUM8
        final View activeView = viewManager.getActiveView ();
        if (activeView instanceof AbstractSequencerView)
        {
            ((AbstractSequencerView) activeView).onRight (ButtonEvent.DOWN);
            return;
        }

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        this.scrollTracksRight ();
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollUp ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.VIEW_PLAY))
        {
            ((PlayView) viewManager.getView (Views.VIEW_PLAY)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DRUM))
        {
            ((DrumView) viewManager.getView (Views.VIEW_DRUM)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DRUM64))
        {
            ((DrumView64) viewManager.getView (Views.VIEW_DRUM64)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_SEQUENCER))
        {
            ((SequencerView) viewManager.getView (Views.VIEW_SEQUENCER)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_RAINDROPS))
        {
            ((RaindropsView) viewManager.getView (Views.VIEW_RAINDROPS)).onOctaveUp (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
        {
            this.model.getCursorDevice ().selectNext ();
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_BROWSER) || viewManager.isActiveView (Views.VIEW_SHIFT) || viewManager.isActiveView (Views.VIEW_DRUM4) || viewManager.isActiveView (Views.VIEW_DRUM8))
            return;

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        super.scrollUp ();

        // TODO could be used for layer navigation
        // VIEW_DEVICE
    }


    /** {@inheritDoc} */
    @Override
    protected void scrollDown ()
    {
        final ViewManager viewManager = this.surface.getViewManager ();

        if (viewManager.isActiveView (Views.VIEW_PLAY))
        {
            ((PlayView) viewManager.getView (Views.VIEW_PLAY)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DRUM))
        {
            ((DrumView) viewManager.getView (Views.VIEW_DRUM)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DRUM64))
        {
            ((DrumView64) viewManager.getView (Views.VIEW_DRUM64)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_SEQUENCER))
        {
            ((SequencerView) viewManager.getView (Views.VIEW_SEQUENCER)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_RAINDROPS))
        {
            ((RaindropsView) viewManager.getView (Views.VIEW_RAINDROPS)).onOctaveDown (ButtonEvent.DOWN);
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_DEVICE))
        {
            this.model.getCursorDevice ().selectPrevious ();
            return;
        }

        if (viewManager.isActiveView (Views.VIEW_BROWSER) || viewManager.isActiveView (Views.VIEW_SHIFT) || viewManager.isActiveView (Views.VIEW_DRUM4) || viewManager.isActiveView (Views.VIEW_DRUM8))
            return;

        // VIEW_SESSION, VIEW_VOLUME, VIEW_PAN, VIEW_SENDS
        super.scrollDown ();

        // TODO could be used for layer navigation
        // VIEW_DEVICE
    }


    /** {@inheritDoc} */
    @Override
    protected void delayedUpdateArrows ()
    {
        if (this.surface.isPro ())
        {
            this.surface.setButton (this.surface.getLeftButtonId (), this.canScrollLeft ? this.getButtonOnColor () : this.getButtonOffColor ());
            this.surface.setButton (this.surface.getRightButtonId (), this.canScrollRight ? this.getButtonOnColor () : this.getButtonOffColor ());
            this.surface.setButton (this.surface.getUpButtonId (), this.canScrollUp ? this.getButtonOnColor () : this.getButtonOffColor ());
            this.surface.setButton (this.surface.getDownButtonId (), this.canScrollDown ? this.getButtonOnColor () : this.getButtonOffColor ());
        }
        else
        {
            this.surface.updateButton (this.surface.getLeftButtonId (), this.canScrollLeft ? this.getButtonOnColor () : this.getButtonOffColor ());
            this.surface.updateButton (this.surface.getRightButtonId (), this.canScrollRight ? this.getButtonOnColor () : this.getButtonOffColor ());
            this.surface.updateButton (this.surface.getUpButtonId (), this.canScrollUp ? this.getButtonOnColor () : this.getButtonOffColor ());
            this.surface.updateButton (this.surface.getDownButtonId (), this.canScrollDown ? this.getButtonOnColor () : this.getButtonOffColor ());
        }
    }
}
