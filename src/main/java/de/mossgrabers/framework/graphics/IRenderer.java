// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

/**
 * An interface to a renderer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IRenderer
{
    /**
     * Render into a graphics context.
     *
     * @param gc The graphics context
     */
    void render (IGraphicsContext gc);
}
