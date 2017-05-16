// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.GrooveProxy;
import de.mossgrabers.framework.daw.data.ParameterData;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Editing of groove parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GrooveMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public GrooveMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.setActive (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.setActive (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (index == 0)
            this.surface.getConfiguration ().changeQuantizeAmount (value);
        else if (index > 1)
            this.model.getGroove ().getParameters ()[index - 2].inc (this.model.getValueChanger ().calcKnobSpeed (value));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final ParameterData [] parameters = this.model.getGroove ().getParameters ();
        final int quantizeAmount = this.surface.getConfiguration ().getQuantizeAmount ();
        d.clear ().setCell (0, 0, "Quant Amnt").setCell (1, 0, quantizeAmount + "%").setCell (2, 0, quantizeAmount * 1023 / 100, Format.FORMAT_VALUE);
        for (int i = 0; i < 6; i++)
            d.setCell (0, 2 + i, parameters[i].getName (8)).setCell (1, 2 + i, parameters[i].getDisplayedValue (8)).setCell (2, 2 + i, parameters[i].getValue (), Format.FORMAT_VALUE);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final GrooveProxy groove = this.model.getGroove ();
        final ParameterData [] parameters = groove.getParameters ();
        final int quantizeAmount = this.surface.getConfiguration ().getQuantizeAmount ();

        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        message.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);
        message.addString ("");
        message.addBoolean (false);
        message.addString ("");
        message.addString ("");
        message.addColor (null);
        message.addBoolean (false);

        message.addString ("Quant Amnt");
        message.addInteger (quantizeAmount * 1023 / 100);
        message.addString (quantizeAmount + "%");
        message.addBoolean (this.isKnobTouched[0]);
        message.addInteger (-1);

        message.addOptionElement ("     Groove", "", false, "", "", false, false);

        for (int i = 0; i < 6; i++)
        {
            message.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);
            message.addString ("");
            message.addBoolean (false);
            message.addString ("");
            message.addString ("");
            message.addColor (null);
            message.addBoolean (false);

            message.addString (parameters[i].getName (10));
            message.addInteger (parameters[i].getValue ());
            message.addString (parameters[i].getDisplayedValue (8));
            message.addBoolean (this.isKnobTouched[i]);
            message.addInteger (-1);
        }

        message.send ();
    }


    private void setActive (final boolean enable)
    {
        this.model.getGroove ().enableObservers (enable);
        this.model.getGroove ().setIndication (enable);
    }
}