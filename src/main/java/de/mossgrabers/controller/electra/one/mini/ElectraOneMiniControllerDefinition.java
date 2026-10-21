// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the Electra One Mini controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniControllerDefinition extends DefaultControllerDefinition
{
    private static final String ELECTRA_CONTROLLER_ELECTRA_CTRL = "Electra Controller Electra CTRL";


    /**
     * Constructor.
     */
    public ElectraOneMiniControllerDefinition ()
    {
        super (UUID.fromString ("610CBC4D-E7D0-4D62-BCEB-97E91419C73F"), "Electra One Mini", "Electra", 2, 2);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> pairs = super.getMidiDiscoveryPairs (os);

        switch (os)
        {
            case WINDOWS:
                pairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Electra Controller",
                    "MIDIIN3 (Electra Controller)"
                }, new String []
                {
                    "Electra Controller",
                    "MIDIOUT3 (Electra Controller)"
                }));
                for (int i = 2; i < 10; i++)
                {
                    final String [] portInNames = new String []
                    {
                        "Electra Controller #" + i,
                        "MIDIIN" + 3 * i + " (Electra Controller)"
                    };
                    final String [] portOutNames = new String []
                    {
                        "Electra Controller #" + i,
                        "MIDIOUT" + 3 * i + " (Electra Controller)"
                    };
                    pairs.add (this.addDeviceDiscoveryPair (portInNames, portOutNames));
                }
                break;

            case MAC, MAC_ARM:
                final String [] portNamesMac = new String []
                {
                    "Electra Controller Electra Port 1",
                    ELECTRA_CONTROLLER_ELECTRA_CTRL
                };
                pairs.add (this.addDeviceDiscoveryPair (portNamesMac, portNamesMac));
                break;

            default:
            case LINUX:
                final String [] portNamesLinux = new String []
                {
                    "Electra Controller Electra Port",
                    ELECTRA_CONTROLLER_ELECTRA_CTRL
                };
                pairs.add (this.addDeviceDiscoveryPair (portNamesLinux, portNamesLinux));
                break;
        }

        return pairs;
    }
}
