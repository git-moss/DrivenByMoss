// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.controller.display.DisplayModel;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of the scale layout.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScaleLayoutMode extends BaseMode
{
    private Scales scales;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ScaleLayoutMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
        this.scales = model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final int sl = this.scales.getScaleLayout ().ordinal ();
        final String [] names = ScaleLayout.getNames ();
        if (index < names.length / 2)
            this.scales.setScaleLayout (ScaleLayout.getByName (names[index * 2 + sl % 2]));
        else if (index == 7)
            this.scales.setScaleLayout (ScaleLayout.getByName (names[sl / 2 * 2 + (sl % 2 == 0 ? 1 : 0)]));
        else
            return;

        this.surface.getViewManager ().getActiveView ().updateNoteMapping ();
        this.surface.getConfiguration ().setScaleLayout (this.scales.getScaleLayout ().getName ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final int sl = this.scales.getScaleLayout ().ordinal ();
        final int pos = sl / 2;
        for (int i = 0; i < ScaleLayout.getNames ().length / 2; i++)
            this.surface.updateButton (20 + i, pos == i ? AbstractMode.BUTTON_COLOR_HI : AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (25, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (26, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (27, AbstractMode.BUTTON_COLOR_ON);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final int sl = this.scales.getScaleLayout ().ordinal ();
        final int pos = sl / 2;
        final String [] names = ScaleLayout.getNames ();
        d.clear ().setBlock (1, 0, "Scale layout:");
        for (int i = 0; i < names.length; i += 2)
            d.setCell (3, i / 2, (pos == i / 2 ? PushDisplay.RIGHT_ARROW : " ") + names[i].replace (" ^", ""));
        d.setCell (3, 7, sl % 2 == 0 ? "Horizontal" : "Vertical");
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int sl = this.scales.getScaleLayout ().ordinal ();
        final int pos = sl / 2;
        final String [] names = ScaleLayout.getNames ();

        final DisplayModel message = this.surface.getDisplay ().getModel ();
        for (int i = 0; i < names.length; i += 2)
            message.addOptionElement ("", "", false, i == 0 ? "Scale layout" : "", names[i].replace (" ^", ""), pos == i / 2, false);

        message.addOptionElement ("", "", false, "", "", false, false);
        message.addOptionElement ("", "", false, "", "", false, false);
        message.addOptionElement ("", "", false, "", sl % 2 == 0 ? "Horizontal" : "Vertical", false, false);
        message.send ();
    }
}