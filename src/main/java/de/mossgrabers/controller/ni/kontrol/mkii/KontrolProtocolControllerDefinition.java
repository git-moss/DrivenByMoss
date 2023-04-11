// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii;

import de.mossgrabers.controller.ni.kontrol.mkii.controller.IKontrolProtocolDeviceDescriptor;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;


/**
 * Definition class for the Kontrol MkII extension using the NI MIDI host protocol.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolProtocolControllerDefinition extends DefaultControllerDefinition
{
    private static final String []                 WINDOWS_STARTS =
    {
        "",
        "2- ",
        "3- ",
        "4- "
    };

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
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            case MAC:
                for (final String [] ports: this.deviceDescriptor.getMidiDiscoveryPairs (os))
                    midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (ports, ports));
                break;

            case WINDOWS:
                for (final String start: WINDOWS_STARTS)
                {
                    for (final String [] ports: this.deviceDescriptor.getMidiDiscoveryPairs (os))
                    {
                        final String [] ps =
                        {
                            ports[0],
                            start + ports[1]
                        };
                        midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (ps, ps));
                    }
                }
                break;

            case LINUX:
            default:
                // Not supported
                break;
        }
        return midiDiscoveryPairs;
    }
}
