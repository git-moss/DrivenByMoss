// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.akai.apcmini.definition.IAPCminiControllerDefinition;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.sequencer.AbstractNoteSequencerView;


/**
 * The Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class SequencerView extends AbstractNoteSequencerView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    private final IAPCminiControllerDefinition definition;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     * @param definition The APCmini definition
     */
    public SequencerView (final APCminiControlSurface surface, final IModel model, final boolean useTrackColor, final IAPCminiControllerDefinition definition)
    {
        super ("Sequencer", surface, model, useTrackColor);

        this.definition = definition;
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || !this.isActive ())
            return;

        switch (this.definition.swapShiftedTrackIndices (index))
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
    public int getTrackButtonColor (final int index)
    {
        final int octave = this.scales.getOctave ();
        final INoteClip clip = this.getClip ();
        switch (this.definition.swapShiftedTrackIndices (index))
        {
            case 0:
                return octave < Scales.OCTAVE_RANGE ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 1:
                return octave > -Scales.OCTAVE_RANGE ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
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
        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final int res = 7 - index;
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        return isKeyboardEnabled && res == this.getResolutionIndex () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }
}