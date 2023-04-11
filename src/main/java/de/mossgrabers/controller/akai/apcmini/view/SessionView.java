// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiColorManager;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    private final TrackButtons extensions;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     * @param trackButtons The track button control
     */
    public SessionView (final APCminiControlSurface surface, final IModel model, final TrackButtons trackButtons)
    {
        super ("Session", surface, model, 8, 8, false);
        this.extensions = trackButtons;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doSelectClipOnLaunch ()
    {
        return this.surface.getConfiguration ().isSelectClipOnLaunch ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int x = 0; x < 8; x++)
        {
            final ITrack t = tb.getItem (x);
            final ISlotBank slotBank = t.getSlotBank ();
            for (int y = 0; y < 8; y++)
                this.drawPad (slotBank.getItem (y), x, y, t.isRecArm ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawPad (final ISlot slot, final int x, final int y, final boolean isArmed)
    {
        int color = APCminiColorManager.APC_COLOR_BLACK;

        if (slot.isRecording ())
            color = APCminiColorManager.APC_COLOR_RED;
        else if (slot.isRecordingQueued ())
            color = APCminiColorManager.APC_COLOR_RED_BLINK;
        else if (slot.isPlaying ())
            color = APCminiColorManager.APC_COLOR_GREEN;
        else if (slot.isPlayingQueued ())
            color = APCminiColorManager.APC_COLOR_GREEN_BLINK;
        else if (slot.hasContent ())
            color = APCminiColorManager.APC_COLOR_YELLOW;

        this.surface.getPadGrid ().light (36 + (7 - y) * 8 + x, color);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return this.surface.getButton (buttonID).isPressed () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        this.extensions.onSelectTrack (index, event);
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        return this.extensions.getTrackButtonColor (index);
    }
}