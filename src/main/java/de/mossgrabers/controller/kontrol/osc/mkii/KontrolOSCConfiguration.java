// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.osc.mkii;

import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.osc.AbstractOpenSoundControlConfiguration;


/**
 * The configuration settings for OSC.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class KontrolOSCConfiguration extends AbstractOpenSoundControlConfiguration
{
    /** Record in arranger. */
    public static final int        RECORD_ARRANGER                = 0;
    /** Record in clip. */
    public static final int        RECORD_CLIP                    = 1;
    /** Create a new clip, enable overdub and start playback. */
    public static final int        NEW_CLIP                       = 2;
    /** Toggle arranger overdub. */
    public static final int        TOGGLE_ARRANGER_OVERDUB        = 3;
    /** Toggle clip overdub. */
    public static final int        TOGGLE_CLIP_OVERDUB            = 4;

    private static final int       DEFAULT_SEND_PORT              = KontrolOSCControllerSetup.IS_16 ? 7577 : 7575;
    private static final int       DEFAULT_RECEIVE_PORT           = KontrolOSCControllerSetup.IS_16 ? 7578 : 7576;

    private static final String [] RECORD_OPTIONS                 = new String []
    {
        "Record arranger",
        "Record clip",
        "New clip",
        "Toggle arranger overdub",
        "Toggle clip overdub",
    };

    private static final Integer   RECORD_BUTTON_FUNCTION         = Integer.valueOf (50);
    private static final Integer   SHIFTED_RECORD_BUTTON_FUNCTION = Integer.valueOf (51);

    private int                    receivePort                    = DEFAULT_RECEIVE_PORT;
    private String                 sendHost                       = DEFAULT_SERVER;
    private int                    sendPort                       = DEFAULT_SEND_PORT;

    private int                    recordButtonFunction           = 0;
    private int                    shiftedRecordButtonFunction    = 1;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     */
    public KontrolOSCConfiguration (final IValueChanger valueChanger)
    {
        super (valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI settingsUI)
    {
        final IEnumSetting recordButtonSetting = settingsUI.getEnumSetting ("Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[1]);
        recordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.recordButtonFunction = i;
            }
            this.notifyObservers (RECORD_BUTTON_FUNCTION);
        });

        final IEnumSetting shiftedRecordButtonSetting = settingsUI.getEnumSetting ("Shift + Record button", CATEGORY_TRANSPORT, RECORD_OPTIONS, RECORD_OPTIONS[0]);
        shiftedRecordButtonSetting.addValueObserver (value -> {
            for (int i = 0; i < RECORD_OPTIONS.length; i++)
            {
                if (RECORD_OPTIONS[i].equals (value))
                    this.shiftedRecordButtonFunction = i;
            }
            this.notifyObservers (SHIFTED_RECORD_BUTTON_FUNCTION);
        });

        ///////////////////////////
        // Debug

        this.activateOSCLogging (settingsUI);
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
     * Get the selected function for the record button.
     *
     * @return The function index
     */
    public int getRecordButtonFunction ()
    {
        return this.recordButtonFunction;
    }


    /**
     * Get the selected function for the shifted record button.
     *
     * @return The function index
     */
    public int getShiftedRecordButtonFunction ()
    {
        return this.shiftedRecordButtonFunction;
    }
}
