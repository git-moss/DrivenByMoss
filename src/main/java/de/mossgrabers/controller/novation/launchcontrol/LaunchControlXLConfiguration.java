// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for LaunchControl XL.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchControlXLConfiguration extends AbstractConfiguration
{
    /** Active template. */
    public static final Integer ACTIVE_TEMPLATE = Integer.valueOf (50);

    private int                 templateID      = -1;
    private boolean             isDeviceActive  = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public LaunchControlXLConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Scale

        this.activateScaleSetting (documentSettings);
        this.activateScaleBaseSetting (documentSettings);
        this.activateScaleInScaleSetting (documentSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Play & Sequence

        this.activateMidiEditChannelSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
    }


    /**
     * Set the active template on the device (0-15).
     *
     * @param templateID The ID to set
     */
    public void setTemplate (final int templateID)
    {
        this.templateID = templateID;

        this.notifyObservers (ACTIVE_TEMPLATE);
    }


    /**
     * Get the active template on the device (0-15).
     *
     * @return The ID to set
     */
    public int getTemplate ()
    {
        return this.templateID;
    }


    /**
     * Are device parameters active?
     *
     * @return True if active otherwise panorama is active
     */
    public boolean isDeviceActive ()
    {
        return this.isDeviceActive;
    }


    /**
     * Toggle between panorama and device parameters control on 3rd row.
     */
    public void toggleDeviceActive ()
    {
        this.isDeviceActive = !this.isDeviceActive;
    }
}
