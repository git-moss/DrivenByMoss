// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.mode.track;

import de.mossgrabers.controller.novation.slmkiii.SLMkIIIConfiguration;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIColorManager;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIControlSurface;
import de.mossgrabers.controller.novation.slmkiii.controller.SLMkIIIDisplay;
import de.mossgrabers.controller.novation.slmkiii.mode.BaseMode;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.Pair;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.framework.view.Views;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode<ITrack>
{
    protected final List<Pair<String, Boolean>>                           menu      = new ArrayList<> ();

    private static final String []                                        MODE_MENU =
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

    private static final Modes []                                         MODES     =
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

    private final NewCommand<SLMkIIIControlSurface, SLMkIIIConfiguration> newCommand;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractTrackMode (final String name, final SLMkIIIControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCurrentTrackBank ());

        this.newCommand = new NewCommand<> (model, surface);

        model.addTrackBankObserver (this::switchBanks);

        for (int i = 0; i < 8; i++)
            this.menu.add (new Pair<> (" ", Boolean.FALSE));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || row != 0)
            return;

        // Combination with Shift
        if (this.surface.isShiftPressed ())
        {
            this.onButtonShifted (index);
            return;
        }

        // Combination with Arrow Down
        if (this.surface.isLongPressed (ButtonID.ARROW_DOWN))
        {
            this.surface.getModeManager ().setActive (MODES[index]);
            this.surface.setTriggerConsumed (ButtonID.ARROW_DOWN);
            return;
        }

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getItem (index);

        // Combination with Duplicate
        if (this.surface.isPressed (ButtonID.DUPLICATE))
        {
            this.surface.setTriggerConsumed (ButtonID.DUPLICATE);
            track.duplicate ();
            return;
        }

        // Combination with Delete
        if (this.surface.isPressed (ButtonID.DELETE))
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            track.remove ();
            return;
        }

        // Select track or expand group
        track.selectOrExpandGroup ();
    }


    /**
     * Handle button presses in combination with Shift.
     *
     * @param index The index of the button
     */
    private void onButtonShifted (final int index)
    {
        final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        switch (index)
        {
            case 0:
                if (selectedTrack.isPresent ())
                    selectedTrack.get ().toggleIsActivated ();
                break;
            case 1:
                if (selectedTrack.isPresent ())
                    this.model.getCursorTrack ().togglePinned ();
                break;
            case 2:
                if (selectedTrack.isPresent ())
                    this.surface.getViewManager ().setActive (Views.COLOR);
                break;
            case 4:
                this.newCommand.execute ();
                break;
            case 5:
                this.model.getTrackBank ().addChannel (ChannelType.INSTRUMENT);
                break;
            case 6:
                this.model.getTrackBank ().addChannel (ChannelType.AUDIO);
                break;
            case 7:
                this.model.getApplication ().addEffectTrack ();
                break;
            default:
                // Not used
                break;
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
        final Modes si = Modes.get (Modes.SEND1, sendIndex);
        final ModeManager modeManager = this.surface.getModeManager ();
        modeManager.setActive (modeManager.isActive (si) ? Modes.TRACK : si);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            switch (buttonID)
            {
                case ROW1_1:
                    if (selectedTrack.isEmpty ())
                        return SLMkIIIColorManager.SLMKIII_BLACK;
                    return selectedTrack.get ().isActivated () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
                case ROW1_2:
                    if (selectedTrack.isEmpty ())
                        return SLMkIIIColorManager.SLMKIII_BLACK;
                    return this.model.getCursorTrack ().isPinned () ? SLMkIIIColorManager.SLMKIII_RED : SLMkIIIColorManager.SLMKIII_RED_HALF;
                case ROW1_3:
                    if (selectedTrack.isEmpty ())
                        return SLMkIIIColorManager.SLMKIII_BLACK;
                    return SLMkIIIColorManager.SLMKIII_RED_HALF;
                case ROW1_4:
                    return SLMkIIIColorManager.SLMKIII_BLACK;

                default:
                    return SLMkIIIColorManager.SLMKIII_RED_HALF;
            }
        }

        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();

        if (this.surface.isLongPressed (ButtonID.ARROW_DOWN))
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            return modeManager.isActive (MODES[index]) ? SLMkIIIColorManager.SLMKIII_GREEN : SLMkIIIColorManager.SLMKIII_GREEN_HALF;
        }

        final ITrack t = tb.getItem (index);

        if (t.doesExist ())
        {
            if (t.isSelected ())
            {
                final String colorIndex = DAWColor.getColorID (t.getColor ());
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
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();

        if (this.surface.isShiftPressed ())
        {
            this.drawRow4Shifted (d, selectedTrack.isPresent () ? selectedTrack.get () : EmptyTrack.INSTANCE);
            return;
        }

        if (this.surface.isLongPressed (ButtonID.ARROW_DOWN))
        {
            final ModeManager modeManager = this.surface.getModeManager ();
            for (int i = 0; i < 8; i++)
            {
                d.setCell (3, i, MODE_MENU[i]);
                d.setPropertyColor (i, 2, SLMkIIIColorManager.SLMKIII_GREEN);
                d.setPropertyValue (i, 1, modeManager.isActive (MODES[i]) ? 1 : 0);
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
                final String colorIndex = DAWColor.getColorID (t.getColor ());
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
            d.setPropertyValue (1, 1, this.model.getCursorTrack ().isPinned () ? 1 : 0);

            d.setCell (3, 2, "Color");
            d.setPropertyColor (2, 2, SLMkIIIColorManager.SLMKIII_RED);
            d.setPropertyValue (2, 1, 0);
        }

        d.setCell (3, 3, "");
        d.setPropertyColor (3, 2, SLMkIIIColorManager.SLMKIII_BLACK);
        d.setPropertyValue (3, 1, 0);

        d.setCell (3, 4, "New Clip");
        d.setPropertyColor (4, 2, SLMkIIIColorManager.SLMKIII_RED);
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


    protected void setColumnColors (final SLMkIIIDisplay display, final int column, final ITrack track, final int knobColorIndex)
    {
        int color = track.doesExist () ? knobColorIndex : SLMkIIIColorManager.SLMKIII_BLACK;
        display.setPropertyColor (column, 1, track.isActivated () ? color : SLMkIIIColorManager.SLMKIII_DARK_GREY);
        if (track.doesExist ())
        {
            if (track.isActivated ())
            {
                final String colorIndex = DAWColor.getColorID (track.getColor ());
                color = this.model.getColorManager ().getColorIndex (colorIndex);
            }
            else
                color = SLMkIIIColorManager.SLMKIII_DARK_GREY;
        }
        display.setPropertyColor (column, 0, color);
    }
}