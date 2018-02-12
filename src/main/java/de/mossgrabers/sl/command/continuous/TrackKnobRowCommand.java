// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.command.continuous;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.mode.Modes;


/**
 * Command to change a device parameter.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackKnobRowCommand extends AbstractContinuousCommand<SLControlSurface, SLConfiguration>
{
    private int index;


    /**
     * Constructor.
     *
     * @param index The index of the button
     * @param model The model
     * @param surface The surface
     */
    public TrackKnobRowCommand (final int index, final Model model, final SLControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        Integer cm = modeManager.getActiveModeId ();
        if (cm != Modes.MODE_TRACK && cm != Modes.MODE_MASTER)
        {
            modeManager.setActiveMode (Modes.MODE_TRACK);
            cm = Modes.MODE_TRACK;
        }

        if (cm == Modes.MODE_MASTER)
        {
            if (this.index == 0)
                this.model.getMasterTrack ().setVolume (value);
            else if (this.index == 1)
                this.model.getMasterTrack ().setPan (value);
            return;
        }

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedTrack ();
        if (track == null)
            return;

        switch (this.index)
        {
            // Volume
            case 0:
                tb.setVolume (track.getIndex (), value);
                break;

            // Pan
            case 1:
                tb.setPan (track.getIndex (), value);
                break;

            case 2:
                if (this.surface.getConfiguration ().isDisplayCrossfader ())
                    tb.setCrossfadeModeAsNumber (track.getIndex (), value == 0 ? 0 : value == 127 ? 2 : 1);
                else if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).setSend (track.getIndex (), 0, value);
                break;

            // Send 1 - 5
            default:
                if (tb instanceof ITrackBank)
                    ((ITrackBank) tb).setSend (track.getIndex (), this.index - (this.surface.getConfiguration ().isDisplayCrossfader () ? 3 : 2), value);
                break;
        }
    }
}
