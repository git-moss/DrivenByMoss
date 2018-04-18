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

        final int ccCommand = this.configuration.getCcCommand (cc);
        if (ccCommand == 0 || this.configuration.getMidiChannel (cc) != channel)
            return;

        this.handleCommand (ccCommand, cc, value);
    }


    private void handleCommand (final int ccCommand, final int cc, final int value)
    {
        switch (ccCommand)
        {
            // Global: Undo
            case 1:
                if (value > 0)
                    this.model.getApplication ().undo ();
                break;
            // Global: Redo
            case 2:
                if (value > 0)
                    this.model.getApplication ().redo ();
                break;
            // Global: Previous Project
            case 3:
                if (value > 0)
                    this.model.getProject ().previous ();
                break;
            // Global: Next Project
            case 4:
                if (value > 0)
                    this.model.getProject ().next ();
                break;
            // Global: Toggle Audio Engine
            case 5:
                if (value > 0)
                    this.model.getApplication ().toggleEngineActive ();
                break;

            // Transport: Play
            case 6:
                if (value > 0)
                    this.model.getTransport ().play ();
                break;
            // Transport: Stop
            case 7:
                if (value > 0)
                    this.model.getTransport ().stop ();
                break;
            // Transport: Restart
            case 8:
                if (value > 0)
                    this.model.getTransport ().restart ();
                break;
            // Transport: Toggle Repeat
            case 9:
                if (value > 0)
                    this.model.getTransport ().toggleLoop ();
                break;
            // Transport: Toggle Metronome
            case 10:
                if (value > 0)
                    this.model.getTransport ().toggleMetronome ();
                break;
            // Transport: Set Metronome Volume
            case 11:
                this.handleMetronomeVolume (this.configuration.getKnobMode (cc), value);
                break;
            // Transport: Toggle Metronome in Pre-roll
            case 12:
                if (value > 0)
                    this.model.getTransport ().togglePrerollMetronome ();
                break;
            // Transport: Toggle Punch In
            case 13:
                if (value > 0)
                    this.model.getTransport ().togglePunchIn ();
                break;
            // Transport: Toggle Punch Out
            case 14:
                if (value > 0)
                    this.model.getTransport ().togglePunchOut ();
                break;
            // Transport: Toggle Record
            case 15:
                if (value > 0)
                    this.model.getTransport ().record ();
                break;
            // Transport: Toggle Arranger Overdub
            case 16:
                if (value > 0)
                    this.model.getTransport ().toggleOverdub ();
                break;
            // Transport: Toggle Clip Overdub
            case 17:
                if (value > 0)
                    this.model.getTransport ().toggleLauncherOverdub ();
                break;
            // Transport: Set Crossfader
            case 18:
                this.handleCrossfade (this.configuration.getKnobMode (cc), value);
                break;
            // Transport: Toggle Arranger Automation Write
            case 19:
                if (value > 0)
                    this.model.getTransport ().toggleWriteArrangerAutomation ();
                break;
            // Transport: Toggle Clip Automation Write
            case 20:
                if (value > 0)
                    this.model.getTransport ().toggleWriteClipLauncherAutomation ();
                break;
            // Transport: Set Write Mode: Latch
            case 21:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[0]);
                break;
            // Transport: Set Write Mode: Touch
            case 22:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[1]);
                break;
            // Transport: Set Write Mode: Write
            case 23:
                if (value > 0)
                    this.model.getTransport ().setAutomationWriteMode (ITransport.AUTOMATION_MODES_VALUES[2]);
                break;
            // Transport: Set Tempo
            case 24:
                this.handleTempo (this.configuration.getKnobMode (cc), value);
                break;
            // Transport: Tap Tempo
            case 25:
                if (value > 0)
                    this.model.getTransport ().tapTempo ();
                break;
            // Transport: Move Play Cursor
            case 26:
                this.handlePlayCursor (this.configuration.getKnobMode (cc), value);
                break;

            // Layout: Set Arrange Layout
            case 27:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_ARRANGE);
                break;
            // Layout: Set Mix Layout
            case 28:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_MIX);
                break;
            // Layout: Set Edit Layout
            case 29:
                if (value > 0)
                    this.model.getApplication ().setPanelLayout (IApplication.PANEL_LAYOUT_EDIT);
                break;
            // Layout: Toggle Note Editor
            case 30:
                if (value > 0)
                    this.model.getApplication ().toggleNoteEditor ();
                break;
            // Layout: Toggle Automation Editor
            case 31:
                if (value > 0)
                    this.model.getApplication ().toggleAutomationEditor ();
                break;
            // Layout: Toggle Devices Panel
            case 32:
                if (value > 0)
                    this.model.getApplication ().toggleDevices ();
                break;
            // Layout: Toggle Mixer Panel
            case 33:
                if (value > 0)
                    this.model.getApplication ().toggleMixer ();
                break;
            // Layout: Toggle Fullscreen
            case 34:
                if (value > 0)
                    this.model.getApplication ().toggleFullScreen ();
                break;
            // Layout: Toggle Arranger Cue Markers
            case 35:
                if (value > 0)
                    this.model.getArranger ().toggleCueMarkerVisibility ();
                break;
            // Layout: Toggle Arranger Playback Follow
            case 36:
                if (value > 0)
                    this.model.getArranger ().togglePlaybackFollow ();
                break;
            // Layout: Toggle Arranger Track Row Height
            case 37:
                if (value > 0)
                    this.model.getArranger ().toggleTrackRowHeight ();
                break;
            // Layout: Toggle Arranger Clip Launcher Section
            case 38:
                if (value > 0)
                    this.model.getArranger ().toggleClipLauncher ();
                break;
            // Layout: Toggle Arranger Time Line
            case 39:
                if (value > 0)
                    this.model.getArranger ().toggleTimeLine ();
                break;
            // Layout: Toggle Arranger IO Section
            case 40:
                if (value > 0)
                    this.model.getArranger ().toggleIoSection ();
                break;
            // Layout: Toggle Arranger Effect Tracks
            case 41:
                if (value > 0)
                    this.model.getArranger ().toggleEffectTracks ();
                break;
            // Layout: Toggle Mixer Clip Launcher Section
            case 42:
                if (value > 0)
                    this.model.getMixer ().toggleClipLauncherSectionVisibility ();
                break;
            // Layout: Toggle Mixer Cross Fade Section
            case 43:
                if (value > 0)
                    this.model.getMixer ().toggleCrossFadeSectionVisibility ();
                break;
            // Layout: Toggle Mixer Device Section
            case 44:
                if (value > 0)
                    this.model.getMixer ().toggleDeviceSectionVisibility ();
                break;
            // Layout: Toggle Mixer sendsSection
            case 45:
                if (value > 0)
                    this.model.getMixer ().toggleSendsSectionVisibility ();
                break;
            // Layout: Toggle Mixer IO Section
            case 46:
                if (value > 0)
                    this.model.getMixer ().toggleIoSectionVisibility ();
                break;
            // Layout: Toggle Mixer Meter Section
            case 47:
                if (value > 0)
                    this.model.getMixer ().toggleMeterSectionVisibility ();
                break;

            // Track: Add Audio Track
            case 48:
                if (value > 0)
                    this.model.getApplication ().addAudioTrack ();
                break;
            // Track: Add Effect Track
            case 49:
                if (value > 0)
                    this.model.getApplication ().addEffectTrack ();
                break;
            // Track: Add Instrument Track
            case 50:
                if (value > 0)
                    this.model.getApplication ().addInstrumentTrack ();
                break;
            // Track: Select Previous Bank Page
            case 51:
                if (value > 0)
                    this.scrollTrackLeft (true);
                break;
            // Track: Select Next Bank Page
            case 52:
                if (value > 0)
                    this.scrollTrackRight (true);
                break;
            // Track: Select Previous Track
            case 53:
                if (value > 0)
                    this.scrollTrackLeft (false);
                break;
            // Track: Select Next Track
            case 54:
                if (value > 0)
                    this.scrollTrackRight (true);
                break;
            // Track 1-8: Select
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 55).selectAndMakeVisible ();
                break;
            // Track 1-8: Toggle Active
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 63).toggleIsActivated ();
                break;
            case 71:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleIsActivated ();
                }
                break;
            // Track 1-8: Set Volume
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                this.changeTrackVolume (this.configuration.getKnobMode (cc), ccCommand - 72, value);
                break;
            // Track Selected: Set Volume Track
            case 80:
                this.changeTrackVolume (this.configuration.getKnobMode (cc), -1, value);
                break;
            // Track 1-8: Set Panorama
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
                this.changeTrackPanorama (this.configuration.getKnobMode (cc), ccCommand - 81, value);
                break;
            // Track Selected: Set Panorama
            case 89:
                this.changeTrackPanorama (this.configuration.getKnobMode (cc), -1, value);
                break;
            // Track 1-8: Toggle Mute
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 90).toggleMute ();
                break;
            // Track Selected: Toggle Mute
            case 98:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMute ();
                }
                break;
            // Track 1-8: Toggle Solo
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 99).toggleSolo ();
                break;
            // Track Selected: Toggle Solo
            case 107:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleSolo ();
                }
                break;
            // Track 1-8: Toggle Arm
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 108).toggleRecArm ();
                break;
            // Track Selected: Toggle Arm
            case 116:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleRecArm ();
                }
                break;
            // Track 1-8: Toggle Monitor
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 117).toggleMonitor ();
                break;
            // Track Selected: Toggle Monitor
            case 125:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleMonitor ();
                }
                break;
            // Track 1: Toggle Auto Monitor
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
                if (value > 0)
                    this.model.getTrackBank ().getTrack (ccCommand - 126).toggleAutoMonitor ();
                break;
            // Track Selected: Toggle Auto Monitor
            case 134:
                if (value > 0)
                {
                    final ITrack selectedTrack = this.model.getTrackBank ().getSelectedTrack ();
                    if (selectedTrack != null)
                        selectedTrack.toggleAutoMonitor ();
                }
                break;

            // Track 1-8: Set Send 1
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
                this.changeSendVolume (0, this.configuration.getKnobMode (cc), ccCommand - 135, value);
                break;

            // Track 1-8: Set Send 2
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
                this.changeSendVolume (1, this.configuration.getKnobMode (cc), ccCommand - 143, value);
                break;

            // Track 1-8: Set Send 3
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
                this.changeSendVolume (2, this.configuration.getKnobMode (cc), ccCommand - 151, value);
                break;

            // Track 1-8: Set Send 4
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
                this.changeSendVolume (3, this.configuration.getKnobMode (cc), ccCommand - 159, value);
                break;

            // Track 1: Set Send 5
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
                this.changeSendVolume (4, this.configuration.getKnobMode (cc), ccCommand - 167, value);
                break;

            // Track 1: Set Send 6
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
                this.changeSendVolume (5, this.configuration.getKnobMode (cc), ccCommand - 175, value);
                break;

            // Track 1-8: Set Send 7
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
            case 190:
                this.changeSendVolume (6, this.configuration.getKnobMode (cc), ccCommand - 183, value);
                break;

            // Track 1-8: Set Send 8
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
                this.changeSendVolume (7, this.configuration.getKnobMode (cc), ccCommand - 191, value);
                break;

            // Track Selected: Set Send 1-8
            case 199:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
                this.changeSendVolume (ccCommand - 199, this.configuration.getKnobMode (cc), -1, value);
                break;

            // Master: Set Volume
            case 207:
                this.changeMasterVolume (this.configuration.getKnobMode (cc), value);
                break;
            // Master: Set Panorama
            case 208:
                this.changeMasterPanorama (this.configuration.getKnobMode (cc), value);
                break;
            // Master: Toggle Mute
            case 209:
                if (value > 0)
                    this.model.getMasterTrack ().toggleMute ();
                break;
            // Master: Toggle Solo
            case 210:
                if (value > 0)
                    this.model.getMasterTrack ().toggleSolo ();
                break;
            // Master: Toggle Arm
            case 211:
                if (value > 0)
                    this.model.getMasterTrack ().toggleRecArm ();
                break;

            // Device: Toggle Window
            case 212:
                if (value > 0)
                    this.model.getCursorDevice ().toggleWindowOpen ();
                break;
            // Device: Bypass
            case 213:
                if (value > 0)
                    this.model.getCursorDevice ().toggleEnabledState ();
                break;
            // Device: Expand
            case 214:
                if (value > 0)
                    this.model.getCursorDevice ().toggleExpanded ();
                break;
            // Device: Select Previous
            case 215:
                if (value > 0)
                    this.model.getCursorDevice ().selectPrevious ();
                break;
            // Device: Select Next
            case 216:
                if (value > 0)
                    this.model.getCursorDevice ().selectNext ();
                break;
            // Device: Select Previous Parameter Bank
            case 217:
                if (value > 0)
                    this.model.getCursorDevice ().previousParameterPage ();
                break;
            // Device: Select Next Parameter Bank
            case 218:
                if (value > 0)
                    this.model.getCursorDevice ().nextParameterPage ();
                break;

            // Device: Set Parameter 1-8
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
                this.handleParameter (this.configuration.getKnobMode (cc), ccCommand - 219, value);
                break;

            // Scene 1-8: Launch Scene
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
                if (value > 0)
                    this.model.getSceneBank ().launchScene (ccCommand - 227);
                break;

            // Scene: Select Previous Bank
            case 235:
                if (value > 0)
                    this.model.getSceneBank ().scrollScenesPageUp ();
                break;
            // Scene: Select Next Bank
            case 236:
                if (value > 0)
                    this.model.getSceneBank ().scrollScenesPageDown ();
                break;
            // Scene: Create Scene from playing Clips
            case 237:
                if (value > 0)
                    this.model.getProject ().createSceneFromPlayingLauncherClips ();
                break;
            // Browser: Browse Presets
            case 238:
                if (value > 0)
                    this.model.getBrowser ().browseForPresets ();
                break;
            // Browser: Insert Device before current
            case 239:
                if (value > 0)
                    this.model.getCursorDevice ().browseToInsertBeforeDevice ();
                break;
            // Browser: Insert Device after current
            case 240:
                if (value > 0)
                    this.model.getCursorDevice ().browseToInsertAfterDevice ();
                break;
            // Browser: Commit Selection
            case 241:
                if (value > 0)
                    this.model.getBrowser ().stopBrowsing (true);
                break;
            // Browser: Cancel Selection
            case 242:
                if (value > 0)
                    this.model.getBrowser ().stopBrowsing (false);
                break;

            // Browser: Select Previous Filter in Column 1-6
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 248:
                if (value > 0)
                    this.model.getBrowser ().selectPreviousFilterItem (ccCommand - 243);
                break;

            // Browser: Select Next Filter in Column 1-6
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
                if (value > 0)
                    this.model.getBrowser ().selectNextFilterItem (ccCommand - 249);
                break;

            // Browser: Reset Filter Column 1-6
            case 255:
            case 256:
            case 257:
            case 258:
            case 259:
            case 260:
                if (value > 0)
                    this.model.getBrowser ().resetFilterColumn (ccCommand - 255);
                break;

            // Browser: Select the previous preset
            case 261:
                if (value > 0)
                    this.model.getBrowser ().selectPreviousResult ();
                break;
            // Browser: Select the next preset
            case 262:
                if (value > 0)
                    this.model.getBrowser ().selectNextResult ();
                break;
            // Browser: Select the previous tab
            case 263:
                if (value > 0)
                    this.model.getBrowser ().previousContentType ();
                break;
            // Browser: Select the next tab"
            case 264:
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