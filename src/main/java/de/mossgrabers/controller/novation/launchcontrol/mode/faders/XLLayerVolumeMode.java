// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol.mode.faders;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.ISpecificDevice;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.device.VolumeLayerOrDrumPadParameterProvider;

import java.util.List;


/**
 * Mode for editing a volume parameter of all layers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLLayerVolumeMode extends AbstractParameterMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, ILayer>
{
    final ISpecificDevice firstInstrument;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLLayerVolumeMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (Modes.NAME_LAYER_VOLUME, surface, model, true, model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT).getLayerBank (), controls);

        this.firstInstrument = model.getSpecificDevice (DeviceID.FIRST_INSTRUMENT);

        this.setParameterProvider (new VolumeLayerOrDrumPadParameterProvider (this.firstInstrument));

        this.firstInstrument.addHasDrumPadsObserver (hasDrumPads -> this.parametersAdjusted ());
    }


    /** {@inheritDoc} */
    @Override
    public void parametersAdjusted ()
    {
        this.switchBanks (this.firstInstrument.hasDrumPads () ? this.firstInstrument.getDrumPadBank () : this.firstInstrument.getLayerBank ());

        super.parametersAdjusted ();
    }
}