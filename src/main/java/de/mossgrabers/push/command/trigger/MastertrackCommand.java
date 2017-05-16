// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to display the master mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MastertrackCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private boolean quitMasterMode                = false;
    private int     selectedTrackBeforeMasterMode = -1;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public MastertrackCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        // Avoid accidently leaving the browser
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.isActiveMode (Modes.MODE_BROWSER))
            return;

        switch (event)
        {
            case DOWN:
                this.quitMasterMode = false;
                break;

            case UP:
                this.handleButtonUp (modeManager);
                break;

            case LONG:
                this.quitMasterMode = true;
                modeManager.setActiveMode (Modes.MODE_FRAME);
                break;
        }
    }


    private void handleButtonUp (final ModeManager modeManager)
    {
        if (this.quitMasterMode)
        {
            modeManager.restoreMode ();
            return;
        }

        if (modeManager.getActiveModeId () == Modes.MODE_MASTER)
        {
            this.model.getCurrentTrackBank ().select (this.selectedTrackBeforeMasterMode);
            return;
        }

        modeManager.setActiveMode (Modes.MODE_MASTER);
        this.model.getMasterTrack ().select ();
        final TrackData track = this.model.getCurrentTrackBank ().getSelectedTrack ();
        this.selectedTrackBeforeMasterMode = track == null ? -1 : track.getIndex ();
    }
}
