// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.view;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ColorManager;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * The Solo view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SoloView extends BaseView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public SoloView (final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super ("Solo", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeFunction (final int padIndex)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (padIndex);

        final MaschineMikroMk3Configuration configuration = this.surface.getConfiguration ();
        if (configuration.isDuplicateEnabled ())
        {
            track.duplicate ();
            this.disableDuplicate ();
            return;
        }

        track.toggleSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 16; i++)
        {
            final ITrack item = tb.getItem (i);
            final int x = i % 4;
            final int y = 3 - i / 4;
            if (item.doesExist ())
            {
                if (item.isSolo ())
                    padGrid.lightEx (x, y, DAWColor.getColorIndex (item.getColor ()));
                else
                    padGrid.lightEx (x, y, MaschineMikroMk3ColorManager.COLOR_GREY);
            }
            else
                padGrid.lightEx (x, y, AbstractMode.BUTTON_COLOR_OFF);
        }
    }
}