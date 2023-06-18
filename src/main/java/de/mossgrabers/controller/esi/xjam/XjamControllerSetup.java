// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.esi.xjam;

import de.mossgrabers.controller.esi.xjam.controller.XjamControlSurface;
import de.mossgrabers.framework.command.trigger.FootswitchCommand;
import de.mossgrabers.framework.command.trigger.device.DeviceOnOffCommand;
import de.mossgrabers.framework.command.trigger.device.SelectNextDeviceCommand;
import de.mossgrabers.framework.command.trigger.device.SelectParamPageCommand;
import de.mossgrabers.framework.command.trigger.device.SelectPreviousDeviceCommand;
import de.mossgrabers.framework.command.trigger.device.ToggleDeviceWindowCommand;
import de.mossgrabers.framework.command.trigger.track.EnableCommand;
import de.mossgrabers.framework.command.trigger.track.MuteCommand;
import de.mossgrabers.framework.command.trigger.track.RecArmCommand;
import de.mossgrabers.framework.command.trigger.track.SelectCommand;
import de.mossgrabers.framework.command.trigger.track.SoloCommand;
import de.mossgrabers.framework.command.trigger.transport.ConfiguredRecordCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.command.trigger.transport.WindCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Setup to support the ESI Xjam controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControllerSetup extends AbstractControllerSetup<XjamControlSurface, XjamConfiguration>
{
    private static final int    MIDI_CHANNEL  = 15;
    private static final int [] KNOB_MAPPING  =
    {
        0,
        1,
        3,
        4
    };

    private IHwAbsoluteKnob []  knobBank2;
    private boolean             bindOneToFour = true;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public XjamControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.valueChanger = new TwosComplementValueChanger (128, 1);

        // No color control at all
        this.colorManager = new ColorManager ();
        this.colorManager.registerColorIndex (ColorManager.BUTTON_STATE_OFF, 0);
        this.colorManager.registerColorIndex (ColorManager.BUTTON_STATE_ON, 0);
        this.colorManager.registerColorIndex (ColorManager.BUTTON_STATE_HI, 0);
        this.colorManager.registerColor (0, ColorEx.BLACK);

        this.configuration = new XjamConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        // Not used
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFullFlatTrackList (true);
        ms.setNumTracks (8);
        ms.setNumSends (3);
        ms.setNumParams (8);
        ms.setNumParamPages (7);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("Xjam Pads", "8?????" /* Note off */,
                "9?????" /* Note on */, "A?????" /* Polyphonic After-touch */,
                "D?????" /* Channel After-touch */);

        final XjamControlSurface surface = new XjamControlSurface (this.host, this.colorManager, this.configuration, input);
        this.surfaces.add (surface);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final XjamControlSurface surface = this.getSurface ();

        // PAD BANK 2/3 Transport Row: CC 20-23
        this.addButton (ButtonID.PLAY, "Play", new PlayCommand<> (this.model, surface), MIDI_CHANNEL, 20);
        this.addButton (ButtonID.REWIND, "Rwd", new WindCommand<> (this.model, surface, false), MIDI_CHANNEL, 21);
        this.addButton (ButtonID.FORWARD, "Fwd", new WindCommand<> (this.model, surface, true), MIDI_CHANNEL, 22);
        this.addButton (ButtonID.RECORD, "Rec", new ConfiguredRecordCommand<> (this.model, surface), MIDI_CHANNEL, 23);

        // PAD BANK 2: CC 24-35
        this.addButton (ButtonID.SOLO, "Solo", new SoloCommand<> (this.model, surface), MIDI_CHANNEL, 24);
        this.addButton (ButtonID.MUTE, "Mute", new MuteCommand<> (this.model, surface), MIDI_CHANNEL, 25);
        this.addButton (ButtonID.REC_ARM, "RecArm", new RecArmCommand<> (this.model, surface), MIDI_CHANNEL, 26);
        this.addButton (ButtonID.TRACK, "On/Off", new EnableCommand<> (this.model, surface), MIDI_CHANNEL, 27);
        for (int i = 0; i < 8; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.TRACK_SELECT_1, i);
            this.addButton (buttonID, "Track " + (i + 1), new SelectCommand<> (i, this.model, surface), MIDI_CHANNEL, 28 + i);
        }

        // PAD BANK 3: CC 44-55
        this.addButton (ButtonID.DEVICE_LEFT, "Device <<", new SelectPreviousDeviceCommand<> (this.model, surface), MIDI_CHANNEL, 44);
        this.addButton (ButtonID.DEVICE_RIGHT, "Device >>", new SelectNextDeviceCommand<> (this.model, surface), MIDI_CHANNEL, 45);
        this.addButton (ButtonID.DEVICE_ON_OFF, "Device On/Off", new DeviceOnOffCommand<> (this.model, surface), MIDI_CHANNEL, 46);
        this.addButton (ButtonID.TOGGLE_DEVICE_WINDOW, "Device Window", new ToggleDeviceWindowCommand<> (this.model, surface), MIDI_CHANNEL, 47);
        for (int i = 0; i < 7; i++)
        {
            final ButtonID buttonID = ButtonID.get (ButtonID.PARAM_PAGE1, i);
            this.addButton (buttonID, "Param Page " + (i + 1), new SelectParamPageCommand<> (this.model, surface, i), MIDI_CHANNEL, 48 + i);
        }
        this.addButton (ButtonID.TOGGLE_DEVICE, "Pms 1-4/5-8", (event, velocity) -> {

            if (event == ButtonEvent.DOWN)
            {
                this.bindOneToFour = !this.bindOneToFour;
                this.host.showNotification (this.bindOneToFour ? "Params 1-4" : "Params 5-8");
                this.bindParameters ();
            }

        }, MIDI_CHANNEL, 55);

        // Footswitch
        this.addButton (ButtonID.FOOTSWITCH2, "Foot Controller", new FootswitchCommand<> (this.model, surface, 0), 64);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        // There are no modes, therefore everything is hard wired...

        final IParameterBank projectParameterBank = this.model.getProject ().getParameterBank ();

        final IHwAbsoluteKnob [] knobBank1 = new IHwAbsoluteKnob [6];
        this.knobBank2 = new IHwAbsoluteKnob [6];

        for (int i = 0; i < 6; i++)
        {
            final ContinuousID continuousID = ContinuousID.get (ContinuousID.KNOB1, i);
            knobBank1[i] = this.addAbsoluteKnob (continuousID, " Bank 1 - Knob" + (i + 1), null, BindType.CC, MIDI_CHANNEL, XjamControlSurface.BANK1_ENCODER_1 + i);
            knobBank1[i].setIndexInGroup (i);

            final ContinuousID continuousID2 = ContinuousID.get (ContinuousID.DEVICE_KNOB1, i);
            this.knobBank2[i] = this.addAbsoluteKnob (continuousID2, "Bank 2 - Knob " + (i + 1), null, BindType.CC, MIDI_CHANNEL, XjamControlSurface.BANK2_ENCODER_1 + i);
            this.knobBank2[i].setIndexInGroup (i);

            final ContinuousID continuousID3 = ContinuousID.get (ContinuousID.PARAM_KNOB1, i);
            final IHwAbsoluteKnob bank3Knob = this.addAbsoluteKnob (continuousID3, "Bank 3 - Knob " + (i + 1), null, BindType.CC, MIDI_CHANNEL, XjamControlSurface.BANK3_ENCODER_1 + i);
            bank3Knob.setIndexInGroup (i);
            bank3Knob.bind (projectParameterBank.getItem (i));
        }

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();
        final ISendBank sendBank = cursorTrack.getSendBank ();
        knobBank1[0].bind (cursorTrack.getVolumeParameter ());
        knobBank1[1].bind (cursorTrack.getPanParameter ());
        knobBank1[2].bind (this.model.getMasterTrack ().getVolumeParameter ());
        knobBank1[3].bind (sendBank.getItem (0));
        knobBank1[4].bind (sendBank.getItem (1));
        knobBank1[5].bind (sendBank.getItem (2));

        this.bindParameters ();
        this.knobBank2[2].bind (this.model.getMasterTrack ().getVolumeParameter ());
        this.knobBank2[5].bind (cursorTrack.getVolumeParameter ());
    }


    private void bindParameters ()
    {
        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        final int paramOffset = this.bindOneToFour ? 0 : 4;
        for (int i = 0; i < 4; i++)
            this.knobBank2[KNOB_MAPPING[i]].bind (parameterBank.getItem (paramOffset + i));
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final XjamControlSurface surface = this.getSurface ();

        final double padding = 10;
        final double width = 20;

        for (int i = 0; i < 6; i++)
        {
            final double x = padding + (padding + width) * i;

            final ContinuousID continuousID = ContinuousID.get (ContinuousID.KNOB1, i);
            surface.getContinuous (continuousID).setBounds (x, 10, width, width);

            final ContinuousID continuousID2 = ContinuousID.get (ContinuousID.DEVICE_KNOB1, i);
            surface.getContinuous (continuousID2).setBounds (x, 40, width, width);

            final ContinuousID continuousID3 = ContinuousID.get (ContinuousID.PARAM_KNOB1, i);
            surface.getContinuous (continuousID3).setBounds (x, 70, width, width);
        }

        surface.getButton (ButtonID.PLAY).setBounds (43.0, 209.75, 42.25, 36.0);
        surface.getButton (ButtonID.REWIND).setBounds (95.75, 209.75, 42.25, 36.0);
        surface.getButton (ButtonID.FORWARD).setBounds (148.25, 209.75, 42.25, 36.0);
        surface.getButton (ButtonID.RECORD).setBounds (201.0, 209.75, 42.25, 36.0);
        surface.getButton (ButtonID.SOLO).setBounds (404.75, 206.5, 51.25, 36.0);
        surface.getButton (ButtonID.MUTE).setBounds (465.25, 206.5, 51.25, 36.0);
        surface.getButton (ButtonID.REC_ARM).setBounds (525.75, 206.5, 51.25, 36.0);
        surface.getButton (ButtonID.TRACK).setBounds (586.25, 206.5, 51.25, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_1).setBounds (225.75, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_2).setBounds (282.5, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_3).setBounds (339.5, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_4).setBounds (396.25, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_5).setBounds (453.0, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_6).setBounds (509.75, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_7).setBounds (566.75, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.TRACK_SELECT_8).setBounds (623.5, 18.25, 45.5, 36.0);
        surface.getButton (ButtonID.DEVICE_LEFT).setBounds (225.5, 87.25, 79.75, 36.0);
        surface.getButton (ButtonID.DEVICE_RIGHT).setBounds (316.5, 87.25, 79.75, 36.0);
        surface.getButton (ButtonID.DEVICE_ON_OFF).setBounds (407.75, 87.25, 79.75, 36.0);
        surface.getButton (ButtonID.TOGGLE_DEVICE_WINDOW).setBounds (498.75, 87.25, 79.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE1).setBounds (41.5, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE2).setBounds (127.25, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE3).setBounds (212.75, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE4).setBounds (298.5, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE5).setBounds (384.0, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE6).setBounds (469.75, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.PARAM_PAGE7).setBounds (555.25, 153.5, 78.75, 36.0);
        surface.getButton (ButtonID.TOGGLE_DEVICE).setBounds (657.25, 152.0, 86.75, 36.0);
        surface.getButton (ButtonID.FOOTSWITCH2).setBounds (654.25, 87.75, 108.5, 36.0);
    }
}
