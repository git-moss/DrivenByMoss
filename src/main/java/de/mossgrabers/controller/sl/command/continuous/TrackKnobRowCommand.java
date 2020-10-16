// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.command.continuous;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;


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
    public TrackKnobRowCommand (final int index, final IModel model, final SLControlSurface surface)
    {
        super (model, surface);
        this.index = index;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        Modes cm = modeManager.getActiveID ();
        if (!Modes.TRACK.equals (cm) && !Modes.MASTER.equals (cm))
        {
            modeManager.setActive (Modes.TRACK);
            cm = Modes.TRACK;
        }

        if (Modes.MASTER.equals (cm))
        {
            if (this.index == 0)
                this.model.getMasterTrack ().setVolume (value);
            else if (this.index == 1)
                this.model.getMasterTrack ().setPan (value);
            return;
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getSelectedItem ();
        if (track == null)
            return;

        switch (this.index)
        {
            // Volume
            case 0:
                track.setVolume (value);
                break;

            // Pan
            case 1:
                track.setPan (value);
                break;

            // Send 1 - 6
            default:
                if (!this.model.isEffectTrackBankActive ())
                    track.getSendBank ().getItem (this.index - 2).setValue (value);
                break;
        }
    }
}
