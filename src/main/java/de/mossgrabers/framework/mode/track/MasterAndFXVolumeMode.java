package de.mossgrabers.framework.mode.track;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.FrameworkException;

import java.util.List;


/**
 * A mode for N FX track volumes, the metronome and master volume.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class MasterAndFXVolumeMode<S extends IControlSurface<C>, C extends Configuration> extends DefaultTrackMode<S, C>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isAbsolute If true the value change is happening with a setter otherwise relative
     *            change method is used
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public MasterAndFXVolumeMode (final S surface, final IModel model, final boolean isAbsolute, final List<ContinuousID> controls)
    {
        super ("FX and master volumes", surface, model, isAbsolute, controls);

        final int numnFXTracks = controls.size () - 2;
        if (numnFXTracks < 0)
            throw new FrameworkException ("Number of controls must be at least 2.");

        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        this.setParameterProvider (new CombinedParameterProvider (
                // N FX track volumes
                effectTrackBank == null ? new EmptyParameterProvider (numnFXTracks) : new RangeFilterParameterProvider (new VolumeParameterProvider (effectTrackBank), 0, numnFXTracks),
                // Metronome volume
                new FixedParameterProvider (this.model.getTransport ().getMetronomeVolumeParameter ()),
                // Master volume
                new FixedParameterProvider (this.model.getMasterTrack ().getVolumeParameter ())));
    }
}
