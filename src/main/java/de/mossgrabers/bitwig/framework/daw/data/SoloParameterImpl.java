// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data;

import com.bitwig.extension.controller.api.Channel;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.observer.IValueObserver;
import de.mossgrabers.framework.parameter.AbstractParameterImpl;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Encapsulates the data of a mute parameter.
 *
 * @author Jürgen Moßgraber
 */
public class SoloParameterImpl extends AbstractParameterImpl
{
    private final Channel channel;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param channel The Bitwig channel
     * @param index The index of the item in the page
     */
    public SoloParameterImpl (final IValueChanger valueChanger, final Channel channel, final int index)
    {
        super (valueChanger, index);

        this.channel = channel;

        channel.solo ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        super.enableObservers (enable);

        Util.setIsSubscribed (this.channel.solo (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.channel.exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Solo";
    }


    /** {@inheritDoc} */
    @Override
    public String getName (final int limit)
    {
        return StringUtils.limit (this.getName (), limit);
    }


    /** {@inheritDoc} */
    @Override
    public void addNameObserver (final IValueObserver<String> observer)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        return this.valueChanger.fromNormalizedValue (this.channel.solo ().get () ? 1 : 0);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        this.channel.solo ().set (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        this.channel.solo ().set (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.channel.solo ().set (valueChanger.isIncrease (value));
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.channel.solo ().set (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        this.channel.solo ().set (increment > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.channel.solo ().set (false);
    }


    /** {@inheritDoc} */
    @Override
    public void touchValue (final boolean isBeingTouched)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedValue ()
    {
        // Not supported
        return this.getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void setIndication (final boolean enable)
    {
        // Not supported
    }


    /** {@inheritDoc} */
    @Override
    public int getNumberOfSteps ()
    {
        return 2;
    }
}
