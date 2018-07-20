// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Executes only one task. When new tasks arrive for execution only the latest one will be stored
 * for execution.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LatestTaskExecutor implements Executor
{
    private final AtomicReference<Runnable> lastTask = new AtomicReference<> ();
    private final ExecutorService           executor;


    /**
     * Constructor.
     */
    public LatestTaskExecutor ()
    {
        this.executor = Executors.newSingleThreadExecutor ();
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final Runnable command)
    {
        this.lastTask.set (command);
        this.executor.execute ( () -> {
            final Runnable task = this.lastTask.getAndSet (null);
            if (task != null)
                task.run ();
        });
    }


    /**
     * Shutdown the executor.
     */
    public void shutdown ()
    {
        this.executor.shutdown ();
    }


    /**
     * Returns {@code true} if this executor has been shut down.
     *
     * @return {@code true} if this executor has been shut down
     */
    public boolean isShutdown ()
    {
        return this.executor.isShutdown ();
    }
}