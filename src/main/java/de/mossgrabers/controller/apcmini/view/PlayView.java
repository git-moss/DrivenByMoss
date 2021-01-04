// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.view;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
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
                this.updateScaleLayout ();
                break;
            case SCENE2:
                this.scales.prevScaleLayout ();
                this.updateScaleLayout ();
                break;
            case SCENE4:
                this.scales.prevScale ();
                this.updateScale ();
                break;
            case SCENE5:
                this.scales.nextScale ();
                this.updateScale ();
                break;
            case SCENE6:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
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
        this.updateNoteMapping ();
    }


    private void updateScaleLayout ()
    {
        this.updateNoteMapping ();
        final String name = this.scales.getScaleLayout ().getName ();
        this.surface.getConfiguration ().setScaleLayout (name);
        this.surface.getDisplay ().notify (name);
    }


    private void updateScale ()
    {
        final String name = this.scales.getScale ().getName ();
        this.surface.getConfiguration ().setScale (name);
        this.surface.getDisplay ().notify (name);
    }
}