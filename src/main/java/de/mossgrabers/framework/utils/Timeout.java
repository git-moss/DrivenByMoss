// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import de.mossgrabers.framework.daw.IHost;


/**
 * Support a timeout of a number of millisecond. The timeout can be delayed if an interrupt occured
 * meanwhile.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Timeout
{
    private final IHost host;
    private final int   timeoutMillis;
    private long        updateTime;


    /**
     * Constructor.
     *
     * @param host The host for scheduling
     * @param timeoutMillis The delay in milliseconds
     */
    public Timeout (final IHost host, final int timeoutMillis)
    {
        this.host = host;
        this.timeoutMillis = timeoutMillis;
    }


    /**
     * Delays the given runnable. If this method is called again before the previous timeout
     * expired, the previous runnable will not be executed.
     *
     * @param runnable The runnable to execute after the timeout delay
     */
    public void delay (final Runnable runnable)
    {
        final long nowTime = System.currentTimeMillis ();
        this.updateTime = nowTime;

        this.host.scheduleTask ( () -> {

            if (nowTime == this.updateTime)
                runnable.run ();

        }, this.timeoutMillis);
    }
}
