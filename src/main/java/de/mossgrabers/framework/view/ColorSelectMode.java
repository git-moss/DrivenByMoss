// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

/**
 * Helper enumeration for the Color view. Where should the selected color applied to?
 *
 * @author Jürgen Moßgraber
 */
public enum ColorSelectMode
{
    /** Select a track color. */
    MODE_TRACK,
    /** Select a layer color. */
    MODE_LAYER,
    /** Select a clip color. */
    MODE_CLIP
}