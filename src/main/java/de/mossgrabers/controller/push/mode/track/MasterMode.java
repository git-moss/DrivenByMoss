// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for editing the parameters of the master track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MasterMode extends BaseMode
{
    static final String PARAM_NAMES = "Volume   Pan                                       Project:         ";


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param isTemporary If true treat this mode only as temporary
     */
    public MasterMode (final PushControlSurface surface, final IModel model, final boolean isTemporary)
    {
        super ("Master", surface, model);
        this.isTemporary = isTemporary;
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.setActive (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        this.setActive (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        if (index == 0)
            this.model.getMasterTrack ().changeVolume (value);
        else if (index == 1)
            this.model.getMasterTrack ().changePan (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
            if (index == 0)
                this.model.getMasterTrack ().resetVolume ();
            else if (index == 1)
                this.model.getMasterTrack ().resetPan ();
            return;
        }

        if (index == 0)
            this.model.getMasterTrack ().touchVolume (isTouched);
        else if (index == 1)
            this.model.getMasterTrack ().touchPan (isTouched);

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final Display d = this.surface.getDisplay ();
        final IMasterTrack master = this.model.getMasterTrack ();
        d.setRow (0, MasterMode.PARAM_NAMES).setCell (1, 0, master.getVolumeStr (8)).setCell (1, 1, master.getPanStr (8));
        d.clearCell (1, 2).clearCell (1, 3).setBlock (1, 2, "Audio Engine").setBlock (1, 3, this.model.getProject ().getName ()).done (1);
        d.setCell (2, 0, this.surface.getConfiguration ().isEnableVUMeters () ? master.getVu () : master.getVolume (), Format.FORMAT_VALUE);
        d.setCell (2, 1, master.getPan (), Format.FORMAT_PAN).clearCell (2, 2).clearCell (2, 3).clearCell (2, 4).clearCell (2, 5).clearCell (2, 6).clearCell (2, 7).done (2);
        d.setCell (3, 0, master.getName ()).clearCell (3, 1).clearCell (3, 2).clearCell (3, 3).setCell (3, 4, this.model.getApplication ().isEngineActive () ? "Active" : "Off");
        d.clearCell (3, 5).setCell (3, 6, "Previous").setCell (3, 7, "Next").done (3);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final IMasterTrack master = this.model.getMasterTrack ();
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final DisplayModel message = this.surface.getDisplay ().getModel ();
        final boolean enableVUMeters = this.surface.getConfiguration ().isEnableVUMeters ();
        final int vuR = valueChanger.toDisplayValue (enableVUMeters ? master.getVuRight () : 0);
        final int vuL = valueChanger.toDisplayValue (enableVUMeters ? master.getVuLeft () : 0);
        message.addChannelElement ("Volume", false, master.getName (), ChannelType.MASTER, master.getColor (), master.isSelected (), valueChanger.toDisplayValue (master.getVolume ()), valueChanger.toDisplayValue (master.getModulatedVolume ()), this.isKnobTouched[0] ? master.getVolumeStr (8) : "", valueChanger.toDisplayValue (master.getPan ()), valueChanger.toDisplayValue (master.getModulatedPan ()), this.isKnobTouched[1] ? master.getPanStr (8) : "", vuL, vuR, master.isMute (), master.isSolo (), master.isRecArm (), 0);

        for (int i = 1; i < 4; i++)
        {
            message.addChannelSelectorElement (i == 1 ? "Pan" : "", false, "", null, new double []
            {
                0.0,
                0.0,
                0.0
            }, false);
        }

        message.addOptionElement ("", "", false, "Audio Engine", this.model.getApplication ().isEngineActive () ? "Active" : "Off", false, false);
        message.addOptionElement ("", "", false, "", "", false, false);
        message.addOptionElement ("Project:", "", false, this.model.getProject ().getName (), "Previous", false, false);
        message.addOptionElement ("", "", false, "", "Next", false, false);
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_RECORD))
        {
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_RECORD);
            this.model.getMasterTrack ().toggleRecArm ();
            return;
        }

        switch (index)
        {
            case 0:
                this.surface.getViewManager ().getActiveView ().executeTriggerCommand (Commands.COMMAND_DEVICE, ButtonEvent.DOWN);
                break;

            case 4:
                this.model.getApplication ().toggleEngineActive ();
                break;

            case 6:
                this.model.getProject ().previous ();
                break;

            case 7:
                this.model.getProject ().next ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        this.surface.updateButton (20, this.getTrackButtonColor ());
        for (int i = 1; i < 4; i++)
            this.surface.updateButton (20 + i, AbstractMode.BUTTON_COLOR_OFF);
        final int red = isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI;
        this.surface.updateButton (24, this.model.getApplication ().isEngineActive () ? this.model.getColorManager ().getColor (AbstractMode.BUTTON_COLOR_ON) : red);
        this.surface.updateButton (25, AbstractMode.BUTTON_COLOR_OFF);
        this.surface.updateButton (26, AbstractMode.BUTTON_COLOR_ON);
        this.surface.updateButton (27, AbstractMode.BUTTON_COLOR_ON);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (this.isPush2)
            return;

        if (index > 0)
            return;

        final IMasterTrack master = this.model.getMasterTrack ();
        if (this.surface.getConfiguration ().isMuteState ())
            master.toggleMute ();
        else
            master.toggleSolo ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateSecondRow ()
    {
        final int off = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;

        if (this.isPush2)
        {
            for (int i = 0; i < 8; i++)
                this.surface.updateButton (102 + i, off);
            return;
        }

        final boolean muteState = this.surface.getConfiguration ().isMuteState ();

        final IMasterTrack master = this.model.getMasterTrack ();

        int color = off;
        if (muteState)
        {
            if (!master.isMute ())
                color = PushColors.PUSH1_COLOR2_YELLOW_HI;
        }
        else
            color = master.isSolo () ? PushColors.PUSH1_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_GREY_LO;

        this.surface.updateButton (102, color);
        for (int i = 1; i < 8; i++)
            this.surface.updateButton (102 + i, off);
    }


    private int getTrackButtonColor ()
    {
        final IMasterTrack track = this.model.getMasterTrack ();
        if (!track.isActivated ())
            return this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        if (track.isRecArm ())
            return this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI;
        return this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI;
    }


    private void setActive (final boolean enable)
    {
        final IMasterTrack mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (enable);
        mt.setPanIndication (enable);
    }
}