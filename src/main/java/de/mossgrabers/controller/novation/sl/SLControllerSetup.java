// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.sl;

import de.mossgrabers.controller.novation.sl.command.continuous.TapTempoInitMkICommand;
import de.mossgrabers.controller.novation.sl.command.continuous.TapTempoMkICommand;
import de.mossgrabers.controller.novation.sl.command.continuous.TouchpadCommand;
import de.mossgrabers.controller.novation.sl.command.trigger.ButtonRowSelectCommand;
import de.mossgrabers.controller.novation.sl.command.trigger.ButtonRowViewCommand;
import de.mossgrabers.controller.novation.sl.command.trigger.P1ButtonCommand;
import de.mossgrabers.controller.novation.sl.command.trigger.P2ButtonCommand;
import de.mossgrabers.controller.novation.sl.command.trigger.TransportButtonCommand;
import de.mossgrabers.controller.novation.sl.controller.SLColorManager;
import de.mossgrabers.controller.novation.sl.controller.SLControlSurface;
import de.mossgrabers.controller.novation.sl.controller.SLDisplay;
import de.mossgrabers.controller.novation.sl.mode.FixedMode;
import de.mossgrabers.controller.novation.sl.mode.FrameMode;
import de.mossgrabers.controller.novation.sl.mode.FunctionMode;
import de.mossgrabers.controller.novation.sl.mode.PlayOptionsMode;
import de.mossgrabers.controller.novation.sl.mode.SLTrackMode;
import de.mossgrabers.controller.novation.sl.mode.SLVolumeMode;
import de.mossgrabers.controller.novation.sl.mode.SessionMode;
import de.mossgrabers.controller.novation.sl.mode.TrackTogglesMode;
import de.mossgrabers.controller.novation.sl.mode.ViewSelectMode;
import de.mossgrabers.controller.novation.sl.mode.device.DevicePresetsMode;
import de.mossgrabers.controller.novation.sl.mode.device.SLParameterMode;
import de.mossgrabers.controller.novation.sl.view.ControlView;
import de.mossgrabers.controller.novation.sl.view.PlayView;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.Views;


/**
 * Support for the Novation SLMkII Pro and SLMkII MkII controllers.
 *
 * @author Jürgen Moßgraber
 */
public class SLControllerSetup extends AbstractControllerSetup<SLControlSurface, SLConfiguration>
{
    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        0,  1,  2, 3,   4,  5,  6,  7,
        8,  9, 10, 11, 12, 13, 14, 15,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on

    private final boolean       isMkII;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     * @param isMkII True if SLMkII
     */
    public SLControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings, final boolean isMkII)
    {
        super (factory, host, globalSettings, documentSettings);
        this.isMkII = isMkII;
        this.colorManager = new SLColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new SLConfiguration (host, this.valueChanger, factory.getArpeggiatorModes (), isMkII);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        // Always refresh volume mode
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (!modeManager.isActive (Modes.VOLUME))
            modeManager.get (Modes.VOLUME).updateDisplay ();
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
        ms.setHasFullFlatTrackList (true);
        ms.setNumSends (6);
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput (this.isMkII ? "Novation SL MkII (Drumpads)" : "Novation SL MkI (Drumpads)", "90????", "80????");
        final IMidiInput keyboardInput = midiAccess.createInput (1, this.isMkII ? "Novation SL MkII (Keyboard)" : "Novation SL MkI (Keyboard)", "80????", "90????", "B0????", "D0????", "E0????");
        final IHost hostProxy = this.model.getHost ();
        final SLControlSurface surface = new SLControlSurface (hostProxy, this.colorManager, this.configuration, output, input, this.isMkII);
        this.surfaces.add (surface);

        surface.addPianoKeyboard (25, keyboardInput, true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final SLControlSurface surface = this.getSurface ();

        final ModeManager modeManager = surface.getModeManager ();

        modeManager.register (Modes.VOLUME, new SLVolumeMode (surface, this.model));
        modeManager.register (Modes.TRACK, new SLTrackMode (surface, this.model));
        modeManager.register (Modes.DEVICE_PARAMS, new SLParameterMode (surface, this.model));

        modeManager.register (Modes.FIXED, new FixedMode (surface, this.model));
        modeManager.register (Modes.FRAME, new FrameMode (surface, this.model));
        modeManager.register (Modes.FUNCTIONS, new FunctionMode (surface, this.model));
        modeManager.register (Modes.PLAY_OPTIONS, new PlayOptionsMode (surface, this.model));
        modeManager.register (Modes.SESSION, new SessionMode (surface, this.model));
        modeManager.register (Modes.TRACK_DETAILS, new TrackTogglesMode (surface, this.model));
        modeManager.register (Modes.VIEW_SELECT, new ViewSelectMode (surface, this.model));
        modeManager.register (Modes.BROWSER, new DevicePresetsMode (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final SLControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.PLAY, new PlayView (surface, this.model));
        viewManager.register (Views.CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.createScaleObservers (this.configuration);
        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Modes.BROWSER);

        final SLControlSurface surface = this.getSurface ();
        surface.getModeManager ().addChangeListener ( (oldMode, newMode) -> surface.getTextDisplay ().forceFlush ());
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final SLControlSurface surface = this.getSurface ();

        // Needs to be before the switch to Automap to catch the success response
        this.addButton (ButtonID.CONTROL, "DAW Online", (event, velocity) -> surface.setDAWConnected (velocity > 0), SLControlSurface.MKI_CC_OFF_ONLINE_MESSAGE, surface::isDAWConnected);
        surface.sendStartup ();

        for (int i = 0; i < 8; i++)
        {
            final int index = i + 1;
            final ButtonID buttonID1 = ButtonID.get (ButtonID.ROW1_1, i);
            final ButtonID buttonID2 = ButtonID.get (ButtonID.ROW2_1, i);
            final ButtonID buttonID3 = ButtonID.get (ButtonID.ROW3_1, i);
            final ButtonID buttonID4 = ButtonID.get (ButtonID.ROW4_1, i);
            final ButtonID buttonID5 = ButtonID.get (ButtonID.ROW_SELECT_1, i);

            this.addButton (buttonID1, "Row 1-" + index, new ButtonRowViewCommand<> (0, i, this.model, surface), SLControlSurface.MKII_BUTTON_ROW1_1 + i, () -> this.getViewButtonColor (buttonID1));
            this.addButton (buttonID2, "Row 2-" + index, new ButtonRowViewCommand<> (1, i, this.model, surface), SLControlSurface.MKII_BUTTON_ROW2_1 + i, () -> this.getViewButtonColor (buttonID2));
            this.addButton (buttonID3, "Row 3-" + index, new ButtonRowViewCommand<> (2, i, this.model, surface), SLControlSurface.MKII_BUTTON_ROW3_1 + i, () -> this.getViewButtonColor (buttonID3));
            this.addButton (buttonID4, "Row 4-" + index, new ButtonRowViewCommand<> (3, i, this.model, surface), SLControlSurface.MKII_BUTTON_ROW4_1 + i, () -> this.getViewButtonColor (buttonID4));
            this.addButton (buttonID5, "Select " + index, new ButtonRowSelectCommand<> (i, this.model, surface), SLControlSurface.MKII_BUTTON_ROWSEL1 + i, () -> this.getViewButtonColor (buttonID5));
        }

        this.addButton (ButtonID.REWIND, "<<", new ButtonRowViewCommand<> (4, 0, this.model, surface), SLControlSurface.MKII_BUTTON_REWIND);
        this.addButton (ButtonID.FORWARD, ">>", new ButtonRowViewCommand<> (4, 1, this.model, surface), SLControlSurface.MKII_BUTTON_FORWARD);
        this.addButton (ButtonID.STOP, "Stop", new ButtonRowViewCommand<> (4, 2, this.model, surface), SLControlSurface.MKII_BUTTON_STOP);
        this.addButton (ButtonID.PLAY, "Play", new ButtonRowViewCommand<> (4, 3, this.model, surface), SLControlSurface.MKII_BUTTON_PLAY);
        this.addButton (ButtonID.LOOP, "Loop", new ButtonRowViewCommand<> (4, 4, this.model, surface), SLControlSurface.MKII_BUTTON_LOOP);
        this.addButton (ButtonID.RECORD, "Record", new ButtonRowViewCommand<> (4, 6, this.model, surface), SLControlSurface.MKII_BUTTON_RECORD);
        this.addButton (ButtonID.ARROW_LEFT, "Left", new P1ButtonCommand (true, this.model, surface), SLControlSurface.MKII_BUTTON_P1_UP);
        this.addButton (ButtonID.ARROW_RIGHT, "Right", new P1ButtonCommand (false, this.model, surface), SLControlSurface.MKII_BUTTON_P1_DOWN);
        this.addButton (ButtonID.ARROW_UP, "Up", new P2ButtonCommand (true, this.model, surface), SLControlSurface.MKII_BUTTON_P2_UP);
        this.addButton (ButtonID.ARROW_DOWN, "Down", new P2ButtonCommand (false, this.model, surface), SLControlSurface.MKII_BUTTON_P2_DOWN);
        this.addButton (ButtonID.NOTE, "Play View", new TransportButtonCommand (this.model, surface), SLControlSurface.MKII_BUTTON_TRANSPORT);
    }


    private int getViewButtonColor (final ButtonID buttonID)
    {
        final IView activeView = this.getSurface ().getViewManager ().getActive ();
        return activeView == null ? 0 : activeView.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final SLControlSurface surface = this.getSurface ();
        for (int i = 0; i < 8; i++)
        {
            final int index = i;

            this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), null, BindType.CC, SLControlSurface.MKII_SLIDER1 + i);

            final IHwRelativeKnob relativeKnob = this.addRelativeKnob (ContinuousID.get (ContinuousID.DEVICE_KNOB1, i), "Device Knob " + (i + 1), null, SLControlSurface.MKII_KNOB_ROW1_1 + i, RelativeEncoding.SIGNED_BIT);
            relativeKnob.addOutput ( () -> {

                final boolean hasDevice = this.model.hasSelectedDevice ();
                final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
                return hasDevice ? parameterBank.getItem (index).getValue () : 0;

            }, value -> surface.getMidiOutput ().sendCC (0x70 + index, Math.min (value * 11 / 127, 11)));

            this.addAbsoluteKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), null, SLControlSurface.MKII_KNOB_ROW2_1 + i);
        }

        this.addFader (ContinuousID.TOUCHPAD_X, "Touchpad X", new TouchpadCommand (true, this.model, surface), BindType.CC, SLControlSurface.MKII_TOUCHPAD_X);
        this.addFader (ContinuousID.TOUCHPAD_Y, "Touchpad Y", new TouchpadCommand (false, this.model, surface), BindType.CC, SLControlSurface.MKII_TOUCHPAD_Y);

        // These are no faders but cannot be mapped to any meaningful control anyway
        this.addFader (ContinuousID.HELLO, "Tap Init", new TapTempoInitMkICommand (this.model, surface), BindType.CC, SLControlSurface.MKI_BUTTON_TAP_TEMPO);
        this.addFader (ContinuousID.TEMPO, "Tap Tempo", new TapTempoMkICommand (this.model, surface), BindType.CC, SLControlSurface.MKI_BUTTON_TAP_TEMPO_VALUE);

        // Volume, Track and Parameter modes are always bound since they have dedicated controls
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.get (Modes.VOLUME).onActivate ();
        modeManager.get (Modes.TRACK).onActivate ();
        modeManager.get (Modes.DEVICE_PARAMS).onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final SLControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (51.0, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD2).setBounds (91.25, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD3).setBounds (131.75, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD4).setBounds (172.0, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD5).setBounds (212.25, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD6).setBounds (252.5, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD7).setBounds (293.0, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.PAD8).setBounds (333.25, 179.75, 34.75, 33.25);
        surface.getButton (ButtonID.ROW1_1).setBounds (56.25, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_1).setBounds (54.75, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_1).setBounds (434.25, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_1).setBounds (434.75, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_1).setBounds (13.5, 57.75, 17.5, 15.0);
        surface.getButton (ButtonID.ROW1_2).setBounds (96.0, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_2).setBounds (95.0, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_2).setBounds (474.25, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_2).setBounds (475.0, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_2).setBounds (13.5, 90.5, 17.5, 15.0);
        surface.getButton (ButtonID.ROW1_3).setBounds (135.5, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_3).setBounds (135.0, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_3).setBounds (514.5, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_3).setBounds (515.25, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_3).setBounds (13.5, 119.75, 17.5, 15.0);
        surface.getButton (ButtonID.ROW1_4).setBounds (175.25, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_4).setBounds (175.25, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_4).setBounds (554.5, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_4).setBounds (555.5, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_4).setBounds (13.5, 152.0, 17.5, 15.0);
        surface.getButton (ButtonID.ROW1_5).setBounds (214.75, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_5).setBounds (215.5, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_5).setBounds (594.75, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_5).setBounds (595.75, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_5).setBounds (13.5, 189.25, 17.5, 15.0);
        surface.getButton (ButtonID.ROW1_6).setBounds (254.5, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_6).setBounds (255.75, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_6).setBounds (634.75, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_6).setBounds (636.0, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_6).setBounds (768.5, 90.0, 19.0, 16.0);
        surface.getButton (ButtonID.ROW1_7).setBounds (294.0, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_7).setBounds (295.75, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_7).setBounds (675.0, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_7).setBounds (676.25, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_7).setBounds (768.5, 139.5, 19.0, 16.0);
        surface.getButton (ButtonID.ROW1_8).setBounds (333.75, 57.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW2_8).setBounds (336.0, 121.25, 31.5, 14.25);
        surface.getButton (ButtonID.ROW3_8).setBounds (715.0, 141.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW4_8).setBounds (716.5, 163.0, 29.75, 15.0);
        surface.getButton (ButtonID.ROW_SELECT_8).setBounds (768.5, 162.0, 19.0, 16.0);
        surface.getButton (ButtonID.REWIND).setBounds (532.5, 193.5, 25.0, 22.75);
        surface.getButton (ButtonID.FORWARD).setBounds (564.0, 193.5, 25.0, 22.75);
        surface.getButton (ButtonID.STOP).setBounds (595.25, 193.5, 25.0, 22.75);
        surface.getButton (ButtonID.PLAY).setBounds (626.5, 193.5, 25.0, 22.75);
        surface.getButton (ButtonID.LOOP).setBounds (657.75, 193.5, 25.0, 22.75);
        surface.getButton (ButtonID.RECORD).setBounds (689.25, 193.5, 25.0, 22.75);
        surface.getButton (ButtonID.ARROW_LEFT).setBounds (12.5, 16.25, 25.0, 15.75);
        surface.getButton (ButtonID.ARROW_RIGHT).setBounds (12.5, 33.5, 25.0, 15.75);
        surface.getButton (ButtonID.ARROW_UP).setBounds (762.5, 11.0, 25.0, 16.5);
        surface.getButton (ButtonID.ARROW_DOWN).setBounds (762.5, 30.0, 25.0, 16.5);
        surface.getButton (ButtonID.NOTE).setBounds (385.75, 161.75, 24.25, 17.0);

        surface.getButton (ButtonID.CONTROL).setBounds (767.75, 328.75, 22.75, 18.25);

        surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (55.0, 83.5, 23.5, 24.25);
        surface.getContinuous (ContinuousID.KNOB1).setBounds (53.75, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (95.75, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (94.25, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (136.25, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (134.75, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (177.0, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (175.25, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (217.5, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (215.75, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (258.25, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (256.25, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (298.75, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (296.75, 146.0, 25.0, 25.0);
        surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (339.5, 83.5, 23.5, 23.5);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (337.5, 146.0, 25.0, 25.0);

        surface.getContinuous (ContinuousID.FADER1).setBounds (435.5, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER2).setBounds (476.25, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER3).setBounds (517.0, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER4).setBounds (557.75, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER5).setBounds (598.5, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER6).setBounds (639.25, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER7).setBounds (680.0, 55.75, 23.25, 75.75);
        surface.getContinuous (ContinuousID.FADER8).setBounds (720.75, 55.75, 23.25, 75.75);

        surface.getContinuous (ContinuousID.TOUCHPAD_X).setBounds (25.0, 375.25, 49.0, 90.25);
        surface.getContinuous (ContinuousID.TOUCHPAD_Y).setBounds (90.75, 375.25, 49.0, 90.25);

        surface.getContinuous (ContinuousID.HELLO).setBounds (767.75, 268.25, 10.0, 50.0);
        surface.getContinuous (ContinuousID.TEMPO).setBounds (780.5, 268.25, 10.0, 50.0);

        final SLDisplay textDisplay = (SLDisplay) surface.getTextDisplay ();
        textDisplay.getHwTextDisplay1 ().setBounds (55.5, 17.5, 316.25, 34.75);
        textDisplay.getHwTextDisplay2 ().setBounds (431.75, 17.5, 317.75, 34.75);

        surface.getContinuous (ContinuousID.PITCHBEND_WHEEL).setBounds (25.0, 262.75, 49.0, 90.25);
        surface.getContinuous (ContinuousID.MODULATION_WHEEL).setBounds (90.75, 262.75, 49.0, 90.25);

        surface.getPianoKeyboard ().setBounds (163.75, 262.0, 593.5, 224.25);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final SLControlSurface surface = this.getSurface ();

        // Initialize 2nd display
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.get (Modes.VOLUME).updateDisplay ();
        surface.getViewManager ().setActive (Views.CONTROL);
        modeManager.setActive (Modes.TRACK);
    }
}
