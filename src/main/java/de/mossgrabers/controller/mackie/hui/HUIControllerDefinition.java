// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui;

import de.mossgrabers.framework.controller.DefaultControllerDefinition;

import java.util.UUID;


/**
 * Definition class for the Mackie Human User Interface (HUI) protocol extension.
 *
 * @author Jürgen Moßgraber
 */
public class HUIControllerDefinition extends DefaultControllerDefinition
{
    private static final UUID []   EXTENSION_ID   =
    {
        UUID.fromString ("AD354AAA-E499-4B89-B86C-3E1F74647FF5"),
        UUID.fromString ("A9517933-B7AD-408A-B987-39560B0057CD"),
        UUID.fromString ("415E6966-B016-4224-A667-47248F945CE2"),
        UUID.fromString ("53566C82-7C54-4A5A-A574-12EDEA44CF33")
    };

    private static final String [] HARDWARE_MODEL =
    {
        "HUI - Human User Interface",
        "HUI - Human User Interface + 1 Extender",
        "HUI - Human User Interface + 2 Extenders",
        "HUI - Human User Interface + 3 Extenders"
    };


    /**
     * Constructor.
     *
     * @param numHUIExtenders The number of supported Extenders
     */
    public HUIControllerDefinition (final int numHUIExtenders)
    {
        super (EXTENSION_ID[numHUIExtenders], HARDWARE_MODEL[numHUIExtenders], "Mackie", numHUIExtenders + 1, numHUIExtenders + 1);
    }
}
