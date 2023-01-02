// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.graphics.canvas.component.TitleChannelsComponent;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;

import java.util.Optional;


/**
 * The track mixer mode. Identical to the track mode but with a visualization of all 16 tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FireTrackMixerMode extends FireTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireTrackMixerMode (final FireControlSurface surface, final IModel model)
    {
        super ("Mixer", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateMode ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        final Optional<ITrack> trackOptional = trackBank.getSelectedItem ();
        String label = "None";
        if (trackOptional.isPresent ())
        {
            final ITrack track = trackOptional.get ();
            label = track.getPosition () + 1 + ": " + track.getName (9);
        }

        final int size = trackBank.getPageSize ();
        final boolean [] selected = new boolean [size];
        final int [] values = new int [size];

        final boolean isMode = this.isAnyKnobTouched () || !this.model.getTransport ().isPlaying ();
        if (isMode)
        {
            switch (this.selectedParameter)
            {
                case VOLUME:
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = trackBank.getItem (i);
                        selected[i] = channel.isSelected ();
                        values[i] = channel.getVolume ();
                    }
                    break;

                case PAN:
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = trackBank.getItem (i);
                        selected[i] = channel.isSelected ();
                        values[i] = channel.getPan ();
                    }
                    break;

                case SEND1:
                case SEND2:
                case SEND3:
                case SEND4:
                case SEND5:
                case SEND6:
                    final int sendIndex = this.selectedParameter.ordinal () - Modes.SEND1.ordinal ();
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = trackBank.getItem (i);
                        selected[i] = channel.isSelected ();

                        final ISendBank sendBank = channel.getSendBank ();
                        if (sendBank != null)
                        {
                            final ISend send = sendBank.getItem (sendIndex);
                            if (send.doesExist ())
                                values[i] = send.getValue ();
                        }
                    }
                    break;

                default:
                    // Not used
                    break;
            }
        }
        else
        {
            for (int i = 0; i < size; i++)
            {
                final IChannel channel = trackBank.getItem (i);
                selected[i] = channel.isSelected ();
                values[i] = channel.getVu ();
            }
        }

        display.addElement (new TitleChannelsComponent (label, selected, values, this.selectedParameter == Modes.PAN && isMode));
        display.send ();
    }
}
