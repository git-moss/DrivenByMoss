// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.NoteOccurrenceType;
import de.mossgrabers.framework.daw.StepState;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.constants.TransportConstants;
import de.mossgrabers.framework.daw.data.GridStep;
import de.mossgrabers.framework.daw.data.empty.EmptyStepInfo;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;
import com.bitwig.extension.controller.api.NoteOccurrence;
import com.bitwig.extension.controller.api.NoteStep;
import com.bitwig.extension.controller.api.PinnableCursorClip;
import com.bitwig.extension.controller.api.SettableColorValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Proxy to the Bitwig Cursor clip.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class CursorClipImpl implements INoteClip
{
    private final ControllerHost     host;
    private final IValueChanger      valueChanger;
    private final int                numSteps;
    private final int                numRows;

    private final IStepInfo [] [] [] launcherData;
    private final PinnableCursorClip launcherClip;
    private int                      editPage  = 0;
    private double                   stepLength;
    private final List<GridStep>     editSteps = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param host The host
     * @param cursorTrack The cursor track
     * @param valueChanger The value changer
     * @param numSteps The number of steps of the clip to monitor
     * @param numRows The number of note rows of the clip to monitor
     */
    public CursorClipImpl (final ControllerHost host, final CursorTrack cursorTrack, final IValueChanger valueChanger, final int numSteps, final int numRows)
    {
        this.host = host;
        this.valueChanger = valueChanger;

        this.numSteps = numSteps;
        this.numRows = numRows;
        this.stepLength = 1.0 / 4.0; // 16th

        this.launcherData = new IStepInfo [16] [this.numSteps] [];

        // TODO Bugfix required: https://github.com/teotigraphix/Framework4Bitwig/issues/140
        this.launcherClip = cursorTrack.createLauncherCursorClip (this.numSteps, this.numRows);

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
        this.launcherClip.isPinned ().markInterested ();

        this.launcherClip.getTrack ().canHoldNoteData ().markInterested ();
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
        Util.setIsSubscribed (this.launcherClip.isPinned (), enable);

        Util.setIsSubscribed (this.launcherClip.getTrack ().canHoldNoteData (), enable);
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
    public boolean isPinned ()
    {
        return this.launcherClip.isPinned ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void togglePinned ()
    {
        this.launcherClip.isPinned ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setPinned (final boolean isPinned)
    {
        this.launcherClip.isPinned ().set (isPinned);
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
    public void changePlayStart (final int control, final boolean slow)
    {
        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.getClip ().getPlayStart ().inc (increase ? frac : -frac);
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
    public void changePlayEnd (final int control, final boolean slow)
    {
        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.getClip ().getPlayStop ().inc (increase ? frac : -frac);
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
    public void changeLoopStart (final int control, final boolean slow)
    {
        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.getClip ().getLoopStart ().inc (increase ? frac : -frac);
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
    public void changeLoopLength (final int control, final boolean slow)
    {
        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.getClip ().getLoopLength ().inc (increase ? frac : -frac);
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
    public void changeAccent (final int control, final boolean slow)
    {
        final boolean increase = this.valueChanger.isIncrease (control);
        final double frac = slow ? TransportConstants.INC_FRACTION_TIME_SLOW : TransportConstants.INC_FRACTION_TIME;
        this.getClip ().getAccent ().inc (increase ? frac : -frac);
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
    public void changeMuteState (final int channel, final int step, final int row, final int control)
    {
        final boolean increase = this.valueChanger.isIncrease (control);
        this.updateMuteState (channel, step, row, increase);
    }


    /** {@inheritDoc} */
    @Override
    public void updateMuteState (final int channel, final int step, final int row, final boolean isMuted)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setMuted (isMuted);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setIsMuted (isMuted);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepDuration (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final boolean increase = this.valueChanger.isIncrease (control);
        final double res = Resolution.RES_1_32.getValue ();
        this.updateStepDuration (channel, step, row, Math.max (0, info.getDuration () + (increase ? res : -res)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepDuration (final int channel, final int step, final int row, final double duration)
    {
        final double d = Math.max (0, duration);
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setDuration (d);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setDuration (d);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepVelocity (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocity = info.getVelocity () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepVelocity (channel, step, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepVelocity (final int channel, final int step, final int row, final double velocity)
    {
        final double v = Math.min (1.0, Math.max (0, velocity));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setVelocity (v);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setVelocity (v);
    }


    /** {@inheritDoc} */
    @Override
    public void changeVelocitySpread (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocitySpread = info.getVelocitySpread () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateVelocitySpread (channel, step, row, velocitySpread);
    }


    /** {@inheritDoc} */
    @Override
    public void updateVelocitySpread (final int channel, final int step, final int row, final double velocitySpread)
    {
        final double v = Math.min (1.0, Math.max (0, velocitySpread));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setVelocitySpread (v);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setVelocitySpread (v);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepReleaseVelocity (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocity = info.getReleaseVelocity () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepReleaseVelocity (channel, step, row, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepReleaseVelocity (final int channel, final int step, final int row, final double releaseVelocity)
    {
        final double rv = Math.min (1.0, Math.max (0, releaseVelocity));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setReleaseVelocity (rv);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setReleaseVelocity (rv);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPressure (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double pressure = info.getPressure () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepPressure (channel, step, row, pressure);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPressure (final int channel, final int step, final int row, final double pressure)
    {
        final double p = Math.min (1.0, Math.max (0, pressure));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setPressure (p);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setPressure (p);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTimbre (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double timbre = info.getTimbre () + 2.0 * this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepTimbre (channel, step, row, timbre);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTimbre (final int channel, final int step, final int row, final double timbre)
    {
        final double t = Math.min (1.0, Math.max (-1.0, timbre));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setTimbre (t);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setTimbre (t);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepPan (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double pan = info.getPan () + 2.0 * this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepPan (channel, step, row, pan);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepPan (final int channel, final int step, final int row, final double pan)
    {
        final double p = Math.min (1.0, Math.max (-1.0, pan));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setPan (p);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setPan (p);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepTranspose (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double transpose = info.getTranspose () + this.valueChanger.calcSteppedKnobChange (control) / 10.0;
        this.updateStepTranspose (channel, step, row, transpose);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepTranspose (final int channel, final int step, final int row, final double transpose)
    {
        final double t = Math.min (24.0, Math.max (-24.0, transpose));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setTranspose (t);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setTranspose (t);
    }


    /** {@inheritDoc} */
    @Override
    public void changeStepGain (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double gain = info.getGain () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateStepGain (channel, step, row, gain);
    }


    /** {@inheritDoc} */
    @Override
    public void updateStepGain (final int channel, final int step, final int row, final double gain)
    {
        final double g = Math.min (1.0, Math.max (0, gain));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setGain (g);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setGain (g);
    }


    /** {@inheritDoc} */
    @Override
    public void updateIsChanceEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setIsChanceEnabled (isEnabled);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setIsChanceEnabled (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public void changeChance (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double chance = info.getChance () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateChance (channel, step, row, chance);
    }


    /** {@inheritDoc} */
    @Override
    public void updateChance (final int channel, final int step, final int row, final double chance)
    {
        final double c = Math.min (1.0, Math.max (0, chance));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setChance (c);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setChance (c);
    }


    /** {@inheritDoc} */
    @Override
    public void updateIsOccurrenceEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setIsOccurrenceEnabled (isEnabled);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setIsOccurrenceEnabled (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public void setPrevNextOccurrence (final int channel, final int step, final int row, final boolean increase)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        final NoteOccurrenceType occurrenceType = stepInfo.getOccurrence ();
        final List<NoteOccurrenceType> types = Arrays.asList (NoteOccurrenceType.values ());
        final int typeIndex = Math.max (0, types.indexOf (occurrenceType));
        final int newIndex = Math.max (0, Math.min (types.size () - 1, typeIndex + (increase ? 1 : -1)));
        final NoteOccurrenceType newType = types.get (newIndex);
        stepInfo.setOccurrence (newType);
        if (this.editSteps.isEmpty ())
        {
            final NoteOccurrence v = NoteOccurrence.valueOf (newType.name ());
            this.getClip ().getStep (channel, step, row).setOccurrence (v);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setOccurrence (final int channel, final int step, final int row, final NoteOccurrenceType occurrence)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setOccurrence (occurrence);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setOccurrence (NoteOccurrence.valueOf (occurrence.name ()));
    }


    /** {@inheritDoc} */
    @Override
    public void updateIsRecurrenceEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setIsRecurrenceEnabled (isEnabled);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setIsRecurrenceEnabled (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public void changeRecurrenceLength (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final int recurrenceLength = info.getRecurrenceLength () + (this.valueChanger.isIncrease (control) ? 1 : -1);
        this.updateRecurrenceLength (channel, step, row, Math.min (8, Math.max (1, recurrenceLength)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateRecurrenceLength (final int channel, final int step, final int row, final int recurrenceLength)
    {
        final int rl = Math.min (8, Math.max (1, recurrenceLength));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setRecurrenceLength (rl);
        if (this.editSteps.isEmpty ())
        {
            final NoteStep noteStep = this.getClip ().getStep (channel, step, row);
            noteStep.setRecurrence (rl, noteStep.recurrenceMask ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateRecurrenceMask (final int channel, final int step, final int row, final int mask)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setRecurrenceMask (mask);
        if (this.editSteps.isEmpty ())
        {
            final NoteStep noteStep = this.getClip ().getStep (channel, step, row);
            noteStep.setRecurrence (noteStep.recurrenceLength (), mask);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateIsRepeatEnabled (final int channel, final int step, final int row, final boolean isEnabled)
    {
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setIsRepeatEnabled (isEnabled);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setIsRepeatEnabled (isEnabled);
    }


    /** {@inheritDoc} */
    @Override
    public void changeRepeatCount (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final int repeatCount = info.getRepeatCount () + (this.valueChanger.isIncrease (control) ? 1 : -1);
        this.updateRepeatCount (channel, step, row, repeatCount);
    }


    /** {@inheritDoc} */
    @Override
    public void updateRepeatCount (final int channel, final int step, final int row, final int value)
    {
        final int v = Math.min (127, Math.max (-127, value));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setRepeatCount (v);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setRepeatCount (v);
    }


    /** {@inheritDoc} */
    @Override
    public void changeRepeatCurve (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double repeatCurve = info.getRepeatCurve () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateRepeatCurve (channel, step, row, repeatCurve);
    }


    /** {@inheritDoc} */
    @Override
    public void updateRepeatCurve (final int channel, final int step, final int row, final double value)
    {
        final double v = Math.min (1.0, Math.max (-1.0, value));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setRepeatCurve (v);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setRepeatCurve (v);
    }


    /** {@inheritDoc} */
    @Override
    public void changeRepeatVelocityCurve (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocityCurve = info.getRepeatVelocityCurve () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateRepeatVelocityCurve (channel, step, row, velocityCurve);
    }


    /** {@inheritDoc} */
    @Override
    public void updateRepeatVelocityCurve (final int channel, final int step, final int row, final double velocityCurve)
    {
        final double vc = Math.min (1.0, Math.max (-1.0, velocityCurve));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setRepeatVelocityCurve (vc);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setRepeatVelocityCurve (vc);
    }


    /** {@inheritDoc} */
    @Override
    public void changeRepeatVelocityEnd (final int channel, final int step, final int row, final int control)
    {
        final IStepInfo info = this.getStep (channel, step, row);
        final double velocityEnd = info.getRepeatVelocityEnd () + this.valueChanger.toNormalizedValue ((int) this.valueChanger.calcKnobChange (control));
        this.updateRepeatVelocityEnd (channel, step, row, velocityEnd);
    }


    /** {@inheritDoc} */
    @Override
    public void updateRepeatVelocityEnd (final int channel, final int step, final int row, final double velocityEnd)
    {
        final double ve = Math.min (1.0, Math.max (-1.0, velocityEnd));
        final StepInfoImpl stepInfo = this.getUpdateableStep (channel, step, row);
        stepInfo.setRepeatVelocityEnd (ve);
        if (this.editSteps.isEmpty ())
            this.getClip ().getStep (channel, step, row).setRepeatVelocityEnd (ve);
    }


    /** {@inheritDoc} */
    @Override
    public void clearAll ()
    {
        this.getClip ().clearSteps ();
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
            if (data[channel] != null && data[channel][step] != null && data[channel][step][row] != null && data[channel][step][row].getState () != StepState.OFF)
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
        return this.getEditPage () > 0;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canScrollStepsForwards ()
    {
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
        final Clip clip = this.getClip ();
        // Workaround Bitwig crashing when it is not a MIDI clip
        if (clip.getTrack ().canHoldNoteData ().get ())
            clip.transpose (semitones);
    }


    /** {@inheritDoc} */
    @Override
    public void startEdit (final List<GridStep> editSteps)
    {
        // Is there a previous edit, which is not stopped yet?
        this.stopEdit ();

        this.editSteps.addAll (editSteps);
        for (final GridStep step: this.editSteps)
            this.delayedUpdate (step);
    }


    /** {@inheritDoc} */
    @Override
    public void stopEdit ()
    {
        for (final GridStep editStep: this.editSteps)
            this.sendClipData (editStep.channel (), editStep.step (), editStep.note ());
        this.editSteps.clear ();
    }


    private void delayedUpdate (final GridStep editStep)
    {
        if (this.editSteps.isEmpty ())
            return;
        final int channel = editStep.channel ();
        final int step = editStep.step ();
        final int note = editStep.note ();
        this.sendClipData (channel, step, note);
        this.host.scheduleTask ( () -> this.delayedUpdate (new GridStep (channel, step, note)), 100);
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
        noteInfo.setIsMuted (stepInfo.isMuted ());
        noteInfo.setDuration (stepInfo.getDuration ());
        noteInfo.setVelocity (stepInfo.getVelocity ());
        noteInfo.setVelocitySpread (stepInfo.getVelocitySpread ());
        noteInfo.setReleaseVelocity (stepInfo.getReleaseVelocity ());
        noteInfo.setPressure (stepInfo.getPressure ());
        noteInfo.setTimbre (stepInfo.getTimbre ());
        noteInfo.setPan (stepInfo.getPan ());
        noteInfo.setTranspose (stepInfo.getTranspose ());
        noteInfo.setGain (stepInfo.getGain ());

        noteInfo.setIsChanceEnabled (stepInfo.isChanceEnabled ());
        noteInfo.setChance (stepInfo.getChance ());

        noteInfo.setIsOccurrenceEnabled (stepInfo.isOccurrenceEnabled ());
        noteInfo.setOccurrence (NoteOccurrence.valueOf (stepInfo.getOccurrence ().name ()));

        noteInfo.setIsRecurrenceEnabled (stepInfo.isRecurrenceEnabled ());
        final int recurrenceLength = Math.max (1, stepInfo.getRecurrenceLength ());
        noteInfo.setRecurrence (recurrenceLength, stepInfo.getRecurrenceMask ());

        noteInfo.setIsRepeatEnabled (stepInfo.isRepeatEnabled ());
        noteInfo.setRepeatCount (stepInfo.getRepeatCount ());
        noteInfo.setRepeatCurve (stepInfo.getRepeatCurve ());
        noteInfo.setRepeatVelocityCurve (stepInfo.getRepeatVelocityCurve ());
        noteInfo.setRepeatVelocityEnd (stepInfo.getRepeatVelocityEnd ());
    }


    /**
     * Update the step info with the incoming data from Bitwig if the note is not currently edited.
     *
     * @param noteStep The new data
     */
    private void handleStepData (final NoteStep noteStep)
    {
        final int channel = noteStep.channel ();
        final int step = noteStep.x ();
        final int note = noteStep.y ();

        for (final GridStep editStep: this.editSteps)
        {
            // Is the note among the currently edited ones?
            if (editStep.channel () == channel && editStep.step () == step && editStep.note () == note)
                return;
        }

        this.getUpdateableStep (channel, step, note).updateData (noteStep);
    }


    /**
     * Get the step at the given position. If the position still contains the Empty Step Info object
     * an updatable one is created.
     *
     * @param channel The MIDI channel
     * @param step The step
     * @param row The row
     * @return The updatable step info
     */
    private StepInfoImpl getUpdateableStep (final int channel, final int step, final int row)
    {
        final IStepInfo [] [] [] stepInfos = this.getStepInfos ();
        synchronized (stepInfos)
        {
            try
            {
                // Lazily create an updatable object and keep it
                if (stepInfos[channel][step] == null)
                    stepInfos[channel][step] = new IStepInfo [this.numRows];
                if (row >= this.numRows)
                {
                    this.host.errorln ("Requested row (" + row + " is outside of the range of the number of rows (" + this.numRows + ").");
                    return new StepInfoImpl ();
                }
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
        return this.launcherClip;
    }


    /**
     * Get the launcher or arranger step information. Depending on which is active.
     *
     * @return The step information
     */
    private IStepInfo [] [] [] getStepInfos ()
    {
        // Note: Keep this in a function in case the issue with arranger clips gets ever fixed
        return this.launcherData;
    }
}