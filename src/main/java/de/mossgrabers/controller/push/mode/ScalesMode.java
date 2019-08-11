// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
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
import de.mossgrabers.framework.utils.Pair;


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
        super ("Scale", surface, model);
        this.isTemporary = false;
        this.scales = model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
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
            this.surface.updateTrigger (20 + i, i == 7 ? cm.getColor (AbstractMode.BUTTON_COLOR_OFF) : isFirstOrLast ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO : cm.getColor (offset == i - 1 ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON));
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
            this.surface.updateTrigger (102 + i, isFirstOrLast ? this.isPush2 ? PushColors.PUSH2_COLOR2_AMBER : PushColors.PUSH1_COLOR2_AMBER : cm.getColor (offset == i - 1 + 6 ? AbstractMode.BUTTON_COLOR2_HI : AbstractMode.BUTTON_COLOR2_ON));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();

        final int selIndex = this.scales.getScale ().ordinal ();
        int pos = 0;
        for (final Pair<String, Boolean> p: PushDisplay.createMenuList (4, Scale.getNames (), selIndex))
        {
            d.setBlock (pos, 0, (p.getValue ().booleanValue () ? PushDisplay.SELECT_ARROW : " ") + p.getKey ());
            pos++;
        }

        d.setBlock (0, 3, this.scales.getRangeText ());

        final int offset = this.scales.getScaleOffset ();
        for (int i = 0; i < 6; i++)
        {
            d.setCell (2, i + 1, "  " + (offset == i ? PushDisplay.SELECT_ARROW : " ") + Scales.BASES[i]);
            d.setCell (3, i + 1, "  " + (offset == 6 + i ? PushDisplay.SELECT_ARROW : " ") + Scales.BASES[6 + i]);
        }
        d.setCell (3, 7, this.scales.isChromatic () ? "Chromatc" : "In Key");

        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayModel message = this.surface.getDisplay ().getModel ();

        final int selIndex = this.scales.getScale ().ordinal ();
        message.addListElement (6, Scale.getNames (), selIndex);

        final int offset = this.scales.getScaleOffset ();
        final String rangeText = this.scales.getRangeText ();
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