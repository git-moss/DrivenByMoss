// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.command.trigger;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ZoomAndKeysCursorCommand extends AbstractTriggerCommand<HUIControlSurface, HUIConfiguration>
{
    protected Direction direction;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public ZoomAndKeysCursorCommand (final Direction direction, final IModel model, final HUIControlSurface surface)
    {
        super (model, surface);
        this.direction = direction;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (this.direction)
        {
            case LEFT:
                this.scrollLeft ();
                break;
            case RIGHT:
                this.scrollRight ();
                break;
            case UP:
                this.scrollUp ();
                break;
            case DOWN:
                this.scrollDown ();
                break;
        }
    }


    protected void scrollLeft ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().zoomOut ();
        else
            this.model.getApplication ().arrowKeyLeft ();
    }


    protected void scrollRight ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().zoomIn ();
        else
            this.model.getApplication ().arrowKeyRight ();
    }


    protected void scrollUp ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().decTrackHeight ();
        else
            this.model.getApplication ().arrowKeyUp ();
    }


    protected void scrollDown ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().incTrackHeight ();
        else
            this.model.getApplication ().arrowKeyDown ();
    }
}
