// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;

import com.bitwig.extension.controller.api.Arpeggiator;


/**
 * Implementation for a note repeat.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class NoteRepeatImpl implements INoteRepeat
{
    private final Arpeggiator noteRepeat;


    /**
     * Constructor.
     *
     * @param arpeggiator The Bitwig arpeggiator object
     */
    public NoteRepeatImpl (final Arpeggiator arpeggiator)
    {
        this.noteRepeat = arpeggiator;

        this.noteRepeat.isEnabled ().markInterested ();
        this.noteRepeat.rate ().markInterested ();
        this.noteRepeat.gateLength ().markInterested ();
        this.noteRepeat.shuffle ().markInterested ();
        this.noteRepeat.usePressureToVelocity ().markInterested ();
        this.noteRepeat.mode ().markInterested ();
        this.noteRepeat.octaves ().markInterested ();
        this.noteRepeat.isFreeRunning ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        Util.setIsSubscribed (this.noteRepeat.isEnabled (), enable);
        Util.setIsSubscribed (this.noteRepeat.rate (), enable);
        Util.setIsSubscribed (this.noteRepeat.gateLength (), enable);
        Util.setIsSubscribed (this.noteRepeat.shuffle (), enable);
        Util.setIsSubscribed (this.noteRepeat.usePressureToVelocity (), enable);
        Util.setIsSubscribed (this.noteRepeat.mode (), enable);
        Util.setIsSubscribed (this.noteRepeat.octaves (), enable);
        Util.setIsSubscribed (this.noteRepeat.isFreeRunning (), enable);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActive ()
    {
        return this.noteRepeat.isEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleActive ()
    {
        this.noteRepeat.isEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public void setActive (final boolean active)
    {
        this.noteRepeat.isEnabled ().set (active);
    }


    /** {@inheritDoc} */
    @Override
    public void setPeriod (final double length)
    {
        this.noteRepeat.rate ().set (length);
    }


    /** {@inheritDoc} */
    @Override
    public double getPeriod ()
    {
        return this.noteRepeat.rate ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setNoteLength (final double length)
    {
        this.noteRepeat.gateLength ().set (length);
    }


    /** {@inheritDoc} */
    @Override
    public double getNoteLength ()
    {
        return this.noteRepeat.gateLength ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShuffle ()
    {
        return this.noteRepeat.shuffle ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleShuffle ()
    {
        this.noteRepeat.shuffle ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean usePressure ()
    {
        return this.noteRepeat.usePressureToVelocity ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleUsePressure ()
    {
        this.noteRepeat.usePressureToVelocity ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public int getOctaves ()
    {
        return this.noteRepeat.octaves ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setOctaves (final int octaves)
    {
        if (octaves >= 0 && octaves < 9)
            this.noteRepeat.octaves ().set (octaves);
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode getMode ()
    {
        return NoteRepeatModes.lookup (this.noteRepeat.mode ().get ());
    }


    /** {@inheritDoc} */
    @Override
    public ArpeggiatorMode [] getModes ()
    {
        return NoteRepeatModes.getArpeggiatorModes ();
    }


    /** {@inheritDoc} */
    @Override
    public void setMode (final String modeValue)
    {
        this.noteRepeat.mode ().set (modeValue);
    }


    /** {@inheritDoc} */
    @Override
    public void changeMode (final boolean increase)
    {
        final ArpeggiatorMode mode = this.getMode ();
        if (mode == null)
            return;

        final int index = mode.getIndex ();
        final ArpeggiatorMode [] modes = NoteRepeatModes.getArpeggiatorModes ();
        final int newIndex = Math.max (0, Math.min (modes.length - 1, index + (increase ? 1 : -1)));
        this.setMode (modes[newIndex].getValue ());
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFreeRunning ()
    {
        return this.noteRepeat.isFreeRunning ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsFreeRunning ()
    {
        this.noteRepeat.isFreeRunning ().toggle ();
    }
}
