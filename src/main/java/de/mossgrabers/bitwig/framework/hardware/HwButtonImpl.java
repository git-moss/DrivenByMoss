// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.AbstractHwButton;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.daw.midi.IMidiInput;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareButton;


/**
 * Implementation of a proxy to a button on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwButtonImpl extends AbstractHwButton
{
    private final HardwareButton hardwareButton;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param hardwareButton The Bitwig hardware button
     * @param label The label of the button
     */
    public HwButtonImpl (final HostImpl host, final HardwareButton hardwareButton, final String label)
    {
        super (host, label);

        this.hardwareButton = hardwareButton;
        this.hardwareButton.setLabel (label);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final TriggerCommand command)
    {
        this.command = command;

        final ControllerHost controllerHost = ((HostImpl) this.host).getControllerHost ();
        this.hardwareButton.pressedAction ().addBinding (controllerHost.createAction (this::handleButtonPressed, () -> ""));
        this.hardwareButton.releasedAction ().addBinding (controllerHost.createAction (this::handleButtonRelease, () -> ""));
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        input.bind (this, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void unbind (final IMidiInput input)
    {
        input.unbind (this);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control, final int value)
    {
        input.bind (this, type, channel, control, value);
    }


    /** {@inheritDoc} */
    @Override
    public void addLight (final IHwLight light)
    {
        super.addLight (light);

        this.hardwareButton.setBackgroundLight (((HwLightImpl) light).hardwareLight);
    }


    /**
     * Get the Bitwig hardware button proxy.
     *
     * @return The button proxy
     */
    public HardwareButton getHardwareButton ()
    {
        return this.hardwareButton;
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.hardwareButton.setBounds (x, y, width, height);
    }
}
