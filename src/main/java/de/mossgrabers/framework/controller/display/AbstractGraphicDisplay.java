// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.controller.display;

import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.canvas.utils.GridChangeListener;
import de.mossgrabers.framework.graphics.display.DisplayCanvas;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.graphics.display.ModelInfo;


/**
 * A display which uses graphics rather than fixed characters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractGraphicDisplay implements IGraphicsDisplay, GridChangeListener
{
    protected final IHost        host;
    protected final DisplayModel model = new DisplayModel ();
    protected DisplayCanvas      canvas;


    /**
     * Constructor.
     *
     * @param host The host
     */
    public AbstractGraphicDisplay (final IHost host)
    {
        this.host = host;

        this.model.addGridElementChangeListener (this);
    }


    /** {@inheritDoc} */
    @Override
    public DisplayModel getModel ()
    {
        return this.model;
    }


    /** {@inheritDoc} */
    @Override
    public void showDebugWindow ()
    {
        if (this.canvas != null)
            this.canvas.getImage ().showDisplayWindow ();
    }


    /** {@inheritDoc} */
    @Override
    public void render (final ModelInfo info)
    {
        if (this.canvas != null)
            this.send (this.canvas.getImage ());
    }
}
