// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.slmkiii.mode.track;

import de.mossgrabers.controller.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.slmkiii.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode
{
    protected final List<Pair<String, Boolean>> menu      = new ArrayList<> ();

    private static final String []              MODE_MENU =
    {
        "Track",
        "Volume",
        "Pan",
        "Send 1",
        "Send 2",
        "Send 3",
        "Send 4",
        "Send 5"
    };

    private static final Modes []               MODES     =
    {
        Modes.TRACK,
        Modes.VOLUME,
        Modes.PAN,
        Modes.SEND1,
        Modes.SEND2,
        Modes.SEND3,
        Modes.SEND4,
        Modes.SEND5
    };


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

        if (event != ButtonEvent.UP)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            switch (index)
            {
                case 0:
                    if (selectedTrack != null)
                        selectedTrack.toggleIsActivated ();
                    break;
                case 1:
                    if (selectedTrack != null)
                        this.model.toggleCursorTrackPinned ();
                    break;
                case 2:
                    if (selectedTrack != null)
                        this.surface.getViewManager ().setActiveView (Views.COLOR);
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
            return;
        }

        if (this.surface.isLongPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.getModeManager ().setActiveMode (MODES[index]);
            this.surface.setTriggerConsumed (ButtonID.ARROW_DOWN);
            return;
        }

        final ITrack track = tb.getItem (index);

        if (this.surface.isPressed (ButtonID.DUPLICATE))
        {
            this.surface.setTriggerConsumed (ButtonID.DUPLICATE);
            track.duplicate ();
            return;
        }

        if (this.surface.isPressed (ButtonID.DELETE))
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            track.remove ();
            return;
        }

        final ITrack selTrack = tb.getSelectedItem ();
        if (selTrack != null && selTrack.getIndex () == index)
            this.surface.getButton (ButtonID.ARROW_UP).getCommand ().execute (ButtonEvent.DOWN, 127);
        else
            track.select ();
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
        final Modes si = Modes.get (Modes.SEND1, sendIndex);
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActiveMode (modeManager.isActiveOrTempMode (si) ? Modes.TRACK : si);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            switch (buttonID)
            {
                case ROW1_1:
                    if (selectedTrack == null)
                        return SLMkIIIColorManager.SLMKIII_BLACK;
                    return selectedTrack.isActivated () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
                case ROW1_2:
                    if (selectedTrack == null)
                        return SLMkIIIColorManager.SLMKIII_BLACK;
                    return this.model.isCursorTrackPinned () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
                case ROW1_3:
                    if (selectedTrack == null)
                        return SLMkIIIColorManager.SLMKIII_BLACK;
                    return SLMkIIIColorManager.SLMKIII_RED_HALF;
                case ROW1_4:
                case ROW1_5:
                    return SLMkIIIColorManager.SLMKIII_BLACK;

                default:
                    return SLMkIIIColorManager.SLMKIII_RED_HALF;
            }
        }

        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();

        if (this.surface.isLongPressed (ButtonID.ARROW_DOWN))
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            return modeManager.isActiveMode (MODES[index]) ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_GREEN_HALF;
        }

        final ITrack t = tb.getItem (index);

        if (t.doesExist ())
        {
            if (t.isSelected ())
            {
                final String colorIndex = DAWColor.getColorIndex (t.getColor ());
                return this.model.getColorManager ().getColorIndex (colorIndex);
            }
            return SLMkIIIColorManager.SLMKIII_WHITE_HALF;
        }
        return SLMkIIIColorManager.SLMKIII_BLACK;
    }


    /**
     * Draw the row with track names.
     */
    protected void drawRow4 ()
    {
        final SLMkIIIDisplay d = this.surface.getDisplay ();

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            this.drawRow4Shifted (d, selectedTrack);
            return;
        }

        if (this.surface.isLongPressed (ButtonID.ARROW_DOWN))
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            for (int i = 0; i < 8; i++)
            {
                d.setCell (3, i, MODE_MENU[i]);
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_GREEN);
                d.setPropertyValue (i, 1, modeManager.isActiveMode (MODES[i]) ? 1 : 0);
            }
            return;
        }

        // Format track names
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            d.setCell (3, i, StringUtils.shortenAndFixASCII (t.getName (9), 9));

            final boolean exists = t.doesExist ();

            int color;
            if (t.isActivated ())
            {
                final String colorIndex = DAWColor.getColorIndex (t.getColor ());
                color = this.model.getColorManager ().getColorIndex (colorIndex);
            }
            else
                color = SLMkIIIColorManager.SLMKIII_DARK_GREY;

            d.setPropertyColor (i, 2, color);
            d.setPropertyValue (i, 1, exists && t.isSelected () ? 1 : 0);
        }
    }


    private void drawRow4Shifted (final SLMkIIIDisplay d, final ITrack selectedTrack)
    {
        if (selectedTrack == null)
        {
            for (int i = 0; i < 3; i++)
            {
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_BLACK);
                d.setPropertyValue (i, 1, 0);
            }
        }
        else
        {
            d.setCell (3, 0, "On/Off");
            d.setPropertyColor (0, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (0, 1, selectedTrack.isActivated () ? 1 : 0);

            d.setCell (3, 1, "Pin");
            d.setPropertyColor (1, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (1, 1, this.model.isCursorTrackPinned () ? 1 : 0);

            d.setCell (3, 2, "Color");
            d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (2, 1, 0);
        }

        d.setCell (3, 3, "");
        d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_BLACK);
        d.setPropertyValue (3, 1, 0);

        d.setCell (3, 4, "");
        d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_BLACK);
        d.setPropertyValue (4, 1, 0);

        d.setCell (3, 5, "Add Instr");
        d.setPropertyColor (5, 2, SLMkIIIColorManager.SLMKIII_RED);
        d.setPropertyValue (5, 1, 0);

        d.setCell (3, 6, "Add Audio");
        d.setPropertyColor (6, 2, SLMkIIIColorManager.SLMKIII_RED);
        d.setPropertyValue (6, 1, 0);

        d.setCell (3, 7, "Add FX");
        d.setPropertyColor (7, 2, SLMkIIIColorManager.SLMKIII_RED);
        d.setPropertyValue (7, 1, 0);
    }


    /** {@inheritDoc} */
    @Override
    protected ITrackBank getBank ()
    {
        return this.model.getCurrentTrackBank ();
    }


    protected void setColumnColors (final SLMkIIIDisplay display, final int column, final ITrack track, final int knobColorIndex)
    {
        int color = track.doesExist () ? knobColorIndex : SLMkIIIColorManager.SLMKIII_BLACK;
        display.setPropertyColor (column, 1, track.isActivated () ? color : SLMkIIIColorManager.SLMKIII_DARK_GREY);
        if (track.doesExist ())
        {
            if (track.isActivated ())
            {
                final String colorIndex = DAWColor.getColorIndex (track.getColor ());
                color = this.model.getColorManager ().getColorIndex (colorIndex);
            }
            else
                color = SLMkIIIColorManager.SLMKIII_DARK_GREY;
        }
        display.setPropertyColor (column, 0, color);
    }
}