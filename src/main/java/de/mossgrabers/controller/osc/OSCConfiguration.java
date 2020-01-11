// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlConfiguration;


/**
 * The configuration settings for the OSC implementation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OSCConfiguration extends AbstractOpenSoundControlConfiguration
{
    private static final String CATEGORY_SETUP   = "Setup";

    /** ID for receive port setting. */
    public static final Integer RECEIVE_PORT     = Integer.valueOf (50);
    /** ID for send host setting. */
    public static final Integer SEND_HOST        = Integer.valueOf (51);
    /** ID for send port setting. */
    public static final Integer SEND_PORT        = Integer.valueOf (52);
    /** ID for value resolution setting. */
    public static final Integer VALUE_RESOLUTION = Integer.valueOf (53);


    /** The resolution for values. */
    public enum ValueResolution
    {
        /** Low resolution, 128 values. */
        LOW,
        /** Medium resolution, 1024 values. */
        MEDIUM,
        /** High resolution, 16384 values. */
        HIGH
    }


    private static final String [] VALUE_RESOLUTION_OPTIONS =
    {
        "Low (128)",
        "Medium (1024)",
        "High (16384)"
    };

    private int                    receivePort              = 8000;
    private String                 sendHost                 = DEFAULT_SERVER;
    private int                    sendPort                 = 9000;
    private ValueResolution        valueResolution          = ValueResolution.LOW;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public OSCConfiguration (final IHost host, final IValueChanger valueChanger, final ArpeggiatorMode [] arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Network

        final IIntegerSetting receivePortSetting = globalSettings.getRangeSetting ("Port to receive on", CATEGORY_SETUP, 0, 65535, 1, "", 8000);
        receivePortSetting.addValueObserver (value -> {
            this.receivePort = value.intValue ();
            this.notifyObservers (OSCConfiguration.RECEIVE_PORT);
        });

        final IStringSetting sendHostSetting = globalSettings.getStringSetting ("Host to send to", CATEGORY_SETUP, 15, DEFAULT_SERVER);
        sendHostSetting.addValueObserver (value -> {
            this.sendHost = value;
            this.notifyObservers (OSCConfiguration.SEND_HOST);
        });

        final IIntegerSetting sendPortSetting = globalSettings.getRangeSetting ("Port to send to", CATEGORY_SETUP, 0, 65535, 1, "", 9000);
        sendPortSetting.addValueObserver (value -> {
            this.sendPort = value.intValue ();
            this.notifyObservers (SEND_PORT);
        });

        final IEnumSetting valueResolutionSetting = globalSettings.getEnumSetting ("Value resolution", CATEGORY_SETUP, VALUE_RESOLUTION_OPTIONS, VALUE_RESOLUTION_OPTIONS[0]);
        valueResolutionSetting.addValueObserver (value -> {
            if (VALUE_RESOLUTION_OPTIONS[0].equals (value))
                this.valueResolution = ValueResolution.LOW;
            else if (VALUE_RESOLUTION_OPTIONS[1].equals (value))
                this.valueResolution = ValueResolution.MEDIUM;
            else if (VALUE_RESOLUTION_OPTIONS[2].equals (value))
                this.valueResolution = ValueResolution.HIGH;

            this.notifyObservers (VALUE_RESOLUTION);
        });

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);

        ///////////////////////////
        // Accent

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateEnableVUMetersSetting (globalSettings);

        ///////////////////////////
        // Debug

        this.activateOSCLogging (globalSettings);
    }


    /**
     * Get the port of the host on which the extension receives OSC messages.
     *
     * @return The port
     */
    public int getReceivePort ()
    {
        return this.receivePort;
    }


    /**
     * Get the host on which the extension sends OSC messages.
     *
     * @return The receive host
     */
    public String getSendHost ()
    {
        return this.sendHost;
    }


    /**
     * Get the port of the host on which the extension sends OSC messages.
     *
     * @return The port
     */
    public int getSendPort ()
    {
        return this.sendPort;
    }


    /**
     * Get the selected value resolution.
     *
     * @return The value resolution
     */
    public ValueResolution getValueResolution ()
    {
        return this.valueResolution;
    }
}
