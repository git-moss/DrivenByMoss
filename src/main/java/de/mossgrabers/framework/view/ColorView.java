// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.controller.ableton.push.mode.device.DeviceLayerDetailsMode;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.mode.Modes;

import java.util.Optional;


/**
 * The Color view. Presents all DAW colors for selection. Depending on the mode it is applied to the
 * cursor track, layer or clip. If the colors do not fit on the grid it is paged into several color
 * pages which can be changed via the setPage method.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ColorView<S extends IControlSurface<C>, C extends Configuration> extends AbstractView<S, C>
{
    private final int       pageSize;
    private final int       pages;
    private int             page = 0;
    private ColorSelectMode mode;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ColorView (final S surface, final IModel model)
    {
        super ("Color", surface, model);

        this.mode = ColorSelectMode.MODE_TRACK;

        final IPadGrid padGrid = this.surface.getPadGrid ();
        this.pageSize = padGrid.getCols () * padGrid.getRows ();

        final DAWColor [] dawColors = DAWColor.values ();
        this.pages = 1 + (dawColors.length - 1) / this.pageSize;
    }


    /**
     * Set the color selections mode.
     *
     * @param mode The selection mode
     */
    public void setMode (final ColorSelectMode mode)
    {
        this.mode = mode;
    }


    /**
     * Select the currently active color page.
     *
     * @param page The page index to set
     */
    public void setPage (final int page)
    {
        this.page = Math.max (0, Math.min (page, this.pages - 1));
    }


    /**
     * Get the currently active color page.
     *
     * @return The currently active color page
     */
    public int getPage ()
    {
        return this.page;
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final DAWColor [] dawColors = DAWColor.values ();
        final int offset = this.page * this.pageSize;
        for (int i = 0; i < this.pageSize; i++)
        {
            final int index = offset + i;
            padGrid.light (36 + i, index < dawColors.length ? dawColors[index].name () : IPadGrid.GRID_OFF);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity > 0)
            return;

        final int offset = this.page * this.pageSize;
        final int color = offset + note - 36;

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
                    final IBank<? extends ILayer> layerBank = this.getLayerBank ();
                    final Optional<?> selectedItem = layerBank.getSelectedItem ();
                    if (selectedItem.isPresent ())
                        ((ILayer) selectedItem.get ()).setColor (entry);
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


    private IBank<? extends ILayer> getLayerBank ()
    {
        final IMode mode = this.surface.getModeManager ().get (Modes.DEVICE_LAYER_DETAILS);
        return ((DeviceLayerDetailsMode) mode).getBank ();
    }
}