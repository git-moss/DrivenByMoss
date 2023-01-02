// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.mackie.hui.mode.track;

import de.mossgrabers.controller.mackie.hui.HUIConfiguration;
import de.mossgrabers.controller.mackie.hui.HUIControllerSetup;
import de.mossgrabers.controller.mackie.hui.controller.HUIControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends DefaultTrackMode<HUIControlSurface, HUIConfiguration>
{
    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractTrackMode (final String name, final HUIControlSurface surface, final IModel model)
    {
        super (name, surface, model, false);
    }


    protected ITextDisplay drawTrackHeader ()
    {
        final ITextDisplay d = this.surface.getTextDisplay ().clear ();

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final Optional<ITrack> track = this.getTrack (i);
            if (track.isPresent ())
                d.setCell (0, i, StringUtils.shortenAndFixASCII (track.get ().getName (), 4));
        }

        return d;
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 0)
            this.resetParameter (index);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        for (int i = 0; i < 8; i++)
        {
            final Optional<ITrack> track = this.getTrack (i);

            final boolean exists = track.isPresent ();

            // HUI_INSERT1 is used on icon for selection
            if (buttonID == ButtonID.get (ButtonID.ROW_SELECT_1, i) || buttonID == ButtonID.get (ButtonID.ROW5_1, i))
                return exists && track.get ().isSelected () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW2_1, i))
                return exists && track.get ().isRecArm () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW3_1, i))
                return exists && track.get ().isSolo () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
            if (buttonID == ButtonID.get (ButtonID.ROW4_1, i))
                return exists && track.get ().isMute () ? HUIControllerSetup.HUI_BUTTON_STATE_ON : HUIControllerSetup.HUI_BUTTON_STATE_OFF;
        }

        return HUIControllerSetup.HUI_BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    protected Optional<ITrack> getTrack (final int index)
    {
        final int extenderOffset = this.surface.getExtenderOffset ();
        return super.getTrack (extenderOffset + index);
    }


    /**
     * Update the knob LEDs.
     */
    public abstract void updateKnobLEDs ();


    protected abstract void resetParameter (int index);
}