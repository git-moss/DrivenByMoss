// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.daw.ICursorClip;

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.SettableColorValue;

import java.util.Arrays;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipProxy implements ICursorClip
{
    private int             numSteps;
    private int             numRows;

    private final int [] [] data;
    private Clip            clip;
    private ValueChanger    valueChanger;
    private int             editPage = 0;
    private double          stepLength;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param numSteps The number of steps of the clip to monitor
     * @param numRows The number of note rows of the clip to monitor
     */
    public CursorClipProxy (final ControllerHost host, final ValueChanger valueChanger, final int numSteps, final int numRows)
    {
        this.valueChanger = valueChanger;

        this.numSteps = numSteps;
        this.numRows = numRows;
        this.stepLength = 1.0 / 4.0; // 16th
        this.data = new int [this.numSteps] [];

        for (int step = 0; step < this.numSteps; step++)
        {
            this.data[step] = new int [this.numRows];
            Arrays.fill (this.data[step], 0);
        }

        // TODO We need the old method back to monitor both launcher and arranger - otherwise use
        // both and check which one exists!
        this.clip = host.createLauncherCursorClip (this.numSteps, this.numRows);

        this.clip.playingStep ().markInterested ();
        this.clip.addStepDataObserver (this::handleStepData);

        this.clip.getPlayStart ().markInterested ();
        this.clip.getPlayStop ().markInterested ();
        this.clip.getLoopStart ().markInterested ();
        this.clip.getLoopLength ().markInterested ();
        this.clip.isLoopEnabled ().markInterested ();
        this.clip.getShuffle ().markInterested ();
        this.clip.getAccent ().markInterested ();
        this.clip.canScrollStepsBackwards ().markInterested ();
        this.clip.canScrollStepsForwards ().markInterested ();
        this.clip.color ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.clip.playingStep ().setIsSubscribed (enable);
        this.clip.getPlayStart ().setIsSubscribed (enable);
        this.clip.getPlayStop ().setIsSubscribed (enable);
        this.clip.getLoopStart ().setIsSubscribed (enable);
        this.clip.getLoopLength ().setIsSubscribed (enable);
        this.clip.isLoopEnabled ().setIsSubscribed (enable);
        this.clip.getShuffle ().setIsSubscribed (enable);
        this.clip.getAccent ().setIsSubscribed (enable);
        this.clip.color ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final double red, final double green, final double blue)
    {
        this.clip.color ().set ((float) red, (float) green, (float) blue);
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayStart ()
    {
        return this.clip.getPlayStart ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayStart (final double start)
    {
        this.clip.getPlayStart ().set (start);
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayStart (final int control)
    {
        this.clip.getPlayStart ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayEnd ()
    {
        return this.clip.getPlayStop ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayEnd (final double end)
    {
        this.clip.getPlayStop ().set (end);
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayEnd (final int control)
    {
        this.clip.getPlayStop ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayRange (final double start, final double end)
    {
        // Need to distinguish if we move left or right since the start and
        // end cannot be the same value
        if (this.getPlayStart () < start)
        {
            this.setPlayEnd (end);
            this.setPlayStart (start);
        }
        else
        {
            this.setPlayStart (start);
            this.setPlayEnd (end);
        }
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopStart ()
    {
        return this.clip.getLoopStart ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopStart (final double start)
    {
        this.clip.getLoopStart ().set (start);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopStart (final int control)
    {
        this.clip.getLoopStart ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopLength ()
    {
        return this.clip.getLoopLength ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopLength (final int length)
    {
        this.clip.getLoopLength ().set (length);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopLength (final int control)
    {
        this.clip.getLoopLength ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoopEnabled ()
    {
        return this.clip.isLoopEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopEnabled (final boolean enable)
    {
        this.clip.isLoopEnabled ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShuffleEnabled ()
    {
        return this.clip.getShuffle ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setShuffleEnabled (final boolean enable)
    {
        this.clip.getShuffle ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getFormattedAccent ()
    {
        return Math.round (this.getAccent () * 10000) / 100 + "%";
    }


    /** {@inheritDoc} */
    @Override
    public double getAccent ()
    {
        return this.clip.getAccent ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetAccent ()
    {
        this.clip.getAccent ().set (0.5);
    }


    /** {@inheritDoc} */
    @Override
    public void changeAccent (final int control)
    {
        final double speed = this.valueChanger.calcKnobSpeed (control, this.valueChanger.getFractionValue () / 100.0);
        this.clip.getAccent ().inc (speed);
    }


    /** {@inheritDoc} */
    @Override
    public Color getColor ()
    {
        final SettableColorValue color = this.clip.color ();
        return Color.fromRGB (color.red (), color.green (), color.blue ());
    }


    /** {@inheritDoc} */
    @Override
    public int getNumSteps ()
    {
        return this.numSteps;
    }


    /** {@inheritDoc} */
    @Override
    public int getNumRows ()
    {
        return this.numRows;
    }


    /** {@inheritDoc} */
    @Override
    public int getCurrentStep ()
    {
        return this.clip.playingStep ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public int getStep (final int step, final int row)
    {
        if (row < 0)
            return 0;
        return this.data[step][row];
    }


    /** {@inheritDoc} */
    @Override
    public void toggleStep (final int step, final int row, final int velocity)
    {
        this.clip.toggleStep (step, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int step, final int row, final int velocity, final double duration)
    {
        this.clip.setStep (step, row, velocity, duration);
    }


    /** {@inheritDoc} */
    @Override
    public void clearRow (final int row)
    {
        this.clip.clearSteps (row);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRowData (final int row)
    {
        for (int step = 0; step < this.numSteps; step++)
            if (this.data[step][row] > 0)
                return true;
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData ()
    {
        for (int row = 0; row < this.numRows; row++)
            if (this.hasRowData (row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData ()
    {
        for (int row = this.numRows - 1; row >= 0; row--)
            if (this.hasRowData (row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setStepLength (final double length)
    {
        this.stepLength = length;
        this.clip.setStepSize (length);
    }


    /** {@inheritDoc} */
    @Override
    public double getStepLength ()
    {
        return this.stepLength;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollTo (final int step, final int row)
    {
        this.clip.scrollToKey (row);
        this.clip.scrollToStep (step);
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToPage (final int page)
    {
        this.clip.scrollToStep (page * this.numSteps);
        this.editPage = page;
    }


    /** {@inheritDoc} */
    @Override
    public int getEditPage ()
    {
        return this.editPage;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageBackwards ()
    {
        if (this.editPage <= 0)
            return;
        this.clip.scrollStepsPageBackwards ();
        this.editPage--;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageForward ()
    {
        this.clip.scrollStepsPageForward ();
        this.editPage++;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsBackwards ()
    {
        // TODO Bugfix required: this.clip.canScrollStepsBackwards ().get ();
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsForwards ()
    {
        // TODO Bugfix required: this.clip.canScrollStepsForwards ().get ();
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.clip.duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateContent ()
    {
        this.clip.duplicateContent ();
    }


    /** {@inheritDoc} */
    @Override
    public void quantize (final double amount)
    {
        if (amount < 0.000001 || amount > 1)
            return;
        this.clip.quantize (amount);
    }


    /** {@inheritDoc} */
    @Override
    public void transpose (final int semitones)
    {
        this.clip.transpose (semitones);
    }


    private void handleStepData (final int col, final int row, final int state)
    {
        // state: step is empty (0) or a note continues playing (1) or starts playing (2)
        this.data[col][row] = state;
    }
}