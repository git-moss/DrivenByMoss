// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.controller;

import java.util.List;


/**
 * Enumeration for all non-grid buttons.
 *
 * @author Jürgen Moßgraber
 */
public enum APCminiButton
{
    /** The Shift button. */
    SHIFT,

    /** The Arrow Up button. */
    ARROW_UP,
    /** The Arrow Down button. */
    ARROW_DOWN,
    /** The Arrow Left button. */
    ARROW_LEFT,
    /** The Arrow Right button. */
    ARROW_RIGHT,

    /** The Volume button. */
    VOLUME,
    /** The Panorama button. */
    PAN,
    /** The Send button. */
    SEND,
    /** The Device button. */
    DEVICE,

    /** The Track 1 button. */
    TRACK1,
    /** The Track 2 button. */
    TRACK2,
    /** The Track 3 button. */
    TRACK3,
    /** The Track 4 button. */
    TRACK4,
    /** The Track 5 button. */
    TRACK5,
    /** The Track 6 button. */
    TRACK6,
    /** The Track 7 button. */
    TRACK7,
    /** The Track 8 button. */
    TRACK8,

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
    public static final List<APCminiButton> SCENES = List.of (SCENE1, SCENE2, SCENE3, SCENE4, SCENE5, SCENE6, SCENE7, SCENE8);

    /** All track buttons. */
    public static final List<APCminiButton> TRACKS = List.of (TRACK1, TRACK2, TRACK3, TRACK4, TRACK5, TRACK6, TRACK7, TRACK8);
}
