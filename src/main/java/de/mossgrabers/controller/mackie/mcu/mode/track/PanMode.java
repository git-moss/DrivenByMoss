// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;


/**
 * Mode for editing the panorama of all tracks.
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
    public PanMode (final MCUControlSurface surface, final IModel model)
    {
        super (Modes.NAME_PANORAMA, surface, model);

        final IParameterProvider parameterProvider;
        if (this.pinFXtoLastDevice)
            parameterProvider = new PanParameterProvider (model.getEffectTrackBank ());
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            parameterProvider = new RangeFilterParameterProvider (new PanParameterProvider (model), surfaceID * 8, 8);
        }
        this.setParameterProvider (parameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        this.updateKnobLEDs (LED_MODES_BOOST_CUT);
    }
}