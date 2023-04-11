// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.hardware;

import de.mossgrabers.framework.controller.hardware.AbstractHwControl;
import de.mossgrabers.framework.controller.hardware.IHwTextDisplay;

import com.bitwig.extension.controller.api.HardwareTextDisplay;


/**
 * Implementation of a proxy to a text display on a hardware controller.
 *
 * @author Jürgen Moßgraber
 */
public class HwTextDisplayImpl extends AbstractHwControl implements IHwTextDisplay
{
    private final HardwareTextDisplay textDisplay;


    /**
     * Constructor.
     *
     * @param textDisplay The Bitwig text display proxy
     */
    public HwTextDisplayImpl (final HardwareTextDisplay textDisplay)
    {
        super (null, null);

        this.textDisplay = textDisplay;
    }


    /** {@inheritDoc}} */
    @Override
    public void setBounds (final double x, final double y, final double width, final double height)
    {
        this.textDisplay.setBounds (x, y, width, height);
    }


    /** {@inheritDoc}} */
    @Override
    public void setLine (final int line, final String text)
    {
        this.textDisplay.line (line).text ().setValue (text);
    }
}
