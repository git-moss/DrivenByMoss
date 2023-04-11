// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini.view;

import de.mossgrabers.controller.novation.launchkey.mini.LaunchkeyMiniMk3Configuration;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.framework.command.trigger.clip.QuantizeCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The pad mode select view.
 *
 * @author Jürgen Moßgraber
 */
public class PadModeSelectView extends AbstractView<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    private static final String []                                                               PAD_MODE_NAMES =
    {
        "Clips",
        "Record Arm",
        "Track Select",
        "Mute",
        "Solo",
        "Stop Clips"
    };

    private final QuantizeCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> quantizeCommand;
    private boolean                                                                              isConsumed     = false;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PadModeSelectView (final LaunchkeyMiniMk3ControlSurface surface, final IModel model)
    {
        super ("Pad Mode Select", surface, model);

        this.quantizeCommand = new QuantizeCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid pads = this.surface.getPadGrid ();

        final ITransport transport = this.model.getTransport ();
        pads.lightEx (0, 0, transport.isMetronomeOn () ? LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_HI : LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_GREEN_LO);
        pads.lightEx (1, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_BLUE_LO);
        pads.lightEx (2, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO);
        pads.lightEx (3, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_RED_LO);
        pads.lightEx (4, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_WHITE);
        pads.lightEx (5, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_YELLOW);
        pads.lightEx (6, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_YELLOW);
        pads.lightEx (7, 0, LaunchkeyMiniMk3ColorManager.LAUNCHKEY_COLOR_AMBER_YELLOW);

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

        final IDisplay display = this.surface.getDisplay ();
        final ITransport transport = this.model.getTransport ();
        final IApplication application = this.model.getApplication ();

        final int index = note - 36;
        switch (index)
        {
            case 0, 1, 2, 3, 4, 5:
                final SessionView view = (SessionView) this.surface.getViewManager ().get (Views.SESSION);
                view.setPadMode (index == 0 ? null : SessionView.PAD_MODES.get (index - 1));
                display.notify (PAD_MODE_NAMES[index]);
                break;

            case 8:
                transport.toggleMetronome ();
                this.mvHelper.delayDisplay ( () -> transport.isMetronomeOn () ? "Metronome: On" : "Metronome: Off");
                break;

            case 9:
                transport.tapTempo ();
                this.mvHelper.notifyTempo ();
                break;

            case 10:
                application.undo ();
                display.notify ("Undo");
                break;

            case 11:
                application.redo ();
                display.notify ("Redo");
                break;

            case 12:
                this.quantizeCommand.executeNormal (ButtonEvent.UP);
                display.notify ("Quantize");
                break;

            case 13:
                application.addInstrumentTrack ();
                display.notify ("Add Instrument Track");
                break;

            case 14:
                application.addAudioTrack ();
                display.notify ("Add Audio Track");
                break;

            case 15:
                application.addEffectTrack ();
                display.notify ("Add Effect Track");
                break;

            default:
                // Ignore 6 and 7
                break;
        }

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
            scene.launch (true, false);
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