// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.controller.PushColorManager;
import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.controller.ableton.push.mode.BaseMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.constants.Capability;
import de.mossgrabers.framework.daw.data.ICursorTrack;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.empty.EmptyParameter;
import de.mossgrabers.framework.daw.resource.ChannelType;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Mode for editing the parameters of the master track.
 *
 * @author Jürgen Moßgraber
 */
public class MasterMode extends BaseMode<ITrack>
{
    private static final String TAG_VOLUME = "Volume";


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

        final IMasterTrack masterTrack = this.model.getMasterTrack ();
        final IProject project = this.model.getProject ();
        this.setParameterProvider (new FixedParameterProvider (masterTrack.getVolumeParameter (), masterTrack.getPanParameter (), project.getCueVolumeParameter (), project.getCueMixParameter (), EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE));
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.setActive (true);
    }


    /** {@inheritDoc} */
    @Override
    public void onDeactivate ()
    {
        super.onDeactivate ();

        this.setActive (false);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobTouch (final int index, final boolean isTouched)
    {
        this.setTouchedKnob (index, isTouched);

        if (isTouched && this.surface.isDeletePressed ())
        {
            this.surface.setTriggerConsumed (ButtonID.DELETE);

            switch (index)
            {
                case 0:
                    this.model.getMasterTrack ().resetVolume ();
                    break;
                case 1:
                    this.model.getMasterTrack ().resetPan ();
                    break;
                case 2:
                    this.model.getProject ().resetCueVolume ();
                    break;
                case 3:
                    this.model.getProject ().resetCueMix ();
                    break;
                default:
                    // Not used
                    break;
            }
        }

        switch (index)
        {
            case 0:
                this.model.getMasterTrack ().touchVolume (isTouched);
                break;
            case 1:
                this.model.getMasterTrack ().touchPan (isTouched);
                break;
            case 2:
                this.model.getProject ().touchCueVolume (isTouched);
                break;
            case 3:
                this.model.getProject ().touchCueMix (isTouched);
                break;
            default:
                // Not used
                break;
        }

        this.checkStopAutomationOnKnobRelease (isTouched);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IMasterTrack master = this.model.getMasterTrack ();
        final IProject project = this.model.getProject ();
        final boolean canEditCueVolume = this.model.getHost ().supports (Capability.CUE_VOLUME);

        display.setCell (0, 0, TAG_VOLUME).setCell (0, 1, "Pan");
        if (canEditCueVolume)
            display.setCell (0, 2, TAG_VOLUME).setCell (0, 3, "Mix");
        display.setCell (0, 6, "Project:");
        display.setCell (1, 0, master.getVolumeStr (8)).setCell (1, 1, master.getPanStr (8));
        if (canEditCueVolume)
            display.setCell (1, 2, project.getCueVolumeStr (8)).setCell (1, 3, project.getCueMixStr (8));
        display.setBlock (1, 2, "Audio Engine").setBlock (1, 3, this.model.getProject ().getName ());
        display.setCell (2, 0, this.surface.getConfiguration ().isEnableVUMeters () ? master.getVu () : master.getVolume (), Format.FORMAT_VALUE);
        display.setCell (2, 1, master.getPan (), Format.FORMAT_PAN);
        if (canEditCueVolume)
        {
            display.setCell (2, 2, project.getCueVolume (), Format.FORMAT_VALUE);
            display.setCell (2, 3, project.getCueMix (), Format.FORMAT_VALUE);
            display.setCell (3, 0, master.getName ()).setCell (3, 2, "Cue");
        }
        display.setCell (3, 4, this.model.getApplication ().isEngineActive () ? "Active" : "Off");
        display.setCell (3, 6, "Previous").setCell (3, 7, "Next");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IMasterTrack master = this.model.getMasterTrack ();
        final IProject project = this.model.getProject ();

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final boolean enableVUMeters = this.surface.getConfiguration ().isEnableVUMeters ();
        final int vuR = valueChanger.toDisplayValue (enableVUMeters ? master.getVuRight () : 0);
        final int vuL = valueChanger.toDisplayValue (enableVUMeters ? master.getVuLeft () : 0);

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();

        display.addChannelElement (TAG_VOLUME, false, master.getName (), ChannelType.MASTER, master.getColor (), master.isSelected (), valueChanger.toDisplayValue (master.getVolume ()), valueChanger.toDisplayValue (master.getModulatedVolume ()), this.isKnobTouched (0) ? master.getVolumeStr (8) : "", valueChanger.toDisplayValue (master.getPan ()), valueChanger.toDisplayValue (master.getModulatedPan ()), this.isKnobTouched (1) ? master.getPanStr (8) : "", vuL, vuR, master.isMute (), master.isSolo (), master.isRecArm (), master.isActivated (), 0, master.isSelected () && cursorTrack.isPinned ());
        display.addChannelSelectorElement ("Pan", false, "", null, ColorEx.BLACK, false, master.isActivated ());

        if (this.model.getHost ().supports (Capability.CUE_VOLUME))
        {
            display.addChannelElement ("Cue Volume", false, "Cue", ChannelType.MASTER, ColorEx.GRAY, false, valueChanger.toDisplayValue (project.getCueVolume ()), -1, this.isKnobTouched (2) ? project.getCueVolumeStr (8) : "", valueChanger.toDisplayValue (project.getCueMix ()), -1, this.isKnobTouched (3) ? project.getCueMixStr (8) : "", 0, 0, false, false, false, true, 0, false);
            display.addChannelSelectorElement ("Cue Mix", false, "", null, ColorEx.BLACK, false, true);
        }
        else
        {
            display.addOptionElement ("", "", false, "", "", false, false);
            display.addOptionElement ("", "", false, "", "", false, false);
        }

        display.addOptionElement ("", "", false, "Audio Engine", this.model.getApplication ().isEngineActive () ? "Active" : "Off", false, false);
        display.addOptionElement ("", "", false, "", "", false, false);
        display.addOptionElement ("Project:", "", false, this.model.getProject ().getName (), "Previous", false, false);
        display.addOptionElement ("", "", false, "", "Next", false, false);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;

        if (this.surface.isPressed (ButtonID.RECORD))
        {
            this.surface.setTriggerConsumed (ButtonID.RECORD);
            this.model.getMasterTrack ().toggleRecArm ();
            return;
        }

        switch (index)
        {
            case 0:
                this.surface.getButton (ButtonID.DEVICE).trigger (ButtonEvent.DOWN);
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
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final ColorManager colorManager = this.model.getColorManager ();

            final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
            if (index == 0)
                return this.getTrackButtonColor ();
            if (index < 4 || index == 5)
                return colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_OFF);
            if (index > 5)
                return colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON);

            final int red = isPush2 ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH1_COLOR_RED_HI;
            return this.model.getApplication ().isEngineActive () ? colorManager.getColorIndex (AbstractFeatureGroup.BUTTON_COLOR_ON) : red;
        }

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final int off = this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;

            if (this.isPush2 || index > 0)
                return off;

            final boolean muteState = this.surface.getConfiguration ().isMuteState ();
            final IMasterTrack master = this.model.getMasterTrack ();
            if (muteState)
                return master.isMute () ? off : PushColorManager.PUSH1_COLOR2_YELLOW_HI;
            return master.isSolo () ? PushColorManager.PUSH1_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_GREY_LO;
        }

        return super.getButtonColor (buttonID);

    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || this.isPush2 || index > 0)
            return;

        final IMasterTrack master = this.model.getMasterTrack ();
        if (this.surface.getConfiguration ().isMuteState ())
            master.toggleMute ();
        else
            master.toggleSolo ();
    }


    private int getTrackButtonColor ()
    {
        final IMasterTrack track = this.model.getMasterTrack ();
        if (!track.isActivated ())
            return this.isPush2 ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
        if (track.isRecArm ())
            return this.isPush2 ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH1_COLOR_RED_HI;
        return this.isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI;
    }


    private void setActive (final boolean enable)
    {
        final IMasterTrack mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (enable);
        mt.setPanIndication (enable);
    }
}