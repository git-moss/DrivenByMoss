// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.controller.push.controller.Push1Display;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.graphics.canvas.utils.SendData;
import de.mossgrabers.framework.utils.Pair;


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
        super ("Track", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return;

        switch (index)
        {
            case 0:
                selectedTrack.changeVolume (value);
                return;
            case 1:
                selectedTrack.changePan (value);
                return;
            default:
                // Not used
                break;
        }

        final ISendBank sendBank = selectedTrack.getSendBank ();
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
                    sendBank.getItem (index - sendOffset).changeValue (value);
                    break;
            }
            return;
        }

        sendBank.getItem (index - 2).changeValue (value);
    }


    private void changeCrossfader (final int value, final ITrack selectedTrack)
    {
        if (this.increaseKnobMovement ())
            selectedTrack.changeCrossfadeModeAsNumber (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (selectedTrack == null)
            return;

        this.isKnobTouched[index] = isTouched;

        final ISendBank sendBank = selectedTrack.getSendBank ();
        if (this.isPush2)
        {
            if (isTouched && this.surface.isDeletePressed ())
            {
                this.surface.setTriggerConsumed (ButtonID.DELETE);
                switch (index)
                {
                    case 0:
                        selectedTrack.resetVolume ();
                        break;
                    case 1:
                        selectedTrack.resetPan ();
                        break;
                    case 2:
                        selectedTrack.setCrossfadeMode ("AB");
                        break;
                    case 3:
                        // Not used
                        break;
                    default:
                        sendBank.getItem (index - 4).resetValue ();
                        break;
                }
                return;
            }

            switch (index)
            {
                case 0:
                    selectedTrack.touchVolume (isTouched);
                    break;
                case 1:
                    selectedTrack.touchPan (isTouched);
                    break;
                case 2:
                case 3:
                    // Not used
                    break;
                default:
                    final int sendIndex = index - 4;
                    sendBank.getItem (sendIndex).touchValue (isTouched);
                    break;
            }

            this.checkStopAutomationOnKnobRelease (isTouched);
            return;
        }

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);
            switch (index)
            {
                case 0:
                    selectedTrack.resetVolume ();
                    break;
                case 1:
                    selectedTrack.resetPan ();
                    break;
                default:
                    sendBank.getItem (index - 2).resetValue ();
                    break;
            }
            return;
        }

        switch (index)
        {
            case 0:
                selectedTrack.touchVolume (isTouched);
                break;
            case 1:
                selectedTrack.touchPan (isTouched);
                break;
            default:
                final int sendIndex = index - 2;
                sendBank.getItem (sendIndex).touchValue (isTouched);
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack t = tb.getSelectedItem ();
        if (t == null)
            display.setRow (1, "                     Please selecta track...                        ");
        else
        {
            final PushConfiguration config = this.surface.getConfiguration ();
            final int upperBound = this.model.getValueChanger ().getUpperBound ();
            final String volValueStr = config.isEnableVUMeters () ? Push1Display.formatValue (t.getVolume (), t.getVu (), upperBound) : Push1Display.formatValue (t.getVolume (), upperBound);
            display.setCell (0, 0, "Volume").setCell (1, 0, t.getVolumeStr (8)).setCell (2, 0, volValueStr);
            display.setCell (0, 1, "Pan").setCell (1, 1, t.getPanStr (8)).setCell (2, 1, t.getPan (), Format.FORMAT_PAN);

            final int sendStart = 2;
            final int sendCount = 6;
            final boolean isEffectTrackBankActive = this.model.isEffectTrackBankActive ();
            final ISendBank sendBank = t.getSendBank ();
            for (int i = 0; i < sendCount; i++)
            {
                final int pos = sendStart + i;
                if (!isEffectTrackBankActive)
                {
                    final ISend send = sendBank.getItem (i);
                    if (send.doesExist ())
                        display.setCell (0, pos, send.getName ()).setCell (1, pos, send.getDisplayedValue (8)).setCell (2, pos, send.getValue (), Format.FORMAT_VALUE);
                }
            }
        }

        this.drawRow4 (display);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        // Get the index at which to draw the Sends element
        final int selectedIndex = selectedTrack == null ? -1 : selectedTrack.getIndex ();
        int sendsIndex = selectedTrack == null || this.model.isEffectTrackBankActive () ? -1 : selectedTrack.getIndex () + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateMenuItems (0);

        final PushConfiguration config = this.surface.getConfiguration ();
        final boolean displayCrossfader = this.model.getHost ().supports (Capability.HAS_CROSSFADER);
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);

            // The menu item
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean topMenuSelected = pair.getValue ().booleanValue ();

            // Channel info
            final String bottomMenu = t.doesExist () ? t.getName () : "";
            final ColorEx bottomMenuColor = t.getColor ();
            final boolean isBottomMenuOn = t.isSelected ();

            final IValueChanger valueChanger = this.model.getValueChanger ();
            if (t.isSelected ())
            {
                final int crossfadeMode = displayCrossfader ? t.getCrossfadeModeAsNumber () : -1;
                final boolean enableVUMeters = config.isEnableVUMeters ();
                final int vuR = valueChanger.toDisplayValue (enableVUMeters ? t.getVuRight () : 0);
                final int vuL = valueChanger.toDisplayValue (enableVUMeters ? t.getVuLeft () : 0);
                display.addChannelElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), this.isKnobTouched[0] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), this.isKnobTouched[1] ? t.getPanStr (8) : "", vuL, vuR, t.isMute (), t.isSolo (), t.isRecArm (), t.isActivated (), crossfadeMode);
            }
            else if (sendsIndex == i)
            {
                final ITrack selTrack = tb.getItem (selectedIndex);
                final SendData [] sendData = new SendData [4];
                for (int j = 0; j < 4; j++)
                {
                    if (selTrack != null)
                    {
                        final int sendOffset = config.isSendsAreToggled () ? 4 : 0;
                        final int sendPos = sendOffset + j;
                        final ISend send = selTrack.getSendBank ().getItem (sendPos);
                        if (send != null)
                        {
                            final boolean exists = send.doesExist ();
                            sendData[j] = new SendData (send.getName (), exists && this.isKnobTouched[4 + j] ? send.getDisplayedValue (8) : "", valueChanger.toDisplayValue (exists ? send.getValue () : 0), valueChanger.toDisplayValue (exists ? send.getModulatedValue () : 0), true);
                            continue;
                        }
                    }
                    sendData[j] = new SendData ("", "", 0, 0, true);
                }
                display.addSendsElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, sendData, true, selTrack == null || selTrack.isActivated (), t.isActivated ());
            }
            else
                display.addChannelSelectorElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, t.isActivated ());
        }
    }
}