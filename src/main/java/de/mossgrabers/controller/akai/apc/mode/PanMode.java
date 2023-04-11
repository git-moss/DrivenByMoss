// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apc.mode;

import de.mossgrabers.controller.akai.apc.controller.APCControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;


/**
 * Panorama knob mode.
 *
 * @author Jürgen Moßgraber
 */
public class PanMode extends BaseMode<ITrack>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public PanMode (final APCControlSurface surface, final IModel model)
    {
        super ("Panorama", surface, model, APCControlSurface.LED_MODE_PAN, model.getCurrentTrackBank ());

        this.setParameterProvider (new PanParameterProvider (model));
    }
}