// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteControl;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.parameter.IParameter;

import com.bitwig.extension.controller.api.AbsoluteHardwarControlBindable;
import com.bitwig.extension.controller.api.AbsoluteHardwareControl;
import com.bitwig.extension.controller.api.AbsoluteHardwareControlBinding;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareBindable;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @param <T> The type of the absolute hardware control
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractHwAbsoluteControl<T extends AbsoluteHardwareControl> extends AbstractHwContinuousControl implements IHwAbsoluteControl
{
    protected final ControllerHost               controllerHost;
    protected final T                            hardwareControl;

    private final AbsoluteHardwarControlBindable defaultAction;
    private final AbsoluteHardwarControlBindable defaultSimpleParameterAction;
    private AbsoluteHardwareControlBinding       binding;
    private IParameter                           parameter;
    private int                                  control;


    /**
     * Constructor.
     *
     * @param host The host
     * @param label The label of the control
     * @param hardwareControl The Bitwig hardware control to wrap
     */
    protected AbstractHwAbsoluteControl (final HostImpl host, final String label, final T hardwareControl)
    {
        super (host, label);

        this.hardwareControl = hardwareControl;
        this.controllerHost = host.getControllerHost ();

        this.hardwareControl.setLabel (label);

        HwUtils.markInterested (this.hardwareControl);

        this.defaultAction = this.controllerHost.createAbsoluteHardwareControlAdjustmentTarget (this::handleValue);
        this.defaultSimpleParameterAction = this.controllerHost.createAbsoluteHardwareControlAdjustmentTarget (this::handleSimpleParameterValue);
    }


    /** {@inheritDoc} */
    @Override
    public void setIndexInGroup (final int index)
    {
        this.hardwareControl.setIndexInGroup (index);
    }


    /** {@inheritDoc} */
    @Override
    public void disableTakeOver ()
    {
        this.hardwareControl.disableTakeOver ();
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final ContinuousCommand command)
    {
        super.bind (command);

        this.binding = this.hardwareControl.setBinding (this.defaultAction);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final PitchbendCommand command)
    {
        super.bind (command);
        this.binding = this.hardwareControl.addBinding (this.controllerHost.createAbsoluteHardwareControlAdjustmentTarget (this::handleValue));
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IParameter parameter)
    {
        if (this.binding != null)
            this.binding.removeBinding ();

        // Remove the previously bound Bitwig parameter
        if (this.parameter instanceof final ParameterImpl param)
            HwUtils.enableObservers (false, this.hardwareControl, param);

        HardwareBindable target = null;
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
                HwUtils.enableObservers (true, this.hardwareControl, param);
            }
            else
                target = this.defaultSimpleParameterAction;
        }

        this.parameter = parameter;
        this.binding = target == null ? null : this.hardwareControl.setBinding (target);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.input = input;
        this.type = type;
        this.channel = channel;
        this.control = control;

        this.rebind ();
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
            this.input.bind (this, this.type, this.channel, this.control);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        if (this.command != null)
        {
            this.command.execute ((int) Math.round (value * 127.0));
            return;
        }

        if (this.pitchbendCommand != null)
        {
            final double v = value * 16383.0;
            final int data1 = (int) Math.min (127, Math.round (v % 128.0));
            final int data2 = (int) Math.min (127, Math.round (v / 128.0));
            this.pitchbendCommand.onPitchbend (data1, data2);
        }
    }


    private void handleSimpleParameterValue (final double value)
    {
        if (this.parameter != null)
            this.parameter.setNormalizedValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.hardwareControl.setBounds (x, y, width, height);
    }


    /**
     * Get the Bitwig hardware control proxy.
     *
     * @return The control proxy
     */
    public T getHardwareControl ()
    {
        return this.hardwareControl;
    }
}
