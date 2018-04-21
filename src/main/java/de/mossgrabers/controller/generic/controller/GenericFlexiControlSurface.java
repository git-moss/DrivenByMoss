// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import de.mossgrabers.controller.generic.GenericFlexiConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.controller.Relative2ValueChanger;
import de.mossgrabers.framework.controller.Relative3ValueChanger;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * The Generic Flexi.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class GenericFlexiControlSurface extends AbstractControlSurface<GenericFlexiConfiguration>
{
    private static final int BUTTON_REPEAT_INTERVAL = 75;

    private static final int KNOB_MODE_ABSOLUTE     = 0;
    private static final int KNOB_MODE_RELATIVE1    = 1;
    private static final int KNOB_MODE_RELATIVE2    = 2;
    private static final int KNOB_MODE_RELATIVE3    = 3;

    private IModel           model;
    private IValueChanger    relative2ValueChanger  = new Relative2ValueChanger (128, 1, 0.5);
    private IValueChanger    relative3ValueChanger  = new Relative3ValueChanger (128, 1, 0.5);


    /**
     * Constructor.
     *
     * @param host The host
     * @param model
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The midi output
     * @param input The midi input
     */
    public GenericFlexiControlSurface (final IHost host, final IModel model, final ColorManager colorManager, final GenericFlexiConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (host, configuration, colorManager, output, input, new int [0]);

        this.model = model;
    }


    /** {@inheritDoc} */
    @Override
    protected void handleCC (final int channel, final int cc, final int value)
    {
        this.configuration.setAddValues (channel, cc);

        final FlexiCommand ccCommand = this.configuration.getCcCommand (cc);
        if (ccCommand.ordinal () == 0 || this.configuration.getMidiChannel (cc) != channel)
            return;

        this.handleCommand (ccCommand, cc, value);
    }


    private void handleCommand (final FlexiCommand ccCommand, final int cc, final int value)
    {
        switch (ccCommand)
        {
            case OFF:
                // No function
                break;

            // Global: Undo
            case GLOBAL_UNDO:
                if (value > 0)
                    this.model.getApplication ().undo ();
                break;
            // Global: Redo
            case GLOBAL_REDO:
                if (value > 0)
                    this.model.getApplication ().redo ();
                break;
            // Global: Previous Project
            case GLOBAL_PREVIOUS_PROJECT:
                if (value > 0)
                    this.model.getProject ().previous ();
                break;
            // Global: Next Project
            case GLOBAL_NEXT_PROJECT:
                if (value > 0)
                    this.model.getProject ().next ();
                break;
            // Global: Toggle Audio Engine
            case GLOBAL_TOGGLE_AUDIO_ENGINE:
                if (value > 0)
                    this.model.getApplication ().toggleEngineActive ();
                break;

            // Transport: Play
            case TRANSPORT_PLAY:
                if (value > 0)
                    this.model.getTransport ().play ();
                break;
            // Transport: Stop
            case TRANSPORT_STOP:
                if (value > 0)
                    this.model.getTransport ().stop ();
                break;
            // Transport: Restart
            case TRANSPORT_RESTART:
                if (value > 0)
                    this.model.getTransport ().restart ();
                break;
            // Transport: Toggle Repeat
            case TRANSPORT_TOGGLE_REPEAT:
                if (value > 0)
                    this.model.getTransport ().toggleLoop ();
                break;
            // Transport: Toggle Metronome
            case TRANSPORT_TOGGLE_METRONOME:
                if (value > 0)
                    this.model.getTransport ().toggleMetronome ();
                break;
            // Transport: Set Metronome Volume
            case TRANSPORT_SET_METRONOME_VOLUME:
                this.handleMetronomeVolume (this.configuration.getKnobMode (cc), value);
                break;
            // Transport: Toggle Metronome in Pre-roll
            case TRANSPORT_TOGGLE_METRONOME_IN_PREROLL:
                if (value > 0)
                    this.model.getTransport ().togglePrerollMetronome ();
                break;
            // Transport: Toggle Punch In
            case TRANSPORT_TOGGLE_PUNCH_IN:
                if (value > 0)
                    this.model.getTransport ().togglePunchIn ();
                break;
            // Transport: Toggle Punch Out
            case TRANSPORT_TOGGLE_PUNCH_OUT:
                if (value > 0)
                    this.model.getTransport ().togglePunchOut ();
                break;
            // Transport: Toggle Record
            case TRANSPORT_TOGGLE_RECORD:
                if (value > 0)
                    this.model.getTransport ().record ();
                break;
            // Transport: Toggle Arranger Overdub
            case TRANSPORT_TOGGLE_ARRANGER_OVERDUB:
                if (value > 0)
                    this.model.getTransport ().toggleOverdub ();
                break;
            // Transport: Toggle Clip Overdub
            case TRANSPORT_TOGGLE_CLIP_OVERDUB:
                if (value > 0)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;
            // Transport: Set Crossfader
            case TRANSPORT_SET_CROSSFADER:
                this.handleCrossfade (this.configuration.getKnobMode (cc), value);
                break;
            // Transport: Toggle Arranger Automation Write
            case TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE:
                if (value > 0)
                    this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;
            // Transport: Toggle Clip Automation Write
            case TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE:
                if (value > 0)
                    this.model.getTransport ().toggleWriteClipLauncherAutomation ();
                break;
            // Transport: Set Write Mode: Latch
            case TRANSPORT_SET_WRITE_MODE_LATCH:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[0]);
                break;
            // Transport: Set Write Mode: Touch
            case TRANSPORT_SET_WRITE_MODE_TOUCH:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[1]);
                break;
            // Transport: Set Write Mode: Write
            case TRANSPORT_SET_WRITE_MODE_WRITE:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[2]);
                break;
            // Transport: Set Tempo
            case TRANSPORT_SET_TEMPO:
                this.handleTempo (this.configuration.getKnobMode (cc), value);
                break;
            // Transport: Tap Tempo
            case TRANSPORT_TAP_TEMPO:
                if (value > 0)
                    this.model.getTransport ().tapTempo ();
                break;
            // Transport: Move Play Cursor
            case TRANSPORT_MOVE_PLAY_CURSOR:
                this.handlePlayCursor (this.configuration.getKnobMode (cc), value);
                break;

            // Layout: Set Arrange Layout
            case LAYOUT_SET_ARRANGE_LAYOUT:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;
            // Layout: Set Mix Layout
            case LAYOUT_SET_MIX_LAYOUT:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;
            // Layout: Set Edit Layout
            case LAYOUT_SET_EDIT_LAYOUT:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;
            // Layout: Toggle Note Editor
            case LAYOUT_TOGGLE_NOTE_EDITOR:
                if (value > 0)
                    this.model.getApplication ().toggleNoteEditor ();
                break;
            // Layout: Toggle Automation Editor
            case LAYOUT_TOGGLE_AUTOMATION_EDITOR:
                if (value > 0)
                    this.model.getApplication ().toggleAutomationEditor ();
                break;
            // Layout: Toggle Devices Panel
            case LAYOUT_TOGGLE_DEVICES_PANEL:
                if (value > 0)
                    this.model.getApplication ().toggleDevices ();
                break;
            // Layout: Toggle Mixer Panel
            case LAYOUT_TOGGLE_MIXER_PANEL:
                if (value > 0)
                    this.model.getApplication ().toggleMixer ();
                break;
            // Layout: Toggle Fullscreen
            case LAYOUT_TOGGLE_FULLSCREEN:
                if (value > 0)
                    this.model.getApplication ().toggleFullScreen ();
                break;
            // Layout: Toggle Arranger Cue Markers
            case LAYOUT_TOGGLE_ARRANGER_CUE_MARKERS:
                if (value > 0)
                    this.model.getArranger ().toggleCueMarkerVisibility ();
                break;
            // Layout: Toggle Arranger Playback Follow
            case LAYOUT_TOGGLE_ARRANGER_PLAYBACK_FOLLOW:
                if (value > 0)
                    this.model.getArranger ().togglePlaybackFollow ();
                break;
            // Layout: Toggle Arranger Track Row Height
            case LAYOUT_TOGGLE_ARRANGER_TRACK_ROW_HEIGHT:
                if (value > 0)
                    this.model.getArranger ().toggleTrackRowHeight ();
                break;
            // Layout: Toggle Arranger Clip Launcher Section
            case LAYOUT_TOGGLE_ARRANGER_CLIP_LAUNCHER_SECTION:
                if (value > 0)
                    this.model.getArranger ().toggleClipLauncher ();
                break;
            // Layout: Toggle Arranger Time Line
            case LAYOUT_TOGGLE_ARRANGER_TIME_LINE:
                if (value > 0)
                    this.model.getArranger ().toggleTimeLine ();
                break;
            // Layout: Toggle Arranger IO Section
            case LAYOUT_TOGGLE_ARRANGER_IO_SECTION:
                if (value > 0)
                    this.model.getArranger ().toggleIoSection ();
                break;
            // Layout: Toggle Arranger Effect Tracks
            case LAYOUT_TOGGLE_ARRANGER_EFFECT_TRACKS:
                if (value > 0)
                    this.model.getArranger ().toggleEffectTracks ();
                break;
            // Layout: Toggle Mixer Clip Launcher Section
            case LAYOUT_TOGGLE_MIXER_CLIP_LAUNCHER_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleClipLauncherSectionVisibility ();
                break;
            // Layout: Toggle Mixer Cross Fade Section
            case LAYOUT_TOGGLE_MIXER_CROSS_FADE_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleCrossFadeSectionVisibility ();
                break;
            // Layout: Toggle Mixer Device Section
            case LAYOUT_TOGGLE_MIXER_DEVICE_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleDeviceSectionVisibility ();
                break;
            // Layout: Toggle Mixer sendsSection
            case LAYOUT_TOGGLE_MIXER_SENDSSECTION:
                if (value > 0)
                    this.model.getMixer ().toggleSendsSectionVisibility ();
                break;
            // Layout: Toggle Mixer IO Section
            case LAYOUT_TOGGLE_MIXER_IO_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleIoSectionVisibility ();
                break;
            // Layout: Toggle Mixer Meter Section
            case LAYOUT_TOGGLE_MIXER_METER_SECTION:
                if (value > 0)
                    this.model.getMixer ().toggleMeterSectionVisibility ();
                break;

            // Track: Add Audio Track
            case TRACK_ADD_AUDIO_TRACK:
                if (value > 0)
                    this.model.getApplication ().addAudioTrack ();
                break;
            // Track: Add Effect Track
            case TRACK_ADD_EFFECT_TRACK:
                if (value > 0)
                    this.model.getApplication ().addEffectTrack ();
                break;
            // Track: Add Instrument Track
            case TRACK_ADD_INSTRUMENT_TRACK:
                if (value > 0)
                    this.model.getApplication ().addInstrumentTrack ();
                break;
            // Track: Select Previous Bank Page
            case TRACK_SELECT_PREVIOUS_BANK_PAGE:
                if (value > 0)
                    this.scrollTrackLeft (true);
                break;
            // Track: Select Next Bank Page
            case TRACK_SELECT_NEXT_BANK_PAGE:
                if (value > 0)
                    this.scrollTrackRight (true);
                break;
            // Track: Select Previous Track
            case TRACK_SELECT_PREVIOUS_TRACK:
                if (value > 0)
                    this.scrollTrackLeft (false);
                break;
            // Track: Select Next Track
            case TRACK_SELECT_NEXT_TRACK:
                if (value > 0)
                    this.scrollTrackRight (true);
                break;
            // Track 1-8: Select
            case TRACK_1_SELECT:
            case TRACK_2_SELECT:
            case TRACK_3_SELECT:
            case TRACK_4_SELECT:
            case TRACK_5_SELECT:
            case TRACK_6_SELECT:
            case TRACK_7_SELECT:
            case TRACK_8_SELECT:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_SELECT.ordinal ()).selectAndMakeVisible ();
                break;
            // Track 1-8: Toggle Active
            case TRACK_1_TOGGLE_ACTIVE:
            case TRACK_2_TOGGLE_ACTIVE:
            case TRACK_3_TOGGLE_ACTIVE:
            case TRACK_4_TOGGLE_ACTIVE:
            case TRACK_5_TOGGLE_ACTIVE:
            case TRACK_6_TOGGLE_ACTIVE:
            case TRACK_7_TOGGLE_ACTIVE:
            case TRACK_8_TOGGLE_ACTIVE:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ACTIVE.ordinal ()).toggleIsActivated ();
                break;
            case TRACK_SELECTED_TOGGLE_ACTIVE:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleIsActivated ();
                }
                break;
            // Track 1-8: Set Volume
            case TRACK_1_SET_VOLUME:
            case TRACK_2_SET_VOLUME:
            case TRACK_3_SET_VOLUME:
            case TRACK_4_SET_VOLUME:
            case TRACK_5_SET_VOLUME:
            case TRACK_6_SET_VOLUME:
            case TRACK_7_SET_VOLUME:
            case TRACK_8_SET_VOLUME:
                this.changeTrackVolume (this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_VOLUME.ordinal (), value);
                break;
            // Track Selected: Set Volume Track
            case TRACK_SELECTED_SET_VOLUME_TRACK:
                this.changeTrackVolume (this.configuration.getKnobMode (cc), -1, value);
                break;
            // Track 1-8: Set Panorama
            case TRACK_1_SET_PANORAMA:
            case TRACK_2_SET_PANORAMA:
            case TRACK_3_SET_PANORAMA:
            case TRACK_4_SET_PANORAMA:
            case TRACK_5_SET_PANORAMA:
            case TRACK_6_SET_PANORAMA:
            case TRACK_7_SET_PANORAMA:
            case TRACK_8_SET_PANORAMA:
                this.changeTrackPanorama (this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_PANORAMA.ordinal (), value);
                break;
            // Track Selected: Set Panorama
            case TRACK_SELECTED_SET_PANORAMA:
                this.changeTrackPanorama (this.configuration.getKnobMode (cc), -1, value);
                break;
            // Track 1-8: Toggle Mute
            case TRACK_1_TOGGLE_MUTE:
            case TRACK_2_TOGGLE_MUTE:
            case TRACK_3_TOGGLE_MUTE:
            case TRACK_4_TOGGLE_MUTE:
            case TRACK_5_TOGGLE_MUTE:
            case TRACK_6_TOGGLE_MUTE:
            case TRACK_7_TOGGLE_MUTE:
            case TRACK_8_TOGGLE_MUTE:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MUTE.ordinal ()).toggleMute ();
                break;
            // Track Selected: Toggle Mute
            case TRACK_SELECTED_TOGGLE_MUTE:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMute ();
                }
                break;
            // Track 1-8: Toggle Solo
            case TRACK_1_TOGGLE_SOLO:
            case TRACK_2_TOGGLE_SOLO:
            case TRACK_3_TOGGLE_SOLO:
            case TRACK_4_TOGGLE_SOLO:
            case TRACK_5_TOGGLE_SOLO:
            case TRACK_6_TOGGLE_SOLO:
            case TRACK_7_TOGGLE_SOLO:
            case TRACK_8_TOGGLE_SOLO:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_TOGGLE_SOLO.ordinal ()).toggleSolo ();
                break;
            // Track Selected: Toggle Solo
            case TRACK_SELECTED_TOGGLE_SOLO:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleSolo ();
                }
                break;
            // Track 1-8: Toggle Arm
            case TRACK_1_TOGGLE_ARM:
            case TRACK_2_TOGGLE_ARM:
            case TRACK_3_TOGGLE_ARM:
            case TRACK_4_TOGGLE_ARM:
            case TRACK_5_TOGGLE_ARM:
            case TRACK_6_TOGGLE_ARM:
            case TRACK_7_TOGGLE_ARM:
            case TRACK_8_TOGGLE_ARM:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_TOGGLE_ARM.ordinal ()).toggleRecArm ();
                break;
            // Track Selected: Toggle Arm
            case TRACK_SELECTED_TOGGLE_ARM:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleRecArm ();
                }
                break;
            // Track 1-8: Toggle Monitor
            case TRACK_1_TOGGLE_MONITOR:
            case TRACK_2_TOGGLE_MONITOR:
            case TRACK_3_TOGGLE_MONITOR:
            case TRACK_4_TOGGLE_MONITOR:
            case TRACK_5_TOGGLE_MONITOR:
            case TRACK_6_TOGGLE_MONITOR:
            case TRACK_7_TOGGLE_MONITOR:
            case TRACK_8_TOGGLE_MONITOR:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_TOGGLE_MONITOR.ordinal ()).toggleMonitor ();
                break;
            // Track Selected: Toggle Monitor
            case TRACK_SELECTED_TOGGLE_MONITOR:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMonitor ();
                }
                break;
            // Track 1: Toggle Auto Monitor
            case TRACK_1_TOGGLE_AUTO_MONITOR:
            case TRACK_2_TOGGLE_AUTO_MONITOR:
            case TRACK_3_TOGGLE_AUTO_MONITOR:
            case TRACK_4_TOGGLE_AUTO_MONITOR:
            case TRACK_5_TOGGLE_AUTO_MONITOR:
            case TRACK_6_TOGGLE_AUTO_MONITOR:
            case TRACK_7_TOGGLE_AUTO_MONITOR:
            case TRACK_8_TOGGLE_AUTO_MONITOR:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand.ordinal () - FlexiCommand.TRACK_1_TOGGLE_AUTO_MONITOR.ordinal ()).toggleAutoMonitor ();
                break;
            // Track Selected: Toggle Auto Monitor
            case TRACK_SELECTED_TOGGLE_AUTO_MONITOR:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleAutoMonitor ();
                }
                break;

            // Track 1-8: Set Send 1
            case TRACK_1_SET_SEND_1:
            case TRACK_2_SET_SEND_1:
            case TRACK_3_SET_SEND_1:
            case TRACK_4_SET_SEND_1:
            case TRACK_5_SET_SEND_1:
            case TRACK_6_SET_SEND_1:
            case TRACK_7_SET_SEND_1:
            case TRACK_8_SET_SEND_1:
                this.changeSendVolume (0, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_1.ordinal (), value);
                break;

            // Track 1-8: Set Send 2
            case TRACK_1_SET_SEND_2:
            case TRACK_2_SET_SEND_2:
            case TRACK_3_SET_SEND_2:
            case TRACK_4_SET_SEND_2:
            case TRACK_5_SET_SEND_2:
            case TRACK_6_SET_SEND_2:
            case TRACK_7_SET_SEND_2:
            case TRACK_8_SET_SEND_2:
                this.changeSendVolume (1, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_2.ordinal (), value);
                break;

            // Track 1-8: Set Send 3
            case TRACK_1_SET_SEND_3:
            case TRACK_2_SET_SEND_3:
            case TRACK_3_SET_SEND_3:
            case TRACK_4_SET_SEND_3:
            case TRACK_5_SET_SEND_3:
            case TRACK_6_SET_SEND_3:
            case TRACK_7_SET_SEND_3:
            case TRACK_8_SET_SEND_3:
                this.changeSendVolume (2, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_3.ordinal (), value);
                break;

            // Track 1-8: Set Send 4
            case TRACK_1_SET_SEND_4:
            case TRACK_2_SET_SEND_4:
            case TRACK_3_SET_SEND_4:
            case TRACK_4_SET_SEND_4:
            case TRACK_5_SET_SEND_4:
            case TRACK_6_SET_SEND_4:
            case TRACK_7_SET_SEND_4:
            case TRACK_8_SET_SEND_4:
                this.changeSendVolume (3, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_4.ordinal (), value);
                break;

            // Track 1: Set Send 5
            case TRACK_1_SET_SEND_5:
            case TRACK_2_SET_SEND_5:
            case TRACK_3_SET_SEND_5:
            case TRACK_4_SET_SEND_5:
            case TRACK_5_SET_SEND_5:
            case TRACK_6_SET_SEND_5:
            case TRACK_7_SET_SEND_5:
            case TRACK_8_SET_SEND_5:
                this.changeSendVolume (4, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_5.ordinal (), value);
                break;

            // Track 1: Set Send 6
            case TRACK_1_SET_SEND_6:
            case TRACK_2_SET_SEND_6:
            case TRACK_3_SET_SEND_6:
            case TRACK_4_SET_SEND_6:
            case TRACK_5_SET_SEND_6:
            case TRACK_6_SET_SEND_6:
            case TRACK_7_SET_SEND_6:
            case TRACK_8_SET_SEND_6:
                this.changeSendVolume (5, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_6.ordinal (), value);
                break;

            // Track 1-8: Set Send 7
            case TRACK_1_SET_SEND_7:
            case TRACK_2_SET_SEND_7:
            case TRACK_3_SET_SEND_7:
            case TRACK_4_SET_SEND_7:
            case TRACK_5_SET_SEND_7:
            case TRACK_6_SET_SEND_7:
            case TRACK_7_SET_SEND_7:
            case TRACK_8_SET_SEND_7:
                this.changeSendVolume (6, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_7.ordinal (), value);
                break;

            // Track 1-8: Set Send 8
            case TRACK_1_SET_SEND_8:
            case TRACK_2_SET_SEND_8:
            case TRACK_3_SET_SEND_8:
            case TRACK_4_SET_SEND_8:
            case TRACK_5_SET_SEND_8:
            case TRACK_6_SET_SEND_8:
            case TRACK_7_SET_SEND_8:
            case TRACK_8_SET_SEND_8:
                this.changeSendVolume (7, this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.TRACK_1_SET_SEND_8.ordinal (), value);
                break;

            // Track Selected: Set Send 1-8
            case TRACK_SELECTED_SET_SEND_1:
            case TRACK_SELECTED_SET_SEND_2:
            case TRACK_SELECTED_SET_SEND_3:
            case TRACK_SELECTED_SET_SEND_4:
            case TRACK_SELECTED_SET_SEND_5:
            case TRACK_SELECTED_SET_SEND_6:
            case TRACK_SELECTED_SET_SEND_7:
            case TRACK_SELECTED_SET_SEND_8:
                this.changeSendVolume (ccCommand.ordinal () - FlexiCommand.TRACK_SELECTED_SET_SEND_1.ordinal (), this.configuration.getKnobMode (cc), -1, value);
                break;

            // Master: Set Volume
            case MASTER_SET_VOLUME:
                this.changeMasterVolume (this.configuration.getKnobMode (cc), value);
                break;
            // Master: Set Panorama
            case MASTER_SET_PANORAMA:
                this.changeMasterPanorama (this.configuration.getKnobMode (cc), value);
                break;
            // Master: Toggle Mute
            case MASTER_TOGGLE_MUTE:
                if (value > 0)
                    this.model.getMasterTrack ().toggleMute ();
                break;
            // Master: Toggle Solo
            case MASTER_TOGGLE_SOLO:
                if (value > 0)
                    this.model.getMasterTrack ().toggleSolo ();
                break;
            // Master: Toggle Arm
            case MASTER_TOGGLE_ARM:
                if (value > 0)
                    this.model.getMasterTrack ().toggleRecArm ();
                break;

            // Device: Toggle Window
            case DEVICE_TOGGLE_WINDOW:
                if (value > 0)
                    this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            // Device: Bypass
            case DEVICE_BYPASS:
                if (value > 0)
                    this.model.getCursorDevice ().toggleEnabledState ();
                break;
            // Device: Expand
            case DEVICE_EXPAND:
                if (value > 0)
                    this.model.getCursorDevice ().toggleExpanded ();
                break;
            // Device: Select Previous
            case DEVICE_SELECT_PREVIOUS:
                if (value > 0)
                    this.model.getCursorDevice ().selectPrevious ();
                break;
            // Device: Select Next
            case DEVICE_SELECT_NEXT:
                if (value > 0)
                    this.model.getCursorDevice ().selectNext ();
                break;
            // Device: Select Previous Parameter Bank
            case DEVICE_SELECT_PREVIOUS_PARAMETER_BANK:
                if (value > 0)
                    this.model.getCursorDevice ().previousParameterPage ();
                break;
            // Device: Select Next Parameter Bank
            case DEVICE_SELECT_NEXT_PARAMETER_BANK:
                if (value > 0)
                    this.model.getCursorDevice ().nextParameterPage ();
                break;

            // Device: Set Parameter 1-8
            case DEVICE_SET_PARAMETER_1:
            case DEVICE_SET_PARAMETER_2:
            case DEVICE_SET_PARAMETER_3:
            case DEVICE_SET_PARAMETER_4:
            case DEVICE_SET_PARAMETER_5:
            case DEVICE_SET_PARAMETER_6:
            case DEVICE_SET_PARAMETER_7:
            case DEVICE_SET_PARAMETER_8:
                this.handleParameter (this.configuration.getKnobMode (cc), ccCommand.ordinal () - FlexiCommand.DEVICE_SET_PARAMETER_1.ordinal (), value);
                break;

            // Scene 1-8: Launch Scene
            case SCENE_1_LAUNCH_SCENE:
            case SCENE_2_LAUNCH_SCENE:
            case SCENE_3_LAUNCH_SCENE:
            case SCENE_4_LAUNCH_SCENE:
            case SCENE_5_LAUNCH_SCENE:
            case SCENE_6_LAUNCH_SCENE:
            case SCENE_7_LAUNCH_SCENE:
            case SCENE_8_LAUNCH_SCENE:
                if (value > 0)
                    this.model.getSceneBank ().launchScene (ccCommand.ordinal () - FlexiCommand.SCENE_1_LAUNCH_SCENE.ordinal ());
                break;

            // Scene: Select Previous Bank
            case SCENE_SELECT_PREVIOUS_BANK:
                if (value > 0)
                    this.model.getSceneBank ().scrollScenesPageUp ();
                break;
            // Scene: Select Next Bank
            case SCENE_SELECT_NEXT_BANK:
                if (value > 0)
                    this.model.getSceneBank ().scrollScenesPageDown ();
                break;
            // Scene: Create Scene from playing Clips
            case SCENE_CREATE_SCENE_FROM_PLAYING_CLIPS:
                if (value > 0)
                    this.model.getProject ().createSceneFromPlayingLauncherClips ();
                break;
            // Browser: Browse Presets
            case BROWSER_BROWSE_PRESETS:
                if (value > 0)
                    this.model.getBrowser ().browseForPresets ();
                break;
            // Browser: Insert Device before current
            case BROWSER_INSERT_DEVICE_BEFORE_CURRENT:
                if (value > 0)
                    this.model.getCursorDevice ().browseToInsertBeforeDevice ();
                break;
            // Browser: Insert Device after current
            case BROWSER_INSERT_DEVICE_AFTER_CURRENT:
                if (value > 0)
                    this.model.getCursorDevice ().browseToInsertAfterDevice ();
                break;
            // Browser: Commit Selection
            case BROWSER_COMMIT_SELECTION:
                if (value > 0)
                    this.model.getBrowser ().stopBrowsing (true);
                break;
            // Browser: Cancel Selection
            case BROWSER_CANCEL_SELECTION:
                if (value > 0)
                    this.model.getBrowser ().stopBrowsing (false);
                break;

            // Browser: Select Previous Filter in Column 1-6
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_2:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_3:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_4:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_5:
            case BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_6:
                if (value > 0)
                    this.model.getBrowser ().selectPreviousFilterItem (ccCommand.ordinal () - FlexiCommand.BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1.ordinal ());
                break;

            // Browser: Select Next Filter in Column 1-6
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_2:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_3:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_4:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_5:
            case BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_6:
                if (value > 0)
                    this.model.getBrowser ().selectNextFilterItem (ccCommand.ordinal () - FlexiCommand.BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1.ordinal ());
                break;

            // Browser: Reset Filter Column 1-6
            case BROWSER_RESET_FILTER_COLUMN_1:
            case BROWSER_RESET_FILTER_COLUMN_2:
            case BROWSER_RESET_FILTER_COLUMN_3:
            case BROWSER_RESET_FILTER_COLUMN_4:
            case BROWSER_RESET_FILTER_COLUMN_5:
            case BROWSER_RESET_FILTER_COLUMN_6:
                if (value > 0)
                    this.model.getBrowser ().resetFilterColumn (ccCommand.ordinal () - FlexiCommand.BROWSER_RESET_FILTER_COLUMN_1.ordinal ());
                break;

            // Browser: Select the previous preset
            case BROWSER_SELECT_THE_PREVIOUS_PRESET:
                if (value > 0)
                    this.model.getBrowser ().selectPreviousResult ();
                break;
            // Browser: Select the next preset
            case BROWSER_SELECT_THE_NEXT_PRESET:
                if (value > 0)
                    this.model.getBrowser ().selectNextResult ();
                break;
            // Browser: Select the previous tab
            case BROWSER_SELECT_THE_PREVIOUS_TAB:
                if (value > 0)
                    this.model.getBrowser ().previousContentType ();
                break;
            // Browser: Select the next tab"
            case BROWSER_SELECT_THE_NEXT_TAB:
                if (value > 0)
                    this.model.getBrowser ().nextContentType ();
                break;
        }
    }


    private void handleParameter (int knobMode, int index, int value)
    {
        final IParameter fxParam = this.model.getCursorDevice ().getFXParam (index);
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                fxParam.setValue (value);
                break;
            case KNOB_MODE_RELATIVE1:
                fxParam.changeValue (value);
                break;
            case KNOB_MODE_RELATIVE2:
                fxParam.setValue (fxParam.getValue () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                fxParam.setValue (fxParam.getValue () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }

    }


    private void changeSendVolume (int sendIndex, int knobMode, int trackIndex, int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        if (track == null)
            return;
        final ISend send = track.getSend (sendIndex);
        if (send == null)
            return;

        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                send.setValue (value);
                break;
            case KNOB_MODE_RELATIVE1:
                send.changeValue (value);
                break;
            case KNOB_MODE_RELATIVE2:
                send.setValue (track.getVolume () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                send.setValue (track.getVolume () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }
    }


    private void handlePlayCursor (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                transport.setTempo (value);
                break;
            case KNOB_MODE_RELATIVE1:
                transport.changePosition (value > 0);
                break;
            case KNOB_MODE_RELATIVE2:
                transport.changePosition (this.relative2ValueChanger.calcKnobSpeed (value) > 0);
                break;
            case KNOB_MODE_RELATIVE3:
                transport.changePosition (this.relative3ValueChanger.calcKnobSpeed (value) > 0);
                break;
        }
    }


    private void handleTempo (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                transport.setTempo (value);
                break;
            case KNOB_MODE_RELATIVE1:
                transport.changeTempo (value > 0);
                break;
            case KNOB_MODE_RELATIVE2:
                transport.changeTempo (this.relative2ValueChanger.calcKnobSpeed (value) > 0);
                break;
            case KNOB_MODE_RELATIVE3:
                transport.changeTempo (this.relative3ValueChanger.calcKnobSpeed (value) > 0);
                break;
        }
    }


    private void handleCrossfade (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                transport.setCrossfade (value);
                break;
            case KNOB_MODE_RELATIVE1:
                transport.changeCrossfade (value);
                break;
            case KNOB_MODE_RELATIVE2:
                transport.setCrossfade ((int) (transport.getCrossfade () + this.relative2ValueChanger.calcKnobSpeed (value)));
                break;
            case KNOB_MODE_RELATIVE3:
                transport.setCrossfade ((int) (transport.getCrossfade () + this.relative3ValueChanger.calcKnobSpeed (value)));
                break;
        }
    }


    private void handleMetronomeVolume (final int knobMode, final int value)
    {
        final ITransport transport = this.model.getTransport ();
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                transport.setMetronomeVolume (value);
                break;
            case KNOB_MODE_RELATIVE1:
                transport.changeMetronomeVolume (value);
                break;
            case KNOB_MODE_RELATIVE2:
                transport.setMetronomeVolume (transport.getMetronomeVolume () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                transport.setMetronomeVolume (transport.getMetronomeVolume () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }
    }


    private void changeTrackVolume (final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = getTrack (trackIndex);
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                track.setVolume (value);
                break;
            case KNOB_MODE_RELATIVE1:
                track.changeVolume (value);
                break;
            case KNOB_MODE_RELATIVE2:
                track.setVolume (track.getVolume () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                track.setVolume (track.getVolume () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }
    }


    private void changeMasterVolume (final int knobMode, final int value)
    {
        final ITrack track = this.model.getMasterTrack ();
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                track.setVolume (value);
                break;
            case KNOB_MODE_RELATIVE1:
                track.changeVolume (value);
                break;
            case KNOB_MODE_RELATIVE2:
                track.setVolume (track.getVolume () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                track.setVolume (track.getVolume () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }
    }


    private void changeTrackPanorama (final int knobMode, final int trackIndex, final int value)
    {
        final ITrack track = this.getTrack (trackIndex);
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                track.setPan (value);
                break;
            case KNOB_MODE_RELATIVE1:
                track.changePan (value);
                break;
            case KNOB_MODE_RELATIVE2:
                track.setPan (track.getPan () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                track.setPan (track.getPan () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }
    }


    private void changeMasterPanorama (final int knobMode, final int value)
    {
        final ITrack track = this.model.getMasterTrack ();
        switch (knobMode)
        {
            case KNOB_MODE_ABSOLUTE:
                track.setPan (value);
                break;
            case KNOB_MODE_RELATIVE1:
                track.changePan (value);
                break;
            case KNOB_MODE_RELATIVE2:
                track.setPan (track.getPan () + this.relative2ValueChanger.calcKnobSpeed (value));
                break;
            case KNOB_MODE_RELATIVE3:
                track.setPan (track.getPan () + this.relative3ValueChanger.calcKnobSpeed (value));
                break;
        }
    }


    private void scrollTrackLeft (final boolean switchBank)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () - 1;
        if (index == -1 || switchBank)
        {
            this.scrollTrackBankLeft (sel, index);
            return;
        }
        tb.getTrack (index).selectAndMakeVisible ();
    }


    private void scrollTrackBankLeft (final ITrack sel, final int index)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollTracksUp ())
            return;
        tb.scrollTracksPageUp ();
        final int newSel = index == -1 || sel == null ? 7 : sel.getIndex ();
        this.scheduleTask ( () -> tb.getTrack (newSel).selectAndMakeVisible (), BUTTON_REPEAT_INTERVAL);
    }


    private void scrollTrackRight (final boolean switchBank)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        final ITrack sel = tb.getSelectedTrack ();
        final int index = sel == null ? 0 : sel.getIndex () + 1;
        if (index == 8 || switchBank)
        {
            this.scrollTrackBankRight (sel, index);
            return;
        }
        tb.getTrack (index).selectAndMakeVisible ();
    }


    private void scrollTrackBankRight (final ITrack sel, final int index)
    {
        final IChannelBank tb = this.model.getCurrentTrackBank ();
        if (!tb.canScrollTracksDown ())
            return;
        tb.scrollTracksPageDown ();
        final int newSel = index == 8 || sel == null ? 0 : sel.getIndex ();
        this.scheduleTask ( () -> tb.getTrack (newSel).selectAndMakeVisible (), BUTTON_REPEAT_INTERVAL);
    }


    private ITrack getTrack (int trackIndex)
    {
        final ITrackBank tb = this.model.getTrackBank ();
        return trackIndex < 0 ? tb.getSelectedTrack () : tb.getTrack (trackIndex);
    }
}