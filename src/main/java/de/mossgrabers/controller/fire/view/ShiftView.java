// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.fire.view;

import de.mossgrabers.controller.fire.FireConfiguration;
import de.mossgrabers.controller.fire.controller.FireControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractView;


/**
 * Simulates the missing buttons (in contrast to Fire Pro) on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<FireControlSurface, FireConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final FireControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        // Note Repeat
        final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

        // - on/off
        padGrid.light (60, DAWColor.getColorIndex ((noteRepeat.isActive () ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));

        // Octave
        final int octaves = noteRepeat.getOctaves ();
        for (int i = 0; i < 4; i++)
        {
            padGrid.light (36 + i, DAWColor.getColorIndex ((octaves == i ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
            padGrid.light (44 + i, DAWColor.getColorIndex ((octaves == 4 + i ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));
        }
        padGrid.light (52, DAWColor.getColorIndex ((octaves == 8 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_GRAY).getColor ()));

        padGrid.light (53, 0);
        padGrid.light (54, 0);
        padGrid.light (55, 0);
        padGrid.light (61, 0);

        // Dec/Inc Arp Mode
        padGrid.light (50, DAWColor.getColorIndex (ColorEx.WHITE));
        padGrid.light (51, DAWColor.getColorIndex (ColorEx.WHITE));

        // Note Repeat period
        final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
        padGrid.light (64, DAWColor.getColorIndex ((periodIndex == 0 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (56, DAWColor.getColorIndex ((periodIndex == 2 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (48, DAWColor.getColorIndex ((periodIndex == 4 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (40, DAWColor.getColorIndex ((periodIndex == 6 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));

        padGrid.light (65, DAWColor.getColorIndex ((periodIndex == 1 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (57, DAWColor.getColorIndex ((periodIndex == 3 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (49, DAWColor.getColorIndex ((periodIndex == 5 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (41, DAWColor.getColorIndex ((periodIndex == 7 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));

        // Note Repeat length
        final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
        padGrid.light (66, DAWColor.getColorIndex ((lengthIndex == 0 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (58, DAWColor.getColorIndex ((lengthIndex == 2 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (50, DAWColor.getColorIndex ((lengthIndex == 4 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));
        padGrid.light (42, DAWColor.getColorIndex ((lengthIndex == 6 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_BLUE).getColor ()));

        padGrid.light (67, DAWColor.getColorIndex ((lengthIndex == 1 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (59, DAWColor.getColorIndex ((lengthIndex == 3 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (51, DAWColor.getColorIndex ((lengthIndex == 5 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));
        padGrid.light (43, DAWColor.getColorIndex ((lengthIndex == 7 ? DAWColor.DAW_COLOR_GREEN : DAWColor.DAW_COLOR_PINK).getColor ()));

        // New clip length
        final int clipLengthIndex = this.surface.getConfiguration ().getNewClipLength ();
        for (int i = 0; i < 8; i++)
            padGrid.light (68 + i, DAWColor.getColorIndex ((i == clipLengthIndex ? DAWColor.DAW_COLOR_RED : DAWColor.DAW_COLOR_LIGHT_ORANGE).getColor ()));

        // Not used
        for (int i = 0; i < 21; i++)
            padGrid.light (76 + i, 0);

        // Add tracks
        padGrid.light (97, DAWColor.getColorIndex (ColorEx.ORANGE));
        padGrid.light (98, DAWColor.getColorIndex (ColorEx.BLUE));
        padGrid.light (99, DAWColor.getColorIndex (ColorEx.PINK));
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final FireConfiguration configuration = this.surface.getConfiguration ();

        switch (note)
        {
            case 36:
            case 37:
            case 38:
            case 39:
                this.setNoteRepeatOctave (note - 36);
                break;
            case 44:
            case 45:
            case 46:
            case 47:
                this.setNoteRepeatOctave (note - 40);
                break;
            case 52:
                this.setNoteRepeatOctave (note - 44);
                break;

            case 62:
                configuration.setNoteRepeatMode (configuration.prevArpeggiatorMode ());
                break;
            case 63:
                configuration.setNoteRepeatMode (configuration.nextArpeggiatorMode ());
                break;

            case 60:
                configuration.toggleNoteRepeatActive ();
                this.mvHelper.delayDisplay ( () -> "Note Repeat: " + (configuration.isNoteRepeatActive () ? "Active" : "Off"));
                break;

            case 64:
                this.setPeriod (0);
                break;
            case 65:
                this.setPeriod (1);
                break;
            case 56:
                this.setPeriod (2);
                break;
            case 57:
                this.setPeriod (3);
                break;
            case 48:
                this.setPeriod (4);
                break;
            case 49:
                this.setPeriod (5);
                break;
            case 40:
                this.setPeriod (6);
                break;
            case 41:
                this.setPeriod (7);
                break;

            case 66:
                this.setNoteLength (0);
                break;
            case 67:
                this.setNoteLength (1);
                break;
            case 58:
                this.setNoteLength (2);
                break;
            case 59:
                this.setNoteLength (3);
                break;
            case 50:
                this.setNoteLength (4);
                break;
            case 51:
                this.setNoteLength (5);
                break;
            case 42:
                this.setNoteLength (6);
                break;
            case 43:
                this.setNoteLength (7);
                break;

            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
                final int newClipLength = note - 68;
                configuration.setNewClipLength (newClipLength);
                this.surface.getDisplay ().notify ("New clip length: " + AbstractConfiguration.getNewClipLengthValue (newClipLength));
                break;

            case 97:
                this.model.getApplication ().addInstrumentTrack ();
                return;
            case 98:
                this.model.getApplication ().addAudioTrack ();
                return;
            case 99:
                this.model.getApplication ().addEffectTrack ();
                return;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        switch (buttonID)
        {
            case SCENE1:
            case SCENE2:
            case SCENE3:
                return 1;
            case SCENE4:
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (buttonID)
        {
            case SCENE1:
                this.model.getApplication ().undo ();
                break;
            case SCENE2:
                this.model.getApplication ().redo ();
                break;
            case SCENE3:
                this.model.getClip ().quantize (1);
                break;
            case SCENE4:
                // Note used
                break;
            default:
                // Not used
                break;
        }
    }


    private void setPeriod (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatPeriod (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Period: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteLength (final int index)
    {
        this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
        this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (index)), 100);
    }


    private void setNoteRepeatOctave (final int octave)
    {
        final FireConfiguration configuration = this.surface.getConfiguration ();
        configuration.setNoteRepeatOctave (octave);
        this.mvHelper.delayDisplay ( () -> "Octave: " + configuration.getNoteRepeatOctave ());
    }
}