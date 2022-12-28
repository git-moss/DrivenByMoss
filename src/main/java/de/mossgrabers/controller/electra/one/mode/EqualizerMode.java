// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.ElectraOnePlayPositionParameter;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.EqualizerBandType;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * The equalizer mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EqualizerMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    private final PageCache        pageCache;
    private final ITransport       transport;
    private final IMasterTrack     masterTrack;
    private final IEqualizerDevice eqDevice;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public EqualizerMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_EQUALIZER, surface, model, true, ElectraOneControlSurface.KNOB_IDS);

        this.pageCache = new PageCache (3, surface);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        this.eqDevice = (IEqualizerDevice) this.model.getSpecificDevice (DeviceID.EQ);

        final IParameterProvider emptyProvider = new EmptyParameterProvider (1);
        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (0), this.eqDevice.getFrequencyParameter (0), this.eqDevice.getGainParameter (0), this.eqDevice.getQParameter (0), this.masterTrack.getVolumeParameter ()),
                // Row 2
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (1), this.eqDevice.getFrequencyParameter (1), this.eqDevice.getGainParameter (1), this.eqDevice.getQParameter (1), new ElectraOnePlayPositionParameter (model.getValueChanger (), model.getTransport (), surface)),
                // Row 3
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (2), this.eqDevice.getFrequencyParameter (2), this.eqDevice.getGainParameter (2), this.eqDevice.getQParameter (2)), emptyProvider,
                // Row 4
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (3), this.eqDevice.getFrequencyParameter (3), this.eqDevice.getGainParameter (3), this.eqDevice.getQParameter (3)), emptyProvider,
                // Row 5
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (4), this.eqDevice.getFrequencyParameter (4), this.eqDevice.getGainParameter (4), this.eqDevice.getQParameter (4)), emptyProvider,
                // Row 6
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (5), this.eqDevice.getFrequencyParameter (5), this.eqDevice.getGainParameter (5), this.eqDevice.getQParameter (5)), emptyProvider));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (column == 0)
        {
            final boolean isOff = this.eqDevice.getTypeID (row) == EqualizerBandType.OFF;
            this.eqDevice.setType (row, isOff ? EqualizerBandType.BELL : EqualizerBandType.OFF);
            return;
        }

        if (column == 5)
        {
            switch (row)
            {
                // Add EQ
                case 2:
                    final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
                    if (track.isPresent ())
                        track.get ().addEqualizerDevice ();
                    break;
                // On/Off
                case 3:
                    this.eqDevice.toggleEnabledState ();
                    break;
                // Record
                case 4:
                    this.transport.startRecording ();
                    break;
                // Play
                case 5:
                    this.transport.play ();
                    break;

                default:
                    // Not used
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        for (int row = 0; row < 6; row++)
        {
            final EqualizerBandType typeID = this.eqDevice.getTypeID (row);
            final Boolean exists = Boolean.valueOf (typeID != EqualizerBandType.OFF);

            this.pageCache.updateLabel (row, 0, null, exists.booleanValue () ? ElectraOneColorManager.BAND_ON : ElectraOneColorManager.BAND_OFF, null);

            this.pageCache.updateValue (row, 1, this.eqDevice.getTypeParameter (row).getValue ());
            this.pageCache.updateValue (row, 2, this.eqDevice.getFrequencyParameter (row).getValue ());
            this.pageCache.updateValue (row, 3, this.eqDevice.getGainParameter (row).getValue ());
            this.pageCache.updateValue (row, 4, this.eqDevice.getQParameter (row).getValue ());

            this.pageCache.updateLabel (row, 2, null, null, exists);
            this.pageCache.updateLabel (row, 3, null, null, exists);
            this.pageCache.updateLabel (row, 4, null, null, exists);
        }

        this.pageCache.updateLabel (3, 5, null, this.eqDevice.doesExist () && this.eqDevice.isEnabled () ? ElectraOneColorManager.BAND_ON : ElectraOneColorManager.BAND_OFF, null);

        // Master
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume ());
        this.pageCache.updateLabel (0, 5, null, this.masterTrack.getColor (), null);

        // Transport
        this.pageCache.updateLabel (1, 5, this.transport.getBeatText (), null, null);
        this.pageCache.updateLabel (4, 5, null, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF, null);
        this.pageCache.updateLabel (5, 5, null, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF, null);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.pageCache.reset ();

        super.onActivate ();
    }
}