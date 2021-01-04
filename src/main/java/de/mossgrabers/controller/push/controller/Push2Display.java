// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.controller.display.AbstractGraphicDisplay;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.graphics.DefaultGraphicsDimensions;
import de.mossgrabers.framework.graphics.IBitmap;


/**
 * The display of Push 2.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class Push2Display extends AbstractGraphicDisplay
{
    private final PushUsbDisplay usbDisplay;


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
        if (this.usbDisplay != null)
            this.usbDisplay.shutdown ();

        super.shutdown ();
    }


    /** {@inheritDoc} */
    @Override
    protected void send (final IBitmap image)
    {
        if (this.usbDisplay != null)
            this.usbDisplay.send (image);
    }
}