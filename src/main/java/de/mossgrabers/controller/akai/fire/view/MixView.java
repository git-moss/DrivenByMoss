// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.fire.view;

import de.mossgrabers.controller.akai.fire.FireConfiguration;
import de.mossgrabers.controller.akai.fire.controller.FireColorManager;
import de.mossgrabers.controller.akai.fire.controller.FireControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * A view for mixing with track select, mute, solo, rec arm, stop clip, volume and panorama.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixView extends AbstractView<FireControlSurface, FireConfiguration> implements IFireView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public MixView (final FireControlSurface surface, final IModel model)
    {
        super ("Mix", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < tb.getPageSize (); i++)
        {
            final ITrack track = tb.getItem (i);
            if (track.doesExist ())
            {
                // Select
                final int colorIndex = this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ()));
                if (track.isSelected ())
                    padGrid.lightEx (i, 0, colorIndex, FireColorManager.FIRE_COLOR_WHITE, false);
                else
                    padGrid.lightEx (i, 0, colorIndex);

                // Mute
                padGrid.lightEx (i, 1, track.isMute () ? FireColorManager.FIRE_COLOR_ORANGE : FireColorManager.FIRE_COLOR_DARKER_ORANGE);
                // Solo
                padGrid.lightEx (i, 2, track.isSolo () ? FireColorManager.FIRE_COLOR_YELLOW : FireColorManager.FIRE_COLOR_DARKER_YELLOW);
                // Record Arm
                padGrid.lightEx (i, 3, track.isRecArm () ? FireColorManager.FIRE_COLOR_RED : FireColorManager.FIRE_COLOR_DARKER_RED);
            }
            else
            {
                padGrid.lightEx (i, 3, FireColorManager.FIRE_COLOR_BLACK);
                padGrid.lightEx (i, 2, FireColorManager.FIRE_COLOR_BLACK);
                padGrid.lightEx (i, 1, FireColorManager.FIRE_COLOR_BLACK);
                padGrid.lightEx (i, 0, FireColorManager.FIRE_COLOR_BLACK);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int n = note - 36;
        final int index = n % 16;
        final int what = n / 16;

        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);

        switch (what)
        {
            case 3:
                final FireConfiguration configuration = this.surface.getConfiguration ();
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
                    track.select ();
                break;

            case 2:
                track.toggleMute ();
                break;

            case 1:
                track.toggleSolo ();
                break;

            case 0:
                track.toggleRecArm ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getSoloButtonColor (final int index)
    {
        switch (index)
        {
            case 1:
                return this.model.getProject ().hasMute () ? 3 : 0;

            case 2:
                return this.model.getProject ().hasSolo () ? 4 : 0;

            default:
                return 0;
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


    /** {@inheritDoc} */
    @Override
    public void onSelectKnobValue (final int value)
    {
        final ITransport transport = this.model.getTransport ();
        transport.changePosition (this.model.getValueChanger ().isIncrease (value), this.surface.isPressed (ButtonID.SHIFT));
        this.mvHelper.notifyPlayPosition ();
    }
}