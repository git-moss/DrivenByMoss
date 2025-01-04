// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import com.bitwig.extension.controller.api.SettableRangedValue;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;


/**
 * A parameter for the metronome volume.
 *
 * @author Jürgen Moßgraber
 */
public class MetronomeVolumeParameterImpl extends RangedValueImpl
{
    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param rangedValue The ranged value
     */
    public MetronomeVolumeParameterImpl (final IValueChanger valueChanger, final SettableRangedValue rangedValue)
    {
        super ("Metronome Volume", valueChanger, rangedValue, 0);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setNormalizedValue (0.8);
    }
}
