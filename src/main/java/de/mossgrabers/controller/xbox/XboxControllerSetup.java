// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.xbox;

import de.mossgrabers.controller.xbox.controller.XboxControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;


/**
 * Support for the Ableton Xbox 1 and Xbox 2 controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XboxControllerSetup extends AbstractControllerSetup<XboxControlSurface, XboxConfiguration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public XboxControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);

        this.colorManager = new ColorManager ();
        // TODO
        // XboxColors.addColors (this.colorManager, isXbox2);
        // TODO
        this.valueChanger = new DefaultValueChanger (1024, 10, 1);
        this.configuration = new XboxConfiguration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        // TODO
        // this.flushSurfaces ();
        //
        // this.updateButtons ();
        // final XboxControlSurface surface = this.getSurface ();
        // this.updateMode (surface.getModeManager ().getActiveOrTempModeId ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        // TODO
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IHost host = this.model.getHost ();
        // final XboxUsbDevice usbDevice = new XboxUsbDevice (host);

        final XboxControlSurface surface = new XboxControlSurface (this.model.getHost (), this.colorManager, this.configuration);
        this.surfaces.add (surface);
        // TODO
        // surface.getModeManager ().setDefaultMode (Modes.MODE_TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        // TODO
        // final XboxControlSurface surface = this.getSurface ();
        // final ModeManager modeManager = surface.getModeManager ();
        // modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        // TODO
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        // TODO
        // final XboxControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // TODO
        // final XboxControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // this.addTriggerCommand (Commands.COMMAND_PLAY, XboxControlSurface.PUSH_BUTTON_PLAY, new
        // PlayCommand<> (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        // TODO
        // final XboxControlSurface surface = this.getSurface ();
        // this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB,
        // XboxControlSurface.PUSH_KNOB9, new MasterVolumeCommand<> (this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // TODO
        // final XboxControlSurface surface = this.getSurface ();
        // surface.getViewManager ().setActiveView (this.configuration.getDefaultNoteView ());
    }


    private void updateButtons ()
    {
        // TODO
        // final ITransport t = this.model.getTransport ();
        // final XboxControlSurface surface = this.getSurface ();
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_METRONOME, t.isMetronomeOn () ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_PLAY, t.isPlaying () ?
        // XboxColors.PUSH_BUTTON_STATE_PLAY_HI : XboxColors.PUSH_BUTTON_STATE_PLAY_ON);
        //
        // final boolean isShift = surface.isShiftPressed ();
        // final boolean isFlipRecord = this.configuration.isFlipRecord ();
        // final boolean isRecordShifted = isShift && !isFlipRecord || !isShift && isFlipRecord;
        // if (isRecordShifted)
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_AUTOMATION,
        // t.isWritingClipLauncherAutomation () ? XboxColors.PUSH_BUTTON_STATE_REC_HI :
        // XboxColors.PUSH_BUTTON_STATE_REC_ON);
        // else
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_AUTOMATION,
        // t.isWritingArrangerAutomation () ? XboxColors.PUSH_BUTTON_STATE_REC_HI :
        // XboxColors.PUSH_BUTTON_STATE_REC_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_RECORD, isRecordShifted ?
        // t.isLauncherOverdub () ? XboxColors.PUSH_BUTTON_STATE_OVR_HI :
        // XboxColors.PUSH_BUTTON_STATE_OVR_ON : t.isRecording () ?
        // XboxColors.PUSH_BUTTON_STATE_REC_HI : XboxColors.PUSH_BUTTON_STATE_REC_ON);
        //
        // String repeatState = ColorManager.BUTTON_STATE_OFF;
        // if (this.host.hasRepeat ())
        // {
        // final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        // if (selectedTrack != null)
        // repeatState = selectedTrack.isNoteRepeat () ? ColorManager.BUTTON_STATE_HI :
        // ColorManager.BUTTON_STATE_ON;
        // }
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_REPEAT, repeatState);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_ACCENT,
        // this.configuration.isAccentActive () ? ColorManager.BUTTON_STATE_HI :
        // ColorManager.BUTTON_STATE_ON);
        //
        // final XboxConfiguration config = surface.getConfiguration ();
        //
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_ACCENT, config.isAccentActive () ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        //
        // final View activeView = viewManager.getActiveView ();
        // if (activeView != null)
        // {
        // ((XboxCursorCommand) activeView.getTriggerCommand
        // (Commands.COMMAND_ARROW_DOWN)).updateArrows ();
        // ((SceneView) activeView).updateSceneButtons ();
        // }
        //
        // final INoteClip clip = activeView instanceof AbstractSequencerView && !(activeView
        // instanceof ClipView) ? ((AbstractSequencerView<?, ?>) activeView).getClip () : null;
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_DEVICE_LEFT, clip != null &&
        // clip.canScrollStepsBackwards () ? ColorManager.BUTTON_STATE_ON :
        // ColorManager.BUTTON_STATE_OFF);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_DEVICE_RIGHT, clip != null &&
        // clip.canScrollStepsForwards () ? ColorManager.BUTTON_STATE_ON :
        // ColorManager.BUTTON_STATE_OFF);
    }


    private void updateMode (final Integer mode)
    {
        // TODO
        // if (mode == null)
        // return;
        //
        // this.updateIndication (mode);
        //
        // final boolean isMasterOn = Modes.MODE_MASTER.equals (mode) ||
        // Modes.MODE_MASTER_TEMP.equals (mode) || Modes.MODE_FRAME.equals (mode);
        // final boolean isVolumeOn = Modes.MODE_VOLUME.equals (mode) ||
        // Modes.MODE_CROSSFADER.equals (mode);
        // final boolean isPanOn = mode.intValue () >= Modes.MODE_PAN.intValue () && mode.intValue
        // () <= Modes.MODE_SEND8.intValue ();
        // final boolean isDeviceOn = Modes.isDeviceMode (mode);
        // boolean isMixOn = Modes.MODE_TRACK.equals (mode);
        // if (this.isXbox2)
        // isMixOn = isMixOn || isVolumeOn || isPanOn;
        //
        // final XboxControlSurface surface = this.getSurface ();
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_MASTER, isMasterOn ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_TRACK, isMixOn ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_VOLUME, isVolumeOn ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_PAN_SEND, isPanOn ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_DEVICE, isDeviceOn ?
        // ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_SCALES, Modes.MODE_SCALES.equals
        // (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_FIXED_LENGTH,
        // Modes.MODE_FIXED.equals (mode) ? ColorManager.BUTTON_STATE_HI :
        // ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_BROWSE, Modes.MODE_BROWSER.equals
        // (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_CLIP, Modes.MODE_CLIP.equals (mode)
        // ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
        //
        // if (this.isXbox2)
        // surface.updateButton (XboxControlSurface.PUSH_BUTTON_SETUP, Modes.MODE_SETUP.equals
        // (mode) ? ColorManager.BUTTON_STATE_HI : ColorManager.BUTTON_STATE_ON);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        // TODO
        // if (mode == this.currentMode)
        // return;
        // this.currentMode = mode;
        //
        // final ITrackBank tb = this.model.getTrackBank ();
        // final ITrackBank tbe = this.model.getEffectTrackBank ();
        // final XboxControlSurface surface = this.getSurface ();
        // final boolean isSession = surface.getViewManager ().isActiveView (Views.VIEW_SESSION);
        // final boolean isEffect = this.model.isEffectTrackBankActive ();
        // final boolean isPan = Modes.MODE_PAN.equals (mode);
        // final boolean isVolume = Modes.MODE_VOLUME.equals (mode);
        // final boolean isDevice = Modes.isDeviceMode (mode) || Modes.isLayerMode (mode);
        //
        // tb.setIndication (!isEffect && isSession);
        // if (tbe != null)
        // tbe.setIndication (isEffect && isSession);
        //
        // final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        // final ITrack selectedTrack = tb.getSelectedItem ();
        // final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        // for (int i = 0; i < tb.getPageSize (); i++)
        // {
        // final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i &&
        // Modes.MODE_TRACK.equals (mode);
        // final ITrack track = tb.getItem (i);
        // track.setVolumeIndication (!isEffect && (isVolume || hasTrackSel));
        // track.setPanIndication (!isEffect && (isPan || hasTrackSel));
        //
        // final ISendBank sendBank = track.getSendBank ();
        // for (int j = 0; j < sendBank.getPageSize (); j++)
        // sendBank.getItem (j).setIndication (!isEffect && (mode.intValue () -
        // Modes.MODE_SEND1.intValue () == j || hasTrackSel));
        //
        // if (tbe != null)
        // {
        // final ITrack fxTrack = tbe.getItem (i);
        // fxTrack.setVolumeIndication (isEffect);
        // fxTrack.setPanIndication (isEffect && isPan);
        // }
        //
        // parameterBank.getItem (i).setIndication (isDevice);
        // }
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
    {
        // TODO
        // if (!isSelected)
        // return;
        //
        // final XboxControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // final ModeManager modeManager = surface.getModeManager ();
        //
        // // Recall last used view (if we are not in session mode)
        // if (!viewManager.isActiveView (Views.VIEW_SESSION))
        // {
        // final ITrack selectedTrack = this.model.getSelectedTrack ();
        // if (selectedTrack != null)
        // {
        // final Integer preferredView = viewManager.getPreferredView (selectedTrack.getPosition
        // ());
        // viewManager.setActiveView (preferredView == null ? this.configuration.getDefaultNoteView
        // () : preferredView);
        // }
        // }
        //
        // if (modeManager.isActiveOrTempMode (Modes.MODE_MASTER))
        // modeManager.setActiveMode (Modes.MODE_TRACK);
        //
        // if (viewManager.isActiveView (Views.VIEW_PLAY))
        // viewManager.getActiveView ().updateNoteMapping ();
        //
        // // Reset drum octave because the drum pad bank is also reset
        // this.scales.setDrumOctave (0);
        // if (viewManager.isActiveView (Views.VIEW_DRUM))
        // viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
