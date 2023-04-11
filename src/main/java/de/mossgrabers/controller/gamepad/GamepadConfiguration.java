// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.gamepad;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.ISignalSetting;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.scale.Scales;

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * The configuration settings for a Gamepad.
 *
 * @author Jürgen Moßgraber
 */
public class GamepadConfiguration extends AbstractConfiguration
{
    /** The index of Off (no function selected). */
    public static final int                             FUNCTION_OFF                 = 0;
    /** The index of the first note (MIDI note = 0). */
    public static final int                             FUNCTION_NOTE_0              = 1;
    /** The index of the last note (MIDI note = 127). */
    public static final int                             FUNCTION_NOTE_127            = 128;
    /** The index of the first CC. */
    public static final int                             FUNCTION_CC_0                = 129;
    /** The index of the last CC. */
    public static final int                             FUNCTION_CC_127              = 256;
    /** The index of pitchbend. */
    public static final int                             FUNCTION_PITCHBEND           = 257;
    /** The index of note repeat on/off. */
    public static final int                             FUNCTION_NOTE_REPEAT_ENABLE  = 258;
    /** The index of note repeat period. */
    public static final int                             FUNCTION_NOTE_REPEAT_PERIOD  = 259;
    /** The index of note repeat length. */
    public static final int                             FUNCTION_NOTE_REPEAT_LENGTH  = 260;
    /** The index of select the previous track. */
    public static final int                             FUNCTION_TRACK_PREVIOUS      = 261;
    /** The index of select the next track. */
    public static final int                             FUNCTION_TRACK_NEXT          = 262;
    /** The index of select the previous clip. */
    public static final int                             FUNCTION_CLIP_PREVIOUS       = 263;
    /** The index of select the next clip. */
    public static final int                             FUNCTION_CLIP_NEXT           = 264;
    /** The index of create new clip. */
    public static final int                             FUNCTION_NEW_CLIP            = 265;
    /** The index of play a clip. */
    public static final int                             FUNCTION_PLAY_CLIP           = 266;
    /** The index of transport play/stop. */
    public static final int                             FUNCTION_TRANSPORT_PLAY      = 267;
    /** The index of transport metronome. */
    public static final int                             FUNCTION_TRANSPORT_METRONOME = 268;

    /** A different gamepad was selected. */
    public static final Integer                         SELECTED_GAMEPAD             = Integer.valueOf (50);

    private static final String                         NOT_AVAILABLE                = "<Not available>";
    private static final String                         CATEGORY_GAMEPAD             = "Gamepad";

    private static final List<String>                   FUNCTIONS                    = new ArrayList<> ();
    private static final Map<ControllerButton, String>  BUTTON_NAMES                 = new EnumMap<> (ControllerButton.class);
    private static final Map<ControllerAxis, String>    AXIS_NAMES                   = new EnumMap<> (ControllerAxis.class);
    private static final Map<ControllerButton, Integer> BUTTON_DEFAULTS              = new EnumMap<> (ControllerButton.class);
    private static final Map<ControllerAxis, Integer>   AXIS_DEFAULTS                = new EnumMap<> (ControllerAxis.class);

    static
    {
        FUNCTIONS.add ("Off");
        for (int i = 0; i < 128; i++)
            FUNCTIONS.add ("Note " + Scales.formatNoteAndOctave (i, -3));
        final String [] ccNames = MidiConstants.getCCNames ();
        for (int i = 0; i < 128; i++)
            FUNCTIONS.add ("CC " + ccNames[i]);
        FUNCTIONS.add ("Pitchbend");
        FUNCTIONS.add ("Note Repeat: On/Off");
        FUNCTIONS.add ("Note Repeat: Period (only for axis)");
        FUNCTIONS.add ("Note Repeat: Length (only for axis)");
        FUNCTIONS.add ("Track: Select Previous");
        FUNCTIONS.add ("Track: Select Next");
        FUNCTIONS.add ("Clip: Select Previous");
        FUNCTIONS.add ("Clip: Select Next");
        FUNCTIONS.add ("Clip: New (only for buttons)");
        FUNCTIONS.add ("Clip: Play (only for buttons)");
        FUNCTIONS.add ("Transport: Play/Stop (only for buttons)");
        FUNCTIONS.add ("Transport: Metronome (only for buttons)");

        BUTTON_NAMES.put (ControllerButton.A, "A");
        BUTTON_NAMES.put (ControllerButton.B, "B");
        BUTTON_NAMES.put (ControllerButton.X, "X");
        BUTTON_NAMES.put (ControllerButton.Y, "Y");
        BUTTON_NAMES.put (ControllerButton.BACK, "Back");
        BUTTON_NAMES.put (ControllerButton.GUIDE, "Guide");
        BUTTON_NAMES.put (ControllerButton.START, "Start");
        BUTTON_NAMES.put (ControllerButton.LEFTSTICK, "Left Stick");
        BUTTON_NAMES.put (ControllerButton.RIGHTSTICK, "Right Stick");
        BUTTON_NAMES.put (ControllerButton.LEFTBUMPER, "Left Bumper");
        BUTTON_NAMES.put (ControllerButton.RIGHTBUMPER, "Right Bumper");
        BUTTON_NAMES.put (ControllerButton.DPAD_UP, "Cursor Up");
        BUTTON_NAMES.put (ControllerButton.DPAD_DOWN, "Cursor Down");
        BUTTON_NAMES.put (ControllerButton.DPAD_LEFT, "Cursor Left");
        BUTTON_NAMES.put (ControllerButton.DPAD_RIGHT, "Cursor Right");
        BUTTON_NAMES.put (ControllerButton.BUTTON_MISC1, "Xbox Series X share button, PS5 microphone button, Nintendo Switch Pro capture button");
        BUTTON_NAMES.put (ControllerButton.BUTTON_PADDLE1, "Xbox Elite paddle P1");
        BUTTON_NAMES.put (ControllerButton.BUTTON_PADDLE2, "Xbox Elite paddle P3");
        BUTTON_NAMES.put (ControllerButton.BUTTON_PADDLE3, "Xbox Elite paddle P2");
        BUTTON_NAMES.put (ControllerButton.BUTTON_PADDLE4, "Xbox Elite paddle P4");
        BUTTON_NAMES.put (ControllerButton.BUTTON_TOUCHPAD, "PS4/PS5 touchpad button");

        BUTTON_DEFAULTS.put (ControllerButton.A, Integer.valueOf (FUNCTION_NOTE_REPEAT_ENABLE));
        BUTTON_DEFAULTS.put (ControllerButton.B, Integer.valueOf (FUNCTION_NEW_CLIP));
        BUTTON_DEFAULTS.put (ControllerButton.X, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.Y, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.BACK, Integer.valueOf (FUNCTION_PLAY_CLIP));
        BUTTON_DEFAULTS.put (ControllerButton.GUIDE, Integer.valueOf (FUNCTION_TRANSPORT_METRONOME));
        BUTTON_DEFAULTS.put (ControllerButton.START, Integer.valueOf (FUNCTION_TRANSPORT_PLAY));
        BUTTON_DEFAULTS.put (ControllerButton.LEFTSTICK, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.RIGHTSTICK, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.LEFTBUMPER, Integer.valueOf (FUNCTION_NOTE_0 + 37));
        BUTTON_DEFAULTS.put (ControllerButton.RIGHTBUMPER, Integer.valueOf (FUNCTION_NOTE_0 + 38));
        BUTTON_DEFAULTS.put (ControllerButton.DPAD_UP, Integer.valueOf (FUNCTION_TRACK_PREVIOUS));
        BUTTON_DEFAULTS.put (ControllerButton.DPAD_DOWN, Integer.valueOf (FUNCTION_TRACK_NEXT));
        BUTTON_DEFAULTS.put (ControllerButton.DPAD_LEFT, Integer.valueOf (FUNCTION_CLIP_PREVIOUS));
        BUTTON_DEFAULTS.put (ControllerButton.DPAD_RIGHT, Integer.valueOf (FUNCTION_CLIP_NEXT));
        BUTTON_DEFAULTS.put (ControllerButton.BUTTON_MISC1, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.BUTTON_PADDLE1, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.BUTTON_PADDLE2, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.BUTTON_PADDLE3, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.BUTTON_PADDLE4, Integer.valueOf (0));
        BUTTON_DEFAULTS.put (ControllerButton.BUTTON_TOUCHPAD, Integer.valueOf (0));

        AXIS_NAMES.put (ControllerAxis.LEFTX, "Left X");
        AXIS_NAMES.put (ControllerAxis.LEFTY, "Left Y");
        AXIS_NAMES.put (ControllerAxis.RIGHTX, "Right X");
        AXIS_NAMES.put (ControllerAxis.RIGHTY, "Right Y");
        AXIS_NAMES.put (ControllerAxis.TRIGGERLEFT, "Trigger Left");
        AXIS_NAMES.put (ControllerAxis.TRIGGERRIGHT, "Trigger Right");

        AXIS_DEFAULTS.put (ControllerAxis.LEFTX, Integer.valueOf (FUNCTION_PITCHBEND));
        AXIS_DEFAULTS.put (ControllerAxis.LEFTY, Integer.valueOf (FUNCTION_CC_0 + 1));
        AXIS_DEFAULTS.put (ControllerAxis.RIGHTX, Integer.valueOf (FUNCTION_CC_0 + 71));
        AXIS_DEFAULTS.put (ControllerAxis.RIGHTY, Integer.valueOf (FUNCTION_CC_0 + 74));
        AXIS_DEFAULTS.put (ControllerAxis.TRIGGERLEFT, Integer.valueOf (FUNCTION_NOTE_0 + 36));
        AXIS_DEFAULTS.put (ControllerAxis.TRIGGERRIGHT, Integer.valueOf (FUNCTION_NOTE_0 + 39));
    }

    private final ControllerManager              gamepadManager;
    private final List<String>                   gamepadNames    = new ArrayList<> ();
    private final IEnumSetting []                buttonSettings  = new IEnumSetting [ControllerButton.values ().length];
    private final IEnumSetting []                axisSettings    = new IEnumSetting [ControllerAxis.values ().length];
    private int                                  selectedGamepad = -1;
    private final Map<ControllerButton, Integer> buttonFunctions = new EnumMap<> (ControllerButton.class);
    private final Map<ControllerAxis, Integer>   axisFunctions   = new EnumMap<> (ControllerAxis.class);


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param gamepadManager The gamepad manager to get access to the available gamepads
     */
    public GamepadConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes, final ControllerManager gamepadManager)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.gamepadManager = gamepadManager;
        this.fillGamepads ();
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        final IEnumSetting gamepadSetting = globalSettings.getEnumSetting ("Gamepad", CATEGORY_GAMEPAD, this.gamepadNames, this.gamepadNames.get (0));

        // There is an error popping up if a list is created with only one item
        gamepadSetting.setVisible (this.gamepadManager.getNumControllers () > 0);

        final ISignalSetting actionSetting = globalSettings.getSignalSetting ("Update the list of connected gamepads", CATEGORY_GAMEPAD, "Refresh");
        actionSetting.addSignalObserver (Void -> this.host.restart ());

        final ControllerButton [] buttons = ControllerButton.values ();
        final String buttonCategory = CATEGORY_GAMEPAD + " - Buttons";
        for (int i = 0; i < buttons.length; i++)
        {
            final int pos = i;

            final String initialValue = FUNCTIONS.get (BUTTON_DEFAULTS.get (buttons[i]).intValue ());
            this.buttonSettings[i] = globalSettings.getEnumSetting (BUTTON_NAMES.get (buttons[i]), buttonCategory, FUNCTIONS, initialValue);
            this.buttonSettings[i].addValueObserver (value -> {

                this.buttonFunctions.put (buttons[pos], Integer.valueOf (FUNCTIONS.indexOf (value)));

            });
        }

        final ControllerAxis [] axes = ControllerAxis.values ();
        final String axisCategory = CATEGORY_GAMEPAD + " - Axis";
        for (int i = 0; i < axes.length; i++)
        {
            final int pos = i;

            final String initialValue = FUNCTIONS.get (AXIS_DEFAULTS.get (axes[i]).intValue ());
            this.axisSettings[i] = globalSettings.getEnumSetting (AXIS_NAMES.get (axes[i]), axisCategory, FUNCTIONS, initialValue);
            this.axisSettings[i].addValueObserver (value -> {

                this.axisFunctions.put (axes[pos], Integer.valueOf (FUNCTIONS.indexOf (value)));

            });
        }

        // Do not trigger before all function enumeration settings are created
        gamepadSetting.addValueObserver (value -> {

            this.selectedGamepad = NOT_AVAILABLE.equals (value) ? -1 : this.gamepadNames.indexOf (value) - 1;
            this.updateFunctionSettings ();
            this.notifyObservers (SELECTED_GAMEPAD);

        });

        this.activateNoteRepeatSetting (documentSettings);
    }


    private void updateFunctionSettings ()
    {
        try
        {
            final ControllerIndex gamepad = this.selectedGamepad < 0 ? null : this.gamepadManager.getControllerIndex (this.selectedGamepad);

            final ControllerButton [] buttons = ControllerButton.values ();
            for (int i = 0; i < buttons.length; i++)
                this.buttonSettings[i].setEnabled (gamepad != null && gamepad.isButtonAvailable (buttons[i]));

            final ControllerAxis [] axes = ControllerAxis.values ();
            for (int i = 0; i < axes.length; i++)
                this.axisSettings[i].setEnabled (gamepad != null && gamepad.isAxisAvailable (axes[i]));
        }
        catch (final RuntimeException | ControllerUnpluggedException ex)
        {
            this.host.error ("Could not access controller.", ex);
        }
    }


    /**
     * Get the index of the selected gamepad.
     *
     * @return The selected gamepad
     */
    public int getSelectedGamepad ()
    {
        return this.selectedGamepad;
    }


    /**
     * Get the selected function to execute for a gamepad button.
     *
     * @param button The button
     * @return The selected function
     */
    public Integer getFunction (final ControllerButton button)
    {
        return this.buttonFunctions.get (button);
    }


    /**
     * Get the selected function to execute for a gamepad axis (continuous control).
     *
     * @param axis The axis
     * @return The selected function
     */
    public Integer getFunction (final ControllerAxis axis)
    {
        return this.axisFunctions.get (axis);
    }


    private void fillGamepads ()
    {
        final int numGamepads = this.gamepadManager.getNumControllers ();
        this.gamepadNames.add ("None");
        if (numGamepads == 0)
        {
            this.gamepadNames.add ("<No controllers>");
            return;
        }

        for (int i = 0; i < numGamepads; i++)
        {
            try
            {
                this.gamepadNames.add (this.gamepadManager.getControllerIndex (i).getName ());
            }
            catch (final ControllerUnpluggedException ex)
            {
                this.gamepadNames.add (NOT_AVAILABLE);
            }
        }
    }
}
