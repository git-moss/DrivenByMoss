// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.definition;

import de.mossgrabers.controller.akai.apcmini.controller.APCminiButton;
import de.mossgrabers.framework.controller.color.ColorManager;


/**
 * Additional configuration options for the different APCmini models.
 *
 * @author Jürgen Moßgraber
 */
public interface IAPCminiControllerDefinition
{

    /**
     * Get the ID for the given button.
     *
     * @param button The button
     * @return The ID
     */
    int getButtonID (APCminiButton button);


    /**
     * Re-order indexes in shifted track button mode.
     *
     * @param trackIndex The index of the track button
     * @return The modified button index
     */
    int swapShiftedTrackIndices (int trackIndex);


    /**
     * Re-order indexes in shifted scene button mode.
     *
     * @param sceneIndex The index of the scene button
     * @return The modified button index
     */
    int swapShiftedSceneIndices (int sceneIndex);


    /**
     * Get the color manger.
     * 
     * @return The color manager
     */
    ColorManager getColorManager ();


    /**
     * Returns true if it supports pad brightness.
     * 
     * @return True if it supports pad brightness
     */
    boolean hasBrightness ();


    /**
     * Returns true if it supports RGB pads.
     * 
     * @return True if it supports RGB pads
     */
    boolean hasRGBColors ();
}
