// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ableton.push.mode;

import de.mossgrabers.controller.ableton.push.controller.PushControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.display.IGraphicDisplay;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IArranger;
import de.mossgrabers.framework.daw.IMixer;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.featuregroup.AbstractFeatureGroup;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FrameMode extends BaseMode<IItem>
{
    private static final String       ROW0             = "Layouts:                  Panels:                                   ";
    private static final String       ROW1             = "Arrange  Mix     Edit     Notes   Automate Device  Mixer    Inspectr";

    private static final String []    ARRANGER_ROWS    =
    {
        "Arranger:                                                           ",
        "ClpLnchr I/O     Markers  TimelineFXTracks Follow  TrckHght Full    "
    };

    private static final String []    MIXER_ROWS       =
    {
        "Mixer:                                                              ",
        "ClpLnchr I/O     CrossFde Device  Meters   Sends            Full    "
    };

    private static final String []    EMPTY_ROWS       =
    {
        "                                                                    ",
        "                                                                    "
    };

    private static final String []    LAYOUTS1         =
    {
        "Layouts",
        "",
        "",
        "Panels",
        "",
        "",
        "",
        ""
    };

    private static final String []    LAYOUTS2         =
    {
        "Arrange",
        "Mix",
        "Edit",
        "Notes",
        "Automate",
        "Device",
        "Mixer",
        "Inspector"
    };

    private static final String [] [] ARRANGER_OPTIONS =
    {
        {
            "Arranger",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        },
        {
            "Clip Launcher",
            "I/O",
            "Markers",
            "Timeline",
            "FX Tracks",
            "Follow",
            "Track Height",
            "Fullscreen"
        }
    };

    private static final String [] [] MIXER_OPTIONS    =
    {
        {
            "Mixer",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        },
        {
            "Clip Launcher",
            "I/O",
            "Crossfader",
            "Device",
            "Meters",
            "Sends",
            "",
            "Fullscreen"
        }
    };

    private static final String [] [] EMPTY_OPTIONS    =
    {
        {
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        },
        {
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        }
    };


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public FrameMode (final PushControlSurface surface, final IModel model)
    {
        super ("Frame", surface, model);
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
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IApplication app = this.model.getApplication ();
        switch (index)
        {
            case 0:
                app.setPanelLayout ("ARRANGE");
                break;
            case 1:
                app.setPanelLayout ("MIX");
                break;
            case 2:
                app.setPanelLayout ("EDIT");
                break;
            case 3:
                app.toggleNoteEditor ();
                break;
            case 4:
                app.toggleAutomationEditor ();
                break;
            case 5:
                app.toggleDevices ();
                break;
            case 6:
                app.toggleMixer ();
                break;
            case 7:
                app.toggleInspector ();
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onSecondRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IApplication app = this.model.getApplication ();
        if (app.isArrangeLayout ())
        {
            final IArranger arrange = this.model.getArranger ();
            switch (index)
            {
                case 0:
                    arrange.toggleClipLauncher ();
                    break;
                case 1:
                    arrange.toggleIoSection ();
                    break;
                case 2:
                    arrange.toggleCueMarkerVisibility ();
                    break;
                case 3:
                    arrange.toggleTimeLine ();
                    break;
                case 4:
                    arrange.toggleEffectTracks ();
                    break;
                case 5:
                    arrange.togglePlaybackFollow ();
                    break;
                case 6:
                    arrange.toggleTrackRowHeight ();
                    break;
                case 7:
                    app.toggleFullScreen ();
                    break;
                default:
                    // Not used
                    break;
            }
        }
        else if (app.isMixerLayout ())
        {
            final IMixer mix = this.model.getMixer ();
            switch (index)
            {
                case 0:
                    mix.toggleClipLauncherSectionVisibility ();
                    break;
                case 1:
                    mix.toggleIoSectionVisibility ();
                    break;
                case 2:
                    mix.toggleCrossFadeSectionVisibility ();
                    break;
                case 3:
                    mix.toggleDeviceSectionVisibility ();
                    break;
                case 4:
                    mix.toggleMeterSectionVisibility ();
                    break;
                case 5:
                    mix.toggleSendsSectionVisibility ();
                    break;
                case 7:
                    app.toggleFullScreen ();
                    break;
                default:
                    // Not used
                    break;
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 (final ITextDisplay display)
    {
        final IApplication app = this.model.getApplication ();

        final String [] rows34;
        if (app.isArrangeLayout ())
            rows34 = FrameMode.ARRANGER_ROWS;
        else if (app.isMixerLayout ())
            rows34 = FrameMode.MIXER_ROWS;
        else
            rows34 = FrameMode.EMPTY_ROWS;

        display.setRow (0, FrameMode.ROW0).setRow (1, FrameMode.ROW1).setRow (2, rows34[0]).setRow (3, rows34[1]);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 (final IGraphicDisplay display)
    {
        final IApplication app = this.model.getApplication ();
        String [] [] options;
        if (app.isArrangeLayout ())
            options = ARRANGER_OPTIONS;
        else if (app.isMixerLayout ())
            options = MIXER_OPTIONS;
        else
            options = EMPTY_OPTIONS;

        for (int i = 0; i < 8; i++)
            display.addOptionElement (options[0][i], options[1][i], this.getSecondRowButtonState (i) > 0, FrameMode.LAYOUTS1[i], FrameMode.LAYOUTS2[i], this.getFirstRowButtonState (i), false);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        int index = this.isButtonRow (0, buttonID);
        if (index >= 0)
            return this.colorManager.getColorIndex (this.getFirstRowButtonState (index) ? AbstractMode.BUTTON_COLOR_HI : AbstractFeatureGroup.BUTTON_COLOR_ON);

        index = this.isButtonRow (1, buttonID);
        if (index >= 0)
        {
            final String colorID;
            switch (this.getSecondRowButtonState (index))
            {
                case 0:
                    colorID = AbstractMode.BUTTON_COLOR2_ON;
                    break;
                case 1:
                    colorID = AbstractMode.BUTTON_COLOR2_HI;
                    break;
                default:
                    colorID = AbstractFeatureGroup.BUTTON_COLOR_OFF;
                    break;
            }
            return this.colorManager.getColorIndex (colorID);
        }

        return super.getButtonColor (buttonID);
    }


    private boolean getFirstRowButtonState (final int index)
    {
        switch (index)
        {
            case 0:
                return this.model.getApplication ().isArrangeLayout ();
            case 1:
                return this.model.getApplication ().isMixerLayout ();
            case 2:
                return this.model.getApplication ().isEditLayout ();
            default:
                return false;
        }
    }


    private int getSecondRowButtonState (final int index)
    {
        final IApplication app = this.model.getApplication ();
        if (app.isArrangeLayout ())
        {
            final IArranger arrange = this.model.getArranger ();
            switch (index)
            {
                case 0:
                    return arrange.isClipLauncherVisible () ? 1 : 0;
                case 1:
                    return arrange.isIoSectionVisible () ? 1 : 0;
                case 2:
                    return arrange.areCueMarkersVisible () ? 1 : 0;
                case 3:
                    return arrange.isTimelineVisible () ? 1 : 0;
                case 4:
                    return arrange.areEffectTracksVisible () ? 1 : 0;
                case 5:
                    return arrange.isPlaybackFollowEnabled () ? 1 : 0;
                case 6:
                    return arrange.hasDoubleRowTrackHeight () ? 1 : 0;
                default:
                    return 0;
            }
        }

        if (app.isMixerLayout ())
        {
            final IMixer mix = this.model.getMixer ();
            switch (index)
            {
                case 0:
                    return mix.isClipLauncherSectionVisible () ? 1 : 0;
                case 1:
                    return mix.isIoSectionVisible () ? 1 : 0;
                case 2:
                    return mix.isCrossFadeSectionVisible () ? 1 : 0;
                case 3:
                    return mix.isDeviceSectionVisible () ? 1 : 0;
                case 4:
                    return mix.isMeterSectionVisible () ? 1 : 0;
                case 5:
                    return mix.isSendSectionVisible () ? 1 : 0;
                case 6:
                    return -1;
                default:
                    return 0;
            }
        }

        return -1;
    }


    private void setActive (final boolean enable)
    {
        this.model.getArranger ().enableObservers (enable);
        this.model.getMixer ().enableObservers (enable);
    }
}
