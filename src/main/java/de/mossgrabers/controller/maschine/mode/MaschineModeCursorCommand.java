// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.mode;

import de.mossgrabers.controller.maschine.MaschineConfiguration;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command for navigating mode pages and items.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineModeCursorCommand extends ModeCursorCommand<MaschineControlSurface, MaschineConfiguration>
{
    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public MaschineModeCursorCommand (final Direction direction, final IModel model, final MaschineControlSurface surface)
    {
        super (direction, model, surface, false);
    }
}
