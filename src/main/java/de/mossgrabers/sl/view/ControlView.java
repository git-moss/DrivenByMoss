// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.TransportProxy;
import de.mossgrabers.framework.daw.data.SlotData;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.command.trigger.ButtonRowSelectCommand;
import de.mossgrabers.sl.command.trigger.P2ButtonCommand;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.mode.Modes;
import de.mossgrabers.sl.mode.device.DeviceParamsMode;
import de.mossgrabers.sl.mode.device.DevicePresetsMode;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends AbstractView<SLControlSurface, SLConfiguration> implements SLView
{
    private boolean          isTempoDec;
    private boolean          isTempoInc;
    private TransportControl transportControl;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final SLControlSurface surface, final Model model)
    {
        super ("Control", surface, model);
        this.transportControl = new TransportControl (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow1 (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        Integer activeModeId = modeManager.getActiveModeId ();
        if (activeModeId == Modes.MODE_VIEW_SELECT)
        {
            if (index == 1)
            {
                this.surface.getViewManager ().setActiveView (Views.VIEW_PLAY);
                if (modeManager.getPreviousModeId () == Modes.MODE_VOLUME)
                    modeManager.restoreMode ();
                else
                    modeManager.setActiveMode (Modes.MODE_SESSION);
            }
            else
                modeManager.restoreMode ();
            this.surface.turnOffTransport ();
            return;
        }

        if (activeModeId != Modes.MODE_FUNCTIONS && activeModeId != Modes.MODE_FIXED)
        {
            modeManager.setActiveMode (Modes.MODE_FUNCTIONS);
            activeModeId = Modes.MODE_FUNCTIONS;
        }

        if (activeModeId == Modes.MODE_FIXED)
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
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final TrackData t = tb.getSelectedTrack ();
                if (t != null)
                {
                    final SlotData [] slotIndexes = tb.getSelectedSlots (t.getIndex ());
                    final int slotIndex = slotIndexes.length == 0 ? 0 : slotIndexes[0].getIndex ();
                    for (int i = 0; i < 8; i++)
                    {
                        final int sIndex = (slotIndex + i) % 8;
                        final SlotData s = t.getSlots ()[sIndex];
                        if (!s.hasContent ())
                        {
                            final ClipLauncherSlotBank slots = tb.getClipLauncherSlots (t.getIndex ());
                            slots.createEmptyClip (sIndex, (int) Math.pow (2, this.surface.getConfiguration ().getNewClipLength ()));
                            if (slotIndex != sIndex)
                                slots.select (sIndex);
                            slots.launch (sIndex);
                            this.model.getTransport ().setLauncherOverdub (true);
                            return;
                        }
                    }
                }
                this.surface.getDisplay ().notify ("In the current selected grid view there is no empty slot. Please scroll down.");
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
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow2 (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        Integer cm = modeManager.getActiveModeId ();
        if (cm != Modes.MODE_TRACK_TOGGLES && cm != Modes.MODE_FRAME && cm != Modes.MODE_BROWSER)
        {
            modeManager.setActiveMode (Modes.MODE_TRACK_TOGGLES);
            cm = Modes.MODE_TRACK_TOGGLES;
        }

        if (cm == Modes.MODE_FRAME)
        {
            modeManager.getMode (Modes.MODE_FRAME).onRowButton (0, index, event);
            return;
        }
        else if (cm == Modes.MODE_BROWSER)
        {
            modeManager.getMode (Modes.MODE_BROWSER).onRowButton (0, index, event);
            return;
        }

        AbstractTrackBankProxy tb;
        TrackData track;
        switch (index)
        {
            // Mute
            case 0:
                tb = this.model.getCurrentTrackBank ();
                track = tb.getSelectedTrack ();
                if (track != null)
                    tb.toggleMute (track.getIndex ());
                break;

            // Solo
            case 1:
                tb = this.model.getCurrentTrackBank ();
                track = tb.getSelectedTrack ();
                if (track != null)
                    tb.toggleSolo (track.getIndex ());
                break;

            // Arm
            case 2:
                tb = this.model.getCurrentTrackBank ();
                track = tb.getSelectedTrack ();
                if (track != null)
                    tb.toggleArm (track.getIndex ());
                break;

            // Write
            case 3:
                this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;

            // Browse
            case 4:
                this.model.getBrowser ().browseForPresets ();
                modeManager.setActiveMode (Modes.MODE_BROWSER);
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
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow3 (final int index, final ButtonEvent event)
    {
        if (!this.model.getMasterTrack ().isSelected ())
            this.selectTrack (index);
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
        this.model.getTransport ().changeTempo (this.isTempoInc);
        this.surface.scheduleTask (this::doChangeTempo, 200);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow1Select ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (modeManager.getActiveModeId () == Modes.MODE_FUNCTIONS ? Modes.MODE_FIXED : Modes.MODE_FUNCTIONS);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonRow2Select ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (modeManager.getActiveModeId () == Modes.MODE_TRACK_TOGGLES ? Modes.MODE_FRAME : Modes.MODE_TRACK_TOGGLES);
    }


    /** {@inheritDoc} */
    @Override
    public void onButtonP1 (final boolean isUp, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final Integer activeModeId = modeManager.getActiveModeId ();
        if (activeModeId == Modes.MODE_FUNCTIONS || activeModeId == Modes.MODE_FIXED)
            this.onButtonRow1Select ();
        else if (activeModeId == Modes.MODE_VOLUME)
            new P2ButtonCommand (isUp, this.model, this.surface).execute (event);
        else if (activeModeId == Modes.MODE_TRACK || activeModeId == Modes.MODE_MASTER)
            new ButtonRowSelectCommand<> (3, this.model, this.surface).execute (event);
        else if (activeModeId == Modes.MODE_TRACK_TOGGLES || activeModeId == Modes.MODE_FRAME)
            this.onButtonRow2Select ();
        else
        {
            if (isUp)
                ((DeviceParamsMode) modeManager.getMode (Modes.MODE_PARAMS)).nextPage ();
            else
                ((DeviceParamsMode) modeManager.getMode (Modes.MODE_PARAMS)).previousPage ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final CursorDeviceProxy cd = this.model.getCursorDevice ();
        final TransportProxy transport = this.model.getTransport ();
        final int clipLength = this.surface.getConfiguration ().getNewClipLength ();

        final Integer mode = this.surface.getModeManager ().getActiveModeId ();
        final boolean isTrack = mode == Modes.MODE_TRACK;
        final boolean isTrackToggles = mode == Modes.MODE_TRACK_TOGGLES;
        final boolean isVolume = mode == Modes.MODE_VOLUME;
        final boolean isMaster = mode == Modes.MODE_MASTER;
        final boolean isFixed = mode == Modes.MODE_FIXED;
        final boolean isFrame = mode == Modes.MODE_FRAME;
        final boolean isPreset = mode == Modes.MODE_BROWSER;
        final boolean isDevice = mode == Modes.MODE_PARAMS;
        final boolean isFunctions = mode == Modes.MODE_FUNCTIONS;

        if (mode == Modes.MODE_VIEW_SELECT)
        {
            for (int i = 0; i < 8; i++)
                this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_1 + i, SLControlSurface.MKII_BUTTON_STATE_OFF);
        }
        else
        {
            // Button row 1: Clip length or functions
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_1, !isFunctions && clipLength == 0 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_2, !isFunctions && clipLength == 1 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_3, !isFunctions && clipLength == 2 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_4, !isFunctions && clipLength == 3 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_5, !isFunctions && clipLength == 4 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_6, isFunctions && this.model.getCursorDevice ().isWindowOpen () || !isFunctions && clipLength == 5 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_7, isFunctions && transport.isMetronomeOn () || !isFunctions && clipLength == 6 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW1_8, !isFunctions && clipLength == 7 ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        }

        // Button row 2: Track toggles / Browse
        if (mode == Modes.MODE_BROWSER)
        {
            final int selMode = ((DevicePresetsMode) this.surface.getModeManager ().getMode (Modes.MODE_BROWSER)).getSelectionMode ();
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_1, SLControlSurface.MKII_BUTTON_STATE_ON);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_2, SLControlSurface.MKII_BUTTON_STATE_ON);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_3, selMode == DevicePresetsMode.SELECTION_OFF ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_4, selMode == DevicePresetsMode.SELECTION_OFF ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_5, selMode == DevicePresetsMode.SELECTION_OFF ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_6, selMode == DevicePresetsMode.SELECTION_OFF ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_7, selMode == DevicePresetsMode.SELECTION_OFF ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_8, SLControlSurface.MKII_BUTTON_STATE_ON);
        }
        else
        {
            final boolean isNoOverlayMode = mode != Modes.MODE_FRAME && mode != Modes.MODE_BROWSER;
            final TrackData track = tb.getSelectedTrack ();
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_1, isNoOverlayMode && track != null && track.isMute () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_2, isNoOverlayMode && track != null && track.isSolo () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_3, isNoOverlayMode && track != null && track.isRecArm() ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_4, transport.isWritingArrangerAutomation () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_5, SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_6, this.model.getCursorDevice ().isEnabled () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_7, isNoOverlayMode && cd.canSelectPreviousFX () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW2_8, isNoOverlayMode && cd.canSelectNextFX () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        }

        // Button row 3: Selected track indication
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW3_1 + i, tb.getTrack (i).isSelected () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);

        // LED indications for device parameters
        ((DeviceParamsMode) this.surface.getModeManager ().getMode (Modes.MODE_PARAMS)).setLEDs ();

        // Transport buttons
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_3, !transport.isPlaying () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_4, transport.isPlaying () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_5, transport.isLoop () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROW4_6, transport.isRecording () ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);

        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL1, isFunctions || isFixed ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL2, isDevice ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL3, isTrackToggles || isFrame || isPreset ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL4, isTrack || isMaster ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL6, isVolume ? SLControlSurface.MKII_BUTTON_STATE_ON : SLControlSurface.MKII_BUTTON_STATE_OFF);
        this.surface.updateButton (SLControlSurface.MKII_BUTTON_ROWSEL7, SLControlSurface.MKII_BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }
}