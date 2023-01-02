// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Electra.One controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneControllerDefinition extends DefaultControllerDefinition
{
    private static final String ELECTRA_CONTROLLER_ELECTRA_CTRL = "Electra Controller Electra CTRL";


    /**
     * Constructor.
     */
    public ElectraOneControllerDefinition ()
    {
        super (UUID.fromString ("24342204-B891-4F62-BC43-8ABA1DE1D690"), "Electra.One", "Electra", 2, 2);
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
                break;

            case MAC:
                pairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Electra Controller Electra Port 1",
                    ELECTRA_CONTROLLER_ELECTRA_CTRL
                }, new String []
                {
                    "Electra Controller Electra Port 1",
                    ELECTRA_CONTROLLER_ELECTRA_CTRL
                }));
                break;

            default:
            case LINUX:
                pairs.add (this.addDeviceDiscoveryPair (new String []
                {
                    "Electra Controller Electra Port",
                    ELECTRA_CONTROLLER_ELECTRA_CTRL
                }, new String []
                {
                    "Electra Controller Electra Port",
                    ELECTRA_CONTROLLER_ELECTRA_CTRL
                }));
                break;
        }

        return pairs;
    }
}
