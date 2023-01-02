// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.controller;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.DefaultGraphicsDimensions;
import de.mossgrabers.framework.graphics.IBitmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * The display of Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Push2Display extends AbstractGraphicDisplay
{
    private final PushUsbDisplay usbDisplay;
    private boolean              isShutdown = false;


    /**
     * Constructor. 4 rows (0-3) with 4 blocks (0-3). Each block consists of 17 characters or 2
     * cells (0-7).
     *
     * @param host The host
     * @param maxParameterValue The maximum parameter value (upper bound)
     * @param configuration The Push configuration
     */
    public Push2Display (final IHost host, final int maxParameterValue, final PushConfiguration configuration)
    {
        super (host, configuration, new DefaultGraphicsDimensions (960, 160, maxParameterValue), "Push 2 Display");

        this.usbDisplay = new PushUsbDisplay (host);
    }


    /** {@inheritDoc} */
    @Override
    public void notify (final String message)
    {
        if (message == null)
            return;
        this.host.showNotification (message);
        this.setNotificationMessage (message);
    }


    /** {@inheritDoc} */
    @Override
    public void shutdown ()
    {
        this.setMessage (3, "Please start " + this.host.getName () + " to play...");
        this.send ();

        this.isShutdown = true;

        final ExecutorService executor = Executors.newSingleThreadExecutor ();
        executor.execute ( () -> {

            if (this.usbDisplay != null)
                this.usbDisplay.shutdown ();
            super.shutdown ();

        });
        executor.shutdown ();
        try
        {
            executor.awaitTermination (10, TimeUnit.SECONDS);
        }
        catch (final InterruptedException ex)
        {
            this.host.error ("Display shutdown interrupted.", ex);
            Thread.currentThread ().interrupt ();
        }
    }


    /** {@inheritDoc} */
    @Override
    protected void send (final IBitmap image)
    {
        if (!this.isShutdown && this.usbDisplay != null)
            this.usbDisplay.send (image);
    }
}