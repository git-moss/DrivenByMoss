// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.normal.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import de.mossgrabers.controller.electra.one.base.controller.AbstractElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.normal.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.normal.command.trigger.ElectraOneTouchCombinationCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;


/**
 * A control surface which supports the Electra One Mk1/2 controllers.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneControlSurface extends AbstractElectraOneControlSurface<ElectraOneConfiguration>
{
    /** The MIDI CC of the first control on a page. */
    public static final int                ELECTRA_CTRL_1 = 10;
    /** The MIDI CC of the first button on a page. */
    public static final int                ELECTRA_ROW_1  = 70;

    /** The IDs for the continuous elements. */
    public static final List<ContinuousID> KNOB_IDS;
    static
    {
        final List<ContinuousID> knobIds = new ArrayList<> ();
        knobIds.addAll (ContinuousID.createSequentialList (ContinuousID.VOLUME_KNOB1, 6));
        knobIds.addAll (ContinuousID.createSequentialList (ContinuousID.PAN_KNOB1, 6));
        knobIds.addAll (ContinuousID.createSequentialList (ContinuousID.FADER1, 6));
        knobIds.addAll (ContinuousID.createSequentialList (ContinuousID.KNOB1, 6));
        knobIds.addAll (ContinuousID.createSequentialList (ContinuousID.PARAM_KNOB1, 6));
        knobIds.addAll (ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 6));
        KNOB_IDS = Collections.unmodifiableList (knobIds);
    }

    private static final ButtonID []     BUTTON_ROW_IDS                     =
    {
        ButtonID.ROW1_1,
        ButtonID.ROW2_1,
        ButtonID.ROW3_1,
        ButtonID.ROW4_1,
        ButtonID.ROW5_1,
        ButtonID.ROW6_1
    };

    private static final ContinuousID [] CTRL_ROW_IDS                       =
    {
        ContinuousID.VOLUME_KNOB1,
        ContinuousID.PAN_KNOB1,
        ContinuousID.FADER1,
        ContinuousID.KNOB1,
        ContinuousID.PARAM_KNOB1,
        ContinuousID.DEVICE_KNOB1,
    };

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

    private static final int [] []       TOUCH_PATTERNS                     =
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

    private static final List<Modes>     MODES                              = new ArrayList<> ();
    static
    {
        Collections.addAll (MODES, Modes.VOLUME, Modes.SEND, Modes.DEVICE_PARAMS, Modes.EQ_DEVICE_PARAMS, Modes.TRANSPORT, Modes.SESSION, Modes.PROJECT);
    }

    protected List<ElectraOneTouchCombinationCommand>              touchCombinationCommands = null;
    protected final Map<int [], ElectraOneTouchCombinationCommand> commandPatterns          = new HashMap<> ();
    private final List<int []>                                     shiftPatterns            = new ArrayList<> ();
    private ElectraOneType                                         electraOneType           = ElectraOneType.MK1;


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
    public ElectraOneControlSurface (final IHost host, final ColorManager colorManager, final ElectraOneConfiguration configuration, final IMidiOutput output, final IMidiInput input, final IMidiInput ctrlInput, final IMidiOutput ctrlOutput)
    {
        super (host, configuration, colorManager, output, input, ctrlOutput, ctrlInput, "DrivenByMoss", 12, MODES, TOUCH_PATTERNS);
    }


    /** {@inheritDoc} */
    @Override
    protected void checkFirmwareResult (final JsonNode root)
    {
        final int version = root.get ("versionSeq").asInt ();
        final String versionText = root.get ("versionText").asText ();
        if (version < 300400300)
            throw new FrameworkException ("Firmware must be at least 3.4.3 but is " + versionText);

        final JsonNode modelNode = root.get ("model");
        if (modelNode != null)
        {
            final String model = modelNode.asText ();
            switch (model)
            {
                case "mk1":
                    this.electraOneType = ElectraOneType.MK1;
                    break;
                case "mk2":
                    this.electraOneType = ElectraOneType.MK2;
                    break;
                default:
                    throw new FrameworkException ("This extension does only support the Electro One Mk1 and Mk2 controller models but found model: " + model);
            }
        }
        this.host.println ("Model: " + this.electraOneType + " - Firmware: " + versionText);
    }


    /**
     * Update the cache for the active knob touch combinations.
     */
    public void updateTouchCombinationCommandsCache ()
    {
        synchronized (this.touchCombinationCommandsLock)
        {
            if (this.touchCombinationCommands == null)
                return;

            this.shiftPatterns.clear ();
            this.commandPatterns.clear ();

            for (int i = 0; i < this.touchCombinationCommands.size (); i++)
            {
                final ElectraOneTouchCombinationCommand command = this.touchCombinationCommands.get (i);

                final int setting = this.configuration.getAssignable (i);
                if (setting != ElectraOneConfiguration.ELECTRA_ONE_FUNC_OFF)
                {
                    if (setting == ElectraOneConfiguration.ELECTRA_ONE_FUNC_SHIFT_BUTTON)
                    {
                        // Create specific shift patterns which ignore all other touched knobs
                        // (state = 2)
                        final int [] shiftPattern = new int [this.touchPatterns[i].length];
                        for (int pos = 0; pos < shiftPattern.length; pos++)
                            shiftPattern[pos] = this.touchPatterns[i][pos] == 1 ? 1 : 2;
                        this.shiftPatterns.add (shiftPattern);
                    }
                    else
                        this.commandPatterns.put (this.touchPatterns[i], command);
                }
            }
        }
    }


    /**
     * Get the button ID for an element on the page.
     *
     * @param row The row of the button
     * @param column The column of the button
     * @return The ID
     */
    public static ButtonID getButtonID (final int row, final int column)
    {
        return ButtonID.get (BUTTON_ROW_IDS[row], column);
    }


    /**
     * Get the continuous ID for an element on the page.
     *
     * @param row The row of the control
     * @param column The column of the control
     * @return The ID
     */
    public static ContinuousID getContinuousID (final int row, final int column)
    {
        return ContinuousID.get (CTRL_ROW_IDS[row], column);
    }


    /**
     * Set the commands to execute on detected knob touch combinations.
     *
     * @param touchCombinationCommands The commands to set; must match the number of touch patterns
     */
    public void setTouchCombinationCommands (final List<ElectraOneTouchCombinationCommand> touchCombinationCommands)
    {
        if (touchCombinationCommands.size () != this.touchPatterns.length)
            throw new FrameworkException ("The number of touch commands must match the number of available touch patterns.");

        this.touchCombinationCommands = touchCombinationCommands;
    }


    /** {@inheritDoc} */
    @Override
    protected void matchStates ()
    {
        boolean handled = false;

        synchronized (this.touchCombinationCommandsLock)
        {
            for (final Map.Entry<int [], ElectraOneTouchCombinationCommand> e: this.commandPatterns.entrySet ())
            {
                if (Arrays.equals (this.knobStates, e.getKey ()))
                {
                    final ElectraOneTouchCombinationCommand cmd = e.getValue ();
                    cmd.execute (ButtonEvent.DOWN, 127);
                    cmd.execute (ButtonEvent.UP, 0);
                    handled = true;
                    break;
                }
            }
        }

        if (!handled)
        {
            this.clearShift ();
            return;
        }

        // Prevent hanging states
        Arrays.fill (this.knobStates, 0);
    }
}