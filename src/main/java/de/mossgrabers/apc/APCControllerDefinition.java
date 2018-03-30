// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apc;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Novation SL controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID_MK_I  = UUID.fromString ("35E958A0-345F-11E4-8C21-0800200C9A66");
    private static final UUID EXTENSION_ID_MK_II = UUID.fromString ("14787D10-35DE-11E4-8C21-0800200C9A66");


    /**
     * Constructor.
     *
     * @param isMkII True if is Mk II other Mk I
     */
    public APCControllerDefinition (final boolean isMkII)
    {
        super ("APC4Bitwig", "Jürgen Moßgraber", "5.13", isMkII ? EXTENSION_ID_MK_II : EXTENSION_ID_MK_I, isMkII ? "APC40 mkII" : "APC40", "Akai", 1, 1);
    }
}
