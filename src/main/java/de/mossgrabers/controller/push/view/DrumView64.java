// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.Modes;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IScene;
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
    private int              scrollPosition;


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
            return false;
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
            this.scrollPosition = drumPadBank.getScrollPosition ();
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
        this.model.getCursorClip ().clearRow (this.offsetY + playedPad);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleSelectButton (final int playedPad)
    {
        final ICursorDevice drumDevice64 = this.model.getDrumDevice64 ();
        if (!drumDevice64.hasDrumPads ())
            return;

        // Do not reselect
        final IChannel drumPad = drumDevice64.getDrumPadBank ().getItem (playedPad);
        if (drumPad.isSelected ())
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();
        if (cd.isNested ())
            cd.selectParent ();

        this.surface.getModeManager ().setActiveMode (Modes.MODE_DEVICE_LAYER);
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


    /**
     * Filling a slot from the browser moves the bank view to that slot. This function moves it back
     * to the correct position.
     */
    public void repositionBankPage ()
    {
        if (this.scrollPosition >= 0)
            this.model.getDrumDevice64 ().getDrumPadBank ().scrollTo (this.scrollPosition);
    }
}