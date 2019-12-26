// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.OSCColors;
import de.mossgrabers.controller.osc.OSCConfiguration;
import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.controller.osc.exception.UnknownCommandException;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.INoteInput;
import de.mossgrabers.framework.daw.midi.INoteRepeat;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;

import java.util.LinkedList;


/**
 * All MIDI related commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MidiModule extends AbstractModule
{
    private final KeyManager                        keyManager;
    private final IControlSurface<OSCConfiguration> surface;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param surface The surface
     * @param writer The writer
     * @param keyManager The key manager
     */
    public MidiModule (final IHost host, final IModel model, final IControlSurface<OSCConfiguration> surface, final IOpenSoundControlWriter writer, final KeyManager keyManager)
    {
        super (host, model, writer);

        this.surface = surface;
        this.keyManager = keyManager;
    }


    /** {@inheritDoc} */
    @Override
    public String [] getSupportedCommands ()
    {
        return new String []
        {
            "vkb_midi"
        };
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final String command, final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        switch (command)
        {
            case "vkb_midi":
                this.parseMidi (path, value);
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        final String noteAddress = "/vkb_midi/note/";
        for (int i = 0; i < 127; i++)
        {
            final double [] color = this.getNoteColor (i);
            this.writer.sendOSCColor (noteAddress + i + "/color", color[0], color[1], color[2], dump);
        }

        final INoteInput noteInput = this.surface.getMidiInput ().getDefaultNoteInput ();
        if (noteInput == null)
            return;
        final INoteRepeat noteRepeat = noteInput.getNoteRepeat ();
        final String noteRepeatAddress = "/vkb_midi/noterepeat/";

        this.writer.sendOSC (noteRepeatAddress + "isActive", noteRepeat.isActive (), dump);
        this.writer.sendOSC (noteRepeatAddress + "period", Resolution.getNameAt (Resolution.getMatch (noteRepeat.getPeriod ())), dump);
        this.writer.sendOSC (noteRepeatAddress + "length", Resolution.getNameAt (Resolution.getMatch (noteRepeat.getNoteLength ())), dump);
    }


    /**
     * Parse virtual MIDI note commands.
     *
     * @param path The rest of the path
     * @param value The value
     * @throws MissingCommandException Could not find the sub-command
     * @throws UnknownCommandException Unknown sub-command
     * @throws IllegalParameterException Added an illegal parameter
     */
    private void parseMidi (final LinkedList<String> path, final Object value) throws IllegalParameterException, UnknownCommandException, MissingCommandException
    {
        final OSCConfiguration conf = this.surface.getConfiguration ();

        final String command = getSubCommand (path);

        switch (command)
        {
            case "velocity":
                final int numValue = toInteger (value);
                conf.setAccentEnabled (numValue > 0);
                if (numValue > 0)
                    conf.setAccentValue (numValue);
                return;

            case "noterepeat":
                this.parseNoteRepeat (path, value);
                return;

            default:
                // Fall through
                break;
        }

        int midiChannel;
        try
        {
            midiChannel = Math.min (Math.max (0, Integer.parseInt (command) - 1), 15);
        }
        catch (final NumberFormatException ex)
        {
            throw new UnknownCommandException (command);
        }

        final String subCommand = getSubCommand (path);
        final IMidiInput input = this.surface.getMidiInput ();
        final Scales scales = this.model.getScales ();
        switch (subCommand)
        {
            case "note":
                final String n = getSubCommand (path);
                switch (n)
                {
                    case "+":
                        if (isTrigger (value))
                        {
                            scales.incOctave ();
                            this.surface.setKeyTranslationTable (this.model.getScales ().getNoteMatrix ());
                            this.surface.getDisplay ().notify (scales.getRangeText ());
                        }
                        break;

                    case "-":
                        if (isTrigger (value))
                        {
                            scales.decOctave ();
                            this.surface.setKeyTranslationTable (this.model.getScales ().getNoteMatrix ());
                            this.surface.getDisplay ().notify (scales.getRangeText ());
                        }
                        break;

                    default:
                        final int note = Integer.parseInt (n);
                        int numValue = toInteger (value);
                        if (numValue > 0)
                            numValue = conf.isAccentActive () ? conf.getFixedAccentValue () : numValue;
                        final int [] keyTranslationMatrix = this.surface.getKeyTranslationTable ();
                        final int data0 = keyTranslationMatrix[note];
                        if (data0 >= 0)
                            input.sendRawMidiEvent (0x90 + midiChannel, data0, numValue);

                        // Mark selected notes
                        for (int i = 0; i < 128; i++)
                        {
                            if (keyTranslationMatrix[note] == keyTranslationMatrix[i])
                                this.keyManager.setKeyPressed (i, numValue);
                        }
                }
                break;

            case "drum":
                final String n2 = getSubCommand (path);
                switch (n2)
                {
                    case "+":
                        if (isTrigger (value))
                        {
                            scales.incDrumOctave ();
                            this.surface.getDisplay ().notify (scales.getDrumRangeText ());
                        }
                        break;

                    case "-":
                        if (isTrigger (value))
                        {
                            scales.decDrumOctave ();
                            this.surface.getDisplay ().notify (scales.getDrumRangeText ());
                        }
                        break;

                    default:
                        final int note = Integer.parseInt (n2);
                        int numValue = toInteger (value);
                        if (numValue > 0)
                            numValue = conf.isAccentActive () ? conf.getFixedAccentValue () : numValue;
                        final int data0 = this.model.getScales ().getDrumMatrix ()[note];
                        if (data0 >= 0)
                            input.sendRawMidiEvent (0x90 + midiChannel, data0, numValue);
                        break;
                }
                break;

            case "cc":
                if (path.isEmpty ())
                {
                    this.host.println ("Missing Midi CC value.");
                    return;
                }
                final int cc = Integer.parseInt (path.removeFirst ());
                input.sendRawMidiEvent (0xB0 + midiChannel, cc, toInteger (value));
                break;

            case "aftertouch":
                int numValue = toInteger (value);
                if (numValue > 0)
                    numValue = conf.isAccentActive () ? conf.getFixedAccentValue () : numValue;
                if (path.isEmpty ())
                {
                    input.sendRawMidiEvent (0xD0 + midiChannel, 0, numValue);
                    return;
                }
                final int note = Integer.parseInt (path.removeFirst ());
                input.sendRawMidiEvent (0xA0 + midiChannel, this.surface.getKeyTranslationTable ()[note], numValue);
                break;

            case "pitchbend":
                input.sendRawMidiEvent (0xE0 + midiChannel, 0, toInteger (value));
                break;

            default:
                throw new UnknownCommandException (command);
        }
    }


    /**
     * Parse note repeat parameters.
     *
     * @param path The rest of the path
     * @param value The value
     * @throws MissingCommandException Could not find the sub-command
     * @throws UnknownCommandException Unknown sub-command
     * @throws IllegalParameterException Added an illegal parameter
     */
    private void parseNoteRepeat (final LinkedList<String> path, final Object value) throws MissingCommandException, UnknownCommandException, IllegalParameterException
    {
        final INoteInput noteInput = this.surface.getMidiInput ().getDefaultNoteInput ();
        if (noteInput == null)
            return;
        final INoteRepeat noteRepeat = noteInput.getNoteRepeat ();

        final String subCommand = getSubCommand (path);
        switch (subCommand)
        {

            case "isActive":
                noteRepeat.setActive (toInteger (value) > 0);
                break;

            case "period":
                if (value == null)
                    throw new IllegalParameterException ("Value must not be empty.");
                final Resolution period = Resolution.getByName (value.toString ());
                if (period == null)
                    throw new IllegalParameterException ("Value must be one of {1/4, 1/4t, 1/8, 1/8t, 1/16, 1/16t, 1/32, 1/32t}.");
                noteRepeat.setPeriod (period.getValue ());
                break;

            case "length":
                if (value == null)
                    throw new IllegalParameterException ("Value must not be empty.");
                final Resolution length = Resolution.getByName (value.toString ());
                if (length == null)
                    throw new IllegalParameterException ("Value must be one of {1/4, 1/4t, 1/8, 1/8t, 1/16, 1/16t, 1/32, 1/32t}.");
                noteRepeat.setNoteLength (length.getValue ());
                break;

            default:
                throw new UnknownCommandException (subCommand);
        }
    }


    /**
     * Get the color for a note.
     *
     * @param note The note
     * @return The color
     */
    private double [] getNoteColor (final int note)
    {
        final boolean isKeyboardEnabled = this.model.canSelectedTrackHoldNotes ();
        if (!isKeyboardEnabled)
            return OSCColors.getColor (Scales.SCALE_COLOR_OFF);

        if (!this.keyManager.isKeyPressed (note))
            return OSCColors.getColor (this.keyManager.getColor (note));

        final boolean isRecording = this.model.hasRecordingState ();
        return isRecording ? OSCColors.COLOR_RED : OSCColors.COLOR_GREEN;
    }
}
