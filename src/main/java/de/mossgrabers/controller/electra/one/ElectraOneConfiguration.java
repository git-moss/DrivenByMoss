// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for Electra.One.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ElectraOneConfiguration extends AbstractConfiguration
{
    // TODO
    // /** Zoom state. */
    // public static final Integer ZOOM_STATE = Integer.valueOf (50);
    //
    // private static final String CATEGORY_EXTENDER_SETUP = "Extender Setup (requires restart)";
    //
    // private IEnumSetting zoomStateSetting;
    //
    // private boolean zoomState;

    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public ElectraOneConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Hardware

        this.activateHardwareSettings (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnStopSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
    }


    private void activateHardwareSettings (final ISettingsUI settingsUI)
    {
        // TODO
    }
}
