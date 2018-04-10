// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;
import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.DisplayMessage;
import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.controller.PushDisplay;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.mode.Modes;


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
    public AbstractTrackMode (final PushControlSurface surface, final IModel model)
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

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getTrack (index);

        if (event == ButtonEvent.UP)
        {
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_DUPLICATE))
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DUPLICATE);
                track.duplicate ();
                return;
            }

            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_CLIP_STOP);
                track.stop ();
                return;
            }

            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_RECORD))
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_RECORD);
                track.toggleRecArm ();
                return;
            }

            final ITrack selTrack = tb.getSelectedTrack ();
            if (selTrack != null && selTrack.getIndex () == index)
            {
                // If it is a group display child channels of group, otherwise jump into device
                // mode
                if (selTrack.isGroup () && tb instanceof ITrackBank)
                    ((ITrackBank) tb).selectChildren ();
                else
                    this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_DEVICE, ButtonEvent.DOWN);
            }
            else
                track.selectAndMakeVisible ();
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

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getTrack (index);
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
        {
            track.stop ();
            return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        if (!this.isPush2 || config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
        {
            if (config.isMuteState ())
                track.toggleMute ();
            else
                track.toggleSolo ();
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
                if (config.isDisplayCrossfader ())
                {
                    if (modeManager.isActiveMode (Modes.MODE_CROSSFADER))
                        modeManager.setActiveMode (Modes.MODE_TRACK);
                    else
                        modeManager.setActiveMode (Modes.MODE_CROSSFADER);
                }
                break;

            case 3:
                if (!this.model.isEffectTrackBankActive ())
                {
                    config.setSendsAreToggled (!config.isSendsAreToggled ());
                    if (!modeManager.isActiveMode (Modes.MODE_TRACK))
                        modeManager.setActiveMode (Integer.valueOf (Modes.MODE_SEND1.intValue () + (config.isSendsAreToggled () ? 4 : 0)));
                }
                break;

            case 7:
                if (!this.model.isEffectTrackBankActive ())
                    this.model.getTrackBank ().selectParent ();
                break;

            default:
                final int sendIndex = index - (config.isSendsAreToggled () ? 0 : 4);
                if (tb.canEditSend (sendIndex))
                {
                    final Integer si = Integer.valueOf (Modes.MODE_SEND1.intValue () + sendIndex);
                    modeManager.setActiveMode (modeManager.isActiveMode (si) ? Modes.MODE_TRACK : si);
                }
                break;
        }

        config.setDebugMode (modeManager.getActiveModeId ());
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        // Light up selection and record buttons
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (20 + i, this.getTrackButtonColor (tb.getTrack (i)));
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        if (this.isPush2)
        {
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                for (int i = 0; i < 8; i++)
                {
                    final ITrack track = tb.getTrack (i);
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
            final boolean sendsAreToggled = config.isSendsAreToggled ();
            this.surface.updateButton (106, modeManager.isActiveMode (sendsAreToggled ? Modes.MODE_SEND5 : Modes.MODE_SEND1) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (107, modeManager.isActiveMode (sendsAreToggled ? Modes.MODE_SEND6 : Modes.MODE_SEND2) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (108, modeManager.isActiveMode (sendsAreToggled ? Modes.MODE_SEND7 : Modes.MODE_SEND3) ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            this.surface.updateButton (109, tb instanceof ITrackBank && ((ITrackBank) tb).hasParent () ? PushColors.PUSH2_COLOR2_WHITE : PushColors.PUSH2_COLOR_BLACK);
            return;
        }

        final boolean muteState = config.isMuteState ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getTrack (i);

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


    protected int getTrackStateColor (final boolean muteState, final ITrack t)
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
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selTrack = tb.getSelectedTrack ();

        // Format track names
        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        final Display d = this.surface.getDisplay ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final ITrack t = tb.getTrack (i);
            final String n = StringUtils.shortenAndFixASCII (t.getName (), isSel ? 7 : 8);
            d.setCell (3, i, isSel ? PushDisplay.RIGHT_ARROW + n : n);
        }
        d.done (3);
    }


    protected int getTrackButtonColor (final ITrack track)
    {
        if (!track.doesExist () || !track.isActivated ())
            return this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selTrack = tb.getSelectedTrack ();
        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        final boolean isSel = track.getIndex () == selIndex;

        if (track.isRecArm ())
            return isSel ? this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI : this.isPush2 ? PushColors.PUSH2_COLOR_RED_LO : PushColors.PUSH1_COLOR_RED_LO;

        return isSel ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO;
    }

    // Push 2


    // Called from sub-classes
    protected void updateChannelDisplay (final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateTrackMenu ();

        final PushConfiguration config = this.surface.getConfiguration ();
        final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = display.createMessage ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final boolean displayCrossfader = config.isDisplayCrossfader ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getTrack (i);

            // The menu item
            String topMenu;
            boolean isTopMenuOn;
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP) && this.model.getHost ().hasClips ())
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
                isTopMenuOn = i == selectedMenu - 1 || i == 7 && tb instanceof ITrackBank && ((ITrackBank) tb).hasParent ();
            }

            final int crossfadeMode = displayCrossfader ? t.getCrossfadeModeAsNumber () : -1;
            message.addChannelElement (selectedMenu, topMenu, isTopMenuOn, t.doesExist () ? t.getName (12) : "", t.getType (), t.getColor (), t.isSelected (), valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), isVolume && this.isKnobTouched[i] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), isPan && this.isKnobTouched[i] ? t.getPanStr () : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? t.getVu () : 0), t.isMute (), t.isSolo (), t.isRecArm (), crossfadeMode);
        }

        display.send (message);
    }


    protected void updateTrackMenu ()
    {
        final PushConfiguration config = this.surface.getConfiguration ();
        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
        if (this.model.isEffectTrackBankActive ())
        {
            // No sends for FX tracks
            for (int i = 3; i < 7; i++)
                this.menu[i] = " ";
            return;
        }

        this.menu[2] = config.isDisplayCrossfader () ? "Crossfader" : " ";

        final IChannelBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 3; i++)
        {
            this.menu[4 + i] = tb.getEditSendName (sendOffset + i);
            if (this.menu[4 + i].isEmpty ())
                this.menu[4 + i] = " ";
        }
        this.menu[3] = config.isSendsAreToggled () ? "Sends 5-8" : "Sends 1-4";
        this.menu[7] = tb instanceof ITrackBank && ((ITrackBank) tb).hasParent () ? "Up" : " ";
    }
}