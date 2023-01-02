// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.view;

import de.mossgrabers.controller.novation.launchkey.maxi.LaunchkeyMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.maxi.controller.LaunchkeyMk3ControlSurface;
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
public class PadModeSelectView extends AbstractView<LaunchkeyMk3ControlSurface, LaunchkeyMk3Configuration>
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
    public PadModeSelectView (final LaunchkeyMk3ControlSurface surface, final IModel model)
    {
        super ("Pad Mode Select", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid pads = this.surface.getPadGrid ();
        for (int x = 0; x < 8; x++)
            pads.lightEx (x, 0, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);

        final SessionView view = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
        final Modes padMode = view.getPadMode ();
        pads.lightEx (0, 1, padMode == null ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREEN_LO);
        pads.lightEx (1, 1, padMode == Modes.REC_ARM ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_RED_LO);
        pads.lightEx (2, 1, padMode == Modes.TRACK_SELECT ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_WHITE : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_GREY_LO);
        pads.lightEx (3, 1, padMode == Modes.MUTE ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_AMBER_LO);
        pads.lightEx (4, 1, padMode == Modes.SOLO ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_YELLOW_LO);
        pads.lightEx (5, 1, padMode == Modes.STOP_CLIP ? LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_PINK_HI : LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_ROSE);

        pads.lightEx (6, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
        pads.lightEx (7, 1, LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK);
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
        view.setPadMode (index == 0 ? null : SessionView.PAD_MODES.get (index - 1));
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
            return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_WHITE;
        return LaunchkeyMk3ColorManager.LAUNCHKEY_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.isConsumed = false;
    }
}