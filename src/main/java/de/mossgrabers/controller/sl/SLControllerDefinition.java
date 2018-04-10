// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Novation SL controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID_MK_I  = UUID.fromString ("A9041F50-0407-11E5-B939-0800200C9A66");
    private static final UUID EXTENSION_ID_MK_II = UUID.fromString ("D1CEE920-1E51-11E4-8C21-0800200C9A66");


    /**
     * Constructor.
     *
     * @param isMkII True if is Mk II other Mk I
     */
    public SLControllerDefinition (final boolean isMkII)
    {
        super ("SLMkII4Bitwig", "Jürgen Moßgraber", "5.10", isMkII ? EXTENSION_ID_MK_II : EXTENSION_ID_MK_I, isMkII ? "SL MkII" : "SL MkI", "Novation", 2, 1);
    }


    /** [{@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        final List<Pair<String [], String []>> midiDiscoveryPairs = super.getMidiDiscoveryPairs (os);
        midiDiscoveryPairs.add (this.addDeviceDiscoveryPair (new String []
        {
            "ReMOTE SL Port 2",
            "ReMOTE SL Port 1"
        }, new String []
        {
            "ReMOTE SL Port 2"
        }));
        return midiDiscoveryPairs;
    }
}
