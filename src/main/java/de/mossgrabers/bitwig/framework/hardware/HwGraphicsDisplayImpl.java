// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwGraphicsDisplay;

import com.bitwig.extension.controller.api.HardwarePixelDisplay;


/**
 * Implementation of a proxy to a graphics display on a hardware controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class HwGraphicsDisplayImpl extends AbstractHwControl implements IHwGraphicsDisplay
{
    private final HardwarePixelDisplay graphicsDisplay;


    /**
     * Constructor.
     *
     * @param graphicsDisplay The Bitwig text display proxy
     */
    public HwGraphicsDisplayImpl (final HardwarePixelDisplay graphicsDisplay)
    {
        super (null, null);

        this.graphicsDisplay = graphicsDisplay;
    }


    /** {@inheritDoc}} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.graphicsDisplay.setBounds (x, y, width, height);
    }
}
