// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl.view;

import de.mossgrabers.controller.novation.sl.SLConfiguration;
import de.mossgrabers.controller.novation.sl.command.trigger.ButtonRowSelectCommand;
import de.mossgrabers.controller.novation.sl.command.trigger.P2ButtonCommand;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.controller.novation.sl.mode.device.DevicePresetsMode;
import de.mossgrabers.controller.novation.sl.mode.device.SLParameterMode;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.MidiConstants;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ControlOnlyView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * The view for controlling the DAW.
 *
 * @author Jürgen Moßgraber
 */
public class ControlView extends ControlOnlyView<SLControlSurface, SLConfiguration> implements SLView
{
    private boolean                                             isTempoDec;
    private boolean                                             isTempoInc;
    private final TransportControl                              transportControl;
    private final NewCommand<SLControlSurface, SLConfiguration> newCommand;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final SLControlSurface surface, final IModel model)
    {
        super (surface, model);

        this.newCommand = new NewCommand<> (model, surface);
        this.transportControl = new TransportControl (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow1 (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        Modes activeModeId = modeManager.getActiveID ();
        if (Modes.VIEW_SELECT == activeModeId)
        {
            if (index == 1)
            {
                this.surface.getViewManager ().setActive (Views.PLAY);
                if (Modes.VOLUME.equals (modeManager.getPreviousID ()))
                    modeManager.restore ();
                else
                    modeManager.setActive (Modes.SESSION);
            }
            else
                modeManager.restore ();
            this.surface.turnOffTransport ();
            return;
        }

        if (!Modes.FUNCTIONS.equals (activeModeId) && !Modes.FIXED.equals (activeModeId))
        {
            modeManager.setActive (Modes.FUNCTIONS);
            activeModeId = Modes.FUNCTIONS;
        }

        if (Modes.FIXED.equals (activeModeId))
        {
            this.surface.getConfiguration ().setNewClipLength (index);
            return;
        }

        switch (index)
        {
            // Undo
            case 0:
                this.model.getApplication ().undo ();
                break;

            // Redo
            case 1:
                this.model.getApplication ().redo ();
                break;

            // Delete
            case 2:
                this.model.getApplication ().deleteSelection ();
                break;

            // Double
            case 3:
                this.model.getApplication ().duplicate ();
                break;

            // New
            case 4:
                this.newCommand.execute ();
                break;

            // Open the VST window
            case 5:
                this.model.getCursorDevice ().toggleWindowOpen ();
                break;

            // Metronome
            case 6:
                this.model.getTransport ().toggleMetronome ();
                break;

            // Tap Tempo on MKII
            case 7:
                this.model.getTransport ().tapTempo ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow2 (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        Modes cm = modeManager.getActiveID ();
        if (!Modes.TRACK_DETAILS.equals (cm) && !Modes.FRAME.equals (cm) && !Modes.BROWSER.equals (cm))
        {
            modeManager.setActive (Modes.TRACK_DETAILS);
            cm = Modes.TRACK_DETAILS;
        }

        if (Modes.FRAME.equals (cm))
        {
            modeManager.get (Modes.FRAME).onButton (0, index, event);
            return;
        }
        else if (Modes.BROWSER.equals (cm))
        {
            modeManager.get (Modes.BROWSER).onButton (0, index, event);
            return;
        }

        switch (index)
        {
            // Mute
            case 0:
                this.model.getCursorTrack ().toggleMute ();
                break;

            // Solo
            case 1:
                this.model.getCursorTrack ().toggleSolo ();
                break;

            // Arm
            case 2:
                this.model.getCursorTrack ().toggleRecArm ();
                break;

            // Write
            case 3:
                this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;

            // Browse
            case 4:
                this.model.getBrowser ().replace (this.model.getCursorDevice ());
                modeManager.setTemporary (Modes.BROWSER);
                break;

            // Dis-/Enable device
            case 5:
                this.model.getCursorDevice ().toggleEnabledState ();
                break;

            // Previous device
            case 6:
                this.model.getCursorDevice ().selectPrevious ();
                break;

            // Next device
            case 7:
                this.model.getCursorDevice ().selectNext ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow3 (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.UP)
            this.model.getCurrentTrackBank ().getItem (index).selectOrExpandGroup ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow4 (final int index, final ButtonEvent event)
    {
        switch (index)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                this.transportControl.execute (index, event);
                break;

            case 6:
                // Decrease tempo
                if (event == ButtonEvent.DOWN)
                    this.isTempoDec = true;
                else if (event == ButtonEvent.UP)
                    this.isTempoDec = false;
                this.doChangeTempo ();
                break;

            case 7:
                // Increase tempo
                if (event == ButtonEvent.DOWN)
                    this.isTempoInc = true;
                else if (event == ButtonEvent.UP)
                    this.isTempoInc = false;
                this.doChangeTempo ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow5 (final int index, final ButtonEvent event)
    {
        this.transportControl.execute (index, event);
    }


    private void doChangeTempo ()
    {
        if (!this.isTempoInc && !this.isTempoDec)
            return;
        this.model.getTransport ().changeTempo (this.isTempoInc, this.surface.isKnobSensitivitySlow ());
        this.surface.scheduleTask (this::doChangeTempo, 200);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow1Select ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final boolean selectFixed = Modes.FUNCTIONS.equals (modeManager.getActiveID ());
        modeManager.setActive (selectFixed ? Modes.FIXED : Modes.FUNCTIONS);
        this.model.getHost ().showNotification (selectFixed ? "Fixed Length" : "Functions");
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow2Select ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final boolean selectFrame = Modes.TRACK_DETAILS.equals (modeManager.getActiveID ());
        modeManager.setActive (selectFrame ? Modes.FRAME : Modes.TRACK_DETAILS);
        this.model.getHost ().showNotification (selectFrame ? "Layouts & Panels" : "Track & Device");
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonP1 (final boolean isUp, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        switch (modeManager.getActiveID ())
        {
            case FUNCTIONS:
            case FIXED:
                this.onButtonRow1Select ();
                break;

            case VOLUME:
                new P2ButtonCommand (isUp, this.model, this.surface).execute (ButtonEvent.DOWN, 127);
                break;

            case TRACK:
                new ButtonRowSelectCommand<> (3, this.model, this.surface).execute (ButtonEvent.DOWN, 127);
                break;

            case TRACK_DETAILS:
            case FRAME:
                this.onButtonRow2Select ();
                break;

            default:
                final SLParameterMode deviceParamsMode = (SLParameterMode) modeManager.get (Modes.DEVICE_PARAMS);
                if (isUp)
                    deviceParamsMode.selectNextItemPage ();
                else
                    deviceParamsMode.selectPreviousItemPage ();
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ICursorDevice cd = this.model.getCursorDevice ();
        final ITransport transport = this.model.getTransport ();
        final int clipLength = this.surface.getConfiguration ().getNewClipLength ();

        final Modes mode = this.surface.getModeManager ().getActiveID ();
        final boolean isFunctions = Modes.FUNCTIONS.equals (mode);

        final boolean isViewSelectMode = Modes.VIEW_SELECT.equals (mode);

        switch (buttonID)
        {
            case ROW1_1:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return !isFunctions && clipLength == 0 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_2:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return !isFunctions && clipLength == 1 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_3:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return !isFunctions && clipLength == 2 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_4:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return !isFunctions && clipLength == 3 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_5:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return !isFunctions && clipLength == 4 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_6:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return isFunctions && this.model.getCursorDevice ().isWindowOpen () || !isFunctions && clipLength == 5 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_7:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return isFunctions && transport.isMetronomeOn () || !isFunctions && clipLength == 6 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW1_8:
                if (isViewSelectMode)
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                return !isFunctions && clipLength == 7 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;

            default:
                // Fall through
                break;
        }

        // Button row 2: Track toggles / Browse
        if (Modes.BROWSER.equals (mode))
        {
            final int selMode = ((DevicePresetsMode) this.surface.getModeManager ().get (Modes.BROWSER)).getSelectionMode ();

            switch (buttonID)
            {
                case ROW2_1:
                case ROW2_2:
                case ROW2_8:
                    return SLControlSurface.MKII_BUTTON_STATE_ON;
                case ROW2_3:
                case ROW2_4:
                case ROW2_5:
                case ROW2_6:
                case ROW2_7:
                    return selMode == DevicePresetsMode.SELECTION_OFF ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;

                default:
                    // Fall through
                    break;
            }
        }
        else
        {
            final boolean isNoOverlayMode = !Modes.FRAME.equals (mode) && !Modes.BROWSER.equals (mode);
            final Optional<ITrack> track = tb.getSelectedItem ();

            switch (buttonID)
            {
                case ROW2_1:
                    return isNoOverlayMode && track.isPresent () && track.get ().isMute () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_2:
                    return isNoOverlayMode && track.isPresent () && track.get ().isSolo () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_3:
                    return isNoOverlayMode && track.isPresent () && track.get ().isRecArm () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_4:
                    return transport.isWritingArrangerAutomation () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_5:
                    return SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_6:
                    return this.model.getCursorDevice ().isEnabled () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_7:
                    return isNoOverlayMode && cd.canSelectPrevious () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
                case ROW2_8:
                    return isNoOverlayMode && cd.canSelectNext () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;

                default:
                    // Fall through
                    break;
            }
        }

        // Button row 3: Selected track indication

        final int buttonIDOrdinal = buttonID.ordinal ();
        if (buttonIDOrdinal >= ButtonID.ROW3_1.ordinal () && buttonIDOrdinal <= ButtonID.ROW3_8.ordinal ())
        {
            final int index = buttonIDOrdinal - ButtonID.ROW3_1.ordinal ();
            return tb.getItem (index).isSelected () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
        }

        final boolean isTrack = Modes.TRACK.equals (mode);
        final boolean isTrackToggles = Modes.TRACK_DETAILS.equals (mode);
        final boolean isVolume = Modes.VOLUME.equals (mode);
        final boolean isFixed = Modes.FIXED.equals (mode);
        final boolean isFrame = Modes.FRAME.equals (mode);
        final boolean isPreset = Modes.BROWSER.equals (mode);
        final boolean isDevice = Modes.DEVICE_PARAMS.equals (mode);

        // Transport buttons
        switch (buttonID)
        {
            case ROW4_3:
                return !transport.isPlaying () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW4_4:
                return transport.isPlaying () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW4_5:
                return transport.isLoop () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW4_6:
                return transport.isRecording () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;

            case ROW_SELECT_1:
                return isFunctions || isFixed ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW_SELECT_2:
                return isDevice ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW_SELECT_3:
                return isTrackToggles || isFrame || isPreset ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW_SELECT_4:
                return isTrack ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW_SELECT_6:
                return isVolume ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF;
            case ROW_SELECT_7:
                return SLControlSurface.MKII_BUTTON_STATE_OFF;

            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Use drum pads for mode selection to support Remote Zero MkII
        if (this.surface.getConfiguration ().isDrumpadsAsModeSelection ())
        {
            if (velocity > 0)
            {
                final int index = note - 36;
                new ButtonRowSelectCommand<> (index > 3 ? 5 : index, this.model, this.surface).execute (ButtonEvent.DOWN, 127);
            }
            return;
        }

        this.surface.sendMidiEvent (MidiConstants.CMD_NOTE_ON, note, velocity);
    }
}