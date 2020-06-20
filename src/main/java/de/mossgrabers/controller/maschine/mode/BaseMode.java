// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * Base class for all Maschine modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final String name, final MaschineControlSurface surface, final IModel model)
    {
        super (name, surface, model, false);
    }
}
