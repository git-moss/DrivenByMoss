// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.view;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.IDrumPadBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.IExpressionView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrum64View;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * The Drum 64 view.
 *
 * @author Jürgen Moßgraber
 */
public class Drum64View extends AbstractDrum64View<PushControlSurface, PushConfiguration> implements IExpressionView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public Drum64View (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            final IDrumDevice primary = this.model.getDrumDevice (64);
            if (primary.hasDrumPads ())
                this.model.getBrowser ().replace (primary.getDrumPadBank ().getItem (playedPad));
            return;
        }

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleDeleteButton (final int playedPad)
    {
        this.surface.setTriggerConsumed (ButtonID.DELETE);
        final int editMidiChannel = this.surface.getConfiguration ().getMidiEditChannel ();
        this.model.getNoteClip (8, 128).clearRow (editMidiChannel, this.offsetY + playedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSelectButton (final int playedPad)
    {
        // Do we have drum pads?
        final IDrumDevice primary = this.model.getDrumDevice (64);
        if (!primary.hasDrumPads ())
            return;
        final ICursorDevice cd = this.model.getCursorDevice ();
        final boolean isNested = cd.isNested ();
        if (isNested)
        {
            // We have to move up to compare the main drum devices
            cd.selectParent ();
        }

        // Can only scroll to the channel if the cursor device is the primary device
        if (primary.getPosition () != cd.getPosition ())
            return;

        // Align the primary and cursor device drum bank view
        final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
        final int scrollPos = drumPadBank.getScrollPosition ();
        final IDrumPadBank cdDrumPadBank = cd.getDrumPadBank ();
        final int pageSize = cdDrumPadBank.getPageSize ();
        final int adjustedPage = playedPad / pageSize * pageSize;
        cdDrumPadBank.scrollTo (scrollPos + adjustedPage, false);

        // Do not reselect, if pad is already selected
        final IDrumPad drumPad = drumPadBank.getItem (playedPad);
        if (drumPad.isSelected ())
        {
            // If the instrument of the pad was selected for editing, try to select it again
            if (isNested)
            {
                final IDrumPad selectedItem = cdDrumPadBank.getItem (playedPad % pageSize);
                if (selectedItem != null)
                    selectedItem.enter ();
            }
            return;
        }

        // Only activate layer mode if not one of the layer modes is already active
        final ModeManager modeManager = this.surface.getModeManager ();
        if (!Modes.isLayerMode (modeManager.getActiveID ()))
            modeManager.setActive (Modes.DEVICE_LAYER);

        drumPad.select ();
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
}