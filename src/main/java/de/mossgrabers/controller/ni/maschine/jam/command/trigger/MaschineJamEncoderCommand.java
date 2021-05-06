// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.command.trigger;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Support for the NI Maschine controller series.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MaschineJamEncoderCommand extends AbstractTriggerCommand<MaschineJamControlSurface, MaschineJamConfiguration>
{
    private static EncoderMode    activeEncoderMode = null;

    private EncoderMode           encoderMode;
    private final IHwRelativeKnob encoder;


    /**
     * Constructor.
     * 
     * @param encoder The main encoder knob
     * @param encoderMode The mode to trigger with this button
     * @param model The model
     * @param surface The surface
     */
    public MaschineJamEncoderCommand (final IHwRelativeKnob encoder, final EncoderMode encoderMode, final IModel model, final MaschineJamControlSurface surface)
    {
        super (model, surface);

        this.encoder = encoder;
        this.encoderMode = encoderMode;

        // Activate the default mode
        if (this.encoderMode == EncoderMode.MASTER_VOLUME)
        {
            this.execute (ButtonEvent.DOWN, 127);
            this.model.getTrackBank ().addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        }
    }


    /** {@inheritDoc} */
    @Override
    public synchronized void execute (final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN || activeEncoderMode == this.encoderMode)
            return;

        final IParameter parameter;

        switch (this.encoderMode)
        {
            case SELECTED_TRACK_VOLUME:
                final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
                if (selectedTrack.isEmpty ())
                    return;
                parameter = selectedTrack.get ().getVolumeParameter ();
                break;

            case METRONOME_VOLUME:
                parameter = this.model.getTransport ().getMetronomeVolumeParameter ();
                break;

            case CUE_VOLUME:
                parameter = this.model.getProject ().getCueVolumeParameter ();
                break;

            default:
            case MASTER_VOLUME:
                parameter = this.model.getMasterTrack ().getVolumeParameter ();
                break;
        }

        activeEncoderMode = this.encoderMode;

        this.encoder.bind (parameter);
    }


    /**
     * Returns true if one of the encoder related buttons should be lit.
     *
     * @return True if lit
     */
    public boolean isLit ()
    {
        return activeEncoderMode == this.encoderMode;
    }


    /**
     * Bind the selected tracks' volume parameter.
     *
     * @param isSelected True if the track got selected
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected || activeEncoderMode != EncoderMode.SELECTED_TRACK_VOLUME)
            return;
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
            this.encoder.bind (selectedTrack.get ().getVolumeParameter ());
    }
}
