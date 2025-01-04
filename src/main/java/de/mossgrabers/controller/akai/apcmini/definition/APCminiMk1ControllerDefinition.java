// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.definition;

import java.util.List;
import java.util.UUID;

import de.mossgrabers.controller.akai.apcmini.controller.APCminiButton;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiMk1ColorManager;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.utils.OperatingSystem;
import de.mossgrabers.framework.utils.Pair;


/**
 * Definition class for the Akai APCmini Mk1 controller extension.
 *
 * @author Jürgen Moßgraber
 */
public class APCminiMk1ControllerDefinition extends AbstractAPCminiDefinition
{
    private static final UUID EXTENSION_ID = UUID.fromString ("E7E02A80-3657-11E4-8C21-0800200C9A66");


    /**
     * Constructor.
     */
    public APCminiMk1ControllerDefinition ()
    {
        super (EXTENSION_ID, "APCmini Mk1");

        // MIDI Notes
        this.buttonIDs.put (APCminiButton.TRACK1, Integer.valueOf (64));
        this.buttonIDs.put (APCminiButton.TRACK2, Integer.valueOf (65));
        this.buttonIDs.put (APCminiButton.TRACK3, Integer.valueOf (66));
        this.buttonIDs.put (APCminiButton.TRACK4, Integer.valueOf (67));
        this.buttonIDs.put (APCminiButton.TRACK5, Integer.valueOf (68));
        this.buttonIDs.put (APCminiButton.TRACK6, Integer.valueOf (69));
        this.buttonIDs.put (APCminiButton.TRACK7, Integer.valueOf (70));
        this.buttonIDs.put (APCminiButton.TRACK8, Integer.valueOf (71));

        this.buttonIDs.put (APCminiButton.SCENE1, Integer.valueOf (82));
        this.buttonIDs.put (APCminiButton.SCENE2, Integer.valueOf (83));
        this.buttonIDs.put (APCminiButton.SCENE3, Integer.valueOf (84));
        this.buttonIDs.put (APCminiButton.SCENE4, Integer.valueOf (85));
        this.buttonIDs.put (APCminiButton.SCENE5, Integer.valueOf (86));
        this.buttonIDs.put (APCminiButton.SCENE6, Integer.valueOf (87));
        this.buttonIDs.put (APCminiButton.SCENE7, Integer.valueOf (88));
        this.buttonIDs.put (APCminiButton.SCENE8, Integer.valueOf (89));

        this.buttonIDs.put (APCminiButton.SHIFT, Integer.valueOf (98));
    }


    /** {@inheritDoc} */
    @Override
    public List<Pair<String [], String []>> getMidiDiscoveryPairs (final OperatingSystem os)
    {
        return this.createDeviceDiscoveryPairs ("APC MINI");
    }


    /** {@inheritDoc} */
    @Override
    public int swapShiftedTrackIndices (final int trackIndex)
    {
        return trackIndex;
    }


    /** {@inheritDoc} */
    @Override
    public int swapShiftedSceneIndices (final int sceneIndex)
    {
        return sceneIndex;
    }


    /** {@inheritDoc} */
    @Override
    public ColorManager getColorManager ()
    {
        return new APCminiMk1ColorManager ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasBrightness ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRGBColors ()
    {
        return false;
    }
}
