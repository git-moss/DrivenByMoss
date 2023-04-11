// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.yaeltex.turn.view;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnColorManager;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<YaeltexTurnControlSurface, YaeltexTurnConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final YaeltexTurnControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 4, 8, true);

        final int red = YaeltexTurnColorManager.getIndexFor (ColorEx.RED);
        final int redLow = YaeltexTurnColorManager.getIndexFor (ColorEx.DARK_RED);
        final int green = YaeltexTurnColorManager.getIndexFor (ColorEx.GREEN);
        final int orange = YaeltexTurnColorManager.getIndexFor (ColorEx.ORANGE);
        final int black = YaeltexTurnColorManager.getIndexFor (ColorEx.BLACK);

        final LightInfo isRecording = new LightInfo (red, red, false);
        final LightInfo isRecordingQueued = new LightInfo (red, redLow, true);
        final LightInfo isPlaying = new LightInfo (green, green, false);
        final LightInfo isPlayingQueued = new LightInfo (green, green, true);
        final LightInfo hasContent = new LightInfo (orange, -1, false);
        final LightInfo noContent = new LightInfo (black, -1, false);
        final LightInfo recArmed = new LightInfo (redLow, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isBirdsEyeActive ()
    {
        // No support for birds eye view
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (super.handleButtonCombinations (track, slot))
            return true;

        final int index = track.getIndex ();
        if (index < 0)
            return true;

        // Duplicate the slot with Select button
        if (this.isButtonCombination (ButtonID.get (ButtonID.ROW1_1, index)))
        {
            slot.duplicate ();
            return true;
        }

        // Delete the slot with Stop Clip button
        if (this.isButtonCombination (ButtonID.get (ButtonID.ROW6_1, index)))
        {
            slot.remove ();
            return true;
        }

        return false;
    }
}