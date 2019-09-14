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
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterFaderAbsoluteCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
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
    private static final Map<String, Modes> FADER_CTRL_MODES = new HashMap<> ();
    static
    {
        FADER_CTRL_MODES.put ("Volume", Modes.VOLUME);
        FADER_CTRL_MODES.put ("Pan", Modes.PAN);
        FADER_CTRL_MODES.put ("Send 1", Modes.SEND1);
        FADER_CTRL_MODES.put ("Send 2", Modes.SEND2);
        FADER_CTRL_MODES.put ("Send 3", Modes.SEND3);
        FADER_CTRL_MODES.put ("Send 4", Modes.SEND4);
        FADER_CTRL_MODES.put ("Send 5", Modes.SEND5);
        FADER_CTRL_MODES.put ("Send 6", Modes.SEND6);
        FADER_CTRL_MODES.put ("Send 7", Modes.SEND7);
        FADER_CTRL_MODES.put ("Send 8", Modes.SEND8);
        FADER_CTRL_MODES.put ("Device", Modes.DEVICE_PARAMS);
    }


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public APCminiControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);
        this.colorManager = new ColorManager ();
        APCminiColors.addColors (this.colorManager);
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new APCminiConfiguration (host, this.valueChanger);
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
        this.surfaces.add (new APCminiControlSurface (this.host, this.colorManager, this.configuration, output, input));
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
            final Modes modeID = FADER_CTRL_MODES.get (this.configuration.getFaderCtrl ());
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
        modeManager.registerMode (Modes.VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.PAN, new PanMode<> (surface, this.model, true));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Modes.get (Modes.SEND1, i), new SendMode<> (i, surface, this.model, true));
        modeManager.registerMode (Modes.DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true));

        modeManager.setDefaultMode (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.registerView (Views.BROWSER, new BrowserView (surface, this.model));

        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.RAINDROPS, new RaindropsView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();

        // Nop triggers are added to support the CC button updates

        this.addNoteCommand (TriggerCommandID.SHIFT, APCminiControlSurface.APC_BUTTON_SHIFT, new ToggleShiftViewCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SHIFT, APCminiControlSurface.APC_BUTTON_SHIFT, NopCommand.INSTANCE);

        for (int i = 0; i < 8; i++)
        {
            final TriggerCommandID commandID1 = TriggerCommandID.get (TriggerCommandID.ROW_SELECT_1, i);
            final TriggerCommandID commandID2 = TriggerCommandID.get (TriggerCommandID.SCENE1, i);

            this.addNoteCommand (commandID1, APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, new TrackSelectCommand (i, this.model, surface));
            this.addNoteCommand (commandID2, APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, new SceneCommand<> (i, this.model, surface));

            this.addTriggerCommand (commandID1, APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, NopCommand.INSTANCE);
            this.addTriggerCommand (commandID2, APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, NopCommand.INSTANCE);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        this.addContinuousCommand (ContinuousCommandID.MASTER_KNOB, APCminiControlSurface.APC_KNOB_MASTER_LEVEL, new MasterFaderAbsoluteCommand<> (this.model, surface));

        for (int i = 0; i < 8; i++)
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.FADER1, i), APCminiControlSurface.APC_KNOB_TRACK_LEVEL1 + i, new KnobRowModeCommand<> (i, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActiveMode (Modes.VOLUME);
        surface.getViewManager ().setActiveView (Views.PLAY);
        this.host.scheduleTask ( () -> {
            surface.getPadGrid ().forceFlush ();
        }, 1000);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateButtons ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final View activeView = surface.getViewManager ().getActiveView ();
        if (activeView != null)
            ((SceneView) activeView).updateSceneButtons ();
    }


    private void updateMode (final Modes mode)
    {
        this.updateIndication (mode == null ? this.getSurface ().getModeManager ().getActiveOrTempModeId () : mode);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        if (this.currentMode != null && this.currentMode.equals (mode))
            return;
        this.currentMode = mode;

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final boolean isShiftView = viewManager.isActiveView (Views.SHIFT);
        final boolean isSession = viewManager.isActiveView (Views.SESSION) || isShiftView;
        final boolean isEffect = this.model.isEffectTrackBankActive ();
        final boolean isPan = Modes.PAN.equals (mode);
        final boolean isDevice = Modes.DEVICE_PARAMS.equals (mode);

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
                sendBank.getItem (j).setIndication (!isEffect && (Modes.SEND1.equals (mode) && j == 0 || Modes.SEND2.equals (mode) && j == 1 || Modes.SEND3.equals (mode) && j == 2 || Modes.SEND4.equals (mode) && j == 3 || Modes.SEND5.equals (mode) && j == 4 || Modes.SEND6.equals (mode) && j == 5 || Modes.SEND7.equals (mode) && j == 6 || Modes.SEND8.equals (mode) && j == 7));

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
        if (viewManager.isActiveView (Views.PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        if (viewManager.isActiveView (Views.PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.resetDrumOctave ();
        if (viewManager.isActiveView (Views.DRUM))
            viewManager.getView (Views.DRUM).updateNoteMapping ();
    }
}
