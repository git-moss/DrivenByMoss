// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mikro.mk3.mode;

import de.mossgrabers.controller.maschine.mikro.mk3.MaschineMikroMk3Configuration;
import de.mossgrabers.controller.maschine.mikro.mk3.controller.MaschineMikroMk3ControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.AbstractMode;


/**
 * Base class for all Maschine Mikro Mk3 modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<MaschineMikroMk3ControlSurface, MaschineMikroMk3Configuration>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final String name, final MaschineMikroMk3ControlSurface surface, final IModel model)
    {
        super (name, surface, model, false);
    }
}
