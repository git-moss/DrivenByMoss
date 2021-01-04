// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
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
        super (name, surface, model, true);
    }


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param bank The parameter bank to control with this mode, might be null
     */
    public BaseMode (final String name, final SLMkIIIControlSurface surface, final IModel model, final IBank<? extends IItem> bank)
    {
        super (name, surface, model, false, bank, DEFAULT_KNOB_IDS);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final IParameterProvider parameterProvider = this.getParameterProvider ();
        return parameterProvider == null ? 0 : parameterProvider.get (index).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.surface.getDisplay ().setDisplayLayout (SLMkIIIDisplay.SCREEN_LAYOUT_KNOB);
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
    public int getButtonColor (final ButtonID buttonID)
    {
        return SLMkIIIColorManager.SLMKIII_BLACK;
    }


    /**
     * Get the color (index) which represents this mode,
     *
     * @return The color index
     */
    public abstract int getModeColor ();


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
        display.setPropertyColor (8, 0, this.getModeColor ());

        if (this.surface.isMuteSolo ())
        {
            display.setCell (2, 8, "Mute").setCell (3, 8, "Solo");
            display.setPropertyColor (8, 1, SLMkIIIColorManager.SLMKIII_AMBER);
            display.setPropertyColor (8, 2, SLMkIIIColorManager.SLMKIII_YELLOW);
        }
        else
        {
            display.setCell (2, 8, "Monitor").setCell (3, 8, "Arm");
            display.setPropertyColor (8, 1, SLMkIIIColorManager.SLMKIII_GREEN);
            display.setPropertyColor (8, 2, SLMkIIIColorManager.SLMKIII_RED);
        }
    }
}