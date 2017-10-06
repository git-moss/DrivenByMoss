// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;

import java.util.Arrays;


/**
 * Base class for all modes used by Push.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<PushControlSurface, PushConfiguration>
{
    protected boolean []    isKnobTouched;
    protected final boolean isPush2;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);

        this.isPush2 = this.surface.getConfiguration ().isPush2 ();

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
        for (boolean anIsKnobTouched : this.isKnobTouched) {
            if (anIsKnobTouched)
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        if (this.surface.getConfiguration ().isPush2 ())
            this.updateDisplay2 ();
        else
            this.updateDisplay1 ();
    }


    /**
     * Update the display of Push 1.
     */
    public abstract void updateDisplay1 ();


    /**
     * Update the display of Push 2.
     */
    public abstract void updateDisplay2 ();


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
            this.model.getCurrentTrackBank ().select (index);
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
        if (!this.surface.getConfiguration ().isStopAutomationOnKnobRelease () || isTouched)
            return;
        final TransportProxy transport = this.model.getTransport ();
        if (transport.isWritingArrangerAutomation ())
            transport.toggleWriteArrangerAutomation ();
        if (transport.isWritingClipLauncherAutomation ())
            transport.toggleWriteClipLauncherAutomation ();
    }
}