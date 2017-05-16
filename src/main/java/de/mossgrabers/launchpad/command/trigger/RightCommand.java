// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.trigger.MetronomeCommand;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;


/**
 * Command to dis-/enable the metronome. Also toggles metronome ticks when Shift is pressed.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class RightCommand extends MetronomeCommand<LaunchpadControlSurface, LaunchpadConfiguration>
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public RightCommand (final Model model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        final View view = this.surface.getViewManager ().getActiveView ();
        if (index == 8 || this.surface.isShiftPressed ())
        {
            if (!tb.canScrollTracksDown ())
                return;
            tb.scrollTracksPageDown ();
            final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
            this.surface.scheduleTask ( () -> view.selectTrack (newSel), 75);
            return;
        }
        view.selectTrack (index);
    }
}
