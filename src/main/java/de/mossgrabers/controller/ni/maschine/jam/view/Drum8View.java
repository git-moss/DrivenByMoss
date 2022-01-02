// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.core.view.IMaschineView;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrum8View;


/**
 * The 8 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Drum8View extends AbstractDrum8View<MaschineJamControlSurface, MaschineJamConfiguration> implements IMaschineView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum8View (final MaschineJamControlSurface surface, final IModel model)
    {
        super (surface, model, true);
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
                // Not used
                break;

            case TEMPORARY_NOTES:
                // Not used
                break;

            case TEMPORARY_LOCK:
                // Not used
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
    protected boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity, final int accentVelocity)
    {
        final boolean isSelectPressed = this.surface.isSelectPressed ();
        if (isSelectPressed)
        {
            if (velocity > 0)
                this.handleSequencerAreaRepeatOperator (clip, channel, step, note, velocity, isSelectPressed);
            return true;
        }

        return super.handleNoteAreaButtonCombinations (clip, channel, step, row, note, velocity, accentVelocity);
    }
}