// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;

import java.util.List;
import java.util.UUID;


/**
 * Definition class for the Beatstep controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID     = UUID.fromString ("F7FF1750-7EC3-11E4-B4A9-0800200C9A66");
    private static final UUID EXTENSION_ID_PRO = UUID.fromString ("7264A210-5EFE-11E5-A837-0800200C9A66");

    private final boolean     isPro;


    /**
     * Constructor.
     *
     * @param isPro True if it is the Beatstep Pro otherwise normal Beatstep
     */
    public BeatstepControllerDefinition (final boolean isPro)
    {
        super ("Beatstep4Bitwig", "Jürgen Moßgraber", "4.01", isPro ? EXTENSION_ID_PRO : EXTENSION_ID, isPro ? "Beatstep Pro" : "Beatstep", "Arturia", 1, 1);
        this.isPro = isPro;
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.createDeviceDiscoveryPairs (this.isPro ? "Arturia BeatStep Pro" : "Arturia BeatStep");
    }
}
