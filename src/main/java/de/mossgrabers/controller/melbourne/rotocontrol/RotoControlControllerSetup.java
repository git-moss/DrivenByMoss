// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.melbourne.rotocontrol;

import de.mossgrabers.controller.melbourne.rotocontrol.command.trigger.transport.RotoControlPunchInCommand;
import de.mossgrabers.controller.melbourne.rotocontrol.command.trigger.transport.RotoControlPunchOutCommand;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.IMessageCallback;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlColorManager;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlControlSurface;
import de.mossgrabers.controller.melbourne.rotocontrol.controller.RotoControlMessage;
import de.mossgrabers.controller.melbourne.rotocontrol.mode.RotoControlDeviceParameterMode;
import de.mossgrabers.controller.melbourne.rotocontrol.mode.RotoControlDisplay;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.mode.ButtonRowModeCommand;
import de.mossgrabers.framework.command.trigger.transport.AutomationCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.RecordCommand;
import de.mossgrabers.framework.command.trigger.transport.StopCommand;
import de.mossgrabers.framework.command.trigger.transport.ToggleLoopCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.IMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.mode.track.TrackMuteMode;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.mode.track.TrackRecArmMode;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.mode.track.TrackSoloMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.view.DummyView;
import de.mossgrabers.framework.view.Views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


/**
 * Support for the Melbourne Instruments ROTO CONTROL device.
 *
 * @author Jürgen Moßgraber
 */
public class RotoControlControllerSetup extends AbstractControllerSetup<RotoControlControlSurface, RotoControlConfiguration> implements IMessageCallback
{
    private static final int  NUM_PARAM_PAGES    = 16;
    private static final int  MIDI_CC_CHANNEL    = 15;

    private final boolean []  noteBlocker        = new boolean [64];
    private final int []      knobValues         = new int [8];
    private final long []     knobValuesEditTime = new long [8];
    private final ModeManager buttonModeManager  = new ModeManager ();
    private final ModelSetup  modelSetup         = new ModelSetup ();

    private Modes             activeButtonMode   = Modes.MUTE;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public RotoControlControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        Arrays.fill (this.knobValuesEditTime, System.currentTimeMillis ());
        Arrays.fill (this.noteBlocker, false);

        this.colorManager = new RotoControlColorManager ();
        this.valueChanger = new TwosComplementValueChanger (16384, 1);
        this.configuration = new RotoControlConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        this.modelSetup.setHasFullFlatTrackList (true);

        this.modelSetup.setNumFxTracks (8);
        this.modelSetup.setNumSends (8);
        // The 'normal' pages are used for learning the parameters
        this.modelSetup.setNumParamPages (NUM_PARAM_PAGES);
        // These are used to be able to bind any parameter from any of the pages
        this.modelSetup.setNumListParams (8 * NUM_PARAM_PAGES);

        // Only for creating new clips
        this.modelSetup.setNumScenes (16);

        // Not used
        this.modelSetup.setNumDeviceLayers (0);
        this.modelSetup.setNumDrumPadLayers (0);
        this.modelSetup.enableMainDrumDevice (false);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, this.modelSetup);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput (null);
        this.surfaces.add (new RotoControlControlSurface (this.host, this.colorManager, this.configuration, output, input, this, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final RotoControlControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.register (Modes.TRACK, new TrackMode<> (surface, this.model, true));
        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true));
        modeManager.register (Modes.PAN, new TrackPanMode<> (surface, this.model, true));
        for (int i = 0; i < this.modelSetup.getNumSends (); i++)
            modeManager.register (Modes.get (Modes.SEND1, i), new TrackSendMode<> (i, surface, this.model, true));
        final RotoControlDeviceParameterMode deviceParamsMode = new RotoControlDeviceParameterMode (surface, this.model);
        modeManager.register (Modes.DEVICE_PARAMS, deviceParamsMode);

        this.buttonModeManager.register (Modes.DEVICE_PARAMS, deviceParamsMode);
        this.buttonModeManager.register (Modes.MUTE, new TrackMuteMode<> (surface, this.model)
        {
            /** {@inheritDoc} */
            @Override
            public int getButtonColor (final ButtonID buttonID)
            {
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
                final ITrack item = this.model.getCurrentTrackBank ().getItem (index);
                return !item.doesExist () || item.isMute () ? 127 : 0;
            }
        });
        this.buttonModeManager.register (Modes.SOLO, new TrackSoloMode<> (surface, this.model)
        {
            /** {@inheritDoc} */
            @Override
            public int getButtonColor (final ButtonID buttonID)
            {
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
                return this.model.getCurrentTrackBank ().getItem (index).isSolo () ? 127 : 0;
            }
        });
        this.buttonModeManager.register (Modes.REC_ARM, new TrackRecArmMode<> (surface, this.model)
        {
            /** {@inheritDoc} */
            @Override
            public int getButtonColor (final ButtonID buttonID)
            {
                final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
                return this.model.getCurrentTrackBank ().getItem (index).isRecArm () ? 127 : 0;
            }
        });

        modeManager.addChangeListener ( (previousID, activeID) -> {
            if (activeID == Modes.DEVICE_PARAMS)
            {
                this.activeButtonMode = this.buttonModeManager.getActiveID ();
                this.buttonModeManager.setActive (Modes.DEVICE_PARAMS);
            }
            else
                this.buttonModeManager.setActive (this.activeButtonMode);
        });
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final RotoControlControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.CONTROL, new DummyView<> ("Control", surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.model.getTrackBank ().addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));

        this.getSurface ().getModeManager ().addChangeListener ( (previousID, activeID) -> this.updateIndication (activeID));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final RotoControlControlSurface surface = this.getSurface ();

        final ITransport t = this.model.getTransport ();

        this.addButton (ButtonID.PLAY, "PLAY", new PlayCommand<> (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_PLAY, () -> t.isPlaying () ? 127 : 0);
        this.addButton (ButtonID.STOP, "STOP", new StopCommand<> (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_STOP, () -> !t.isPlaying () ? 127 : 0);
        this.addButton (ButtonID.RECORD, "REC", new RecordCommand<> (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_REC, () -> t.isRecording () ? 127 : 0);
        this.addButton (ButtonID.NEW, "NEW", new NewCommand<> (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_SESSION_REC);
        this.addButton (ButtonID.LOOP, "LOOP", new ToggleLoopCommand<> (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_LOOP, () -> t.isLoop () ? 127 : 0);
        this.addButton (ButtonID.PUNCH_IN, "PUNCH IN", new RotoControlPunchInCommand (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_PUNCH_IN, () -> t.isPunchInEnabled () ? 127 : 0);
        this.addButton (ButtonID.PUNCH_OUT, "PUNCH OUT", new RotoControlPunchOutCommand (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_PUNCH_OUT, () -> t.isPunchOutEnabled () ? 127 : 0);
        this.addButton (ButtonID.AUTOMATION, "AUTOMATION", new AutomationCommand<> (this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_AUTOMATION, () -> t.isWritingArrangerAutomation () ? 127 : 0);
        this.addButton (ButtonID.REWIND, "RWD", new WindCommand<> (this.model, surface, false), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_REWIND);
        this.addButton (ButtonID.FORWARD, "FFW", new WindCommand<> (this.model, surface, true), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_FORWARD);

        for (int i = 0; i < 8; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.ROW1_1, i);
            this.addButton (buttonID, "Button " + (i + 1), new ButtonRowModeCommand<> (this.buttonModeManager, 0, i, this.model, surface), MIDI_CC_CHANNEL, RotoControlControlSurface.BUTTON_FIRST + i, () -> this.getModeColor (this.buttonModeManager, buttonID));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final RotoControlControlSurface surface = this.getSurface ();

        for (int i = 0; i < 8; i++)
        {
            final int midiControl = RotoControlControlSurface.KNOB_FIRST + i;
            final KnobRowModeCommand<RotoControlControlSurface, RotoControlConfiguration> command = new KnobRowModeCommand<> (i, this.model, surface)
            {
                /** {@inheritDoc} */
                @Override
                public void execute (final int value)
                {
                    final IMode m = this.surface.getModeManager ().getActive ();
                    if (m != null)
                    {
                        RotoControlControllerSetup.this.knobValuesEditTime[this.index] = System.currentTimeMillis ();
                        m.onKnobValue (this.index, value);
                        RotoControlControllerSetup.this.knobValues[this.index] = m.getKnobValue (this.index);
                    }
                }
            };

            // TODO 5.3.3: binding sequences is currently broken
            // final IHwAbsoluteKnob knob = this.addHiResAbsoluteKnob (this.getSurface (),
            // ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + i, command, MIDI_CC_CHANNEL,
            // midiControl);
            // knob.setIndexInGroup (i);
            // knob.disableTakeover ();

            surface.setHiResContinuousCommand (midiControl, command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final RotoControlControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.CONTROL);
        surface.getModeManager ().setActive (Modes.VOLUME);
        this.buttonModeManager.setActive (Modes.MUTE);

        this.host.scheduleTask (surface::sendStartupCommand, 2000);
    }


    /** {@index} */
    @Override
    public void handle (final RotoControlMessage message)
    {
        final int messageType = message.getType ();
        switch (messageType)
        {
            case RotoControlMessage.GENERAL:
                this.handleGeneralCommands (message);
                break;

            case RotoControlMessage.PLUGIN:
                this.handlePluginCommands (message);
                break;

            case RotoControlMessage.MIX:
                this.handleMixCommands (message);
                break;

            default:
                this.host.error ("Unknown Roto Control message type: " + messageType);
                break;
        }
    }


    private void handleGeneralCommands (final RotoControlMessage message)
    {
        final RotoControlControlSurface surface = this.getSurface ();
        final int [] content = message.getContent ();
        final int subType = message.getSubType ();
        final ITrackBank bank = this.model.getCurrentTrackBank ();
        final int pageSize = bank.getPageSize ();
        switch (subType)
        {
            case RotoControlMessage.RCV_PING_DAW:
                surface.sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_DAW_PING_RESPONSE);
                break;

            case RotoControlMessage.RCV_SET_FIRST_TRACK:
                bank.scrollTo (content[0] / pageSize * pageSize);
                surface.scheduleTask ( () -> {
                    bank.getItem (content[0] % pageSize).select ();
                    this.switchTracks (surface);
                }, 100);
                break;

            case RotoControlMessage.RCV_SELECT_TRACK:
                if (content[0] != 0xFF)
                {
                    bank.scrollTo (content[0] / pageSize * pageSize);
                    bank.getItem (content[0] % pageSize).select ();
                }
                break;

            case RotoControlMessage.RCV_REQUEST_TRANSPORT_STATUS:
                this.updateTransportStatus ();
                break;

            default:
                this.host.error ("Unknown Roto Control General message sub-type: " + subType);
                break;
        }
    }


    private void switchTracks (final RotoControlControlSurface surface)
    {
        surface.scheduleTask ( () -> {
            Arrays.fill (this.knobValues, -1);
            surface.flushButtonLEDs ();
        }, 100);
    }


    private void handlePluginCommands (final RotoControlMessage message)
    {
        final RotoControlControlSurface surface = this.getSurface ();
        final int [] data = message.getContent ();
        final int subType = message.getSubType ();
        final ModeManager modeManager = surface.getModeManager ();
        switch (subType)
        {
            case RotoControlMessage.RCV_SET_PLUGIN_MODE:
                modeManager.setActive (Modes.DEVICE_PARAMS);
                break;

            case RotoControlMessage.RCV_SET_FIRST_PLUGIN:
                this.model.getCursorDevice ().getDeviceBank ().scrollTo (data[0]);
                break;

            case RotoControlMessage.RCV_ROTO_SELECT_PLUGIN:
                this.model.getCursorDevice ().getDeviceBank ().getItem (data[0]).select ();
                break;

            case RotoControlMessage.RCV_SET_PLUGIN_LEARN:
                final boolean activateLearn = data[0] > 0;
                if (modeManager.get (Modes.DEVICE_PARAMS) instanceof final RotoControlDeviceParameterMode rotoParamsMode)
                {
                    if (activateLearn && !modeManager.isActive (Modes.DEVICE_PARAMS))
                        modeManager.setActive (Modes.DEVICE_PARAMS);
                    rotoParamsMode.setParamLearn (activateLearn);
                }
                break;

            case RotoControlMessage.RCV_PARAM_LEARNED:
                final int paramIndex = data[0] * 128 + data[1];
                // Bytes 3-8 contain the hash which we do not need
                final boolean isSwitch = data[8] == 1;
                final int posInPage = data[9];
                if (modeManager.get (Modes.DEVICE_PARAMS) instanceof final RotoControlDeviceParameterMode rotoParamsMode)
                    rotoParamsMode.bind (paramIndex, posInPage, isSwitch);
                break;

            case RotoControlMessage.RCV_SET_PLUGIN_ENABLE:
                this.model.getCursorDevice ().getDeviceBank ().getItem (data[0]).toggleEnabledState ();
                break;

            case RotoControlMessage.RCV_SET_PLUGIN_LOCK:
                this.model.getCursorDevice ().setPinned (data[0] > 0);
                break;

            default:
                this.host.error ("Unknown Roto Control Plugin message sub-type: " + subType);
                break;
        }
    }


    private void handleMixCommands (final RotoControlMessage message)
    {
        final RotoControlControlSurface surface = this.getSurface ();
        final int [] data = message.getContent ();
        final int subType = message.getSubType ();
        final ModeManager modeManager = surface.getModeManager ();
        switch (subType)
        {
            case RotoControlMessage.RCV_SET_MIX_ALL_TRACKS_MODE:

                // <AM KM SM>
                // AM = All tracks mode: Audio (00), Master-Return (01)
                // KM = Knob mode: Level (00), Pan (01), Send (02)
                // SM = Switch mode: Mute (00), Solo (01), Arm Recording (02)

                // Set the mixer FX/master or audio/instrument mode
                if (this.model.getEffectTrackBank () != null)
                {
                    if (data[0] == 1)
                    {
                        if (!this.model.isEffectTrackBankActive ())
                            this.model.toggleCurrentTrackBank ();
                    }
                    else
                    {
                        if (this.model.isEffectTrackBankActive ())
                            this.model.toggleCurrentTrackBank ();
                    }
                }

                // Set the knob mode
                switch (data[1])
                {
                    case 0:
                        modeManager.setActive (Modes.VOLUME);
                        break;
                    case 1:
                        modeManager.setActive (Modes.PAN);
                        break;
                    case 2:
                        modeManager.setActive (Modes.get (Modes.SEND1, data[3]));
                        break;
                    default:
                        surface.errorln ("Unsupported knob mode: " + data[1]);
                        break;
                }
                surface.flushRotoDisplay ();

                // Set the button mode
                switch (data[2])
                {
                    case 0:
                        this.buttonModeManager.setActive (Modes.MUTE);
                        break;
                    case 1:
                        this.buttonModeManager.setActive (Modes.SOLO);
                        break;
                    case 2:
                        this.buttonModeManager.setActive (Modes.REC_ARM);
                        break;
                    default:
                        surface.errorln ("Unsupported button mode: " + data[2]);
                        break;
                }
                this.switchTracks (surface);
                break;

            case RotoControlMessage.RCV_SET_MIX_TRACK_MODE:
                modeManager.setActive (Modes.TRACK);
                // data[0] = selected_mode_page_index
                break;

            case RotoControlMessage.RCV_SET_ALL_TRACKS_MODE:
                // AM = All tracks mode: Audio (00), Master-Return (01)
                if (this.model.getEffectTrackBank () != null)
                {
                    if (data[0] == 1)
                    {
                        if (!this.model.isEffectTrackBankActive ())
                            this.model.toggleCurrentTrackBank ();
                    }
                    else
                    {
                        if (this.model.isEffectTrackBankActive ())
                            this.model.toggleCurrentTrackBank ();
                    }
                }
                final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
                if (currentTrackBank.getSelectedItem ().isEmpty ())
                    currentTrackBank.getItem (0).select ();
                this.switchTracks (surface);
                break;

            default:
                this.host.error ("Unknown Roto Control Plugin message sub-type: " + subType);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        super.flush ();

        final RotoControlControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        final IMode mode = modeManager.getActive ();
        if (mode == null)
            return;

        final IMidiOutput output = surface.getMidiOutput ();
        for (int i = 0; i < 8; i++)
        {
            // Workaround for missing touch messages
            final long now = System.currentTimeMillis ();
            if (now - this.knobValuesEditTime[i] < 1000)
                continue;

            final int value = Math.max (0, mode.getKnobValue (i));
            if (value != this.knobValues[i])
            {
                this.knobValues[i] = value;
                final int midiControl = RotoControlControlSurface.KNOB_FIRST + i;
                output.sendCCEx (MIDI_CC_CHANNEL, midiControl, value / 128);
                output.sendCCEx (MIDI_CC_CHANNEL, midiControl + 32, value % 128);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateTransportStatus ()
    {
        final ITransport transport = this.model.getTransport ();
        final int [] attributes = new int [8];
        attributes[0] = transport.isPlaying () ? 1 : 0;
        attributes[1] = !transport.isPlaying () ? 1 : 0;
        attributes[2] = transport.isRecording () ? 1 : 0;
        attributes[3] = transport.isLauncherOverdub () ? 1 : 0;
        attributes[4] = transport.isLoop () ? 1 : 0;
        attributes[5] = transport.isPunchInEnabled () ? 1 : 0;
        attributes[6] = transport.isPunchOutEnabled () ? 1 : 0;
        attributes[7] = transport.isWritingArrangerAutomation () ? 1 : 0;
        this.getSurface ().sendSysex (RotoControlMessage.GENERAL, RotoControlMessage.TR_TRANSPORT_STATUS, attributes);
    }


    /** {@inheritDoc} */
    @Override
    protected void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected)
            return;

        final Optional<ITrack> selectedItem = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (!selectedItem.isPresent ())
            return;

        final ITrack track = selectedItem.get ();
        final String name = RotoControlDisplay.create13ByteTextArray (track.getName ());
        final int colorIndex = ColorEx.getClosestColorIndex (track.getColor (), RotoControlColorManager.DEFAULT_PALETTE);

        try
        {
            // TI = Track index
            // TN = Track name: 0D-byte NULL terminated ASCII string, padded with 00s if needed
            // CI = Color scheme: 00 - 52

            final ByteArrayOutputStream out = new ByteArrayOutputStream ();
            out.write (track.getPosition ());
            out.write (name.getBytes ());
            out.write (colorIndex);
            final RotoControlControlSurface surface = this.getSurface ();
            surface.sendSysex (RotoControlMessage.MIX, RotoControlMessage.TR_DAW_SELECT_TRACK, out.toByteArray ());

            // Trigger sending device updates
            surface.flushRotoDisplay ();
        }
        catch (final IOException ex)
        {
            // Can never happen
        }
    }


    // TODO 5.3.3: Remove when MIDI sequence mapping is working
    private void updateIndication (final Modes activeModeID)
    {
        final boolean isTrack = activeModeID == Modes.TRACK;
        final boolean isVolume = activeModeID == Modes.VOLUME;
        final boolean isPan = activeModeID == Modes.PAN;
        final boolean isDevice = activeModeID == Modes.DEVICE_PARAMS;
        final int sendModeIndex = Modes.isSendMode (activeModeID) ? activeModeID.ordinal () - Modes.SEND1.ordinal () : -1;

        final ITrackBank trackBank = this.model.getCurrentTrackBank ();
        final Optional<ITrack> selectedTrack = trackBank.getSelectedItem ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack.isPresent () && selectedTrack.get ().getIndex () == i;

            final ITrack track = trackBank.getItem (i);
            track.setVolumeIndication (isVolume || hasTrackSel && isTrack);
            track.setPanIndication (isPan || hasTrackSel && isTrack);

            final ISendBank sendBank = track.getSendBank ();
            final int sendPageSize = sendBank.getPageSize ();
            for (int j = 0; j < sendPageSize; j++)
                sendBank.getItem (j).setIndication (sendModeIndex == j || hasTrackSel && isTrack);
        }

        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
            parameterBank.getItem (i).setIndication (isDevice);
    }
}
