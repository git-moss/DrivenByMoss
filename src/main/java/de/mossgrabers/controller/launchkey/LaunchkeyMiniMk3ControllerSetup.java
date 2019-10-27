// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchkey;

import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3Colors;
import de.mossgrabers.controller.launchkey.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.controller.launchkey.view.DrumConfigView;
import de.mossgrabers.controller.launchkey.view.DrumView;
import de.mossgrabers.controller.launchkey.view.PadModeSelectView;
import de.mossgrabers.controller.launchkey.view.SessionView;
import de.mossgrabers.controller.launchkey.view.UserPadView;
import de.mossgrabers.framework.command.ContinuousCommandID;
import de.mossgrabers.framework.command.SceneCommand;
import de.mossgrabers.framework.command.TriggerCommandID;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand.Direction;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Mode;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.device.UserMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SendMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.framework.view.View;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.Map;


/**
 * Support for the Novation Launchkey Mini Mk3 controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchkeyMiniMk3ControllerSetup extends AbstractControllerSetup<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration>
{
    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        0,  1,  2,  3,  8,  9, 10, 11,
        4,  5,  6,  7, 12, 13, 14, 15,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on

    private static final Map<Integer, Modes> MODES_SELECT = new HashMap<> ();
    private static final Map<Integer, Views> VIEWS_SELECT = new HashMap<> ();
    static
    {
        MODES_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_CUSTOM), Modes.USER);
        MODES_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_VOLUME), Modes.VOLUME);
        MODES_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_PARAMS), Modes.DEVICE_PARAMS);
        MODES_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_PAN), Modes.PAN);
        MODES_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_SEND1), Modes.SEND1);
        MODES_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_SEND2), Modes.SEND2);

        VIEWS_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.PAD_MODE_CUSTOM), Views.PLAY);
        VIEWS_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.PAD_MODE_DRUM), Views.DRUM);
        VIEWS_SELECT.put (Integer.valueOf (LaunchkeyMiniMk3ControlSurface.PAD_MODE_SESSION), Views.SESSION);
    }


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public LaunchkeyMiniMk3ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new ColorManager ();
        LaunchkeyMiniMk3Colors.addColors (this.colorManager);
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new LaunchkeyMiniMk3Configuration (host, this.valueChanger);
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 52, 8, 2);
        this.scales.setDrumMatrix (DRUM_MATRIX);
        this.scales.setDrumNoteEnd (52);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFlatTrackList (true);
        ms.setHasFullFlatTrackList (true);
        ms.setNumScenes (2);
        ms.setNumSends (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????", "90????", "81????", "91????", "82????", "92????", "83????", "93????", "84????", "94????", "85????", "95????", "86????", "96????", "87????", "97????", "88????", "98????", "89????", "99????", "8A????", "9A????", "8B????", "9B????", "8C????", "9C????", "8D????", "9D????", "8E????", "9E????");
        IMidiInput inputKeys = midiAccess.createInput (1, "Keyboard", "8?????" /* Note off */,
                "9?????" /* Note on */, "B?01??" /* Modulation */, "B?40??" /* Sustainpedal */,
                "E?????" /* Pitchbend */);

        final LaunchkeyMiniMk3ControlSurface surface = new LaunchkeyMiniMk3ControlSurface (this.host, this.colorManager, this.configuration, output, input, inputKeys, this::processProgramChangeAction);
        this.surfaces.add (surface);

        surface.setLaunchpadToDAW (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.getSurface ().getModeManager ().addModeListener ( (previousViewId, activeViewId) -> this.updateIndication (null));
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        modeManager.registerMode (Modes.VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.PAN, new PanMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.SEND1, new SendMode<> (0, surface, this.model, true));
        modeManager.registerMode (Modes.SEND2, new SendMode<> (1, surface, this.model, true));
        modeManager.registerMode (Modes.DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.USER, new UserMode<> (surface, this.model, true));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.registerView (Views.SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.CONTROL, new PadModeSelectView (surface, this.model));
        viewManager.registerView (Views.DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.PLAY, new UserPadView (surface, this.model));
        viewManager.registerView (Views.SHIFT, new DrumConfigView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();

        this.addTriggerCommand (TriggerCommandID.SHIFT, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SHIFT, NopCommand.INSTANCE);

        this.addTriggerCommand (TriggerCommandID.PLAY, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_PLAY, 15, new PlayCommand<> (this.model, surface));
        this.addTriggerCommand (TriggerCommandID.RECORD, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_RECORD, 15, new RecordCommand<> (this.model, surface));

        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_LEFT, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_LEFT, 15, new ModeCursorCommand<> (Direction.LEFT, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.MOVE_TRACK_RIGHT, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_RIGHT, 15, new ModeCursorCommand<> (Direction.RIGHT, this.model, surface));

        this.addTriggerCommand (TriggerCommandID.SCENE1, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE1, new SceneCommand<> (0, this.model, surface));
        this.addTriggerCommand (TriggerCommandID.SCENE2, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, new SceneCommand<> (1, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
        {
            final KnobRowModeCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> command = new KnobRowModeCommand<> (i, this.model, surface);
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.KNOB1, i), LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_KNOB_1 + i, 15, command);
            this.addContinuousCommand (ContinuousCommandID.get (ContinuousCommandID.KNOB1, i), LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_KNOB_1 + i, command);
        }

        this.addContinuousCommand (ContinuousCommandID.MODE_SELECTION, 0x09, 0x0F, value -> {
            final Modes mode = MODES_SELECT.get (Integer.valueOf (value));
            if (mode == null)
            {
                this.host.println ("Unknown knob mode " + value);
                return;
            }
            final ModeManager modeManager = this.getSurface ().getModeManager ();
            modeManager.setActiveMode (mode);
            surface.getDisplay ().notify (modeManager.getActiveOrTempMode ().getName ());
        });

        this.addContinuousCommand (ContinuousCommandID.VIEW_SELECTION, 0x03, 0x0F, value -> {
            final Views view = VIEWS_SELECT.get (Integer.valueOf (value));
            if (view == null)
            {
                this.host.println ("Unknown pad mode " + value);
                return;
            }
            final ViewManager viewManager = this.getSurface ().getViewManager ();
            viewManager.setActiveView (view);
            surface.getDisplay ().notify (viewManager.getActiveView ().getName ());
            surface.getPadGrid ().setView (view);
        });
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        surface.getPadGrid ().setView (Views.SESSION);
        surface.getViewManager ().setActiveView (Views.SESSION);
        surface.getModeManager ().setActiveMode (Modes.VOLUME);
        surface.setKnobMode (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_VOLUME);
        surface.setPadMode (LaunchkeyMiniMk3ControlSurface.PAD_MODE_SESSION);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateButtons ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();

        final int colorLow = this.colorManager.getColor (ColorManager.BUTTON_STATE_ON);
        final int colorHi = this.colorManager.getColor (ColorManager.BUTTON_STATE_HI);

        surface.updateTrigger (15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_PLAY, t.isPlaying () ? colorHi : colorLow);
        surface.updateTrigger (15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_RECORD, t.isLauncherOverdub () || t.isRecording () ? colorHi : colorLow);

        final ModeManager modeManager = this.getSurface ().getModeManager ();
        final Mode mode = modeManager.getActiveOrTempMode ();
        if (mode == null)
            return;
        boolean hasPrevItem = mode.hasPreviousItem ();
        boolean hasNextItem = mode.hasNextItem ();
        if (modeManager.isActiveMode (Modes.DEVICE_PARAMS))
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            hasPrevItem = cursorDevice.canSelectPreviousFX ();
            hasNextItem = cursorDevice.canSelectNextFX ();
        }
        surface.updateTrigger (15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_LEFT, hasPrevItem ? colorHi : colorLow);
        surface.updateTrigger (15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_RIGHT, hasNextItem ? colorHi : colorLow);

        final ViewManager viewManager = surface.getViewManager ();
        final View activeView = viewManager.getActiveView ();
        if (activeView instanceof SceneView)
            ((SceneView) activeView).updateSceneButtons ();
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Modes mode)
    {
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        final boolean isVolume = modeManager.isActiveMode (Modes.VOLUME);
        final boolean isPan = modeManager.isActiveMode (Modes.PAN);
        final boolean isSend1 = modeManager.isActiveMode (Modes.SEND1);
        final boolean isSend2 = modeManager.isActiveMode (Modes.SEND2);
        final boolean isDevice = modeManager.isActiveMode (Modes.DEVICE_PARAMS);
        final boolean isUserMode = modeManager.isActiveMode (Modes.USER);

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect);
        if (tbe != null)
            tbe.setIndication (isEffect);

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && isVolume);
            track.setPanIndication (!isEffect && isPan);
            final ISendBank sendBank = track.getSendBank ();
            sendBank.getItem (0).setIndication (!isEffect && isSend1);
            sendBank.getItem (1).setIndication (!isEffect && isSend2);

            if (tbe != null)
            {
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect && isVolume);
                fxTrack.setPanIndication (isEffect && isPan);
            }

            parameterBank.getItem (i).setIndication (isDevice);
        }

        if (this.host.hasUserParameters ())
        {
            final IParameterBank userParameterBank = this.model.getUserParameterBank ();
            for (int i = 0; i < userParameterBank.getPageSize (); i++)
                userParameterBank.getItem (i).setIndication (isUserMode);
        }
    }


    private void processProgramChangeAction (final int value)
    {
        final Modes modeID = this.getSurface ().getModeManager ().getActiveOrTempModeId ();
        if (modeID == null)
            return;
        switch (modeID)
        {
            case VOLUME:
            case PAN:
            case SEND1:
            case SEND2:
                final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
                if (value > 0)
                    currentTrackBank.selectNextPage ();
                else
                    currentTrackBank.selectPreviousPage ();

                this.host.scheduleTask ( () -> {
                    final ITrack selectedTrack = currentTrackBank.getSelectedItem ();
                    if (selectedTrack != null)
                        this.getSurface ().getDisplay ().notify (selectedTrack.getName ());
                }, 200);
                break;

            case DEVICE_PARAMS:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                if (value > 0)
                    parameterBank.selectNextItem ();
                else
                    parameterBank.selectPreviousItem ();

                this.host.scheduleTask ( () -> {
                    final String selectedItem = cursorDevice.getParameterPageBank ().getSelectedItem ();
                    if (selectedItem != null)
                        this.getSurface ().getDisplay ().notify (selectedItem);
                }, 200);
                break;

            default:
                // Not used
                break;
        }
    }
}
