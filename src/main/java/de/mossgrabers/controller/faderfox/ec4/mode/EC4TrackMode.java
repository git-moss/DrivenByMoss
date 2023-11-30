// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.MuteParameter;
import de.mossgrabers.framework.parameter.SoloParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The track mode. The knobs control the volume, panorama and panorama of the tracks on the current
 * track page.
 *
 * @author Jürgen Moßgraber
 */
public class EC4TrackMode extends AbstractEC4Mode<ITrack>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public EC4TrackMode (final EC4ControlSurface surface, final IModel model)
    {
        super (Modes.NAME_TRACK, surface, model, model.getTrackBank ());

        final IParameterProvider trackProvider = new SelectedTrackParameterProvider (model);

        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                new RangeFilterParameterProvider (trackProvider, 0, 2), new FixedParameterProvider (new MuteParameter (model)), new FixedParameterProvider (new SoloParameter (model)),
                // Row 2-3
                new RangeFilterParameterProvider (trackProvider, 2, 8),
                // Row 4
                this.bottomRowProvider));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (row < 3)
        {
            if (event != ButtonEvent.DOWN)
                return;

            final int trackIndex = row * 4 + column;
            this.model.getTrackBank ().getItem (trackIndex).select ();
            return;
        }

        super.onButton (row, column, event);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final List<String> totalDisplayInfo = new ArrayList<> ();
        final ITextDisplay display = this.surface.getTextDisplay ().clear ();

        display.setCell (0, 0, "Vol ").setCell (0, 1, "Pan ").setCell (0, 2, "Mute").setCell (0, 3, "Solo");
        display.setCell (1, 0, "Snd1").setCell (1, 1, "Snd2").setCell (1, 2, "Snd3").setCell (1, 3, "Snd4");
        display.setCell (2, 0, "Snd5").setCell (2, 1, "Snd6").setCell (2, 2, "Snd7").setCell (2, 3, "Snd8");

        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
        {
            final ITrack track = selectedTrack.get ();
            updateCache (0, track.getVolume (), "Volume: " + track.getVolumeStr (), totalDisplayInfo);
            updateCache (1, track.getPan (), "Pan: " + track.getPanStr (), totalDisplayInfo);
            updateCache (2, track.isMute () ? 127 : 0, "Mute: " + (track.isMute () ? "on" : "off"), totalDisplayInfo);
            updateCache (3, track.isSolo () ? 127 : 0, "Solo: " + (track.isSolo () ? "on" : "off"), totalDisplayInfo);

            final ISendBank sendBank = track.getSendBank ();
            for (int i = 0; i < 8; i++)
            {
                final ISend send = sendBank.getItem (i);
                updateCache (4 + i, send.getValue (), "Send " + (i + 1) + " Volume: " + send.getDisplayedValue (), totalDisplayInfo);
            }
        }

        super.updateDisplayRow4 (display, totalDisplayInfo);

        display.allDone ();

        if (!totalDisplayInfo.isEmpty ())
        {
            final ITextDisplay totalDisplay = this.surface.getTextDisplay (1).clear ();
            for (int i = 0; i < Math.min (4, totalDisplayInfo.size ()); i++)
                totalDisplay.setRow (i, StringUtils.pad (StringUtils.fixASCII (totalDisplayInfo.get (i)), 16));
            totalDisplay.allDone ();
            this.surface.showTotalDisplay ();
        }
    }
}