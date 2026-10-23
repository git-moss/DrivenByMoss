// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.mode;

import de.mossgrabers.controller.electra.one.base.mode.AbstractElectraOneBaseMode;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.daw.IModel;


/**
 * Abstract base mode for Electra One Mini modes.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractElectraOneMiniMode extends AbstractElectraOneBaseMode<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration>
{
    private static final int GROUP_OFFSET = 8;


    /**
     * Constructor.
     *
     * @param pageIndex The index of the preset page where the mode is located
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractElectraOneMiniMode (final int pageIndex, final String name, final ElectraOneMiniControlSurface surface, final IModel model)
    {
        super (pageIndex, name, surface, model, 2, 8, ElectraOneMiniControlSurface.KNOB_IDS, GROUP_OFFSET);
    }
}
