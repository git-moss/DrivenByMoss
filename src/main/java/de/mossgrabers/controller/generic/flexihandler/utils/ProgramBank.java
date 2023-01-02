// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.flexihandler.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A bank with programs which can be addressed by program change commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProgramBank
{
    private static final Pattern       BANK_PATTERN  = Pattern.compile ("\\$BANK=(.+)\\$MSB=(\\d+)\\$LSB=(\\d+)\\$CHANNEL=(\\d+)", Pattern.UNICODE_CASE);

    private String                     name;
    private int                        msb;
    private int                        lsb;
    private int                        channel;
    private String []                  programs;
    private final Map<String, Integer> programMapper = new HashMap<> (128);


    /**
     * Parse the programs file.
     *
     * @param text The text to parse
     * @return The parsed banks
     * @throws ParseException
     */
    public static List<ProgramBank> parse (final String text) throws ParseException
    {
        final List<ProgramBank> banks = new ArrayList<> ();

        int pos = 0;
        final String [] lines = text.split ("\\R");

        while (pos < lines.length)
        {
            final Matcher matcher = BANK_PATTERN.matcher (lines[pos]);
            if (!matcher.matches ())
                break;

            final ProgramBank pb = new ProgramBank ();

            pb.name = matcher.group (1);
            if (pb.name == null || pb.name.isBlank ())
                throw new ParseException ("Missing BANK name.", pos);

            final String msb = matcher.group (2);
            if (msb == null || msb.isBlank ())
                throw new ParseException ("Missing MSB value.", pos);

            final String lsb = matcher.group (3);
            if (lsb == null || lsb.isBlank ())
                throw new ParseException ("Missing LSB value.", pos);

            final String channel = matcher.group (4);
            if (channel == null || channel.isBlank ())
                throw new ParseException ("Missing CHANNEL value.", pos);

            try
            {
                pb.msb = Integer.parseInt (msb);
                if (pb.msb < 0 || pb.msb > 127)
                    throw new NumberFormatException ("Value must be in the range of 0..127");
            }
            catch (final NumberFormatException ex)
            {
                throw new ParseException ("MSB value must be a number in the range of 0 to 127.", pos);
            }

            try
            {
                pb.lsb = Integer.parseInt (lsb);
                if (pb.lsb < 0 || pb.lsb > 127)
                    throw new NumberFormatException ("Value must be in the range of 0..127");
            }
            catch (final NumberFormatException ex)
            {
                throw new ParseException ("LSB value must be a number in the range of 0 to 127.", pos);
            }

            try
            {
                pb.channel = Integer.parseInt (channel);
                if (pb.channel < 0 || pb.channel > 15)
                    throw new NumberFormatException ("Value must be in the range of 0..15");
            }
            catch (final NumberFormatException ex)
            {
                throw new ParseException ("LSB value must be a number in the range of 0 to 127.", pos);
            }

            banks.add (pb);

            pos++;

            final List<String> programs = new ArrayList<> (128);
            while (pos < lines.length && !lines[pos].startsWith ("$BANK="))
            {
                pb.programMapper.put (lines[pos], Integer.valueOf (programs.size ()));
                programs.add (lines[pos]);
                pos++;
            }
            if (programs.isEmpty () || programs.size () > 128)
                throw new ParseException ("There must be at least 1 program and not more than 128.", pos);
            pb.programs = programs.toArray (new String [programs.size ()]);
        }

        if (banks.isEmpty ())
            throw new ParseException ("Could not parse any banks. Check file format.", pos);

        return banks;
    }


    /**
     * Get the bank name.
     *
     * @return The name of the bank
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get the MSB (Most Significant Byte) to send for the bank change.
     *
     * @return The MSB
     */
    public int getMSB ()
    {
        return this.msb;
    }


    /**
     * Get the LSB (Least Significant Byte) to send for the bank change.
     *
     * @return The LSB
     */
    public int getLSB ()
    {
        return this.lsb;
    }


    /**
     * Get the MIDI channel to send for the bank change.
     *
     * @return The MIDI channel
     */
    public int getMidiChannel ()
    {
        return this.channel;
    }


    /**
     * Get the programs.
     *
     * @return The programs
     */
    public String [] getPrograms ()
    {
        return this.programs;
    }


    /**
     * Lookup the PC value for the program name.
     *
     * @param programName The program name
     * @return The program change value
     */
    public int lookupProgram (final String programName)
    {
        final Integer pc = this.programMapper.get (programName);
        return pc == null ? -1 : pc.intValue ();
    }
}
