// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.controller.generic.flexihandler.utils.FlexiHandlerException;
import de.mossgrabers.controller.generic.flexihandler.utils.KnobMode;
import de.mossgrabers.controller.generic.flexihandler.utils.MidiValue;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;


/**
 * The handler for scene commands.
 *
 * @author Jürgen Moßgraber
 */
public class SceneHandler extends AbstractHandler
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     * @param configuration The configuration
     * @param absoluteLowResValueChanger The default absolute value changer in low res mode
     * @param signedBitRelativeValueChanger The signed bit relative value changer
     * @param offsetBinaryRelativeValueChanger The offset binary relative value changer
     */
    public SceneHandler (final IModel model, final GenericFlexiControlSurface surface, final GenericFlexiConfiguration configuration, final IValueChanger absoluteLowResValueChanger, final IValueChanger signedBitRelativeValueChanger, final IValueChanger offsetBinaryRelativeValueChanger)
    {
        super (model, surface, configuration, absoluteLowResValueChanger, signedBitRelativeValueChanger, offsetBinaryRelativeValueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public FlexiCommand [] getSupportedCommands ()
    {
        return new FlexiCommand []
        {
            FlexiCommand.SCENE_1_LAUNCH_SCENE,
            FlexiCommand.SCENE_2_LAUNCH_SCENE,
            FlexiCommand.SCENE_3_LAUNCH_SCENE,
            FlexiCommand.SCENE_4_LAUNCH_SCENE,
            FlexiCommand.SCENE_5_LAUNCH_SCENE,
            FlexiCommand.SCENE_6_LAUNCH_SCENE,
            FlexiCommand.SCENE_7_LAUNCH_SCENE,
            FlexiCommand.SCENE_8_LAUNCH_SCENE,
            FlexiCommand.SCENE_1_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_2_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_3_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_4_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_5_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_6_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_7_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_8_LAUNCH_ALT_SCENE,
            FlexiCommand.SCENE_SELECT_PREVIOUS_BANK,
            FlexiCommand.SCENE_SELECT_NEXT_BANK,
            FlexiCommand.SCENE_CREATE_SCENE,
            FlexiCommand.SCENE_CREATE_SCENE_FROM_PLAYING_CLIPS
        };
    }


    /** {@inheritDoc} */
    @Override
    public int getCommandValue (final FlexiCommand command)
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void handle (final FlexiCommand command, final KnobMode knobMode, final MidiValue value)
    {
        final boolean isButtonPressed = this.isButtonPressed (knobMode, value);

        switch (command)
        {
            // Scene 1-8: Launch Scene
            case SCENE_1_LAUNCH_SCENE, SCENE_2_LAUNCH_SCENE, SCENE_3_LAUNCH_SCENE, SCENE_4_LAUNCH_SCENE, SCENE_5_LAUNCH_SCENE, SCENE_6_LAUNCH_SCENE, SCENE_7_LAUNCH_SCENE, SCENE_8_LAUNCH_SCENE:
                if (isButtonPressed)
                {
                    final IScene scene = this.model.getSceneBank ().getItem (command.ordinal () - FlexiCommand.SCENE_1_LAUNCH_SCENE.ordinal ());
                    scene.select ();
                    scene.launch (isButtonPressed, false);
                }
                break;

            // Scene 1-8: Launch Scene Alternative
            case SCENE_1_LAUNCH_ALT_SCENE, SCENE_2_LAUNCH_ALT_SCENE, SCENE_3_LAUNCH_ALT_SCENE, SCENE_4_LAUNCH_ALT_SCENE, SCENE_5_LAUNCH_ALT_SCENE, SCENE_6_LAUNCH_ALT_SCENE, SCENE_7_LAUNCH_ALT_SCENE, SCENE_8_LAUNCH_ALT_SCENE:
                if (isButtonPressed)
                {
                    final IScene scene = this.model.getSceneBank ().getItem (command.ordinal () - FlexiCommand.SCENE_1_LAUNCH_ALT_SCENE.ordinal ());
                    scene.select ();
                    scene.launch (isButtonPressed, true);
                }
                break;

            // Scene: Select Previous Bank
            case SCENE_SELECT_PREVIOUS_BANK:
                if (isButtonPressed)
                    this.model.getSceneBank ().selectPreviousPage ();
                break;

            // Scene: Select Next Bank
            case SCENE_SELECT_NEXT_BANK:
                if (isButtonPressed)
                    this.model.getSceneBank ().selectNextPage ();
                break;

            // Scene: Create Scene
            case SCENE_CREATE_SCENE:
                if (isButtonPressed)
                    this.model.getProject ().createScene ();
                break;

            // Scene: Create Scene from playing Clips
            case SCENE_CREATE_SCENE_FROM_PLAYING_CLIPS:
                if (isButtonPressed)
                    this.model.getProject ().createSceneFromPlayingLauncherClips ();
                break;

            default:
                throw new FlexiHandlerException (command);
        }
    }
}
