// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode.track;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final SLMkIIIControlSurface surface, final IModel model)
    {
        super ("Track", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            case 0:
                if (this.surface.isDeletePressed ())
                    selectedTrack.resetVolume ();
                else
                    selectedTrack.changeVolume (value);
                return;
            case 1:
                if (this.surface.isDeletePressed ())
                    selectedTrack.resetPan ();
                else
                    selectedTrack.changePan (value);
                return;
            default:
                final ISend send = selectedTrack.getSendBank ().getItem (index - 2);
                if (this.surface.isDeletePressed ())
                    send.resetValue ();
                else
                    send.changeValue (value);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();
        d.clear ();
        d.setCell (0, 8, "Track");

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final ITrack t = this.model.getSelectedTrack ();
        if (t == null)
        {
            d.setBlock (1, 1, " Please  select a").setBlock (1, 2, "track.");
            d.setCell (1, 8, "");
            d.hideAllElements ();
        }
        else
        {
            d.setCell (0, 0, "Volume").setCell (1, 0, t.getVolumeStr (9));
            d.setPropertyColor (0, 0, SLMkIIIColors.SLMKIII_BLUE);
            d.setPropertyColor (0, 1, SLMkIIIColors.SLMKIII_BLUE);
            this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_1, valueChanger.toMidiValue (t.getVolume ()));

            d.setCell (0, 1, "Pan").setCell (1, 1, t.getPanStr (9));
            d.setPropertyColor (1, 0, SLMkIIIColors.SLMKIII_ORANGE);
            d.setPropertyColor (1, 1, SLMkIIIColors.SLMKIII_ORANGE);
            this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_1 + 1, valueChanger.toMidiValue (t.getPan ()));

            final ISendBank sendBank = t.getSendBank ();
            for (int i = 0; i < 6; i++)
            {
                final int pos = 2 + i;

                int color = SLMkIIIColors.SLMKIII_BLACK;
                if (sendBank.getItemCount () > 0)
                {
                    final ISend send = sendBank.getItem (i);
                    if (send.doesExist ())
                    {
                        d.setCell (0, pos, send.getName (9)).setCell (1, pos, send.getDisplayedValue (9));
                        color = SLMkIIIColors.SLMKIII_YELLOW;

                        this.surface.updateContinuous (SLMkIIIControlSurface.MKIII_KNOB_1 + pos, valueChanger.toMidiValue (send.getValue ()));
                    }
                }

                d.setPropertyColor (pos, 0, color);
                d.setPropertyColor (pos, 1, color);
            }

            d.setCell (1, 8, StringUtils.fixASCII (t.getName (9)));
        }

        d.setPropertyColor (8, 0, SLMkIIIColors.SLMKIII_GREEN);

        this.drawRow4 ();
        this.setButtonInfo (d);
        d.allDone ();
    }
}