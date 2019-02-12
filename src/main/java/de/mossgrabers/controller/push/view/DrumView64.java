// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.AbstractDrumView64;
import de.mossgrabers.framework.view.AbstractSessionView;


/**
 * The Drum 64 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView64 extends AbstractDrumView64<PushControlSurface, PushConfiguration>
{
    private static final int NUMBER_OF_RETRIES = 20;

    private int              startRetries;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView64 (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, this.drumOctave < 1 ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, this.drumOctave > Scales.DRUM_OCTAVE_LOWER ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        if (buttonID == PushControlSurface.PUSH_BUTTON_REPEAT)
            return this.model.getHost ().hasRepeat ();
        return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_BROWSE))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_BROWSE);

            final ICursorDevice primary = this.model.getDrumDevice64 ();
            if (!primary.hasDrumPads ())
                return;

            final IDrumPadBank drumPadBank = primary.getDrumPadBank ();
            final IDrumPad drumPad = drumPadBank.getItem (playedPad);
            drumPad.browseToInsert ();
            this.activateMode ();
            return;
        }

        super.handleButtonCombinations (playedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleDeleteButton (final int playedPad)
    {
        this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
        this.model.getNoteClip (8, 128).clearRow (this.offsetY + playedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSelectButton (final int playedPad)
    {
        // Do we have drum pads?
        final ICursorDevice primary = this.model.getDrumDevice64 ();
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
        if (!Modes.isLayerMode (modeManager.getActiveModeId ()))
            modeManager.setActiveMode (Modes.MODE_DEVICE_LAYER);

        drumPad.select ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final ColorManager colorManager = this.model.getColorManager ();
        final int colorScene = colorManager.getColor (AbstractSessionView.COLOR_SCENE);
        final int colorSceneSelected = colorManager.getColor (AbstractSessionView.COLOR_SELECTED_SCENE);
        final int colorSceneOff = colorManager.getColor (AbstractSessionView.COLOR_SCENE_OFF);

        final ISceneBank sceneBank = this.model.getSceneBank ();
        for (int i = 0; i < 8; i++)
        {
            final IScene scene = sceneBank.getItem (7 - i);
            final int color = scene.doesExist () ? scene.isSelected () ? colorSceneSelected : colorScene : colorSceneOff;
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, color);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int sceneIndex, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final IScene scene = this.model.getCurrentTrackBank ().getSceneBank ().getItem (sceneIndex);
        scene.select ();
        scene.launch ();
    }


    /**
     * Tries to activate the mode 20 times.
     */
    protected void activateMode ()
    {
        if (this.model.getBrowser ().isActive ())
            this.surface.getModeManager ().setActiveMode (Modes.MODE_BROWSER);
        else if (this.startRetries < NUMBER_OF_RETRIES)
        {
            this.startRetries++;
            this.surface.scheduleTask (this::activateMode, 200);
        }
    }
}