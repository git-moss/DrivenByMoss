// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Novation SL controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID_MK_II = UUID.fromString ("4E01A0B0-67B1-11E5-A837-0800200C9A66");
    private static final UUID EXTENSION_ID_PRO   = UUID.fromString ("80B63970-64F1-11E5-A837-0800200C9A66");


    /**
     * Constructor.
     *
     * @param isMkII True if is Mk II other Mk I
     */
    public LaunchpadControllerDefinition (final boolean isMkII)
    {
        super ("Launchpad4Bitwig", "Jürgen Moßgraber", "3.31", isMkII ? EXTENSION_ID_MK_II : EXTENSION_ID_PRO, isMkII ? "Launchpad MkII" : "Launchpad Pro", "Novation", 1, 1);
    }
}
