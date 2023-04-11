// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics.canvas.component;

import de.mossgrabers.framework.graphics.IGraphicsInfo;


/**
 * A component/element which can be drawn on the canvas. It can draw itself on the canvas.
 *
 * @author Jürgen Moßgraber
 */
public interface IComponent
{
    /**
     * Draw the component.
     *
     * @param info All necessary information to draw the component
     */
    void draw (final IGraphicsInfo info);
}
