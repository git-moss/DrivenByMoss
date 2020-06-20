// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.maschine.view;

import de.mossgrabers.controller.maschine.controller.MaschineColorManager;
import de.mossgrabers.controller.maschine.controller.MaschineControlSurface;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * View to select the note repeat settings.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatView extends BaseView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public NoteRepeatView (final MaschineControlSurface surface, final IModel model)
    {
        super ("Note Repeat", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void executeFunction (final int padIndex)
    {

        switch (padIndex)
        {
            case 12:
                this.setPeriod (0);
                break;
            case 13:
                this.setPeriod (1);
                break;
            case 8:
                this.setPeriod (2);
                break;
            case 9:
                this.setPeriod (3);
                break;
            case 4:
                this.setPeriod (4);
                break;
            case 5:
                this.setPeriod (5);
                break;
            case 0:
                this.setPeriod (6);
                break;
            case 1:
                this.setPeriod (7);
                break;

            case 14:
                this.setNoteLength (0);
                break;
            case 15:
                this.setNoteLength (1);
                break;
            case 10:
                this.setNoteLength (2);
                break;
            case 11:
                this.setNoteLength (3);
                break;
            case 6:
                this.setNoteLength (4);
                break;
            case 7:
                this.setNoteLength (5);
                break;
            case 2:
                this.setNoteLength (6);
                break;
            case 3:
                this.setNoteLength (7);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        // Note Repeat
        final INoteRepeat noteRepeat = this.surface.getMidiInput ().getDefaultNoteInput ().getNoteRepeat ();

        // Note Repeat period
        final int periodIndex = Resolution.getMatch (noteRepeat.getPeriod ());
        padGrid.lightEx (0, 0, periodIndex == 0 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);
        padGrid.lightEx (0, 1, periodIndex == 2 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);
        padGrid.lightEx (0, 2, periodIndex == 4 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);
        padGrid.lightEx (0, 3, periodIndex == 6 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);

        padGrid.lightEx (1, 0, periodIndex == 1 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
        padGrid.lightEx (1, 1, periodIndex == 3 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
        padGrid.lightEx (1, 2, periodIndex == 5 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
        padGrid.lightEx (1, 3, periodIndex == 7 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);

        // Note Repeat length
        final int lengthIndex = Resolution.getMatch (noteRepeat.getNoteLength ());
        padGrid.lightEx (2, 0, lengthIndex == 0 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);
        padGrid.lightEx (2, 1, lengthIndex == 2 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);
        padGrid.lightEx (2, 2, lengthIndex == 4 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);
        padGrid.lightEx (2, 3, lengthIndex == 6 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_SKY_LO);

        padGrid.lightEx (3, 0, lengthIndex == 1 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
        padGrid.lightEx (3, 1, lengthIndex == 3 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
        padGrid.lightEx (3, 2, lengthIndex == 5 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
        padGrid.lightEx (3, 3, lengthIndex == 7 ? MaschineColorManager.COLOR_WHITE : MaschineColorManager.COLOR_PINK_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final Configuration configuration = this.surface.getConfiguration ();
        final int modeIndex = configuration.lookupArpeggiatorModeIndex (configuration.getNoteRepeatMode ());
        final ArpeggiatorMode [] modes = configuration.getArpeggiatorModes ();
        final IDisplay display = this.surface.getDisplay ();

        switch (index)
        {
            case 0:
                configuration.setNoteRepeatMode (modes[Math.max (0, Math.min (modes.length - 1, modeIndex - 1))]);
                this.surface.scheduleTask ( () -> display.notify ("Mode: " + configuration.getNoteRepeatMode ().getName ()), 100);
                break;
            case 1:
                configuration.setNoteRepeatMode (modes[Math.max (0, Math.min (modes.length - 1, modeIndex + 1))]);
                this.surface.scheduleTask ( () -> display.notify ("Mode: " + configuration.getNoteRepeatMode ().getName ()), 100);
                break;
            case 2:
                configuration.setNoteRepeatOctave (configuration.getNoteRepeatOctave () - 1);
                this.surface.scheduleTask ( () -> display.notify ("Octave: " + configuration.getNoteRepeatOctave ()), 100);
                break;
            case 3:
                configuration.setNoteRepeatOctave (configuration.getNoteRepeatOctave () + 1);
                this.surface.scheduleTask ( () -> display.notify ("Octave: " + configuration.getNoteRepeatOctave ()), 100);
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
}