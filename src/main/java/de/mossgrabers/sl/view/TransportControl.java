package de.mossgrabers.sl.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.PlayCommand;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.mode.Modes;


/**
 * Provides an indexed access to commands of the transport control.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportControl
{
    private SLControlSurface                                     surface;
    private Model                                                model;
    private boolean                                              isRewinding;
    private boolean                                              isForwarding;
    private final PlayCommand<SLControlSurface, SLConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public TransportControl (final SLControlSurface surface, final Model model)
    {
        this.surface = surface;
        this.model = model;
        this.playCommand = new PlayCommand<> (model, surface);
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
                    this.model.getTransport ().record ();
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
            this.turnOffTransport ();
            return;
        }
        this.model.getTransport ().changePosition (this.isForwarding, false);
        this.surface.scheduleTask (this::doChangePosition, 100);
    }


    private void onPlay (final ButtonEvent event)
    {
        this.playCommand.executeNormal (event);
        this.turnOffTransport ();
    }


    private void onStop (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final TransportProxy transport = this.model.getTransport ();
        if (transport.isPlaying ())
            transport.play ();
        else
            transport.stopAndRewind ();
        this.turnOffTransport ();
    }


    private void onRecord (final ButtonEvent event)
    {
        // Toggle launcher overdub
        if (event != ButtonEvent.DOWN)
            return;
        this.model.getTransport ().toggleLauncherOverdub ();
        this.turnOffTransport ();
    }


    private void onLoop (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        this.model.getTransport ().toggleLoop ();
        this.turnOffTransport ();
    }


    private void turnOffTransport ()
    {
        this.surface.turnOffTransport ();
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_VIEW_SELECT))
            modeManager.restoreMode ();
    }
}
