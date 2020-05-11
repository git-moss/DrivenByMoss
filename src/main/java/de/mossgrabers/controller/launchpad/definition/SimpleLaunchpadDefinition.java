// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.controller.grid.LightInfo;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


/**
 * Abstract base class for non-pro Launchpads.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class SimpleLaunchpadDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    /**
     * Constructor.
     *
     * @param uuid The UUID of the controller implementation
     * @param hardwareModel The hardware model which this controller implementation supports
     */
    public SimpleLaunchpadDefinition (final UUID uuid, final String hardwareModel)
    {
        super (uuid, hardwareModel, "Novation", 1, 1);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPro ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void resetMode (final LaunchpadControlSurface surface)
    {
        surface.sendLaunchpadSysEx ("0E 00");
    }


    /** {@inheritDoc} */
    @Override
    public void sendBlinkState (final IMidiOutput output, final int note, final int blinkColor, final boolean fast)
    {
        // Start blinking on channel 2, stop it on channel 1
        output.sendNoteEx (blinkColor == 0 ? 1 : 2, note, blinkColor);
    }


    /** {@inheritDoc} */
    @Override
    public void setLogoColor (final LaunchpadControlSurface surface, final int color)
    {
        surface.setTrigger (LaunchpadControlSurface.LAUNCHPAD_LOGO, color);
    }


    /** {@inheritDoc} */
    @Override
    public boolean sceneButtonsUseCC ()
    {
        return true;
    }


    /** {@inheritDoc} */
    @Override
    public List<String> buildLEDUpdate (final Map<Integer, LightInfo> padInfos)
    {
        final StringBuilder sb = new StringBuilder (this.getSysExHeader ()).append ("03 ");
        for (final Entry<Integer, LightInfo> e: padInfos.entrySet ())
        {
            final int note = e.getKey ().intValue ();
            final LightInfo info = e.getValue ();

            if (info.getBlinkColor () <= 0)
            {
                // 00h: Static colour from palette, Lighting data is 1 byte specifying palette
                // entry.
                sb.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
            }
            else
            {
                if (info.isFast ())
                {
                    // 01h: Flashing colour, Lighting data is 2 bytes specifying Colour B and
                    // Colour A.
                    sb.append ("01 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
                else
                {
                    // 02h: Pulsing colour, Lighting data is 1 byte specifying palette entry.
                    sb.append ("02 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
            }
        }
        return Collections.singletonList (sb.append ("F7").toString ());
    }
}
