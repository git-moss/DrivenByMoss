// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.view;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.SceneView;


/**
 * The Play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<APCminiControlSurface, APCminiConfiguration> implements APCminiView, SceneView
{
    private TrackButtons extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public PlayView (final APCminiControlSurface surface, final IModel model)
    {
        super (surface, model, false);

        this.extensions = new TrackButtons (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        this.extensions.onSelectTrack (index, event);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, i == 2 ? APCminiControlSurface.APC_BUTTON_STATE_OFF : APCminiControlSurface.APC_BUTTON_STATE_ON);

        this.extensions.updateTrackButtons ();
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (!this.model.canSelectedTrackHoldNotes ())
            return;
        switch (scene)
        {
            case 0:
                this.scales.nextScaleLayout ();
                this.updateScaleLayout ();
                break;
            case 1:
                this.scales.prevScaleLayout ();
                this.updateScaleLayout ();
                break;
            case 3:
                this.scales.prevScale ();
                this.updateScale ();
                break;
            case 4:
                this.scales.nextScale ();
                this.updateScale ();
                break;
            case 5:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
                break;
            case 6:
                this.onOctaveUp (event);
                break;
            case 7:
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