// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.mode.Modes;


/**
 * Manages all modes.
 *
 * @author Jürgen Moßgraber
 */
public class ModeManager extends FeatureGroupManager<Modes, IMode>
{
    /**
     * Constructor.
     */
    public ModeManager ()
    {
        super (Modes.class);
    }
}
