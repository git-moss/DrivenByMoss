// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.daw.IHost;

import com.bitwig.extension.controller.api.InternalHardwareLightState;
import com.bitwig.extension.controller.api.MultiStateHardwareLight;
import com.bitwig.extension.controller.api.ObjectHardwareProperty;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Implementation of a proxy to a light / LED on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwLightImpl extends AbstractHwControl implements IHwLight
{
    final MultiStateHardwareLight                      hardwareLight;
    private final Supplier<InternalHardwareLightState> valueSupplier;


    /**
     * Constructor.
     *
     * @param host The host
     * @param hardwareLight The Bitwig hardware light
     * @param valueSupplier The value supplier for the light
     * @param hardwareUpdater The hardware updater for the light
     */
    public HwLightImpl (final IHost host, final MultiStateHardwareLight hardwareLight, final Supplier<InternalHardwareLightState> valueSupplier, final Consumer<InternalHardwareLightState> hardwareUpdater)
    {
        super (host, null);

        this.hardwareLight = hardwareLight;
        this.valueSupplier = valueSupplier;

        final ObjectHardwareProperty<InternalHardwareLightState> state = hardwareLight.state ();
        state.setValueSupplier (valueSupplier);
        state.onUpdateHardware (hardwareUpdater);
    }


    /** {@inheritDoc} */
    @Override
    public void forceFlush ()
    {
        // Workaround for missing clear cache method
        this.turnOff ();
        this.host.scheduleTask ( () -> this.hardwareLight.state ().setValueSupplier (this.valueSupplier), 100);
    }


    /** {@inheritDoc} */
    @Override
    public void turnOff ()
    {
        this.hardwareLight.state ().setValueSupplier ( () -> null);
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.hardwareLight.setBounds (x, y, width, height);
    }
}
