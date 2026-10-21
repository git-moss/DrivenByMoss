// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini;

import java.util.List;

import de.mossgrabers.controller.electra.one.base.AbstractElectraOneConfiguration;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Electra One Mini.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniConfiguration extends AbstractElectraOneConfiguration
{
    private static final String [] ASSIGNABLE_VALUES               =
    {
        "Off",
        "Mode: Mixer",
        "Mode: Device",
        "Mode: EQ",
        "Mode: Transport",
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
    public static final int        ELECTRA_ONE_FUNC_OFF            = 0;
    /** Select mixer mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_MIXER     = 1;
    /** Select device mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_DEVICE    = 2;
    /** Select EQ mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_EQ        = 3;
    /** Select transport mode. */
    public static final int        ELECTRA_ONE_FUNC_MODE_TRANSPORT = 4;
    /** Execute an action. */
    public static final int        ELECTRA_ONE_FUNC_MODE_ACTION    = 25;

    private static final int       FOOTSWITCH_CMDS_OFFSET          = ELECTRA_ONE_FUNC_MODE_TRANSPORT + 1;

    private static final int []    ASSIGNABLE_BUTTON_DEFAULTS      =
    {
        // Linear
        ELECTRA_ONE_FUNC_MODE_MIXER,
        ELECTRA_ONE_FUNC_MODE_DEVICE,
        ELECTRA_ONE_FUNC_MODE_EQ,
        ELECTRA_ONE_FUNC_MODE_TRANSPORT,

        // Linear Left
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Linear Right
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Corner Left
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Corner Right
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,
        ELECTRA_ONE_FUNC_OFF,

        // Triangle
        FOOTSWITCH_CMDS_OFFSET + AbstractConfiguration.FOOTSWITCH_TOGGLE_PLAY,
        FOOTSWITCH_CMDS_OFFSET + AbstractConfiguration.FOOTSWITCH_TOGGLE_RECORD,
    };

    private static final String [] ASSIGNABLE_BUTTON_NAMES         =
    {
        "Linear 1 - 3",
        "Linear 2 - 4",
        "Linear 5 - 7",
        "Linear 6 - 8",
        "Linear Left 1, 2, 4",
        "Linear Left 5, 6, 8",
        "Linear Right 1, 3, 4",
        "Linear Right 5, 7, 8",
        "Corner Left 1, 2, 5",
        "Corner Left 2, 3, 6",
        "Corner Left 3, 4, 7",
        "Corner Right 1, 2, 6",
        "Corner Right 2, 3, 7",
        "Corner Right 3, 4, 8",
        "Triangle 2, 5, 7",
        "Triangle 3, 6, 8",
    };


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator-modes
     */
    public ElectraOneMiniConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes, ASSIGNABLE_BUTTON_NAMES, ASSIGNABLE_BUTTON_DEFAULTS, ASSIGNABLE_VALUES);
    }
}
