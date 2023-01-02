// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Base command which allow to double click a button as a third function.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractDoubleTriggerCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private boolean restartFlag = false;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    protected AbstractDoubleTriggerCommand (final IModel model, final S surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || this.handleButtonCombinations ())
            return;

        if (this.restartFlag)
        {
            this.executeDoubleClick ();
            this.restartFlag = false;
            return;
        }

        this.executeSingleClick ();
    }


    /**
     * Overwrite for additional button combinations.
     *
     * @return Return true if button combination was detected and handled otherwise false
     */
    protected boolean handleButtonCombinations ()
    {
        return false;
    }


    /**
     * Implement the method to execute when button was single clicked. Call
     * {@link #doubleClickTest()} if double click action should still be monitored.
     */
    protected abstract void executeSingleClick ();


    /**
     * Implement the method to execute when button was double clicked. Needs to undo the single
     * click method.
     */
    protected abstract void executeDoubleClick ();


    /**
     * Detecting a double click.
     */
    protected void doubleClickTest ()
    {
        this.restartFlag = true;
        this.surface.scheduleTask ( () -> this.restartFlag = false, 250);
    }
}
