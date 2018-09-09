// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.mode.ModeManager;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends DrumViewBase
{
    private static final int NUMBER_OF_RETRIES = 20;

    private int              startRetries;
    private int              scrollPosition    = -1;


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

            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
            this.scrollPosition = drumPadBank.getScrollPosition ();
            final IDrumPad drumPad = drumPadBank.getItem (playedPad);
            drumPad.browseToInsert ();
            this.activateMode ();
            return;
        }

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void handleSelectButton (final int playedPad)
    {
        // Do we have drum pads?
        final ICursorDevice primary = this.model.getPrimaryDevice ();
        if (!primary.hasDrumPads ())
            return;
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd.isNested ())
            cd.selectParent ();

        if (primary.getPosition () != cd.getPosition ())
            return;

        // Align the primary and cursor device drum bank view
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
        final int scrollPos = drumPadBank.getScrollPosition ();
        final IDrumPadBank cdDrumPadBank = cd.getDrumPadBank ();
        cdDrumPadBank.scrollTo (scrollPos);

        // Do not reselect
        final IDrumPad drumPad = drumPadBank.getItem (playedPad);
        if (drumPad.isSelected ())
            return;

        // Only activate layer mode if not one of the layer modes is already active
        final ModeManager modeManager = this.surface.getModeManager ();
        if (!Modes.isLayerMode (modeManager.getActiveModeId ()))
            modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);

        drumPad.select ();

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


    /**
     * Filling a slot from the browser moves the bank view to that slot. This function moves it back
     * to the correct position.
     */
    public void repositionBankPage ()
    {
        if (this.scrollPosition >= 0)
            this.model.getPrimaryDevice ().getDrumPadBank ().scrollTo (this.scrollPosition);
    }
}