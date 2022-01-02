// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller;

import java.util.ArrayList;
import java.util.List;


/**
 * IDs for common continuous controls.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public enum ContinuousID
{
    /** The master track fader. */
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

    /** The master track knob. */
    MASTER_KNOB,

    /** A monitor knob. */
    MONITOR_KNOB,

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

    /** Volume Knob 1. */
    VOLUME_KNOB1,
    /** Volume Knob 2. */
    VOLUME_KNOB2,
    /** Volume Knob 3. */
    VOLUME_KNOB3,
    /** Volume Knob 4. */
    VOLUME_KNOB4,
    /** Volume Knob 5. */
    VOLUME_KNOB5,
    /** Volume Knob 6. */
    VOLUME_KNOB6,
    /** Volume Knob 7. */
    VOLUME_KNOB7,
    /** Volume Knob 8. */
    VOLUME_KNOB8,

    /** Parameter Knob 1. */
    PARAM_KNOB1,
    /** Parameter Knob 2. */
    PARAM_KNOB2,
    /** Parameter Knob 3. */
    PARAM_KNOB3,
    /** Parameter Knob 4. */
    PARAM_KNOB4,
    /** Parameter Knob 5. */
    PARAM_KNOB5,
    /** Parameter Knob 6. */
    PARAM_KNOB6,
    /** Parameter Knob 7. */
    PARAM_KNOB7,
    /** Parameter Knob 8. */
    PARAM_KNOB8,

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

    /** Initialize a handshake. */
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
    /** Change the loop length. */
    LOOP_LENGTH,
    /** Move the track selection. */
    MOVE_TRACK,
    /** Move the track bank selection. */
    MOVE_TRACK_BANK,

    /** Select a mode. */
    MODE_SELECTION,
    /** Select a view. */
    VIEW_SELECTION,

    /** A touchstrip fader. */
    TOUCHSTRIP,
    /** The modulation wheel. */
    MODULATION_WHEEL,
    /** The modulation wheel. */
    PITCHBEND_WHEEL,

    /** Knob 1 controlling send 1. */
    SEND1_KNOB1,
    /** Knob 2 controlling send 1. */
    SEND1_KNOB2,
    /** Knob 3 controlling send 1. */
    SEND1_KNOB3,
    /** Knob 4 controlling send 1. */
    SEND1_KNOB4,
    /** Knob 5 controlling send 1. */
    SEND1_KNOB5,
    /** Knob 6 controlling send 1. */
    SEND1_KNOB6,
    /** Knob 7 controlling send 1. */
    SEND1_KNOB7,
    /** Knob 8 controlling send 1. */
    SEND1_KNOB8,

    /** Knob 1 controlling send 2. */
    SEND2_KNOB1,
    /** Knob 2 controlling send 2. */
    SEND2_KNOB2,
    /** Knob 3 controlling send 2. */
    SEND2_KNOB3,
    /** Knob 4 controlling send 2. */
    SEND2_KNOB4,
    /** Knob 5 controlling send 2. */
    SEND2_KNOB5,
    /** Knob 6 controlling send 2. */
    SEND2_KNOB6,
    /** Knob 7 controlling send 2. */
    SEND2_KNOB7,
    /** Knob 8 controlling send 2. */
    SEND2_KNOB8,

    /** Knob 1 controlling send 3. */
    SEND3_KNOB1,
    /** Knob 2 controlling send 3. */
    SEND3_KNOB2,
    /** Knob 3 controlling send 3. */
    SEND3_KNOB3,
    /** Knob 4 controlling send 3. */
    SEND3_KNOB4,
    /** Knob 5 controlling send 3. */
    SEND3_KNOB5,
    /** Knob 6 controlling send 3. */
    SEND3_KNOB6,
    /** Knob 7 controlling send 3. */
    SEND3_KNOB7,
    /** Knob 8 controlling send 3. */
    SEND3_KNOB8,

    /** Knob 1 controlling send 4. */
    SEND4_KNOB1,
    /** Knob 2 controlling send 4. */
    SEND4_KNOB2,
    /** Knob 3 controlling send 4. */
    SEND4_KNOB3,
    /** Knob 4 controlling send 4. */
    SEND4_KNOB4,
    /** Knob 5 controlling send 4. */
    SEND4_KNOB5,
    /** Knob 6 controlling send 4. */
    SEND4_KNOB6,
    /** Knob 7 controlling send 4. */
    SEND4_KNOB7,
    /** Knob 8 controlling send 4. */
    SEND4_KNOB8;


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


    /**
     * Creates a list of ContinuousIDs which are increasing sequentially.
     *
     * @param firstID The ID to start with
     * @param size The number of IDs to iterate
     * @return The list of size IDs starting with the firstID
     */
    public static List<ContinuousID> createSequentialList (final ContinuousID firstID, final int size)
    {
        final List<ContinuousID> ids = new ArrayList<> ();
        for (int i = 0; i < size; i++)
            ids.add (get (firstID, i));
        return ids;
    }
}
