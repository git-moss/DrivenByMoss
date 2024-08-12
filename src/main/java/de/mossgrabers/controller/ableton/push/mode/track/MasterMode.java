// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2024
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode.track;

import de.mossgrabers.controller.ableton.push.PushConfiguration;
import de.mossgrabers.controller.ableton.push.PushConfiguration.LockState;
import de.mossgrabers.controller.ableton.push.controller.Push1Display;
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
    private static final String     TAG_VOLUME = "Volume";
    private final IMasterTrack      masterTrack;
    private final IProject          project;
    private final PushConfiguration configuration;


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

        this.configuration = this.surface.getConfiguration ();
        this.masterTrack = this.model.getMasterTrack ();
        this.project = this.model.getProject ();
        this.setParameterProvider (new FixedParameterProvider (this.masterTrack.getVolumeParameter (), this.masterTrack.getPanParameter (), this.project.getCueVolumeParameter (), this.project.getCueMixParameter (), EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE, EmptyParameter.INSTANCE));
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
                    this.masterTrack.resetVolume ();
                    break;
                case 1:
                    this.masterTrack.resetPan ();
                    break;
                case 2:
                    this.project.resetCueVolume ();
                    break;
                case 3:
                    this.project.resetCueMix ();
                    break;
                default:
                    // Not used
                    break;
            }
        }

        switch (index)
        {
            case 0:
                this.masterTrack.touchVolume (isTouched);
                break;
            case 1:
                this.masterTrack.touchPan (isTouched);
                break;
            case 2:
                this.project.touchCueVolume (isTouched);
                break;
            case 3:
                this.project.touchCueMix (isTouched);
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
        final boolean canEditCueVolume = this.model.getHost ().supports (Capability.CUE_VOLUME);
        final int upperBound = this.model.getValueChanger ().getUpperBound ();

        display.setCell (0, 0, TAG_VOLUME).setCell (0, 1, "Pan");
        if (canEditCueVolume)
            display.setCell (0, 2, TAG_VOLUME).setCell (0, 3, "Mix");
        display.setCell (0, 6, "Load").setCell (0, 7, "Save").setCell (1, 6, "Project:");
        display.setCell (1, 0, this.masterTrack.getVolumeStr (8)).setCell (1, 1, this.masterTrack.getPanStr (8));
        if (canEditCueVolume)
            display.setCell (1, 2, this.project.getCueVolumeStr (8)).setCell (1, 3, this.project.getCueMixStr (8));
        display.setBlock (1, 2, "Audio Engine").setBlock (2, 3, this.project.getName ());
        display.setCell (2, 0, this.configuration.isEnableVUMeters () ? Push1Display.formatValue (this.masterTrack.getVolume (), this.masterTrack.getVu (), upperBound) : Push1Display.formatValue (this.masterTrack.getVolume (), upperBound));
        display.setCell (2, 1, this.masterTrack.getPan (), Format.FORMAT_PAN);
        if (canEditCueVolume)
        {
            display.setCell (2, 2, this.project.getCueVolume (), Format.FORMAT_VALUE);
            display.setCell (2, 3, this.project.getCueMix (), Format.FORMAT_VALUE);
            display.setCell (3, 0, this.masterTrack.getName ()).setCell (3, 2, "Cue");
        }
        display.setCell (3, 4, this.model.getApplication ().isEngineActive () ? "Active" : "Off");
        display.setCell (3, 6, "Previous").setCell (3, 7, "Next");
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final boolean enableVUMeters = this.configuration.isEnableVUMeters ();
        final int vuR = valueChanger.toDisplayValue (enableVUMeters ? this.masterTrack.getVuRight () : 0);
        final int vuL = valueChanger.toDisplayValue (enableVUMeters ? this.masterTrack.getVuLeft () : 0);

        final ICursorTrack cursorTrack = this.model.getCursorTrack ();

        display.addChannelElement (TAG_VOLUME, false, this.masterTrack.getName (), ChannelType.MASTER, this.masterTrack.getColor (), this.masterTrack.isSelected (), valueChanger.toDisplayValue (this.masterTrack.getVolume ()), valueChanger.toDisplayValue (this.masterTrack.getModulatedVolume ()), this.isKnobTouched (0) ? this.masterTrack.getVolumeStr (8) : "", valueChanger.toDisplayValue (this.masterTrack.getPan ()), valueChanger.toDisplayValue (this.masterTrack.getModulatedPan ()), this.isKnobTouched (1) ? this.masterTrack.getPanStr (8) : "", vuL, vuR, this.masterTrack.isMute (), this.masterTrack.isSolo (), this.masterTrack.isRecArm (), this.masterTrack.isActivated (), 0, this.masterTrack.isSelected () && cursorTrack.isPinned ());
        display.addChannelSelectorElement ("Pan", false, "", null, ColorEx.BLACK, false, this.masterTrack.isActivated ());

        if (this.model.getHost ().supports (Capability.CUE_VOLUME))
        {
            display.addChannelElement ("Cue Volume", false, "Cue", ChannelType.CUE, ColorEx.GRAY, false, valueChanger.toDisplayValue (this.project.getCueVolume ()), -1, this.isKnobTouched (2) ? this.project.getCueVolumeStr (8) : "", valueChanger.toDisplayValue (this.project.getCueMix ()), -1, this.isKnobTouched (3) ? this.project.getCueMixStr (8) : "", 0, 0, false, false, false, true, 0, false);
            display.addChannelSelectorElement ("Cue Mix", false, "", null, ColorEx.BLACK, false, true);
        }
        else
        {
            display.addOptionElement ("", " ", false, "", "", false, true);
            display.addOptionElement ("", " ", false, "", "", false, true);
        }

        display.addOptionElement ("", " ", false, "Audio Engine", this.model.getApplication ().isEngineActive () ? "Active" : "Off", false, true);
        display.addOptionElement ("", " ", false, "", "", false, true);
        display.addOptionElement ("Project:", "Load", false, this.project.getName (), "Previous", false, true);
        display.addOptionElement ("", "Save", false, "", "Next", false, true);
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
            this.masterTrack.toggleRecArm ();
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
                this.project.previous ();
                break;

            case 7:
                this.project.next ();
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
        final boolean isPush2 = this.configuration.isPushModern ();

        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
        {
            final ColorManager colorManager = this.model.getColorManager ();

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
            final int off = this.isPushModern ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;

            switch (index)
            {
                case 0:
                    if (!this.isPushModern)
                    {
                        final boolean muteState = this.configuration.getLockState () == LockState.MUTE;
                        if (muteState)
                            return this.masterTrack.isMute () ? off : PushColorManager.PUSH1_COLOR2_YELLOW_HI;
                        return this.masterTrack.isSolo () ? PushColorManager.PUSH1_COLOR2_BLUE_HI : PushColorManager.PUSH1_COLOR2_GREY_LO;
                    }
                    break;

                case 6:
                    return isPush2 ? PushColorManager.PUSH2_COLOR_GREEN_HI : PushColorManager.PUSH1_COLOR_GREEN_HI;

                case 7:
                    if (this.project.isDirty ())
                        return isPush2 ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI;
                    return isPush2 ? PushColorManager.PUSH2_COLOR_GREEN_LO : PushColorManager.PUSH1_COLOR_GREEN_LO;
            }

            return off;
        }

        return super.getButtonColor (buttonID);
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        switch (index)
        {
            case 0:
                if (!this.isPushModern)
                {
                    if (this.configuration.getLockState () == LockState.MUTE)
                        this.masterTrack.toggleMute ();
                    else
                        this.masterTrack.toggleSolo ();
                }
                break;

            case 6:
                this.project.load ();
                break;

            case 7:
                this.project.save ();
                break;

            default:
                // Not used
                break;
        }
    }


    private int getTrackButtonColor ()
    {
        if (!this.masterTrack.isActivated ())
            return this.isPushModern ? PushColorManager.PUSH2_COLOR_BLACK : PushColorManager.PUSH1_COLOR_BLACK;
        if (this.masterTrack.isRecArm ())
            return this.isPushModern ? PushColorManager.PUSH2_COLOR_RED_HI : PushColorManager.PUSH1_COLOR_RED_HI;
        return this.isPushModern ? PushColorManager.PUSH2_COLOR_ORANGE_HI : PushColorManager.PUSH1_COLOR_ORANGE_HI;
    }


    private void setActive (final boolean enable)
    {
        this.masterTrack.setVolumeIndication (enable);
        this.masterTrack.setPanIndication (enable);
    }
}