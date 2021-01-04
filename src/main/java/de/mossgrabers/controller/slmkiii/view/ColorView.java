// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


/**
 * The Color view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorView extends AbstractView<SLMkIIIControlSurface, SLMkIIIConfiguration>
{
    private boolean flip = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ColorView (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Color", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        ((SessionView) this.surface.getViewManager ().get (Views.SESSION)).drawLightGuide (this.surface.getLightGuide ());

        final IPadGrid padGrid = this.surface.getPadGrid ();
        final DAWColor [] dawColors = DAWColor.values ();
        for (int i = 0; i < 16; i++)
        {
            final int pos = (this.flip ? 16 : 0) + i;
            padGrid.light (36 + i, pos < dawColors.length ? dawColors[pos].name () : IPadGrid.GRID_OFF);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int color = note - 36 + (this.flip ? 16 : 0);
        final DAWColor [] dawColors = DAWColor.values ();
        if (color < dawColors.length)
        {
            final ColorEx entry = dawColors[color].getColor ();
            final ITrack cursorTrack = this.model.getCursorTrack ();
            if (cursorTrack.doesExist ())
                cursorTrack.setColor (entry);
            else
            {
                final IMasterTrack master = this.model.getMasterTrack ();
                if (master.isSelected ())
                    master.setColor (entry);
            }
        }
        this.surface.getViewManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return AbstractSequencerView.COLOR_RESOLUTION_OFF;
    }


    /**
     * Select the first or last 16 colors.
     *
     * @param flip Select colors 1-16 is false and colors 17-32 if true.
     */
    public void setFlip (final boolean flip)
    {
        this.flip = flip;
    }


    /**
     * Return true if flipped.
     *
     * @return True if flipped
     */
    public boolean isFlip ()
    {
        return this.flip;
    }
}