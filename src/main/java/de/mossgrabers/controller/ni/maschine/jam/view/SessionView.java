// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.view;

import de.mossgrabers.controller.ni.maschine.core.MaschineColorManager;
import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.controller.MaschineJamControlSurface;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * The Session view.
 *
 * @author Jürgen Moßgraber
 */
public class SessionView extends AbstractSessionView<MaschineJamControlSurface, MaschineJamConfiguration> implements IViewNavigation
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final MaschineJamControlSurface surface, final IModel model)
    {
        super ("Session", surface, model, 8, 8, true);

        final LightInfo isRecording = new LightInfo (MaschineColorManager.COLOR_RED, MaschineColorManager.COLOR_RED, false);
        final LightInfo isRecordingQueued = new LightInfo (MaschineColorManager.COLOR_RED, MaschineColorManager.COLOR_BLACK, true);
        final LightInfo isPlaying = new LightInfo (MaschineColorManager.COLOR_GREEN, -1, false);
        final LightInfo isPlayingQueued = new LightInfo (MaschineColorManager.COLOR_GREEN, MaschineColorManager.COLOR_GREEN, true);
        final LightInfo hasContent = new LightInfo (MaschineColorManager.COLOR_AMBER, MaschineColorManager.COLOR_WHITE, false);
        final LightInfo noContent = new LightInfo (MaschineColorManager.COLOR_BLACK, -1, false);
        final LightInfo recArmed = new LightInfo (MaschineColorManager.COLOR_RED_LO, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);

        this.birdColorHasContent = hasContent;
        this.birdColorSelected = isPlaying;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Birds-eye-view navigation
        if (this.isBirdsEyeActive ())
        {
            if (velocity == 0)
                return;

            final int index = note - 36;
            final int x = index % this.columns;
            final int y = this.rows - 1 - index / this.columns;

            this.onGridNoteBirdsEyeView (x, y, 0);
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        final boolean isDown = event == ButtonEvent.DOWN;

        switch (buttonID)
        {
            case ARROW_LEFT, ARROW_RIGHT:
                if (isDown)
                {
                    if (flipSession)
                        this.scrollSceneBank (buttonID == ButtonID.ARROW_RIGHT);
                    else
                        this.scrollTrackBank (buttonID == ButtonID.ARROW_RIGHT);
                }
                break;

            case ARROW_UP, ARROW_DOWN:
                if (isDown)
                {
                    if (flipSession)
                        this.scrollTrackBank (buttonID == ButtonID.ARROW_DOWN);
                    else
                        this.scrollSceneBank (buttonID == ButtonID.ARROW_DOWN);
                }
                break;

            default:
                super.onButton (buttonID, event, velocity);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 8)
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final IScene s = sceneBank.getItem (scene);
        if (s.doesExist ())
            return s.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE;
        return AbstractSessionView.COLOR_SCENE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleButtonCombinations (final ITrack track, final ISlot slot)
    {
        if (!track.doesExist ())
            return true;

        // Stop clip
        if (this.isButtonCombination (ButtonID.MUTE))
        {
            track.stop (this.surface.isSelectPressed ());
            return true;
        }

        // Select the clip (without playback)
        if (this.isButtonCombination (ButtonID.SELECT))
        {
            slot.select ();
            return true;
        }

        // Switch back to arranger playback
        if (this.isButtonCombination (ButtonID.GROOVE))
        {
            track.returnToArrangement ();
            return true;
        }

        return super.handleButtonCombinations (track, slot);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScroll (final Direction direction)
    {
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();

        switch (direction)
        {
            case LEFT, RIGHT:
                if (flipSession)
                    return direction == Direction.LEFT ? trackBank.getSceneBank ().canScrollPageBackwards () : trackBank.getSceneBank ().canScrollPageForwards ();
                return direction == Direction.LEFT ? trackBank.canScrollPageBackwards () : trackBank.canScrollPageForwards ();
            case UP, DOWN:
                if (flipSession)
                    return direction == Direction.UP ? trackBank.canScrollPageBackwards () : trackBank.canScrollPageForwards ();
                return direction == Direction.UP ? trackBank.getSceneBank ().canScrollPageBackwards () : trackBank.getSceneBank ().canScrollPageForwards ();
            default:
                // No more directions
                break;
        }
        return false;
    }


    private void scrollTrackBank (final boolean isForwards)
    {
        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        if (isForwards)
            trackBank.scrollForwards ();
        else
            trackBank.scrollBackwards ();
    }


    private void scrollSceneBank (final boolean isForwards)
    {
        final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
        if (isForwards)
            sceneBank.scrollForwards ();
        else
            sceneBank.scrollBackwards ();
    }
}