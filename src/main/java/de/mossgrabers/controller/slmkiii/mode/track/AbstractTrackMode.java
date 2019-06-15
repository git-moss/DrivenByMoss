// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode.track;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColors;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.slmkiii.mode.BaseMode;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.daw.DAWColors;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode
{
    protected final List<Pair<String, Boolean>> menu = new ArrayList<> ();


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    public AbstractTrackMode (final String name, final SLMkIIIControlSurface surface, final IModel model)
    {
        super (name, surface, model);
        this.isTemporary = false;

        for (int i = 0; i < 8; i++)
            this.menu.add (new Pair<> (" ", Boolean.FALSE));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row != 0)
            return;

        if (event == ButtonEvent.DOWN)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            if (selectedTrack != null)
            {
                switch (index)
                {
                    case 0:
                        selectedTrack.toggleIsActivated ();
                        break;
                    case 1:
                        this.model.toggleCursorTrackPinned ();
                        break;
                    case 2:
                        // TODO implement Color select view
                        break;
                    case 5:
                        this.model.getApplication ().addInstrumentTrack ();
                        break;
                    case 6:
                        this.model.getApplication ().addAudioTrack ();
                        break;
                    case 7:
                        this.model.getApplication ().addEffectTrack ();
                        break;
                    default:
                        // Not used
                        break;
                }
            }
            return;
        }

        final ITrack track = tb.getItem (index);

        if (event == ButtonEvent.UP)
        {
            if (this.surface.isPressed (SLMkIIIControlSurface.MKIII_DUPLICATE))
            {
                this.surface.setButtonConsumed (SLMkIIIControlSurface.MKIII_DUPLICATE);
                track.duplicate ();
                return;
            }

            if (this.surface.isPressed (SLMkIIIControlSurface.MKIII_CLEAR))
            {
                this.surface.setButtonConsumed (SLMkIIIControlSurface.MKIII_CLEAR);
                track.remove ();
                return;
            }

            final ITrack selTrack = tb.getSelectedItem ();
            if (selTrack != null && selTrack.getIndex () == index)
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_DEVICE, ButtonEvent.DOWN);
            else
                track.select ();
        }
    }


    /**
     * Handle the selection of a send effect.
     *
     * @param sendIndex The index of the send
     */
    protected void handleSendEffect (final int sendIndex)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        if (tb == null || !tb.canEditSend (sendIndex))
            return;
        final Integer si = Integer.valueOf (Modes.MODE_SEND1.intValue () + sendIndex);
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (modeManager.isActiveOrTempMode (si) ? Modes.MODE_TRACK : si);
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            if (selectedTrack == null)
            {
                for (int i = 0; i < 8; i++)
                    this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, SLMkIIIColors.SLMKIII_BLACK);
                return;
            }

            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1, selectedTrack.isActivated () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_2, this.model.isCursorTrackPinned () ? SLMkIIIColors.SLMKIII_RED : SLMkIIIColors.SLMKIII_RED_HALF);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_3, SLMkIIIColors.SLMKIII_RED_HALF);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_4, SLMkIIIColors.SLMKIII_BLACK);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_5, SLMkIIIColors.SLMKIII_BLACK);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_6, SLMkIIIColors.SLMKIII_RED_HALF);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_7, SLMkIIIColors.SLMKIII_RED_HALF);
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_8, SLMkIIIColors.SLMKIII_RED_HALF);
            return;
        }

        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);

            int color = SLMkIIIColors.SLMKIII_BLACK;
            if (t.doesExist ())
            {
                if (t.isSelected ())
                {
                    final String colorIndex = DAWColors.getColorIndex (t.getColor ());
                    color = this.model.getColorManager ().getColor (colorIndex);
                }
                else
                    color = SLMkIIIColors.SLMKIII_WHITE_HALF;
            }
            this.surface.updateButton (SLMkIIIControlSurface.MKIII_DISPLAY_BUTTON_1 + i, color);
        }
    }


    /**
     * Draw the row with track names.
     */
    protected void drawRow4 ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        if (selectedTrack == null)
        {
            for (int i = 0; i < 8; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColors.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, 0);
            }
            return;
        }

        if (this.surface.isShiftPressed ())
        {
            d.setCell (3, 0, "On/Off");
            d.setPropertyColor (0, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (0, 1, selectedTrack.isActivated () ? 1 : 0);

            d.setCell (3, 1, "Pin");
            d.setPropertyColor (1, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (1, 1, this.model.isCursorTrackPinned () ? 1 : 0);

            d.setCell (3, 2, "Color");
            d.setPropertyColor (2, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (2, 1, 0);

            d.setCell (3, 3, "");
            d.setPropertyColor (3, 2, SLMkIIIColors.SLMKIII_BLACK);
            d.setPropertyValue (3, 1, 0);

            d.setCell (3, 4, "");
            d.setPropertyColor (4, 2, SLMkIIIColors.SLMKIII_BLACK);
            d.setPropertyValue (4, 1, 0);

            d.setCell (3, 5, "Add Instr");
            d.setPropertyColor (5, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (5, 1, 0);

            d.setCell (3, 6, "Add Audio");
            d.setPropertyColor (6, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (6, 1, 0);

            d.setCell (3, 7, "Add FX");
            d.setPropertyColor (7, 2, SLMkIIIColors.SLMKIII_RED);
            d.setPropertyValue (7, 1, 0);

            return;
        }

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            d.setCell (3, i, StringUtils.shortenAndFixASCII (t.getName (9), 9));

            final boolean exists = t.doesExist ();

            int color = SLMkIIIColors.SLMKIII_BLACK;
            if (t.isActivated ())
            {
                final String colorIndex = DAWColors.getColorIndex (t.getColor ());
                color = this.model.getColorManager ().getColor (colorIndex);
            }
            else
                color = SLMkIIIColors.SLMKIII_DARK_GREY;

            d.setPropertyColor (i, 2, color);
            d.setPropertyValue (i, 1, exists && t.isSelected () ? 1 : 0);
        }
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }


    protected void setColumnColors (final SLMkIIIDisplay display, int column, final ITrack track, final int knobColorIndex)
    {
        int color = track.doesExist () ? knobColorIndex : SLMkIIIColors.SLMKIII_BLACK;
        display.setPropertyColor (column, 1, track.isActivated () ? color : SLMkIIIColors.SLMKIII_DARK_GREY);
        if (track.doesExist ())
        {
            if (track.isActivated ())
            {
                final String colorIndex = DAWColors.getColorIndex (track.getColor ());
                color = this.model.getColorManager ().getColor (colorIndex);
            }
            else
                color = SLMkIIIColors.SLMKIII_DARK_GREY;
        }
        display.setPropertyColor (column, 0, color);
    }
}