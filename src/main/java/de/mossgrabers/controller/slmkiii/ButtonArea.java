// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * Management of the 16 buttons for solo/mute and monitor/rec arm.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonArea
{
    private final SLMkIIIControlSurface surface;
    private final IModel                model;

    private boolean                     isMuteSolo = true;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ButtonArea (final SLMkIIIControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
    }


    /**
     * Update the 16 button LEDs.
     */
    public void updateButtons ()
    {
        final ITrackBank tb = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);

            int color1;
            int color2;

            final boolean exists = track.doesExist ();
            if (this.isMuteSolo)
            {
                color1 = exists ? track.isMute () ? SLMkIIIColors.SLMKIII_ORANGE : SLMkIIIColors.SLMKIII_ORANGE_HALF : SLMkIIIColors.SLMKIII_BLACK;
                color2 = exists ? track.isSolo () ? SLMkIIIColors.SLMKIII_YELLOW : SLMkIIIColors.SLMKIII_YELLOW_HALF : SLMkIIIColors.SLMKIII_BLACK;
            }
            else
            {
                color1 = exists ? track.isMonitor () ? SLMkIIIColors.SLMKIII_GREEN : SLMkIIIColors.SLMKIII_GREEN_HALF : SLMkIIIColors.SLMKIII_BLACK;
                color2 = exists ? track.isRecArm () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF : SLMkIIIColors.SLMKIII_BLACK;
            }

            this.surface.updateButton (SLMkIIIControlSurface.MKIII_BUTTON_ROW1_1 + i, color1);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_BUTTON_ROW2_1 + i, color2);
        }
    }


    /**
     * Check if mute/solo or monitor/racarm is active.
     *
     * @return True if mute/solo is active
     */
    public boolean isMuteSolo ()
    {
        return this.isMuteSolo;
    }


    /**
     * Toggle if mute/solo or monitor/racarm is active.
     */
    public void toggleMuteSolo ()
    {
        this.isMuteSolo = !this.isMuteSolo;
    }
}
