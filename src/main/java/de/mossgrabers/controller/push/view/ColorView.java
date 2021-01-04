// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IClip;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The Color view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorView extends AbstractView<PushControlSurface, PushConfiguration>
{
    /** What should the color be selected for? */
    public enum SelectMode
    {
        /** Select a track color. */
        MODE_TRACK,
        /** Select a layer color. */
        MODE_LAYER,
        /** Select a clip color. */
        MODE_CLIP
    }


    private SelectMode mode;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ColorView (final PushControlSurface surface, final IModel model)
    {
        super ("Color", surface, model);
        this.mode = SelectMode.MODE_TRACK;
    }


    /**
     * Set the color selections mode.
     *
     * @param mode The selection mode
     */
    public void setMode (final SelectMode mode)
    {
        this.mode = mode;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final DAWColor [] dawColors = DAWColor.values ();
        for (int i = 0; i < 64; i++)
            padGrid.light (36 + i, i < dawColors.length ? dawColors[i].name () : IPadGrid.GRID_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int color = note - 36;
        final DAWColor [] dawColors = DAWColor.values ();
        if (color < dawColors.length)
        {
            final ColorEx entry = dawColors[color].getColor ();
            switch (this.mode)
            {
                case MODE_TRACK:
                    final ITrack cursorTrack = this.model.getCursorTrack ();
                    if (cursorTrack.doesExist ())
                        cursorTrack.setColor (entry);
                    else
                    {
                        final IMasterTrack master = this.model.getMasterTrack ();
                        if (master.isSelected ())
                            master.setColor (entry);
                    }
                    break;

                case MODE_LAYER:
                    this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ().setColor (entry);
                    break;

                case MODE_CLIP:
                    final IClip clip = this.model.getCursorClip ();
                    if (clip.doesExist ())
                        clip.setColor (entry);
                    break;
            }
        }
        this.surface.getViewManager ().restore ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return AbstractFeatureGroup.BUTTON_COLOR_OFF;
    }
}