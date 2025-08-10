// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * A view for mixing with track select, mute, solo, record arm, stop clip, volume and panning as
 * well as 4 rows of clips.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneMixAndSessionView extends AbstractSessionView<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneMixAndSessionView (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Track Mixer", surface, model, 4, 16, true);

        final int redLo = OxiOneColorManager.OXI_ONE_COLOR_DARKER_RED;
        final int redHi = OxiOneColorManager.OXI_ONE_COLOR_RED;
        final int black = OxiOneColorManager.OXI_ONE_COLOR_BLACK;
        final int white = OxiOneColorManager.OXI_ONE_COLOR_WHITE;
        final int green = OxiOneColorManager.OXI_ONE_COLOR_GREEN;
        final int amber = OxiOneColorManager.OXI_ONE_COLOR_ORANGE;
        final int gray = OxiOneColorManager.OXI_ONE_COLOR_GRAY;
        final LightInfo isRecording = new LightInfo (redHi, -1, false);
        final LightInfo isRecordingQueued = new LightInfo (redHi, black, true);
        final LightInfo isPlaying = new LightInfo (green, -1, false);
        final LightInfo isPlayingQueued = new LightInfo (green, green, true);
        final LightInfo isStopQueued = new LightInfo (green, green, true);
        final LightInfo hasContent = new LightInfo (amber, white, false);
        final LightInfo noContent = new LightInfo (black, -1, false);
        final LightInfo recArmed = new LightInfo (redLo, -1, false);
        final LightInfo isMuted = new LightInfo (gray, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, isStopQueued, hasContent, noContent, recArmed, isMuted);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final boolean colorTrackStates = this.surface.getConfiguration ().isColorTrackStates ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final ITrack track = tb.getItem (i);
            if (track.doesExist ())
            {
                // Select
                final int colorIndex = this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ()));
                if (track.isSelected ())
                    padGrid.lightEx (i, 0, colorIndex, OxiOneColorManager.OXI_ONE_COLOR_WHITE, false);
                else
                    padGrid.lightEx (i, 0, colorIndex);

                // Mute
                padGrid.lightEx (i, 1, getTrackStateColor (track.isMute (), colorTrackStates, OxiOneColorManager.OXI_ONE_COLOR_YELLOW, OxiOneColorManager.OXI_ONE_COLOR_DARKER_YELLOW));
                // Solo
                padGrid.lightEx (i, 2, getTrackStateColor (track.isSolo (), colorTrackStates, OxiOneColorManager.OXI_ONE_COLOR_BLUE, OxiOneColorManager.OXI_ONE_COLOR_DARKER_BLUE));
                // Record Arm
                padGrid.lightEx (i, 3, getTrackStateColor (track.isRecArm (), colorTrackStates, OxiOneColorManager.OXI_ONE_COLOR_RED, OxiOneColorManager.OXI_ONE_COLOR_DARKER_RED));
            }
            else
            {
                padGrid.lightEx (i, 3, OxiOneColorManager.OXI_ONE_COLOR_BLACK);
                padGrid.lightEx (i, 2, OxiOneColorManager.OXI_ONE_COLOR_BLACK);
                padGrid.lightEx (i, 1, OxiOneColorManager.OXI_ONE_COLOR_BLACK);
                padGrid.lightEx (i, 0, OxiOneColorManager.OXI_ONE_COLOR_BLACK);
            }
        }

        super.drawGrid ();
    }


    private static int getTrackStateColor (final boolean state, final boolean colorTrackStates, final int activeColor, final int inActiveColor)
    {
        if (state)
            return activeColor;
        return colorTrackStates ? inActiveColor : OxiOneColorManager.OXI_ONE_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        final int row = note / 16;
        if (row < 4)
        {
            super.onGridNote (note, velocity);
        }
        else
        {
            final int index = note % 16;
            final ITrack track = this.model.getCurrentTrackBank ().getItem (index);

            if (velocity == 0)
            {
                switch (row)
                {
                    case 7:
                        final OxiOneConfiguration configuration = this.surface.getConfiguration ();
                        if (configuration.isDeleteModeActive ())
                        {
                            configuration.toggleDeleteModeActive ();
                            track.remove ();
                        }
                        else if (configuration.isDuplicateModeActive ())
                        {
                            configuration.toggleDuplicateModeActive ();
                            track.duplicate ();
                        }
                        else if (this.surface.isShiftPressed ())
                            track.stop (false);
                        else
                            track.selectOrExpandGroup ();
                        break;

                    case 6:
                        track.toggleMute ();
                        break;

                    case 5:
                        track.toggleSolo ();
                        break;

                    case 4:
                        track.toggleRecArm ();
                        break;

                    default:
                        // Handled above
                        break;
                }
            }
        }

        // Revert to last view of the selected track
        if (velocity == 0 && this.surface.isPressed (ButtonID.SESSION))
        {
            this.surface.setTriggerConsumed (ButtonID.SESSION);
            this.surface.recallPreferredView (this.model.getCursorTrack ());
        }
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (!track.doesExist ())
            return true;

        // Stop clip
        if (this.isButtonCombination (ButtonID.STOP))
        {
            track.stop (this.isAlternateFunction ());
            return true;
        }

        return super.handleButtonCombinations (track, slot);
    }


    /** {@inheritDoc} */
    @Override
    protected int getYOffset ()
    {
        return 4;
    }
}