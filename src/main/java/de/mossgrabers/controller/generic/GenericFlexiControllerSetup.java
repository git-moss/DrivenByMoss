// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic;

import de.mossgrabers.controller.generic.controller.FlexiCommand;
import de.mossgrabers.controller.generic.controller.GenericFlexiControlSurface;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.configuration.IValueObserver;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.DefaultValueChanger;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IParameterBank;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
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
        this.configuration = new GenericFlexiConfiguration (this.valueChanger);
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
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1, 8);
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
    public void startup ()
    {
        this.host.scheduleTask ( () -> this.getSurface ().updateKeyTranslation (), 2000);
    }


    /** {@inheritDoc} */
    @Override
    public void update (final FlexiCommand value)
    {
        final Set<FlexiCommand> commands = this.configuration.getMappedCommands ();
        final FlexiCommand [] allCommands = FlexiCommand.values ();

        final ITrackBank trackBank = this.model.getTrackBank ();
        final ITrack selectedTrack = trackBank.getSelectedItem ();
        for (int i = 0; i < trackBank.getPageSize (); i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;

            final ITrack track = trackBank.getItem (i);
            track.setVolumeIndication (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_VOLUME_TRACK) || commands.contains (allCommands[FlexiCommand.TRACK_1_SET_VOLUME.ordinal () + i]));
            track.setPanIndication (hasTrackSel && commands.contains (FlexiCommand.TRACK_SELECTED_SET_PANORAMA) || commands.contains (allCommands[FlexiCommand.TRACK_1_SET_PANORAMA.ordinal () + i]));

            final ISendBank sendBank = track.getSendBank ();
            final int sendPageSize = sendBank.getPageSize ();
            for (int j = 0; j < sendPageSize; j++)
            {
                final ISend send = sendBank.getItem (j);
                send.setIndication (hasTrackSel && commands.contains (allCommands[FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal () + j]) || commands.contains (allCommands[FlexiCommand.TRACK_1_SET_SEND_1.ordinal () + j * sendPageSize + i]));
            }
        }
        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        masterTrack.setVolumeIndication (commands.contains (FlexiCommand.MASTER_SET_VOLUME));
        masterTrack.setPanIndication (commands.contains (FlexiCommand.MASTER_SET_PANORAMA));

        final IParameterBank parameterBank = this.model.getCursorDevice ().getParameterBank ();
        for (int i = 0; i < parameterBank.getPageSize (); i++)
        {
            final IParameter parameter = parameterBank.getItem (i);
            parameter.setIndication (commands.contains (allCommands[FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal () + i]));
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        trackBank.addSelectionObserver (this::handleTrackChange);
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver (this::handleTrackChange);
    }


    /**
     * Handle a track selection change.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        if (isSelected)
            this.update (null);
    }
}
