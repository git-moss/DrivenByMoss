// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.definition;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.controller.grid.IPadGrid;
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
public abstract class AbstractSimpleLaunchpad extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    private final int []     faderColorCache = new int [8];
    private final boolean [] faderPanCache   = new boolean [8];


    /**
     * Constructor.
     *
     * @param uuid The UUID of the controller implementation
     * @param hardwareModel The hardware model which this controller implementation supports
     */
    public AbstractSimpleLaunchpad (final UUID uuid, final String hardwareModel)
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
    public boolean hasFaderSupport ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getFaderModeCommand ()
    {
        return this.getProgramModeCommand ();
    }


    /** {@inheritDoc} */
    @Override
    public String getPanModeCommand ()
    {
        return this.getProgramModeCommand ();
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


    /** {@inheritDoc} */
    @Override
    public void setupFader (final LaunchpadControlSurface surface, final int index, final int color, final boolean isPan)
    {
        this.faderColorCache[index] = color;
        this.faderPanCache[index] = isPan;
    }


    /** {@inheritDoc} */
    @Override
    public void setFaderValue (final IPadGrid padGrid, final IMidiOutput output, final int index, final int value)
    {
        if (this.faderPanCache[index])
        {
            // Simulate pan fader
            if (value == 64)
            {
                for (int i = 0; i < 8; i++)
                    padGrid.lightEx (index, i, i == 3 || i == 4 ? this.faderColorCache[index] : 0);
                return;
            }

            if (value < 64)
            {
                for (int i = 4; i < 8; i++)
                    padGrid.lightEx (index, 7 - i, 0);

                final double numPads = 4.0 * value / 64.0 - 1;
                for (int i = 0; i < 4; i++)
                    padGrid.lightEx (index, 7 - i, i > numPads ? this.faderColorCache[index] : 0);
                return;
            }

            for (int i = 0; i < 4; i++)
                padGrid.lightEx (index, 7 - i, 0);

            final double numPads = 4.0 * (value - 64) / 64.0;
            for (int i = 4; i < 8; i++)
                padGrid.lightEx (index, 7 - i, i - 4 < numPads ? this.faderColorCache[index] : 0);
            return;
        }

        // Simulate normal fader
        final double numPads = 8.0 * value / 127.0;
        for (int i = 0; i < 8; i++)
            padGrid.lightEx (index, 7 - i, i < numPads ? this.faderColorCache[index] : 0);
    }
}
