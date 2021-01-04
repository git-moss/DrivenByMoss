// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushColorManager;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.ModeManager;
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
    public AbstractTrackMode (final String name, final PushControlSurface surface, final IModel model)
    {
        super (name, surface, model, model.getCurrentTrackBank ());

        model.addTrackBankObserver (this::switchBanks);

        for (int i = 0; i < 8; i++)
            this.menu.add (new Pair<> (" ", Boolean.FALSE));
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event == ButtonEvent.DOWN)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getItem (index);

        if (event == ButtonEvent.UP)
        {
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

            if (this.surface.isPressed (ButtonID.STOP_CLIP))
            {
                this.surface.setTriggerConsumed (ButtonID.STOP_CLIP);
                track.stop ();
                return;
            }

            if (this.surface.isPressed (ButtonID.RECORD))
            {
                this.surface.setTriggerConsumed (ButtonID.RECORD);
                track.toggleRecArm ();
                return;
            }

            final ITrack selTrack = tb.getSelectedItem ();
            if (selTrack != null && selTrack.getIndex () == index)
            {
                // If it is a group display child channels of group, otherwise jump into device
                // mode
                if (selTrack.isGroup ())
                    selTrack.enter ();
                else
                    this.surface.getButton (ButtonID.DEVICE).trigger (ButtonEvent.DOWN);
            }
            else
                track.select ();
            return;
        }

        // LONG press, go out of group
        if (!this.model.isEffectTrackBankActive ())
        {
            this.model.getTrackBank ().selectParent ();
            this.surface.setTriggerConsumed (ButtonID.get (ButtonID.ROW1_1, index));
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack track = tb.getItem (index);
        if (this.surface.isPressed (ButtonID.STOP_CLIP))
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
                if (modeManager.isActive (Modes.VOLUME))
                    modeManager.setActive (Modes.TRACK);
                else
                    modeManager.setActive (Modes.VOLUME);
                break;

            case 1:
                if (modeManager.isActive (Modes.PAN))
                    modeManager.setActive (Modes.TRACK);
                else
                    modeManager.setActive (Modes.PAN);
                break;

            case 2:
                if (modeManager.isActive (Modes.CROSSFADER))
                    modeManager.setActive (Modes.TRACK);
                else
                    modeManager.setActive (Modes.CROSSFADER);
                break;

            case 3:
                if (!this.model.isEffectTrackBankActive ())
                {
                    config.setSendsAreToggled (!config.isSendsAreToggled ());
                    if (!modeManager.isActive (Modes.TRACK))
                        modeManager.setActive (Modes.get (Modes.SEND1, config.isSendsAreToggled () ? 4 : 0));
                }
                break;

            case 7:
                if (!this.model.isEffectTrackBankActive ())
                {
                    if (this.lastSendIsAccessible ())
                        this.handleSendEffect (config.isSendsAreToggled () ? 7 : 3);
                    else
                        this.model.getTrackBank ().selectParent ();
                }
                break;

            default:
                this.handleSendEffect (index - (config.isSendsAreToggled () ? 0 : 4));
                break;
        }

        config.setDebugMode (modeManager.getActiveID ());
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
        final PushConfiguration config = this.surface.getConfiguration ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final ITrack track = tb.getItem (index);
            if (!track.doesExist () || !track.isActivated ())
                return this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;

            final ITrack cursorTrack = this.model.getCursorTrack ();
            final int selIndex = cursorTrack.doesExist () ? cursorTrack.getIndex () : -1;
            final boolean isSel = track.getIndex () == selIndex;

            if (track.isRecArm ())
                return isSel ? this.isPush2 ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH1_COLOR_RED_HI : this.isPush2 ? PushColorManager.PUSH2_COLOR_RED_LO : PushColorManager.PUSH1_COLOR_RED_LO;

            return isSel ? this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColorManager.PUSH2_COLOR_YELLOW_LO : PushColorManager.PUSH1_COLOR_YELLOW_LO;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final ITrack track = tb.getItem (index);

            if (this.isPush2)
            {
                if (this.surface.isPressed (ButtonID.STOP_CLIP))
                    return track.doesExist () && track.isPlaying () ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH2_COLOR_BLACK;

                if (config.isMuteLongPressed () || config.isSoloLongPressed () || config.isMuteSoloLocked ())
                {
                    final boolean muteState = config.isMuteState ();
                    return this.getTrackStateColor (muteState, track);
                }

                final ModeManager modeManager = this.surface.getModeManager ();
                final boolean sendsAreToggled = config.isSendsAreToggled ();
                switch (index)
                {
                    case 0:
                        return modeManager.isActive (Modes.VOLUME) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 1:
                        return modeManager.isActive (Modes.PAN) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 2:
                        return modeManager.isActive (Modes.CROSSFADER) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 4:
                        final Modes sendMode1 = sendsAreToggled ? Modes.SEND5 : Modes.SEND1;
                        return modeManager.isActive (sendMode1) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 5:
                        final Modes sendMode2 = sendsAreToggled ? Modes.SEND6 : Modes.SEND2;
                        return modeManager.isActive (sendMode2) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 6:
                        final Modes sendMode3 = sendsAreToggled ? Modes.SEND7 : Modes.SEND3;
                        return modeManager.isActive (sendMode3) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    case 7:
                        if (this.lastSendIsAccessible ())
                        {
                            final Modes sendMode4 = sendsAreToggled ? Modes.SEND8 : Modes.SEND4;
                            return modeManager.isActive (sendMode4) ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                        }
                        return tb.hasParent () ? PushColorManager.PUSH2_COLOR2_WHITE : PushColorManager.PUSH2_COLOR_BLACK;
                    default:
                    case 3:
                        return PushColorManager.PUSH2_COLOR_BLACK;
                }
            }

            final boolean muteState = config.isMuteState ();
            if (!track.doesExist ())
                return PushColorManager.PUSH1_COLOR_BLACK;

            if (muteState)
                return track.isMute () ? PushColorManager.PUSH1_COLOR_BLACK : PushColorManager.PUSH1_COLOR2_YELLOW_HI;
            return track.isSolo () ? PushColorManager.PUSH1_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_GREY_LO;
        }

        return super.getButtonColor (buttonID);
    }


    protected int getTrackStateColor (final boolean muteState, final ITrack t)
    {
        if (!t.doesExist ())
            return PushColorManager.PUSH2_COLOR_BLACK;

        if (muteState)
        {
            if (t.isMute ())
                return PushColorManager.PUSH2_COLOR2_AMBER_LO;
        }
        else if (t.isSolo ())
            return PushColorManager.PUSH2_COLOR2_YELLOW_HI;

        return PushColorManager.PUSH2_COLOR_BLACK;
    }


    protected void drawRow4 (final ITextDisplay d)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selTrack = tb.getSelectedItem ();

        // Format track names
        final int selIndex = selTrack == null ? -1 : selTrack.getIndex ();
        for (int i = 0; i < 8; i++)
        {
            final boolean isSel = i == selIndex;
            final ITrack t = tb.getItem (i);
            final String n = StringUtils.shortenAndFixASCII (t.getName (), isSel ? 7 : 8);
            d.setCell (3, i, isSel ? Push1Display.SELECT_ARROW + n : n);
        }
    }

    // Push 2


    // Called from sub-classes
    protected void updateChannelDisplay (final IGraphicDisplay display, final int selectedMenu, final boolean isVolume, final boolean isPan)
    {
        this.updateMenuItems (selectedMenu);

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final boolean displayCrossfader = this.model.getHost ().supports (Capability.HAS_CROSSFADER);
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean isTopMenuOn = pair.getValue ().booleanValue ();
            final int crossfadeMode = displayCrossfader ? t.getCrossfadeModeAsNumber () : -1;
            final boolean enableVUMeters = config.isEnableVUMeters ();
            final int vuR = valueChanger.toDisplayValue (enableVUMeters ? t.getVuRight () : 0);
            final int vuL = valueChanger.toDisplayValue (enableVUMeters ? t.getVuLeft () : 0);
            display.addChannelElement (selectedMenu, topMenu, isTopMenuOn, t.doesExist () ? t.getName (12) : "", t.getType (), t.getColor (), t.isSelected (), valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), isVolume && this.isKnobTouched[i] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), isPan && this.isKnobTouched[i] ? t.getPanStr (8) : "", vuL, vuR, t.isMute (), t.isSolo (), t.isRecArm (), t.isActivated (), crossfadeMode);
        }
    }


    protected void updateMenuItems (final int selectedMenu)
    {
        if (this.surface.isPressed (ButtonID.STOP_CLIP))
        {
            this.updateStopMenu ();
            return;
        }
        final PushConfiguration config = this.surface.getConfiguration ();
        if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            this.updateMuteMenu ();
        else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            this.updateSoloMenu ();
        else
            this.updateTrackMenu (selectedMenu);
    }


    protected void updateStopMenu ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            this.menu.get (i).set (t.doesExist () ? "Stop Clip" : "", Boolean.valueOf (t.isPlaying ()));
        }
    }


    protected void updateMuteMenu ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            this.menu.get (i).set (t.doesExist () ? "Mute" : "", Boolean.valueOf (t.isMute ()));
        }
    }


    protected void updateSoloMenu ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);
            this.menu.get (i).set (t.doesExist () ? "Solo" : "", Boolean.valueOf (t.isSolo ()));
        }
    }


    protected void updateTrackMenu (final int selectedMenu)
    {
        final PushConfiguration config = this.surface.getConfiguration ();

        this.menu.get (0).set ("Volume", Boolean.valueOf (selectedMenu - 1 == 0));
        this.menu.get (1).set ("Pan", Boolean.valueOf (selectedMenu - 1 == 1));
        this.menu.get (2).set (this.model.getHost ().supports (Capability.HAS_CROSSFADER) ? "Crossfader" : " ", Boolean.valueOf (selectedMenu - 1 == 2));

        if (this.model.isEffectTrackBankActive ())
        {
            // No sends for FX tracks
            for (int i = 3; i < 7; i++)
                this.menu.get (i).set (" ", Boolean.FALSE);
            return;
        }

        final boolean sendsAreToggled = config.isSendsAreToggled ();

        this.menu.get (3).set (sendsAreToggled ? "Sends 5-8" : "Sends 1-4", Boolean.valueOf (sendsAreToggled));

        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final int sendOffset = sendsAreToggled ? 4 : 0;
        for (int i = 0; i < 4; i++)
        {
            final String sendName = tb.getEditSendName (sendOffset + i);
            final boolean exists = !sendName.isEmpty ();
            this.menu.get (4 + i).set (exists ? sendName : " ", Boolean.valueOf (exists && 4 + i == selectedMenu - 1));
        }

        if (this.lastSendIsAccessible ())
            return;

        final boolean isUpAvailable = tb.hasParent ();
        this.menu.get (7).set (isUpAvailable ? "Up" : " ", Boolean.valueOf (isUpAvailable));
    }


    /**
     * Check if the 4th/8th send is accessible. This is the case if the current tracks are not
     * inside a group (hence no need to go up), Shift is pressed or the 8th knob is touched.
     *
     * @return True if one of the above described conditions is met
     */
    private boolean lastSendIsAccessible ()
    {
        return this.surface.isShiftPressed () || !this.model.getCurrentTrackBank ().hasParent () || this.isKnobTouched (7);
    }
}