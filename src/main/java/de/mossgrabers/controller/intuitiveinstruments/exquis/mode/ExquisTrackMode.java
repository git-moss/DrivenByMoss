// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis.mode;

import de.mossgrabers.controller.intuitiveinstruments.exquis.ExquisConfiguration;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisColorManager;
import de.mossgrabers.controller.intuitiveinstruments.exquis.controller.ExquisControlSurface;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.track.TrackMode;


/**
 * The track mode for the Exquis.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisTrackMode extends TrackMode<ExquisControlSurface, ExquisConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ExquisTrackMode (final ExquisControlSurface surface, final IModel model)
    {
        super (surface, model, false, ExquisControlSurface.KNOBS);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobColor (final int index)
    {
        if (index != 0)
            return ExquisColorManager.FIRST_DAW_COLOR_INDEX + DAWColor.DAW_COLOR_SILVER.ordinal ();
        final String colorID = this.model.getTrackBank ().getSelectedChannelColorEntry ();
        final int colorIndex = this.colorManager.getColorIndex (colorID);
        return colorIndex;
    }
}
