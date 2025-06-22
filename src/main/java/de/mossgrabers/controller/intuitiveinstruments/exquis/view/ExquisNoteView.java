// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.view;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.scale.Scales;


/**
 * The note view. Mostly handled by the device itself.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisNoteView extends AbstractView<ExquisControlSurface, ExquisConfiguration>
{
    private int cachedTempo = -1;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ExquisNoteView (final ExquisControlSurface surface, final IModel model)
    {
        super ("Note", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.surface.configureDeveloperMode (ExquisControlSurface.DEV_MODE_PLAY_MODE);

        super.onActivate ();
        this.surface.forceFlush ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Drawn by the device itself but let's update the tempo-setting

        final double tempo = this.model.getTransport ().getTempo ();
        final int intTempo = Math.clamp (Math.round (tempo), 20, 240);
        if (this.cachedTempo != intTempo)
        {
            this.cachedTempo = intTempo;
            this.surface.updateTempo (intTempo);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return this.colorManager.getColorIndex (IPadGrid.GRID_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.scheduleTask ( () -> this.delayedUpdateNoteMapping (Scales.getIdentityMatrix ()), 100);
    }
}
