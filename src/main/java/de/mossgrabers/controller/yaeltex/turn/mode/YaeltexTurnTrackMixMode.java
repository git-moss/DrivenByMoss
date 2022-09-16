package de.mossgrabers.controller.yaeltex.turn.mode;

import de.mossgrabers.controller.yaeltex.turn.YaeltexTurnConfiguration;
import de.mossgrabers.controller.yaeltex.turn.controller.YaeltexTurnControlSurface;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.List;
import java.util.Optional;


/**
 * Track mix mode for the Yaeltex Turn.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class YaeltexTurnTrackMixMode extends AbstractParameterMode<YaeltexTurnControlSurface, YaeltexTurnConfiguration, ITrack> implements IYaeltexKnobMode
{
    private final IEqualizerDevice eqDevice;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public YaeltexTurnTrackMixMode (final YaeltexTurnControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Track Mixer", surface, model, true, model.getTrackBank (), controls);

        final IParameterProvider panParameterProvider = new PanParameterProvider (model);
        final IParameterProvider sendParameterProvider1 = new SendParameterProvider (model, 0, 0);
        final IParameterProvider sendParameterProvider2 = new SendParameterProvider (model, 1, 0);
        final IParameterProvider deviceParameterProvider = new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ());

        this.eqDevice = (IEqualizerDevice) model.getSpecificDevice (DeviceID.EQ);
        final IParameterProvider typeProvider = new FixedParameterProvider (this.eqDevice.getTypeParameters ());
        final IParameterProvider qProvider = new FixedParameterProvider (this.eqDevice.getQParameters ());
        final IParameterProvider frequencyProvider = new FixedParameterProvider (this.eqDevice.getFrequencyParameters ());
        final IParameterProvider gainProvider = new FixedParameterProvider (this.eqDevice.getGainParameters ());

        final IParameterProvider volumeParameterProvider = new VolumeParameterProvider (model);

        this.setParameterProvider (new CombinedParameterProvider (panParameterProvider, sendParameterProvider1, sendParameterProvider2, deviceParameterProvider, typeProvider, qProvider, frequencyProvider, gainProvider, volumeParameterProvider));

        this.model.getTrackBank ().addSelectionObserver (this::trackSelectionChanged);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.trackSelectionChanged (-1, true);

        super.onActivate ();
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        final ITrack track = this.model.getTrackBank ().getItem (column);
        switch (row)
        {
            case 0:
                return track.getPan ();
            case 1:
                return track.getSendBank ().getItem (0).getValue ();
            case 2:
                return track.getSendBank ().getItem (1).getValue ();
            case 3:
                return this.model.getCursorDevice ().getParameterBank ().getItem (column).getValue ();
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getKnobColor (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        final ITrack track = this.model.getTrackBank ().getItem (column);
        switch (row)
        {
            case 0, 1, 2:
                return track.getColor ();
            case 3:
                return ColorEx.WHITE;
            default:
                return ColorEx.BLACK;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        switch (row)
        {
            case 0:
                final IParameter crossfadeParameter = track.getCrossfadeParameter ();
                final double value = this.model.getValueChanger ().toNormalizedValue (crossfadeParameter.getValue ()) + 0.5;
                crossfadeParameter.setNormalizedValue (value > 1.1 ? 0 : value);
                break;

            case 1:
                if (this.surface.isShiftPressed ())
                    track.returnToArrangement ();
                else
                    track.stop ();
                break;

            case 2:
                track.toggleRecArm ();
                break;

            case 3:
                track.toggleSolo ();
                break;

            case 4:
                track.toggleMute ();
                break;

            case 5:
                if (this.surface.isShiftPressed ())
                {
                    this.surface.getDisplay ().notify (AbstractConfiguration.getNewClipLengthValue (index));
                    this.surface.getConfiguration ().setNewClipLength (index);
                }
                else
                    track.selectOrExpandGroup ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank currentTrackBank = this.model.getCurrentTrackBank ();
        ITrack track;

        switch (buttonID)
        {
            case ROW1_1, ROW1_2, ROW1_3, ROW1_4, ROW1_5, ROW1_6, ROW1_7, ROW1_8:
                track = currentTrackBank.getItem (buttonID.ordinal () - ButtonID.ROW1_1.ordinal ());
                if (!track.doesExist ())
                    return 0;
                final String crossfadeMode = track.getCrossfadeParameter ().getDisplayedValue ();
                if ("AB".equals (crossfadeMode))
                    return 0;
                return "A".equals (crossfadeMode) ? 1 : 2;

            case ROW2_1, ROW2_2, ROW2_3, ROW2_4, ROW2_5, ROW2_6, ROW2_7, ROW2_8:
                track = currentTrackBank.getItem (buttonID.ordinal () - ButtonID.ROW2_1.ordinal ());
                return track.doesExist () ? 1 : 0;

            case ROW3_1, ROW3_2, ROW3_3, ROW3_4, ROW3_5, ROW3_6, ROW3_7, ROW3_8:
                track = currentTrackBank.getItem (buttonID.ordinal () - ButtonID.ROW3_1.ordinal ());
                return track.isRecArm () ? 1 : 0;

            case ROW4_1, ROW4_2, ROW4_3, ROW4_4, ROW4_5, ROW4_6, ROW4_7, ROW4_8:
                track = currentTrackBank.getItem (buttonID.ordinal () - ButtonID.ROW4_1.ordinal ());
                return track.isSolo () ? 1 : 0;

            case ROW5_1, ROW5_2, ROW5_3, ROW5_4, ROW5_5, ROW5_6, ROW5_7, ROW5_8:
                track = currentTrackBank.getItem (buttonID.ordinal () - ButtonID.ROW5_1.ordinal ());
                return track.isMute () ? 1 : 0;

            case ROW6_1, ROW6_2, ROW6_3, ROW6_4, ROW6_5, ROW6_6, ROW6_7, ROW6_8:
                final int index = buttonID.ordinal () - ButtonID.ROW6_1.ordinal ();
                if (this.surface.isShiftPressed ())
                    return this.surface.getConfiguration ().getNewClipLength () == index ? 3 : 0;
                track = currentTrackBank.getItem (index);
                if (!track.doesExist ())
                    return 0;
                return track.isSelected () ? 2 : 1;

            // Not used
            default:
                break;
        }

        return 0;
    }


    /**
     * Add a equalizer device to a track which does not already contain one.
     *
     * @param index The index of the selected or de-selected track
     * @param isSelected Is the track selected or de-selected?
     */
    private void trackSelectionChanged (final int index, final boolean isSelected)
    {
        if (!(isSelected && this.isActive))
            return;

        // Add an equalizer if not present
        if (!this.eqDevice.doesExist ())
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isPresent ())
                selectedTrack.get ().addEqualizerDevice ();
        }
    }
}
