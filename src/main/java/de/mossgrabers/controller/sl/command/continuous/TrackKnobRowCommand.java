// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.command.continuous;

import de.mossgrabers.controller.sl.SLConfiguration;
import de.mossgrabers.controller.sl.controller.SLControlSurface;
import de.mossgrabers.framework.command.core.AbstractContinuousCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
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
        Modes cm = modeManager.getActiveOrTempModeId ();
        if (!Modes.MODE_TRACK.equals (cm) && !Modes.MODE_MASTER.equals (cm))
        {
            modeManager.setActiveMode (Modes.MODE_TRACK);
            cm = Modes.MODE_TRACK;
        }

        if (Modes.MODE_MASTER.equals (cm))
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

            case 2:
                if (this.surface.getConfiguration ().isDisplayCrossfader ())
                    track.setCrossfadeModeAsNumber (toCrossfadeNumber (value));
                else if (!this.model.isEffectTrackBankActive ())
                    track.getSendBank ().getItem (0).setValue (value);
                break;

            // Send 1 - 5
            default:
                if (!this.model.isEffectTrackBankActive ())
                    track.getSendBank ().getItem (this.index - (this.surface.getConfiguration ().isDisplayCrossfader () ? 3 : 2)).setValue (value);
                break;
        }
    }


    private static int toCrossfadeNumber (final int value)
    {
        if (value == 0)
            return 0;
        return value == 127 ? 2 : 1;
    }
}
