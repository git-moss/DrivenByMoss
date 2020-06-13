// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.command.continuous;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.controller.fire.view.IFireView;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.View;


/**
 * The Select button.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SelectKnobCommand extends AbstractContinuousCommand<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectKnobCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ModeManager modeManager = this.surface.getModeManager ();

        // In note mode use it to change the transposition
        if (modeManager.isActiveOrTempMode (Modes.NOTE))
        {
            final Mode mode = modeManager.getMode (Modes.NOTE);
            mode.onKnobTouch (4, true);
            mode.onKnobValue (4, value);
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.BROWSER))
        {
            final Mode mode = modeManager.getMode (Modes.BROWSER);
            mode.onKnobTouch (8, true);
            mode.onKnobValue (8, value);
            return;
        }

        // Bank scrolling with ALT button is always active
        if (this.surface.isPressed (ButtonID.ALT))
        {
            final boolean isInc = this.model.getValueChanger ().calcKnobSpeed (value) > 0;
            if (modeManager.isActiveOrTempMode (Modes.TRACK, Modes.DEVICE_LAYER))
                handleTrackSelection (this.surface, this.model.getTrackBank (), isInc);
            else if (modeManager.isActiveOrTempMode (Modes.DEVICE_PARAMS))
                this.handleDevicePageSelection (isInc);
            else if (modeManager.isActiveOrTempMode (Modes.USER))
                this.handleUserPageSelection (isInc);
            return;
        }

        // Change the tempo in combination with the drum button
        if (this.surface.isPressed (ButtonID.DRUM))
        {
            this.surface.setTriggerConsumed (ButtonID.DRUM);
            final ITransport transport = this.model.getTransport ();
            double amount = this.surface.isPressed (ButtonID.SELECT) ? 10 : 1;
            if (this.surface.isPressed (ButtonID.SHIFT))
                amount /= 100.0;
            if (this.model.getValueChanger ().calcKnobSpeed (value) < 0)
                amount *= -1;
            transport.setTempo (transport.getTempo () + amount);
            this.mvHelper.delayDisplay ( () -> String.format ("Tempo: %.02f", Double.valueOf (transport.getTempo ())));
            return;
        }

        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView instanceof IFireView)
            ((IFireView) activeView).onSelectKnobValue (value);
    }


    private void handleDevicePageSelection (final boolean isInc)
    {
        if (!this.model.hasSelectedDevice ())
            return;
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        if (isInc)
        {
            if (this.surface.isShiftPressed ())
                cursorDevice.getParameterBank ().selectNextItem ();
            else
                cursorDevice.selectNext ();
        }
        else
        {
            if (this.surface.isShiftPressed ())
                cursorDevice.getParameterBank ().selectPreviousItem ();
            else
                cursorDevice.selectPrevious ();
        }
    }


    private void handleUserPageSelection (final boolean isInc)
    {
        final Mode userMode = this.surface.getModeManager ().getMode (Modes.USER);
        if (isInc)
            userMode.selectNextItem ();
        else
            userMode.selectPreviousItem ();
    }


    /**
     * Change to the previous/next track or page.
     *
     * @param surface The surface
     * @param trackBank The track bank
     * @param isInc True to move forward otherwise backwards
     */
    public static void handleTrackSelection (final FireControlSurface surface, final ITrackBank trackBank, final boolean isInc)
    {
        if (isInc)
        {
            if (surface.isPressed (ButtonID.SELECT))
            {
                if (trackBank.canScrollPageForwards ())
                {
                    trackBank.selectNextPage ();
                    return;
                }

                final int positionOfLastItem = trackBank.getPositionOfLastItem ();
                if (positionOfLastItem >= 0)
                {
                    final int index = positionOfLastItem % trackBank.getPageSize ();
                    final ITrack lastItem = trackBank.getItem (index);
                    if (!lastItem.isSelected ())
                        lastItem.select ();
                }
                return;
            }
            trackBank.selectNextItem ();
            return;
        }

        if (surface.isPressed (ButtonID.SELECT))
        {
            if (trackBank.canScrollPageBackwards ())
            {
                trackBank.selectPreviousPage ();
                return;
            }

            final ITrack firstItem = trackBank.getItem (0);
            if (!firstItem.isSelected ())
                firstItem.select ();
            return;
        }

        trackBank.selectPreviousItem ();
    }
}
