// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.command.trigger;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.command.continuous.MasterFaderAbsoluteCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Workaround for proprietary value calculation of HUI protocol for the master fader.
 *
 * @author Jürgen Moßgraber
 */
public class WorkaroundMasterFader extends MasterFaderAbsoluteCommand<HUIControlSurface, HUIConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public WorkaroundMasterFader (final IModel model, final HUIControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc}} */
    @Override
    public void execute (final int value)
    {
        // This is called from the emulator, boost up the value to 16384
        super.execute (value * 128);
    }


    /**
     * Route the controller value from handleMidi directly.
     *
     * @param value A value in the range [0..16384]
     */
    public void executeHiRes (final int value)
    {
        super.execute (value);
    }
}
