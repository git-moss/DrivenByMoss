// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract class for all modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractMode<S extends IControlSurface<C>, C extends Configuration> implements Mode
{
    /** Color identifier for a mode button which is off. */
    public static final String BUTTON_COLOR_OFF       = "BUTTON_COLOR_OFF";
    /** Color identifier for a mode button which is on. */
    public static final String BUTTON_COLOR_ON        = "BUTTON_COLOR_ON";
    /** Color identifier for a mode button which is hilighted. */
    public static final String BUTTON_COLOR_HI        = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String BUTTON_COLOR2_ON       = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is hilighted (second row). */
    public static final String BUTTON_COLOR2_HI       = "BUTTON_COLOR2_HI";

    private static final int   BUTTON_REPEAT_INTERVAL = 75;

    protected S                surface;
    protected IModel           model;
    protected boolean          isTemporary;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AbstractMode (final S surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTemporary ()
    {
        return this.isTemporary;
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousTrack ()
    {
        final ITrack sel = this.model.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1 || this.surface.isShiftPressed ())
        {
            this.selectPreviousTrackBankPage (sel, index);
            return;
        }
        this.selectTrack (index);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousTrackBankPage (final ITrack sel, final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollBackwards ())
            return;
        tb.scrollPageBackwards ();
        final int newSel = index == -1 || sel == null ? tb.getPageSize () - 1 : sel.getIndex ();
        this.surface.scheduleTask ( () -> this.selectTrack (newSel), BUTTON_REPEAT_INTERVAL);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextTrack ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedItem ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == tb.getPageSize () || this.surface.isShiftPressed ())
        {
            this.selectNextTrackBankPage (sel, index);
            return;
        }
        this.selectTrack (index);
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextTrackBankPage (final ITrack sel, final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollForwards ())
            return;
        tb.scrollPageForwards ();
        final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
        this.surface.scheduleTask ( () -> this.selectTrack (newSel), BUTTON_REPEAT_INTERVAL);
    }


    /** {@inheritDoc} */
    @Override
    public void selectTrack (final int index)
    {
        this.model.getCurrentTrackBank ().getItem (index).select ();
    }
}