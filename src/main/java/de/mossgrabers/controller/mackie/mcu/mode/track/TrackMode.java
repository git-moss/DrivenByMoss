// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import java.util.Arrays;
import java.util.Optional;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.MainDisplay;
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

        if (this.surface.getConfiguration ().getMainDisplayType () == MainDisplay.ASPARION && this.surface.getSurfaceID () == 0)
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isEmpty ())
                return;

            final ITextDisplay display = this.surface.getTextDisplay ();
            display.clearRow (0);
            display.setCell (0, 0, StringUtils.shortenAndFixASCII (selectedTrack.get ().getName (), this.getTextLength ()));
            display.done (0);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void drawParameterHeader (final ITextDisplay display, final int row)
    {
        super.drawParameterHeader (display, row);

        if (this.getExtenderOffset () == 0 && this.configuration.isDisplayTrackNames ())
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isEmpty ())
                return;

            display.setCell (row, 0, StringUtils.shortenAndFixASCII (selectedTrack.get ().getName (), this.getTextLength ()));
            display.done (row);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void updateItemIndices ()
    {
        final int [] indices = new int [8];
        Arrays.fill (indices, 0);
        if (this.getExtenderOffset () == 0)
        {
            final ITrack selectedTrack = this.model.getCursorTrack ();
            if (selectedTrack.doesExist ())
                indices[0] = selectedTrack.getPosition () + 1;
        }
        this.surface.setItemIndices (indices);
    }
}