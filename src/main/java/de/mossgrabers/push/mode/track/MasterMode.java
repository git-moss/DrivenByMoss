// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode.track;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.ApplicationProxy;
import de.mossgrabers.framework.daw.MasterTrackProxy;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;
import de.mossgrabers.push.mode.BaseMode;


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
    public MasterMode (final PushControlSurface surface, final Model model, final boolean isTemporary)
    {
        super (surface, model);
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
    public void onValueKnob (final int index, final int value)
    {
        if (index == 0)
            this.model.getMasterTrack ().changeVolume (value);
        else if (index == 1)
            this.model.getMasterTrack ().changePan (value);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;

        if (isTouched)
        {
            if (this.surface.isDeletePressed ())
            {
                this.surface.setButtonConsumed (this.surface.getDeleteButtonId ());
                if (index == 0)
                    this.model.getMasterTrack ().resetVolume ();
                else if (index == 1)
                    this.model.getMasterTrack ().resetPan ();
                return;
            }

            if (index == 0)
                this.surface.getDisplay ().notify ("Volume: " + this.model.getMasterTrack ().getVolumeStr (8));
            else if (index == 1)
                this.surface.getDisplay ().notify ("Pan: " + this.model.getMasterTrack ().getPanStr (8));
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
        final ApplicationProxy application = this.model.getApplication ();
        final String projectName = application.getProjectName ();
        final MasterTrackProxy master = this.model.getMasterTrack ();
        d.setRow (0, MasterMode.PARAM_NAMES).setCell (1, 0, master.getVolumeStr (8)).setCell (1, 1, master.getPanStr (8));
        d.clearCell (1, 2).clearCell (1, 3).setBlock (1, 2, "Audio Engine").setBlock (1, 3, projectName).done (1);
        d.setCell (2, 0, this.surface.getConfiguration ().isEnableVUMeters () ? master.getVu () : master.getVolume (), Format.FORMAT_VALUE);
        d.setCell (2, 1, master.getPan (), Format.FORMAT_PAN).clearCell (2, 2).clearCell (2, 3).clearCell (2, 4).clearCell (2, 5).clearCell (2, 6).clearCell (2, 7).done (2);
        d.setCell (3, 0, master.getName ()).clearCell (3, 1).clearCell (3, 2).clearCell (3, 3).setCell (3, 4, application.isEngineActive () ? "Turn off" : "Turn on");
        d.clearCell (3, 5).setCell (3, 6, "Previous").setCell (3, 7, "Next").done (3);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final MasterTrackProxy master = this.model.getMasterTrack ();
        final ApplicationProxy application = this.model.getApplication ();
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final DisplayMessage message = ((PushDisplay) this.surface.getDisplay ()).createMessage ();

        message.addChannelElement ("Volume", false, master.getName (), "master", master.getColor (), master.isSelected (), valueChanger.toDisplayValue (master.getVolume ()), valueChanger.toDisplayValue (master.getModulatedVolume ()), this.isKnobTouched[0] ? master.getVolumeStr (8) : "", valueChanger.toDisplayValue (master.getPan ()), valueChanger.toDisplayValue (master.getModulatedPan ()), this.isKnobTouched[1] ? master.getPanStr (8) : "", valueChanger.toDisplayValue (this.surface.getConfiguration ().isEnableVUMeters () ? master.getVu () : 0), master.isMute (), master.isSolo (), master.isRecArm(), 0);

        for (int i = 1; i < 4; i++)
        {
            message.addChannelSelectorElement (i == 1 ? "Pan" : "", false, "", "", new double []
            {
                0.0,
                0.0,
                0.0
            }, false);
        }

        message.addOptionElement ("", "", false, "Audio Engine", application.isEngineActive () ? "Turn off" : "Turn on", false, false);
        message.addOptionElement ("", "", false, "", "", false, false);
        message.addOptionElement ("Project:", "", false, application.getProjectName (), "Previous", false, false);
        message.addOptionElement ("", "", false, "", "Next", false, false);

        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final ApplicationProxy application = this.model.getApplication ();
        switch (index)
        {
            case 0:
                this.model.getMasterTrack ().toggleArm ();
                break;

            case 4:
                application.setEngineActive (!application.isEngineActive ());
                break;

            case 6:
                application.previousProject ();
                break;

            case 7:
                application.nextProject ();
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

        final MasterTrackProxy master = this.model.getMasterTrack ();
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

        final MasterTrackProxy master = this.model.getMasterTrack ();

        int color = off;
        if (muteState)
        {
            if (!master.isMute ())
                color = this.isPush2 ? PushColors.PUSH2_COLOR2_YELLOW_HI : PushColors.PUSH1_COLOR2_YELLOW_HI;
        }
        else
            color = master.isSolo () ? this.isPush2 ? PushColors.PUSH2_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_BLUE_HI : this.isPush2 ? PushColors.PUSH2_COLOR2_GREY_LO : PushColors.PUSH1_COLOR2_GREY_LO;

        this.surface.updateButton (102, color);
        for (int i = 1; i < 8; i++)
            this.surface.updateButton (102 + i, off);
    }


    private int getTrackButtonColor ()
    {
        final MasterTrackProxy track = this.model.getMasterTrack ();
        if (!track.isActivated ())
            return this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        if (track.isRecArm())
            return this.isPush2 ? PushColors.PUSH2_COLOR_RED_HI : PushColors.PUSH1_COLOR_RED_HI;
        return this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI;
    }


    private void setActive (final boolean enable)
    {
        final MasterTrackProxy mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (enable);
        mt.setPanIndication (enable);
    }
}