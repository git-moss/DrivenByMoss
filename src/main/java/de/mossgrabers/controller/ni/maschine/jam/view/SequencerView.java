// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.view.IMaschineView;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.INoteClip;
import de.mossgrabers.framework.daw.clip.NotePosition;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;
import de.mossgrabers.framework.view.sequencer.AbstractNoteSequencerView;


/**
 * The Sequencer view.
 *
 * @author Jürgen Moßgraber
 */
public class SequencerView extends AbstractNoteSequencerView<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineView, IViewNavigation
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final MaschineJamControlSurface surface, final IModel model)
    {
        super (Views.NAME_SEQUENCER, surface, model, true);
    }


    /** {@inheritDoc} */
    @Override
    public void changeOption (final EncoderMode temporaryEncoderMode, final int control)
    {
        this.keyManager.clearPressedKeys ();

        final boolean increase = this.model.getValueChanger ().isIncrease (control);

        switch (temporaryEncoderMode)
        {
            case TEMPORARY_PERFORM:
                if (increase)
                    this.scales.nextScale ();
                else
                    this.scales.prevScale ();
                this.mvHelper.delayDisplay ( () -> "Scale: " + this.scales.getScale ().getName ());
                break;

            case TEMPORARY_NOTES:
                if (increase)
                    this.scales.nextScaleOffset ();
                else
                    this.scales.prevScaleOffset ();
                this.mvHelper.delayDisplay ( () -> "Scale Offset: " + Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
                break;

            case TEMPORARY_LOCK:
                this.scales.toggleChromatic ();
                this.mvHelper.delayDisplay ( () -> "Chromatic: " + (this.scales.isChromatic () ? "On" : "Off"));
                break;

            case TEMPORARY_TUNE:
                if (increase)
                    this.onOctaveUp (ButtonEvent.DOWN);
                else
                    this.onOctaveDown (ButtonEvent.DOWN);
                break;

            default:
                // Not used
                break;
        }

        this.updateScaleConfig ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        switch (buttonID)
        {
            case ARROW_LEFT:
                this.onLeft (event);
                break;
            case ARROW_RIGHT:
                this.onRight (event);
                break;

            case ARROW_UP:
                this.onOctaveUp (event);
                break;
            case ARROW_DOWN:
                this.onOctaveDown (event);
                break;

            default:
                super.onButton (buttonID, event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScroll (final Direction direction)
    {
        final INoteClip clip = this.getClip ();
        switch (direction)
        {
            case LEFT:
                return clip.canScrollStepsBackwards ();
            case RIGHT:
                return clip.canScrollStepsForwards ();
            case UP:
                return this.isOctaveUpButtonOn ();
            case DOWN:
                return this.isOctaveDownButtonOn ();
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSequencerAreaButtonCombinations (final INoteClip clip, final NotePosition notePosition, final int row, final int velocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();
        if (isSelectPressed)
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, notePosition, velocity, isSelectPressed);
            return true;
        }

        return super.handleSequencerAreaButtonCombinations (clip, notePosition, row, velocity);
    }


    private void updateScaleConfig ()
    {
        final MaschineJamConfiguration config = this.surface.getConfiguration ();
        config.setScale (this.scales.getScale ().getName ());
        config.setScaleBase (Scales.BASES.get (this.scales.getScaleOffsetIndex ()));
        config.setScaleInKey (!this.scales.isChromatic ());
        config.setScaleLayout (this.scales.getScaleLayout ().getName ());

        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean wasAlternateInteractionUsed ()
    {
        // Only used as marker interface
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void setAlternateInteractionUsed (final boolean wasUsed)
    {
        // Only used as marker interface
    }
}