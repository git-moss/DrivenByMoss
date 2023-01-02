// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Definition class for the NI Maschine Jam controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID   EXTENSION_ID        = UUID.fromString ("72F32A83-A697-446B-9016-64AA3F9476E4");
    private static final String MASCHINE_JAM_PREFIX = "Maschine Jam - ";


    /**
     * Constructor.
     */
    public MaschineJamControllerDefinition ()
    {
        super (EXTENSION_ID, "Maschine JAM", "Native Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> pairs = new ArrayList<> ();

        switch (os)
        {
            case MAC:
                for (int i = 1; i <= 4; i++)
                    pairs.add (this.addDeviceDiscoveryPair (MASCHINE_JAM_PREFIX + i + " Input", MASCHINE_JAM_PREFIX + i + " Output"));
                break;

            case WINDOWS:
            default:
                for (int i = 1; i <= 4; i++)
                    pairs.addAll (this.createDeviceDiscoveryPairs (MASCHINE_JAM_PREFIX + i));
                break;
        }

        return pairs;
    }
}
