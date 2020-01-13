// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.empty.EmptyStepInfo;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.NoteStep;
import com.bitwig.extension.controller.api.SettableColorValue;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipImpl implements INoteClip
{
    private final ControllerHost     host;
    private IValueChanger            valueChanger;
    private int                      numSteps;
    private int                      numRows;

    private final IStepInfo [] [] [] launcherData;
    private final IStepInfo [] [] [] arrangerData;
    private Clip                     launcherClip;
    private Clip                     arrangerClip;
    private int                      editPage = 0;
    private double                   stepLength;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param numSteps The number of steps of the clip to monitor
     * @param numRows The number of note rows of the clip to monitor
     */
    public CursorClipImpl (final ControllerHost host, final IValueChanger valueChanger, final int numSteps, final int numRows)
    {
        this.host = host;
        this.valueChanger = valueChanger;

        this.numSteps = numSteps;
        this.numRows = numRows;
        this.stepLength = 1.0 / 4.0; // 16th

        this.launcherData = new IStepInfo [16] [this.numSteps] [];
        this.arrangerData = new IStepInfo [16] [this.numSteps] [];

        // TODO Bugfix required: https://github.com/teotigraphix/Framework4Bitwig/issues/140
        this.launcherClip = host.createLauncherCursorClip (this.numSteps, this.numRows);

        this.launcherClip.addNoteStepObserver (this::handleStepData);

        this.launcherClip.exists ().markInterested ();
        this.launcherClip.playingStep ().markInterested ();
        this.launcherClip.getPlayStart ().markInterested ();
        this.launcherClip.getPlayStop ().markInterested ();
        this.launcherClip.getLoopStart ().markInterested ();
        this.launcherClip.getLoopLength ().markInterested ();
        this.launcherClip.isLoopEnabled ().markInterested ();
        this.launcherClip.getShuffle ().markInterested ();
        this.launcherClip.getAccent ().markInterested ();
        this.launcherClip.canScrollStepsBackwards ().markInterested ();
        this.launcherClip.canScrollStepsForwards ().markInterested ();
        this.launcherClip.color ().markInterested ();

        this.arrangerClip = host.createLauncherCursorClip (this.numSteps, this.numRows);

        this.arrangerClip.addNoteStepObserver (this::handleStepData);

        this.arrangerClip.exists ().markInterested ();
        this.arrangerClip.playingStep ().markInterested ();
        this.arrangerClip.getPlayStart ().markInterested ();
        this.arrangerClip.getPlayStop ().markInterested ();
        this.arrangerClip.getLoopStart ().markInterested ();
        this.arrangerClip.getLoopLength ().markInterested ();
        this.arrangerClip.isLoopEnabled ().markInterested ();
        this.arrangerClip.getShuffle ().markInterested ();
        this.arrangerClip.getAccent ().markInterested ();
        this.arrangerClip.canScrollStepsBackwards ().markInterested ();
        this.arrangerClip.canScrollStepsForwards ().markInterested ();
        this.arrangerClip.color ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.launcherClip.exists (), enable);
        Util.setIsSubscribed (this.launcherClip.playingStep (), enable);
        Util.setIsSubscribed (this.launcherClip.getPlayStart (), enable);
        Util.setIsSubscribed (this.launcherClip.getPlayStop (), enable);
        Util.setIsSubscribed (this.launcherClip.getLoopStart (), enable);
        Util.setIsSubscribed (this.launcherClip.getLoopLength (), enable);
        Util.setIsSubscribed (this.launcherClip.isLoopEnabled (), enable);
        Util.setIsSubscribed (this.launcherClip.getShuffle (), enable);
        Util.setIsSubscribed (this.launcherClip.getAccent (), enable);
        Util.setIsSubscribed (this.launcherClip.canScrollStepsBackwards (), enable);
        Util.setIsSubscribed (this.launcherClip.canScrollStepsForwards (), enable);
        Util.setIsSubscribed (this.launcherClip.color (), enable);

        Util.setIsSubscribed (this.arrangerClip.exists (), enable);
        Util.setIsSubscribed (this.arrangerClip.playingStep (), enable);
        Util.setIsSubscribed (this.arrangerClip.getPlayStart (), enable);
        Util.setIsSubscribed (this.arrangerClip.getPlayStop (), enable);
        Util.setIsSubscribed (this.arrangerClip.getLoopStart (), enable);
        Util.setIsSubscribed (this.arrangerClip.getLoopLength (), enable);
        Util.setIsSubscribed (this.arrangerClip.isLoopEnabled (), enable);
        Util.setIsSubscribed (this.arrangerClip.getShuffle (), enable);
        Util.setIsSubscribed (this.arrangerClip.getAccent (), enable);
        Util.setIsSubscribed (this.arrangerClip.canScrollStepsBackwards (), enable);
        Util.setIsSubscribed (this.arrangerClip.canScrollStepsForwards (), enable);
        Util.setIsSubscribed (this.arrangerClip.color (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return this.getClip ().exists ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (final ColorEx color)
    {
        this.getClip ().color ().set ((float) color.getRed (), (float) color.getGreen (), (float) color.getBlue ());
    }


    /** {@inheritDoc} */
    @Override
    public ColorEx getColor ()
    {
        final SettableColorValue color = this.getClip ().color ();
        return new ColorEx (color.red (), color.green (), color.blue ());
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayStart ()
    {
        return this.getClip ().getPlayStart ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayStart (final double start)
    {
        this.getClip ().getPlayStart ().set (start);
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayStart (final int control)
    {
        this.getClip ().getPlayStart ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public double getPlayEnd ()
    {
        return this.getClip ().getPlayStop ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPlayEnd (final double end)
    {
        this.getClip ().getPlayStop ().set (end);
    }


    /** {@inheritDoc} */
    @Override
    public void changePlayEnd (final int control)
    {
        this.getClip ().getPlayStop ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
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
        return this.getClip ().getLoopStart ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopStart (final double start)
    {
        this.getClip ().getLoopStart ().set (start);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopStart (final int control)
    {
        this.getClip ().getLoopStart ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public double getLoopLength ()
    {
        return this.getClip ().getLoopLength ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopLength (final int length)
    {
        this.getClip ().getLoopLength ().set (length);
    }


    /** {@inheritDoc} */
    @Override
    public void changeLoopLength (final int control)
    {
        this.getClip ().getLoopLength ().inc (this.valueChanger.calcKnobSpeed (control, this.valueChanger.isSlow () ? 0.1 : 1));
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLoopEnabled ()
    {
        return this.getClip ().isLoopEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLoopEnabled (final boolean enable)
    {
        this.getClip ().isLoopEnabled ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShuffleEnabled ()
    {
        return this.getClip ().getShuffle ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setShuffleEnabled (final boolean enable)
    {
        this.getClip ().getShuffle ().set (enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getFormattedAccent ()
    {
        return Math.round (this.getAccent () * 200 - 100) + "%";
    }


    /** {@inheritDoc} */
    @Override
    public double getAccent ()
    {
        return this.getClip ().getAccent ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void resetAccent ()
    {
        this.getClip ().getAccent ().setImmediately (0.5);
    }


    /** {@inheritDoc} */
    @Override
    public void changeAccent (final int control)
    {
        final double speed = this.valueChanger.calcKnobSpeed (control, this.valueChanger.getFractionValue () / 100.0);
        this.getClip ().getAccent ().inc (speed);
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
        return this.getClip ().playingStep ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setStepLength (final double length)
    {
        this.stepLength = length;
        this.launcherClip.setStepSize (length);
        this.arrangerClip.setStepSize (length);
    }


    /** {@inheritDoc} */
    @Override
    public double getStepLength ()
    {
        return this.stepLength;
    }


    /** {@inheritDoc} */
    @Override
    public IStepInfo getStep (final int channel, final int step, final int row)
    {
        final IStepInfo [] [] [] stepInfos = this.getStepInfos ();
        try
        {
            if (stepInfos[channel][step] == null || stepInfos[channel][step][row] == null)
                return EmptyStepInfo.INSTANCE;
            return stepInfos[channel][step][row];
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            this.host.errorln (ex.getLocalizedMessage ());
            return EmptyStepInfo.INSTANCE;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void toggleStep (final int channel, final int step, final int row, final int velocity)
    {
        this.getClip ().toggleStep (channel, step, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int channel, final int step, final int row, final int velocity, final double duration)
    {
        this.getClip ().setStep (channel, step, row, velocity, duration);
    }


    /** {@inheritDoc} */
    @Override
    public void setStep (final int channel, final int step, final int row, final IStepInfo noteStep)
    {
        this.setStep (channel, step, row, (int) (noteStep.getVelocity () * 127), 0.25);
        this.host.scheduleTask ( () -> {

            this.updateStepVelocity (channel, step, row, noteStep.getVelocity ());
            this.updateStepDuration (channel, step, row, noteStep.getDuration ());
            this.updateStepGain (channel, step, row, noteStep.getGain ());
            this.updateStepPan (channel, step, row, noteStep.getPan ());
            this.updateStepPressure (channel, step, row, noteStep.getPressure ());
            this.updateStepReleaseVelocity (channel, step, row, noteStep.getReleaseVelocity ());
            this.updateStepTimbre (channel, step, row, noteStep.getTimbre ());
            this.updateStepTranspose (channel, step, row, noteStep.getTranspose ());

        }, 100);
    }


    /** {@inheritDoc} */
    @Override
    public void clearStep (final int channel, final int step, final int row)
    {
        this.getClip ().clearStep (channel, step, row);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepDuration (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double frac = this.valueChanger.isSlow () ? TransportConstants.INC_FRACTION_TIME_SLOW / 16.0 : TransportConstants.INC_FRACTION_TIME_SLOW;
        this.updateStepDuration (channel, step, row, Math.max (0, info.getDuration () + this.valueChanger.calcKnobSpeed (control, frac)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepDuration (final int channel, final int step, final int row, final double duration)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setDuration (duration);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setDuration (duration);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepVelocity (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocity = info.getVelocity () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobSpeed (control));
        this.updateStepVelocity (channel, step, row, Math.min (1.0, Math.max (0, velocity)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepVelocity (final int channel, final int step, final int row, final double velocity)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setVelocity (velocity);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setVelocity (velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepReleaseVelocity (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocity = info.getReleaseVelocity () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobSpeed (control));
        this.updateStepReleaseVelocity (channel, step, row, Math.min (1.0, Math.max (0, velocity)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepReleaseVelocity (final int channel, final int step, final int row, final double releaseVelocity)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setReleaseVelocity (releaseVelocity);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setReleaseVelocity (releaseVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPressure (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double pressure = info.getPressure () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobSpeed (control));
        this.updateStepPressure (channel, step, row, Math.min (1.0, Math.max (0, pressure)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPressure (final int channel, final int step, final int row, final double pressure)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setPressure (pressure);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setPressure (pressure);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTimbre (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double timbre = info.getTimbre () + 2.0 * this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobSpeed (control));
        this.updateStepTimbre (channel, step, row, Math.min (1.0, Math.max (-1.0, timbre)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTimbre (final int channel, final int step, final int row, final double timbre)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setTimbre (timbre);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setTimbre (timbre);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPan (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double pan = info.getPan () + 2.0 * this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobSpeed (control));
        this.updateStepPan (channel, step, row, Math.min (1.0, Math.max (-1.0, pan)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPan (final int channel, final int step, final int row, final double pan)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setPan (pan);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setPan (pan);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTranspose (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double transpose = info.getTranspose () + this.valueChanger.calcKnobSpeed (control) / 8.0;
        this.updateStepTranspose (channel, step, row, Math.min (24.0, Math.max (-24.0, transpose)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTranspose (final int channel, final int step, final int row, final double transpose)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setTranspose (transpose);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setTranspose (transpose);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepGain (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double gain = info.getGain () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobSpeed (control));
        this.updateStepGain (channel, step, row, Math.min (1.0, Math.max (0, gain)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepGain (final int channel, final int step, final int row, final double gain)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setGain (gain);
        if (!stepInfo.isEditing ())
            this.getClip ().getStep (channel, step, row).setGain (gain);
    }


    /** {@inheritDoc} */
    @Override
    public void clearRow (final int channel, final int row)
    {
        this.getClip ().clearStepsAtY (channel, row);
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasRowData (final int channel, final int row)
    {
        final IStepInfo [] [] [] data = this.getStepInfos ();
        for (int step = 0; step < this.numSteps; step++)
        {
            if (data[channel] != null && data[channel][step] != null && data[channel][step][row] != null && data[channel][step][row].getState () > 0)
                return true;
        }
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData ()
    {
        int min = 128;
        for (int channel = 0; channel < 16; channel++)
        {
            final int lower = this.getLowerRowWithData (channel);
            if (lower >= 0 && lower < min)
                min = lower;
        }
        return min == 128 ? -1 : min;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData ()
    {
        int max = -1;
        for (int channel = 0; channel < 16; channel++)
        {
            final int upper = this.getUpperRowWithData (channel);
            if (upper >= 0 && upper > max)
                max = upper;
        }
        return max;
    }


    /** {@inheritDoc} */
    @Override
    public int getLowerRowWithData (final int channel)
    {
        for (int row = 0; row < this.numRows; row++)
            if (this.hasRowData (channel, row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public int getUpperRowWithData (final int channel)
    {
        for (int row = this.numRows - 1; row >= 0; row--)
            if (this.hasRowData (channel, row))
                return row;
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToPage (final int page)
    {
        this.getClip ().scrollToStep (page * this.numSteps);
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
        this.getClip ().scrollStepsPageBackwards ();
        this.editPage--;
    }


    /** {@inheritDoc} */
    @Override
    public void scrollStepsPageForward ()
    {
        this.getClip ().scrollStepsPageForward ();
        this.editPage++;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsBackwards ()
    {
        // TODO Bugfix required: https://github.com/teotigraphix/Framework4Bitwig/issues/217
        // return this.getClip ().canScrollStepsBackwards ().get ();
        return this.getEditPage () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsForwards ()
    {
        // TODO Bugfix required: https://github.com/teotigraphix/Framework4Bitwig/issues/217
        // return this.getClip ().canScrollStepsForwards ().get ();
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public void duplicate ()
    {
        this.getClip ().duplicate ();
    }


    /** {@inheritDoc} */
    @Override
    public void duplicateContent ()
    {
        this.getClip ().duplicateContent ();
    }


    /** {@inheritDoc} */
    @Override
    public void quantize (final double amount)
    {
        if (amount < 0.000001 || amount > 1)
            return;
        this.getClip ().quantize (amount);
    }


    /** {@inheritDoc} */
    @Override
    public void transpose (final int semitones)
    {
        this.getClip ().transpose (semitones);
    }


    /** {@inheritDoc} */
    @Override
    public void edit (final int channel, final int step, final int row, final boolean enable)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        if (enable)
        {
            stepInfo.setEditing (true);
            this.delayedUpdate (channel, step, row);
            return;
        }

        this.sendClipData (channel, step, row);
        stepInfo.setEditing (false);
    }


    private void delayedUpdate (final int channel, final int step, final int row)
    {
        final IStepInfo stepInfo = this.getStep (channel, step, row);
        if (!stepInfo.isEditing ())
            return;
        this.sendClipData (channel, step, row);
        this.host.scheduleTask ( () -> this.delayedUpdate (channel, step, row), 100);
    }


    /**
     * Update the locally changed step data in Bitwig.
     *
     * @param channel The MIDI channel
     * @param step The step of the clip
     * @param row The row of the clip
     */
    private void sendClipData (final int channel, final int step, final int row)
    {
        final NoteStep noteInfo = this.getClip ().getStep (channel, step, row);
        if (noteInfo == null)
            return;

        final IStepInfo stepInfo = this.getStep (channel, step, row);
        noteInfo.setDuration (stepInfo.getDuration ());
        noteInfo.setVelocity (stepInfo.getVelocity ());
        noteInfo.setReleaseVelocity (stepInfo.getReleaseVelocity ());
        noteInfo.setPressure (stepInfo.getPressure ());
        noteInfo.setTimbre (stepInfo.getTimbre ());
        noteInfo.setPan (stepInfo.getPan ());
        noteInfo.setTranspose (stepInfo.getTranspose ());
        noteInfo.setGain (stepInfo.getGain ());
    }


    /**
     * Update the step info with the incoming data from Bitwig if the note is not currently edited.
     *
     * @param noteStep The new data
     */
    private void handleStepData (final NoteStep noteStep)
    {
        final StepInfoImpl sinfo = this.getUpdateableStep (noteStep.channel (), noteStep.x (), noteStep.y ());
        if (!sinfo.isEditing ())
            sinfo.updateData (noteStep);
    }


    /**
     * Get the step at the given position. If the position still contains the Empty Step Info object
     * an updateable one is created.
     *
     * @param channel The midi channel
     * @param step The step
     * @param row The row
     * @return The updateable step info
     */
    private StepInfoImpl getUpdateableStep (final int channel, final int step, final int row)
    {
        final IStepInfo [] [] [] stepInfos = this.getStepInfos ();
        synchronized (stepInfos)
        {
            try
            {
                // Lazily create an updateable object and keep it
                if (stepInfos[channel][step] == null)
                    stepInfos[channel][step] = new IStepInfo [this.numRows];
                if (stepInfos[channel][step][row] == null)
                    stepInfos[channel][step][row] = new StepInfoImpl ();
                return (StepInfoImpl) stepInfos[channel][step][row];
            }
            catch (final ArrayIndexOutOfBoundsException ex)
            {
                this.host.errorln (ex.getLocalizedMessage ());
                return new StepInfoImpl ();
            }
        }
    }


    /**
     * Get the launcher or arranger clip. Depending on which is active.
     *
     * @return The clip
     */
    private Clip getClip ()
    {
        return this.launcherClip.exists ().get () ? this.launcherClip : this.arrangerClip;
    }


    /**
     * Get the launcher or arranger step infos. Depending on which is active.
     *
     * @return The step infos
     */
    private IStepInfo [] [] [] getStepInfos ()
    {
        return this.launcherClip.exists ().get () ? this.launcherData : this.arrangerData;
    }
}