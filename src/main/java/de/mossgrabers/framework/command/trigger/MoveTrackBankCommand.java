// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to move the window of the track bank by 1 or 8.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MoveTrackBankCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private boolean moveLeft;
    private boolean moveBy1;
    private Integer deviceMode;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param deviceMode The ID of the device mode
     * @param moveBy1 If true the bank window moves by 1 otherwise by 8
     * @param moveLeft If true the bank window is moved left otherwise to the right
     */
    public MoveTrackBankCommand (final IModel model, final S surface, final Integer deviceMode, final boolean moveBy1, final boolean moveLeft)
    {
        super (model, surface);
        this.deviceMode = deviceMode;
        this.moveBy1 = moveBy1;
        this.moveLeft = moveLeft;
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (this.surface.getModeManager ().isActiveOrTempMode (this.deviceMode))
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
                this.model.getCurrentTrackBank ().scrollBackwards ();
            else
                this.model.getCurrentTrackBank ().scrollForwards ();
        }
        else
        {
            if (this.moveLeft)
                this.model.getCurrentTrackBank ().scrollPageBackwards ();
            else
                this.model.getCurrentTrackBank ().scrollPageForwards ();
        }
    }
}
