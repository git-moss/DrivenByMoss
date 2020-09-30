// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Selects the next mode from a list. If the last element is reached it wraps around to the first.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ModeMultiSelectCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final ModeManager modeManager;
    private final List<Modes> modeIds = new ArrayList<> ();
    private final Modes       send1;
    private Modes             currentModeID;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param modeIds The list with IDs of the modes to select
     */
    public ModeMultiSelectCommand (final IModel model, final S surface, final Modes... modeIds)
    {
        super (model, surface);

        this.modeManager = this.surface.getModeManager ();
        this.modeIds.addAll (Arrays.asList (modeIds));
        this.send1 = Modes.SEND1;
        this.currentModeID = this.modeIds.get (0);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final Modes activeModeId = this.modeManager.getActiveModeId ();
        Modes newMode = this.currentModeID;

        // If coming from a mode not on the list, activate the last one
        if (this.currentModeID.equals (activeModeId))
        {
            final ITrackBank trackBank = this.model.getTrackBank ();
            int index = this.modeIds.indexOf (activeModeId);
            // If a send mode is selected check if the according send exists
            do
            {
                index--;
                if (index < 0 || index >= this.modeIds.size ())
                    index = this.modeIds.size () - 1;
                newMode = this.modeIds.get (index);
            } while (Modes.isSendMode (newMode) && !trackBank.canEditSend (newMode.ordinal () - this.send1.ordinal ()));
        }

        this.activateMode (newMode);
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final Modes activeModeId = this.modeManager.getActiveModeId ();
        Modes newMode = this.currentModeID;

        // If coming from a mode not on the list, activate the last one
        if (this.currentModeID.equals (activeModeId))
        {
            final ITrackBank trackBank = this.model.getTrackBank ();
            int index = this.modeIds.indexOf (activeModeId);
            // If a send mode is selected check if the according send exists
            do
            {
                index++;
                if (index < 0 || index >= this.modeIds.size ())
                    index = 0;
                newMode = this.modeIds.get (index);
            } while (Modes.isSendMode (newMode) && !trackBank.canEditSend (newMode.ordinal () - this.send1.ordinal ()));
        }

        this.activateMode (newMode);
    }


    /**
     * Activate a mode.
     *
     * @param modeID The mode to activate
     */
    public void activateMode (final Modes modeID)
    {
        this.currentModeID = modeID;
        this.modeManager.setActiveMode (modeID);

        String modeName = this.modeManager.getMode (modeID).getName ();

        if (Modes.isSendMode (modeID))
        {
            final int sendIndex = modeID.ordinal () - this.send1.ordinal ();
            modeName = modeName + " " + (sendIndex + 1);
            final ITrackBank trackBank = this.model.getTrackBank ();
            ITrack selectedTrack = trackBank.getSelectedItem ();
            if (selectedTrack == null)
                selectedTrack = trackBank.getItem (0);
            if (selectedTrack != null)
                modeName += ": " + selectedTrack.getSendBank ().getItem (sendIndex).getName ();
        }

        this.model.getHost ().showNotification (modeName);
    }
}
