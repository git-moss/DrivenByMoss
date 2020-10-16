// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.command.pitchbend;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.command.core.AbstractPitchbendCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;


/**
 * Command to handle pitchbend.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PitchbendVolumeCommand extends AbstractPitchbendCommand<MCUControlSurface, MCUConfiguration>
{
    private final boolean useFxBank;
    private final int     channel;


    /**
     * Constructor.
     *
     * @param channel The channel on which to change the volume (0-8, 8 = Master)
     * @param model The model
     * @param surface The surface
     */
    public PitchbendVolumeCommand (final int channel, final IModel model, final MCUControlSurface surface)
    {
        super (model, surface);

        final MCUConfiguration configuration = this.surface.getConfiguration ();
        this.useFxBank = configuration.shouldPinFXTracksToLastController () && this.surface.getSurfaceID () == configuration.getNumMCUDevices () - 1;

        this.channel = channel;
    }


    /** {@inheritDoc} */
    @Override
    public void onPitchbend (final int data1, final int data2)
    {
        final int value = Math.min (data2 * 127 + data1, this.model.getValueChanger ().getUpperBound () - 1);
        if (this.channel == 8)
        {
            if (this.surface.isShiftPressed ())
                this.model.getTransport ().setMetronomeVolume (value);
            else
                this.model.getMasterTrack ().setVolume (value);
            return;
        }

        final int extenderOffset = this.getExtenderOffset ();
        final ITrackBank tb = this.getTrackBank ();
        final ITrack track = tb.getItem (extenderOffset + this.channel);
        if (this.surface.getConfiguration ().useFadersAsKnobs ())
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            if (modeManager.isActive (Modes.VOLUME))
                track.setVolume (value);
            else if (modeManager.isActive (Modes.PAN))
                track.setPan (value);
            else if (modeManager.isActive (Modes.TRACK))
                this.handleTrack (this.channel, value);
            else if (modeManager.isActive (Modes.SEND1))
                track.getSendBank ().getItem (0).setValue (value);
            else if (modeManager.isActive (Modes.SEND2))
                track.getSendBank ().getItem (1).setValue (value);
            else if (modeManager.isActive (Modes.SEND3))
                track.getSendBank ().getItem (2).setValue (value);
            else if (modeManager.isActive (Modes.SEND4))
                track.getSendBank ().getItem (3).setValue (value);
            else if (modeManager.isActive (Modes.SEND5))
                track.getSendBank ().getItem (4).setValue (value);
            else if (modeManager.isActive (Modes.SEND6))
                track.getSendBank ().getItem (5).setValue (value);
            else if (modeManager.isActive (Modes.SEND7))
                track.getSendBank ().getItem (6).setValue (value);
            else if (modeManager.isActive (Modes.SEND8))
                track.getSendBank ().getItem (7).setValue (value);
            else if (modeManager.isActive (Modes.DEVICE_PARAMS))
                this.model.getCursorDevice ().getParameterBank ().getItem (extenderOffset + this.channel).setValue (value);
            return;
        }

        track.setVolume (value);
    }


    private void handleTrack (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        switch (index)
        {
            case 0:
                selectedTrack.setVolume (value);
                break;

            case 1:
                selectedTrack.setPan (value);
                break;

            default:
                if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.getSendBank ().getItem (index - 2).setValue (value);
                break;
        }
    }


    protected ITrackBank getTrackBank ()
    {
        if (this.useFxBank)
        {
            final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
            if (effectTrackBank != null)
                return effectTrackBank;
        }
        return this.model.getCurrentTrackBank ();
    }


    protected int getExtenderOffset ()
    {
        return this.useFxBank ? 0 : this.surface.getExtenderOffset ();
    }
}
