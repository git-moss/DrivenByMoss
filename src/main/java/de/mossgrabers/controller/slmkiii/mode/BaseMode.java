// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Base class for all modes used by SLMkIII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    protected static final int SCROLL_RATE     = 8;

    private int                movementCounter = 0;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final String name, final SLMkIIIControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.getDisplay ().setDisplayLayout (SLMkIIIDisplay.SCREEN_LAYOUT_KNOB);
        this.surface.clearKnobCache ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getCurrentTrackBank ().getItem (index).select ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        this.disableFirstRow ();
    }


    /**
     * Turn off all buttons of the first row.
     */
    protected void disableFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        for (int i = 0; i < 8; i++)
            this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));
    }


    /**
     * Slows down knob movement. Increases the counter till the scroll rate.
     *
     * @return True if the knob movement should be executed otherwise false
     */
    protected boolean increaseKnobMovement ()
    {
        this.movementCounter++;
        if (this.movementCounter < SCROLL_RATE)
            return false;
        this.movementCounter = 0;
        return true;
    }


    protected void setButtonInfo (final SLMkIIIDisplay display)
    {
        if (this.surface.isMuteSolo ())
        {
            display.setCell (2, 8, "Mute").setCell (3, 8, "Solo");
            display.setPropertyColor (8, 1, SLMkIIIColors.SLMKIII_AMBER);
            display.setPropertyColor (8, 2, SLMkIIIColors.SLMKIII_YELLOW);
        }
        else
        {
            display.setCell (2, 8, "Monitor").setCell (3, 8, "Arm");
            display.setPropertyColor (8, 1, SLMkIIIColors.SLMKIII_GREEN);
            display.setPropertyColor (8, 2, SLMkIIIColors.SLMKIII_RED);
        }
    }
}