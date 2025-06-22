// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the Intuitive Instruments Exquis controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisControllerDefinition extends DefaultControllerDefinition
{
    /**
     * Constructor.
     */
    public ExquisControllerDefinition ()
    {
        super (UUID.fromString ("01EF814F-87D2-43ED-935F-0092E1CE4CE0"), "Exquis", "Intuitive Instruments", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> pairs = super.getMidiDiscoveryPairs (os);
        pairs.add (this.addDeviceDiscoveryPair (os == OperatingSystem.LINUX ? "Exquis MIDI 1" : "Exquis"));
        return pairs;
    }
}
