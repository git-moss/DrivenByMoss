// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey.mini.view;

import de.mossgrabers.controller.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The pad mode select view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PadModeSelectView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    private static final String [] PAD_MODE_NAMES =
    {
        "Clips",
        "Record Arm",
        "Track Select",
        "Mute",
        "Solo",
        "Stop Clips"
    };

    private boolean                isConsumed     = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PadModeSelectView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Pad Mode Select", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid pads = this.surface.getPadGrid ();
        for (int x = 0; x < 8; x++)
            pads.lightEx (x, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);

        final SessionView view = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
        final Modes padMode = view.getPadMode ();
        pads.lightEx (0, 1, padMode == null ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_LO);
        pads.lightEx (1, 1, padMode == Modes.REC_ARM ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO);
        pads.lightEx (2, 1, padMode == Modes.TRACK_SELECT ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO);
        pads.lightEx (3, 1, padMode == Modes.MUTE ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_LO);
        pads.lightEx (4, 1, padMode == Modes.SOLO ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_LO);
        pads.lightEx (5, 1, padMode == Modes.STOP_CLIP ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_PINK_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_ROSE);

        pads.lightEx (6, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        pads.lightEx (7, 1, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int index = note - 36;
        if (index > 5)
            return;

        final SessionView view = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
        view.setPadMode (index == 0 ? null : SessionView.PAD_MODES[index - 1]);
        this.surface.getDisplay ().notify (PAD_MODE_NAMES[index]);

        this.isConsumed = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID))
            return;

        if (event == ButtonEvent.UP)
        {
            this.surface.getViewManager ().restore ();

            if (this.isConsumed)
                return;

            final ISceneBank sceneBank = this.model.getCurrentTrackBank ().getSceneBank ();
            final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
            final IScene scene = sceneBank.getItem (index);
            scene.select ();
            scene.launch ();
        }
        else if (event == ButtonEvent.LONG)
            this.isConsumed = true;
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.SCENE2)
            return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
        return LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.isConsumed = false;
    }
}