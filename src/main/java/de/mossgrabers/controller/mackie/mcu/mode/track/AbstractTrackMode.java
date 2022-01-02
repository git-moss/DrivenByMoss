// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.mode.track;

import de.mossgrabers.controller.mackie.mcu.controller.MCUControlSurface;
import de.mossgrabers.controller.mackie.mcu.mode.BaseMode;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode<ITrack>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractTrackMode (final String name, final MCUControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCurrentTrackBank ());

        model.addTrackBankObserver (this::switchBanks);
    }


    protected boolean drawTrackHeader ()
    {
        final ITrackBank tb = this.getTrackBank ();
        final int extenderOffset = this.getExtenderOffset ();

        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), 6));
        }
        d.done (0);

        return true;
    }
}