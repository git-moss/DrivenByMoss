// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.definition;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.controller.akai.apcmini.controller.APCminiButton;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiMk2ColorManager;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the Akai APCmini Mk2 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiMk2ControllerDefinition extends AbstractAPCminiDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("3B953D86-4A53-43B1-BB2C-65A33DC778F1");


    /**
     * Constructor.
     */
    public APCminiMk2ControllerDefinition ()
    {
        super (EXTENSION_ID, "APCmini Mk2");

        // MIDI Notes
        this.buttonIDs.put (APCminiButton.TRACK1, Integer.valueOf (0x64));
        this.buttonIDs.put (APCminiButton.TRACK2, Integer.valueOf (0x65));
        this.buttonIDs.put (APCminiButton.TRACK3, Integer.valueOf (0x66));
        this.buttonIDs.put (APCminiButton.TRACK4, Integer.valueOf (0x67));
        this.buttonIDs.put (APCminiButton.TRACK5, Integer.valueOf (0x68));
        this.buttonIDs.put (APCminiButton.TRACK6, Integer.valueOf (0x69));
        this.buttonIDs.put (APCminiButton.TRACK7, Integer.valueOf (0x6A));
        this.buttonIDs.put (APCminiButton.TRACK8, Integer.valueOf (0x6B));

        this.buttonIDs.put (APCminiButton.SCENE1, Integer.valueOf (0x70));
        this.buttonIDs.put (APCminiButton.SCENE2, Integer.valueOf (0x71));
        this.buttonIDs.put (APCminiButton.SCENE3, Integer.valueOf (0x72));
        this.buttonIDs.put (APCminiButton.SCENE4, Integer.valueOf (0x73));
        this.buttonIDs.put (APCminiButton.SCENE5, Integer.valueOf (0x74));
        this.buttonIDs.put (APCminiButton.SCENE6, Integer.valueOf (0x75));
        this.buttonIDs.put (APCminiButton.SCENE7, Integer.valueOf (0x76));
        this.buttonIDs.put (APCminiButton.SCENE8, Integer.valueOf (0x77));

        this.buttonIDs.put (APCminiButton.SHIFT, Integer.valueOf (0x7A));
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.createDeviceDiscoveryPairs ("APC mini mk2");
    }


    /** {@inheritDoc} */
    @Override
    public int swapShiftedTrackIndices (final int trackIndex)
    {
        // Switch cursor buttons and fader mode buttons
        return trackIndex < 4 ? trackIndex + 4 : trackIndex - 4;
    }


    /** {@inheritDoc} */
    @Override
    public int swapShiftedSceneIndices (final int sceneIndex)
    {
        // Switch mute and record arm
        if (sceneIndex == 2)
            return 3;
        if (sceneIndex == 3)
            return 2;
        return sceneIndex;
    }


    /** {@inheritDoc} */
    @Override
    public ColorManager getColorManager ()
    {
        return new APCminiMk2ColorManager ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasBrightness ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRGBColors ()
    {
        return true;
    }
}
