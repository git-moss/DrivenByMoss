// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * Mode for editing the crossfader setting of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CrossfaderMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public CrossfaderMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.model.getCurrentTrackBank ().setCrossfadeMode (index, "AB");
                return;
            }

            final TrackData t = this.model.getCurrentTrackBank ().getTrack (index);
            if (t.doesExist ())
                this.surface.getDisplay ().notify ("Crossfader: " + t.getCrossfadeMode ());
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Slow down scrolling
        this.movementCounter++;
        if (this.movementCounter < SCROLL_RATE)
            return;
        this.movementCounter = 0;

        this.model.getCurrentTrackBank ().changeCrossfadeModeAsNumber (index, value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (i);
            d.setCell (0, i, t.doesExist () ? "Crossfdr" : "");
            if (t.doesExist ())
            {
                final boolean isA = "A".equals (t.getCrossfadeMode ());
                d.setCell (1, i, isA ? "A" : "B".equals (t.getCrossfadeMode ()) ? "       B" : "   <> ");
                d.setCell (2, i, isA ? 0 : "B".equals (t.getCrossfadeMode ()) ? upperBound : upperBound / 2, Format.FORMAT_PAN);
            }
            else
            {
                d.clearCell (1, i);
                d.clearCell (2, i);
            }
        }
        d.done (0).done (1).done (2);

        this.drawRow4 ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        this.updateChannelDisplay (DisplayMessage.GRID_ELEMENT_CHANNEL_CROSSFADER, false, false);
    }

}