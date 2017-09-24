// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.command.pitchbend;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Command to handle pitchbend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PitchbendVolumeCommand extends AbstractPitchbendCommand<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public PitchbendVolumeCommand (final Model model, final MCUControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int channel, final int data1, final int data2)
    {
        final double value = Math.min (data2 * 127 + (double) data1, this.model.getValueChanger ().getUpperBound () - 1);
        if (channel == 8)
        {
            this.model.getMasterTrack ().setVolume (value);
            return;
        }

        final int extenderOffset = this.surface.getExtenderOffset ();
        this.model.getCurrentTrackBank ().setVolume (extenderOffset + channel, value);
    }
}
