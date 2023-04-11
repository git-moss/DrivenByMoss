// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractChordView;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.Views;


/**
 * The chord view.
 *
 * @author Jürgen Moßgraber
 */
public class ChordsView extends AbstractChordView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ChordsView (final PushControlSurface surface, final IModel model)
    {
        this (Views.NAME_CHORDS, surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public ChordsView (final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (ButtonID.isSceneButton (buttonID))
            this.onSceneButton (buttonID, event);
    }


    /** {@inheritDoc} */
    @Override
    protected boolean handleSceneButtonCombinations (final int index, final IScene scene)
    {
        if (this.surface.isPressed (ButtonID.REPEAT))
        {
            NoteRepeatSceneHelper.handleNoteRepeatSelection (this.surface, 7 - index);
            return true;
        }

        return super.handleSceneButtonCombinations (index, scene);
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return AbstractFeatureGroup.BUTTON_COLOR_OFF;

        if (this.surface.isPressed (ButtonID.REPEAT))
            return NoteRepeatSceneHelper.getButtonColorID (this.surface, buttonID);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final IScene s = sceneBank.getItem (scene);
        if (s.doesExist ())
            return s.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE;
        return AbstractSessionView.COLOR_SCENE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (this.isButtonCombination (ButtonID.DELETE))
        {
            final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
            this.model.getNoteClip (8, 128).clearRow (editMidiChannel, this.keyManager.map (note));
            return;
        }
        super.onGridNote (note, velocity);
    }
}