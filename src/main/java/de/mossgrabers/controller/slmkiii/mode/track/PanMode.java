// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode.track;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a panorama parameter of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PanMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Panorama", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        if (this.surface.isDeletePressed ())
            track.resetPan ();
        else
            track.changePan (value);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Pan");

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            if (t.doesExist ())
                d.setCell (0, i, "Pan").setCell (1, i, t.getPanStr (9));

            this.surface.updateTrigger (SLMkIIIControlSurface.MKIII_KNOB_1 + i, 15, valueChanger.toMidiValue (t.getPan ()));
            this.setColumnColors (d, i, t, SLMkIIIColors.SLMKIII_AMBER);
        }

        final ITrack t = this.model.getSelectedTrack ();
        d.setCell (1, 8, t == null ? "" : StringUtils.fixASCII (t.getName (9)));

        d.setPropertyColor (8, 0, SLMkIIIColors.SLMKIII_AMBER);

        this.drawRow4 ();
        this.setButtonInfo (d);
        d.allDone ();
    }
}