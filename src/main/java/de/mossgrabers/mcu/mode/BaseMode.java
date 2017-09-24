// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.mcu.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.StringUtils;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.CursorDeviceProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.mcu.MCUConfiguration;
import de.mossgrabers.mcu.MCUControllerExtension;
import de.mossgrabers.mcu.controller.MCUControlSurface;


/**
 * Base class for all modes used by MCU.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class BaseMode extends AbstractMode<MCUControlSurface, MCUConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final MCUControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final int channel = this.surface.getExtenderOffset () + index;

        if (row == 0)
        {
            // Mode specific
            this.resetParameter (index);
            return;
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (row == 1)
        {
            tb.toggleArm (channel);
        }
        else if (row == 2)
        {
            if (this.surface.isShiftPressed ())
                tb.toggleAutoMonitor (channel);
            else
                tb.toggleSolo (channel);
        }
        else if (row == 3)
        {
            if (this.surface.isShiftPressed ())
                tb.toggleMonitor (channel);
            else
                tb.toggleMute (channel);
        }
    }


    protected abstract void resetParameter (final int index);


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData track = tb.getTrack (extenderOffset + i);
            this.surface.updateButton (MCUControlSurface.MCU_ARM1 + i, track.isRecarm () ? MCUControllerExtension.MCU_BUTTON_STATE_ON : MCUControllerExtension.MCU_BUTTON_STATE_OFF);
            this.surface.updateButton (MCUControlSurface.MCU_SOLO1 + i, track.isSolo () ? MCUControllerExtension.MCU_BUTTON_STATE_ON : MCUControllerExtension.MCU_BUTTON_STATE_OFF);
            this.surface.updateButton (MCUControlSurface.MCU_MUTE1 + i, track.isMute () ? MCUControllerExtension.MCU_BUTTON_STATE_ON : MCUControllerExtension.MCU_BUTTON_STATE_OFF);
            this.surface.updateButton (MCUControlSurface.MCU_SELECT1 + i, track.isSelected () ? MCUControllerExtension.MCU_BUTTON_STATE_ON : MCUControllerExtension.MCU_BUTTON_STATE_OFF);
        }

        this.updateKnobLEDs ();
    }


    protected abstract void updateKnobLEDs ();


    protected void drawDisplay2 ()
    {
        if (!this.surface.getConfiguration ().hasDisplay2 ())
            return;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();

        // Format track names
        final Display d2 = this.surface.getSecondDisplay ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (extenderOffset + i);
            d2.setCell (0, i, this.optimizeName (StringUtils.fixASCII (t.getName ()), 6));
        }
        d2.setCell (0, 8, "Maste").done (0);

        final MasterTrackProxy masterTrack = this.model.getMasterTrack ();
        final CursorDeviceProxy cursorDevice = this.model.getCursorDevice ();
        if (masterTrack.isSelected ())
        {
            d2.clearRow (1).setBlock (1, 0, "Sel.track: ").setBlock (1, 1, StringUtils.fixASCII (masterTrack.getName ()));
            d2.setBlock (1, 2, "Sel.devce: ").setBlock (1, 3, cursorDevice.hasSelectedDevice () ? cursorDevice.getName () : "None").setCell (1, 8, "      ");
            d2.done (1);
            return;
        }

        final TrackData selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            d2.setRow (1, "               Please select a track...                 ");
        else
        {
            d2.clearRow (1).setBlock (1, 0, "Sel.track: ").setBlock (1, 1, StringUtils.fixASCII (selectedTrack.getName ()));
            d2.setBlock (1, 2, "Sel.devce: ").setBlock (1, 3, cursorDevice.hasSelectedDevice () ? cursorDevice.getName () : "None").setCell (1, 8, "      ");
        }
        d2.done (1);
    }
}