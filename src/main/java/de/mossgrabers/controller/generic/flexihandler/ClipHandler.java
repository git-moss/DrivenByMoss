// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.ISlot;

import java.util.Optional;


/**
 * The handler for clip commands.
 *
 * @author Jürgen Moßgraber
 */
public class ClipHandler extends AbstractHandler
{
    private final NewCommand<GenericFlexiControlSurface, GenericFlexiConfiguration> newCommand;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    public ClipHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);

        this.newCommand = new NewCommand<> (this.model, this.surface);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.CLIP_TOGGLE_PIN,
            FlexiCommand.CLIP_PREVIOUS,
            FlexiCommand.CLIP_NEXT,
            FlexiCommand.CLIP_SCROLL,
            FlexiCommand.CLIP_PLAY,
            FlexiCommand.CLIP_PLAY_ALT,
            FlexiCommand.CLIP_STOP,
            FlexiCommand.CLIP_RECORD,
            FlexiCommand.CLIP_NEW,
            FlexiCommand.CLIP_QUANTIZE
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        final Optional<ISlot> selectedSlot = this.model.getSelectedSlot ();
        switch (command)
        {
            case CLIP_TOGGLE_PIN:
                return this.model.getCursorClip ().isPinned () ? 127 : 0;

            case CLIP_PLAY, CLIP_PLAY_ALT:
                return selectedSlot.isPresent () && selectedSlot.get ().isPlaying () ? 127 : 0;

            case CLIP_STOP:
                return selectedSlot.isPresent () && selectedSlot.get ().isPlaying () ? 0 : 127;

            case CLIP_RECORD:
                return selectedSlot.isPresent () && selectedSlot.get ().isRecording () ? 127 : 0;

            default:
                return -1;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            case CLIP_TOGGLE_PIN:
                if (isButtonPressed)
                    this.model.getCursorClip ().togglePinned ();
                break;

            case CLIP_PREVIOUS:
                if (isButtonPressed)
                    this.scrollClipLeft ();
                break;

            case CLIP_NEXT:
                if (isButtonPressed)
                    this.scrollClipRight ();
                break;

            case CLIP_SCROLL:
                this.scrollClips (knobMode, value);
                break;

            case CLIP_PLAY, CLIP_PLAY_ALT:
                final Optional<ISlot> selectedSlot = this.model.getSelectedSlot ();
                if (selectedSlot.isPresent ())
                    selectedSlot.get ().launch (isButtonPressed, command == FlexiCommand.CLIP_PLAY_ALT);
                break;

            case CLIP_STOP:
                if (isButtonPressed)
                    this.model.getCursorTrack ().stop ();
                break;

            case CLIP_RECORD:
                if (isButtonPressed)
                {
                    final Optional<ISlot> selectedSlot2 = this.model.getSelectedSlot ();
                    if (selectedSlot2.isPresent ())
                        selectedSlot2.get ().startRecording ();
                }
                break;

            case CLIP_NEW:
                if (isButtonPressed)
                    this.newCommand.execute ();
                break;

            case CLIP_QUANTIZE:
                if (isButtonPressed)
                {
                    final IClip clip = this.model.getCursorClip ();
                    if (clip.doesExist ())
                        clip.quantize (1);
                }
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }


    private void scrollClips (final KnobMode knobMode, final MidiValue value)
    {
        if (isAbsolute (knobMode) || !this.increaseKnobMovement ())
            return;

        if (this.isIncrease (knobMode, value))
            this.scrollClipRight ();
        else
            this.scrollClipLeft ();
    }


    private void scrollClipLeft ()
    {
        this.model.getCursorTrack ().getSlotBank ().selectPreviousItem ();
    }


    private void scrollClipRight ()
    {
        this.model.getCursorTrack ().getSlotBank ().selectNextItem ();
    }
}
