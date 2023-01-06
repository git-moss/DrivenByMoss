// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.data.EqualizerBandType;

import com.bitwig.extension.controller.api.Parameter;

import java.util.EnumMap;


/**
 * Extends the default parameter handling for extended handling of value reset.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EqTypeParameter extends ParameterImpl
{
    private static final EnumMap<EqualizerBandType, Integer> EQ_TYPE_INDICES = new EnumMap<> (EqualizerBandType.class);
    static
    {
        EQ_TYPE_INDICES.put (EqualizerBandType.OFF, Integer.valueOf (0));
        EQ_TYPE_INDICES.put (EqualizerBandType.LOWCUT, Integer.valueOf (3));
        EQ_TYPE_INDICES.put (EqualizerBandType.LOWSHELF, Integer.valueOf (6));
        EQ_TYPE_INDICES.put (EqualizerBandType.BELL, Integer.valueOf (7));
        EQ_TYPE_INDICES.put (EqualizerBandType.HIGHCUT, Integer.valueOf (10));
        EQ_TYPE_INDICES.put (EqualizerBandType.HIGHSHELF, Integer.valueOf (13));
        EQ_TYPE_INDICES.put (EqualizerBandType.NOTCH, Integer.valueOf (14));
    }


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param parameter The parameter
     * @param index The index of the item in the page
     */
    public EqTypeParameter (final IValueChanger valueChanger, final Parameter parameter, final int index)
    {
        super (valueChanger, parameter, index);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        if (this.index == 0)
            this.set (EqualizerBandType.LOWCUT);
        else if (this.index == 7)
            this.set (EqualizerBandType.HIGHCUT);
        else
            super.resetValue ();
    }


    /**
     * Set the type value.
     *
     * @param typeID The type ID
     */
    public void set (final EqualizerBandType typeID)
    {
        final Integer v = EQ_TYPE_INDICES.get (typeID);
        this.getParameter ().set (v == null ? Integer.valueOf (0) : v, Integer.valueOf (15));
    }
}
