// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import de.mossgrabers.framework.daw.IHost;

import java.util.LinkedList;


/**
 * Test framework.
 *
 * @author Jürgen Moßgraber
 */
public class TestFramework
{
    private static final int           ANSWER_DELAY = 100;

    private final IHost                host;
    private final LinkedList<Runnable> scheduler    = new LinkedList<> ();


    /**
     * Constructor.
     *
     * @param host The controller host
     */
    public TestFramework (final IHost host)
    {
        this.host = host;
    }


    /**
     * Execute all scheduled test functions.
     *
     * @param callback
     */
    public void executeScheduler (final TestCallback callback)
    {
        if (this.scheduler.isEmpty ())
        {
            callback.endTesting ();
            return;
        }

        final int delay = ANSWER_DELAY;

        final Runnable exec = this.scheduler.remove ();
        try
        {
            exec.run ();
        }
        catch (final RuntimeException ex)
        {
            this.host.error (ex.getLocalizedMessage (), ex);
        }

        this.host.scheduleTask ( () -> this.executeScheduler (callback), delay);
    }


    /**
     * Schedule a function for later test processing.
     *
     * @param f The function to schedule
     */
    public void scheduleFunction (final Runnable f)
    {
        this.scheduler.add (f);
    }
}
