// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IApplication;


/**
 * A parameter implementation for zooming in the arranger.
 *
 * @author Jürgen Moßgraber
 */
public class ZoomParameter extends AbstractParameterImpl
{
    protected final IApplication application;
    protected final boolean      isHorizontal;


    /**
     * Constructor.
     *
     * @param valueChanger The value changer
     * @param application The application
     * @param isHorizontal True to zoom horizontally otherwise vertical (track height)
     */
    public ZoomParameter (final IValueChanger valueChanger, final IApplication application, final boolean isHorizontal)
    {
        super (valueChanger, 0);

        this.application = application;
        this.isHorizontal = isHorizontal;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        throw new UnsupportedOperationException ();
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        this.inc (valueChanger.isIncrease (value) ? 1 : -1);
    }


    /** {@inheritDoc} */
    @Override
    public void setValueImmediatly (final int value)
    {
        this.setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public void inc (final double increment)
    {
        if (this.isHorizontal)
        {
            if (increment > 0)
                this.application.zoomIn ();
            else
                this.application.zoomOut ();
            return;
        }

        if (increment > 0)
            this.application.incTrackHeight ();
        else
            this.application.decTrackHeight ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetValue ()
    {
        this.setValue (0);
    }


    /** {@inheritDoc} */
    @Override
    public String getDisplayedValue ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Zoom In/Out";
    }
}
