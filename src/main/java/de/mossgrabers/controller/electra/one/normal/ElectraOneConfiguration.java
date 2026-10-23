// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.normal;

import java.util.List;

import de.mossgrabers.controller.electra.one.base.AbstractElectraOneConfiguration;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Electra One Mk1/2.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneConfiguration extends AbstractElectraOneConfiguration
{
    private static final String [] ASSIGNABLE_VALUES                  =
    {
        "Off",
        "Shift Button",
        "Mode: Mixer",
        "Mode: Sends",
        "Mode: Device",
        "Mode: EQ",
        "Mode: Transport",
        "Mode: Session",
        "Mode: Project/Track Parameters",
        "Mode: Select Synth Preset",
        "Function: Toggle Play",
        "Function: Toggle Record",
        "Function: Stop All Clips",
        "Function: Toggle Clip Overdub",
        "Function: Undo",
        "Function: Tap Tempo",
        "Function: New Button",
        "Function: Clip Based Looper",
        "Function: Panel layout arrange",
        "Function: Panel layout mix",
        "Function: Panel layout edit",
        "Function: Add instrument track",
        "Function: Add audio track",
        "Function: Add effect track",
        "Function: Quantize",
        "Action"
    };

    /** No function. */
    public static final int        ELECTRA_ONE_FUNC_OFF               = 0;
    /** Emulate the shift button. */
    public static final int        ELECTRA_ONE_FUNC_SHIFT_BUTTON      = 1;
    /** Select mixer mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_MIXER        = 2;
    /** Select sends mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_SENDS        = 3;
    /** Select device mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_DEVICE       = 4;
    /** Select EQ mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_EQ           = 5;
    /** Select transport mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_TRANSPORT    = 6;
    /** Select session mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_SESSION      = 7;
    /** Select project/track parameter mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_PARAMS       = 8;
    /** Select a matching synthesizer preset. */
    public static final int        ELECTRA_ONE_FUNC_MODE_SYNTH_PRESET = 9;
    /** Execute an action. */
    public static final int        ELECTRA_ONE_FUNC_MODE_ACTION       = 25;

    private static final int       FOOTSWITCH_CMDS_OFFSET             = ELECTRA_ONE_FUNC_MODE_SYNTH_PRESET + 1;

    private static final int []    ASSIGNABLE_BUTTON_DEFAULTS         =
    {
        // Linear
        ELECTRA_ONE_FUNC_MODE_MIXER,
        ELECTRA_ONE_FUNC_MODE_SENDS,
        ELECTRA_ONE_FUNC_MODE_DEVICE,
        ELECTRA_ONE_FUNC_MODE_EQ,
        ELECTRA_ONE_FUNC_MODE_TRANSPORT,
        ELECTRA_ONE_FUNC_MODE_SESSION,
        ELECTRA_ONE_FUNC_MODE_PARAMS,
        ELECTRA_ONE_FUNC_MODE_SYNTH_PRESET,

        // Linear Left
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Linear Right
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Linear Broad
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Corner Left
        ELECTRA_ONE_FUNC_SHIFT_BUTTON,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Corner Right
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_SHIFT_BUTTON,

        // Triangle
        FOOTSWITCH_CMDS_OFFSET + AbstractConfiguration.FOOTSWITCH_TOGGLE_PLAY,
        FOOTSWITCH_CMDS_OFFSET + AbstractConfiguration.FOOTSWITCH_TOGGLE_RECORD,
        FOOTSWITCH_CMDS_OFFSET + AbstractConfiguration.FOOTSWITCH_STOP_ALL_CLIPS,
        FOOTSWITCH_CMDS_OFFSET + AbstractConfiguration.FOOTSWITCH_UNDO
    };

    private static final String [] ASSIGNABLE_BUTTON_NAMES            =
    {
        "Linear 1 - 3",
        "Linear 2 - 4",
        "Linear 3 - 5",
        "Linear 4 - 6",
        "Linear 7 - 9",
        "Linear 8 - 10",
        "Linear 9 - 11",
        "Linear 10 - 12",
        "Linear Left 1, 2, 4",
        "Linear Left 2, 3, 5",
        "Linear Left 3, 4, 6",
        "Linear Left 7, 8, 10",
        "Linear Left 8, 9, 11",
        "Linear Left 9, 10, 12",
        "Linear Right 1, 3, 4",
        "Linear Right 2, 4, 5",
        "Linear Right 3, 5, 6",
        "Linear Right 7, 9, 10",
        "Linear Right 8, 10, 11",
        "Linear Right 9, 11, 12",
        "Linear Broad 1, 3, 5",
        "Linear Broad 2, 4, 6",
        "Linear Broad 7, 9, 11",
        "Linear Broad 8, 10, 12",
        "Corner Left 1, 2, 7",
        "Corner Left 2, 3, 8",
        "Corner Left 3, 4, 9",
        "Corner Left 4, 5, 10",
        "Corner Left 5, 6, 11",
        "Corner Right 1, 2, 8",
        "Corner Right 2, 3, 9",
        "Corner Right 3, 4, 10",
        "Corner Right 4, 5, 11",
        "Corner Right 5, 6, 12",
        "Triangle 2, 7, 9",
        "Triangle 3, 8, 10",
        "Triangle 4, 9, 11",
        "Triangle 5, 10, 12"
    };


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public ElectraOneConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes, ASSIGNABLE_BUTTON_NAMES, ASSIGNABLE_BUTTON_DEFAULTS, ASSIGNABLE_VALUES, 100);
    }
}
