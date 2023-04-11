// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.mode;

import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.graphics.canvas.component.TitleChannelsComponent;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.ILayerBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.mode.Modes;

import java.util.Optional;


/**
 * The layer mixer mode. Identical to the layer mode but with a visualization of all 16 layers.
 *
 * @author Jürgen Moßgraber
 */
public class FireLayerMixerMode extends FireLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FireLayerMixerMode (final FireControlSurface surface, final IModel model)
    {
        super ("Mixer", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.updateMode ();

        final IGraphicDisplay display = this.surface.getGraphicsDisplay ();

        final ISpecificDevice cd = this.model.getDrumDevice ();
        final ILayerBank layerBank = cd.getLayerBank ();
        final Optional<ILayer> channelOptional = layerBank.getSelectedItem ();
        String label = "None";
        if (channelOptional.isPresent ())
        {
            final IChannel channel = channelOptional.get ();
            label = channel.getPosition () + 1 + ": " + channel.getName (9);
        }

        final int size = layerBank.getPageSize ();
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
                        final IChannel channel = layerBank.getItem (i);
                        selected[i] = channel.isSelected ();
                        values[i] = channel.getVolume ();
                    }
                    break;

                case PAN:
                    for (int i = 0; i < size; i++)
                    {
                        final IChannel channel = layerBank.getItem (i);
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
                        final IChannel channel = layerBank.getItem (i);
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
                final IChannel channel = layerBank.getItem (i);
                selected[i] = channel.isSelected ();
                values[i] = channel.getVu ();
            }
        }

        display.addElement (new TitleChannelsComponent (label, selected, values, this.selectedParameter == Modes.PAN && isMode));
        display.send ();
    }
}
