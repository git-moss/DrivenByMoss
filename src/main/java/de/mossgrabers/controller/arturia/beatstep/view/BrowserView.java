// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The Browser view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public BrowserView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        final int steps = Math.abs (this.model.getValueChanger ().calcSteppedKnobChange (value));

        int column;
        switch (index)
        {
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                column = index - 8;
                if (isTurnedRight)
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectNextFilterItem (column);
                }
                else
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectPreviousFilterItem (column);
                }
                break;

            case 15:
                if (isTurnedRight)
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectNextResult ();
                }
                else
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectPreviousResult ();
                }
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        switch (note - 36)
        {
            // Cancel
            case 0:
                if (velocity == 0)
                    return;
                browser.stopBrowsing (false);
                this.surface.getViewManager ().restore ();
                break;

            // OK
            case 7:
                if (velocity == 0)
                    return;
                browser.stopBrowsing (true);
                this.surface.getViewManager ().restore ();
                break;

            // Notes for preview
            case 2:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 12, velocity);
                break;
            case 3:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 24, velocity);
                break;
            case 4:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 36, velocity);
                break;
            case 5:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 48, velocity);
                break;
            case 10:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 60, velocity);
                break;
            case 11:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 72, velocity);
                break;
            case 12:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 84, velocity);
                break;
            case 13:
                this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, 96, velocity);
                break;

            // Not used
            default:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 2; i < 6; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (44, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (45, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 10; i < 14; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (50, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (51, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
    }
}