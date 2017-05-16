// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.sl.mode.Modes;
import de.mossgrabers.sl.view.SLView;


/**
 * Command to delegate the button pushes of a button row to the active mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonRowSelectCommand<S extends ControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private int     row;
    private boolean isMasterMode;


    /**
     * Constructor.
     *
     * @param row The number of the button row
     * @param model The model
     * @param surface The surface
     */
    public ButtonRowSelectCommand (final int row, final Model model, final S surface)
    {
        super (model, surface);
        this.row = row;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final View view = this.surface.getViewManager ().getActiveView ();
        if (view == null)
            return;

        switch (this.row)
        {
            case 0:
                ((SLView) view).onButtonRow1Select ();
                break;

            case 1:
                this.surface.getModeManager ().setActiveMode (Modes.MODE_PARAMS);
                break;

            case 2:
                ((SLView) view).onButtonRow2Select ();
                break;

            case 3:
                this.onKnobRow2Select ();
                break;

            case 4:
                break;

            case 5:
                this.onSliderRowSelect ();
                break;

            case 6:
            case 7:
                this.onSliderRowSelect ();
                break;

            default:
                // Intentionally empty
                break;
        }
    }


    private void onKnobRow2Select ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer activeModeId = modeManager.getActiveModeId ();
        if (activeModeId == Modes.MODE_MASTER)
        {
            modeManager.setActiveMode (Modes.MODE_TRACK);
            this.isMasterMode = false;
            if (this.model.isEffectTrackBankActive ())
                this.model.toggleCurrentTrackBank ();
            this.surface.getDisplay ().notify ("Tracks");
        }
        else if (activeModeId == Modes.MODE_TRACK)
        {
            if (this.model.isEffectTrackBankActive ())
            {
                modeManager.setActiveMode (Modes.MODE_MASTER);
                this.surface.getDisplay ().notify ("Master");
                this.isMasterMode = true;
            }
            else
            {
                this.model.toggleCurrentTrackBank ();
                this.surface.getDisplay ().notify ("Effects");
            }
        }
        else
            modeManager.setActiveMode (this.isMasterMode ? Modes.MODE_MASTER : Modes.MODE_TRACK);

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData track = tb.getSelectedTrack ();
        if (track == null)
            this.selectTrack (0);
    }


    private void onSliderRowSelect ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        if (modeManager.getActiveModeId () == Modes.MODE_VOLUME)
            this.model.toggleCurrentTrackBank ();
        else
            modeManager.setActiveMode (Modes.MODE_VOLUME);
    }
}
