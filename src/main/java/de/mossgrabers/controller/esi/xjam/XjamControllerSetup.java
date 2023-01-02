// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.esi.xjam;

import de.mossgrabers.controller.esi.xjam.controller.XjamControlSurface;
import de.mossgrabers.framework.command.continuous.KnobRowModeCommand;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.hardware.BindType;
import de.mossgrabers.framework.controller.hardware.IHwAbsoluteKnob;
import de.mossgrabers.framework.controller.valuechanger.TwosComplementValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.device.UserMode;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.mode.track.TrackPanMode;
import de.mossgrabers.framework.mode.track.TrackSendMode;
import de.mossgrabers.framework.mode.track.TrackVolumeMode;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.Views;

import java.util.List;


/**
 * Setup to support the ESI Xjam controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XjamControllerSetup extends AbstractControllerSetup<XjamControlSurface, XjamConfiguration>
{
    private static final int CHANNEL = 0;


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
        this.colorManager = new ColorManager ();
        this.configuration = new XjamConfiguration (host, this.valueChanger, factory.getArpeggiatorModes ());
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 0, 127, 4, 4);
        this.scales.setChromatic (true);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setHasFullFlatTrackList (true);
        ms.setNumTracks (6);
        ms.setNumSends (4);
        ms.setNumParams (6);
        ms.setNumUserPageSize (6);

        this.model = this.factory.createModel (this.configuration, this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiInput input = midiAccess.createInput ("Xjam Pads", "80????" /* Note off */,
                "90????" /* Note on */, "D0????" /* Channel After-touch */);

        final XjamControlSurface surface = new XjamControlSurface (this.host, this.colorManager, this.configuration, input);
        this.surfaces.add (surface);

        surface.getModeManager ().setDefaultID (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final XjamControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();

        final List<ContinuousID> trackKnobs = ContinuousID.createSequentialList (ContinuousID.KNOB1, 6);
        modeManager.register (Modes.TRACK, new TrackMode<> (surface, this.model, true, trackKnobs));
        modeManager.register (Modes.VOLUME, new TrackVolumeMode<> (surface, this.model, true, trackKnobs));
        modeManager.register (Modes.PAN, new TrackPanMode<> (surface, this.model, true, trackKnobs));
        modeManager.register (Modes.SEND1, new TrackSendMode<> (0, surface, this.model, true, trackKnobs));
        modeManager.register (Modes.SEND2, new TrackSendMode<> (1, surface, this.model, true, trackKnobs));
        modeManager.register (Modes.SEND3, new TrackSendMode<> (2, surface, this.model, true, trackKnobs));
        modeManager.register (Modes.SEND4, new TrackSendMode<> (3, surface, this.model, true, trackKnobs));

        final List<ContinuousID> deviceKnobs = ContinuousID.createSequentialList (ContinuousID.PARAM_KNOB1, 6);
        modeManager.register (Modes.DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true, deviceKnobs));

        final List<ContinuousID> userKnobs = ContinuousID.createSequentialList (ContinuousID.DEVICE_KNOB1, 6);
        modeManager.register (Modes.USER, new UserMode<> (surface, this.model, true, userKnobs));
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final XjamControlSurface surface = this.getSurface ();
        // final ViewManager viewManager = surface.getViewManager ();
        // viewManager.register (Views.CONTROL, new ControlView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        super.createObservers ();

        this.createScaleObservers (this.configuration);
        this.configuration.addSettingObserver (XjamConfiguration.SCALE_IS_ACTIVE, this::updateViewNoteMapping);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));

        this.configuration.registerDeactivatedItemsHandler (this.model);
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        final XjamControlSurface surface = this.getSurface ();
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final XjamControlSurface surface = this.getSurface ();
        final IMidiInput input = surface.getMidiInput ();

        for (int i = 0; i < 8; i++)
        {
            final IHwAbsoluteKnob bank1Knob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.KNOB1, i), "Knob " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, CHANNEL, XjamControlSurface.BANK1_ENCODER_1 + i);
            bank1Knob.setIndexInGroup (i);

            final IHwAbsoluteKnob bank2Knob = this.addAbsoluteKnob (ContinuousID.get (ContinuousID.PARAM_KNOB1, i), "Param Knob " + (i + 1), new KnobRowModeCommand<> (i, this.model, surface), BindType.CC, CHANNEL, XjamControlSurface.BANK1_ENCODER_1 + i);
            bank2Knob.setIndexInGroup (i);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        final XjamControlSurface surface = this.getSurface ();
        surface.getViewManager ().setActive (Views.CONTROL);
        surface.getModeManager ().setActive (Modes.TRACK);
    }


    /** {@inheritDoc} */
    @Override
    protected void layoutControls ()
    {
        final XjamControlSurface surface = this.getSurface ();

        // TODO
        // surface.getButton (ButtonID.PLAY).setBounds (7.25, 135.25, 42.25, 23.5);
        //
        // surface.getContinuous (ContinuousID.MASTER_KNOB).setBounds (693.25, 44.75, 43.0, 53.75);
    }
}
