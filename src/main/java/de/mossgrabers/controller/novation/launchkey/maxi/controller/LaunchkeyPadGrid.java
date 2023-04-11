// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchkey.maxi.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.PadGridImpl;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.view.Views;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of the Launchkey grid of pads.
 *
 * @author Jürgen Moßgraber
 */
public class LaunchkeyPadGrid extends PadGridImpl
{
    // @formatter:off
    private static final int [] MAP_DRUM =
    {
        36, 37, 38, 39, 44, 45, 46, 47,
        40, 41, 42, 43, 48, 49, 50, 51
    };

    private static final int [] MAP_SESSION =
    {
        112, 113, 114, 115, 116, 117, 118, 119,
         96,  97,  98,  99, 100, 101, 102, 103
    };

    private static final int [] MAP_DEVICE_SELECT =
    {
         80,  81,  82,  83,  84,  85,  86, 87,
         64,  65,  66,  67,  68,  69,  70, 71
    };
    // @formatter:on

    private static final Map<Integer, Integer> INVERSE_MAP_DRUM          = new HashMap<> (16);
    private static final Map<Integer, Integer> INVERSE_MAP_SESSION       = new HashMap<> (16);
    private static final Map<Integer, Integer> INVERSE_MAP_DEVICE_SELECT = new HashMap<> (16);

    static
    {
        for (int i = 0; i < 16; i++)
        {
            final Integer note = Integer.valueOf (36 + i);
            INVERSE_MAP_DRUM.put (Integer.valueOf (MAP_DRUM[i]), note);
            INVERSE_MAP_SESSION.put (Integer.valueOf (MAP_SESSION[i]), note);
            INVERSE_MAP_DEVICE_SELECT.put (Integer.valueOf (MAP_DEVICE_SELECT[i]), note);
        }
    }

    private Views activeView;


    /**
     * Constructor.
     *
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public LaunchkeyPadGrid (final ColorManager colorManager, final IMidiOutput output)
    {
        super (colorManager, output, 2, 8, 36);
    }


    /** {@inheritDoc} */
    @Override
    public int translateToGrid (final int note)
    {
        if (this.activeView == null)
            return note;

        final Integer n = Integer.valueOf (note);
        Integer inverse;
        switch (this.activeView)
        {
            case DRUM:
                inverse = INVERSE_MAP_DRUM.get (n);
                break;

            case DEVICE:
                inverse = INVERSE_MAP_DEVICE_SELECT.get (n);
                break;

            default:
            case SESSION:
                inverse = INVERSE_MAP_SESSION.get (n);
                break;
        }

        return inverse == null ? note : inverse.intValue ();
    }


    /** {@inheritDoc} */
    @Override
    public int [] translateToController (final int note)
    {
        return translateToController (this.activeView, note);
    }


    /**
     * Plug for grids not sending notes in the range of 36-100.
     *
     * @param view The view
     * @param note The outgoing note
     * @return The MIDI channel (index 0) and note (index 1) scaled to the controller
     */
    public static int [] translateToController (final Views view, final int note)
    {
        if (view == null)
            return new int []
            {
                0,
                note
            };

        final int [] result = new int [2];
        final int n = note - 36;
        switch (view)
        {
            case DRUM:
                result[0] = 9;
                result[1] = MAP_DRUM[n];
                break;

            case DEVICE:
                result[0] = 0;
                result[1] = MAP_DEVICE_SELECT[n];
                break;

            default:
            case SESSION:
                result[0] = 0;
                result[1] = MAP_SESSION[n];
                break;
        }
        return result;
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        if (this.activeView != null && this.activeView == Views.DRUM)
            this.output.sendNoteEx (0x09, note, color);
        else
            this.output.sendNote (note, color);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendBlinkState (final int channel, final int note, final int blinkColor, final boolean fast)
    {
        if (this.activeView != null && this.activeView == Views.DRUM)
            this.output.sendNoteEx (0x09 + (fast ? 1 : 2), note, blinkColor);
        else
            this.output.sendNoteEx (fast ? 1 : 2, note, blinkColor);
    }


    /**
     * Set the active view since the Launchkey has different key mappings in the different views.
     *
     * @param view The active view
     */
    public void setView (final Views view)
    {
        this.activeView = view;
    }
}