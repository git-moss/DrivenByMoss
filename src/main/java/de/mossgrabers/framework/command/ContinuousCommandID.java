// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command;

/**
 * IDs for continuous commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ContinuousCommandID
{
    /** Continuous knob 1. */
    KNOB1,
    /** Continuous knob 2. */
    KNOB2,
    /** Continuous knob 3. */
    KNOB3,
    /** Continuous knob 4. */
    KNOB4,
    /** Continuous knob 5. */
    KNOB5,
    /** Continuous knob 6. */
    KNOB6,
    /** Continuous knob 7. */
    KNOB7,
    /** Continuous knob 8. */
    KNOB8,

    /** Continuous master knob. */
    MASTER_KNOB,

    /** Continuous tempo knob. */
    TEMPO,
    /** Continuous play position knob. */
    PLAY_POSITION,
    /** Footswitch command. */
    FOOTSWITCH,

    /** Change the crossfader. */
    CROSSFADER,

    /** Continuous fader 1. */
    FADER1,
    /** Continuous fader 2. */
    FADER2,
    /** Continuous fader 3. */
    FADER3,
    /** Continuous fader 4. */
    FADER4,
    /** Continuous fader 5. */
    FADER5,
    /** Continuous fader 6. */
    FADER6,
    /** Continuous fader 7. */
    FADER7,
    /** Continuous fader 8. */
    FADER8,

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
    MOVE_TRACK_BANK;


    /**
     * Get an offset command, e.g. to get KNOB4 set KNOB1 and 3 as parameters.
     *
     * @param command The base command
     * @param offset The offset
     * @return The offset command
     */
    public static ContinuousCommandID get (final ContinuousCommandID command, final int offset)
    {
        return ContinuousCommandID.values ()[command.ordinal () + offset];
    }
}
