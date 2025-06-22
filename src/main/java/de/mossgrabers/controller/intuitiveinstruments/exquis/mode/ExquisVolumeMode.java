// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;


/**
 * The track volume mode for the Exquis.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisVolumeMode extends TrackVolumeMode<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ExquisVolumeMode (final ExquisControlSurface surface, final IModel model)
    {
        super (surface, model, false, ExquisControlSurface.KNOBS);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobColor (int index)
    {
        final String colorID = this.model.getTrackBank ().getSelectedChannelColorEntry ();
        return this.colorManager.getColorIndex (colorID);
    }
}
