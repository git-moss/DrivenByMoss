// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.parameter.IParameter;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareBindable;
import com.bitwig.extension.controller.api.RelativeHardwarControlBindable;
import com.bitwig.extension.controller.api.RelativeHardwareControlBinding;
import com.bitwig.extension.controller.api.RelativeHardwareKnob;


/**
 * Implementation of a proxy to a relative knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwRelativeKnobImpl extends AbstractHwContinuousControl implements IHwRelativeKnob
{
    private final RelativeHardwareKnob           hardwareKnob;
    private final ControllerHost                 controllerHost;
    private final RelativeEncoding               encoding;
    private final RelativeHardwarControlBindable defaultAction;
    private final RelativeHardwarControlBindable defaultSimpleParameterAction;
    private RelativeHardwareControlBinding       binding;
    private IParameter                           parameter;
    private boolean                              shouldAdaptSensitivity = true;
    private int                                  control;


    /**
     * Constructor. Uses Two's complement as the default relative encoding.
     *
     * @param host The controller host
     * @param hardwareKnob The Bitwig hardware knob
     * @param label The label of the knob
     */
    public HwRelativeKnobImpl (final HostImpl host, final RelativeHardwareKnob hardwareKnob, final String label)
    {
        this (host, hardwareKnob, label, RelativeEncoding.TWOS_COMPLEMENT);
    }


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param hardwareKnob The Bitwig hardware knob
     * @param label The label of the knob
     * @param encoding The encoding of the relative value
     */
    public HwRelativeKnobImpl (final HostImpl host, final RelativeHardwareKnob hardwareKnob, final String label, final RelativeEncoding encoding)
    {
        super (host, label);

        this.encoding = encoding;

        this.controllerHost = host.getControllerHost ();
        this.hardwareKnob = hardwareKnob;
        this.hardwareKnob.setLabel (label);

        HwUtils.markInterested (this.hardwareKnob);

        this.defaultAction = this.controllerHost.createRelativeHardwareControlAdjustmentTarget (this::handleValue);
        this.defaultSimpleParameterAction = this.controllerHost.createRelativeHardwareControlAdjustmentTarget (this::handleSimpleParameterValue);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndexInGroup (final int index)
    {
        this.hardwareKnob.setIndexInGroup (index);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final ContinuousCommand command)
    {
        super.bind (command);

        this.binding = this.hardwareKnob.setBinding (this.defaultAction);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IParameter parameter)
    {
        if (this.binding != null)
            this.binding.removeBinding ();

        // Remove the previously bound Bitwig parameter
        if (this.parameter instanceof final ParameterImpl param)
            HwUtils.enableObservers (false, this.hardwareKnob, param);

        final HardwareBindable target;
        if (parameter == null)
        {
            // No Bitwig parameter, use the default action
            target = this.defaultAction;
        }
        else
        {
            // Is parameter a real Bitwig parameter? If yes, map it.
            if (parameter instanceof final ParameterImpl param)
            {
                target = param.getParameter ();
                HwUtils.enableObservers (true, this.hardwareKnob, param);
            }
            else
                target = this.defaultSimpleParameterAction;
        }

        this.parameter = parameter;
        this.binding = target == null ? null : this.hardwareKnob.setBinding (target);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.input = input;
        this.type = type;
        this.channel = channel;
        this.control = control;

        input.bind (this, type, channel, control, this.encoding);
    }


    /** {@inheritDoc} */
    @Override
    public void unbind ()
    {
        if (this.input != null)
            this.input.unbind (this);
    }


    /** {@inheritDoc} */
    @Override
    public void rebind ()
    {
        if (this.input != null)
            this.input.bind (this, this.type, this.channel, this.control, this.encoding);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final TriggerCommand command, final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.touchCommand = command;

        this.hardwareKnob.beginTouchAction ().addBinding (this.controllerHost.createAction ( () -> this.triggerTouch (true), () -> ""));
        this.hardwareKnob.endTouchAction ().addBinding (this.controllerHost.createAction ( () -> this.triggerTouch (false), () -> ""));

        input.bindTouch (this, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        if (this.command == null)
            return;

        // Convert the value back from the default 2s relative matcher, because we do the conversion
        // our own way
        final double a = value * 61.0;
        final int v = (int) (a > 0 ? Math.ceil (a) : Math.floor (a));
        this.command.execute (v < 0 ? v + 128 : v);
    }


    private void handleSimpleParameterValue (final double value)
    {
        if (this.parameter == null)
            return;

        // Convert the value back from the default 2s relative matcher, because we do the conversion
        // our own way
        final double a = value * 61.0;
        final int v = (int) (a > 0 ? Math.ceil (a) : Math.floor (a));
        this.parameter.changeValue (v);
    }


    /**
     * Get the Bitwig hardware knob proxy.
     *
     * @return The knob proxy
     */
    public RelativeHardwareKnob getHardwareKnob ()
    {
        return this.hardwareKnob;
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.hardwareKnob.setBounds (x, y, width, height);
    }


    /** {@inheritDoc} */
    @Override
    public void setSensitivity (final double sensitivity)
    {
        this.hardwareKnob.setSensitivity (TwosComplementValueChanger.rescale (sensitivity));
    }


    /** {@inheritDoc} */
    @Override
    public boolean shouldAdaptSensitivity ()
    {
        return this.shouldAdaptSensitivity;
    }


    /** {@inheritDoc} */
    @Override
    public void setShouldAdaptSensitivity (final boolean shouldAdaptSensitivity)
    {
        this.shouldAdaptSensitivity = shouldAdaptSensitivity;
    }
}
