// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.daw.midi.IMidiInput;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwAbsoluteKnobImpl extends AbstractHwAbsoluteControl<AbsoluteHardwareKnob> implements IHwAbsoluteKnob
{
    private final AbsoluteHardwareKnob hardwareKnob;


    /**
     * Constructor.
     *
     * @param host The controller host
     * @param hardwareKnob The Bitwig hardware knob
     * @param label The label of the knob
     */
    public HwAbsoluteKnobImpl (final HostImpl host, final AbsoluteHardwareKnob hardwareKnob, final String label)
    {
        super (host, label, hardwareKnob);

        this.hardwareKnob = hardwareKnob;
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
}
