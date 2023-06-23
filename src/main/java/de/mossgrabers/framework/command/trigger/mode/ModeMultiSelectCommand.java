// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.command.trigger.mode;

import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Selects the next mode from a list. If the last element is reached it wraps around to the first.
 * Contains specific handling of Send modes which are checked for existence.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class ModeMultiSelectCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private final ModeManager modeManager;
    private final List<Modes> modeIds = new ArrayList<> ();
    private Modes             currentModeID;
    private final int         numModeIds;


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
        this.currentModeID = this.modeIds.get (0);
        this.numModeIds = this.modeIds.size ();
    }


    /** {@inheritDoc} */
    @Override
    public void executeNormal (final ButtonEvent event)
    {
        this.switchMode (true, event);
    }


    /** {@inheritDoc} */
    @Override
    public void executeShifted (final ButtonEvent event)
    {
        this.switchMode (false, event);
    }


    private void switchMode (final boolean selectNext, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        final Modes activeModeId = this.modeManager.getActiveID ();
        Modes newMode = this.currentModeID;

        // If coming from a mode not on the list, activate the last one
        if (this.currentModeID.equals (activeModeId))
        {
            int index = this.modeIds.indexOf (activeModeId);
            final int startIndex = index;
            // Select the previous/next mode. Skips send modes for which no send exists.
            // Sticks with the active send mode if there is no other one with an existing send.
            do
            {
                index = this.changeIndex (selectNext, index);
                newMode = this.modeIds.get (index);
            } while (this.sendDoesNotExist (newMode) && index != startIndex);
        }

        this.activateMode (newMode);
    }


    private boolean sendDoesNotExist (final Modes mode)
    {
        if (Modes.isSendMode (mode))
            return !this.model.getTrackBank ().canEditSend (mode.ordinal () - Modes.SEND1.ordinal ());
        if (Modes.isLayerSendMode (mode))
            return !this.model.getTrackBank ().canEditSend (mode.ordinal () - Modes.DEVICE_LAYER_SEND1.ordinal ());
        return false;
    }


    /**
     * Select the previous or next index. Wraps if necessary.
     *
     * @param selectNext Select the next index if true otherwise the previous one
     * @param index The current index
     * @return The previous/next index
     */
    private int changeIndex (final boolean selectNext, final int index)
    {
        if (selectNext)
        {
            final int next = index + 1;
            return next < 0 || next >= this.numModeIds ? 0 : next;
        }
        final int prev = index - 1;
        return prev < 0 || prev >= this.numModeIds ? this.numModeIds - 1 : prev;
    }


    /**
     * Activate a mode.
     *
     * @param modeID The mode to activate
     */
    public void activateMode (final Modes modeID)
    {
        this.currentModeID = modeID;
        this.modeManager.setActive (modeID);

        String modeName = this.modeManager.get (modeID).getName ();
        if (Modes.isSendMode (modeID))
            modeName = getSendModeNotification (modeID, modeName, this.model.getTrackBank ());

        this.model.getHost ().showNotification (modeName);
    }


    /**
     * Build the information text for a send channel.
     *
     * @param modeID The ID of the send mode
     * @param modeName The name of the mode
     * @param trackBank The track bank from which to get more effect info
     * @return The text
     */
    public static String getSendModeNotification (final Modes modeID, final String modeName, final ITrackBank trackBank)
    {
        final int sendIndex = modeID.ordinal () - Modes.SEND1.ordinal ();
        String sendModeName = modeName + " " + (sendIndex + 1);
        Optional<ITrack> selectedTrack = trackBank.getSelectedItem ();
        if (selectedTrack.isEmpty ())
        {
            final ITrack item = trackBank.getItem (0);
            selectedTrack = item.doesExist () ? Optional.of (item) : Optional.empty ();
        }
        if (selectedTrack.isPresent ())
        {
            sendModeName += ": ";
            final ISend send = selectedTrack.get ().getSendBank ().getItem (sendIndex);
            sendModeName += send.doesExist () ? send.getName () : "-";
        }
        return sendModeName;
    }
}
