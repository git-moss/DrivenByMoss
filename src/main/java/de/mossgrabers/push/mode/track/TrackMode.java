// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Mode for editing a track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackMode extends AbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TrackMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            case 0:
                tb.changeVolume (selectedTrack.getIndex (), value);
                return;
            case 1:
                tb.changePan (selectedTrack.getIndex (), value);
                return;
        }

        final PushConfiguration config = this.surface.getConfiguration ();
        if (this.isPush2)
        {
            switch (index)
            {
                case 2:
                    this.changeCrossfader (value, selectedTrack);
                    break;
                case 3:
                    break;
                default:
                    final int sendOffset = config.isSendsAreToggled () ? 0 : 4;
                    ((ITrackBank) tb).changeSend (selectedTrack.getIndex (), index - sendOffset, value);
                    break;
            }
            return;
        }

        switch (index)
        {
            case 2:
                if (config.isDisplayCrossfader ())
                    this.changeCrossfader (value, selectedTrack);
                else
                    ((ITrackBank) tb).changeSend (selectedTrack.getIndex (), 0, value);
                break;
            default:
                ((ITrackBank) tb).changeSend (selectedTrack.getIndex (), index - (config.isDisplayCrossfader () ? 3 : 2), value);
                break;
        }
    }


    private void changeCrossfader (final int value, final ITrack selectedTrack)
    {
        if (this.increaseKnobMovement ())
            this.model.getCurrentTrackBank ().changeCrossfadeModeAsNumber (selectedTrack.getIndex (), value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        this.isKnobTouched[index] = isTouched;

        final PushConfiguration config = this.surface.getConfiguration ();
        if (this.isPush2)
        {
            if (isTouched)
            {
                if (this.surface.isDeletePressed ())
                {
                    this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DELETE);
                    switch (index)
                    {
                        case 0:
                            tb.resetVolume (selectedTrack.getIndex ());
                            break;
                        case 1:
                            tb.resetPan (selectedTrack.getIndex ());
                            break;
                        case 2:
                            tb.setCrossfadeMode (selectedTrack.getIndex (), "AB");
                            break;
                        case 3:
                            // Not used
                            break;
                        default:
                            ((ITrackBank) tb).resetSend (selectedTrack.getIndex (), index - 4);
                            break;
                    }
                    return;
                }

                final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
                switch (index)
                {
                    case 0:
                        display.notify ("Volume: " + selectedTrack.getVolumeStr (8));
                        break;
                    case 1:
                        display.notify ("Pan: " + selectedTrack.getPanStr (8));
                        break;
                    case 2:
                        display.notify ("Crossfader: " + selectedTrack.getCrossfadeMode ());
                        break;
                    case 3:
                        // Not used
                        break;
                    default:
                        final int sendIndex = index - 4;
                        final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                        final String name = fxTrackBank == null ? selectedTrack.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ();
                        if (name.length () > 0)
                            display.notify ("Send " + name + ": " + selectedTrack.getSends ()[sendIndex].getDisplayedValue (8));
                        break;
                }
            }

            switch (index)
            {
                case 0:
                    tb.touchVolume (selectedTrack.getIndex (), isTouched);
                    break;
                case 1:
                    tb.touchPan (selectedTrack.getIndex (), isTouched);
                    break;
                case 2:
                case 3:
                    // Not used
                    break;
                default:
                    final int sendIndex = index - 4;
                    ((ITrackBank) tb).touchSend (selectedTrack.getIndex (), sendIndex, isTouched);
                    break;
            }

            this.checkStopAutomationOnKnobRelease (isTouched);
            return;
        }

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_DELETE);
                switch (index)
                {
                    case 0:
                        tb.resetVolume (selectedTrack.getIndex ());
                        break;
                    case 1:
                        tb.resetPan (selectedTrack.getIndex ());
                        break;
                    case 2:
                        if (config.isDisplayCrossfader ())
                            tb.setCrossfadeMode (selectedTrack.getIndex (), "AB");
                        else
                            ((ITrackBank) tb).resetSend (selectedTrack.getIndex (), 0);
                        break;
                    default:
                        ((ITrackBank) tb).resetSend (selectedTrack.getIndex (), index - (config.isDisplayCrossfader () ? 3 : 2));
                        break;
                }
                return;
            }

            final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
            switch (index)
            {
                case 0:
                    display.notify ("Volume: " + selectedTrack.getVolumeStr (8));
                    break;
                case 1:
                    display.notify ("Pan: " + selectedTrack.getPanStr (8));
                    break;
                case 2:
                    if (config.isDisplayCrossfader ())
                        display.notify ("Crossfader: " + selectedTrack.getCrossfadeMode ());
                    else
                    {
                        final int sendIndex = 0;
                        final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                        final String name = fxTrackBank == null ? selectedTrack.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ();
                        if (name.length () > 0)
                            display.notify ("Send " + name + ": " + selectedTrack.getSends ()[sendIndex].getDisplayedValue (8));
                    }
                    break;
                default:
                    final int sendIndex = index - (config.isDisplayCrossfader () ? 3 : 2);
                    final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                    final String name = fxTrackBank == null ? selectedTrack.getSends ()[sendIndex].getName () : fxTrackBank.getTrack (sendIndex).getName ();
                    if (name.length () > 0)
                        display.notify ("Send " + name + ": " + selectedTrack.getSends ()[sendIndex].getDisplayedValue (8));
                    break;
            }
        }

        switch (index)
        {
            case 0:
                tb.touchVolume (selectedTrack.getIndex (), isTouched);
                break;
            case 1:
                tb.touchPan (selectedTrack.getIndex (), isTouched);
                break;
            case 2:
                if (!config.isDisplayCrossfader ())
                    ((ITrackBank) tb).touchSend (selectedTrack.getIndex (), 0, isTouched);
                break;
            default:
                final int sendIndex = index - (config.isDisplayCrossfader () ? 3 : 2);
                ((ITrackBank) tb).touchSend (selectedTrack.getIndex (), sendIndex, isTouched);
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ().clear ();
        final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        final ITrack t = currentTrackBank.getSelectedTrack ();
        if (t == null)
            d.setRow (1, "                     Please selecta track...                        ").done (0).done (2);
        else
        {
            final PushConfiguration config = this.surface.getConfiguration ();
            d.setCell (0, 0, "Volume").setCell (1, 0, t.getVolumeStr (8)).setCell (2, 0, config.isEnableVUMeters () ? t.getVu () : t.getVolume (), Format.FORMAT_VALUE);
            d.setCell (0, 1, "Pan").setCell (1, 1, t.getPanStr (8)).setCell (2, 1, t.getPan (), Format.FORMAT_PAN);

            int sendStart = 2;
            int sendCount = 6;
            if (config.isDisplayCrossfader ())
            {
                sendStart = 3;
                sendCount = 5;
                final String crossfadeMode = t.getCrossfadeMode ();
                final int upperBound = this.model.getValueChanger ().getUpperBound ();
                d.setCell (0, 2, "Crossfdr").setCell (1, 2, "A".equals (crossfadeMode) ? "A" : "B".equals (crossfadeMode) ? "       B" : "   <> ");
                d.setCell (2, 2, "A".equals (crossfadeMode) ? 0 : "B".equals (crossfadeMode) ? upperBound : upperBound / 2, Format.FORMAT_PAN);
            }
            final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
            for (int i = 0; i < sendCount; i++)
            {
                final int pos = sendStart + i;
                if (!isEffectTrackBankActive)
                {
                    final ISend send = t.getSends ()[i];
                    if (send.doesExist ())
                        d.setCell (0, pos, send.getName ()).setCell (1, pos, send.getDisplayedValue (8)).setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                }
            }
            d.done (0).done (1).done (2);
        }

        this.drawRow4 ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();

        // Get the index at which to draw the Sends element
        final int selectedIndex = selectedTrack == null ? -1 : selectedTrack.getIndex ();
        int sendsIndex = selectedTrack == null || this.model.isEffectTrackBankActive () ? -1 : selectedTrack.getIndex () + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateTrackMenu ();

        final PushConfiguration config = this.surface.getConfiguration ();
        final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = display.createMessage ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getTrack (i);

            // The menu item
            String topMenu;
            boolean topMenuSelected;
            if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
            {
                topMenu = t.doesExist () ? "Stop Clip" : "";
                topMenuSelected = t.isPlaying ();
            }
            else if (config.isMuteLongPressed () || config.isMuteSoloLocked () && config.isMuteState ())
            {
                topMenu = t.doesExist () ? "Mute" : "";
                topMenuSelected = t.isMute ();
            }
            else if (config.isSoloLongPressed () || config.isMuteSoloLocked () && config.isSoloState ())
            {
                topMenu = t.doesExist () ? "Solo" : "";
                topMenuSelected = t.isSolo ();
            }
            else
            {
                topMenu = this.menu[i];
                topMenuSelected = i == 7;
            }

            // Channel info
            final String bottomMenu = t.doesExist () ? t.getName () : "";
            final String bottomMenuIcon = t.getType ();
            final double [] bottomMenuColor = tb.getTrackColorEntry (i);
            final boolean isBottomMenuOn = t.isSelected ();

            final ValueChanger valueChanger = this.model.getValueChanger ();
            if (t.isSelected ())
            {
                final int crossfadeMode = "A".equals (t.getCrossfadeMode ()) ? 0 : "B".equals (t.getCrossfadeMode ()) ? 2 : 1;
                message.addChannelElement (topMenu, topMenuSelected, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), this.isKnobTouched[0] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), this.isKnobTouched[1] ? t.getPanStr (8) : "", valueChanger.toDisplayValue (config.isEnableVUMeters () ? t.getVu () : 0), t.isMute (), t.isSolo (), t.isRecArm (), crossfadeMode);
            }
            else if (sendsIndex == i)
            {
                final IChannelBank fxTrackBank = this.model.getEffectTrackBank ();
                final ITrack selTrack = tb.getTrack (selectedIndex);
                final String [] sendName = new String [4];
                final String [] valueStr = new String [4];
                final int [] value = new int [4];
                final int [] modulatedValue = new int [4];
                final boolean [] selected = new boolean [4];
                for (int j = 0; j < 4; j++)
                {
                    final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
                    final int sendPos = sendOffset + j;
                    selected[j] = true;
                    sendName[j] = "";
                    valueStr[j] = "";
                    value[j] = 0;
                    if (selTrack == null)
                        continue;
                    final ISend send = selTrack.getSends ()[sendPos];
                    if (send == null)
                        continue;
                    sendName[j] = fxTrackBank == null ? send.getName () : fxTrackBank.getTrack (sendPos).getName ();
                    valueStr[j] = send.doesExist () && this.isKnobTouched[4 + j] ? send.getDisplayedValue (8) : "";
                    value[j] = valueChanger.toDisplayValue (send.doesExist () ? send.getValue () : 0);
                    modulatedValue[j] = valueChanger.toDisplayValue (send.doesExist () ? send.getModulatedValue () : 0);
                }
                message.addSendsElement (topMenu, topMenuSelected, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn, sendName, valueStr, value, modulatedValue, selected, true);
            }
            else
                message.addChannelSelectorElement (topMenu, topMenuSelected, bottomMenu, bottomMenuIcon, bottomMenuColor, isBottomMenuOn);
        }
        display.send (message);
    }
}