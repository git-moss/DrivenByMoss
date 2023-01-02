// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * The configuration settings for Maschine Jam.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamConfiguration extends AbstractConfiguration
{
    private static final Views [] PREFERRED_NOTE_VIEWS =
    {
        Views.PLAY,
        Views.CHORDS,
        Views.PIANO,
        Views.DRUM64,
        Views.DRUM,
        Views.DRUM4,
        Views.DRUM8,
        Views.SEQUENCER,
        Views.RAINDROPS,
        Views.POLY_SEQUENCER
    };


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public MaschineJamConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
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
        this.activateScaleLayoutSetting (documentSettings);

        ///////////////////////////
        // Note Repeat

        this.activateNoteRepeatSetting (documentSettings);

        ///////////////////////////
        // Transport

        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);
        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Play and Sequence

        this.activateAccentActiveSetting (globalSettings);
        this.activateAccentValueSetting (globalSettings);
        this.activateQuantizeAmountSetting (globalSettings);
        this.activatePreferredNoteViewSetting (globalSettings, PREFERRED_NOTE_VIEWS);
        this.activateStartWithSessionViewSetting (globalSettings);

        ///////////////////////////
        // Drum Sequencer

        if (this.host.supports (Capability.HAS_DRUM_DEVICE))
            this.activateTurnOffEmptyDrumPadsSetting (globalSettings);

        ///////////////////////////
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateFlipSessionSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);
        this.activateKnobSpeedSetting (globalSettings);
        this.activateFootswitchSetting (globalSettings, 0, "Footswitch (Tip)");
        this.activateFootswitchSetting (globalSettings, 1, "Footswitch (Ring)");
    }
}
