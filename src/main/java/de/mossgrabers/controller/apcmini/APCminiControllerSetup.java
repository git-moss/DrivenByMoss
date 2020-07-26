// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.apcmini;

import de.mossgrabers.controller.apcmini.command.trigger.TrackSelectCommand;
import de.mossgrabers.controller.apcmini.controller.APCminiColorManager;
import de.mossgrabers.controller.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.apcmini.controller.APCminiScales;
import de.mossgrabers.controller.apcmini.view.APCminiView;
import de.mossgrabers.controller.apcmini.view.BrowserView;
import de.mossgrabers.controller.apcmini.view.DrumView;
import de.mossgrabers.controller.apcmini.view.PlayView;
import de.mossgrabers.controller.apcmini.view.RaindropsView;
import de.mossgrabers.controller.apcmini.view.SequencerView;
import de.mossgrabers.controller.apcmini.view.SessionView;
import de.mossgrabers.controller.apcmini.view.ShiftView;
import de.mossgrabers.controller.apcmini.view.TrackButtons;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.continuous.MasterFaderAbsoluteCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.DefaultValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SendMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
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
    private static final String []          ROW_NAMES        =
    {
        "Track 1\nUp",
        "Track 2\nDown",
        "Track 3\nLeft",
        "Track 4\nRight",
        "Track 5\nVolume",
        "Track 6\nPan",
        "Track 7\nSend",
        "Track 8\nDevice",
    };

    private static final String []          COL_NAMES        =
    {
        "Scene 1\n Clip Stop",
        "Scene 2\nSolo",
        "Scene 3\nRec Arm",
        "Scene 4\nMute",
        "Scene 5\nSelect",
        "Scene 6\n-",
        "Scene 7\n-",
        "Scene 8\nStop All Clips",
    };

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

        this.colorManager = new APCminiColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new APCminiConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
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
        final TrackButtons trackButtons = new TrackButtons (surface, this.model);

        viewManager.registerView (Views.PLAY, new PlayView (surface, this.model, trackButtons));
        viewManager.registerView (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.registerView (Views.BROWSER, new BrowserView (surface, this.model));

        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model, trackButtons));
        viewManager.registerView (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.RAINDROPS, new RaindropsView (surface, this.model));
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

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Views.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        this.addButton (ButtonID.SHIFT, "Shift", new ToggleShiftViewCommand<> (this.model, surface), APCminiControlSurface.APC_BUTTON_SHIFT);

        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            final ButtonID buttonID = ButtonID.get (ButtonID.SCENE1, i);
            this.addButton (buttonID, COL_NAMES[i], new ViewButtonCommand<> (buttonID, surface), APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, () -> this.getViewColor (buttonID));

            this.addButton (ButtonID.get (ButtonID.ROW_SELECT_1, i), ROW_NAMES[i], new TrackSelectCommand (i, this.model, surface), APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, () -> {
                final View view = viewManager.getActiveView ();
                if (view instanceof APCminiView)
                {
                    final int trackButtonColor = ((APCminiView) view).getTrackButtonColor (index);
                    // Track buttons are only red!
                    return trackButtonColor > 0 ? APCminiColorManager.APC_COLOR_RED : 0;
                }
                return APCminiColorManager.APC_COLOR_BLACK;
            });
        }
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        this.addFader (ContinuousID.FADER_MASTER, "Master", new MasterFaderAbsoluteCommand<> (this.model, surface), BindType.CC, APCminiControlSurface.APC_KNOB_MASTER_LEVEL);
        for (int i = 0; i < 8; i++)
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, APCminiControlSurface.APC_KNOB_TRACK_LEVEL1 + i);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final double width = 10;
        final double height = 6;
        final double space = 2;
        final double stepX = width + space;
        final double stepY = height + space;

        final APCminiControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.SHIFT).setBounds (space + 8 * stepX, space + 8 * stepY, width, height);
        surface.getContinuous (ContinuousID.FADER_MASTER).setBounds (space + 8 * stepX, space + 9 * stepY, width, width * 3);

        for (int i = 0; i < 8; i++)
        {
            final double x = i * stepX;
            final double y = i * stepY;

            for (int k = 0; k < 8; k++)
                surface.getButton (ButtonID.get (ButtonID.PAD1, k * 8 + i)).setBounds (space + x, space + (7 - k) * stepY, width, height);

            surface.getButton (ButtonID.get (ButtonID.SCENE1, i)).setBounds (space + 8.0 * stepX, space + y, width, height);
            surface.getButton (ButtonID.get (ButtonID.ROW_SELECT_1, i)).setBounds (space + x, space + 8.0 * stepY, width, height);
            surface.getContinuous (ContinuousID.get (ContinuousID.FADER1, i)).setBounds (space + x, space + 9.0 * stepY, width, width * 3);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        surface.getModeManager ().setActiveMode (Modes.VOLUME);
        surface.getViewManager ().setActiveView (Views.PLAY);
        this.host.scheduleTask (surface.getPadGrid ()::forceFlush, 1000);
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
