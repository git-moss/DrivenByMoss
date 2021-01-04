// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apc.view;

import de.mossgrabers.controller.apc.APCConfiguration;
import de.mossgrabers.controller.apc.controller.APCControlSurface;
import de.mossgrabers.controller.apc.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractNoteSequencerView;


/**
 * The sequencer view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SequencerView extends AbstractNoteSequencerView<APCControlSurface, APCConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SequencerView (final APCControlSurface surface, final IModel model)
    {
        super ("Sequencer", surface, model, surface.isMkII ());

        this.numDisplayRows = 5;
        this.numSequencerRows = 4;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSequencerArea (final int index, final int x, final int y, final int velocity)
    {
        if (!this.isActive ())
            return;

        final ModeManager modeManager = this.surface.getModeManager ();

        if (velocity > 0)
        {
            // Turn on Note mode if an existing note is pressed
            final INoteClip cursorClip = this.getClip ();
            final int mappedNote = this.keyManager.map (y);
            final int editMidiChannel = this.configuration.getMidiEditChannel ();
            final int state = cursorClip.getStep (editMidiChannel, x, mappedNote).getState ();
            if (state == IStepInfo.NOTE_START)
            {
                final NoteMode noteMode = (NoteMode) modeManager.get (Modes.NOTE);
                noteMode.setValues (cursorClip, editMidiChannel, x, mappedNote);
                modeManager.setActive (Modes.NOTE);
            }
        }
        else
        {
            // Turn off Note mode
            if (modeManager.isActive (Modes.NOTE))
                modeManager.restore ();

            if (this.isNoteEdited)
            {
                this.isNoteEdited = false;
                return;
            }
        }

        super.handleSequencerArea (index, x, y, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN || !this.isActive ())
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.scales.nextScale ();
                this.notifyScale ();
                break;

            case SCENE2:
                this.scales.prevScale ();
                this.notifyScale ();
                break;

            case SCENE3:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                this.surface.getDisplay ().notify (isChromatic ? "Chromatic" : "In Key");
                break;

            case SCENE4:
                this.onOctaveUp (event);
                break;

            case SCENE5:
                this.onOctaveDown (event);
                break;

            default:
                // Not used
                break;
        }
        this.updateNoteMapping ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE3)
            return ColorManager.BUTTON_STATE_OFF;
        return this.isActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        super.updateNoteMapping ();
        this.updateScale ();
    }


    private void notifyScale ()
    {
        final String name = this.scales.getScale ().getName ();
        this.surface.getConfiguration ().setScale (name);
        this.surface.getDisplay ().notify (name);
    }
}