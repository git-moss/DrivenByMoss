// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.scale.ScaleLayout;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of the scale layout.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ScaleLayoutMode extends BaseMode<IItem>
{
    private final Scales scales;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ScaleLayoutMode (final PushControlSurface surface, final IModel model)
    {
        super ("Scale Layout", surface, model);

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

        this.surface.getViewManager ().getActive ().updateNoteMapping ();
        this.surface.getConfiguration ().setScaleLayout (this.scales.getScaleLayout ().getName ());
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final int sl = this.scales.getScaleLayout ().ordinal ();
            final int pos = sl / 2;
            if (index < ScaleLayout.getNames ().length / 2)
                return pos == index ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON;
            return index == 7 ? AbstractFeatureGroup.BUTTON_COLOR_ON : AbstractFeatureGroup.BUTTON_COLOR_OFF;
        }

        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final int sl = this.scales.getScaleLayout ().ordinal ();
        final int pos = sl / 2;
        final String [] names = ScaleLayout.getNames ();
        display.setBlock (1, 0, "Scale layout:");
        for (int i = 0; i < names.length; i += 2)
            display.setCell (3, i / 2, (pos == i / 2 ? Push1Display.SELECT_ARROW : " ") + names[i].replace (" ^", ""));
        display.setCell (3, 7, sl % 2 == 0 ? "Horizontal" : "Vertical");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final int sl = this.scales.getScaleLayout ().ordinal ();
        final int pos = sl / 2;
        final String [] names = ScaleLayout.getNames ();

        for (int i = 0; i < names.length; i += 2)
            display.addOptionElement ("", "", false, i == 0 ? "Scale layout" : "", names[i].replace (" ^", ""), pos == i / 2, false);

        display.addOptionElement ("", "", false, "", "", false, false);
        display.addOptionElement ("", "", false, "", sl % 2 == 0 ? "Horizontal" : "Vertical", false, false);
    }
}