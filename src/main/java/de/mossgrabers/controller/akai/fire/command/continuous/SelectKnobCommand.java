// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.command.continuous;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.controller.akai.fire.view.IFireView;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.Timeout;


/**
 * The Select button.
 *
 * @author Jürgen Moßgraber
 */
public class SelectKnobCommand extends AbstractContinuousCommand<FireControlSurface, FireConfiguration>
{
    private final Timeout timeout;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public SelectKnobCommand (final IModel model, final FireControlSurface surface)
    {
        super (model, surface);

        this.timeout = new Timeout (model.getHost (), 500);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ModeManager modeManager = this.surface.getModeManager ();

        if (modeManager.isActive (Modes.BROWSER))
        {
            final IMode mode = modeManager.get (Modes.BROWSER);
            mode.onKnobTouch (8, true);
            mode.onKnobValue (8, value);
            this.checkUntouch (8);
            return;
        }

        // Bank scrolling with ALT button is always active
        if (this.surface.isPressed (ButtonID.ALT))
        {
            final boolean isInc = this.model.getValueChanger ().isIncrease (value);
            if (modeManager.isActive (Modes.TRACK, Modes.VOLUME, Modes.DEVICE_LAYER, Modes.DEVICE_LAYER_VOLUME, Modes.NOTE))
                handleTrackSelection (this.surface, this.model.getTrackBank (), isInc);
            else if (modeManager.isActive (Modes.DEVICE_PARAMS))
                this.handleDevicePageSelection (isInc);
            else if (modeManager.isActive (Modes.USER))
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
            if (this.model.getValueChanger ().calcKnobChange (value) < 0)
                amount *= -1;
            transport.setTempo (transport.getTempo () + amount);
            this.mvHelper.notifyTempo ();
            return;
        }

        // Change the play position in combination with the metronome button
        if (this.surface.isPressed (ButtonID.METRONOME))
        {
            this.surface.setTriggerConsumed (ButtonID.METRONOME);
            this.handlePlayPosition (value);
            return;
        }

        final IView activeView = this.surface.getViewManager ().getActive ();
        if (activeView instanceof final IFireView fireView)
            fireView.onSelectKnobValue (value);
    }


    /**
     * The Select knob does not send touch data. Therefore, it must be simulated.
     *
     * @param index The Select index 4 or 8
     */
    private void checkUntouch (final int index)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.NOTE))
            this.timeout.delay ( () -> modeManager.get (Modes.NOTE).onKnobTouch (index, false));
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
        final IMode userMode = this.surface.getModeManager ().get (Modes.USER);
        if (isInc)
            userMode.selectNextItem ();
        else
            userMode.selectPreviousItem ();
    }


    /**
     * Move the play position.
     *
     * @param value The knob value
     */
    private void handlePlayPosition (final int value)
    {
        final ITransport transport = this.model.getTransport ();
        transport.changePosition (this.model.getValueChanger ().isIncrease (value), this.surface.isPressed (ButtonID.SHIFT));
        this.mvHelper.notifyPlayPosition ();
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
