// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;


/**
 * Mode for editing the panorama of all tracks.
 *
 * @author Jürgen Moßgraber
 */
public class PanMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PanMode (final PushControlSurface surface, final IModel model)
    {
        super ("Panorama", surface, model);

        this.setParameterProvider (new PanParameterProvider (model));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();

        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            display.setCell (0, i, t.doesExist () ? "Pan" : "").setCell (1, i, t.getPanStr (8));
            if (t.doesExist ())
                display.setCell (2, i, t.getPan (), Format.FORMAT_PAN);
        }

        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        this.updateChannelDisplay (display, AbstractGraphicDisplay.GRID_ELEMENT_CHANNEL_PAN, false, true);
    }
}