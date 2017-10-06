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
        "Up"
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
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DUPLICATE);
                tb.duplicate (index);
                return;
            }
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_CLIP_STOP);
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
                        modeManager.setActiveMode (Modes.MODE_SEND1.intValue() + (config.isSendsAreToggled() ? 4 : 0));
                }
                break;

            case 7:
                if (!this.model.isEffectTrackBankActive ())
                    this.model.getTrackBank ().selectParent ();
                break;

            default:
                if (!this.model.isEffectTrackBankActive ())
                {
                    final int sendOffset = config.isSendsAreToggled () ? 0 : 4;
                    final int sendIndex = index - sendOffset;
                    final EffectTrackBankProxy fxTrackBank = this.model.getEffectTrackBank ();
                    if (fxTrackBank != null && fxTrackBank.getTrack (sendIndex).doesExist ())
                    {
                        final Integer si = Modes.MODE_SEND1.intValue() + sendIndex;
                        if (modeManager.isActiveMode (si))
                            modeManager.setActiveMode (Modes.MODE_TRACK);
                        else
                            modeManager.setActiveMode (si);
                    }
                }
                break;
        }

        config.setCurrentMixMode (modeManager.getActiveModeId ());
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
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();
        if (this.isPush2)
        {
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                for (int i = 0; i < 8; i++)
                {
                    final TrackData track = tb.getTrack (i);
                    this.surface.updateButton (102 + i, track.doesExist () && track.isPlaying () ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH2_COLOR_BLACK);
                }
                return;
            }

            if (config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
            {
                final boolean muteState = config.isMuteState ();
                for (int i = 0; i < 8; i++)
                    this.surface.updateButton (102 + i, this.getTrackStateColor (muteState, tb.getTrack (i)));
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
            this.surface.updateButton (109, PushColors.PUSH2_COLOR2_WHITE);
            return;
        }

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


    protected int getTrackStateColor (final boolean muteState, final TrackData t)
    {
        if (!t.doesExist ())
            return PushColors.PUSH2_COLOR_BLACK;

        if (muteState)
        {
            if (t.isMute ())
                return PushColors.PUSH2_COLOR2_AMBER_LO;
        }
        else if (t.isSolo ())
            return PushColors.PUSH2_COLOR2_YELLOW_HI;

        return PushColors.PUSH2_COLOR_BLACK;
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

        if (track.isRecArm())
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

            // The menu item
            String topMenu;
            boolean isTopMenuOn;
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                topMenu = t.doesExist () ? "Stop Clip" : "";
                isTopMenuOn = t.isPlaying ();
            }
            else if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                topMenu = t.doesExist () ? "Mute" : "";
                isTopMenuOn = t.isMute ();
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                topMenu = t.doesExist () ? "Solo" : "";
                isTopMenuOn = t.isSolo ();
            }
            else
            {
                topMenu = this.menu[i];
                isTopMenuOn = i == selectedMenu - 1 || i == 7;
            }

            message.addChannelElement (selectedMenu, topMenu, isTopMenuOn, t.doesExist () ? t.getName () : "", t.getType (), tb.getTrackColorEntry (i), t.isSelected (), valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), isVolume && this.isKnobTouched[i] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), isPan && this.isKnobTouched[i] ? t.getPanStr () : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? t.getVu () : 0), t.isMute (), t.isSolo (), t.isRecArm(), "A".equals (t.getCrossfadeMode ()) ? 0 : "B".equals (t.getCrossfadeMode ()) ? 2 : 1);
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

        for (int i = 0; i < 3; i++)
            this.menu[4 + i] = fxTrackBank.getTrack (sendOffset + i).getName ();
        this.menu[3] = config.isSendsAreToggled () ? "Sends 5-8" : "Sends 1-4";
    }
}