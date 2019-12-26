// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.mode.BrowserActivator;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.Views;


/**
 * The Drum view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView extends DrumViewBase
{
    private final BrowserActivator<PushControlSurface, PushConfiguration> browserModeActivator;
    private int                                                           scrollPosition = -1;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM, surface, model, 4, 4);

        this.browserModeActivator = new BrowserActivator<> (Modes.BROWSER, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isPressed (ButtonID.BROWSE))
        {
            this.surface.setTriggerConsumed (ButtonID.BROWSE);

            final ICursorDevice primary = this.model.getInstrumentDevice ();
            if (!primary.hasDrumPads ())
                return;

            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
            this.scrollPosition = drumPadBank.getScrollPosition ();
            this.model.getBrowser ().replace (drumPadBank.getItem (playedPad));
            this.browserModeActivator.activate ();
            return;
        }

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void handleSelectButton (final int playedPad)
    {
        // Do we have drum pads?
        final ICursorDevice primary = this.model.getInstrumentDevice ();
        if (!primary.hasDrumPads ())
            return;
        final ICursorDevice cd = this.model.getCursorDevice ();
        final boolean isNested = cd.isNested ();
        if (isNested)
        {
            // We have to move up to compare the main drum devices
            cd.selectParent ();
        }

        // Can only scroll to the channel if the cursor device is the primary device
        if (primary.getPosition () != cd.getPosition ())
            return;

        // Align the primary and cursor device drum bank view
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
        final int scrollPos = drumPadBank.getScrollPosition ();
        final IDrumPadBank cdDrumPadBank = cd.getDrumPadBank ();
        final int pageSize = cdDrumPadBank.getPageSize ();
        final int adjustedPage = playedPad / pageSize * pageSize;
        cdDrumPadBank.scrollTo (scrollPos + adjustedPage, false);

        // Do not reselect
        final IDrumPad drumPad = drumPadBank.getItem (playedPad);
        if (drumPad.isSelected ())
        {
            // If the instrument of the pad was selected for editing, try to select it again
            if (isNested)
            {
                final IDrumPad selectedItem = cdDrumPadBank.getItem (playedPad % pageSize);
                if (selectedItem != null)
                    selectedItem.enter ();
            }
            return;
        }

        // Only activate layer mode if not one of the layer modes is already active
        final ModeManager modeManager = this.surface.getModeManager ();
        if (!Modes.isLayerMode (modeManager.getActiveModeId ()))
            modeManager.setActiveMode (Modes.DEVICE_LAYER);

        drumPad.select ();

        this.updateNoteMapping ();
    }


    /**
     * Filling a slot from the browser moves the bank view to that slot. This function moves it back
     * to the correct position.
     */
    public void repositionBankPage ()
    {
        if (this.scrollPosition >= 0)
            this.model.getInstrumentDevice ().getDrumPadBank ().scrollTo (this.scrollPosition);
    }
}