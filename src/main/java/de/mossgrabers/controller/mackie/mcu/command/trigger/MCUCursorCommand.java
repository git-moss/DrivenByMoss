// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.command.trigger;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command for cursor arrow keys.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MCUCursorCommand extends AbstractTriggerCommand<MCUControlSurface, MCUConfiguration>
{
    protected Direction        direction;

    private final ModeSwitcher switcher;


    /**
     * Constructor.
     *
     * @param direction The direction of the pushed cursor arrow
     * @param model The model
     * @param surface The surface
     */
    public MCUCursorCommand (final Direction direction, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);
        this.direction = direction;
        this.switcher = new ModeSwitcher (surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.UP)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (browser.isActive ())
        {
            switch (this.direction)
            {
                case LEFT:
                    browser.insertBeforeCursorDevice ();
                    break;
                case RIGHT:
                    browser.insertAfterCursorDevice ();
                    break;
                case UP:
                    browser.nextContentType ();
                    break;
                case DOWN:
                    browser.previousContentType ();
                    break;
            }
            return;
        }

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
            this.model.getApplication ().zoomOut ();
        else
            this.model.getApplication ().arrowKeyLeft ();
    }


    private void scrollRight ()
    {
        if (this.surface.getConfiguration ().isZoomState ())
            this.model.getApplication ().zoomIn ();
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
                this.model.getApplication ().decTrackHeight ();
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
                this.model.getApplication ().incTrackHeight ();
        }
        else
            this.model.getApplication ().arrowKeyDown ();
    }
}
