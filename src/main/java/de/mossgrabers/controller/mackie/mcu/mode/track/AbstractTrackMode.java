// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;


/**
 * Abstract base mode for all track modes.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractTrackMode extends BaseMode<ITrack>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractTrackMode (final String name, final MCUControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCurrentTrackBank ());

        model.addTrackBankObserver (this::switchBanks);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        super.updateDisplay ();
        this.updateItemIndices ();
    }


    protected void updateItemIndices ()
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        final int extenderOffset = this.getExtenderOffset ();
        final int [] indices = new int [8];
        for (int i = 0; i < 8; i++)
        {
            final ITrack item = trackBank.getItem (extenderOffset + i);
            indices[i] = item.doesExist () ? item.getPosition () + 1 : 0;
        }
        this.surface.setItemIndices (indices);
    }
}