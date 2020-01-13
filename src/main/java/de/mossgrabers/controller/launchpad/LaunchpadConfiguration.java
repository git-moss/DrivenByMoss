// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad;

import de.mossgrabers.controller.launchpad.definition.ILaunchpadControllerDefinition;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for Launchpad.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchpadConfiguration extends AbstractConfiguration
{
    private final ILaunchpadControllerDefinition definition;

    private boolean                              isDeleteActive    = false;
    private boolean                              isDuplicateActive = false;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     * @param definition The Launchpad definition
     */
    public LaunchpadConfiguration (final IHost host, final IValueChanger valueChanger, final ArpeggiatorMode [] arpeggiatorModes, final ILaunchpadControllerDefinition definition)
    {
        super (host, valueChanger, arpeggiatorModes);

        this.definition = definition;
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
        this.activateScaleLayoutSetting (documentSettings);

        ///////////////////////////
        // Note Repeat

        this.activateNoteRepeatSetting (documentSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateQuantizeAmountSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateBehaviourOnStopSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateFlipSessionSetting (globalSettings);
        if (this.definition.isPro ())
            this.activateFlipRecordSetting (globalSettings);
        this.activateAutoSelectDrumSetting (globalSettings);
        this.activateTurnOffEmptyDrumPadsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Pad Sensitivity

        this.activateConvertAftertouchSetting (globalSettings);
    }


    /**
     * Returns true if the delete mode is active.
     *
     * @return True if active
     */
    public boolean isDeleteModeActive ()
    {
        return this.isDeleteActive;
    }


    /**
     * Toggle the delete mode.
     */
    public void toggleDeleteModeActive ()
    {
        this.isDeleteActive = !this.isDeleteActive;
        if (this.isDeleteActive)
            this.isDuplicateActive = false;
    }


    /**
     * Returns true if the duplicate mode is active.
     *
     * @return True if active
     */
    public boolean isDuplicateModeActive ()
    {
        return this.isDuplicateActive;
    }


    /**
     * Toggle the duplicate mode.
     */
    public void toggleDuplicateModeActive ()
    {
        this.isDuplicateActive = !this.isDuplicateActive;
        if (this.isDuplicateActive)
            this.isDeleteActive = false;
    }
}
