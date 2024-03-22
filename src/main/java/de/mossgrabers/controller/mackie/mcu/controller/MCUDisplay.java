// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.mcu.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.mossgrabers.controller.mackie.mcu.MCUConfiguration;
import de.mossgrabers.controller.mackie.mcu.MCUConfiguration.SecondDisplay;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.display.AbstractTextDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.LatestTaskExecutor;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The MCU main display.
 *
 * @author Jürgen Moßgraber
 */
public class MCUDisplay extends AbstractTextDisplay
{
    private static final String         SYSEX_DISPLAY_HEADER1_MAIN     = "F0 00 00 66 14 12 ";
    private static final String         SYSEX_DISPLAY_HEADER1_EXTENDER = "F0 00 00 66 15 12 ";
    private static final String         SYSEX_DISPLAY_HEADER2          = "F0 00 00 67 15 13 ";

    private final boolean               isFirstDisplay;
    private final boolean               isExtender;
    private final boolean               isMainDevice;
    private final Configuration         configuration;

    private final LatestTaskExecutor [] executors                      = new LatestTaskExecutor [4];
    private boolean                     isShutdown                     = false;
    private boolean                     insertSpace                    = true;


    /**
     * Constructor. 2 rows (0-1) with 4 blocks (0-3). Each block consists of 18 characters or 2
     * cells (0-8).
     *
     * @param host The host
     * @param output The MIDI output which addresses the display
     * @param isFirst True if it is the first display, otherwise the second
     * @param isMCUExtender True if it is an original Mackie extender
     * @param isMainDevice True if this is the main device
     * @param configuration The configuration
     */
    public MCUDisplay (final IHost host, final IMidiOutput output, final boolean isFirst, final boolean isMCUExtender, final boolean isMainDevice, final Configuration configuration)
    {
        super (host, output, 2 /* No of rows */, !isFirst && isMainDevice ? 9 : 8 /* No of cells */, 56);

        this.isFirstDisplay = isFirst;
        this.isMainDevice = isMainDevice;
        this.isExtender = isMCUExtender;
        this.configuration = configuration;

        this.centerNotification = false;

        for (int i = 0; i < this.executors.length; i++)
            this.executors[i] = new LatestTaskExecutor ();
    }


    /**
     * Update to use the 'short' or 'long' (including 'Master').
     */
    public void updateShortSecondDisplay ()
    {
        if (!this.isFirstDisplay && this.isMainDevice && this.configuration instanceof MCUConfiguration conf)
            this.setNumberOfCells (this.noOfLines, conf.hasDisplay2 () == SecondDisplay.QCON ? 9 : 8, this.noOfCharacters);
    }


    /**
     * If enabled, shortens the cell content by 1 and adds a blank character instead.
     *
     * @param enable
     */
    public void insertSpace (final boolean enable)
    {
        this.insertSpace = enable;
    }


    /** {@inheritDoc} */
    @Override
    public ITextDisplay setCell (final int row, final int column, final String value)
    {
        try
        {
            final String content;
            if (this.insertSpace)
                content = StringUtils.pad (value, this.charactersOfCell - 1) + " ";
            else
                content = StringUtils.pad (value, this.charactersOfCell);
            this.cells[row * this.noOfCells + column] = content;
        }
        catch (final ArrayIndexOutOfBoundsException ex)
        {
            this.host.error ("Display array index out of bounds.", ex);
        }
        return this;
    }


    /** {@inheritDoc} */
    @Override
    protected void updateLine (final int row, final String text, final String previousText)
    {
        String t = text;
        if (!this.isFirstDisplay && this.isMainDevice && this.configuration instanceof MCUConfiguration conf && conf.hasDisplay2 () == SecondDisplay.QCON)
        {
            // If a 9th master cell should be added
            if (row == 0)
                t = t.substring (0, t.length () - 1) + 'r';
            t = "  " + t;
        }

        super.updateLine (row, t, previousText);
    }


    /** {@inheritDoc} */
    @Override
    public void writeLine (final int row, final String text, final String previousText)
    {
        if (this.isShutdown)
            return;

        final LatestTaskExecutor executor = this.executors[row + (this.isFirstDisplay ? 0 : 2)];
        executor.execute ( () -> {
            try
            {
                final int length = text.length ();
                final int [] array = new int [length];
                for (int i = 0; i < length; i++)
                    array[i] = text.charAt (i);
                this.output.sendSysex (new StringBuilder (this.getHeader ()).append (row == 0 ? "00 " : "38 ").append (StringUtils.toHexStr (array)).append ("F7").toString ());
            }
            catch (final RuntimeException ex)
            {
                this.host.error ("Could not send line to MCU display.", ex);
            }
        });
    }


    private String getHeader ()
    {
        if (this.isFirstDisplay)
            return this.isExtender ? SYSEX_DISPLAY_HEADER1_EXTENDER : SYSEX_DISPLAY_HEADER1_MAIN;
        return SYSEX_DISPLAY_HEADER2;
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        if (this.isShutdown)
            return;

        this.notifyOnDisplay ("Please  start " + this.host.getName () + " ...     ");

        // Prevent further sends
        this.isShutdown = true;

        final ExecutorService shutdownExecutor = Executors.newSingleThreadExecutor ();
        shutdownExecutor.execute ( () -> {

            for (final LatestTaskExecutor executor: this.executors)
            {
                executor.shutdown ();
                try
                {
                    if (!executor.awaitTermination (5, TimeUnit.SECONDS))
                        this.host.error ("MCU display send executor did not end in 5 seconds.");
                }
                catch (final InterruptedException ex)
                {
                    this.host.error ("MCU display send executor interrupted.", ex);
                    Thread.currentThread ().interrupt ();
                }
            }

        });
        shutdownExecutor.shutdown ();
        try
        {
            if (!shutdownExecutor.awaitTermination (10, TimeUnit.SECONDS))
                this.host.error ("MCU display shutdown executor did not end in 10 seconds.");
        }
        catch (final InterruptedException ex)
        {
            this.host.error ("Display shutdown interrupted.", ex);
            Thread.currentThread ().interrupt ();
        }
    }
}