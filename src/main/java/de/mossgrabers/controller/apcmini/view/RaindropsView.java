// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.view;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractRaindropsView;


/**
 * Raindrops view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
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
        if (event != ButtonEvent.DOWN)
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
    public void updateSceneButtons ()
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, isKeyboardEnabled && i == 7 - this.selectedIndex ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);

        this.canScrollUp = this.offsetY + AbstractRaindropsView.NUM_OCTAVE <= this.getClip ().getNumRows () - AbstractRaindropsView.NUM_OCTAVE;
        this.canScrollDown = this.offsetY - AbstractRaindropsView.NUM_OCTAVE >= 0;
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1, this.canScrollUp ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON2, this.canScrollDown ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);

        for (int i = 0; i < 6; i++)
            this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON3 + i, APCminiControlSurface.APC_BUTTON_STATE_OFF);
    }
}