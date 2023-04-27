// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.esi.xjam;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the ESI Xjam controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("3883D79D-141C-4AD6-912C-1E74FFBE6CD7");


    /**
     * Constructor.
     */
    public XjamControllerDefinition ()
    {
        super (EXTENSION_ID, "Xjam", "ESI", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        switch (os)
        {
            default:
            case WINDOWS:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Xjam", "Xjam"));
                break;

            // TODO Test
            case MAC:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Xjam", "Xjam"));
                break;

            // TODO Test
            case LINUX:
                midiDiscoveryPairs.add (this.addDeviceDiscoveryPair ("Xjam", "Xjam"));
                break;
        }
        return midiDiscoveryPairs;
    }
}
