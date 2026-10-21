// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import de.mossgrabers.controller.electra.one.base.controller.AbstractElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.FrameworkException;


/**
 * A control surface which supports the Electra One Mk1/2 controllers.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniControlSurface extends AbstractElectraOneControlSurface<ElectraOneMiniConfiguration>
{
    /** The MIDI CC of the first control on a page. */
    public static final int                ELECTRA_CTRL_1                     = 10;

    /** The IDs for the continuous elements. */
    public static final List<ContinuousID> KNOB_IDS                           = Collections.unmodifiableList (ContinuousID.createSequentialList (ContinuousID.KNOB1, 8));

    // @formatter:off

    // 0 = not touched, 1 = touched
    private static final int [] TOUCH_PATTERN_LINEAR_1__3          = new int [] { 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_2__4          = new int [] { 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_3__5          = new int [] { 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_4__6          = new int [] { 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_7__9          = new int [] { 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_8__10         = new int [] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_9__11         = new int [] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_10__12        = new int [] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_1_2_4    = new int [] { 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_2_3_5    = new int [] { 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_3_4_6    = new int [] { 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_7_8_10   = new int [] { 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_8_9_11   = new int [] { 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_9_10_12  = new int [] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_1_3_4   = new int [] { 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_2_4_5   = new int [] { 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_3_5_6   = new int [] { 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_7_9_10  = new int [] { 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_8_10_11 = new int [] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_9_11_12 = new int [] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1 };
    private static final int [] TOUCH_PATTERN_LINEAR_BROAD_1_3_5   = new int [] { 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_BROAD_2_4_6   = new int [] { 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_BROAD_7_9_11  = new int [] { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_BROAD_8_10_12 = new int [] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_1_2_7    = new int [] { 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_2_3_8    = new int [] { 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_3_4_9    = new int [] { 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_4_5_10   = new int [] { 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_5_6_11   = new int [] { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_1_2_8   = new int [] { 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_2_3_9   = new int [] { 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_3_4_10  = new int [] { 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_4_5_11  = new int [] { 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_5_6_12  = new int [] { 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1 };
    private static final int [] TOUCH_PATTERN_TRIANGLE_2_7_9       = new int [] { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_TRIANGLE_3_8_10      = new int [] { 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_TRIANGLE_4_9_11      = new int [] { 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_TRIANGLE_5_10_12     = new int [] { 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1 };
    // @formatter:on

    private static final int [] []         TOUCH_PATTERNS                     =
    {
        TOUCH_PATTERN_LINEAR_1__3,
        TOUCH_PATTERN_LINEAR_2__4,
        TOUCH_PATTERN_LINEAR_3__5,
        TOUCH_PATTERN_LINEAR_4__6,
        TOUCH_PATTERN_LINEAR_7__9,
        TOUCH_PATTERN_LINEAR_8__10,
        TOUCH_PATTERN_LINEAR_9__11,
        TOUCH_PATTERN_LINEAR_10__12,
        TOUCH_PATTERN_LINEAR_LEFT_1_2_4,
        TOUCH_PATTERN_LINEAR_LEFT_2_3_5,
        TOUCH_PATTERN_LINEAR_LEFT_3_4_6,
        TOUCH_PATTERN_LINEAR_LEFT_7_8_10,
        TOUCH_PATTERN_LINEAR_LEFT_8_9_11,
        TOUCH_PATTERN_LINEAR_LEFT_9_10_12,
        TOUCH_PATTERN_LINEAR_RIGHT_1_3_4,
        TOUCH_PATTERN_LINEAR_RIGHT_2_4_5,
        TOUCH_PATTERN_LINEAR_RIGHT_3_5_6,
        TOUCH_PATTERN_LINEAR_RIGHT_7_9_10,
        TOUCH_PATTERN_LINEAR_RIGHT_8_10_11,
        TOUCH_PATTERN_LINEAR_RIGHT_9_11_12,
        TOUCH_PATTERN_LINEAR_BROAD_1_3_5,
        TOUCH_PATTERN_LINEAR_BROAD_2_4_6,
        TOUCH_PATTERN_LINEAR_BROAD_7_9_11,
        TOUCH_PATTERN_LINEAR_BROAD_8_10_12,
        TOUCH_PATTERN_CORNER_LEFT_1_2_7,
        TOUCH_PATTERN_CORNER_LEFT_2_3_8,
        TOUCH_PATTERN_CORNER_LEFT_3_4_9,
        TOUCH_PATTERN_CORNER_LEFT_4_5_10,
        TOUCH_PATTERN_CORNER_LEFT_5_6_11,
        TOUCH_PATTERN_CORNER_RIGHT_1_2_8,
        TOUCH_PATTERN_CORNER_RIGHT_2_3_9,
        TOUCH_PATTERN_CORNER_RIGHT_3_4_10,
        TOUCH_PATTERN_CORNER_RIGHT_4_5_11,
        TOUCH_PATTERN_CORNER_RIGHT_5_6_12,
        TOUCH_PATTERN_TRIANGLE_2_7_9,
        TOUCH_PATTERN_TRIANGLE_3_8_10,
        TOUCH_PATTERN_TRIANGLE_4_9_11,
        TOUCH_PATTERN_TRIANGLE_5_10_12
    };

    private static final List<Modes>       MODES                              = new ArrayList<> ();
    static
    {
        Collections.addAll (MODES, Modes.VOLUME, Modes.SEND, Modes.DEVICE_PARAMS, Modes.EQ_DEVICE_PARAMS, Modes.TRANSPORT, Modes.SESSION, Modes.PROJECT);
    }


    /**
     * Constructor.
     *
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param ctrlOutput The control MIDI output
     * @param ctrlInput The control MIDI input
     */
    public ElectraOneMiniControlSurface (final IHost host, final ColorManager colorManager, final ElectraOneMiniConfiguration configuration, final IMidiOutput output, final IMidiInput input, final IMidiInput ctrlInput, final IMidiOutput ctrlOutput)
    {
        super (host, configuration, colorManager, output, input, "DrivenByMoss Mini", 8, MODES, TOUCH_PATTERNS);
    }


    /** {@inheritDoc} */
    @Override
    protected void checkFirmwareResult (final JsonNode root)
    {
        final int version = root.get ("versionSeq").asInt ();
        final String versionText = root.get ("versionText").asText ();
        if (version < 500000400)
            throw new FrameworkException ("Firmware must be at least 5.0.4 but is " + versionText);

        final JsonNode modelNode = root.get ("model");
        if (modelNode != null)
        {
            final String model = modelNode.asText ();
            if (!"mini".equals (model))
                throw new FrameworkException ("This extension does only support the Electro One Mini but found model: " + model);
        }
        this.host.println ("Model: Mini - Firmware: " + versionText);
    }
}