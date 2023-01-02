// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.definition;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.controller.novation.launchpad.definition.button.ButtonSetup;
import de.mossgrabers.controller.novation.launchpad.definition.button.LaunchpadButton;
import de.mossgrabers.framework.controller.DefaultControllerDefinition;
import de.mossgrabers.framework.controller.grid.LightInfo;
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
public abstract class AbstractLaunchpadDefinition extends DefaultControllerDefinition implements ILaunchpadControllerDefinition
{
    protected final ButtonSetup buttonSetup = new ButtonSetup ();


    /**
     * Constructor.
     *
     * @param uuid The UUID of the controller implementation
     * @param hardwareModel The hardware model which this controller implementation supports
     */
    protected AbstractLaunchpadDefinition (final UUID uuid, final String hardwareModel)
    {
        super (uuid, hardwareModel, "Novation", 1, 1);

        this.buttonSetup.setButton (LaunchpadButton.ARROW_UP, 91);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_DOWN, 92);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_LEFT, 93);
        this.buttonSetup.setButton (LaunchpadButton.ARROW_RIGHT, 94);

        this.buttonSetup.setButton (LaunchpadButton.SESSION, 95);
        this.buttonSetup.setButton (LaunchpadButton.NOTE, 96);
        this.buttonSetup.setButton (LaunchpadButton.DEVICE, 97);

        this.buttonSetup.setButton (LaunchpadButton.SHIFT, 98);

        this.buttonSetup.setButton (LaunchpadButton.SCENE1, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1);
        this.buttonSetup.setButton (LaunchpadButton.SCENE2, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2);
        this.buttonSetup.setButton (LaunchpadButton.SCENE3, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3);
        this.buttonSetup.setButton (LaunchpadButton.SCENE4, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4);
        this.buttonSetup.setButton (LaunchpadButton.SCENE5, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5);
        this.buttonSetup.setButton (LaunchpadButton.SCENE6, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6);
        this.buttonSetup.setButton (LaunchpadButton.SCENE7, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7);
        this.buttonSetup.setButton (LaunchpadButton.SCENE8, LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8);
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPro ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasTrackSelectionButtons ()
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
    public ButtonSetup getButtonSetup ()
    {
        return this.buttonSetup;
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
                // 00h: Static color from palette, Lighting data is 1 byte specifying palette
                // entry.
                sb.append ("00 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
            }
            else
            {
                if (info.isFast ())
                {
                    // 01h: Flashing color, Lighting data is 2 bytes specifying Color B and
                    // Color A.
                    sb.append ("01 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getBlinkColor ())).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
                else
                {
                    // 02h: Pulsing color, Lighting data is 1 byte specifying palette entry.
                    sb.append ("02 ").append (StringUtils.toHexStr (note)).append (' ').append (StringUtils.toHexStr (info.getColor ())).append (' ');
                }
            }
        }
        return Collections.singletonList (sb.append ("F7").toString ());
    }
}
