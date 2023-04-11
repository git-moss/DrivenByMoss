// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.view;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Provides an indexed access to commands of the transport control.
 *
 * @author Jürgen Moßgraber
 */
public class TransportControl
{
    private final SLControlSurface                               surface;
    private final IModel                                         model;
    private boolean                                              isRewinding;
    private boolean                                              isForwarding;
    private final PlayCommand<SLControlSurface, SLConfiguration> playCommand;
    private final StopCommand<SLControlSurface, SLConfiguration> stopCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TransportControl (final SLControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
        this.playCommand = new PlayCommand<> (model, surface);
        this.stopCommand = new StopCommand<> (model, surface);
    }


    /**
     * Execute a transport command.
     *
     * @param index The index of the command
     * @param event The button event
     */
    public void execute (final int index, final ButtonEvent event)
    {
        switch (index)
        {
            case 0:
                this.onRewind (event);
                break;

            case 1:
                this.onForward (event);
                break;

            case 2:
                this.onStop (event);
                break;

            case 3:
                this.onPlay (event);
                break;

            case 4:
                this.onLoop (event);
                break;

            case 5:
                if (event == ButtonEvent.DOWN)
                    this.model.getTransport ().startRecording ();
                break;

            case 6:
                this.onRecord (event);
                break;

            default:
                // Intentionally empty
                break;
        }
    }


    private void onRewind (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.isRewinding = true;
        else if (event == ButtonEvent.UP)
            this.isRewinding = false;
        this.doChangePosition ();
    }


    private void onForward (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.isForwarding = true;
        else if (event == ButtonEvent.UP)
            this.isForwarding = false;
        this.doChangePosition ();
    }


    private void doChangePosition ()
    {
        if (!this.isRewinding && !this.isForwarding)
        {
            this.turnOffTransport (ButtonEvent.UP);
            return;
        }
        this.model.getTransport ().changePosition (this.isForwarding, false);
        this.surface.scheduleTask (this::doChangePosition, 100);
    }


    private void onPlay (final ButtonEvent event)
    {
        this.playCommand.executeNormal (event);
        this.turnOffTransport (event);
    }


    private void onStop (final ButtonEvent event)
    {
        this.stopCommand.executeNormal (event);
        this.turnOffTransport (event);
    }


    private void onRecord (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getTransport ().toggleLauncherOverdub ();
        this.turnOffTransport (event);
    }


    private void onLoop (final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            this.model.getTransport ().toggleLoop ();
        this.turnOffTransport (event);
    }


    private void turnOffTransport (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        this.surface.turnOffTransport ();
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActive (Modes.VIEW_SELECT))
            modeManager.restore ();
    }
}
