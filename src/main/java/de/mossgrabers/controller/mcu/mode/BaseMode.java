// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mcu.mode;

import de.mossgrabers.controller.mcu.MCUConfiguration;
import de.mossgrabers.controller.mcu.MCUControllerSetup;
import de.mossgrabers.controller.mcu.controller.MCUControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


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
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public BaseMode (final String name, final MCUControlSurface surface, final IModel model)
    {
        super (name, surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
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

        final ITrack track = this.model.getCurrentTrackBank ().getItem (channel);
        if (row == 1)
            track.toggleRecArm ();
        else if (row == 2)
        {
            if (this.surface.isShiftPressed ())
                track.toggleAutoMonitor ();
            else if (this.surface.isSelectPressed ())
                this.model.clearSolo ();
            else
                track.toggleSolo ();
        }
        else if (row == 3)
        {
            if (this.surface.isShiftPressed ())
                track.toggleMonitor ();
            else if (this.surface.isSelectPressed ())
                this.model.clearMute ();
            else
                track.toggleMute ();
        }
    }


    protected abstract void resetParameter (final int index);


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int extenderOffset = this.surface.getExtenderOffset ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (extenderOffset + i);

            if (buttonID == ButtonID.get (ButtonID.ROW_SELECT_1, i))
                return track.isSelected () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW2_1, i))
                return track.isRecArm () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW3_1, i))
                return track.isSolo () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW4_1, i))
                return track.isMute () ? MCUControllerSetup.MCU_BUTTON_STATE_ON : MCUControllerSetup.MCU_BUTTON_STATE_OFF;
        }

        return MCUControllerSetup.MCU_BUTTON_STATE_OFF;
    }


    /**
     * Update the knob LED rings.
     */
    public abstract void updateKnobLEDs ();


    protected void drawDisplay2 ()
    {
        if (!this.surface.getConfiguration ().hasDisplay2 ())
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();

        // Format track names
        final ITextDisplay d2 = this.surface.getTextDisplay (1);
        final int extenderOffset = this.surface.getExtenderOffset ();

        final boolean isMainDevice = this.surface.isMainDevice ();

        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (extenderOffset + i);
            d2.setCell (0, i, StringUtils.shortenAndFixASCII (t.getName (), isMainDevice ? 6 : 7));
        }

        if (isMainDevice)
            d2.setCell (0, 8, "Maste");

        d2.done (0);
        d2.clearRow (1);

        if (isMainDevice)
        {
            final IMasterTrack masterTrack = this.model.getMasterTrack ();
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            final ITrack selectedTrack = masterTrack.isSelected () ? masterTrack : tb.getSelectedItem ();
            d2.setBlock (1, 0, "Sel. track:").setBlock (1, 1, selectedTrack == null ? "None" : StringUtils.shortenAndFixASCII (selectedTrack.getName (), 11));
            d2.setBlock (1, 2, "Sel. devce:").setBlock (1, 3, cursorDevice.doesExist () ? StringUtils.shortenAndFixASCII (cursorDevice.getName (), 11) : "None");
        }

        d2.done (1);
    }
}