// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition.button;

import java.util.List;


/**
 * Enumeration for all non-grid buttons.
 *
 * @author Jürgen Moßgraber
 */
public enum LaunchpadButton
{
    /** The Shift button. */
    SHIFT,

    /** The Click/Metronome button. */
    CLICK,
    /** The Undo button. */
    UNDO,
    /** The Delete button. */
    DELETE,
    /** The Quantize button. */
    QUANTIZE,
    /** The Duplicate button. */
    DUPLICATE,
    /** The Play button. */
    PLAY,
    /** The Record button. */
    RECORD,

    /** The Record Arm mode button. */
    REC_ARM,
    /** The Track Select mode button. */
    TRACK_SELECT,
    /** The Mute mode button. */
    MUTE,
    /** The Solo mode button. */
    SOLO,
    /** The Volume mode button. */
    VOLUME,
    /** The Panorama mode button. */
    PAN,
    /** The Sends mode button. */
    SENDS,
    /** The Stop Clip mode button. */
    STOP_CLIP,

    /** The Arrow Up button. */
    ARROW_UP,
    /** The Arrow Down button. */
    ARROW_DOWN,
    /** The Arrow Left button. */
    ARROW_LEFT,
    /** The Arrow Rght button. */
    ARROW_RIGHT,

    /** The Session button. */
    SESSION,
    /** The Note button. */
    NOTE,
    /** The Device button. */
    DEVICE,
    /** The User button. */
    USER,
    /** The project button. */
    PROJECT,

    /** The Scene 1 button. */
    SCENE1,
    /** The Scene 2 button. */
    SCENE2,
    /** The Scene 3 button. */
    SCENE3,
    /** The Scene 4 button. */
    SCENE4,
    /** The Scene 5 button. */
    SCENE5,
    /** The Scene 6 button. */
    SCENE6,
    /** The Scene 7 button. */
    SCENE7,
    /** The Scene 8 button. */
    SCENE8;


    /** All scene buttons. */
    public static final List<LaunchpadButton> SCENES = List.of (SCENE1, SCENE2, SCENE3, SCENE4, SCENE5, SCENE6, SCENE7, SCENE8);
}
