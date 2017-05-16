// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AccentMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AccentMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Will never need fine increments on accent velocity since they are integers
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final int fixedAccentValue = config.getFixedAccentValue ();
        config.setAccentValue (Math.max (1, valueChanger.changeValue (value, fixedAccentValue, 1, 128)));
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final int fixedAccentValue = this.surface.getConfiguration ().getFixedAccentValue ();
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final Display d = this.surface.getDisplay ();
        d.clear ().setCell (0, 7, "Accent").setCell (1, 7, fixedAccentValue, Format.FORMAT_RAW).setCell (2, 7, valueChanger.toDAWValue (fixedAccentValue), Format.FORMAT_VALUE).allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int fixedAccentValue = this.surface.getConfiguration ().getFixedAccentValue ();
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        for (int i = 0; i < 8; i++)
        {
            message.addByte (DisplayMessage.GRID_ELEMENT_PARAMETERS);

            // The menu item
            message.addString ("");
            message.addBoolean (false);

            message.addString ("");
            message.addString ("");
            message.addColor (null);
            message.addBoolean (false);

            message.addString (i == 7 ? "Accent" : "");
            message.addInteger (i == 7 ? valueChanger.toDisplayValue (valueChanger.toDAWValue (fixedAccentValue)) : 0);
            message.addString (i == 7 ? Integer.toString (fixedAccentValue) : "");
            message.addBoolean (this.isKnobTouched[i]);

            message.addInteger (-1);
        }
        message.send ();
    }
}