// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.track.CrossfadeParameterProvider;

import java.util.HashMap;
import java.util.Map;


/**
 * Mode for editing the cross-fade setting of all tracks.
 *
 * @author Jürgen Moßgraber
 */
public class CrossfadeMode extends AbstractTrackMode
{
    private static final Map<String, String> CROSSFADE_TEXT = new HashMap<> (3);

    static
    {
        CROSSFADE_TEXT.put ("A", "A");
        CROSSFADE_TEXT.put ("B", "       B");
        CROSSFADE_TEXT.put ("AB", "   <> ");
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public CrossfadeMode (final PushControlSurface surface, final IModel model)
    {
        super (Modes.NAME_CROSSFADE, surface, model);

        this.setParameterProvider (new CrossfadeParameterProvider (model));
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            if (!t.doesExist ())
                continue;
            display.setCell (0, i, "Crossfdr");
            final IParameter crossfadeParameter = t.getCrossfadeParameter ();
            display.setCell (1, i, CROSSFADE_TEXT.get (crossfadeParameter.getDisplayedValue ()));
            display.setCell (2, i, crossfadeParameter.getValue (), Format.FORMAT_PAN);
        }
        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        this.updateChannelDisplay (display, AbstractGraphicDisplay.GRID_ELEMENT_CHANNEL_CROSSFADER, false, false);
    }

}