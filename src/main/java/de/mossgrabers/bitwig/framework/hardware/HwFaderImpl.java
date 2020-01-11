// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.bitwig.framework.daw.data.ParameterImpl;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.command.core.PitchbendCommand;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.AbstractHwContinuousControl;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.utils.ButtonEvent;

import com.bitwig.extension.controller.api.AbsoluteHardwarControlBindable;
import com.bitwig.extension.controller.api.AbsoluteHardwareControlBinding;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.HardwareBindable;
import com.bitwig.extension.controller.api.HardwareSlider;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwFaderImpl extends AbstractHwContinuousControl implements IHwFader
{
    private final HardwareSlider           hardwareFader;
    private final ControllerHost           controllerHost;
    private AbsoluteHardwarControlBindable defaultAction;
    private AbsoluteHardwareControlBinding binding;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param hardwareFader The Bitwig hardware fader
     * @param label The label of the fader
     * @param isVertical True if the fader is vertical, otherwise horizontal
     */
    public HwFaderImpl (final HostImpl host, final HardwareSlider hardwareFader, final String label, final boolean isVertical)
    {
        super (host, label);

        this.controllerHost = host.getControllerHost ();
        this.hardwareFader = hardwareFader;
        this.hardwareFader.setLabel (label);
        this.hardwareFader.setIsHorizontal (!isVertical);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final ContinuousCommand command)
    {
        super.bind (command);

        this.defaultAction = this.controllerHost.createAbsoluteHardwareControlAdjustmentTarget (this::handleValue);
        this.hardwareFader.addBinding (this.defaultAction);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final PitchbendCommand command)
    {
        super.bind (command);
        this.hardwareFader.addBinding (this.controllerHost.createAbsoluteHardwareControlAdjustmentTarget (this::handleValue));
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IParameter parameter)
    {
        if (this.binding != null)
            this.binding.removeBinding ();

        final HardwareBindable target = parameter == null ? this.defaultAction : ((ParameterImpl) parameter).getParameter ();
        this.binding = this.hardwareFader.setBinding (target);
    }


    /** {@inheritDoc} */
    @Override
    public void bind (final IMidiInput input, final BindType type, final int channel, final int control)
    {
        input.bind (this, type, channel, control);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final TriggerCommand command, final IMidiInput input, final BindType type, final int control)
    {
        this.touchCommand = command;

        this.hardwareFader.beginTouchAction ().addBinding (this.controllerHost.createAction ( () -> this.touchCommand.execute (ButtonEvent.DOWN, 127), () -> ""));
        this.hardwareFader.endTouchAction ().addBinding (this.controllerHost.createAction ( () -> this.touchCommand.execute (ButtonEvent.UP, 0), () -> ""));

        input.bindTouch (this, type, 0, control);
    }


    /** {@inheritDoc} */
    @Override
    public void handleValue (final double value)
    {
        if (this.command != null)
            this.command.execute ((int) Math.round (value * 127.0));
        else if (this.pitchbendCommand != null)
        {
            final double v = value * 16383.0;
            final int data1 = (int) Math.min (127, Math.round (v % 128.0));
            final int data2 = (int) Math.min (127, Math.round (v / 128.0));
            this.pitchbendCommand.onPitchbend (data1, data2);
        }
    }


    /**
     * Get the Bitwig hardware fader proxy.
     *
     * @return The fader proxy
     */
    public HardwareSlider getHardwareFader ()
    {
        return this.hardwareFader;
    }


    /** {@inheritDoc} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.hardwareFader.setBounds (x, y, width, height);
    }
}
