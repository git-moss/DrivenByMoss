// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.display.DisplayModel;


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
    public CrossfaderMode (final PushControlSurface surface, final IModel model)
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
            final ITrack t = this.model.getCurrentTrackBank ().getItem (index);
            if (t.doesExist ())
            {
                if (this.surface.isDeletePressed ())
                {
                    this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                    t.setCrossfadeMode ("AB");
                    return;
                }
                this.surface.getDisplay ().notify ("Crossfader: " + t.getCrossfadeMode ());
            }
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (this.increaseKnobMovement ())
            this.model.getCurrentTrackBank ().getItem (index).changeCrossfadeModeAsNumber (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
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
        this.updateChannelDisplay (DisplayModel.GRID_ELEMENT_CHANNEL_CROSSFADER, false, false);
    }

}