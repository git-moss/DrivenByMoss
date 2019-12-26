// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

/**
 * IDs for common continuous controls.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ContinuousID
{
    /** The Mastertrack fader. */
    FADER_MASTER,

    /** The fader 1. */
    FADER1,
    /** The fader 2. */
    FADER2,
    /** The fader 3. */
    FADER3,
    /** The fader 4. */
    FADER4,
    /** The fader 5. */
    FADER5,
    /** The fader 6. */
    FADER6,
    /** The fader 7. */
    FADER7,
    /** The fader 8. */
    FADER8,

    /** The crossfader. */
    CROSSFADER,

    /** The Mastertrack knob. */
    MASTER_KNOB,

    /** Knob 1. */
    KNOB1,
    /** Knob 2. */
    KNOB2,
    /** Knob 3. */
    KNOB3,
    /** Knob 4. */
    KNOB4,
    /** Knob 5. */
    KNOB5,
    /** Knob 6. */
    KNOB6,
    /** Knob 7. */
    KNOB7,
    /** Knob 8. */
    KNOB8,

    /** The tempo knob. */
    TEMPO,
    /** The play position knob. */
    PLAY_POSITION,
    /** An expression footswitch. */
    FOOTSWITCH,

    /** Continuous device knob 1. */
    DEVICE_KNOB1,
    /** Continuous device knob 2. */
    DEVICE_KNOB2,
    /** Continuous device knob 3. */
    DEVICE_KNOB3,
    /** Continuous device knob 4. */
    DEVICE_KNOB4,
    /** Continuous device knob 5. */
    DEVICE_KNOB5,
    /** Continuous device knob 6. */
    DEVICE_KNOB6,
    /** Continuous device knob 7. */
    DEVICE_KNOB7,
    /** Continuous device knob 8. */
    DEVICE_KNOB8,

    /** Continuous touch pad in x direction. */
    TOUCHPAD_X,
    /** Continuous touch pad in y direction. */
    TOUCHPAD_Y,

    /** Initialise a handhake. */
    HELLO,

    /** Selection of tracks with multiple values. */
    TRACK_SELECT,
    /** Selection of mute with multiple values. */
    TRACK_MUTE,
    /** Selection of solo with multiple values. */
    TRACK_SOLO,
    /** Selection of rec arm with multiple values. */
    TRACK_ARM,

    /** Navigate clips with multiple values. */
    NAVIGATE_CLIPS,
    /** Navigate scenes with multiple values. */
    NAVIGATE_SCENES,
    /** Navigate volume with multiple values. */
    NAVIGATE_VOLUME,
    /** Navigate pan with multiple values. */
    NAVIGATE_PAN,

    /** Move the play cursor. */
    MOVE_TRANSPORT,
    /** Move the loop. */
    MOVE_LOOP,
    /** Move the track selection. */
    MOVE_TRACK,
    /** Move the track bank selection. */
    MOVE_TRACK_BANK,

    /** Select a mode. */
    MODE_SELECTION,
    /** Select a view. */
    VIEW_SELECTION,

    /** A touchstrip fader. */
    TOUCHSTRIP;


    /**
     * Get an offset control ID, e.g. to get FADER4 set FADER1 and 3 as parameters.
     *
     * @param cid The base control ID
     * @param offset The offset
     * @return The offset control ID
     */
    public static ContinuousID get (final ContinuousID cid, final int offset)
    {
        return ContinuousID.values ()[cid.ordinal () + offset];
    }
}
