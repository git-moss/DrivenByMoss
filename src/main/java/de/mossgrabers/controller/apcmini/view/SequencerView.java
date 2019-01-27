// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini.view;

import de.mossgrabers.controller.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;


/**
 * The Sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public SequencerView (final APCminiControlSurface surface, final IModel model)
    {
        super ("Sequencer", surface, model, false);
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
                this.onLeft (event);
                break;
            case 3:
                this.onRight (event);
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

        final int octave = this.scales.getOctave ();
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1, octave < Scales.OCTAVE_RANGE ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON2, octave > -Scales.OCTAVE_RANGE ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);

        final INoteClip clip = this.getClip ();
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON3, clip != null && clip.canScrollStepsBackwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON4, clip != null && clip.canScrollStepsForwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF);
        for (int i = 0; i < 4; i++)
            this.surface.updateButton (APCminiControlSurface.APC_BUTTON_TRACK_BUTTON5 + i, APCminiControlSurface.APC_BUTTON_STATE_OFF);
    }
}