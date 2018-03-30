// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Ableton Push controller extension.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PushControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID_MK_I  = UUID.fromString ("DBED9610-C474-11E6-9598-0800200C9A66");
    private static final UUID EXTENSION_ID_MK_II = UUID.fromString ("15176AA0-C476-11E6-9598-0800200C9A66");


    /**
     * Constructor.
     *
     * @param isMkII True if is Mk II other Mk I
     */
    public PushControllerDefinition (final boolean isMkII)
    {
        super ("Push4Bitwig", "Jürgen Moßgraber", "9.51", isMkII ? EXTENSION_ID_MK_II : EXTENSION_ID_MK_I, isMkII ? "Push 2" : "Push 1", "Ableton", 1, 1);
    }
}
