// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.mini;

import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ColorManager;
import de.mossgrabers.controller.novation.launchkey.mini.controller.LaunchkeyMiniMk3ControlSurface;
import de.mossgrabers.controller.novation.launchkey.mini.view.DrumConfigView;
import de.mossgrabers.controller.novation.launchkey.mini.view.DrumView;
import de.mossgrabers.controller.novation.launchkey.mini.view.PadModeSelectView;
import de.mossgrabers.controller.novation.launchkey.mini.view.SessionView;
import de.mossgrabers.controller.novation.launchkey.mini.view.UserPadView;
import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.command.core.NopCommand;
import de.mossgrabers.framework.command.trigger.Direction;
import de.mossgrabers.framework.command.trigger.mode.ModeCursorCommand;
import de.mossgrabers.framework.command.trigger.mode.ModeSelectCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.view.FeatureGroupButtonColorSupplier;
import de.mossgrabers.framework.command.trigger.view.ViewButtonCommand;
import de.mossgrabers.framework.command.trigger.view.ViewMultiSelectCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.OutputID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.hardware.IHwLight;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.device.UserMode;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.Views;

import java.util.function.BooleanSupplier;


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

    private MVHelper<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> mvHelper;
    private IMidiInput                                                              inputKeys;


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

        this.colorManager = new LaunchkeyMiniMk3ColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new LaunchkeyMiniMk3Configuration (host, this.valueChanger, factory.getArpeggiatorModes ());
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
        ms.setHasFullFlatTrackList (this.configuration.areMasterTracksIncluded ());
        ms.setNumScenes (2);
        ms.setNumSends (8);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        this.model.getTrackBank ().setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Pads", "80????", "90????", "81????", "91????", "82????", "92????", "83????", "93????", "84????", "94????", "85????", "95????", "86????", "96????", "87????", "97????", "88????", "98????", "89????", "99????", "8A????", "9A????", "8B????", "9B????", "8C????", "9C????", "8D????", "9D????", "8E????", "9E????");
        this.inputKeys = midiAccess.createInput (1, "Keyboard", "8?????" /* Note off */,
                "9?????" /* Note on */, "B?01??" /* Modulation */, "B?40??" /* Sustainpedal */,
                "E?????" /* Pitchbend */);

        final LaunchkeyMiniMk3ControlSurface surface = new LaunchkeyMiniMk3ControlSurface (this.host, this.colorManager, this.configuration, output, input, this::processProgramChangeAction);
        this.surfaces.add (surface);
        surface.addPianoKeyboard (25, this.inputKeys, true);

        this.mvHelper = new MVHelper<> (this.model, surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.createScaleObservers (this.configuration);
        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final BooleanSupplier offSupplier = () -> false;

        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true, AbstractParameterMode.DEFAULT_KNOB_IDS, offSupplier));
        modeManager.register (Modes.PAN, new TrackPanMode<> (surface, this.model, true, AbstractParameterMode.DEFAULT_KNOB_IDS, offSupplier));
        modeManager.register (Modes.SEND1, new TrackSendMode<> (0, surface, this.model, true, AbstractParameterMode.DEFAULT_KNOB_IDS, offSupplier));
        modeManager.register (Modes.SEND2, new TrackSendMode<> (1, surface, this.model, true, AbstractParameterMode.DEFAULT_KNOB_IDS, offSupplier));
        modeManager.register (Modes.DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true, AbstractParameterMode.DEFAULT_KNOB_IDS, offSupplier));
        modeManager.register (Modes.USER, new UserMode<> (surface, this.model, true, ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 8), offSupplier));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        viewManager.register (Views.SESSION, new SessionView (surface, this.model));
        viewManager.register (Views.CONTROL, new PadModeSelectView (surface, this.model));
        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.PLAY, new UserPadView (surface, this.model));
        viewManager.register (Views.SHIFT, new DrumConfigView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ITransport t = this.model.getTransport ();
        final ViewManager viewManager = surface.getViewManager ();

        this.addButton (ButtonID.SHIFT, "Shift", NopCommand.INSTANCE, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SHIFT);

        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_PLAY, t::isPlaying);
        final ConfiguredRecordCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> recordCommand = new ConfiguredRecordCommand<> (this.model, surface);
        this.addButton (ButtonID.RECORD, "Record", recordCommand, 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_RECORD, (BooleanSupplier) recordCommand::isLit);

        final ModeCursorCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> leftCommand = new ModeCursorCommand<> (Direction.LEFT, this.model, surface, true);
        this.addButton (ButtonID.MOVE_TRACK_LEFT, "Previous", leftCommand, 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_LEFT, leftCommand::canScroll);
        final ModeCursorCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> rightCommand = new ModeCursorCommand<> (Direction.RIGHT, this.model, surface, true);
        this.addButton (ButtonID.MOVE_TRACK_RIGHT, "Next", rightCommand, 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_RIGHT, rightCommand::canScroll);

        // Scene buttons
        this.addButton (ButtonID.SCENE1, "Scene 1", new ViewButtonCommand<> (ButtonID.SCENE1, surface), LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE1, new FeatureGroupButtonColorSupplier (viewManager, ButtonID.SCENE1));
        this.addButton (ButtonID.SCENE2, "Scene 2", new ViewButtonCommand<> (ButtonID.SCENE2, surface), LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_SCENE2, new FeatureGroupButtonColorSupplier (viewManager, ButtonID.SCENE2));

        // View selection with Pads in Shift mode
        this.createViewButton (ButtonID.ROW2_1, OutputID.LED_RING1, "Session", Views.SESSION, LaunchkeyMiniMk3ControlSurface.PAD_MODE_SESSION);
        this.createViewButton (ButtonID.ROW2_2, OutputID.LED_RING2, "Drum", Views.DRUM, LaunchkeyMiniMk3ControlSurface.PAD_MODE_DRUM);
        this.createViewButton (ButtonID.ROW2_3, OutputID.LED_RING3, "Custom", Views.PLAY, LaunchkeyMiniMk3ControlSurface.PAD_MODE_CUSTOM);

        // Knob mode selection with Pads in Shift mode
        this.createModeButton (ButtonID.ROW1_1, OutputID.LED1, "Device", Modes.DEVICE_PARAMS, LaunchkeyMiniMk3ControlSurface.KNOB_MODE_PARAMS);
        this.createModeButton (ButtonID.ROW1_2, OutputID.LED2, "Volume", Modes.VOLUME, LaunchkeyMiniMk3ControlSurface.KNOB_MODE_VOLUME);
        this.createModeButton (ButtonID.ROW1_3, OutputID.LED3, "Pan", Modes.PAN, LaunchkeyMiniMk3ControlSurface.KNOB_MODE_PAN);
        this.createModeButton (ButtonID.ROW1_4, OutputID.LED4, "Send 1", Modes.SEND1, LaunchkeyMiniMk3ControlSurface.KNOB_MODE_SEND1);
        this.createModeButton (ButtonID.ROW1_5, OutputID.LED5, "Send 2", Modes.SEND2, LaunchkeyMiniMk3ControlSurface.KNOB_MODE_SEND2);
        this.createModeButton (ButtonID.ROW1_6, OutputID.LED6, "Custom", Modes.USER, LaunchkeyMiniMk3ControlSurface.KNOB_MODE_CUSTOM);

        // Online state
        this.addButton (ButtonID.CONTROL, "DAW Online", (event, velocity) -> surface.setDAWConnected (velocity > 0), 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_DAW_ONLINE, surface::isDAWConnected);
    }


    private void createViewButton (final ButtonID buttonID, final OutputID outputID, final String label, final Views view, final int viewIndex)
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ViewMultiSelectCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> viewSelectCommand = new ViewMultiSelectCommand<> (this.model, surface, view);
        this.addButton (surface, buttonID, label, (event, velocity) -> {
            viewSelectCommand.executeNormal (event);
            surface.getPadGrid ().setView (view);
        }, 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_VIEW_SELECT, viewIndex, false, null);
        final IHwLight light = surface.createLight (outputID, () -> surface.getViewManager ().isActive (view) ? ColorEx.ORANGE : ColorEx.DARK_ORANGE, color -> {
            // Intentionally empty
        });
        surface.getButton (buttonID).addLight (light);
    }


    private void createModeButton (final ButtonID buttonID, final OutputID outputID, final String label, final Modes mode, final int modeIndex)
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        final ModeSelectCommand<LaunchkeyMiniMk3ControlSurface, LaunchkeyMiniMk3Configuration> modeSelectCommand = new ModeSelectCommand<> (this.model, surface, mode);
        this.addButton (surface, buttonID, label, (event, velocity) -> modeSelectCommand.executeNormal (event), 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_MODE_SELECT, modeIndex, false, null);
        final IHwLight light = surface.createLight (outputID, () -> surface.getModeManager ().isActive (mode) ? ColorEx.GREEN : ColorEx.DARK_GREEN, color -> {
            // Intentionally empty
        });
        surface.getButton (buttonID).addLight (light);
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        if (buttonID == ButtonID.CONTROL)
            return BindType.NOTE;
        return super.getTriggerBindType (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
        {
            this.addAbsoluteKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), null, BindType.CC, 15, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_KNOB_1 + i).setIndexInGroup (i);

            // Knobs in user mode send on the keys input on MIDI channel 1 (instead of 16), command
            // is not needed since it is mapped to IParameter when user mode is activated
            final IHwAbsoluteKnob userKnob = surface.createAbsoluteKnob (ContinuousID.get (ContinuousID.DEVICE_KNOB1, i), "User Knob " + (i + 1));
            userKnob.bind (this.inputKeys, BindType.CC, 0, LaunchkeyMiniMk3ControlSurface.LAUNCHKEY_KNOB_1 + i);
            userKnob.setIndexInGroup (i);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (193.5, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD2).setBounds (247.0, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD3).setBounds (301.0, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD4).setBounds (354.75, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD5).setBounds (408.0, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD6).setBounds (462.5, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD7).setBounds (516.25, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD8).setBounds (570.0, 127.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD9).setBounds (193.5, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD10).setBounds (247.0, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD11).setBounds (301.0, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD12).setBounds (354.75, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD13).setBounds (408.0, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD14).setBounds (462.5, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD15).setBounds (516.25, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD16).setBounds (570.0, 75.5, 47.0, 46.5);

        surface.getButton (ButtonID.PAD17).setBounds (193.5, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD18).setBounds (247.0, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD19).setBounds (301.0, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD20).setBounds (354.75, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD21).setBounds (408.0, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD22).setBounds (462.5, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD23).setBounds (516.25, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD24).setBounds (570.0, 257.5, 47.0, 46.5);
        surface.getButton (ButtonID.PAD25).setBounds (193.5, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD26).setBounds (247.0, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD27).setBounds (301.0, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD28).setBounds (354.75, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD29).setBounds (408.0, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD30).setBounds (462.5, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD31).setBounds (516.25, 203.75, 47.0, 46.5);
        surface.getButton (ButtonID.PAD32).setBounds (570.0, 203.75, 47.0, 46.5);

        surface.getButton (ButtonID.ROW1_1).setBounds (354.75, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_2).setBounds (408.0, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_3).setBounds (462.5, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_4).setBounds (516.25, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_5).setBounds (570.0, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW1_6).setBounds (624.0, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_1).setBounds (193.5, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_2).setBounds (247.0, 333.25, 47.0, 46.5);
        surface.getButton (ButtonID.ROW2_3).setBounds (301.0, 333.25, 47.0, 46.5);

        surface.getButton (ButtonID.SHIFT).setBounds (138.0, 31.0, 38.5, 19.25);
        surface.getButton (ButtonID.PLAY).setBounds (687.0, 155.0, 38.5, 19.25);
        surface.getButton (ButtonID.RECORD).setBounds (732.25, 155.0, 38.5, 19.25);
        surface.getButton (ButtonID.MOVE_TRACK_LEFT).setBounds (687.0, 102.75, 38.5, 19.25);
        surface.getButton (ButtonID.MOVE_TRACK_RIGHT).setBounds (732.25, 102.75, 38.5, 19.25);
        surface.getButton (ButtonID.SCENE1).setBounds (623.75, 75.5, 47.0, 46.5);
        surface.getButton (ButtonID.SCENE2).setBounds (623.75, 127.75, 47.0, 46.5);

        surface.getButton (ButtonID.CONTROL).setBounds (15.5, 26.5, 49.75, 29.0);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (203.0, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (256.75, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (310.75, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (364.5, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (418.5, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (472.25, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (526.25, 5, 25.5, 25.0);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (580.0, 5, 25.5, 25.0);

        surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (203.0, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (256.75, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (310.75, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (364.5, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (418.5, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (472.25, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (526.25, 30, 25.5, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (580.0, 30, 25.5, 25.0);

        surface.getContinuous (ContinuousID.MODULATION_WHEEL).setBounds (91.25, 77.5, 31.75, 171.25);
        surface.getContinuous (ContinuousID.PITCHBEND_WHEEL).setBounds (48.0, 77.5, 31.75, 171.25);

        surface.getPianoKeyboard ().setBounds (40.0, 422.0, 726.75, 175.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final LaunchkeyMiniMk3ControlSurface surface = this.getSurface ();

        surface.setLaunchpadToDAW (true);

        surface.getPadGrid ().setView (Views.SESSION);
        surface.getViewManager ().setActive (Views.SESSION);
        surface.getModeManager ().setActive (Modes.VOLUME);
        surface.setKnobMode (LaunchkeyMiniMk3ControlSurface.KNOB_MODE_VOLUME);
        surface.setPadMode (LaunchkeyMiniMk3ControlSurface.PAD_MODE_SESSION);
    }


    private void processProgramChangeAction (final int value)
    {
        final Modes modeID = this.getSurface ().getModeManager ().getActiveID ();
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
                this.mvHelper.notifySelectedTrack ();
                break;

            case DEVICE_PARAMS:
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                final IParameterBank parameterBank = cursorDevice.getParameterBank ();
                if (value > 0)
                    parameterBank.selectNextItem ();
                else
                    parameterBank.selectPreviousItem ();
                this.mvHelper.notifySelectedParameterPage ();
                break;

            default:
                // Not used
                break;
        }
    }
}
