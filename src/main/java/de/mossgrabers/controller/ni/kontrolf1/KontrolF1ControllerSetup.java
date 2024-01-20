// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrolf1;

import de.mossgrabers.controller.ni.kontrolf1.controller.KontrolF1ColorManager;
import de.mossgrabers.controller.ni.kontrolf1.controller.KontrolF1ControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.valuechanger.OffsetBinaryRelativeValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Support for the Arturia Beatstep and Beatstep Pro controllers.
 *
 * @author Jürgen Moßgraber
 */
public class KontrolF1ControllerSetup extends AbstractControllerSetup<KontrolF1ControlSurface, KontrolF1Configuration>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param globalSettings The global settings
     * @param documentSettings The document (project) specific settings
     */
    public KontrolF1ControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        super (factory, host, globalSettings, documentSettings);

        this.colorManager = new KontrolF1ColorManager ();
        this.valueChanger = new OffsetBinaryRelativeValueChanger (128, 1);
        this.configuration = new KontrolF1Configuration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, value) -> this.handleTrackChange (value));
        trackBank.setIndication (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        // Might need to change these to CCs or more specific for the pads.
        final IMidiInput input = midiAccess.createInput ("Pads", "80????" /* Note off */,
                "90????" /* Note on */);

        this.surfaces.add (new KontrolF1ControlSurface (this.host, this.colorManager, this.configuration, output, input));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        // final KontrolF1ControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // viewManager.register (Views.SESSION, new SessionView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.getSurface ().getViewManager ().addChangeListener ( (previousViewId, activeViewId) -> this.updateIndication ());
        this.createScaleObservers (this.configuration);

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected BindType getTriggerBindType (final ButtonID buttonID)
    {
        // TBC - possibly indicates the type of midi event handled by `registerTriggerCommands`?
        return BindType.NOTE;
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        // This is a shift mode, I might want one of these.
        // final BeatstepControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();

        // this.addButton (ButtonID.SHIFT, "Shift", (event, value) -> {

        //     if (event == ButtonEvent.DOWN)
        //     {
        //         viewManager.setActive (Views.SHIFT);
        //         return;
        //     }

        //     if (event == ButtonEvent.UP)
        //     {
        //         if (viewManager.isActive (Views.SHIFT))
        //             viewManager.restore ();

        //         // Red LED is turned off on button release, restore the correct color
        //         final BeatstepPadGrid beatstepPadGrid = (BeatstepPadGrid) surface.getPadGrid ();
        //         for (int note = 36; note < 52; note++)
        //         {
        //             final LightInfo lightInfo = beatstepPadGrid.getLightInfo (note);
        //             beatstepPadGrid.lightPad (note, lightInfo.getColor ());
        //         }
        //     }

        // }, BeatstepControlSurface.BEATSTEP_SHIFT);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        // TBC - this is where I set up my channel remote controls.

        // final BeatstepControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // for (int i = 0; i < 8; i++)
        // {
        //     this.addRelativeKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowViewCommand (i, this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_1 + i, RelativeEncoding.OFFSET_BINARY);
        //     this.addRelativeKnob (ContinuousID.get (ContinuousID.DEVICE_KNOB1, i), "Knob " + (i + 9), new KnobRowViewCommand (i + 8, this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_9 + i, RelativeEncoding.OFFSET_BINARY);
        // }

        // this.addRelativeKnob (ContinuousID.MASTER_KNOB, "Master", new PlayPositionCommand<> (this.model, surface), BindType.CC, 2, BeatstepControlSurface.BEATSTEP_KNOB_MAIN, RelativeEncoding.OFFSET_BINARY);

        // final PlayView playView = (PlayView) viewManager.get (Views.PLAY);
        // playView.registerAftertouchCommand (new AftertouchViewCommand<> (playView, this.model, surface));

        // this.addFader (ContinuousID.get (ContinuousID.FADER1, i), "Fader " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, APCminiControlSurface.APC_KNOB_TRACK_LEVEL1 + i).setIndexInGroup (i);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        // TBC - Assuming optional, virtual representation of controller. Can add this if useful.

        final KontrolF1ControlSurface surface = this.getSurface ();

        // surface.getButton (ButtonID.PAD1).setBounds (145.25, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD2).setBounds (222.5, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD3).setBounds (302.75, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD4).setBounds (382.75, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD5).setBounds (463.0, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD6).setBounds (543.0, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD7).setBounds (623.25, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD8).setBounds (703.25, 232.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD9).setBounds (145.25, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD10).setBounds (222.5, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD11).setBounds (302.75, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD12).setBounds (382.75, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD13).setBounds (463.0, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD14).setBounds (543.0, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD15).setBounds (623.25, 151.75, 57.0, 55.5);
        // surface.getButton (ButtonID.PAD16).setBounds (703.25, 151.75, 57.0, 55.5);

        // surface.getButton (ButtonID.SHIFT).setBounds (45.5, 262.0, 30.25, 30.25);

        // surface.getContinuous (ContinuousID.DEVICE_KNOB1).setBounds (155.25, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB2).setBounds (237.0, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB3).setBounds (318.75, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB4).setBounds (400.5, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB5).setBounds (482.25, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB6).setBounds (564.0, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB7).setBounds (646.0, 92.25, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.DEVICE_KNOB8).setBounds (727.75, 92.25, 35.0, 32.5);

        surface.getContinuous (ContinuousID.KNOB1).setBounds (10, 10, 20, 20);
        // surface.getContinuous (ContinuousID.KNOB2).setBounds (237.0, 19.5, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.KNOB3).setBounds (318.75, 19.5, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.KNOB4).setBounds (400.5, 19.5, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.KNOB5).setBounds (482.25, 19.5, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.KNOB6).setBounds (564.0, 19.5, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.KNOB7).setBounds (646.0, 19.5, 35.0, 32.5);
        // surface.getContinuous (ContinuousID.KNOB8).setBounds (727.75, 19.5, 35.0, 32.5);

        // surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (39.75, 30.5, 75.5, 77.75);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
    }


    protected void updateIndication ()
    {
        // TBC - Assuming not required, this is for specific lights/outputs; assuming the session view is handled elsewhere.
    }
}
