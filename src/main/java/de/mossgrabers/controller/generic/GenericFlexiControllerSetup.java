// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ModelSetup;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.device.BrowserMode;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.mode.track.PanMode;
import de.mossgrabers.framework.mode.track.SendMode;
import de.mossgrabers.framework.mode.track.TrackMode;
import de.mossgrabers.framework.mode.track.VolumeMode;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.scale.Scales;

import java.util.Set;


/**
 * Support for generic controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiControllerSetup extends AbstractControllerSetup<GenericFlexiControlSurface, GenericFlexiConfiguration> implements IValueObserver<FlexiCommand>
{
    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param settings The settings
     */
    public GenericFlexiControllerSetup (final IHost host, final ISetupFactory factory, final ISettingsUI settings)
    {
        super (factory, host, settings);
        this.colorManager = new ColorManager ();
        this.valueChanger = new DefaultValueChanger (128, 1, 0.5);
        this.configuration = new GenericFlexiConfiguration (this.valueChanger, host);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 100, 8, 8);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        final ModelSetup ms = new ModelSetup ();
        ms.setNumMarkers (8);
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, ms);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Generic Flexi");

        final GenericFlexiControlSurface surface = new GenericFlexiControlSurface (this.model.getHost (), this.model, this.colorManager, this.configuration, output, input);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));

        this.configuration.setCommandObserver (this);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModes ()
    {
        final GenericFlexiControlSurface surface = this.getSurface ();
        final ModeManager modeManager = surface.getModeManager ();
        modeManager.registerMode (Modes.MODE_TRACK, new TrackMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.MODE_VOLUME, new VolumeMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.MODE_PAN, new PanMode<> (surface, this.model, true));
        for (int i = 0; i < 8; i++)
            modeManager.registerMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + i), new SendMode<> (i, surface, this.model, true));
        modeManager.registerMode (Modes.MODE_DEVICE_PARAMS, new ParameterMode<> (surface, this.model, true));
        modeManager.registerMode (Modes.MODE_BROWSER, new BrowserMode<> (surface, this.model));

        modeManager.setDefaultMode (Modes.MODE_VOLUME);
    }


    /** {@inheritDoc} */
    @Override
    public void startup ()
    {
        this.host.scheduleTask ( () -> {
            final GenericFlexiControlSurface surface = this.getSurface ();
            surface.getConfiguration ().clearNoteMap ();
            surface.getModeManager ().setActiveMode (Modes.MODE_TRACK);
        }, 2000);
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final GenericFlexiControlSurface surface = this.getSurface ();
        this.configuration.addSettingObserver (GenericFlexiConfiguration.SLOT_CHANGE, surface::updateKeyTranslation);

        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver ( (index, selected) -> this.handleTrackChange (selected));
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver ( (index, selected) -> this.handleTrackChange (selected));

        surface.getModeManager ().addModeListener ( (oldMode, newMode) -> this.updateIndication (newMode));
    }


    /**
     * Handle a track selection change.
     *
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (isSelected)
            this.update (null);
    }


    /** {@inheritDoc} */
    @Override
    public void update (final FlexiCommand value)
    {
        this.updateIndication (null);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateIndication (final Integer mode)
    {
        final Set<FlexiCommand> commands = this.configuration.getMappedCommands ();
        final FlexiCommand [] allCommands = FlexiCommand.values ();

        final ITrackBank trackBank = this.model.getTrackBank ();
        final ITrack selectedTrack = trackBank.getSelectedItem ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;

            final ITrack track = trackBank.getItem (i);
            track.setVolumeIndication (this.testVolumeIndication (commands, allCommands, i, hasTrackSel));
            track.setPanIndication (this.testPanIndication (commands, allCommands, i, hasTrackSel));

            final ISendBank sendBank = track.getSendBank ();
            final int sendPageSize = sendBank.getPageSize ();
            for (int j = 0; j < sendPageSize; j++)
                sendBank.getItem (j).setIndication (this.testSendIndication (commands, allCommands, i, hasTrackSel, sendPageSize, j));
        }
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        masterTrack.setVolumeIndication (commands.contains (FlexiCommand.MASTER_SET_VOLUME));
        masterTrack.setPanIndication (commands.contains (FlexiCommand.MASTER_SET_PANORAMA));

        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
            parameterBank.getItem (i).setIndication (this.testParameterIndication (commands, allCommands, i));
    }


    private boolean testVolumeIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel)
    {
        if (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_VOLUME_TRACK))
            return true;
        if (commands.contains (allCommands[FlexiCommand.TRACK_1_SET_VOLUME.ordinal () + trackIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + trackIndex]) && this.getSurface ().getModeManager ().isActiveMode (Modes.MODE_VOLUME);
    }


    private boolean testPanIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel)
    {
        if (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_PANORAMA))
            return true;
        if (commands.contains (allCommands[FlexiCommand.TRACK_1_SET_PANORAMA.ordinal () + trackIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + trackIndex]) && this.getSurface ().getModeManager ().isActiveMode (Modes.MODE_PAN);
    }


    private boolean testSendIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int trackIndex, final boolean hasTrackSel, final int sendPageSize, final int sendIndex)
    {
        final ModeManager modeManager = this.getSurface ().getModeManager ();
        if (hasTrackSel)
        {
            if (commands.contains (allCommands[FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal () + sendIndex]))
                return true;
            if (modeManager.isActiveMode (Modes.MODE_TRACK) && sendIndex < 6)
                return true;
        }
        if (commands.contains (allCommands[FlexiCommand.TRACK_1_SET_SEND_1.ordinal () + sendIndex * sendPageSize + trackIndex]))
            return true;
        return modeManager.isActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + sendIndex));
    }


    private boolean testParameterIndication (final Set<FlexiCommand> commands, final FlexiCommand [] allCommands, final int parameterIndex)
    {
        if (commands.contains (allCommands[FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal () + parameterIndex]))
            return true;
        return commands.contains (allCommands[FlexiCommand.MODES_KNOB1.ordinal () + parameterIndex]) && this.getSurface ().getModeManager ().isActiveMode (Modes.MODE_DEVICE_PARAMS);
    }
}
