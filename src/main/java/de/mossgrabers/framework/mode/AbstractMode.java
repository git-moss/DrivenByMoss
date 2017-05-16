// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.ControlSurface;


/**
 * Abstract class for all modes.
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractMode<S extends ControlSurface<C>, C extends Configuration> implements Mode
{
    /** Color identifier for a mode button which is off. */
    public static final String   BUTTON_COLOR_OFF = "BUTTON_COLOR_OFF";
    /** Color identifier for a mode button which is on. */
    public static final String   BUTTON_COLOR_ON  = "BUTTON_COLOR_ON";
    /** Color identifier for a mode button which is hilighted. */
    public static final String   BUTTON_COLOR_HI  = "BUTTON_COLOR_HI";
    /** Color identifier for a mode button which is on (second row). */
    public static final String   BUTTON_COLOR2_ON = "BUTTON_COLOR2_ON";
    /** Color identifier for a mode button which is hilighted (second row). */
    public static final String   BUTTON_COLOR2_HI = "BUTTON_COLOR2_HI";

    private static final char [] REMOVABLE_CHARS  =
    {
            ' ',
            'e',
            'a',
            'u',
            'i',
            'o'
    };

    protected S                  surface;
    protected Model              model;
    protected boolean            isTemporary;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AbstractMode (final S surface, final Model model)
    {
        this.surface = surface;
        this.model = model;
        this.isTemporary = true;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTemporary ()
    {
        return this.isTemporary;
    }


    /**
     * Shortens a text to the given length.
     *
     * @param text The text to shorten
     * @param length The length to shorten to
     * @return The shortened text
     */
    protected String optimizeName (final String text, final int length)
    {
        if (text == null)
            return "";

        String shortened = text;
        for (final char element: REMOVABLE_CHARS)
        {
            if (shortened.length () <= length)
                return shortened;
            int pos;
            while ((pos = shortened.indexOf (element)) != -1)
            {
                shortened = shortened.substring (0, pos) + shortened.substring (pos + 1, shortened.length ());
                if (shortened.length () <= length)
                    return shortened;
            }
        }
        return shortened;
    }
}