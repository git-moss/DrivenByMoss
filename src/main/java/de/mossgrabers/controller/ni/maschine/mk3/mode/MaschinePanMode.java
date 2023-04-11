// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.mode;

import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a pan parameter of all tracks.
 *
 * @author Jürgen Moßgraber
 */
public class MaschinePanMode extends TrackPanMode<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public MaschinePanMode (final MaschineControlSurface surface, final IModel model)
    {
        super (surface, model, false, surface.getMaschine ().hasMCUDisplay () ? DEFAULT_KNOB_IDS : null);

        this.initTouchedStates (9);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            String name = StringUtils.shortenAndFixASCII (t.getName (), 6);
            if (t.isSelected ())
                name = ">" + name;
            d.setCell (0, i, name);
            d.setCell (1, i, t.getPanStr (6));
        }
        d.allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        super.onKnobTouch (index == 8 ? -1 : index, isTouched);
    }
}
