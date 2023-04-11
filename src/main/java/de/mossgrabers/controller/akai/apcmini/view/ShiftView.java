// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini.view;

import de.mossgrabers.controller.akai.apcmini.APCminiConfiguration;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiColorManager;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.IDisplay;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The Shift view.
 *
 * @author Jürgen Moßgraber
 */
public class ShiftView extends AbstractView<APCminiControlSurface, APCminiConfiguration> implements APCminiView
{
    private static final int []                                    TRANSLATE =
    {
        0,
        2,
        4,
        6,
        1,
        3,
        5,
        -1,
        -1,
        10,
        8,
        -1,
        11,
        9,
        7,
        -1
    };

    final PlayCommand<APCminiControlSurface, APCminiConfiguration> playCommand;
    final NewCommand<APCminiControlSurface, APCminiConfiguration>  newCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ShiftView (final APCminiControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);

        this.playCommand = new PlayCommand<> (this.model, this.surface);
        this.newCommand = new NewCommand<> (this.model, this.surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Draw the keyboard
        final int scaleOffset = this.scales.getScaleOffsetIndex ();
        // 0'C', 1'G', 2'D', 3'A', 4'E', 5'B', 6'F', 7'Bb', 8'Eb', 9'Ab', 10'Db', 11'Gb'
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 7; i < 64; i++)
            padGrid.light (36 + i, APCminiColorManager.APC_COLOR_BLACK);
        padGrid.light (36 + 0, scaleOffset == 0 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 1, scaleOffset == 2 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 2, scaleOffset == 4 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 3, scaleOffset == 6 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 4, scaleOffset == 1 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 5, scaleOffset == 3 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 6, scaleOffset == 5 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 9, scaleOffset == 10 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 10, scaleOffset == 8 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 12, scaleOffset == 11 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 13, scaleOffset == 9 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 14, scaleOffset == 7 ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_RED);

        // Device Parameters up/down
        padGrid.light (36 + 24, APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 25, APCminiColorManager.APC_COLOR_YELLOW);
        // Device up/down
        padGrid.light (36 + 32, APCminiColorManager.APC_COLOR_GREEN);
        padGrid.light (36 + 33, APCminiColorManager.APC_COLOR_GREEN);

        // Change the scale
        padGrid.light (36 + 35, APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 36, APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 27, APCminiColorManager.APC_COLOR_GREEN);

        // Draw the view selection: Session, Note, Drum, Sequencer
        final Views previousViewId = this.surface.getViewManager ().getActiveIDIgnoreTemporary ();
        padGrid.light (36 + 56, Views.SESSION == previousViewId ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 57, Views.PLAY == previousViewId ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 58, Views.DRUM == previousViewId ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 59, Views.SEQUENCER == previousViewId ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 60, Views.RAINDROPS == previousViewId ? APCminiColorManager.APC_COLOR_GREEN : APCminiColorManager.APC_COLOR_YELLOW);

        // Draw transport
        final ITransport transport = this.model.getTransport ();
        padGrid.light (36 + 63, transport.isPlaying () ? APCminiColorManager.APC_COLOR_GREEN_BLINK : APCminiColorManager.APC_COLOR_GREEN);
        padGrid.light (36 + 55, transport.isRecording () ? APCminiColorManager.APC_COLOR_RED_BLINK : APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 47, APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 39, APCminiColorManager.APC_COLOR_YELLOW);

        padGrid.light (36 + 62, APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 54, transport.isLauncherOverdub () ? APCminiColorManager.APC_COLOR_RED_BLINK : APCminiColorManager.APC_COLOR_RED);
        padGrid.light (36 + 46, APCminiColorManager.APC_COLOR_YELLOW);
        padGrid.light (36 + 38, APCminiColorManager.APC_COLOR_YELLOW);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity != 0)
            return;

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final APCminiConfiguration configuration = this.surface.getConfiguration ();
        final IDisplay display = this.surface.getDisplay ();

        final int n = this.surface.getPadGrid ().translateToController (note)[1];
        switch (n)
        {
            // Flip views
            case 56:
                this.switchToView (Views.SESSION);
                break;
            case 57:
                this.switchToView (Views.PLAY);
                break;
            case 58:
                this.switchToView (Views.DRUM);
                break;
            case 59:
                this.switchToView (Views.SEQUENCER);
                break;
            case 60:
                this.switchToView (Views.RAINDROPS);
                break;

            // Last row transport
            case 63:
                this.playCommand.executeNormal (ButtonEvent.UP);
                display.notify ("Start/Stop");
                break;
            case 55:
                this.model.getTransport ().startRecording ();
                display.notify ("Record");
                break;
            case 47:
                this.model.getTransport ().toggleLoop ();
                display.notify ("Toggle Loop");
                break;
            case 39:
                this.model.getTransport ().toggleMetronome ();
                display.notify ("Toggle Click");
                break;

            // Navigation
            case 62:
                this.newCommand.execute ();
                display.notify ("New clip");
                break;
            case 54:
                this.model.getTransport ().toggleLauncherOverdub ();
                display.notify ("Toggle Launcher Overdub");
                break;
            case 46:
                final IClip clip = this.model.getCursorClip ();
                if (clip.doesExist ())
                    clip.quantize (configuration.getQuantizeAmount () / 100.0);
                display.notify ("Quantize");
                break;
            case 38:
                this.model.getApplication ().undo ();
                display.notify ("Undo");
                break;

            // Device Parameters up/down
            case 24:
                this.scrollParameterBank (true, cursorDevice);
                break;

            case 25:
                this.scrollParameterBank (false, cursorDevice);
                break;

            // Device up/down
            case 32:
                if (cursorDevice.canSelectPrevious ())
                {
                    cursorDevice.selectPrevious ();
                    display.notify ("Device: " + cursorDevice.getName ());
                }
                break;
            case 33:
                if (cursorDevice.canSelectNext ())
                {
                    cursorDevice.selectNext ();
                    display.notify ("Device: " + cursorDevice.getName ());
                }
                break;

            // Change the scale
            case 35:
                this.scales.prevScale ();
                final String prevScale = this.scales.getScale ().getName ();
                configuration.setScale (prevScale);
                display.notify (prevScale);
                break;
            case 36:
                this.scales.nextScale ();
                final String nextScale = this.scales.getScale ().getName ();
                configuration.setScale (nextScale);
                display.notify (nextScale);
                break;
            case 27:
                final boolean isChromatic = !configuration.isScaleInKey ();
                configuration.setScaleInKey (isChromatic);
                display.notify (isChromatic ? "Chromatic" : "In Key");
                break;

            // Scale Base note selection
            default:
                if (n > 15)
                    return;
                final int pos = TRANSLATE[n];
                if (pos == -1)
                    return;
                this.scales.setScaleOffsetByIndex (pos);
                configuration.setScaleBase (Scales.BASES.get (pos));
                display.notify (Scales.BASES.get (pos));
                this.surface.getViewManager ().getActive ().updateNoteMapping ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSelectTrack (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ModeManager modeManager = this.surface.getModeManager ();

        switch (index)
        {
            case 0:
                tb.getSceneBank ().selectPreviousPage ();
                break;
            case 1:
                tb.getSceneBank ().selectNextPage ();
                break;
            case 2:
                tb.selectPreviousPage ();
                break;
            case 3:
                tb.selectNextPage ();
                break;

            case 4:
                modeManager.setActive (Modes.VOLUME);
                this.surface.getConfiguration ().setFaderCtrl ("Volume");
                this.surface.getDisplay ().notify ("Volume");
                break;

            case 5:
                modeManager.setActive (Modes.PAN);
                this.surface.getConfiguration ().setFaderCtrl ("Pan");
                this.surface.getDisplay ().notify ("Pan");
                break;

            case 6:
                if (this.model.isEffectTrackBankActive ())
                    return;
                Modes mode = Modes.get (modeManager.getActiveID (), 1);
                // Wrap
                if (!Modes.isSendMode (mode))
                    mode = Modes.SEND1;
                // Check if Send channel exists
                if (Modes.isSendMode (mode) && tb.canEditSend (mode.ordinal () - Modes.SEND1.ordinal ()))
                    mode = Modes.SEND1;
                modeManager.setActive (mode);
                final String name = "Send " + (mode.ordinal () - Modes.SEND1.ordinal () + 1);
                this.surface.getConfiguration ().setFaderCtrl (name);
                this.surface.getDisplay ().notify (name);
                break;

            case 7:
                if (modeManager.isActive (Modes.DEVICE_PARAMS))
                    this.model.getBrowser ().replace (this.model.getCursorDevice ());
                else
                {
                    modeManager.setActive (Modes.DEVICE_PARAMS);
                    this.surface.getConfiguration ().setFaderCtrl ("Device");
                    this.surface.getDisplay ().notify ("Device");
                }
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        switch (buttonID)
        {
            case SCENE6:
                this.model.toggleCurrentTrackBank ();
                final boolean isEffectTrackBank = this.model.isEffectTrackBankActive ();
                if (isEffectTrackBank)
                {
                    // No Sends on effect tracks
                    final ModeManager modeManager = this.surface.getModeManager ();
                    if (Modes.isSendMode (modeManager.getActiveID ()))
                        modeManager.setActive (Modes.VOLUME);
                }
                this.surface.getDisplay ().notify (isEffectTrackBank ? "Effect Tracks" : "Instrument/Audio Tracks");
                break;
            case SCENE7:
                this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            case SCENE8:
                this.model.getCurrentTrackBank ().stop ();
                break;
            default:
                final int index = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();
                this.surface.setTrackState (index);
                final String softKeys = APCminiConfiguration.SOFT_KEYS_OPTIONS.get (index);
                this.surface.getConfiguration ().setSoftKeys (softKeys);
                this.surface.getDisplay ().notify (softKeys);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getTrackButtonColor (final int index)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = tb.getSceneBank ();
        final Modes mode = this.surface.getModeManager ().getActiveID ();

        switch (index)
        {
            case 0:
                return sceneBank.canScrollPageBackwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 1:
                return sceneBank.canScrollPageForwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 2:
                return tb.canScrollPageBackwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 3:
                return tb.canScrollPageForwards () ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 4:
                return Modes.VOLUME.equals (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 5:
                return Modes.PAN.equals (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 6:
                return Modes.isSendMode (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            case 7:
                return Modes.DEVICE_PARAMS.equals (mode) ? APCminiControlSurface.APC_BUTTON_STATE_ON : APCminiControlSurface.APC_BUTTON_STATE_OFF;
            default:
                // Never reached
                break;
        }

        return APCminiColorManager.APC_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public String getButtonColorID (final ButtonID buttonID)
    {
        final int trackState = this.surface.getTrackState ();

        // Draw the track states on the scene buttons
        switch (buttonID)
        {
            case SCENE1:
                return trackState == APCminiControlSurface.TRACK_STATE_CLIP_STOP ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
            case SCENE2:
                return trackState == APCminiControlSurface.TRACK_STATE_SOLO ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
            case SCENE3:
                return trackState == APCminiControlSurface.TRACK_STATE_REC_ARM ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
            case SCENE4:
                return trackState == APCminiControlSurface.TRACK_STATE_MUTE ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
            case SCENE5:
                return trackState == APCminiControlSurface.TRACK_STATE_SELECT ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
            case SCENE6:
                return this.model.isEffectTrackBankActive () ? ColorManager.BUTTON_STATE_ON : ColorManager.BUTTON_STATE_OFF;
            default:
                return ColorManager.BUTTON_STATE_OFF;
        }
    }


    private void switchToView (final Views viewID)
    {
        final ViewManager viewManager = this.surface.getViewManager ();
        viewManager.setActive (viewID);
        this.surface.getDisplay ().notify (viewManager.get (viewID).getName ());
    }


    private void scrollParameterBank (final boolean scrollBack, final ICursorDevice cursorDevice)
    {
        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        if (scrollBack)
        {
            if (!parameterBank.canScrollPageBackwards ())
                return;
            parameterBank.scrollBackwards ();
        }
        else
        {
            if (!parameterBank.canScrollPageForwards ())
                return;
            parameterBank.scrollForwards ();
        }
        this.mvHelper.notifySelectedParameterPage ();
    }
}