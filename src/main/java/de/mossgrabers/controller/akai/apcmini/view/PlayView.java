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
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    private final TrackButtons trackButtons;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     * @param trackButtons The track button control
     */
    public PlayView (final APCminiControlSurface surface, final IModel model, final TrackButtons trackButtons)
    {
        super (surface, model, false);

        this.trackButtons = trackButtons;
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        this.trackButtons.onSelectTrack (index, event);
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        return this.trackButtons.getTrackButtonColor (index);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        return ButtonID.SCENE3 == buttonID ? ColorManager.BUTTON_STATE_OFF : ColorManager.BUTTON_STATE_ON;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.model.canSelectedTrackHoldNotes ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.scales.nextScaleLayout ();
                this.surface.getDisplay ().notify ("Scale layout: " + this.scales.getScaleLayout ().getName ());
                break;
            case SCENE2:
                this.scales.prevScaleLayout ();
                this.surface.getDisplay ().notify ("Scale layout: " + this.scales.getScaleLayout ().getName ());
                break;
            case SCENE4:
                this.scales.prevScale ();
                this.surface.getDisplay ().notify (this.scales.getScale ().getName ());
                break;
            case SCENE5:
                this.scales.nextScale ();
                this.surface.getDisplay ().notify (this.scales.getScale ().getName ());
                break;
            case SCENE6:
                this.scales.toggleChromatic ();
                this.surface.getDisplay ().notify (this.scales.isChromatic () ? "Chromatic" : "In Key");
                break;
            case SCENE7:
                this.onOctaveUp (event);
                break;
            case SCENE8:
                this.onOctaveDown (event);
                break;
            default:
                // Not used
                break;
        }
        this.updateScale ();
    }
}