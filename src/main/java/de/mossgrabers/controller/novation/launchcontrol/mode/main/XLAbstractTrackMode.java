package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.controller.novation.launchcontrol.mode.buttons.XLTemporaryButtonMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;


/**
 * Mix mode for the LauchControl XL to control 2 sends and panorama.
 *
 * @author Jürgen Moßgraber
 */
public abstract class XLAbstractTrackMode extends XLAbstractMainMode<ITrack>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    protected XLAbstractTrackMode (final String name, final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (name, surface, model, model.getTrackBank (), controls);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeRow0 (final int index)
    {
        final ITrack track = this.model.getTrackBank ().getItem (index);
        if (!track.doesExist ())
            return;

        if (track.isSelected () && track.isGroup ())
        {
            track.toggleGroupExpanded ();
            return;
        }

        track.select ();
        this.mvHelper.notifySelectedTrack ();
    }


    /** {@inheritDoc} */
    @Override
    protected void handleRow2 (final int index, final ButtonEvent event)
    {
        final ModeManager trackButtonModeManager = this.surface.getTrackButtonModeManager ();

        switch (index)
        {
            // Device button
            case 0:
                if (event == ButtonEvent.DOWN)
                {
                    trackButtonModeManager.setTemporary (Modes.DEVICE_PARAMS);
                    return;
                }

                if (event == ButtonEvent.UP)
                {
                    if (!((XLTemporaryButtonMode) trackButtonModeManager.get (Modes.DEVICE_PARAMS)).hasBeenUsed ())
                        this.toggleDeviceActive ();
                    trackButtonModeManager.restore ();
                }
                break;

            // Mute button
            case 1:
                if (event == ButtonEvent.DOWN)
                    trackButtonModeManager.setActive (Modes.MUTE);
                break;

            // Solo button
            case 2:
                this.alternativeModeSelect (event, Modes.SOLO, Modes.CLIP);
                break;

            // Record Arm button
            case 3:
                this.alternativeModeSelect (event, Modes.REC_ARM, Modes.TRANSPORT);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ModeManager trackModeManager = this.surface.getTrackButtonModeManager ();
        switch (buttonID)
        {
            case DEVICE:
                return this.surface.getConfiguration ().isDeviceActive () ? 127 : 0;

            case MUTE:
                return trackModeManager.isActive (Modes.MUTE) ? 127 : 0;

            case SOLO:
                return trackModeManager.isActive (Modes.SOLO) ? 127 : 0;

            case REC_ARM:
                return trackModeManager.isActive (Modes.REC_ARM) ? 127 : 0;

            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();

                if (this.surface.isPressed (ButtonID.REC_ARM))
                    return super.getTransportButtonColor (index);

                final ITrack track = this.model.getTrackBank ().getItem (index);
                if (track.doesExist ())
                    return track.isSelected () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;

            default:
                return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
            trackBank.getItem (i).getSendBank ().selectPreviousPage ();
        this.mvHelper.notifySelectedSends (trackBank.getItem (0).getSendBank ());
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
            trackBank.getItem (i).getSendBank ().selectNextPage ();
        this.mvHelper.notifySelectedSends (trackBank.getItem (0).getSendBank ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageBackwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageForwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        if (this.isButtonCombination (ButtonID.DEVICE))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getCursorDevice ().selectPrevious ();
            this.mvHelper.notifySelectedDevice ();
            return;
        }

        if (this.isButtonCombination (ButtonID.SOLO))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getSceneBank ().selectPreviousPage ();
            this.mvHelper.notifyScenePage ();
            return;
        }

        super.selectPreviousItemPage ();
        this.mvHelper.notifySelectedTrack ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getCursorDevice ().selectNext ();
            this.mvHelper.notifySelectedDevice ();
            return;
        }

        if (this.surface.isPressed (ButtonID.SOLO))
        {
            if (this.surface.getTrackButtonModeManager ().getActive () instanceof final XLTemporaryButtonMode tempMode)
                tempMode.setHasBeenUsed ();
            this.model.getSceneBank ().selectNextPage ();
            this.mvHelper.notifyScenePage ();
            return;
        }

        super.selectNextItemPage ();
        this.mvHelper.notifySelectedTrack ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
            return this.model.getCursorDevice ().canSelectPrevious ();

        if (this.surface.isPressed (ButtonID.SOLO))
            return this.model.getSceneBank ().canScrollPageBackwards ();

        return super.hasPreviousItemPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItemPage ()
    {
        if (this.surface.isPressed (ButtonID.DEVICE))
            return this.model.getCursorDevice ().canSelectNext ();

        if (this.surface.isPressed (ButtonID.SOLO))
            return this.model.getSceneBank ().canScrollPageForwards ();

        return super.hasNextItemPage ();
    }
}
