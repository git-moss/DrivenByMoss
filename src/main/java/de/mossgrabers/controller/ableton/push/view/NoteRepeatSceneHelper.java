// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;


/**
 * Helper for selecting note repeat settings with scene buttons.
 *
 * @author Jürgen Moßgraber
 */
public class NoteRepeatSceneHelper
{
    /**
     * Constructor.
     */
    private NoteRepeatSceneHelper ()
    {
        // Intentionally empty
    }


    /**
     * Get the color ID for a scene button, which is controlled by the view.
     *
     * @param surface The surface
     * @param buttonID The ID of the button
     * @return A color ID
     */
    public static String getButtonColorID (final PushControlSurface surface, final ButtonID buttonID)
    {
        final INoteRepeat noteRepeat = surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
        final int sceneIndex = 7 - (buttonID.ordinal () - ButtonID.SCENE1.ordinal ());
        if (surface.isShiftPressed ())
            return Resolution.getMatch (noteRepeat.getNoteLength ()) == sceneIndex ? PushColorManager.NOTE_REPEAT_LENGTH_HI : PushColorManager.NOTE_REPEAT_LENGTH_OFF;
        return Resolution.getMatch (noteRepeat.getPeriod ()) == sceneIndex ? PushColorManager.NOTE_REPEAT_PERIOD_HI : PushColorManager.NOTE_REPEAT_PERIOD_OFF;
    }


    /**
     * Handle the note repeat selection (period and note length).
     *
     * @param surface The surface
     * @param sceneIndex The index of the scene
     */
    public static void handleNoteRepeatSelection (final PushControlSurface surface, final int sceneIndex)
    {
        surface.setTriggerConsumed (ButtonID.REPEAT);
        final INoteRepeat noteRepeat = surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();
        if (surface.isShiftPressed ())
            noteRepeat.setNoteLength (Resolution.getValueAt (sceneIndex));
        else
            noteRepeat.setPeriod (Resolution.getValueAt (sceneIndex));
    }
}
