// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.controller;

import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.LatestTaskExecutor;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * The HUI main display. Note that the original HUI display uses a modified ASCII set (e.g. it
 * supports umlauts) but since emulations do not support it this implementation sticks to basic
 * ASCII.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HUIDisplay extends AbstractTextDisplay
{
    private static final String      SYSEX_DISPLAY_HEADER = "F0 00 00 66 05 00 10 ";

    private final LatestTaskExecutor executor             = new LatestTaskExecutor ();


    /**
     * Constructor. 1 row (0) with 9 blocks (0-8). Each block consists of 4 characters or 1 cell
     * (0-8).
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     */
    public HUIDisplay (final IHost host, final IMidiOutput output)
    {
        super (host, output, 1 /* No of rows */, 9 /* No of cells */, 36);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text)
    {
        if (this.executor.isShutdown ())
            return;
        this.executor.execute ( () -> {
            try
            {
                this.sendDisplayLine (text);
            }
            catch (final RuntimeException ex)
            {
                this.host.error ("Could not send line to HUI display.", ex);
            }
        });
    }


    /**
     * Send a line to the display.
     *
     * @param text The text to send
     */
    private void sendDisplayLine (final String text)
    {
        final String t = text;

        final int [] array = new int [5];
        for (int cell = 0; cell < this.noOfCells; cell++)
        {
            array[0] = cell;
            for (int i = 0; i < 4; i++)
                array[1 + i] = t.charAt (cell * 4 + i);
            this.output.sendSysex (new StringBuilder (SYSEX_DISPLAY_HEADER).append (StringUtils.toHexStr (array)).append ("F7").toString ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.notifyOnDisplay ("Please start " + this.host.getName () + "...");

        final ExecutorService shutdownExecutor = Executors.newSingleThreadExecutor ();
        shutdownExecutor.execute ( () -> {

            // Prevent further sends
            this.executor.shutdown ();
            try
            {
                if (!this.executor.awaitTermination (5, TimeUnit.SECONDS))
                    this.host.error ("HUI display send executor did not end in 5 seconds.");
            }
            catch (final InterruptedException ex)
            {
                this.host.error ("HUI display send executor interrupted.", ex);
                Thread.currentThread ().interrupt ();
            }

        });
        shutdownExecutor.shutdown ();
        try
        {
            if (!shutdownExecutor.awaitTermination (10, TimeUnit.SECONDS))
                this.host.error ("HUI display shutdown executor did not end in 10 seconds.");
        }
        catch (final InterruptedException ex)
        {
            this.host.error ("Display shutdown interrupted.", ex);
            Thread.currentThread ().interrupt ();
        }
    }
}