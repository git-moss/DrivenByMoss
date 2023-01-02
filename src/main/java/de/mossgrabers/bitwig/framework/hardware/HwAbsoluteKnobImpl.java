// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.bitwig.framework.daw.HostImpl;
import de.mossgrabers.framework.command.core.TriggerCommand;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.daw.midi.IMidiInput;

import com.bitwig.extension.controller.api.AbsoluteHardwareKnob;


/**
 * Implementation of a proxy to an absolute knob on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwAbsoluteKnobImpl extends AbstractHwAbsoluteControl<AbsoluteHardwareKnob> implements IHwAbsoluteKnob
{
    /**
     * Constructor.
     *
     * @param host The controller host
     * @param hardwareControl The Bitwig hardware knob
     * @param label The label of the knob
     */
    public HwAbsoluteKnobImpl (final HostImpl host, final AbsoluteHardwareKnob hardwareControl, final String label)
    {
        super (host, label, hardwareControl);
    }


    /** {@inheritDoc} */
    @Override
    public void bindTouch (final TriggerCommand command, final IMidiInput input, final BindType type, final int channel, final int control)
    {
        // No touch on absolute knob
    }
}
