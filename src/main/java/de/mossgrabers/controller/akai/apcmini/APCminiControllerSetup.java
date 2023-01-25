// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.apcmini;

import de.mossgrabers.controller.akai.apcmini.command.trigger.TrackSelectCommand;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiColorManager;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiControlSurface;
import de.mossgrabers.controller.akai.apcmini.controller.APCminiScales;
import de.mossgrabers.controller.akai.apcmini.view.APCMiniBrowserView;
import de.mossgrabers.controller.akai.apcmini.view.APCminiView;
import de.mossgrabers.controller.akai.apcmini.view.DrumView;
import de.mossgrabers.controller.akai.apcmini.view.PlayView;
import de.mossgrabers.controller.akai.apcmini.view.RaindropsView;
import de.mossgrabers.controller.akai.apcmini.view.SequencerView;
import de.mossgrabers.controller.akai.apcmini.view.SessionView;
import de.mossgrabers.controller.akai.apcmini.view.ShiftView;
import de.mossgrabers.controller.akai.apcmini.view.TrackButtons;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.view.ToggleShiftViewCommand;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Support for the Akai APCmini controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class APCminiControllerSetup extends AbstractControllerSetup<APCminiControlSurface, APCminiConfiguration>
{
    private static final List<ContinuousID> FADER_IDS        = ContinuousID.createSequentialList (ContinuousID.FADER1, 8);

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
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new APCminiConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new APCminiScales (this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
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
        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true, FADER_IDS));
        modeManager.register (Modes.PAN, new TrackPanMode<> (surface, this.model, true, FADER_IDS));
        for (int i = 0; i < 8; i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new TrackSendMode<> (i, surface, this.model, true, FADER_IDS));
        modeManager.register (Modes.DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true, FADER_IDS));

        modeManager.setDefaultID (Modes.VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final APCminiControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final TrackButtons trackButtons = new TrackButtons (surface, this.model);

        viewManager.register (Views.PLAY, new PlayView (surface, this.model, trackButtons));
        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
        viewManager.register (Views.BROWSER, new APCMiniBrowserView (surface, this.model));

        viewManager.register (Views.SESSION, new SessionView (surface, this.model, trackButtons));
        viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.RAINDROPS, new RaindropsView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        final APCminiControlSurface surface = this.getSurface ();
        this.createScaleObservers (this.configuration);

        this.configuration.addSettingObserver (APCminiConfiguration.FADER_CTRL, () -> {
            final Modes modeID = FADER_CTRL_MODES.get (this.configuration.getFaderCtrl ());
            if (modeID != null)
                surface.getModeManager ().setActive (modeID);
        });

        this.configuration.addSettingObserver (APCminiConfiguration.SOFT_KEYS, () -> {
            final int index = APCminiConfiguration.SOFT_KEYS_OPTIONS.indexOf (this.configuration.getSoftKeys ());
            if (index >= 0)
                surface.setTrackState (index);
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
            this.addButton (buttonID, COL_NAMES[i], new ViewButtonCommand<> (buttonID, surface), APCminiControlSurface.APC_BUTTON_SCENE_BUTTON1 + i, () -> this.getButtonColorFromActiveView (buttonID));

            this.addButton (ButtonID.get (ButtonID.ROW_SELECT_1, i), ROW_NAMES[i], new TrackSelectCommand (i, this.model, surface), APCminiControlSurface.APC_BUTTON_TRACK_BUTTON1 + i, () -> {
                final IView view = viewManager.getActive ();
                if (view instanceof final APCminiView miniView)
                {
                    final int trackButtonColor = miniView.getTrackButtonColor (index);
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

        this.addFader (ContinuousID.FADER_MASTER, "Master", null, BindType.CC, APCminiControlSurface.APC_KNOB_MASTER_LEVEL);
        surface.getContinuous (ContinuousID.FADER_MASTER).bind (this.model.getMasterTrack ().getVolumeParameter ());

        for (int i = 0; i < 8; i++)
            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, APCminiControlSurface.APC_KNOB_TRACK_LEVEL1 + i).setIndexInGroup (i);
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
        surface.getModeManager ().setActive (Modes.VOLUME);
        surface.getViewManager ().setActive (this.configuration.shouldStartWithSessionView () ? Views.SESSION : this.configuration.getPreferredNoteView ());
        this.host.scheduleTask (surface.getPadGrid ()::forceFlush, 1000);
    }
}
