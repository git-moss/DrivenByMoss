// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Executes only one task. When new tasks arrive for execution only the latest one will be stored
 * for execution.
 *
 * @author Jürgen Moßgraber
 */
public class LatestTaskExecutor implements ExecutorService
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


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.executor.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isShutdown ()
    {
        return this.executor.isShutdown ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean awaitTermination (final long timeout, final TimeUnit unit) throws InterruptedException
    {
        return this.executor.awaitTermination (timeout, unit);
    }


    /** {@inheritDoc} */
    @Override
    public List<Runnable> shutdownNow ()
    {
        return this.executor.shutdownNow ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isTerminated ()
    {
        return this.executor.isTerminated ();
    }


    /** {@inheritDoc} */
    @Override
    public <T> Future<T> submit (final Callable<T> task)
    {
        throw new LatestTaskException ();
    }


    /** {@inheritDoc} */
    @Override
    public <T> Future<T> submit (final Runnable task, final T result)
    {
        throw new LatestTaskException ();
    }


    /** {@inheritDoc} */
    @Override
    public Future<?> submit (final Runnable task)
    {
        throw new LatestTaskException ();
    }


    /** {@inheritDoc} */
    @Override
    public <T> List<Future<T>> invokeAll (final Collection<? extends Callable<T>> tasks) throws InterruptedException
    {
        throw new LatestTaskException ();
    }


    /** {@inheritDoc} */
    @Override
    public <T> List<Future<T>> invokeAll (final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException
    {
        throw new LatestTaskException ();
    }


    /** {@inheritDoc} */
    @Override
    public <T> T invokeAny (final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
    {
        throw new LatestTaskException ();
    }


    /** {@inheritDoc} */
    @Override
    public <T> T invokeAny (final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        throw new LatestTaskException ();
    }
}