// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.EqualizerBandType;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.parameter.IParameter;

import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SpecificBitwigDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Encapsulates the data of a Bitwig equalizer EQ+ device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EqualizerDeviceImpl extends SpecificDeviceImpl implements IEqualizerDevice
{
    /** The ID of the Bitwig EQ+ plugin. */
    public static final UUID                            ID_BITWIG_EQ_PLUS = UUID.fromString ("e4815188-ba6f-4d14-bcfc-2dcb8f778ccb");

    private static final Map<String, EqualizerBandType> EQ_TYPES          = new HashMap<> ();
    static
    {
        EQ_TYPES.put ("Off", EqualizerBandType.OFF);
        EQ_TYPES.put ("Low-c", EqualizerBandType.LOWCUT);
        EQ_TYPES.put ("Low-s", EqualizerBandType.LOWSHELF);
        EQ_TYPES.put ("Bell", EqualizerBandType.BELL);
        EQ_TYPES.put ("High-c", EqualizerBandType.HIGHCUT);
        EQ_TYPES.put ("High-s", EqualizerBandType.HIGHSHELF);
        EQ_TYPES.put ("Notch", EqualizerBandType.NOTCH);
    }

    private static final int            NUMBER_OF_BANDS = 8;

    private final List<EqTypeParameter> types           = new ArrayList<> ();
    private final List<IParameter>      frequencies     = new ArrayList<> ();
    private final List<IParameter>      gains           = new ArrayList<> ();
    private final List<IParameter>      qs              = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param device The device to encapsulate
     * @param numParamPages The number of parameter pages
     * @param numParams The number of parameters
     */
    public EqualizerDeviceImpl (final IHost host, final IValueChanger valueChanger, final Device device, final int numParamPages, final int numParams)
    {
        super (host, valueChanger, device, 0, numParamPages, numParams, 0, 0, 0);

        final SpecificBitwigDevice eqDevice = device.createSpecificBitwigDevice (ID_BITWIG_EQ_PLUS);

        for (int i = 0; i < NUMBER_OF_BANDS; i++)
        {
            final String index = Integer.toString (i + 1);

            final Parameter typeParameter = eqDevice.createParameter ("TYPE" + index);
            this.types.add (new EqTypeParameter (valueChanger, typeParameter, i));

            final Parameter frequencyParameter = eqDevice.createParameter ("FREQ" + index);
            this.frequencies.add (new ParameterImpl (valueChanger, frequencyParameter, i));

            final Parameter gainParameter = eqDevice.createParameter ("GAIN" + index);
            this.gains.add (new ParameterImpl (valueChanger, gainParameter, i));

            final Parameter qsParameter = eqDevice.createParameter ("Q" + index);
            this.qs.add (new ParameterImpl (valueChanger, qsParameter, i));
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getBandCount ()
    {
        return NUMBER_OF_BANDS;
    }


    /** {@inheritDoc} */
    @Override
    public EqualizerBandType getTypeID (final int index)
    {
        final String typeName = this.types.get (index).getDisplayedValue ();
        for (final Entry<String, EqualizerBandType> e: EQ_TYPES.entrySet ())
        {
            if (typeName.startsWith (e.getKey ()))
                return e.getValue ();
        }
        return EqualizerBandType.OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void setType (final int index, final EqualizerBandType typeID)
    {
        this.types.get (index).set (typeID);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getTypeParameter (final int index)
    {
        return this.types.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getFrequencyParameter (final int index)
    {
        return this.frequencies.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getGainParameter (final int index)
    {
        return this.gains.get (index);
    }


    /** {@inheritDoc} */
    @Override
    public IParameter getQParameter (final int index)
    {
        return this.qs.get (index);
    }
}
