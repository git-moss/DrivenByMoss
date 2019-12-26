// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.sl.command.trigger;

import de.mossgrabers.controller.sl.view.SLView;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.View;


/**
 * Command to delegate the button pushes of a button row to the active mode.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ButtonRowSelectCommand<S extends IControlSurface<C>, C extends Configuration> extends AbstractTriggerCommand<S, C>
{
    private int row;


    /**
     * Constructor.
     *
     * @param row The number of the button row
     * @param model The model
     * @param surface The surface
     */
    public ButtonRowSelectCommand (final int row, final IModel model, final S surface)
    {
        super (model, surface);
        this.row = row;
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event, final int velocity)
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
                this.surface.getModeManager ().setActiveMode (Modes.DEVICE_PARAMS);
                this.model.getHost ().showNotification ("Device Parameters");
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

        if (modeManager.isActiveOrTempMode (Modes.MASTER))
        {
            this.activateTrackMode (true, false);
            return;
        }

        if (modeManager.isActiveOrTempMode (Modes.TRACK))
        {
            if (this.model.isEffectTrackBankActive ())
                this.activateMasterMode (true);
            else
                this.activateTrackMode (true, true);
            return;
        }

        this.activateTrackMode (true, this.model.isEffectTrackBankActive ());
    }


    private void activateTrackMode (final boolean activateMode, final boolean isEffect)
    {
        final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
        if (isEffect != isEffectTrackBankActive)
            this.model.toggleCurrentTrackBank ();
        final ModeManager modeManager = this.surface.getModeManager ();
        if (activateMode)
            modeManager.setActiveMode (Modes.TRACK);
        this.model.getHost ().showNotification (isEffect ? "Effects" : "Tracks");
        if (this.model.getSelectedTrack () != null)
            return;
        final Mode activeMode = modeManager.getActiveOrTempMode ();
        if (activeMode != null)
            activeMode.selectItem (0);
    }


    private void activateMasterMode (final boolean activateMode)
    {
        this.model.getMasterTrack ().select ();
        if (activateMode)
            this.surface.getModeManager ().setActiveMode (Modes.MASTER);
        this.model.getHost ().showNotification ("Master");
    }


    private void onSliderRowSelect ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();

        if (!modeManager.isActiveOrTempMode (Modes.VOLUME))
        {
            modeManager.setActiveMode (Modes.VOLUME);
            this.activateTrackMode (false, false);
            return;
        }

        if (this.model.getMasterTrack ().isSelected ())
            this.activateTrackMode (false, false);
        else if (this.model.isEffectTrackBankActive ())
            this.activateMasterMode (false);
        else
            this.activateTrackMode (false, true);
    }
}
