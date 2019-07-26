// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.view;

import de.mossgrabers.controller.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The Color view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorView extends AbstractView<SLMkIIIControlSurface, SLMkIIIConfiguration> implements SceneView
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
        final PadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < 16; i++)
        {
            final int pos = (this.flip ? 16 : 0) + i;
            padGrid.light (36 + i, pos < DAWColors.DAW_COLORS.length ? DAWColors.DAW_COLORS[pos] : PadGrid.GRID_OFF);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int color = note - 36 + (this.flip ? 16 : 0);
        if (color < DAWColors.DAW_COLORS.length)
        {
            final double [] entry = DAWColors.getColorEntry (DAWColors.DAW_COLORS[color]);
            final ITrack t = this.model.getSelectedTrack ();
            if (t == null)
            {
                final IMasterTrack master = this.model.getMasterTrack ();
                if (master.isSelected ())
                    master.setColor (entry[0], entry[1], entry[2]);
            }
            else
                t.setColor (entry[0], entry[1], entry[2]);
        }
        this.surface.getViewManager ().restoreView ();
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final int colorOff = this.model.getColorManager ().getColor (AbstractSequencerView.COLOR_RESOLUTION_OFF);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_SCENE_1, colorOff);
        this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_SCENE_2, colorOff);
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