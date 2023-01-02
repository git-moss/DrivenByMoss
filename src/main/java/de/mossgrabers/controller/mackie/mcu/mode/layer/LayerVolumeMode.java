// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.layer;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.VolumeLayerOrDrumPadParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;


/**
 * Mode for editing a volume parameter of all layers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerVolumeMode extends AbstractLayerMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public LayerVolumeMode (final MCUControlSurface surface, final IModel model)
    {
        super (Modes.NAME_LAYER_VOLUME, surface, model);

        final IParameterProvider parameterProvider;
        if (this.pinFXtoLastDevice)
            parameterProvider = new VolumeParameterProvider (model.getEffectTrackBank ());
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            parameterProvider = new RangeFilterParameterProvider (new VolumeLayerOrDrumPadParameterProvider (model.getCursorDevice ()), surfaceID * 8, 8);
        }
        this.setParameterProvider (parameterProvider);
    }
}