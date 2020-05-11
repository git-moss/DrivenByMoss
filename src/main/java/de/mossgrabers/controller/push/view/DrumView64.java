// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IDrumPadBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISceneBank;
import de.mossgrabers.framework.daw.data.IDrumPad;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.mode.BrowserActivator;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
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
    private final BrowserActivator<PushControlSurface, PushConfiguration> browserModeActivator;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView64 (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);

        this.browserModeActivator = new BrowserActivator<> (Modes.BROWSER, model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleButtonCombinations (final int playedPad)
    {
        if (this.isButtonCombination (ButtonID.BROWSE))
        {
            final ICursorDevice primary = this.model.getDrumDevice64 ();
            if (!primary.hasDrumPads ())
                return;

            this.model.getBrowser ().replace (primary.getDrumPadBank ().getItem (playedPad));
            this.browserModeActivator.activate ();
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
            modeManager.setActiveMode (Modes.DEVICE_LAYER);

        drumPad.select ();
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        if (scene < 0 || scene >= 8)
            return AbstractMode.BUTTON_COLOR_OFF;

        final ISceneBank sceneBank = this.model.getSceneBank ();
        final IScene s = sceneBank.getItem (scene);
        if (s.doesExist ())
            return s.isSelected () ? AbstractSessionView.COLOR_SELECTED_SCENE : AbstractSessionView.COLOR_SCENE;
        return AbstractSessionView.COLOR_SCENE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
        final IScene scene = this.model.getCurrentTrackBank ().getSceneBank ().getItem (index);

        if (this.isButtonCombination (ButtonID.DELETE))
        {
            scene.remove ();
            return;
        }

        if (this.isButtonCombination (ButtonID.DUPLICATE))
        {
            scene.duplicate ();
            return;
        }

        scene.select ();
        scene.launch ();
    }
}