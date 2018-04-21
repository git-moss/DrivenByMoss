// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
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
    private FlexiCommand []       ccCommand          = new FlexiCommand [128];
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
            final String [] names = FlexiCommand.getNames ();
            this.functionSetting[i] = settingsUI.getEnumSetting ("Function CC " + i + ":", category, names, names[0]);
            this.functionSetting[i].addValueObserver (value -> {
                this.setCCVisibility (index, !names[0].equals (value));
                this.ccCommand[index] = FlexiCommand.lookupByName (value);
                if (this.ccCommand[index].isTrigger () && this.knobMode[index] > 0)
                {
                    this.knobMode[index] = 0;
                    this.knobModeSetting[index].set (OPTIONS_KNOBMODE[0]);
                }
            });
            this.midiChannelSetting[i] = settingsUI.getEnumSetting ("Midi Channel CC " + i + ":", category, OPTIONS_MIDI_CHANNEL, OPTIONS_MIDI_CHANNEL[0]);
            this.midiChannelSetting[i].addValueObserver (value -> this.midiChannel[index] = lookupIndex (OPTIONS_MIDI_CHANNEL, value));
            this.knobModeSetting[i] = settingsUI.getEnumSetting ("Knob Mode CC " + i + ":", category, OPTIONS_KNOBMODE, OPTIONS_KNOBMODE[0]);
            this.knobModeSetting[i].addValueObserver (value -> {
                final int km = lookupIndex (OPTIONS_KNOBMODE, value);
                this.knobMode[index] = km;
                if (this.ccCommand[index].isTrigger () && km > 0)
                {
                    this.knobMode[index] = 0;
                    this.knobModeSetting[index].set (OPTIONS_KNOBMODE[0]);
                }
            });
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
    public FlexiCommand getCcCommand (final int cc)
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
