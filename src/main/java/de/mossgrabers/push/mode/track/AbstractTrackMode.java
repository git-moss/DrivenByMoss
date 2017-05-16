// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.EffectTrackBankProxy;
import de.mossgrabers.framework.daw.TrackBankProxy;
import de.mossgrabers.framework.daw.data.TrackData;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.mode.BaseMode;
import de.mossgrabers.push.mode.Modes;


/**
 * Abstract base mode for all track modes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public abstract class AbstractTrackMode extends BaseMode
{
    protected final String [] menu =
    {
            "Volume",
            "Pan",
            "Crossfader",
            "Sends 1-4",
            "Send 1",
            "Send 2",
            "Send 3",
            "Send 4"
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AbstractTrackMode (final PushControlSurface surface, final Model model)
    {
        super (surface, model);
        this.isTemporary = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();

        if (event == ButtonEvent.UP)
        {
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_DUPLICATE))
            {
                tb.duplicate (index);
                return;
            }
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                tb.stop (index);
                return;
            }
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_RECORD))
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_RECORD);
                tb.toggleArm (index);
                return;
            }

            final TrackData selTrack = tb.getSelectedTrack ();
            if (selTrack != null && selTrack.getIndex () == index)
            {
                // If it is a group display child channels of group, otherwise jump into device
                // mode
                if (selTrack.isGroup () && tb instanceof TrackBankProxy)
                    ((TrackBankProxy) tb).selectChildren ();
                else
                    this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_DEVICE, ButtonEvent.DOWN);
            }
            else
            {
                tb.select (index);
                tb.makeVisible (index);
            }
            return;
        }

        // LONG press, go out of group
        if (!this.model.isEffectTrackBankActive ())
        {
            this.model.getTrackBank ().selectParent ();
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_ROW1_1 + index);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();

        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
        {
            tb.stop (index);
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        if (!this.isPush2 || config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
        {
            if (config.isMuteState ())
                tb.toggleMute (index);
            else
                tb.toggleSolo (index);
            return;
        }

        final ModeManager modeManager = this.surface.getModeManager ();
        switch (index)
        {
            case 0:
                if (modeManager.isActiveMode (Modes.MODE_VOLUME))
                    modeManager.setActiveMode (Modes.MODE_TRACK);
                else
                    modeManager.setActiveMode (Modes.MODE_VOLUME);
                break;

            case 1:
                if (modeManager.isActiveMode (Modes.MODE_PAN))
                    modeManager.setActiveMode (Modes.MODE_TRACK);
                else
                    modeManager.setActiveMode (Modes.MODE_PAN);
                break;

            case 2:
                if (modeManager.isActiveMode (Modes.MODE_CROSSFADER))
                    modeManager.setActiveMode (Modes.MODE_TRACK);
                else
                    modeManager.setActiveMode (Modes.MODE_CROSSFADER);
                break;

            case 3:
                if (!this.model.isEffectTrackBankActive ())
                {
                    // Check if there are more than 4 FX channels
                    if (!config.isSendsAreToggled ())
                    {
                        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                        if (fxTrackBank == null || !fxTrackBank.getTrack (4).doesExist ())
                            return;
                    }
                    config.setSendsAreToggled (!config.isSendsAreToggled ());

                    if (!modeManager.isActiveMode (Modes.MODE_TRACK))
                        modeManager.setActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + (config.isSendsAreToggled () ? 4 : 0)));
                }
                break;

            default:
                if (!this.model.isEffectTrackBankActive ())
                {
                    final int sendOffset = config.isSendsAreToggled () ? 0 : 4;
                    final int sendIndex = index - sendOffset;
                    final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                    if (fxTrackBank != null && fxTrackBank.getTrack (sendIndex).doesExist ())
                    {
                        final Integer si = Integer.valueOf (Modes.MODE_SEND1.intValue () + sendIndex);
                        if (modeManager.isActiveMode (si))
                            modeManager.setActiveMode (Modes.MODE_TRACK);
                        else
                            modeManager.setActiveMode (si);
                    }
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        // Light up selection and record buttons
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (20 + i, this.getTrackButtonColor (tb.getTrack (i)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        if (this.isPush2)
        {
            if (config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
            {
                final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
                final boolean muteState = config.isMuteState ();
                for (int i = 0; i < 8; i++)
                {
                    final TrackData t = tb.getTrack (i);
                    int color = PushColors.PUSH2_COLOR_BLACK;
                    if (t.doesExist ())
                    {
                        if (muteState)
                        {
                            if (t.isMute ())
                                color = PushColors.PUSH2_COLOR2_AMBER_LO;
                        }
                        else if (t.isSolo ())
                            color = PushColors.PUSH2_COLOR2_YELLOW_HI;
                    }

                    this.surface.updateButton (102 + i, color);
                }
                return;
            }

            final ModeManager modeManager = this.surface.getModeManager ();
            this.surface.updateButton (102, modeManager.isActiveMode (Modes.MODE_VOLUME) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (103, modeManager.isActiveMode (Modes.MODE_PAN) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (104, modeManager.isActiveMode (Modes.MODE_CROSSFADER) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (105, PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (106, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_SEND5 : Modes.MODE_SEND1) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (107, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_SEND6 : Modes.MODE_SEND2) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (108, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_SEND7 : Modes.MODE_SEND3) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (109, modeManager.isActiveMode (config.isSendsAreToggled () ? Modes.MODE_SEND8 : Modes.MODE_SEND4) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            return;
        }

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final boolean muteState = config.isMuteState ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (i);

            int color = PushColors.PUSH1_COLOR_BLACK;
            if (t.doesExist ())
            {
                if (muteState)
                {
                    if (!t.isMute ())
                        color = PushColors.PUSH1_COLOR2_YELLOW_HI;
                }
                else
                    color = t.isSolo () ? PushColors.PUSH1_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_GREY_LO;
            }

            this.surface.updateButton (102 + i, color);
        }
    }


    protected void drawRow4 ()
    {
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selTrack = tb.getSelectedTrack ();

        // Format track names
        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        final Display d = this.surface.getDisplay ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final TrackData t = tb.getTrack (i);
            final String n = this.optimizeName (t.getName (), isSel ? 7 : 8);
            d.setCell (3, i, isSel ? PushDisplay.RIGHT_ARROW + n : n);
        }
        d.done (3);
    }


    protected int getTrackButtonColor (final TrackData track)
    {
        if (!track.doesExist () || !track.isActivated ())
            return this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;

        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        final TrackData selTrack = tb.getSelectedTrack ();
        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        final boolean isSel = track.getIndex () == selIndex;

        if (track.isRecarm ())
            return isSel ? this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI : this.isPush2 ? PushColors.PUSH2_COLOR_RED_LO : PushColors.PUSH1_COLOR_RED_LO;

        return isSel ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO;
    }

    // Push 2


    // Called from sub-classes
    protected void updateChannelDisplay (final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateTrackMenu ();

        final PushConfiguration config = this.surface.getConfiguration ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final TrackData t = tb.getTrack (i);

            message.addByte (selectedMenu);

            // The menu item
            if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                message.addString (t.doesExist () ? "Mute" : "");
                message.addBoolean (t.isMute ());
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                message.addString (t.doesExist () ? "Solo" : "");
                message.addBoolean (t.isSolo ());
            }
            else
            {
                message.addString (this.menu[i]);
                message.addBoolean (i == selectedMenu - 1);
            }

            // Channel info
            message.addString (t.doesExist () ? t.getName () : "");
            message.addString (t.getType ());
            message.addColor (tb.getTrackColorEntry (i));
            message.addByte (t.isSelected () ? 1 : 0);
            message.addInteger (valueChanger.toDisplayValue (t.getVolume ()));
            message.addInteger (valueChanger.toDisplayValue (t.getModulatedVolume ()));
            message.addString (isVolume && this.isKnobTouched[i] ? t.getVolumeStr (8) : "");
            message.addInteger (valueChanger.toDisplayValue (t.getPan ()));
            message.addInteger (valueChanger.toDisplayValue (t.getModulatedPan ()));
            message.addString (isPan && this.isKnobTouched[i] ? t.getPanStr () : "");
            message.addInteger (valueChanger.toDisplayValue (config.isEnableVUMeters () ? t.getVu () : 0));
            message.addBoolean (t.isMute ());
            message.addBoolean (t.isSolo ());
            message.addBoolean (t.isRecarm ());
            message.addByte ("A".equals (t.getCrossfadeMode ()) ? 0 : "B".equals (t.getCrossfadeMode ()) ? 2 : 1);
        }

        message.send ();
    }


    protected void updateTrackMenu ()
    {
        final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        if (this.model.isEffectTrackBankActive ())
        {
            // No sends for FX tracks
            for (int i = 3; i < 8; i++)
                this.menu[i] = "";
            return;
        }

        for (int i = 0; i < 4; i++)
            this.menu[4 + i] = fxTrackBank.getTrack (sendOffset + i).getName ();
        this.menu[3] = config.isSendsAreToggled () ? "Sends 5-8" : "Sends 1-4";
    }
}