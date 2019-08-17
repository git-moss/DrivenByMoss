// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.utils;

import de.mossgrabers.framework.graphics.display.ModelInfo;


/**
 * Callback interface for when the display grid has changed.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@FunctionalInterface
public interface GridChangeListener
{
    /**
     * The display grid has changed.
     * 
     * @param info The model info
     */
    void render (final ModelInfo info);
}
