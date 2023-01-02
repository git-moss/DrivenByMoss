// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * Simulates the missing buttons (in contrast to MaschineJam Pro) on the grid.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatView extends AbstractView<MaschineJamControlSurface, MaschineJamConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public NoteRepeatView (final MaschineJamControlSurface surface, final IModel model)
    {
        super ("Note Repeat", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final IHost host = this.model.getHost ();

        // Note Repeat
        final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

        if (host.supports (Capability.NOTE_REPEAT_OCTAVES))
        {
            final int octaves = noteRepeat.getOctaves ();
            for (int i = 0; i < 8; i++)
                padGrid.light (36 + i, i == octaves ? MaschineColorManager.COLOR_GREEN : MaschineColorManager.COLOR_GREY);
        }
        else
        {
            for (int i = 36; i < 44; i++)
                padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        }

        for (int i = 44; i < 52; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);

        // Note Repeat period
        final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
        padGrid.light (79, periodIndex == 0 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);
        padGrid.light (71, periodIndex == 2 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);
        padGrid.light (63, periodIndex == 4 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);
        padGrid.light (55, periodIndex == 6 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);

        padGrid.light (80, periodIndex == 1 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
        padGrid.light (72, periodIndex == 3 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
        padGrid.light (64, periodIndex == 5 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
        padGrid.light (56, periodIndex == 7 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);

        // Note Repeat length
        if (host.supports (Capability.NOTE_REPEAT_LENGTH))
        {
            final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
            padGrid.light (81, lengthIndex == 0 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);
            padGrid.light (73, lengthIndex == 2 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);
            padGrid.light (65, lengthIndex == 4 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);
            padGrid.light (57, lengthIndex == 6 ? MaschineColorManager.COLOR_SKY : MaschineColorManager.COLOR_SKY_LO);

            padGrid.light (82, lengthIndex == 1 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
            padGrid.light (74, lengthIndex == 3 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
            padGrid.light (66, lengthIndex == 5 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
            padGrid.light (58, lengthIndex == 7 ? MaschineColorManager.COLOR_PINK : MaschineColorManager.COLOR_PINK_LO);
        }
        else
        {
            padGrid.light (81, MaschineColorManager.COLOR_BLACK);
            padGrid.light (73, MaschineColorManager.COLOR_BLACK);
            padGrid.light (65, MaschineColorManager.COLOR_BLACK);
            padGrid.light (57, MaschineColorManager.COLOR_BLACK);
            padGrid.light (82, MaschineColorManager.COLOR_BLACK);
            padGrid.light (74, MaschineColorManager.COLOR_BLACK);
            padGrid.light (66, MaschineColorManager.COLOR_BLACK);
            padGrid.light (58, MaschineColorManager.COLOR_BLACK);
        }

        for (int i = 52; i < 55; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 59; i < 63; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 67; i < 71; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);

        padGrid.light (75, MaschineColorManager.COLOR_BLACK);

        if (host.supports (Capability.NOTE_REPEAT_MODE))
        {
            // Note Repeat Octave
            padGrid.light (76, MaschineColorManager.COLOR_LIME);
            padGrid.light (77, MaschineColorManager.COLOR_LIME);
        }
        else
        {
            padGrid.light (76, MaschineColorManager.COLOR_BLACK);
            padGrid.light (77, MaschineColorManager.COLOR_BLACK);
        }

        for (int i = 78; i < 79; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 83; i < 100; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final IHost host = this.model.getHost ();
        final MaschineJamConfiguration configuration = this.surface.getConfiguration ();

        switch (note)
        {
            // Note Repeat Octave
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
                if (host.supports (Capability.NOTE_REPEAT_OCTAVES))
                {
                    final int octave = note - 36;
                    configuration.setNoteRepeatOctave (octave);
                    this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Repeat Octave: " + octave), 100);
                }
                return;

            // Arpeggiator type
            case 76:
            case 77:
                if (host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    configuration.setPrevNextNoteRepeatMode (note == 77);
                    this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Repeat Mode: " + configuration.getNoteRepeatMode ().getName ()), 100);
                }
                break;

            // Note Repeat period
            case 79:
                this.setPeriod (0);
                return;
            case 80:
                this.setPeriod (1);
                return;
            case 71:
                this.setPeriod (2);
                return;
            case 72:
                this.setPeriod (3);
                return;
            case 63:
                this.setPeriod (4);
                return;
            case 64:
                this.setPeriod (5);
                return;
            case 55:
                this.setPeriod (6);
                return;
            case 56:
                this.setPeriod (7);
                return;

            // Note Repeat Length
            case 81:
                this.setNoteLength (0);
                return;
            case 82:
                this.setNoteLength (1);
                return;
            case 73:
                this.setNoteLength (2);
                return;
            case 74:
                this.setNoteLength (3);
                return;
            case 65:
                this.setNoteLength (4);
                return;
            case 66:
                this.setNoteLength (5);
                return;
            case 57:
                this.setNoteLength (6);
                return;
            case 58:
                this.setNoteLength (7);
                return;

            default:
                // Fall through to be handled below
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
        if (this.model.getHost ().supports (Capability.NOTE_REPEAT_LENGTH))
        {
            this.surface.getConfiguration ().setNoteRepeatLength (Resolution.values ()[index]);
            this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (index)), 100);
        }
    }
}