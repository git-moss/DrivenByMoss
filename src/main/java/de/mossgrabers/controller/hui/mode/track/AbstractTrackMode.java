// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.hui.mode.track;

import de.mossgrabers.controller.hui.HUIConfiguration;
import de.mossgrabers.controller.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends AbstractMode<HUIControlSurface, HUIConfiguration>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public AbstractTrackMode (final String name, final HUIControlSurface surface, final IModel model)
    {
        super (name, surface, model);
        this.isTemporary = false;
    }


    protected ITextDisplay drawTrackHeader ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            d.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), 4));
        }

        return d;
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
            this.resetParameter (index);
    }


    /**
     * Update the knob LEDs.
     */
    public abstract void updateKnobLEDs ();


    protected abstract void resetParameter (int index);
}