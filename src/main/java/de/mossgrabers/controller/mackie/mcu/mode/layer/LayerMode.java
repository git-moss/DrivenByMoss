// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.layer;

import java.util.Arrays;
import java.util.Optional;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.SelectedLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.SendLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Mode for editing a layer parameters.
 *
 * @author Jürgen Moßgraber
 */
public class LayerMode extends AbstractLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public LayerMode (final MCUControlSurface surface, final IModel model)
    {
        super (Modes.NAME_LAYER, surface, model);

        final IParameterProvider parameterProvider;
        if (this.pinFXtoLastDevice)
            parameterProvider = new VolumeParameterProvider (model.getEffectTrackBank ());
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            if (surfaceID == 0)
                parameterProvider = new RangeFilterParameterProvider (new SelectedLayerOrDrumPadParameterProvider (getDevice (model)), 0, 8);
            else if (surfaceID == 1)
                parameterProvider = new RangeFilterParameterProvider (new SendLayerOrDrumPadParameterProvider (getDevice (model), 6), 0, 8);
            else
                parameterProvider = new EmptyParameterProvider (8);
        }
        this.setParameterProvider (parameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int [] ledModes = new int [8];
        Arrays.fill (ledModes, MCUControlSurface.KNOB_LED_MODE_WRAP);

        if (this.getExtenderOffset () == 0 && this.getParameterProvider ().get (1).doesExist ())
            ledModes[1] = MCUControlSurface.KNOB_LED_MODE_BOOST_CUT;

        this.updateKnobLEDs (ledModes);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawTrackNameHeader ()
    {
        this.drawParameterHeader ();
    }


    /** {@inheritDoc} */
    @Override
    protected void drawParameterHeader (final ITextDisplay display, final int row)
    {
        super.drawParameterHeader (display, row);

        // Overwrite the label of the first cell
        if (this.surface.getExtenderOffset () == 0 && this.configuration.isDisplayTrackNames ())
        {
            final ISpecificDevice device = getDevice (this.model);
            final IChannelBank<? extends IChannel> layerBank = device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ();
            final Optional<? extends IChannel> selectedLayer = layerBank.getSelectedItem ();
            if (selectedLayer.isEmpty ())
                return;

            display.setCell (row, 0, StringUtils.shortenAndFixASCII (selectedLayer.get ().getName (), this.getTextLength ()));
            display.done (row);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void updateItemIndices ()
    {
        final int [] indices = new int [8];
        Arrays.fill (indices, 0);
        if (this.getExtenderOffset () == 0)
        {
            final ISpecificDevice device = getDevice (this.model);
            final IChannelBank<? extends IChannel> layerBank = device.hasDrumPads () ? device.getDrumPadBank () : device.getLayerBank ();
            final Optional<? extends IChannel> selectedItem = layerBank.getSelectedItem ();
            if (selectedItem.isPresent ())
                indices[0] = selectedItem.get ().getPosition () + 1;
        }
        this.surface.setItemIndices (indices);
    }
}