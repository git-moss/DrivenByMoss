// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini;

import de.mossgrabers.controller.apcmini.command.trigger.TrackSelectCommand;
import de.mossgrabers.controller.apcmini.controller.APCminiColors;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.apcmini.controller.APCminiScales;
import de.mossgrabers.controller.apcmini.view.BrowserView;
import de.mossgrabers.controller.apcmini.view.DrumView;
import de.mossgrabers.controller.apcmini.view.PlayView;
import de.mossgrabers.controller.apcmini.view.RaindropsView;
import de.mossgrabers.controller.apcmini.view.SequencerView;
import de.mossgrabers.controller.apcmini.view.SessionView;
import de.mossgrabers.controller.apcmini.view.ShiftView;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterFaderAbsoluteCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SendMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.Map;


/**
 * Support for the Akai APCmini controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiControllerSetup extends AbstractControllerSetup<APCminiControlSurface, APCminiConfiguration>
{
    private static final Map<String, Integer> FADER_CTRL_MODES = new HashMap<> ();
    static
    {
        FADER_CTRL_MODES.put ("Volume", Modes.MODE_VOLUME);
        FADER_CTRL_MODES.put ("Pan", Modes.MODE_PAN);
        FADER_CTRL_MODES.put ("Send 1", Modes.MODE_SEND1);
        FADER_CTRL_MODES.put ("Send 2", Modes.MODE_SEND2);
        FADER_CTRL_MODES.put ("Send 3", Modes.MODE_SEND3);
        FADER_CTRL_MODES.put ("Send 4", Modes.MODE_SEND4);
        FADER_CTRL_MODES.put ("Send 5", Modes.MODE_SEND5);
        FADER_CTRL_MODES.put ("Send 6", Modes.MODE_SEND6);
        FADER_CTRL_MODES.put ("Send 7", Modes.MODE_SEND7);
        FADER_CTRL_MODES.put ("Send 8", Modes.MODE_SEND8);
        FADER_CTRL_MODES.put ("Device", Modes.MODE_DEVICE_PARAMS);
    }


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public APCminiControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);
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
        final ModelSetup ms = new ModelSetup ();
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.setIndication (true);
        trackBank.addSelectionObserver ( (index, value) -> this.handleTrackChange (value));
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Akai APCmini");
        final APCminiControlSurface surface = new APCminiControlSurface (this.model.getHost (), this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
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
            final Integer modeID = FADER_CTRL_MODES.get (this.configuration.getFaderCtrl ());
            if (modeID != null)
                surface.getModeManager ().setActiveMode (modeID);
        });

        this.configuration.addSettingObserver (APCminiConfiguration.SOFT_KEYS, () -> {
            final String softKeys = this.configuration.getSoftKeys ();
            for (int i = 0; i < APCminiConfiguration.SOFT_KEYS_OPTIONS.length; i++)
            {
                if (APCminiConfiguration.SOFT_KEYS_OPTIONS[i].equals (softKeys))
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
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode<> (surface, this.model, true));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + i), new SendMode<> (i, surface, this.model, true));
        modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true));

        modeManager.setDefaultMode (Modes.MODE_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (surface, this.model));

        if (this.host.hasClips ())
        {
            viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
            viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
            viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
            viewManager.registerView (Views.VIEW_RAINDROPS, new RaindropsView (surface, this.model));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        this.addNoteCommand (Commands.COMMAND_SHIFT, APCminiControlSurface.APC_BUTTON_SHIFT, new ToggleShiftViewCommand<> (this.model, surface));
        for (int i = 0; i < 8; i++)
        {
            this.addNoteCommand (Integer.valueOf (Commands.COMMAND_ROW_SELECT_1.intValue () + i), APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, new TrackSelectCommand (i, this.model, surface));
            this.addNoteCommand (Integer.valueOf (Commands.COMMAND_SCENE1.intValue () + i), APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, new SceneCommand<> (7 - i, this.model, surface));
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
            final Integer knobCommand = Integer.valueOf (Commands.CONT_COMMAND_FADER1.intValue () + i);
            this.addContinuousCommand (knobCommand, APCminiControlSurface.APC_KNOB_TRACK_LEVEL1 + i, new KnobRowModeCommand<> (i, this.model, surface));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActiveMode (Modes.MODE_VOLUME);
        surface.getViewManager ().setActiveView (Views.VIEW_PLAY);
    }


    private void updateButtons ()
    {
        final View activeView = this.getSurface ().getViewManager ().getActiveView ();
        if (activeView != null)
            ((SceneView) activeView).updateSceneButtons ();
    }


    private void updateMode (final Integer mode)
    {
        this.updateIndication (mode == null ? this.getSurface ().getModeManager ().getActiveOrTempModeId () : mode);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        if (mode == this.currentMode)
            return;
        this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final boolean isShiftView = viewManager.isActiveView (Views.VIEW_SHIFT);
        final boolean isSession = viewManager.isActiveView (Views.VIEW_SESSION) || isShiftView;
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isPan = Modes.MODE_PAN.equals (mode);
        final boolean isDevice = Modes.MODE_DEVICE_PARAMS.equals (mode);

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect);
            track.setPanIndication (!isEffect && isPan);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 8; j++)
                sendBank.getItem (j).setIndication (!isEffect && (Modes.MODE_SEND1.equals (mode) && j == 0 || Modes.MODE_SEND2.equals (mode) && j == 1 || Modes.MODE_SEND3.equals (mode) && j == 2 || Modes.MODE_SEND4.equals (mode) && j == 3 || Modes.MODE_SEND5.equals (mode) && j == 4 || Modes.MODE_SEND6.equals (mode) && j == 5 || Modes.MODE_SEND7.equals (mode) && j == 6 || Modes.MODE_SEND8.equals (mode) && j == 7));

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            cursorDevice.getParameterBank ().getItem (i).setIndication (isDevice || isShiftView);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
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
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}
