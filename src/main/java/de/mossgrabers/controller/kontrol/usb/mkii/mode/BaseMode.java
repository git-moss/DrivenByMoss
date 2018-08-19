// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.mode;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;


/**
 * Base class for all modes used by Kontrol2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<Kontrol2ControlSurface, Kontrol2Configuration>
{
    protected static final int SCROLL_RATE     = 8;

    private int                movementCounter = 0;

    protected boolean []       isKnobTouched;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final Kontrol2ControlSurface surface, final IModel model)
    {
        super (surface, model);

        this.isKnobTouched = new boolean [8];
        Arrays.fill (this.isKnobTouched, false);
    }


    /**
     * Check if a knob is touched.
     *
     * @return True if at least 1 knob is touched
     */
    public boolean isAKnobTouched ()
    {
        for (final boolean anIsKnobTouched: this.isKnobTouched)
        {
            if (anIsKnobTouched)
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
            this.onFirstRow (index, event);
        else
            this.onSecondRow (index, event);
    }


    /**
     * Down press on a first row button.
     *
     * @param index The index of the button
     * @param event The button event
     */
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getCurrentTrackBank ().getItem (index).select ();
    }


    /**
     * Down press on a second row button.
     *
     * @param index The index of the button
     * @param event The button event
     */
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        this.disableFirstRow ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        this.disableSecondRow ();
    }


    /**
     * Turn off all buttons of the first row.
     */
    protected void disableFirstRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (20 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));
    }


    /**
     * Turn off all buttons of the second row.
     */
    protected void disableSecondRow ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (102 + i, colorManager.getColor (AbstractMode.BUTTON_COLOR_OFF));
    }


    /**
     * Check if the automation needs to be stopped because a knob is no longer touched.
     *
     * @param isTouched The touch state
     */
    protected void checkStopAutomationOnKnobRelease (final boolean isTouched)
    {
        if (isTouched)
            return;
        final ITransport transport = this.model.getTransport ();
        if (transport.isWritingArrangerAutomation ())
            transport.toggleWriteArrangerAutomation ();
        if (transport.isWritingClipLauncherAutomation ())
            transport.toggleWriteClipLauncherAutomation ();
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
}