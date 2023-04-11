// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractRaindropsView;


/**
 * Raindrops view.
 *
 * @author Jürgen Moßgraber
 */
public class RaindropsView extends AbstractRaindropsView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public RaindropsView (final APCminiControlSurface surface, final IModel model)
    {
        super ("Raindrops", surface, model, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.isActive ())
            return;

        switch (index)
        {
            case 0:
                this.onOctaveUp (event);
                break;
            case 1:
                this.onOctaveDown (event);
                break;
            case 2:
            case 3:
                break;
            default:
                // Not used
                break;
        }
        this.updateScale ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final int res = 7 - index;
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        return isKeyboardEnabled && res == this.getResolutionIndex () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        switch (index)
        {
            case 0:
                final boolean canScrollUp = this.offsetY + AbstractRaindropsView.NUM_OCTAVE <= this.getClip ().getNumRows () - AbstractRaindropsView.NUM_OCTAVE;
                return canScrollUp ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 1:
                final boolean canScrollDown = this.offsetY - AbstractRaindropsView.NUM_OCTAVE >= 0;
                return canScrollDown ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            default:
                return APCminiControlSurface.APC_BUTTON_STATE_OFF;
        }
    }
}