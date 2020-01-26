// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.mode;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Master mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    private static final String PARAM_NAMES = "Volume   Pan                                                     Master ";


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public MasterMode (final SLControlSurface surface, final IModel model)
    {
        super ("Master", surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IMasterTrack master = this.model.getMasterTrack ();
        this.surface.getTextDisplay ().setRow (0, MasterMode.PARAM_NAMES).done (0).clearRow (2).setCell (2, 0, master.getVolumeStr (8)).setCell (2, 1, master.getPanStr (8)).done (2);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}