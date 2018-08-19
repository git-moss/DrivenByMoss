// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Selection of the scale and base note.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScalesMode extends BaseMode
{
    private Scales scales;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ScalesMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.isTemporary = false;
        this.scales = model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (index != 0)
            return;

        if (!this.increaseKnobMovement ())
            return;

        this.scales.changeScale (value);
        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
        {
            if (this.isPush2)
                this.scales.nextScale ();
            else
                this.scales.prevScale ();
        }
        else if (index > 0 && index < 7)
            this.scales.setScaleOffset (index - 1);
        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final int offset = this.scales.getScaleOffset ();
        final ColorManager cm = this.model.getColorManager ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isFirstOrLast = i == 0 || i == 7;
            this.surface.updateButton (20 + i, i == 7 ? cm.getColor (AbstractMode.BUTTON_COLOR_OFF) : isFirstOrLast ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO : cm.getColor (offset == i - 1 ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        if (index == 0)
        {
            if (this.isPush2)
                this.scales.prevScale ();
            else
                this.scales.nextScale ();
        }
        else if (index == 7)
            this.scales.toggleChromatic ();
        else
            this.scales.setScaleOffset (index + 5);
        this.update ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final int offset = this.scales.getScaleOffset ();
        final ColorManager cm = this.model.getColorManager ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isFirstOrLast = i == 0 || i == 7;
            this.surface.updateButton (102 + i, isFirstOrLast ? this.isPush2 ? PushColors.PUSH2_COLOR2_AMBER : PushColors.PUSH1_COLOR2_AMBER : cm.getColor (offset == i - 1 + 6 ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR2_ON));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final Scale scale = this.scales.getScale ();
        final int offset = this.scales.getScaleOffset ();
        final Scale [] scaleValues = Scale.values ();
        final String rangeText = this.scales.getRangeText ();
        d.setBlock (0, 0, PushDisplay.SELECT_ARROW + scale.getName ()).clearBlock (0, 1).clearBlock (0, 2).setBlock (0, 3, rangeText).done (0);
        int pos = scale.ordinal () + 1;
        final String name1 = pos < scaleValues.length ? scaleValues[pos].getName () : "";
        d.setBlock (1, 0, " " + name1).clearBlock (1, 1).clearBlock (1, 2).clearBlock (1, 3).done (1);
        pos++;
        final String name2 = pos < scaleValues.length ? scaleValues[pos].getName () : "";
        d.setCell (2, 0, " " + name2);
        for (int i = 0; i < 6; i++)
            d.setCell (2, i + 1, "  " + (offset == i ? PushDisplay.SELECT_ARROW : " ") + Scales.BASES[i]);
        d.clearCell (2, 7).done (2);
        pos++;
        final String name3 = pos < scaleValues.length ? scaleValues[pos].getName () : "";
        d.setCell (3, 0, " " + name3);
        for (int i = 6; i < 12; i++)
            d.setCell (3, i - 5, "  " + (offset == i ? PushDisplay.SELECT_ARROW : " ") + Scales.BASES[i]);
        d.setCell (3, 7, this.scales.isChromatic () ? "Chromatc" : "In Key").done (3);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final Scale scale = this.scales.getScale ();
        final int offset = this.scales.getScaleOffset ();
        final Scale [] scaleValues = Scale.values ();
        final String rangeText = this.scales.getRangeText ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final String [] items = new String [6];
        final boolean [] selected = new boolean [6];
        for (int i = 0; i < 6; i++)
        {
            final int pos = scale.ordinal () + i;
            items[i] = pos < scaleValues.length ? scaleValues[pos].getName () : "";
            selected[i] = i == 0;
        }
        message.addListElement (items, selected);
        for (int i = 0; i < 6; i++)
            message.addOptionElement (i == 3 ? "Note range: " + rangeText : "", Scales.BASES[6 + i], offset == 6 + i, "", Scales.BASES[i], offset == i, false);
        message.addOptionElement ("", this.scales.isChromatic () ? "Chromatc" : "In Key", this.scales.isChromatic (), "", "", false, false);
        message.send ();
    }


    private void update ()
    {
        this.surface.getViewManager ().getActiveView ().updateNoteMapping ();
        final PushConfiguration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES[this.scales.getScaleOffset ()]);
        config.setScaleInKey (!this.scales.isChromatic ());
    }
}