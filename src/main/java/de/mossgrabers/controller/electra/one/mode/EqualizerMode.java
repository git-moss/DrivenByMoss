// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.EqualizerBandType;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * The equalizer mode.
 *
 * @author Jürgen Moßgraber
 */
public class EqualizerMode extends AbstractElectraOneMode
{
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
        super (3, Modes.NAME_EQUALIZER, surface, model);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        this.eqDevice = (IEqualizerDevice) this.model.getSpecificDevice (DeviceID.EQ);

        final IParameterProvider emptyProvider = new EmptyParameterProvider (1);
        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (0), this.eqDevice.getFrequencyParameter (0), this.eqDevice.getGainParameter (0), this.eqDevice.getQParameter (0), this.masterTrack.getVolumeParameter ()),
                // Row 2
                emptyProvider, new FixedParameterProvider (this.eqDevice.getTypeParameter (1), this.eqDevice.getFrequencyParameter (1), this.eqDevice.getGainParameter (1), this.eqDevice.getQParameter (1), new PlayPositionParameter (model.getValueChanger (), this.transport, surface)),
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
    public void updateDisplay ()
    {
        for (int row = 0; row < 6; row++)
        {
            final EqualizerBandType typeID = this.eqDevice.getTypeID (row);
            final boolean exists = typeID != EqualizerBandType.OFF;

            this.pageCache.updateColor (row, 0, exists ? ElectraOneColorManager.BAND_ON : ElectraOneColorManager.BAND_OFF);

            final IParameter typeParameter = this.eqDevice.getTypeParameter (row);
            final IParameter frequencyParameter = this.eqDevice.getFrequencyParameter (row);
            final IParameter gainParameter = this.eqDevice.getGainParameter (row);
            final IParameter qParameter = this.eqDevice.getQParameter (row);

            this.pageCache.updateValue (row, 1, typeParameter.getValue (), StringUtils.optimizeName (StringUtils.fixASCII (typeParameter.getDisplayedValue ()), 15));
            this.pageCache.updateValue (row, 2, frequencyParameter.getValue (), StringUtils.optimizeName (StringUtils.fixASCII (frequencyParameter.getDisplayedValue ()), 15));
            this.pageCache.updateValue (row, 3, gainParameter.getValue (), StringUtils.optimizeName (StringUtils.fixASCII (gainParameter.getDisplayedValue ()), 15));
            this.pageCache.updateValue (row, 4, qParameter.getValue (), StringUtils.optimizeName (StringUtils.fixASCII (qParameter.getDisplayedValue ()), 15));

            this.pageCache.updateElement (row, 2, null, null, Boolean.TRUE);
            this.pageCache.updateElement (row, 3, null, null, Boolean.TRUE);
            this.pageCache.updateElement (row, 4, null, null, Boolean.TRUE);
        }

        this.pageCache.updateColor (3, 5, this.eqDevice.doesExist () && this.eqDevice.isEnabled () ? ElectraOneColorManager.BAND_ON : ElectraOneColorManager.BAND_OFF);

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));
        this.pageCache.updateValue (1, 5, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
        this.pageCache.updateElement (1, 5, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getPositionText ()), 15), null, null);

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }
}