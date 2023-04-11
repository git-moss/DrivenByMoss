// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.parameter.AbstractParameterImpl;

import com.bitwig.extension.controller.api.Track;


/**
 * A parameter encapsulating the tracks cross-fade setting.
 *
 * @author Jürgen Moßgraber
 */
public class CrossfadeParameter extends AbstractParameterImpl
{
    private enum CrossfadeSetting
    {
        A,
        AB,
        B
    }


    private final Track track;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param track The track which crossfade setting to edit
     * @param index The index of the crossfade parameter
     */
    public CrossfadeParameter (final IValueChanger valueChanger, final Track track, final int index)
    {
        super (valueChanger, index);

        this.track = track;
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.track.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return this.track.position ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Crossfade";
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        final double v = this.valueChanger.toNormalizedValue (this.getValue ());
        this.setNormalizedValue (v + (increment > 0 ? 0.5 : -0.5));
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return this.track.crossFadeMode ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        final double v = Math.max (Math.min (value, 1), 0);

        final CrossfadeSetting cs;
        if (v < 0.1)
            cs = CrossfadeSetting.A;
        else if (v > 0.9)
            cs = CrossfadeSetting.B;
        else
            cs = CrossfadeSetting.AB;

        this.track.crossFadeMode ().set (cs.name ());
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        switch (this.track.crossFadeMode ().get ())
        {
            case "A":
                return 0;

            case "B":
                return this.valueChanger.getUpperBound () - 1;

            default:
                return this.valueChanger.getUpperBound () / 2;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.setNormalizedValue (valueChanger.toNormalizedValue (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int control)
    {
        this.inc (valueChanger.isIncrease (control) ? 1 : -1);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setNormalizedValue (0.5);
    }
}
