// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDrumPad;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends DrumViewBase
{
    private static final int NUMBER_OF_RETRIES = 20;

    protected int            startRetries;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM, surface, model, 4, 4);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_BROWSE))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_BROWSE);

            final ICursorDevice primary = this.model.getPrimaryDevice ();
            if (!primary.hasDrumPads ())
                return;
            final IDrumPad drumPad = primary.getDrumPad (playedPad);
            drumPad.browseToInsert ();
            this.activateMode ();
            return;
        }

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public void handleSelectButton (final int playedPad)
    {
        final ICursorDevice primary = this.model.getPrimaryDevice ();
        if (!primary.hasDrumPads ())
            return;

        // Do not reselect
        if (primary.getDrumPad (playedPad).isSelected ())
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd.isNested ())
            cd.selectParent ();

        this.surface.getModeManager ().setActiveMode (Modes.MODE_DEVICE_LAYER);
        primary.selectDrumPad (playedPad);

        this.updateNoteMapping ();
    }


    /**
     * Tries to activate the mode 20 times.
     */
    protected void activateMode ()
    {
        if (this.model.getBrowser ().isActive ())
            this.surface.getModeManager ().setActiveMode (Modes.MODE_BROWSER);
        else if (this.startRetries < NUMBER_OF_RETRIES)
        {
            this.startRetries++;
            this.surface.scheduleTask (this::activateMode, 200);
        }
    }
}