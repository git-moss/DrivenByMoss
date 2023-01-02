// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.mode;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3Display;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.List;


/**
 * The device parameter mode. The knobs control the value of the parameter on the parameter page.
 * device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMk3ParameterMode extends ParameterMode<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
{
    private final boolean areKnobs;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public LaunchkeyMk3ParameterMode (final LaunchkeyMk3ControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (surface, model, true, controls);

        this.areKnobs = controls.get (0) == ContinuousID.KNOB1;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();

        if (this.areKnobs)
        {
            // Display device and current parameter page
            final StringBuilder sb = new StringBuilder ();
            if (this.cursorDevice.doesExist ())
                sb.append (this.cursorDevice.getName (8)).append (' ').append (this.cursorDevice.getParameterPageBank ().getSelectedItem ());
            else
                sb.append ("No device");
            d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE, 0, sb.toString ());

            final ICursorTrack cursorTrack = this.model.getCursorTrack ();
            final String trackText = cursorTrack.doesExist () ? String.format ("%d: %s", Integer.valueOf (cursorTrack.getPosition () + 1), cursorTrack.getName ()) : "No sel. track";
            d.setCell (LaunchkeyMk3Display.SCREEN_ROW_BASE + 1, 0, trackText);
        }

        final int row = this.areKnobs ? LaunchkeyMk3Display.SCREEN_ROW_POTS : LaunchkeyMk3Display.SCREEN_ROW_FADERS;

        // Format track names
        final IParameterBank parameterBank = this.cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final IParameter item = parameterBank.getItem (i);
            final boolean exists = item.doesExist ();
            final int offset = i * 2;
            d.setCell (row + offset, 0, exists ? item.getName () : "No parameter");
            d.setCell (row + offset + 1, 0, exists ? item.getDisplayedValue () : "");
        }

        // Add master fader
        if (!this.areKnobs)
        {
            final IMasterTrack masterTrack = this.model.getMasterTrack ();
            d.setCell (row + 16, 0, "Master");
            d.setCell (row + 16 + 1, 0, "Vol: " + masterTrack.getVolumeStr ());
        }

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        // Trigger parameter pages instead of devices
        super.selectPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        // Trigger parameter pages instead of devices
        super.selectNextItemPage ();
    }
}