// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;


/**
 * The configuration settings for Generic Flexi.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiConfiguration extends AbstractConfiguration
{
    private static final String [] FUNCTIONS            = new String []
    {
        "Off",
        "Global: Undo",
        "Global: Redo",
        "Global: Previous Project",
        "Global: Next Project",
        "Global: Toggle Audio Engine",
        "Transport: Play",
        "Transport: Stop",
        "Transport: Restart",
        "Transport: Toggle Repeat",
        "Transport: Toggle Metronome",
        "Transport: Set Metronome Volume",
        "Transport: Toggle Metronome in Pre-roll",
        "Transport: Toggle Punch In",
        "Transport: Toggle Punch Out",
        "Transport: Toggle Record",
        "Transport: Toggle Arranger Overdub",
        "Transport: Toggle Clip Overdub",
        "Transport: Set Crossfader",
        "Transport: Toggle Arranger Automation Write",
        "Transport: Toggle Clip Automation Write",
        "Transport: Set Write Mode: Latch",
        "Transport: Set Write Mode: Touch",
        "Transport: Set Write Mode: Write",
        "Transport: Set Tempo",
        "Transport: Tap Tempo",
        "Transport: Move Play Cursor",
        "Layout: Set Arrange Layout",
        "Layout: Set Mix Layout",
        "Layout: Set Edit Layout",
        "Layout: Toggle Note Editor",
        "Layout: Toggle Automation Editor",
        "Layout: Toggle Devices Panel",
        "Layout: Toggle Mixer Panel",
        "Layout: Toggle Fullscreen",
        "Layout: Toggle Arranger Cue Markers",
        "Layout: Toggle Arranger Playback Follow",
        "Layout: Toggle Arranger Track Row Height",
        "Layout: Toggle Arranger Clip Launcher Section",
        "Layout: Toggle Arranger Time Line",
        "Layout: Toggle Arranger IO Section",
        "Layout: Toggle Arranger Effect Tracks",
        "Layout: Toggle Mixer Clip Launcher Section",
        "Layout: Toggle Mixer Cross Fade Section",
        "Layout: Toggle Mixer Device Section",
        "Layout: Toggle Mixer sendsSection",
        "Layout: Toggle Mixer IO Section",
        "Layout: Toggle Mixer Meter Section",
        "Track: Add Audio Track",
        "Track: Add Effect Track",
        "Track: Add Instrument Track",
        "Track: Select Previous Bank Page",
        "Track: Select Next Bank Page",
        "Track: Select Previous Track",
        "Track: Select Next Track",
        "Track 1: Select",
        "Track 2: Select",
        "Track 3: Select",
        "Track 4: Select",
        "Track 5: Select",
        "Track 6: Select",
        "Track 7: Select",
        "Track 8: Select",
        "Track 1: Toggle Active",
        "Track 2: Toggle Active",
        "Track 3: Toggle Active",
        "Track 4: Toggle Active",
        "Track 5: Toggle Active",
        "Track 6: Toggle Active",
        "Track 7: Toggle Active",
        "Track 8: Toggle Active",
        "Track Selected: Toggle Active",
        "Track 1: Set Volume",
        "Track 2: Set Volume",
        "Track 3: Set Volume",
        "Track 4: Set Volume",
        "Track 5: Set Volume",
        "Track 6: Set Volume",
        "Track 7: Set Volume",
        "Track 8: Set Volume",
        "Track Selected: Set Volume Track",
        "Track 1: Set Panorama",
        "Track 2: Set Panorama",
        "Track 3: Set Panorama",
        "Track 4: Set Panorama",
        "Track 5: Set Panorama",
        "Track 6: Set Panorama",
        "Track 7: Set Panorama",
        "Track 8: Set Panorama",
        "Track Selected: Set Panorama",
        "Track 1: Toggle Mute",
        "Track 2: Toggle Mute",
        "Track 3: Toggle Mute",
        "Track 4: Toggle Mute",
        "Track 5: Toggle Mute",
        "Track 6: Toggle Mute",
        "Track 7: Toggle Mute",
        "Track 8: Toggle Mute",
        "Track Selected: Toggle Mute",
        "Track 1: Toggle Solo",
        "Track 2: Toggle Solo",
        "Track 3: Toggle Solo",
        "Track 4: Toggle Solo",
        "Track 5: Toggle Solo",
        "Track 6: Toggle Solo",
        "Track 7: Toggle Solo",
        "Track 8: Toggle Solo",
        "Track Selected: Toggle Solo",
        "Track 1: Toggle Arm",
        "Track 2: Toggle Arm",
        "Track 3: Toggle Arm",
        "Track 4: Toggle Arm",
        "Track 5: Toggle Arm",
        "Track 6: Toggle Arm",
        "Track 7: Toggle Arm",
        "Track 8: Toggle Arm",
        "Track Selected: Toggle Arm",
        "Track 1: Toggle Monitor",
        "Track 2: Toggle Monitor",
        "Track 3: Toggle Monitor",
        "Track 4: Toggle Monitor",
        "Track 5: Toggle Monitor",
        "Track 6: Toggle Monitor",
        "Track 7: Toggle Monitor",
        "Track 8: Toggle Monitor",
        "Track Selected: Toggle Monitor",
        "Track 1: Toggle Auto Monitor",
        "Track 2: Toggle Auto Monitor",
        "Track 3: Toggle Auto Monitor",
        "Track 4: Toggle Auto Monitor",
        "Track 5: Toggle Auto Monitor",
        "Track 6: Toggle Auto Monitor",
        "Track 7: Toggle Auto Monitor",
        "Track 8: Toggle Auto Monitor",
        "Track Selected: Toggle Auto Monitor",
        "Track 1: Set Send 1",
        "Track 2: Set Send 1",
        "Track 3: Set Send 1",
        "Track 4: Set Send 1",
        "Track 5: Set Send 1",
        "Track 6: Set Send 1",
        "Track 7: Set Send 1",
        "Track 8: Set Send 1",
        "Track 1: Set Send 2",
        "Track 2: Set Send 2",
        "Track 3: Set Send 2",
        "Track 4: Set Send 2",
        "Track 5: Set Send 2",
        "Track 6: Set Send 2",
        "Track 7: Set Send 2",
        "Track 8: Set Send 2",
        "Track 1: Set Send 3",
        "Track 2: Set Send 3",
        "Track 3: Set Send 3",
        "Track 4: Set Send 3",
        "Track 5: Set Send 3",
        "Track 6: Set Send 3",
        "Track 7: Set Send 3",
        "Track 8: Set Send 3",
        "Track 1: Set Send 4",
        "Track 2: Set Send 4",
        "Track 3: Set Send 4",
        "Track 4: Set Send 4",
        "Track 5: Set Send 4",
        "Track 6: Set Send 4",
        "Track 7: Set Send 4",
        "Track 8: Set Send 4",
        "Track 1: Set Send 5",
        "Track 2: Set Send 5",
        "Track 3: Set Send 5",
        "Track 4: Set Send 5",
        "Track 5: Set Send 5",
        "Track 6: Set Send 5",
        "Track 7: Set Send 5",
        "Track 8: Set Send 5",
        "Track 1: Set Send 6",
        "Track 2: Set Send 6",
        "Track 3: Set Send 6",
        "Track 4: Set Send 6",
        "Track 5: Set Send 6",
        "Track 6: Set Send 6",
        "Track 7: Set Send 6",
        "Track 8: Set Send 6",
        "Track 1: Set Send 7",
        "Track 2: Set Send 7",
        "Track 3: Set Send 7",
        "Track 4: Set Send 7",
        "Track 5: Set Send 7",
        "Track 6: Set Send 7",
        "Track 7: Set Send 7",
        "Track 8: Set Send 7",
        "Track 1: Set Send 8",
        "Track 2: Set Send 8",
        "Track 3: Set Send 8",
        "Track 4: Set Send 8",
        "Track 5: Set Send 8",
        "Track 6: Set Send 8",
        "Track 7: Set Send 8",
        "Track 8: Set Send 8",
        "Track Selected: Set Send 1",
        "Track Selected: Set Send 2",
        "Track Selected: Set Send 3",
        "Track Selected: Set Send 4",
        "Track Selected: Set Send 5",
        "Track Selected: Set Send 6",
        "Track Selected: Set Send 7",
        "Track Selected: Set Send 8",
        "Master: Set Volume",
        "Master: Set Panorama",
        "Master: Toggle Mute",
        "Master: Toggle Solo",
        "Master: Toggle Arm",
        "Device: Toggle Window",
        "Device: Bypass",
        "Device: Expand",
        "Device: Select Previous",
        "Device: Select Next",
        "Device: Select Previous Parameter Bank",
        "Device: Select Next Parameter Bank",
        "Device: Set Parameter 1",
        "Device: Set Parameter 2",
        "Device: Set Parameter 3",
        "Device: Set Parameter 4",
        "Device: Set Parameter 5",
        "Device: Set Parameter 6",
        "Device: Set Parameter 7",
        "Device: Set Parameter 8",
        "Scene 1: Launch Scene",
        "Scene 2: Launch Scene",
        "Scene 3: Launch Scene",
        "Scene 4: Launch Scene",
        "Scene 5: Launch Scene",
        "Scene 6: Launch Scene",
        "Scene 7: Launch Scene",
        "Scene 8: Launch Scene",
        "Scene: Select Previous Bank",
        "Scene: Select Next Bank",
        "Scene: Create Scene from playing Clips",
        "Browser: Browse Presets",
        "Browser: Insert Device before current",
        "Browser: Insert Device after current",
        "Browser: Commit Selection",
        "Browser: Cancel Selection",
        "Browser: Select Previous Filter in Column 1",
        "Browser: Select Previous Filter in Column 2",
        "Browser: Select Previous Filter in Column 3",
        "Browser: Select Previous Filter in Column 4",
        "Browser: Select Previous Filter in Column 5",
        "Browser: Select Previous Filter in Column 6",
        "Browser: Select Next Filter in Column 1",
        "Browser: Select Next Filter in Column 2",
        "Browser: Select Next Filter in Column 3",
        "Browser: Select Next Filter in Column 4",
        "Browser: Select Next Filter in Column 5",
        "Browser: Select Next Filter in Column 6",
        "Browser: Reset Filter Column 1",
        "Browser: Reset Filter Column 2",
        "Browser: Reset Filter Column 3",
        "Browser: Reset Filter Column 4",
        "Browser: Reset Filter Column 5",
        "Browser: Reset Filter Column 6",
        "Browser: Select the previous preset",
        "Browser: Select the next preset",
        "Browser: Select the previous tab",
        "Browser: Select the next tab"
    };

    private static final String [] OPTIONS_KNOBMODE     = new String []
    {
        "Absolute",
        "Relative (1-64 increments, 127-65 decrements)",
        "Relative (65-127 increments, 63-0 decrements)",
        "Relative (1-63 increments, 65-127 decrements)"
    };

    private static final String [] OPTIONS_CC           = new String [128];
    private static final String [] OPTIONS_MIDI_CHANNEL = new String [16];
    static
    {
        for (int i = 0; i < OPTIONS_CC.length; i++)
            OPTIONS_CC[i] = Integer.toString (i);
        for (int i = 0; i < OPTIONS_MIDI_CHANNEL.length; i++)
            OPTIONS_MIDI_CHANNEL[i] = Integer.toString (i + 1);
    }

    private IEnumSetting          addCCSetting;
    private IEnumSetting          addMidiChannelSetting;
    private final IEnumSetting [] functionSetting    = new IEnumSetting [128];
    private final IEnumSetting [] midiChannelSetting = new IEnumSetting [128];
    private final IEnumSetting [] knobModeSetting    = new IEnumSetting [128];

    private int                   addCCValue;
    private int                   addMidiChannel;
    private int []                ccCommand          = new int [128];
    private int []                midiChannel        = new int [128];
    private int []                knobMode           = new int [128];


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public GenericFlexiConfiguration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        String category = "Use a knob/fader/button to set, then click add...";

        this.addCCSetting = settingsUI.getEnumSetting ("CC:", category, OPTIONS_CC, OPTIONS_CC[0]);
        this.addCCSetting.addValueObserver (value -> this.addCCValue = Integer.parseInt (value));

        this.addMidiChannelSetting = settingsUI.getEnumSetting ("Midi channel:", category, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
        this.addMidiChannelSetting.addValueObserver (value -> this.addMidiChannel = Integer.parseInt (value));

        settingsUI.getSignalSetting (" ", category, "Add").addValueObserver ( (Void) -> {
            this.midiChannelSetting[this.addCCValue].set (Integer.toString (this.addMidiChannel));
            this.setCCVisibility (this.addCCValue, true);
        });

        category = "CCs";
        for (int i = 0; i < 128; i++)
        {
            final int index = i;
            this.functionSetting[i] = settingsUI.getEnumSetting ("Function CC " + i + ":", category, FUNCTIONS, FUNCTIONS[0]);
            this.functionSetting[i].addValueObserver (value -> {
                this.setCCVisibility (index, !FUNCTIONS[0].equals (value));
                this.ccCommand[index] = lookupIndex (FUNCTIONS, value);
            });
            this.midiChannelSetting[i] = settingsUI.getEnumSetting ("Midi Channel CC " + i + ":", category, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
            this.midiChannelSetting[i].addValueObserver (value -> this.midiChannel[index] = lookupIndex (OPTIONS_MIDI_CHANNEL, value));
            this.knobModeSetting[i] = settingsUI.getEnumSetting ("Knob Mode CC " + i + ":", category, OPTIONS_KNOBMODE, OPTIONS_KNOBMODE[0]);
            this.knobModeSetting[i].addValueObserver (value -> this.knobMode[index] = lookupIndex (OPTIONS_KNOBMODE, value));
        }
    }


    /**
     * Set a received CC value.
     * 
     * @param channel The midi channel
     * @param cc The CC value
     */
    public void setAddValues (final int channel, final int cc)
    {
        this.addMidiChannelSetting.set (Integer.toString (channel + 1));
        this.addCCSetting.set (Integer.toString (cc));
    }


    private void setCCVisibility (final int cc, final boolean visible)
    {
        this.functionSetting[cc].setVisible (visible);
        this.midiChannelSetting[cc].setVisible (visible);
        this.knobModeSetting[cc].setVisible (visible);
    }


    /**
     * Get the configured command for the given CC.
     * 
     * @param cc The CC for which to get the command
     * @return The configured command
     */
    public int getCcCommand (final int cc)
    {
        return this.ccCommand[cc];
    }


    /**
     * Get the configured midi channel for the given CC.
     * 
     * @param cc The CC for which to get the midi channel
     * @return The configured midi channel
     */
    public int getMidiChannel (final int cc)
    {
        return this.midiChannel[cc];
    }


    /**
     * Get the configured knob mode for the given CC.
     * 
     * @param cc The CC for which to get the knob mode
     * @return The configured knob mode
     */
    public int getKnobMode (final int cc)
    {
        return this.knobMode[cc];
    }
}
