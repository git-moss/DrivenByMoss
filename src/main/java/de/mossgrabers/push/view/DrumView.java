// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends DrumViewBase
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final PushControlSurface surface, final Model model)
    {
        super (Views.VIEW_NAME_DRUM, surface, model, 4, 4);
    }


    /** {@inheritDoc} */
    @Override
    public void handleSelectButton (final int playedPad)
    {
        final CursorDeviceProxy primary = this.model.getPrimaryDevice ();
        if (!primary.hasDrumPads ())
            return;

        // Do not reselect
        if (primary.getDrumPad (playedPad).isSelected ())
            return;

        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        if (cd.isNested ())
            cd.selectParent ();

        this.surface.getModeManager ().setActiveMode (Modes.MODE_DEVICE_LAYER);
        primary.selectDrumPad (playedPad);

        this.updateNoteMapping ();
    }
}