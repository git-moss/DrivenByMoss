// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.oxi.one.view;

import de.mossgrabers.controller.oxi.one.OxiOneConfiguration;
import de.mossgrabers.controller.oxi.one.controller.OxiOneColorManager;
import de.mossgrabers.controller.oxi.one.controller.OxiOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A view for mixing with track select, mute, solo, record arm, stop clip, volume and panorama.
 *
 * @author Jürgen Moßgraber
 */
public class OxiOneMixView extends AbstractView<OxiOneControlSurface, OxiOneConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public OxiOneMixView (final OxiOneControlSurface surface, final IModel model)
    {
        super ("Track Mixer", surface, model);
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
        if (velocity == 0)
            return;

        final int index = note % 16;
        final int what = note / 16;

        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);

        switch (what)
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
            case SCENE4:
                return 0;

            case SCENE2:
            case SCENE3:
                return this.surface.isPressed (buttonID) ? 2 : 1;

            default:
                return super.getButtonColor (buttonID);
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
            case ARROW_LEFT:
                this.model.getCurrentTrackBank ().selectPreviousPage ();
                break;

            case ARROW_RIGHT:
                this.model.getCurrentTrackBank ().selectNextPage ();
                break;

            case SCENE1:
                this.model.getTransport ().selectLoopStart ();
                break;

            case SCENE2:
                this.model.getProject ().clearMute ();
                break;

            case SCENE3:
                this.model.getProject ().clearSolo ();
                break;

            case SCENE4:
                this.model.getTransport ().selectLoopEnd ();
                break;

            default:
                // Not used
                break;
        }
    }
}