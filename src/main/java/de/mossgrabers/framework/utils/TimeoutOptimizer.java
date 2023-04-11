// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import de.mossgrabers.framework.daw.IHost;


/**
 * Helper class to optimize a timeout interval depending on the computer/OS for using it with the
 * IHost.scheduleTask method.
 *
 * @author Jürgen Moßgraber
 */
public class TimeoutOptimizer
{
    private static final int RUNS       = 30;

    private final IHost      host;
    private int              delay;
    private long             startValue;
    private int              iterations = 0;
    private long             diff;


    /**
     * Constructor.
     *
     * @param host The host instance
     * @param delay The delay in milliseconds
     */
    public TimeoutOptimizer (final IHost host, final int delay)
    {
        this.host = host;
        this.delay = delay;

        this.startValue = System.currentTimeMillis ();
        this.host.scheduleTask (this::measure, delay);
    }


    /**
     * Executes the measurement for several times and calculates the average after a number of runs.
     */
    private void measure ()
    {
        final long endValue = System.currentTimeMillis ();
        this.diff += endValue - this.startValue;

        if (this.iterations < RUNS)
        {
            this.iterations++;
            this.startValue = System.currentTimeMillis ();
            this.host.scheduleTask (this::measure, this.delay);
        }
        else
        {
            final long effective = this.diff / RUNS;
            this.delay = (int) (this.delay * this.delay / effective);
        }
    }


    /**
     * Get the measured and calculated timeout which should be used to achieve the delay given in
     * the constructor.
     *
     * @return The timeout
     */
    public int getTimeout ()
    {
        return this.delay;
    }
}
