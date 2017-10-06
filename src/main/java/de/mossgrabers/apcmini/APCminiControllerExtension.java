// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.apcmini;

import de.mossgrabers.apcmini.command.trigger.ShiftCommand;
import de.mossgrabers.apcmini.command.trigger.TrackSelectCommand;
import de.mossgrabers.apcmini.controller.APCminiColors;
import de.mossgrabers.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.apcmini.controller.APCminiMidiInput;
import de.mossgrabers.apcmini.controller.APCminiScales;
import de.mossgrabers.apcmini.mode.DeviceMode;
import de.mossgrabers.apcmini.mode.Modes;
import de.mossgrabers.apcmini.mode.PanMode;
import de.mossgrabers.apcmini.mode.SendMode;
import de.mossgrabers.apcmini.mode.VolumeMode;
import de.mossgrabers.apcmini.view.BrowserView;
import de.mossgrabers.apcmini.view.DrumView;
import de.mossgrabers.apcmini.view.PlayView;
import de.mossgrabers.apcmini.view.RaindropsView;
import de.mossgrabers.apcmini.view.SequencerView;
import de.mossgrabers.apcmini.view.SessionView;
import de.mossgrabers.apcmini.view.ShiftView;
import de.mossgrabers.apcmini.view.Views;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterFaderAbsoluteCommand;
import de.mossgrabers.framework.controller.AbstractControllerExtension;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.midi.MidiInput;
import de.mossgrabers.framework.midi.MidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;

import com.bitwig.extension.controller.api.ControllerHost;


/**
 * Bitwig Studio extension to support the Akai APCmini controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiControllerExtension extends AbstractControllerExtension<APCminiControlSurface, APCminiConfiguration>
{
    /**
     * Constructor.
     *
     * @param extensionDefinition The extension definition
     * @param host The Bitwig host
     */
    protected APCminiControllerExtension (final APCminiControllerExtensionDefinition extensionDefinition, final ControllerHost host)
    {
        super (extensionDefinition, host);
        this.colorManager = new ColorManager ();
        APCminiColors.addColors (this.colorManager);
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new APCminiConfiguration (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
        this.updateButtons ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new APCminiScales (this.valueChanger, 36, 100, 8, 8);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        this.model = new Model (this.getHost (), this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1);
        final TrackBankProxy trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addTrackSelectionObserver (this::handleTrackChange);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final ControllerHost host = this.getHost ();
        final MidiOutput output = new MidiOutput (host);
        final MidiInput input = new APCminiMidiInput ();
        final APCminiControlSurface surface = new APCminiControlSurface (host, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (host));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        surface.getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateMode (null));
        surface.getModeManager ().addModeListener ( (previousModeId, activeModeId) -> this.updateMode (activeModeId));
        this.createScaleObservers (this.configuration);

        this.configuration.addSettingObserver (APCminiConfiguration.FADER_CTRL, () -> {
            final ModeManager modeManager = surface.getModeManager ();
            switch (this.configuration.getFaderCtrl ())
            {
                case "Volume":
                    modeManager.setActiveMode (Modes.MODE_VOLUME);
                    break;
                case "Pan":
                    modeManager.setActiveMode (Modes.MODE_PAN);
                    break;
                case "Send 1":
                    modeManager.setActiveMode (Modes.MODE_SEND1);
                    break;
                case "Send 2":
                    modeManager.setActiveMode (Modes.MODE_SEND2);
                    break;
                case "Send 3":
                    modeManager.setActiveMode (Modes.MODE_SEND3);
                    break;
                case "Send 4":
                    modeManager.setActiveMode (Modes.MODE_SEND4);
                    break;
                case "Send 5":
                    modeManager.setActiveMode (Modes.MODE_SEND5);
                    break;
                case "Send 6":
                    modeManager.setActiveMode (Modes.MODE_SEND6);
                    break;
                case "Send 7":
                    modeManager.setActiveMode (Modes.MODE_SEND7);
                    break;
                case "Send 8":
                    modeManager.setActiveMode (Modes.MODE_SEND8);
                    break;
                case "Device":
                    modeManager.setActiveMode (Modes.MODE_DEVICE);
                    break;
            }
        });

        this.configuration.addSettingObserver (APCminiConfiguration.SOFT_KEYS, () -> {
            for (int i = 0; i < APCminiConfiguration.SOFT_KEYS_OPTIONS.length; i++)
            {
                final String opt = APCminiConfiguration.SOFT_KEYS_OPTIONS[i];
                if (opt.equals (this.configuration.getSoftKeys ()))
                    surface.setTrackState (i);
            }
        });

    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode (surface, this.model));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode (surface, this.model));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Modes.MODE_SEND1.intValue() + i, new SendMode (i, surface, this.model));
        modeManager.registerMode (Modes.MODE_DEVICE, new DeviceMode (surface, this.model));

        modeManager.setDefaultMode (Modes.MODE_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        this.addNoteCommand (Commands.COMMAND_SHIFT, APCminiControlSurface.APC_BUTTON_SHIFT, new ShiftCommand (this.model, surface));
        for (int i = 0; i < 8; i++)
        {
            this.addNoteCommand (Commands.COMMAND_ROW_SELECT_1.intValue() + i, APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, new TrackSelectCommand (i, this.model, surface));
            this.addNoteCommand (Commands.COMMAND_SCENE1.intValue() + i, APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, new SceneCommand<> (7 - i, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB, APCminiControlSurface.APC_KNOB_MASTER_LEVEL, new MasterFaderAbsoluteCommand<> (this.model, surface));

        for (int i = 0; i < 8; i++)
        {
            final Integer knobCommand = Commands.CONT_COMMAND_FADER1.intValue() + i;
            this.addContinuousCommand (knobCommand, APCminiControlSurface.APC_KNOB_TRACK_LEVEL1 + i, new KnobRowModeCommand<> (i, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        this.getHost ().scheduleTask ( () -> {
            final APCminiControlSurface surface = this.getSurface ();
            surface.getModeManager ().setActiveMode (Modes.MODE_VOLUME);
            surface.getViewManager ().setActiveView (Views.VIEW_SESSION);
        }, 100);
    }


    private void updateButtons ()
    {
        final View activeView = this.getSurface ().getViewManager ().getActiveView ();
        if (activeView != null)
            ((SceneView) activeView).updateSceneButtons ();
    }


    private void updateMode (final Integer mode)
    {
        this.updateIndication (mode == null ? this.getSurface ().getModeManager ().getActiveModeId () : mode);
    }


    private void updateIndication (final Integer mode)
    {
        final TrackBankProxy tb = this.model.getTrackBank ();
        final EffectTrackBankProxy tbe = this.model.getEffectTrackBank ();
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final boolean isShiftView = viewManager.isActiveView (Views.VIEW_SHIFT);
        final boolean isSession = viewManager.isActiveView (Views.VIEW_SESSION) || isShiftView;
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isPan = Modes.MODE_PAN.equals (mode);
        final boolean isDevice = Modes.MODE_DEVICE.equals (mode);

        tb.setIndication (!isEffect && isSession);
        tbe.setIndication (isEffect && isSession);

        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        for (int i = 0; i < 8; i++)
        {
            tb.setVolumeIndication (i, !isEffect);
            tb.setPanIndication (i, !isEffect && isPan);
            for (int j = 0; j < 8; j++)
                tb.setSendIndication (i, j, !isEffect && (Modes.MODE_SEND1.equals (mode) && j == 0 || Modes.MODE_SEND2.equals (mode) && j == 1 || Modes.MODE_SEND3.equals (mode) && j == 2 || Modes.MODE_SEND4.equals (mode) && j == 3 || Modes.MODE_SEND5.equals (mode) && j == 4 || Modes.MODE_SEND6.equals (mode) && j == 5 || Modes.MODE_SEND7.equals (mode) && j == 6 || Modes.MODE_SEND8.equals (mode) && j == 7));

            tbe.setVolumeIndication (i, isEffect);
            tbe.setPanIndication (i, isEffect && isPan);

            cursorDevice.getParameter (i).setIndication (isDevice || isShiftView);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        if (!isSelected)
            return;

        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.setDrumOctave (0);
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
