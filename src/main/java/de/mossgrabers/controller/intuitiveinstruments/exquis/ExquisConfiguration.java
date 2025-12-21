// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.intuitiveinstruments.exquis;

import java.util.List;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;


/**
 * The configuration settings for the Intuitive Instruments Exquis controller.
 *
 * @author Jürgen Moßgraber
 */
public class ExquisConfiguration extends AbstractConfiguration
{
    /** The scales available on the Exquis. */
    public static final String [] EXQUISE_SCALES = new String []
    {
        "Major",
        "Natural Minor",
        "Melodic Minor",
        "Harmonic Minor",
        "Dorian",
        "Phrygian",
        "Lydian",
        "Mixolydian",
        "Locrian",
        "Phrygian dominant",
        "Major Pentatonic",
        "Minor Pentatonic",
        "Whole Tone",
        "Chromatic"
    };


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public ExquisConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Document settings

        this.activateMPEBendRange (documentSettings, CATEGORY_PADS);
        this.activateScaleSetting (documentSettings, EXQUISE_SCALES, EXQUISE_SCALES[0]);
        this.activateScaleBaseSetting (documentSettings);
        this.activateNoteRepeatSetting (documentSettings);

        ///////////////////////////
        // Session

        this.activateSelectClipOnLaunchSetting (globalSettings);
        this.activateDrawRecordStripeSetting (globalSettings);
        this.activateActionForRecArmedPad (globalSettings);

        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Hardware

        this.activateKnobSpeedSetting (globalSettings, 100, 20, true);
    }
}
