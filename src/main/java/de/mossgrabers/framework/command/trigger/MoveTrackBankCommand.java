// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.mcu.mode.Modes;


/**
 * Command to move the window of the track bank by 1 or 8.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MoveTrackBankCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private boolean moveLeft;
    private boolean moveBy1;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param moveBy1 If true the bank window moves by 1 otherwise by 8
     * @param moveLeft If true the bank window is moved left otherwise to the right
     */
    public MoveTrackBankCommand (final Model model, final S surface, final boolean moveBy1, final boolean moveLeft)
    {
        super (model, surface);
        this.moveBy1 = moveBy1;
        this.moveLeft = moveLeft;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.getModeManager ().isActiveMode (Modes.MODE_DEVICE_PARAMS))
        {
            if (this.moveBy1)
            {
                if (this.moveLeft)
                    this.model.getCursorDevice ().previousParameterPage ();
                else
                    this.model.getCursorDevice ().nextParameterPage ();
            }
            else
            {
                if (this.moveLeft)
                    this.model.getCursorDevice ().selectPrevious ();
                else
                    this.model.getCursorDevice ().selectNext ();
            }
            return;
        }

        if (this.moveBy1)
        {
            if (this.moveLeft)
                this.model.getCurrentTrackBank ().scrollTracksUp ();
            else
                this.model.getCurrentTrackBank ().scrollTracksDown ();
        }
        else
        {
            if (this.moveLeft)
                this.model.getCurrentTrackBank ().scrollTracksPageUp ();
            else
                this.model.getCurrentTrackBank ().scrollTracksPageDown ();
        }
    }
}
