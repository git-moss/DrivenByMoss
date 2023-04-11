// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.osc.module;

import de.mossgrabers.controller.osc.exception.IllegalParameterException;
import de.mossgrabers.controller.osc.exception.MissingCommandException;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.clip.IClip;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.osc.IOpenSoundControlWriter;
import de.mossgrabers.framework.parameter.IParameter;

import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Abstract implementation of an OSC module.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractModule implements IModule
{
    private static final Pattern      RGB_COLOR_PATTERN = Pattern.compile ("(rgb|RGB)\\((\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?),(\\d+(\\.\\d+)?)\\)");

    protected static final String     TAG_EXISTS        = "exists";
    protected static final String     TAG_ACTIVATED     = "activated";
    protected static final String     TAG_NAME          = "name";
    protected static final String     TAG_SELECTED      = "selected";
    protected static final String     TAG_SELECT        = "select";
    protected static final String     TAG_DUPLICATE     = "duplicate";
    protected static final String     TAG_REMOVE        = "remove";
    protected static final String     TAG_VOLUME        = "volume";
    protected static final String     TAG_PAGE          = "page";
    protected static final String     TAG_INDICATE      = "indicate";
    protected static final String     TAG_TOUCHED       = "touched";
    protected static final String     TAG_COLOR         = "color";
    protected static final String     TAG_BYPASS        = "bypass";
    protected static final String     TAG_PARAM         = "param";
    protected static final String     TAG_MIXER         = "mixer";
    protected static final String     TAG_PREROLL       = "preroll";

    protected final IHost             host;
    protected final IModel            model;
    protected IOpenSoundControlWriter writer;


    /**
     * Constructor.
     *
     * @param host The host
     * @param model The model
     * @param writer The writer
     */
    protected AbstractModule (final IHost host, final IModel model, final IOpenSoundControlWriter writer)
    {
        this.host = host;
        this.model = model;
        this.writer = writer;
    }


    /** {@inheritDoc} */
    @Override
    public void flush (final boolean dump)
    {
        // Intentionally empty
    }


    /**
     * Get the clip to use.
     *
     * @return The clip
     */
    protected IClip getClip ()
    {
        return this.model.getNoteClip (8, 128);
    }


    /**
     * Test for a trigger value.
     *
     * @param value The value to test
     * @return Returns true if the value is null or a number with a positive value greater 0
     */
    protected static boolean isTrigger (final Object value)
    {
        return value == null || value instanceof final Number number && number.doubleValue () > 0;
    }


    /**
     * Converts the given value to an integer.
     *
     * @param value The value
     * @return The value is converted to an integer
     * @throws IllegalParameterException If the value is null or not a number
     */
    protected static int toInteger (final Object value) throws IllegalParameterException
    {
        return (int) toNumber (value);
    }


    /**
     * Converts the given value to a number.
     *
     * @param value The value
     * @param defaultValue The default value to return if value is null
     * @return If the value is null the default value is returned, otherwise the value is converted
     *         to a double
     * @throws IllegalParameterException If the value is not null and not a number
     */
    protected static double toNumber (final Object value, final double defaultValue) throws IllegalParameterException
    {
        if (value == null)
            return defaultValue;
        if (value instanceof final Number number)
            return number.doubleValue ();
        throw new IllegalParameterException ("Parameter is not a Number");
    }


    /**
     * Converts the given value to a number.
     *
     * @param value The value
     * @return The value is converted to a double
     * @throws IllegalParameterException If the value is null or not a number
     */
    protected static double toNumber (final Object value) throws IllegalParameterException
    {
        if (value == null)
            throw new IllegalParameterException ("Number parameter missing");
        if (value instanceof final Number number)
            return number.doubleValue ();
        throw new IllegalParameterException ("Parameter is not a Number");
    }


    /**
     * Converts the given value to a string.
     *
     * @param value The value
     * @return The value is converted to a string
     * @throws IllegalParameterException If the value is null
     */
    protected static String toString (final Object value) throws IllegalParameterException
    {
        if (value == null)
            throw new IllegalParameterException ("String parameter missing");
        return value.toString ();
    }


    /**
     * Get the next sub-command from the path and removes it from the path.
     *
     * @param path The path
     * @return The sub-command
     * @throws MissingCommandException If the path is empty
     */
    protected static String getSubCommand (final LinkedList<String> path) throws MissingCommandException
    {
        if (path.isEmpty ())
            throw new MissingCommandException ();
        return path.removeFirst ();
    }


    /**
     * Flush all data of a parameter.
     *
     * @param writer Where to send the messages to
     * @param fxAddress The start address for the effect
     * @param fxParam The parameter
     * @param dump Forces a flush if true otherwise only changed values are flushed
     */
    protected void flushParameterData (final IOpenSoundControlWriter writer, final String fxAddress, final IParameter fxParam, final boolean dump)
    {
        final boolean isSend = fxParam instanceof ISend;
        if (isSend)
            writer.sendOSC (fxAddress + TAG_ACTIVATED, ((ISend) fxParam).isEnabled (), dump);

        writer.sendOSC (fxAddress + TAG_EXISTS, fxParam.doesExist (), dump);
        writer.sendOSC (fxAddress + TAG_NAME, fxParam.getName (), dump);
        writer.sendOSC (fxAddress + (isSend ? "volumeStr" : "valueStr"), fxParam.getDisplayedValue (), dump);
        writer.sendOSC (fxAddress + (isSend ? TAG_VOLUME : "value"), fxParam.getValue (), dump);
        writer.sendOSC (fxAddress + "modulatedValue", fxParam.getModulatedValue (), dump);
    }


    protected static Optional<ColorEx> matchColor (final String value)
    {
        final Matcher matcher = RGB_COLOR_PATTERN.matcher (value);
        if (!matcher.matches ())
            return Optional.empty ();
        final int count = matcher.groupCount ();
        if (count == 7)
            return Optional.of (new ColorEx (Double.parseDouble (matcher.group (2)) / 255.0, Double.parseDouble (matcher.group (4)) / 255.0, Double.parseDouble (matcher.group (6)) / 255.0));
        return Optional.empty ();
    }
}
