// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    /** The direction of the cursor. */
    public enum Direction
    {
        /** Move left. */
        LEFT,
        /** Move right. */
        RIGHT,
        /** Move up. */
        UP,
        /** Move down. */
        DOWN
    }

    protected Direction        direction;

    private final ModeSwitcher switcher;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public CursorCommand (final Direction direction, final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.direction = direction;
        this.switcher = new ModeSwitcher (surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
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


    private void scrollLeft ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().invokeAction ("Zoom Out");
        else
            this.model.getApplication ().arrowKeyLeft ();
    }


    private void scrollRight ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().invokeAction ("Zoom In");
        else
            this.model.getApplication ().arrowKeyRight ();
    }


    private void scrollUp ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
        {
            if (this.surface.getConfiguration ().useVertZoomForModes ())
                this.switcher.scrollUp ();
            else
                this.model.getApplication ().invokeAction ("toggle_double_or_single_row_track_height");
        }
        else
            this.model.getApplication ().arrowKeyUp ();
    }


    private void scrollDown ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
        {
            if (this.surface.getConfiguration ().useVertZoomForModes ())
                this.switcher.scrollDown ();
            else
                this.model.getApplication ().invokeAction ("toggle_double_or_single_row_track_height");
        }
        else
            this.model.getApplication ().arrowKeyDown ();
    }
}
