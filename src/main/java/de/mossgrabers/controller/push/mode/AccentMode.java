// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;


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
    public AccentMode (final PushControlSurface surface, final IModel model)
    {
        super ("Accent", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        // Will never need fine increments on accent velocity since they are integers
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final int fixedAccentValue = config.getFixedAccentValue ();
        config.setFixedAccentValue (Math.max (1, valueChanger.changeValue (value, fixedAccentValue, -100, 128)));
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final int fixedAccentValue = this.surface.getConfiguration ().getFixedAccentValue ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        display.setCell (0, 7, "Accent").setCell (1, 7, fixedAccentValue, Format.FORMAT_RAW).setCell (2, 7, valueChanger.toDAWValue (fixedAccentValue), Format.FORMAT_VALUE);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final int fixedAccentValue = this.surface.getConfiguration ().getFixedAccentValue ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        for (int i = 0; i < 8; i++)
            display.addParameterElement (i == 7 ? "Accent" : "", i == 7 ? valueChanger.toDisplayValue (valueChanger.toDAWValue (fixedAccentValue)) : 0, i == 7 ? Integer.toString (fixedAccentValue) : "", this.isKnobTouched[i], -1);
    }
}