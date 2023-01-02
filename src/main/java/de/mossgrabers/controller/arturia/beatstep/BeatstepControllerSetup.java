// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep;

import de.mossgrabers.controller.arturia.beatstep.command.continuous.KnobRowViewCommand;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepPadGrid;
import de.mossgrabers.controller.arturia.beatstep.view.BrowserView;
import de.mossgrabers.controller.arturia.beatstep.view.DeviceView;
import de.mossgrabers.controller.arturia.beatstep.view.DrumView;
import de.mossgrabers.controller.arturia.beatstep.view.PlayView;
import de.mossgrabers.controller.arturia.beatstep.view.SequencerView;
import de.mossgrabers.controller.arturia.beatstep.view.SessionView;
import de.mossgrabers.controller.arturia.beatstep.view.ShiftView;
import de.mossgrabers.controller.arturia.beatstep.view.TrackView;
import de.mossgrabers.framework.command.aftertouch.AftertouchViewCommand;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.RelativeEncoding;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Support for the Arturia Beatstep and Beatstep Pro controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerSetup extends AbstractControllerSetup<BeatstepControlSurface, BeatstepConfiguration>
{
    // @formatter:off
    private static final int [] DRUM_MATRIX =
    {
        0,  1,  2,  3,  4,  5,  6,  7,
        8,  9, 10, 11, 12, 13, 14, 15,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1,
       -1, -1, -1, -1, -1, -1, -1, -1
    };
    // @formatter:on


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public BeatstepControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new BeatstepColorManager ();
        this.valueChanger = new TwosComplementValueChanger (128, 1);
        this.configuration = new BeatstepConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
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
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        this.model.getTrackBank ().addSelectionObserver ( (index, value) -> this.handleTrackChange (value));
    }


    /** {@inheritDoc} */
    @Override
    protected void recallLastView ()
    {
        // Prevent view change on track change, which is confusing on the Beatstep since it leaves
        // Track view
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Control/Pads", "82????", "92????", "A2????");

        // Sequencer 1 is on channel 1
        final INoteInput seqNoteInput = input.createNoteInput ("Hardware Sequencer", "90????", "80????");

        final Integer [] table = new Integer [128];
        for (int i = 0; i < 128; i++)
        {
            // Block the Shift key
            table[i] = Integer.valueOf (i == 7 ? -1 : i);
        }
        seqNoteInput.setKeyTranslationTable (table);

        this.surfaces.add (new BeatstepControlSurface (this.host, this.colorManager, this.configuration, output, input));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.register (Views.TRACK, new TrackView (surface, this.model));
        viewManager.register (Views.DEVICE, new DeviceView (surface, this.model));
        viewManager.register (Views.PLAY, new PlayView (surface, this.model));

        viewManager.register (Views.DRUM, new DrumView (surface, this.model));
        viewManager.register (Views.SEQUENCER, new SequencerView (surface, this.model));
        viewManager.register (Views.SESSION, new SessionView (surface, this.model));

        viewManager.register (Views.BROWSER, new BrowserView (surface, this.model));
        viewManager.register (Views.SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.getSurface ().getViewManager ().addChangeListener ( (previousViewId, activeViewId) -> this.updateIndication ());
        this.createScaleObservers (this.configuration);

        this.configuration.registerDeactivatedItemsHandler (this.model);

        this.activateBrowserObserver (Views.BROWSER);
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        return BindType.NOTE;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();

        this.addButton (ButtonID.SHIFT, "Shift", (event, value) -> {

            if (event == ButtonEvent.DOWN)
            {
                viewManager.setActive (Views.SHIFT);
                return;
            }

            if (event == ButtonEvent.UP)
            {
                if (viewManager.isActive (Views.SHIFT))
                    viewManager.restore ();

                // Red LED is turned off on button release, restore the correct color
                final BeatstepPadGrid beatstepPadGrid = (BeatstepPadGrid) surface.getPadGrid ();
                for (int note = 36; note < 52; note++)
                {
                    final LightInfo lightInfo = beatstepPadGrid.getLightInfo (note);
                    beatstepPadGrid.lightPad (note, lightInfo.getColor ());
                }
            }

        }, BeatstepControlSurface.BEATSTEP_SHIFT);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        for (int i = 0; i < 8; i++)
        {
            this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowViewCommand (i, this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_1 + i, RelativeEncoding.OFFSET_BINARY);
            this.addRelativeKnob (ContinuousID.get (ContinuousID.DEVICE_KNOB1, i), "Knob " + (i + 9), new KnobRowViewCommand (i + 8, this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_9 + i, RelativeEncoding.OFFSET_BINARY);
        }

        this.addRelativeKnob (ContinuousID.MASTER_KNOB, "Master", new PlayPositionCommand<> (this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_MAIN, RelativeEncoding.OFFSET_BINARY);

        final PlayView playView = (PlayView) viewManager.get (Views.PLAY);
        playView.registerAftertouchCommand (new AftertouchViewCommand<> (playView, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final BeatstepControlSurface surface = this.getSurface ();

        surface.getButton (ButtonID.PAD1).setBounds (145.25, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD2).setBounds (222.5, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD3).setBounds (302.75, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD4).setBounds (382.75, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD5).setBounds (463.0, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD6).setBounds (543.0, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD7).setBounds (623.25, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD8).setBounds (703.25, 232.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD9).setBounds (145.25, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD10).setBounds (222.5, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD11).setBounds (302.75, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD12).setBounds (382.75, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD13).setBounds (463.0, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD14).setBounds (543.0, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD15).setBounds (623.25, 151.75, 57.0, 55.5);
        surface.getButton (ButtonID.PAD16).setBounds (703.25, 151.75, 57.0, 55.5);

        surface.getButton (ButtonID.SHIFT).setBounds (45.5, 262.0, 30.25, 30.25);

        surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (155.25, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (237.0, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (318.75, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (400.5, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (482.25, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (564.0, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (646.0, 92.25, 35.0, 32.5);
        surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (727.75, 92.25, 35.0, 32.5);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (155.25, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB2).setBounds (237.0, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB3).setBounds (318.75, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB4).setBounds (400.5, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB5).setBounds (482.25, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB6).setBounds (564.0, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB7).setBounds (646.0, 19.5, 35.0, 32.5);
        surface.getContinuous (ContinuousID.KNOB8).setBounds (727.75, 19.5, 35.0, 32.5);

        surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (39.75, 30.5, 75.5, 77.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        // Enable Shift button to send MIDI Note 07
        final BeatstepControlSurface surface = this.getSurface ();
        surface.getMidiOutput ().sendSysex ("F0 00 20 6B 7F 42 02 00 01 5E 09 F7");
        surface.getViewManager ().setActive (Views.TRACK);
    }


    protected void updateIndication ()
    {
        final BeatstepControlSurface surface = this.getSurface ();

        final ViewManager viewManager = surface.getViewManager ();
        final boolean isTrack = viewManager.isActive (Views.TRACK);
        final boolean isDevice = viewManager.isActive (Views.DEVICE);
        final boolean isSession = viewManager.isActive (Views.SESSION);

        final IMasterTrack mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (!isDevice);

        final ITrackBank tb = this.model.getTrackBank ();
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
        final ITrackBank tbe = this.model.getEffectTrackBank ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        if (tbe != null)
            tbe.setIndication (isEffect && isSession);

        final IParameterBank parameterBank = cursorDevice.getParameterBank ();
        for (int i = 0; i < 8; i++)
        {
            final boolean hasTrackSel = selectedTrack.isPresent () && selectedTrack.get ().getIndex () == i;
            final ITrack track = tb.getItem (i);
            track.setVolumeIndication (!isEffect && hasTrackSel && !isDevice);
            track.setPanIndication (!isEffect && hasTrackSel && !isDevice);
            final ISendBank sendBank = track.getSendBank ();
            for (int j = 0; j < 6; j++)
                sendBank.getItem (j).setIndication (!isEffect && hasTrackSel && isTrack);

            if (tbe != null)
            {
                final Optional<ITrack> selectedFXTrack = tbe.getSelectedItem ();
                final boolean hasFXTrackSel = selectedFXTrack.isPresent () && selectedFXTrack.get ().getIndex () == i;
                final ITrack fxTrack = tbe.getItem (i);
                fxTrack.setVolumeIndication (isEffect && hasFXTrackSel && isTrack);
                fxTrack.setPanIndication (isEffect && hasFXTrackSel && isTrack);
            }

            parameterBank.getItem (i).setIndication (isDevice);
        }
    }
}
