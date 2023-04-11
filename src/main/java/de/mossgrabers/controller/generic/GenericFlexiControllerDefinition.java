// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Generic Flexi controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class GenericFlexiControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("1B43F14F-CAAF-468A-9418-417C678653A0");


    /**
     * Constructor.
     */
    public GenericFlexiControllerDefinition ()
    {
        super (EXTENSION_ID, "Flexi", "Generic", 1, 1);
    }
}
