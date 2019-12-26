// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;


/**
 * The 8 lane drum sequencer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView8 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 8;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView8 (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Drum 8", surface, model, 1, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        final int sound = y + this.soundOffset;
        final int col = x;

        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        this.getClip ().toggleStep (editMidiChannel, col, this.scales.getDrumOffset () + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Clip length/loop area
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % DrumView8.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        for (int sound = 0; sound < 8; sound++)
        {
            for (int col = 0; col < DrumView8.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (editMidiChannel, col, offsetY + this.selectedPad + sound + this.soundOffset).getState ();
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                y += sound;
                padGrid.lightEx (x, 7 - y, isSet > 0 ? hilite ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_LO : LaunchpadColorManager.LAUNCHPAD_COLOR_BLUE_HI : hilite ? LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN_HI : LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (EMPTY_TABLE));
    }


    /** {@inheritDoc} */
    @Override
    protected int updateLowerSceneButtons (final int scene)
    {
        if (this.isActive ())
        {
            if (scene == 7 && this.soundOffset == 0 || scene == 6 && this.soundOffset == 8)
                return LaunchpadColorManager.LAUNCHPAD_COLOR_YELLOW;
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREEN;
        }
        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        // 7, 6, 5, 4
        if (index < 6)
            return;
        this.soundOffset = index == 7 ? 0 : 8;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }
}