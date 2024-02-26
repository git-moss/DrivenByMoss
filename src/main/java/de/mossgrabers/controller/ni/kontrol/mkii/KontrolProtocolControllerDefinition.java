// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import java.util.List;

import de.mossgrabers.controller.ni.kontrol.mkii.controller.IKontrolProtocolDeviceDescriptor;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the Kontrol MkII extension using the NI MIDI host protocol.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolControllerDefinition extends DefaultControllerDefinition
{
    private final IKontrolProtocolDeviceDescriptor deviceDescriptor;


    /**
     * Constructor.
     *
     * @param deviceDescriptor The NIHIA protocol version descriptor
     */
    public KontrolProtocolControllerDefinition (final IKontrolProtocolDeviceDescriptor deviceDescriptor)
    {
        super (deviceDescriptor.getID (), deviceDescriptor.getName (), "Native Instruments", 2, 2);

        this.deviceDescriptor = deviceDescriptor;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.deviceDescriptor.getMidiDiscoveryPairs (os);
    }
}
