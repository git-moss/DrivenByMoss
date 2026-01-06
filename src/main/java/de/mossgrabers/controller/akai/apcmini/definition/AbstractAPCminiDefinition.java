// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.definition;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import de.mossgrabers.controller.akai.apcmini.controller.APCminiButton;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;


/**
 * Abstract base class for APCminis.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractAPCminiDefinition extends DefaultControllerDefinition implements IAPCminiControllerDefinition
{
    protected final Map<APCminiButton, Integer> buttonIDs = new EnumMap<> (APCminiButton.class);


    /**
     * Constructor.
     *
     * @param uuid The UUID of the controller implementation
     * @param hardwareModel The hardware model which this controller implementation supports
     */
    protected AbstractAPCminiDefinition (final UUID uuid, final String hardwareModel)
    {
        super (uuid, hardwareModel, "Akai", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonID (final APCminiButton button)
    {
        return this.buttonIDs.get (button).intValue ();
    }
}
