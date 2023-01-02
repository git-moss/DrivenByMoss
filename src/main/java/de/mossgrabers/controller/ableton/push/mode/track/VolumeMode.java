// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.Push1Display;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;


/**
 * Mode for editing a volume parameter of all tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class VolumeMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public VolumeMode (final PushControlSurface surface, final IModel model)
    {
        super (Modes.NAME_VOLUME, surface, model);

        this.setParameterProvider (new VolumeParameterProvider (model));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final int upperBound = this.model.getValueChanger ().getUpperBound ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            display.setCell (0, i, t.doesExist () ? "Volume" : "").setCell (1, i, t.getVolumeStr (8));
            if (t.doesExist ())
                display.setCell (2, i, config.isEnableVUMeters () ? Push1Display.formatValue (t.getVolume (), t.getVu (), upperBound) : Push1Display.formatValue (t.getVolume (), upperBound));
        }
        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        this.updateChannelDisplay (display, AbstractGraphicDisplay.GRID_ELEMENT_CHANNEL_VOLUME, true, false);
    }
}