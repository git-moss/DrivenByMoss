// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc;

import de.mossgrabers.framework.configuration.IActionSetting;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.IIntegerSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IStringSetting;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlConfiguration;

import java.util.Arrays;
import java.util.List;


/**
 * The configuration settings for the OSC implementation.
 *
 * @author Jürgen Moßgraber
 */
public class OSCConfiguration extends AbstractOpenSoundControlConfiguration
{
    private static final String CATEGORY_SETUP   = "Setup";

    /** ID for receive port setting. */
    public static final Integer RECEIVE_PORT     = Integer.valueOf (50);
    /** ID for value resolution setting. */
    public static final Integer VALUE_RESOLUTION = Integer.valueOf (51);


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


    private static final String    CATEGORY_PROTOCOL         = "Protocol (must match your client template!)";

    private static final String [] VALUE_RESOLUTION_OPTIONS  =
    {
        "Low (128)",
        "Medium (1024)",
        "High (16384)"
    };

    private int                    receivePort               = 8000;
    private String                 sendHost                  = DEFAULT_SERVER;
    private int                    sendPort                  = 9000;
    private ValueResolution        valueResolution           = ValueResolution.LOW;
    private int                    bankPageSize              = 8;
    private final String []        assignableFunctionActions = new String [8];


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public OSCConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);

        Arrays.fill (this.assignableFunctionActions, "");
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Network

        final IIntegerSetting receivePortSetting = globalSettings.getRangeSetting ("Port to receive on", CATEGORY_SETUP, 1024, 65535, 1, "", 8000);
        receivePortSetting.addValueObserver (value -> {
            this.receivePort = value.intValue ();
            this.notifyObservers (RECEIVE_PORT);
        });
        this.isSettingActive.add (RECEIVE_PORT);

        final IStringSetting sendHostSetting = globalSettings.getStringSetting ("Host to send to (requires restart)", CATEGORY_SETUP, 15, DEFAULT_SERVER);
        this.sendHost = sendHostSetting.get ();

        final IIntegerSetting sendPortSetting = globalSettings.getRangeSetting ("Port to send to (requires restart)", CATEGORY_SETUP, 1024, 65535, 1, "", 9000);
        this.sendPort = sendPortSetting.get ().intValue ();

        ///////////////////////////
        // Protocol

        final IEnumSetting valueResolutionSetting = globalSettings.getEnumSetting ("Value resolution", CATEGORY_PROTOCOL, VALUE_RESOLUTION_OPTIONS, VALUE_RESOLUTION_OPTIONS[0]);
        valueResolutionSetting.addValueObserver (value -> {
            if (VALUE_RESOLUTION_OPTIONS[0].equals (value))
                this.valueResolution = ValueResolution.LOW;
            else if (VALUE_RESOLUTION_OPTIONS[1].equals (value))
                this.valueResolution = ValueResolution.MEDIUM;
            else if (VALUE_RESOLUTION_OPTIONS[2].equals (value))
                this.valueResolution = ValueResolution.HIGH;

            this.notifyObservers (VALUE_RESOLUTION);
        });
        this.isSettingActive.add (VALUE_RESOLUTION);

        final String [] pageSize = new String [200];
        for (int i = 0; i < pageSize.length; i++)
            pageSize[i] = Integer.toString (i + 1);
        final IEnumSetting bankPageSizeSetting = globalSettings.getEnumSetting ("Bank Page Size (requires restart)", CATEGORY_PROTOCOL, pageSize, pageSize[7]);
        this.bankPageSize = Integer.parseInt (bankPageSizeSetting.get ());

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateEnableVUMetersSetting (globalSettings);

        ///////////////////////////
        // Actions

        for (int i = 0; i < this.assignableFunctionActions.length; i++)
        {
            final int pos = i;
            final IActionSetting actionSetting = globalSettings.getActionSetting ("Action " + (i + 1), "Actions");
            actionSetting.addValueObserver (value -> this.assignableFunctionActions[pos] = actionSetting.get ());
        }

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


    /**
     * Get the bank page size.
     *
     * @return The bank page size
     */
    public int getBankPageSize ()
    {
        return this.bankPageSize;
    }


    /**
     * If the assignable function is set to Action this method gets the selected action to execute.
     *
     * @param index The index of the assignable
     * @return The ID of the action to execute
     */
    public String getAssignableAction (final int index)
    {
        return this.assignableFunctionActions[index];
    }
}
