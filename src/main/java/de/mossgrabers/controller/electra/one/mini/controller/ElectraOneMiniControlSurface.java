// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import de.mossgrabers.controller.electra.one.base.controller.AbstractElectraOneControlSurface;
import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.controller.electra.one.mini.command.trigger.ElectraOneMiniTouchCombinationCommand;
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
public class ElectraOneMiniControlSurface extends AbstractElectraOneControlSurface<ElectraOneMiniConfiguration>
{
    /** The MIDI CC of the first knob on a page. */
    public static final int                ELECTRA_KNOB_1                   = 10;
    /** The MIDI CC of the first control button press on a page. */
    public static final int                ELECTRA_CTRL_PUSH_1              = 20;
    /** The MIDI CC of the first function buttons on a page. */
    public static final int                ELECTRA_FUNCTION_BUTTON_1        = 30;
    /** The MIDI CC of the first knob touch on a page. */
    public static final int                ELECTRA_KNOB_TOUCH_1             = 40;

    /** The IDs for the continuous elements. */
    public static final List<ContinuousID> KNOB_IDS                         = Collections.unmodifiableList (ContinuousID.createSequentialList (ContinuousID.KNOB1, 8));

    // @formatter:off

//    "Triangle 2, 5, 7",
//    "Triangle 3, 6, 8",

    // 0 = not touched, 1 = touched
    private static final int [] TOUCH_PATTERN_LINEAR_1__3          = new int [] { 1, 1, 1, 0, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_2__4          = new int [] { 0, 1, 1, 1, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_5__7          = new int [] { 0, 0, 0, 0, 1, 1, 1, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_6__8          = new int [] { 0, 0, 0, 0, 0, 1, 1, 1 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_1_2_4    = new int [] { 1, 1, 0, 1, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_LEFT_5_6_8    = new int [] { 0, 0, 0, 0, 1, 1, 0, 1 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_1_3_4   = new int [] { 1, 0, 1, 1, 0, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_LINEAR_RIGHT_5_7_8   = new int [] { 0, 0, 0, 0, 1, 0, 1, 1 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_1_2_5    = new int [] { 1, 1, 0, 0, 1, 0, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_2_3_6    = new int [] { 0, 1, 1, 0, 0, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_LEFT_3_4_7    = new int [] { 0, 0, 1, 1, 0, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_1_2_6   = new int [] { 1, 1, 0, 0, 0, 1, 0, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_2_3_7   = new int [] { 0, 1, 1, 0, 0, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_CORNER_RIGHT_3_4_8   = new int [] { 0, 0, 1, 1, 0, 0, 0, 1 };
    private static final int [] TOUCH_PATTERN_TRIANGLE_2_5_7       = new int [] { 0, 1, 0, 0, 1, 0, 1, 0 };
    private static final int [] TOUCH_PATTERN_TRIANGLE_3_6_8       = new int [] { 0, 0, 1, 0, 0, 1, 0, 1 };
    // @formatter:on

    private static final int [] []         TOUCH_PATTERNS                   =
    {
        TOUCH_PATTERN_LINEAR_1__3,
        TOUCH_PATTERN_LINEAR_2__4,
        TOUCH_PATTERN_LINEAR_5__7,
        TOUCH_PATTERN_LINEAR_6__8,
        TOUCH_PATTERN_LINEAR_LEFT_1_2_4,
        TOUCH_PATTERN_LINEAR_LEFT_5_6_8,
        TOUCH_PATTERN_LINEAR_RIGHT_1_3_4,
        TOUCH_PATTERN_LINEAR_RIGHT_5_7_8,
        TOUCH_PATTERN_CORNER_LEFT_1_2_5,
        TOUCH_PATTERN_CORNER_LEFT_2_3_6,
        TOUCH_PATTERN_CORNER_LEFT_3_4_7,
        TOUCH_PATTERN_CORNER_RIGHT_1_2_6,
        TOUCH_PATTERN_CORNER_RIGHT_2_3_7,
        TOUCH_PATTERN_CORNER_RIGHT_3_4_8,
        TOUCH_PATTERN_TRIANGLE_2_5_7,
        TOUCH_PATTERN_TRIANGLE_3_6_8
    };

    private static final List<Modes>       MODES                            = new ArrayList<> ();
    static
    {
        Collections.addAll (MODES, Modes.VOLUME, Modes.DEVICE_PARAMS, Modes.EQ_DEVICE_PARAMS, Modes.TRANSPORT);
    }

    protected List<ElectraOneMiniTouchCombinationCommand>              touchCombinationCommands = null;
    protected final Map<int [], ElectraOneMiniTouchCombinationCommand> commandPatterns          = new HashMap<> ();


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
        super (host, configuration, colorManager, output, input, ctrlOutput, ctrlInput, "DrivenByMoss Mini", 8, MODES, TOUCH_PATTERNS);
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


    /**
     * Update the cache for the active knob touch combinations.
     */
    public void updateTouchCombinationCommandsCache ()
    {
        synchronized (this.touchCombinationCommandsLock)
        {
            if (this.touchCombinationCommands == null)
                return;

            this.commandPatterns.clear ();

            for (int i = 0; i < this.touchCombinationCommands.size (); i++)
            {
                final ElectraOneMiniTouchCombinationCommand command = this.touchCombinationCommands.get (i);

                final int setting = this.configuration.getAssignable (i);
                if (setting != ElectraOneMiniConfiguration.ELECTRA_ONE_FUNC_OFF)
                    this.commandPatterns.put (this.touchPatterns[i], command);
            }
        }
    }


    @Override
    protected void clearShift ()
    {
        // Shift combinations on MINI since we use a dedicated SHIFT button
    }


    /**
     * Set the commands to execute on detected knob touch combinations.
     *
     * @param touchCombinationCommands The commands to set; must match the number of touch patterns
     */
    public void setTouchCombinationCommands (final List<ElectraOneMiniTouchCombinationCommand> touchCombinationCommands)
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
            for (final Map.Entry<int [], ElectraOneMiniTouchCombinationCommand> e: this.commandPatterns.entrySet ())
            {
                if (Arrays.equals (this.knobStates, e.getKey ()))
                {
                    final ElectraOneMiniTouchCombinationCommand cmd = e.getValue ();
                    cmd.execute (ButtonEvent.DOWN, 127);
                    cmd.execute (ButtonEvent.UP, 0);
                    handled = true;
                    break;
                }
            }
        }

        if (!handled)
        {
            clearShift ();
            return;
        }

        // Prevent hanging states
        Arrays.fill (this.knobStates, 0);
    }
}