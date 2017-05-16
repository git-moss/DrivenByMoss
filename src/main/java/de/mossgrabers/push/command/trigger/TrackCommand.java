// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to Browse presets.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TrackCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final PushConfiguration config = this.surface.getConfiguration ();

        if (this.surface.isShiftPressed ())
        {
            config.setVUMetersEnabled (!config.isEnableVUMeters ());
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer currentMode = modeManager.getActiveModeId ();

        if (config.isPush2 ())
        {
            if (Modes.MODE_TRACK.equals (currentMode) || Modes.MODE_VOLUME.equals (currentMode) || Modes.MODE_CROSSFADER.equals (currentMode) || Modes.MODE_PAN.equals (currentMode))
            {
                this.model.toggleCurrentTrackBank ();
            }
            else if (currentMode.intValue () >= Modes.MODE_SEND1.intValue () && currentMode.intValue () <= Modes.MODE_SEND8.intValue ())
            {
                modeManager.setActiveMode (Modes.MODE_TRACK);
                this.model.toggleCurrentTrackBank ();
            }
            else
                modeManager.setActiveMode (Modes.MODE_TRACK);
        }
        else
        {
            // Layer mode selection for Push 1
            if (this.surface.isSelectPressed () && Modes.isLayerMode (currentMode))
            {
                modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);
                return;
            }

            if (currentMode == Modes.MODE_TRACK)
                this.model.toggleCurrentTrackBank ();
            else
                modeManager.setActiveMode (Modes.MODE_TRACK);
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData track = tb.getSelectedTrack ();
        if (track == null)
            tb.select (0);
    }
}
