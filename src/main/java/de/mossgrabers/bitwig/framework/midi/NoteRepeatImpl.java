// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.midi;

import de.mossgrabers.bitwig.framework.daw.data.Util;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.utils.FrameworkException;

import com.bitwig.extension.controller.api.Arpeggiator;
import com.bitwig.extension.controller.api.EnumDefinition;
import com.bitwig.extension.controller.api.EnumValueDefinition;
import com.bitwig.extension.controller.api.NoteLatch;

import java.util.Locale;


/**
 * Implementation for a note repeat.
 *
 * @author Jürgen Moßgraber
 */
public class NoteRepeatImpl implements INoteRepeat
{
    private final Arpeggiator noteRepeat;
    private final NoteLatch   noteLatch;


    /**
     * Constructor.
     *
     * @param arpeggiator The Bitwig arpeggiator object
     * @param noteLatch The Bitwig note latch object
     */
    public NoteRepeatImpl (final Arpeggiator arpeggiator, final NoteLatch noteLatch)
    {
        this.noteRepeat = arpeggiator;
        this.noteLatch = noteLatch;

        this.noteRepeat.isEnabled ().markInterested ();
        this.noteRepeat.rate ().markInterested ();
        this.noteRepeat.gateLength ().markInterested ();
        this.noteRepeat.shuffle ().markInterested ();
        this.noteRepeat.usePressureToVelocity ().markInterested ();
        this.noteRepeat.mode ().markInterested ();
        this.noteRepeat.octaves ().markInterested ();
        this.noteRepeat.isFreeRunning ().markInterested ();

        this.noteLatch.isEnabled ().markInterested ();

        // Test if all arpeggiator modes are covered by the enumeration
        final EnumDefinition enumDefinition = this.noteRepeat.mode ().enumDefinition ();
        for (int i = 0; i < enumDefinition.getValueCount (); i++)
        {
            final EnumValueDefinition def = enumDefinition.valueDefinitionAt (i);
            final String id = def.getId ();
            if (ArpeggiatorMode.valueOf (id.toUpperCase (Locale.US).replace ('-', '_')) == null)
                throw new FrameworkException ("New (unsupported) arpeggiator mode has been added: " + id);
        }
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
        Util.setIsSubscribed (this.noteLatch.isEnabled (), enable);
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
        final String v = this.noteRepeat.mode ().get ();
        return ArpeggiatorMode.valueOf (v.toUpperCase (Locale.US).replace ('-', '_'));
    }


    /** {@inheritDoc} */
    @Override
    public void setMode (final ArpeggiatorMode mode)
    {
        final String v = mode.name ().toLowerCase (Locale.US).replace ('_', '-');
        this.noteRepeat.mode ().set (v);
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


    /** {@inheritDoc} */
    @Override
    public void toggleLatchActive ()
    {
        this.noteLatch.isEnabled ().toggle ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isLatchActive ()
    {
        return this.noteLatch.isEnabled ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void setLatchActive (final boolean active)
    {
        this.noteLatch.isEnabled ().set (active);
    }
}
