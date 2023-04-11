// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractDrumView;


/**
 * The Drum view.
 *
 * @author Jürgen Moßgraber
 */
public class DrumView extends AbstractDrumView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DrumView (final APCminiControlSurface surface, final IModel model)
    {
        super ("Drum", surface, model, 4, 4, false);
    }


    /** {@inheritDoc} */
    @Override
    protected String getPadContentColor (final IChannel drumPad)
    {
        return AbstractDrumView.COLOR_PAD_HAS_CONTENT;
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
                this.onLeft (event);
                break;
            case 3:
                this.onRight (event);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        final INoteClip clip = this.getClip ();

        switch (index)
        {
            case 0:
                return this.scales.canScrollDrumOctaveUp () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 1:
                return this.scales.canScrollDrumOctaveDown () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 2:
                return clip.doesExist () && clip.canScrollStepsBackwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 3:
                return clip.doesExist () && clip.canScrollStepsForwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            default:
                return APCminiControlSurface.APC_BUTTON_STATE_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final int res = 7 - index;
        return isKeyboardEnabled && res == this.getResolutionIndex () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }
}