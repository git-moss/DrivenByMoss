// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.midimonitor;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.IEnumSetting;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;


/**
 * The configuration settings for the MIDI Monitor implementation.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiMonitorConfiguration extends AbstractConfiguration
{
    /** Setting for filtering system real-time. */
    public static final Integer FILTER_SYSTEM_REALTIME   = Integer.valueOf (50);

    private boolean             isFilterSystemRealtimeOn = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     */
    public MidiMonitorConfiguration (final IHost host, final IValueChanger valueChanger)
    {
        super (host, valueChanger, null);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        final IEnumSetting filterSystemRealtimeSetting = globalSettings.getEnumSetting ("System Realtime", "Filter", ON_OFF_OPTIONS, ON_OFF_OPTIONS[0]);
        filterSystemRealtimeSetting.addValueObserver (value -> {
            this.isFilterSystemRealtimeOn = "On".equals (value);
            this.notifyObservers (FILTER_SYSTEM_REALTIME);
        });
        this.isSettingActive.add (FILTER_SYSTEM_REALTIME);
    }


    /**
     * Is the filter for system real-time events enabled?
     *
     * @return True if the filter for system real-time events is enabled
     */
    public boolean isFilterSystemRealtimeEnabled ()
    {
        return this.isFilterSystemRealtimeOn;
    }
}
