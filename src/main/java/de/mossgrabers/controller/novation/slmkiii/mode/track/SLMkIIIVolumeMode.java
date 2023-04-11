// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode.track;

import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.special.ResetParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a volume parameter of all tracks.
 *
 * @author Jürgen Moßgraber
 */
public class SLMkIIIVolumeMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SLMkIIIVolumeMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super (Modes.NAME_VOLUME, surface, model);

        final VolumeParameterProvider parameterProvider = new VolumeParameterProvider (model);
        this.setParameterProvider (parameterProvider);
        this.setParameterProvider (ButtonID.DELETE, new ResetParameterProvider (parameterProvider));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Volume");

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
                d.setCell (0, i, "Volume").setCell (1, i, t.getVolumeStr (9));
            this.setColumnColors (d, i, t, SLMkIIIColorManager.SLMKIII_BLUE);
        }

        final ITrack cursorTrack = this.model.getCursorTrack ();
        d.setCell (1, 8, cursorTrack.doesExist () ? StringUtils.fixASCII (cursorTrack.getName (9)) : "");

        this.drawRow4 ();
        this.setButtonInfo (d);
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public int getModeColor ()
    {
        return SLMkIIIColorManager.SLMKIII_BLUE;
    }
}