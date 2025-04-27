// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.parameter;

import java.util.Optional;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * A parameter implementation for soloing the selected track.
 *
 * @author Jürgen Moßgraber
 */
public class SoloParameter extends AbstractParameterImpl
{
    private final IModel model;


    /**
     * Constructor.
     *
     * @param model The model
     */
    public SoloParameter (final IModel model)
    {
        super (model.getValueChanger (), 0);

        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    public int getValue ()
    {
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        return selectedTrack.isPresent () && selectedTrack.get ().isSolo () ? this.valueChanger.getUpperBound () - 1 : 0;
    }


    /** {@inheritDoc} */
    @Override
    public void setValue (final IValueChanger valueChanger, final int value)
    {
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
            selectedTrack.get ().setSolo (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void setNormalizedValue (final double value)
    {
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
            selectedTrack.get ().setSolo (value > 0);
    }


    /** {@inheritDoc} */
    @Override
    public void changeValue (final IValueChanger valueChanger, final int value)
    {
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
            selectedTrack.get ().setSolo (valueChanger.isIncrease (value));
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
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
            selectedTrack.get ().setSolo (increment > 0);
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
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        return selectedTrack.isPresent () && selectedTrack.get ().isSolo () ? "Solod" : "-";
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.model.getTrackBank ().getSelectedItem ().isPresent ();
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "Track Solo";
    }


    /** {@inheritDoc} */
    @Override
    public int getNumberOfSteps ()
    {
        return 2;
    }
}
