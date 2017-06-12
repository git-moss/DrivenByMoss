// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini.view;

import de.mossgrabers.apcmini.APCminiConfiguration;
import de.mossgrabers.apcmini.controller.APCminiColors;
import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.view.AbstractSessionView;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;


/**
 * Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    private final TrackButtons extensions;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final APCminiControlSurface surface, final Model model)
    {
        super ("Session", surface, model, 8, 8, false);
        this.extensions = new TrackButtons (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int channel = note % 8;
        final int scene = 7 - note / 8;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final SlotData slot = tb.getTrack (channel).getSlots ()[scene];
        final ClipLauncherSlotBank slots = tb.getClipLauncherSlots (channel);

        if (tb.getTrack (channel).isRecarm ())
        {
            if (!slot.isRecording ())
                slots.record (scene);
            slots.launch (scene);
        }
        else
            slots.launch (scene);

        if (this.doSelectClipOnLaunch ())
            slots.select (scene);
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
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        for (int x = 0; x < 8; x++)
        {
            final TrackData t = tb.getTrack (x);
            for (int y = 0; y < 8; y++)
                this.drawPad (t.getSlots ()[y], x, y, t.isRecarm ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawPad (final SlotData slot, final int x, final int y, final boolean isArmed)
    {
        int color = APCminiColors.APC_COLOR_BLACK;

        if (slot.isRecording ())
            color = APCminiColors.APC_COLOR_RED;
        else if (slot.isRecordingQueued ())
            color = APCminiColors.APC_COLOR_RED_BLINK;
        else if (slot.isPlaying ())
            color = APCminiColors.APC_COLOR_GREEN;
        else if (slot.isPlayingQueued ())
            color = APCminiColors.APC_COLOR_GREEN_BLINK;
        else if (slot.hasContent ())
            color = APCminiColors.APC_COLOR_YELLOW;

        this.surface.getPadGrid ().light (36 + (7 - y) * 8 + x, color);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        for (int i = APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1; i <= APCminiControlSurface.APC_BUTTON_SCENE_BUTTON8; i++)
            this.surface.updateButton (i, this.surface.getNoteVelocity (i) > 0 ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);

        this.extensions.updateTrackButtons ();
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        this.extensions.onSelectTrack (index, event);
    }
}