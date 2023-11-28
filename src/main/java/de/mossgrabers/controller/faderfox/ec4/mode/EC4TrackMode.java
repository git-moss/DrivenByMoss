// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.mode;

import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.MuteParameter;
import de.mossgrabers.framework.parameter.SoloParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


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
        final ITextDisplay display = this.surface.getTextDisplay ().clear ();

        display.setCell (0, 0, "Vol ").setCell (0, 1, "Pan ").setCell (0, 2, "Mute").setCell (0, 3, "Solo");
        display.setCell (1, 0, "Snd1").setCell (1, 1, "Snd2").setCell (1, 2, "Snd3").setCell (1, 3, "Snd4");
        display.setCell (2, 0, "Snd5").setCell (2, 1, "Snd6").setCell (2, 2, "Snd7").setCell (2, 3, "Snd8");
        display.setCell (3, 0, "Tmpo").setCell (3, 1, "Xfde").setCell (3, 2, "Cue ").setCell (3, 3, "Main");

        display.allDone ();
    }
}