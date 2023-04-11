// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Arrays;
import java.util.Optional;


/**
 * Mode for editing a track parameters.
 *
 * @author Jürgen Moßgraber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final MCUControlSurface surface, final IModel model)
    {
        super (Modes.NAME_TRACK, surface, model);

        final IParameterProvider parameterProvider;
        if (this.pinFXtoLastDevice)
            parameterProvider = new VolumeParameterProvider (model.getEffectTrackBank ());
        else
        {
            final int surfaceID = surface.getSurfaceID ();
            if (surfaceID == 0)
                parameterProvider = new RangeFilterParameterProvider (new SelectedTrackParameterProvider (model), 0, 8);
            else if (surfaceID == 1)
                parameterProvider = new RangeFilterParameterProvider (new SendParameterProvider (model, -1, 6), 0, 8);
            else
                parameterProvider = new EmptyParameterProvider (8);
        }
        this.setParameterProvider (parameterProvider);
    }


    /** {@inheritDoc} */
    @Override
    public void updateKnobLEDs ()
    {
        final int [] ledModes = new int [8];
        Arrays.fill (ledModes, MCUControlSurface.KNOB_LED_MODE_WRAP);

        if (this.getExtenderOffset () == 0 && this.getParameterProvider ().get (1).doesExist ())
            ledModes[1] = MCUControlSurface.KNOB_LED_MODE_BOOST_CUT;

        this.updateKnobLEDs (ledModes);
    }


    /** {@inheritDoc} */
    @Override
    protected void drawTrackNameHeader ()
    {
        this.drawParameterHeader ();
    }


    /** {@inheritDoc} */
    @Override
    protected void drawParameterHeader ()
    {
        super.drawParameterHeader ();

        if (this.getExtenderOffset () == 0 && this.configuration.isDisplayTrackNames ())
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isEmpty ())
                return;

            final ITextDisplay d = this.surface.getTextDisplay ();
            d.setCell (0, 0, StringUtils.shortenAndFixASCII (selectedTrack.get ().getName (), this.getTextLength ()));
            d.done (0);
        }
    }
}