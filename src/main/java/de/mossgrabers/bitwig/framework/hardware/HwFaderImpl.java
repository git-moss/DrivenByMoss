// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwFader;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.utils.ButtonEvent;

import com.bitwig.extension.controller.api.HardwareSlider;


/**
 * Implementation of a proxy to a fader on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwFaderImpl extends AbstractHwAbsoluteControl<HardwareSlider> implements IHwFader
{
    /**
     * Constructor.
     *
     * @param host The controller host
     * @param hardwareControl The Bitwig hardware fader
     * @param label The label of the fader
     * @param isVertical True if the fader is vertical, otherwise horizontal
     */
    public HwFaderImpl (final HostImpl host, final HardwareSlider hardwareControl, final String label, final boolean isVertical)
    {
        super (host, label, hardwareControl);

        this.hardwareControl.setIsHorizontal (!isVertical);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final TriggerCommand command, final IMidiInput input, final BindType type, final int channel, final int control)
    {
        this.touchCommand = command;

        this.hardwareControl.beginTouchAction ().addBinding (this.controllerHost.createAction ( () -> this.touchCommand.execute (ButtonEvent.DOWN, 127), () -> ""));
        this.hardwareControl.endTouchAction ().addBinding (this.controllerHost.createAction ( () -> this.touchCommand.execute (ButtonEvent.UP, 0), () -> ""));

        input.bindTouch (this, type, channel, control);
    }
}
