// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.transport;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.Optional;


/**
 * Command to handle the record button. Toggles arranger record. Combination with Shift toggles
 * launcher overdub. Combination Select creates a new clip.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RecordCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    protected final ITransport     transport;
    private final NewCommand<S, C> newCmd;
    private ButtonID               recordTrigger;
    private ButtonID               launcherOverdubTrigger;
    private ButtonID               newTrigger;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RecordCommand (final IModel model, final S surface)
    {
        this (model, surface, null, ButtonID.SHIFT, ButtonID.SELECT);
    }


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param recordTrigger The button ID for triggering the record command, may be null
     * @param launcherOverdubTrigger The button ID for triggering the launcher command, may be null
     * @param newTrigger The button ID for triggering the new clip command, may be null
     */
    public RecordCommand (final IModel model, final S surface, final ButtonID recordTrigger, final ButtonID launcherOverdubTrigger, final ButtonID newTrigger)
    {
        super (model, surface);

        this.recordTrigger = recordTrigger;
        this.launcherOverdubTrigger = launcherOverdubTrigger;
        this.newTrigger = newTrigger;

        if (this.recordTrigger == this.launcherOverdubTrigger || this.recordTrigger == this.newTrigger || this.launcherOverdubTrigger == this.newTrigger)
            throw new FrameworkException ("Duplicated record modifier!");

        this.newCmd = new NewCommand<> (model, surface);
        this.transport = this.model.getTransport ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
    {
        switch (this.getActiveAction ())
        {
            case RECORD:
                this.executeRecord (event);
                break;
            case LAUNCHER_OVERDUB:
                this.executeLauncherOverdub (event);
                break;
            case NEW_CLIP:
                this.executeNew (event);
                break;
        }
    }


    protected void executeRecord (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.handleExecute (false);
    }


    protected void executeLauncherOverdub (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.handleExecute (true);
    }


    protected void executeNew (final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.newCmd.executeNormal (event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        // Backwards compatibility to old interface, e.g. Launchpad ShiftView
        if (event == ButtonEvent.DOWN)
            this.handleExecute (false);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        // Backwards compatibility to old interface, e.g. Launchpad ShiftView
        if (event == ButtonEvent.DOWN)
            this.handleExecute (true);
    }


    protected void handleExecute (final boolean isShiftPressed)
    {
        final boolean flipRecord = this.surface.getConfiguration ().isFlipRecord ();
        if (isShiftPressed && !flipRecord || !isShiftPressed && flipRecord)
        {
            // If the selected clip is recording, stop the recording instead of toggling the
            // launcher overdub
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isPresent ())
            {
                final Optional<ISlot> selectedSlot = selectedTrack.get ().getSlotBank ().getSelectedItem ();
                if (selectedSlot.isPresent ())
                {
                    final ISlot slot = selectedSlot.get ();
                    if (slot.isRecording ())
                    {
                        slot.launch ();
                        return;
                    }
                }
            }

            this.transport.toggleLauncherOverdub ();
        }
        else
            this.transport.startRecording ();
    }


    /**
     * Returns true if record is on (depending on shift/select state).
     *
     * @return True if enabled
     */
    public boolean isActive ()
    {
        switch (this.getActiveAction ())
        {
            case RECORD:
                return this.transport.isRecording ();
            case LAUNCHER_OVERDUB:
                return this.getLauncherOverdubState ();
            default:
            case NEW_CLIP:
                return false;
        }
    }


    protected boolean getLauncherOverdubState ()
    {
        return this.transport.isLauncherOverdub ();
    }


    private ActiveAction getActiveAction ()
    {
        if (this.recordTrigger != null && this.surface.isPressed (this.recordTrigger))
            return ActiveAction.RECORD;

        if (this.launcherOverdubTrigger != null && this.surface.isPressed (this.launcherOverdubTrigger))
            return ActiveAction.LAUNCHER_OVERDUB;

        if (this.newTrigger != null && this.surface.isPressed (this.newTrigger))
            return ActiveAction.NEW_CLIP;

        if (this.recordTrigger == null)
            return ActiveAction.RECORD;

        if (this.launcherOverdubTrigger == null)
            return ActiveAction.LAUNCHER_OVERDUB;

        return ActiveAction.NEW_CLIP;
    }


    private enum ActiveAction
    {
        RECORD,
        LAUNCHER_OVERDUB,
        NEW_CLIP
    }
}
