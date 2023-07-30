// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.command.continuous;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;


/**
 * Command to change the Master Volume and Cue Volume.
 *
 * @author Jürgen Moßgraber
 */
public class PushMasterVolumeCommand extends AbstractContinuousCommand<PushControlSurface, PushConfiguration>
{
    private boolean isMasterVolumeMode = true;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PushMasterVolumeCommand (final IModel model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.surface.isSelectPressed ())
        {
            this.model.getApplication ().getZoomParameter ().changeValue (value);
            return;
        }

        if (this.isMasterVolumeMode)
            this.model.getMasterTrack ().changeVolume (value);
        else
            this.model.getProject ().changeCueVolume (value);
    }


    /**
     * Toggle between the master volume and cue volume control.
     */
    public void toggleMasterVolumeAndCue ()
    {
        this.isMasterVolumeMode = !this.isMasterVolumeMode;
    }


    /**
     * Notify the active mode.
     */
    public void notifyMode ()
    {
        this.surface.getDisplay ().notify (this.isMasterVolumeMode ? "Master Volume" : "Cue Volume");
    }
}
