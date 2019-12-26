// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.scale;

import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.scale.ScaleGrid.Orientation;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;


/**
 * Helper class for applying scales to a row x column pad grid. There are different layouts
 * available incl. transpositions by octaves. Furthermore, specific matrices are supported for a
 * drum, a piano and a sequencer layout.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Scales
{
    private static final int            DRUM_NOTE_LOWER          = 4;
    private static final int            DRUM_NOTE_UPPER          = 100;
    private static final int            DRUM_DEFAULT_OFFSET      = 16;

    /** The names of notes. */
    private static final String []      NOTE_NAMES               =
    {
        "C",
        "Db",
        "D",
        "Eb",
        "E",
        "F",
        "Gb",
        "G",
        "Ab",
        "A",
        "Bb",
        "B"
    };

    /** The names of the base notes. */
    public static final String []       BASES                    =
    {
        "C",
        "G",
        "D",
        "A",
        "E",
        "B",
        "F",
        "Bb",
        "Eb",
        "Ab",
        "Db",
        "Gb"
    };

    /** The semitone offsets of the base notes. */
    private static final int []         OFFSETS                  =
    {
        0,
        7,
        2,
        9,
        4,
        11,
        5,
        10,
        3,
        8,
        1,
        6
    };

    /** The midi note at which the drum grid starts. */
    public static final int             DRUM_NOTE_START          = 36;
    /** The midi note at which the drum grid ends. */
    public static final int             DRUM_NOTE_END            = 100;

    // @formatter:off
    /** The drum grid matrix. */
    private static final int []         DRUM_MATRIX              =
    {
         0,  1,  2,  3, -1, -1, -1, -1,
         4,  5,  6,  7, -1, -1, -1, -1,
         8,  9, 10, 11, -1, -1, -1, -1,
        12, 13, 14, 15, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1
    };

    /** The matrix for the piano layout. */
    private static final int []         PIANO_MATRIX             =
    {
         0,  2,  4,  5,  7,  9, 11, 12,
        -1,  1,  3, -1,  6,  8, 10, -1,
        12, 14, 16, 17, 19, 21, 23, 24,
        -1, 13, 15, -1, 18, 20, 22, -1,
        24, 26, 28, 29, 31, 33, 35, 36,
        -1, 25, 27, -1, 30, 32, 34, -1,
        36, 38, 40, 41, 43, 45, 47, 48,
        -1, 37, 39, -1, 42, 44, 46, -1
    };
    // @formatter:on

    /** Color index when the pad is turned off. */
    public static final String          SCALE_COLOR_OFF          = "SCALE_COLOR_OFF";
    /** Color index when the pad is the base note of the scale. */
    public static final String          SCALE_COLOR_OCTAVE       = "SCALE_COLOR_OCTAVE";
    /** Color index when the pad is a note of the scale. */
    public static final String          SCALE_COLOR_NOTE         = "SCALE_COLOR_NOTE";
    /** Color index when the pad is a note which is not part of the scale. */
    public static final String          SCALE_COLOR_OUT_OF_SCALE = "SCALE_COLOR_OUT_OF_SCALE";

    /** Number of octaves which can be transposed up and down. */
    public static final int             OCTAVE_RANGE             = 4;
    /** Number of octaves which can be transposed up and down in the piano layout. */
    public static final int             PIANO_OCTAVE_RANGE       = 3;
    /** Number of upper drum octave limit. */
    public static final int             DRUM_OCTAVE_UPPER        = 4;
    /** Number of lower drum octave limit. */
    public static final int             DRUM_OCTAVE_LOWER        = -2;

    private Scale                       selectedScale            = Scale.MAJOR;
    private int                         scaleOffset              = 0;                          // C
    private ScaleLayout                 scaleLayout              = ScaleLayout.FOURTH_UP;
    private Orientation                 orientation              = Orientation.ORIENT_UP;
    private boolean                     chromaticOn              = false;
    private int                         shift                    = 3;
    private int                         octave                   = 0;
    private int                         drumOffset;
    private int                         drumDefaultOffset;
    private int                         pianoOctave              = 0;
    private int                         startNote;
    private int                         endNote;
    private int                         numColumns;
    private int                         numRows;

    private int []                      drumMatrix               = DRUM_MATRIX;
    private int                         drumNoteStart            = DRUM_NOTE_START;
    private int                         drumNoteEnd              = DRUM_NOTE_END;

    private final Map<Scale, ScaleGrid> scaleGrids               = new EnumMap<> (Scale.class);
    private IValueChanger               valueChanger;


    /**
     * Constructor.
     *
     * @param valueChanger A value changer
     * @param startNote The first midi note of the pad grid
     * @param endNote The last midi note of the pad grid
     * @param numColumns The number of columns of the pad grid
     * @param numRows The number of rows of the pad grid
     */
    public Scales (final IValueChanger valueChanger, final int startNote, final int endNote, final int numColumns, final int numRows)
    {
        this.valueChanger = valueChanger;
        this.startNote = startNote;
        this.endNote = endNote; // last note + 1
        this.numColumns = numColumns;
        this.numRows = numRows;

        this.drumOffset = this.drumNoteStart;
        this.drumDefaultOffset = DRUM_DEFAULT_OFFSET;

        this.generateMatrices ();
    }


    /**
     * Get the currently selected scale.
     *
     * @return The scale
     */
    public Scale getScale ()
    {
        return this.selectedScale;
    }


    /**
     * Set the scale.
     *
     * @param scale The scale to set
     */
    public void setScale (final Scale scale)
    {
        this.selectedScale = scale;
    }


    /**
     * Set the scale by its name.
     *
     * @param scaleName The name of the scale to set
     */
    public void setScaleByName (final String scaleName)
    {
        this.selectedScale = Scale.getByName (scaleName);
    }


    /**
     * Change the scale value.
     *
     * @param control The control value
     */
    public void changeScale (final int control)
    {
        final Scale [] values = Scale.values ();
        final int index = this.valueChanger.changeValue (control, this.selectedScale.ordinal (), 1, values.length);
        this.selectedScale = values[index];
    }


    /**
     * Returns true if there is a previous scale to select.
     *
     * @return True if there is a previous scale to select
     */
    public boolean hasPrevScale ()
    {
        return this.selectedScale.ordinal () > 0;
    }


    /**
     * Returns true if there is a next scale to select.
     *
     * @return True if there is a next scale to select
     */
    public boolean hasNextScale ()
    {
        return this.selectedScale.ordinal () < Scale.values ().length - 1;
    }


    /**
     * Select the previous scale.
     */
    public void prevScale ()
    {
        final Scale [] values = Scale.values ();
        this.selectedScale = values[Math.max (0, this.selectedScale.ordinal () - 1)];
    }


    /**
     * Select the next scale.
     */
    public void nextScale ()
    {
        final Scale [] values = Scale.values ();
        this.selectedScale = values[Math.min (values.length - 1, this.selectedScale.ordinal () + 1)];
    }


    /**
     * Returns true if there is a previous scale offset to select.
     *
     * @return True if there is a previous scale offset to select
     */
    public boolean hasPrevScaleOffset ()
    {
        return this.scaleOffset > 0;
    }


    /**
     * Returns true if there is a next scale offset to select.
     *
     * @return True if there is a next scale offset to select
     */
    public boolean hasNextScaleOffset ()
    {
        return this.scaleOffset < Scales.OFFSETS.length - 1;
    }


    /**
     * Select the previous scale offset.
     */
    public void prevScaleOffset ()
    {
        this.setScaleOffset (this.scaleOffset - 1);
    }


    /**
     * Select the next scale offset.
     */
    public void nextScaleOffset ()
    {
        this.setScaleOffset (this.scaleOffset + 1);
    }


    /**
     * Get the base note (offset) to use for the current scale.
     *
     * @return The offset
     */
    public int getScaleOffset ()
    {
        return this.scaleOffset;
    }


    /**
     * Set the base note (offset) to use for the current scale.
     *
     * @param scaleOffset The offset
     */
    public void setScaleOffset (final int scaleOffset)
    {
        this.scaleOffset = Math.max (0, Math.min (scaleOffset, Scales.OFFSETS.length - 1));
    }


    /**
     * Change the scale offset value.
     *
     * @param control The control value
     */
    public void changeScaleOffset (final int control)
    {
        this.scaleOffset = this.valueChanger.changeValue (control, this.scaleOffset, 1, Scales.OFFSETS.length);
    }


    /**
     * Set the base note (offset) to use for the current scale by its name.
     *
     * @param scaleOffsetName The offsets name (e.g. 'G')
     */
    public void setScaleOffsetByName (final String scaleOffsetName)
    {
        for (int i = 0; i < Scales.BASES.length; i++)
        {
            if (Scales.BASES[i].equals (scaleOffsetName))
            {
                this.scaleOffset = i;
                break;
            }
        }
    }


    /**
     * Get the scale layout.
     *
     * @return The scale layout
     */
    public ScaleLayout getScaleLayout ()
    {
        return this.scaleLayout;
    }


    /**
     * Set the scale layout.
     *
     * @param scaleLayout The scale layout
     */
    public void setScaleLayout (final ScaleLayout scaleLayout)
    {
        this.scaleLayout = scaleLayout;
        this.orientation = this.scaleLayout.ordinal () % 2 == 0 ? Orientation.ORIENT_UP : Orientation.ORIENT_RIGHT;
        switch (this.scaleLayout)
        {
            case FOURTH_UP:
            case FOURTH_RIGHT:
                this.setPlayShift (3);
                break;
            case THIRD_UP:
            case THIRD_RIGHT:
                this.setPlayShift (2);
                break;
            case SEQUENT_UP:
            case SEQUENT_RIGHT:
                this.setPlayShift (8);
                break;
            case EIGHT_UP:
            case EIGHT_RIGHT:
            case EIGHT_UP_CENTER:
            case EIGHT_RIGHT_CENTER:
                this.setPlayShift (7);
                break;
        }
    }


    /**
     * Set the scale layout by its name.
     *
     * @param scaleLayoutName The name of the layout
     */
    public void setScaleLayoutByName (final String scaleLayoutName)
    {
        this.setScaleLayout (ScaleLayout.getByName (scaleLayoutName));
    }


    /**
     * Select the previous scale layout.
     */
    public void prevScaleLayout ()
    {
        final ScaleLayout [] values = ScaleLayout.values ();
        this.scaleLayout = values[Math.max (0, this.scaleLayout.ordinal () - 1)];
    }


    /**
     * Select the next scale layout.
     */
    public void nextScaleLayout ()
    {
        final ScaleLayout [] values = ScaleLayout.values ();
        this.scaleLayout = values[Math.min (values.length - 1, this.scaleLayout.ordinal () + 1)];
    }


    /**
     * DIs-/enable chromatic mode.
     *
     * @param enable True to enable
     */
    public void setChromatic (final boolean enable)
    {
        this.chromaticOn = enable;
    }


    /**
     * Toggle the chromatic setting.
     */
    public void toggleChromatic ()
    {
        this.chromaticOn = !this.chromaticOn;
    }


    /**
     * True if chromatic.
     *
     * @return The chromatic setting
     */
    public boolean isChromatic ()
    {
        return this.chromaticOn;
    }


    /**
     * Sets the octave offset.
     *
     * @param octave The octave
     */
    public void setOctave (final int octave)
    {
        this.octave = Math.max (-Scales.OCTAVE_RANGE, Math.min (octave, Scales.OCTAVE_RANGE));
    }


    /**
     * Get the octave offset.
     *
     * @return The octave
     */
    public int getOctave ()
    {
        return this.octave;
    }


    /**
     * Increase the octave offset by 1.
     */
    public void incOctave ()
    {
        this.setOctave (this.octave + 1);
    }


    /**
     * Decrease the octave offset by 1.
     */
    public void decOctave ()
    {
        this.setOctave (this.octave - 1);
    }


    /**
     * Resetss the octave offset for the drum layout.
     */
    public void resetDrumOctave ()
    {
        this.drumOffset = this.drumNoteStart;
    }


    /**
     * Get the current offset of the drum grid.
     *
     * @return The offset
     */
    public int getDrumOffset ()
    {
        return this.drumOffset;
    }


    /**
     * Returns true if the drum octave can be decreased.
     *
     * @return True if the drum octave can be decreased.
     */
    public boolean canScrollDrumOctaveDown ()
    {

        return this.drumOffset - this.drumDefaultOffset >= DRUM_NOTE_LOWER;
    }


    /**
     * Returns true if the drum octave can be increased.
     *
     * @return True if the drum octave can be increased.
     */
    public boolean canScrollDrumOctaveUp ()
    {

        return this.drumOffset + this.drumDefaultOffset <= DRUM_NOTE_UPPER;
    }


    /**
     * Increases the drum layout by default drum offset.
     */
    public void incDrumOctave ()
    {
        this.drumOffset = Math.min (DRUM_NOTE_UPPER, this.drumOffset + this.drumDefaultOffset);
    }


    /**
     * Decreases the drum layout by the default drum offset.
     */
    public void decDrumOctave ()
    {
        this.drumOffset = Math.max (DRUM_NOTE_LOWER, this.drumOffset - this.drumDefaultOffset);
    }


    /**
     * Increases the drum layout by the given offset.
     *
     * @param offset The offset by which to increase the drum offset
     */
    public void incDrumOffset (final int offset)
    {
        this.drumOffset = Math.min (100, this.drumOffset + offset);
    }


    /**
     * Decreases the drum layout by the given offset.
     *
     * @param offset The offset by which to decrease the drum offset
     */
    public void decDrumOffset (final int offset)
    {
        this.drumOffset = Math.max (4, this.drumOffset - offset);
    }


    /**
     * Set the default value for de-/increasing the drum offset.
     *
     * @param drumDefaultOffset The offset
     */
    public void setDrumDefaultOffset (final int drumDefaultOffset)
    {
        this.drumDefaultOffset = drumDefaultOffset;
    }


    /**
     * Get the default value for de-/increasing the drum offset.
     *
     * @return The offset
     */
    public int getDrumDefaultOffset ()
    {
        return this.drumDefaultOffset;
    }


    /**
     * Sets the octave offset for the piano layout.
     *
     * @param octave The octave offset
     */
    public void setPianoOctave (final int octave)
    {
        this.pianoOctave = Math.max (-Scales.PIANO_OCTAVE_RANGE, Math.min (octave, Scales.PIANO_OCTAVE_RANGE));
    }


    /**
     * Get the octave offset for the piano layout.
     *
     * @return The octave offset
     */
    public int getPianoOctave ()
    {
        return this.pianoOctave;
    }


    /**
     * Increases the piano layout by 1 octave.
     */
    public void incPianoOctave ()
    {
        this.setPianoOctave (this.pianoOctave + 1);
    }


    /**
     * Decreases the piano layout by 1 octave.
     */
    public void decPianoOctave ()
    {
        this.setPianoOctave (this.pianoOctave - 1);
    }


    /**
     * Sets the number of scale steps that the notes in the next rows are shifted (e.g. 4).
     *
     * @param shift The steps
     */
    public void setPlayShift (final int shift)
    {
        this.shift = shift;
        this.generateMatrices ();
    }


    /**
     * Get the number of scale steps that the notes in the next rows are shifted (e.g. 4).
     *
     * @return The steps
     */
    public int getPlayShift ()
    {
        return this.shift;
    }


    /**
     * Get the color index for the given note respecting the note map.
     *
     * @param noteMap The note map
     * @param note A note
     * @return The color index
     */
    public String getColor (final int [] noteMap, final int note)
    {
        final int midiNote = noteMap[note];
        if (midiNote == -1)
            return Scales.SCALE_COLOR_OFF;
        // Add 12 to prevent negative values
        final int n = (12 + midiNote - Scales.OFFSETS[this.scaleOffset]) % 12;
        if (n == 0)
            return Scales.SCALE_COLOR_OCTAVE;
        if (!this.isChromatic ())
            return Scales.SCALE_COLOR_NOTE;
        return this.isInScale (n) ? Scales.SCALE_COLOR_NOTE : Scales.SCALE_COLOR_OUT_OF_SCALE;
    }


    /**
     * Test if the note is part of the selected scale.
     *
     * @param note The note to test (0-11)
     * @return True if it is part of the scale
     */
    public boolean isInScale (final int note)
    {
        for (final int interval: this.selectedScale.getIntervals ())
        {
            if (interval == note)
                return true;
        }
        return false;
    }


    /**
     * Get the active note matrix.
     *
     * @return The matrix
     */
    public int [] getNoteMatrix ()
    {
        final int [] matrix = this.getActiveMatrix ();
        final int [] noteMap = Scales.getEmptyMatrix ();
        for (int note = this.startNote; note < this.endNote; note++)
        {
            final int n = matrix[note - this.startNote] + Scales.OFFSETS[this.scaleOffset] + this.startNote + this.octave * 12;
            noteMap[note] = n < 0 || n > 127 ? -1 : n;
        }
        return noteMap;
    }


    /**
     * Get the active sequencer matrix.
     *
     * @param length The expected length
     * @param offset An offset to add to the notes
     * @return The matrix
     */
    public int [] getSequencerMatrix (final int length, final int offset)
    {
        final int [] matrix = this.getActiveMatrix ();
        final int [] noteMap = new int [length];
        Arrays.fill (noteMap, -1);
        for (int note = 0; note < length; note++)
        {
            final int n = matrix[note] + Scales.OFFSETS[this.scaleOffset] + offset;
            noteMap[note] = n < 0 || n > 127 ? -1 : n;
        }
        return noteMap;
    }


    /**
     * Get the piano matrix.
     *
     * @return The matrix
     */
    public int [] getPianoMatrix ()
    {
        final int [] matrix = PIANO_MATRIX;
        final int [] noteMap = Scales.getEmptyMatrix ();
        for (int note = this.startNote; note < this.endNote; note++)
        {
            final int ns = matrix[note - this.startNote];
            final int n = ns == -1 ? -1 : ns + this.startNote + this.pianoOctave * 12;
            noteMap[note] = n < 0 || n > 127 ? -1 : n;
        }
        return noteMap;
    }


    /**
     * Get a new empty matrix. All notes are off.
     *
     * @return The empty matrix
     */
    public static int [] getEmptyMatrix ()
    {
        final int [] emptyMatrix = new int [128];
        Arrays.fill (emptyMatrix, -1);
        return emptyMatrix;
    }


    /**
     * Get a new identity matrix. All notes are mapped to themselves.
     *
     * @return The identity matrix
     */
    public static int [] getIdentityMatrix ()
    {
        final int [] identityMatrix = new int [128];
        for (int i = 0; i < 128; i++)
            identityMatrix[i] = i;
        return identityMatrix;
    }


    /**
     * Get the drum matrix.
     *
     * @return The drum matrix
     */
    public int [] getDrumMatrix ()
    {
        final int [] noteMap = Scales.getEmptyMatrix ();
        for (int note = this.drumNoteStart; note < this.drumNoteEnd; note++)
        {
            final int ns = this.drumMatrix[note - this.drumNoteStart];
            final int n = ns == -1 ? -1 : ns + this.drumOffset;
            noteMap[note] = n < 0 || n > 127 ? -1 : n;
        }
        return noteMap;
    }


    /**
     * Set a new drum matrix.
     *
     * @param matrix The new drum matrix
     */
    public void setDrumMatrix (final int [] matrix)
    {
        this.drumMatrix = matrix;
    }


    /**
     * Set the first drum note.
     *
     * @param drumNoteStart The first drum note
     */
    public void setDrumNoteStart (final int drumNoteStart)
    {
        this.drumNoteStart = drumNoteStart;
    }


    /**
     * Set the last drum note.
     *
     * @param drumNoteEnd The last drum note
     */
    public void setDrumNoteEnd (final int drumNoteEnd)
    {
        this.drumNoteEnd = drumNoteEnd;
    }


    /**
     * Create a text description for the current minimum and maximum notes of the scale, respecting
     * offsets and octaves.
     *
     * @return The text
     */
    public String getRangeText ()
    {
        final int [] matrix = this.getActiveMatrix ();
        final int offset = Scales.OFFSETS[this.scaleOffset];
        return this.formatNote (offset + matrix[0]) + " to " + this.formatNote (offset + matrix[matrix.length - 1]);
    }


    /**
     * Create a text description for the from and to note parameters.
     *
     * @param from The start note
     * @param to The end note
     * @return The text
     */
    public static String getSequencerRangeText (final int from, final int to)
    {
        return Scales.formatNoteAndOctave (from, -3) + " to " + Scales.formatNoteAndOctave (to, -3);
    }


    /**
     * Create a text description for the current minimum and maximum notes of the drum layout,
     * respecting the octaves.
     *
     * @return The text
     */
    public String getDrumRangeText ()
    {
        final int s = this.getDrumOffset ();
        return Scales.formatDrumNote (s) + " to " + Scales.formatDrumNote (s + 15);
    }


    /**
     * Create a text description for the current minimum and maximum notes of the piano layout,
     * respecting the octaves.
     *
     * @return The text
     */
    public String getPianoRangeText ()
    {
        return this.formatNote (this.pianoOctave * 12) + " to " + this.formatNote ((this.pianoOctave + 4) * 12);
    }


    /**
     * Format a note as text.
     *
     * @param note The note
     * @return The text
     */
    public String formatNote (final int note)
    {
        return Scales.formatNoteAndOctave (note, this.octave);
    }


    /**
     * Format a drum note as text.
     *
     * @param note The note
     * @return The text
     */
    public static String formatDrumNote (final int note)
    {
        return Scales.formatNoteAndOctave (note, -3);
    }


    /**
     * Formats a note with an octave.
     *
     * @param note The note
     * @param octaveOffset The octave
     * @return The text
     */
    public static String formatNoteAndOctave (final int note, final int octaveOffset)
    {
        return Scales.NOTE_NAMES[Math.abs (note % 12)] + Integer.toString (note / 12 + octaveOffset + 1);
    }


    /**
     * Get the matrix of the selected scale.
     *
     * @return The matrix
     */
    public int [] getActiveMatrix ()
    {
        final ScaleGrid scaleGrid = this.scaleGrids.get (this.selectedScale);
        return this.isChromatic () ? scaleGrid.getChromatic () : scaleGrid.getMatrix ();
    }


    /**
     * Overwrite to hook in translation for grids which do not send midi notes 36-100.
     *
     * @param matrix The matrix to translate
     * @return The modified matrix
     */
    public int [] translateMatrixToGrid (final int [] matrix)
    {
        return matrix;
    }


    /**
     * Generate all matrices for all scales.
     */
    private void generateMatrices ()
    {
        this.scaleGrids.clear ();
        for (final Scale scale: Scale.values ())
            this.scaleGrids.put (scale, new ScaleGrid (scale, this.scaleLayout, this.orientation, this.numRows, this.numColumns, this.shift));
    }


    /**
     * Get the first midi note of the pad grid.
     *
     * @return The first midi note of the pad grid
     */
    public int getStartNote ()
    {
        return this.startNote;
    }


    /**
     * Set the first midi note of the pad grid.
     *
     * @param startNote The first midi note of the pad grid
     */
    public void setStartNote (final int startNote)
    {
        this.startNote = startNote;
    }


    /**
     * Get the last midi note of the pad grid.
     *
     * @return The last midi note of the pad grid
     */
    public int getEndNote ()
    {
        return this.endNote;
    }


    /**
     * Set the last midi note of the pad grid.
     *
     * @param endNote The last midi note of the pad grid
     */
    public void setEndNote (final int endNote)
    {
        this.endNote = endNote;
    }
}