// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.trigger.AbstractDoubleTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Command to handle the play button.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class PlayCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractDoubleTriggerCommand<S, C>
{
    protected final ButtonID   selectButtonID;
    protected final ITransport transport;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PlayCommand (final IModel model, final S surface)
    {
        this (model, surface, ButtonID.SELECT);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param selectButtonID The buttonID to use for the select button which triggers additional
     *            button combinations
     */
    public PlayCommand (final IModel model, final S surface, final ButtonID selectButtonID)
    {
        super (model, surface);

        this.selectButtonID = selectButtonID;
        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    protected void executeSingleClick ()
    {
        // Handle the different options when the playback is stopped
        switch (this.surface.getConfiguration ().getBehaviourOnPause ())
        {
            case RETURN_TO_ZERO:
                if (this.transport.isPlaying ())
                    this.transport.stopAndRewind ();
                else
                    this.transport.play ();
                break;

            case STOP:
                if (this.transport.isPlaying ())
                    this.transport.stop ();
                else
                    this.transport.play ();
                this.doubleClickTest ();
                break;

            case PAUSE:
                this.transport.play ();
                this.doubleClickTest ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void executeDoubleClick ()
    {
        this.transport.stopAndRewind ();
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations ()
    {
        if (this.surface.isPressed (this.selectButtonID))
        {
            this.transport.togglePunchIn ();
            return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (this.surface.isPressed (this.selectButtonID))
            this.transport.togglePunchOut ();
        else
            this.executeShifted ();
    }


    /**
     * Overwrite to change the execute shifted action.
     */
    protected void executeShifted ()
    {
        this.transport.toggleLoop ();
    }
}
