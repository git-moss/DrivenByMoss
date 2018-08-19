// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.mode.track;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ISendBank;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.graphics.display.DisplayModel;
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
    public TrackMode (final Kontrol2ControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
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
        }

        final ISendBank sendBank = selectedTrack.getSendBank ();
        final Kontrol2Configuration config = this.surface.getConfiguration ();
        switch (index)
        {
            case 2:
                this.changeCrossfader (value, selectedTrack);
                break;
            case 3:
                break;
            default:
                // TODO
                // final int sendOffset = config.isSendsAreToggled () ? 0 : 4;
                // sendBank.getItem (index - sendOffset).changeValue (value);
                break;
        }
    }


    private void changeCrossfader (final int value, final ITrack selectedTrack)
    {
        if (this.increaseKnobMovement ())
            selectedTrack.changeCrossfadeModeAsNumber (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        final ITrack selectedTrack = this.model.getSelectedTrack ();
        if (selectedTrack == null)
            return;

        this.isKnobTouched[index] = isTouched;

        final ISendBank sendBank = selectedTrack.getSendBank ();
        final Kontrol2Configuration config = this.surface.getConfiguration ();
        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                // TODO this.surface.setButtonConsumed (Kontrol2ControlSurface.PUSH_BUTTON_DELETE);
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
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ITrack selectedTrack = tb.getSelectedItem ();

        // Get the index at which to draw the Sends element
        final int selectedIndex = selectedTrack == null ? -1 : selectedTrack.getIndex ();
        int sendsIndex = selectedTrack == null || this.model.isEffectTrackBankActive () ? -1 : selectedTrack.getIndex () + 1;
        if (sendsIndex == 8)
            sendsIndex = 6;

        this.updateMenuItems (0);

        final Kontrol2Configuration config = this.surface.getConfiguration ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final boolean displayCrossfader = config.isDisplayCrossfader ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack t = tb.getItem (i);

            // The menu item
            final Pair<String, Boolean> pair = this.menu.get (i);
            final String topMenu = pair.getKey ();
            final boolean topMenuSelected = pair.getValue ().booleanValue ();

            // Channel info
            final String bottomMenu = t.doesExist () ? t.getName () : "";
            final double [] bottomMenuColor = t.getColor ();
            final boolean isBottomMenuOn = t.isSelected ();

            final IValueChanger valueChanger = this.model.getValueChanger ();
            if (t.isSelected ())
            {
                final int crossfadeMode = displayCrossfader ? t.getCrossfadeModeAsNumber () : -1;
                final boolean enableVUMeters = config.isEnableVUMeters ();
                final int vuR = valueChanger.toDisplayValue (enableVUMeters ? t.getVuRight () : 0);
                final int vuL = valueChanger.toDisplayValue (enableVUMeters ? t.getVuLeft () : 0);
                message.addChannelElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, valueChanger.toDisplayValue (t.getVolume ()), valueChanger.toDisplayValue (t.getModulatedVolume ()), this.isKnobTouched[0] ? t.getVolumeStr (8) : "", valueChanger.toDisplayValue (t.getPan ()), valueChanger.toDisplayValue (t.getModulatedPan ()), this.isKnobTouched[1] ? t.getPanStr (8) : "", vuL, vuR, t.isMute (), t.isSolo (), t.isRecArm (), crossfadeMode);
            }
            else if (sendsIndex == i)
            {
                final ITrackBank fxTrackBank = this.model.getEffectTrackBank ();
                final ITrack selTrack = tb.getItem (selectedIndex);
                final String [] sendName = new String [4];
                final String [] valueStr = new String [4];
                final int [] value = new int [4];
                final int [] modulatedValue = new int [4];
                final boolean [] selected = new boolean [4];
                for (int j = 0; j < 4; j++)
                {
                    final int sendOffset = 0; // TODO config.isSendsAreToggled () ? 4 : 0;
                    final int sendPos = sendOffset + j;
                    selected[j] = true;
                    sendName[j] = "";
                    valueStr[j] = "";
                    value[j] = 0;
                    if (selTrack == null)
                        continue;
                    final ISend send = selTrack.getSendBank ().getItem (sendPos);
                    if (send == null)
                        continue;
                    sendName[j] = fxTrackBank == null ? send.getName () : fxTrackBank.getItem (sendPos).getName ();
                    valueStr[j] = send.doesExist () && this.isKnobTouched[4 + j] ? send.getDisplayedValue (8) : "";
                    value[j] = valueChanger.toDisplayValue (send.doesExist () ? send.getValue () : 0);
                    modulatedValue[j] = valueChanger.toDisplayValue (send.doesExist () ? send.getModulatedValue () : 0);
                }
                message.addSendsElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn, sendName, valueStr, value, modulatedValue, selected, true);
            }
            else
                message.addChannelSelectorElement (topMenu, topMenuSelected, bottomMenu, t.getType (), bottomMenuColor, isBottomMenuOn);
        }
        message.send ();
    }
}