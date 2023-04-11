// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.mk3.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.mk3.MaschineConfiguration;
import de.mossgrabers.controller.ni.maschine.mk3.controller.MaschineControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;


/**
 * View to select the note repeat settings.
 *
 * @author Jürgen Moßgraber
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
    protected void executeFunction (final int padIndex, final ButtonEvent buttonEvent)
    {
        if (buttonEvent != ButtonEvent.DOWN)
            return;

        final IHost host = this.model.getHost ();
        final MaschineConfiguration configuration = this.surface.getConfiguration ();

        switch (padIndex)
        {
            case 6:
            case 7:
                if (host.supports (Capability.NOTE_REPEAT_MODE))
                {
                    final ArpeggiatorMode arpMode = configuration.getNoteRepeatMode ();
                    final int modeIndex = configuration.lookupArpeggiatorModeIndex (arpMode);
                    final boolean increase = padIndex == 7;
                    final List<ArpeggiatorMode> modes = configuration.getArpeggiatorModes ();
                    final int newIndex = Math.max (0, Math.min (modes.size () - 1, modeIndex + (increase ? 1 : -1)));
                    configuration.setNoteRepeatMode (modes.get (newIndex));
                    this.mvHelper.delayDisplay ( () -> "Mode: " + configuration.getNoteRepeatMode ().getName ());
                }
                break;

            case 8:
            case 9:
                if (host.supports (Capability.NOTE_REPEAT_LENGTH))
                {
                    final int sel2 = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatLength ().getValue ()), padIndex == 9);
                    configuration.setNoteRepeatLength (Resolution.values ()[sel2]);
                    this.surface.scheduleTask ( () -> this.surface.getDisplay ().notify ("Note Length: " + Resolution.getNameAt (sel2)), 100);
                }
                break;

            case 12:
            case 13:
                final int sel = Resolution.change (Resolution.getMatch (configuration.getNoteRepeatPeriod ().getValue ()), padIndex == 13);
                configuration.setNoteRepeatPeriod (Resolution.values ()[sel]);
                this.mvHelper.delayDisplay ( () -> "Period: " + Resolution.getNameAt (sel));
                break;

            case 14:
            case 15:
                if (host.supports (Capability.NOTE_REPEAT_OCTAVES))
                {
                    configuration.setNoteRepeatOctave (configuration.getNoteRepeatOctave () + (padIndex == 15 ? 1 : -1));
                    this.mvHelper.delayDisplay ( () -> "Octaves: " + configuration.getNoteRepeatOctave ());
                }
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
        for (int i = 36; i < 42; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 42; i < 44; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLUE);
        for (int i = 44; i < 46; i++)
            padGrid.light (i, MaschineColorManager.COLOR_GREEN);
        for (int i = 46; i < 48; i++)
            padGrid.light (i, MaschineColorManager.COLOR_BLACK);
        for (int i = 48; i < 50; i++)
            padGrid.light (i, MaschineColorManager.COLOR_ROSE);
        for (int i = 50; i < 52; i++)
            padGrid.light (i, MaschineColorManager.COLOR_YELLOW);
    }
}