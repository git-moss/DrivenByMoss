// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode.device;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;


/**
 * Edit parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceParamsMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DeviceParamsMode (final SLControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Display d = this.surface.getDisplay ();
        d.clearRow (2);

        if (this.model.hasSelectedDevice ())
        {
            d.clearRow (0);
            for (int i = 0; i < 8; i++)
            {
                final IParameter p = this.model.getCursorDevice ().getFXParam (i);
                final String name = p.getName (8);
                if (!name.isEmpty ())
                    d.setCell (0, i, name).setCell (2, i, p.getDisplayedValue (8));
            }
            d.done (0);
        }
        else
            d.setRow (0, "                       Please select a device...                       ");
        d.done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCursorDevice ().changeParameter (index, value);
    }


    /**
     * Set the button LEDs.
     */
    public void setLEDs ()
    {
        final boolean hasDevice = this.model.hasSelectedDevice ();
        final IMidiOutput output = this.surface.getOutput ();
        for (int i = 0; i < 8; i++)
        {
            final int value = hasDevice ? this.model.getCursorDevice ().getFXParam (i).getValue () : 0;
            output.sendCC (0x70 + i, Math.min (value * 11 / 127, 11));
        }
    }


    /**
     * Move to the previous parameter page.
     */
    public void previousPage ()
    {
        this.model.getCursorDevice ().previousParameterPage ();
    }


    /**
     * Move to the next parameter page.
     */
    public void nextPage ()
    {
        this.model.getCursorDevice ().nextParameterPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}