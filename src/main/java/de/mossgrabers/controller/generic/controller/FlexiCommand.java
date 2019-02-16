// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.generic.controller;

import java.util.HashMap;
import java.util.Map;


/**
 * All available commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public enum FlexiCommand
{
    OFF(null, "Off", false),

    GLOBAL_UNDO(CommandCategory.GLOBAL, "Global: Undo", true),
    GLOBAL_REDO(CommandCategory.GLOBAL, "Global: Redo", true),
    GLOBAL_PREVIOUS_PROJECT(CommandCategory.GLOBAL, "Global: Previous Project", true),
    GLOBAL_NEXT_PROJECT(CommandCategory.GLOBAL, "Global: Next Project", true),
    GLOBAL_TOGGLE_AUDIO_ENGINE(CommandCategory.GLOBAL, "Global: Toggle Audio Engine", true),

    TRANSPORT_PLAY(CommandCategory.TRANSPORT, "Transport: Play", true),
    TRANSPORT_STOP(CommandCategory.TRANSPORT, "Transport: Stop", true),
    TRANSPORT_RESTART(CommandCategory.TRANSPORT, "Transport: Restart", true),
    TRANSPORT_REWIND(CommandCategory.TRANSPORT, "Transport: Rewind", true),
    TRANSPORT_FAST_FORWARD(CommandCategory.TRANSPORT, "Transport: Fast Forward", true),
    TRANSPORT_TOGGLE_REPEAT(CommandCategory.TRANSPORT, "Transport: Toggle Repeat", true),
    TRANSPORT_TOGGLE_METRONOME(CommandCategory.TRANSPORT, "Transport: Toggle Metronome", true),
    TRANSPORT_SET_METRONOME_VOLUME(CommandCategory.TRANSPORT, "Transport: Set Metronome Volume", false),
    TRANSPORT_TOGGLE_METRONOME_IN_PREROLL(CommandCategory.TRANSPORT, "Transport: Toggle Metronome in Pre-roll", true),
    TRANSPORT_TOGGLE_PUNCH_IN(CommandCategory.TRANSPORT, "Transport: Toggle Punch In", true),
    TRANSPORT_TOGGLE_PUNCH_OUT(CommandCategory.TRANSPORT, "Transport: Toggle Punch Out", true),
    TRANSPORT_TOGGLE_RECORD(CommandCategory.TRANSPORT, "Transport: Toggle Record", true),
    TRANSPORT_TOGGLE_ARRANGER_OVERDUB(CommandCategory.TRANSPORT, "Transport: Toggle Arranger Overdub", true),
    TRANSPORT_TOGGLE_CLIP_OVERDUB(CommandCategory.TRANSPORT, "Transport: Toggle Clip Overdub", true),
    TRANSPORT_TOGGLE_ARRANGER_AUTOMATION_WRITE(CommandCategory.TRANSPORT, "Transport: Toggle Arranger Automation Write", true),
    TRANSPORT_TOGGLE_CLIP_AUTOMATION_WRITE(CommandCategory.TRANSPORT, "Transport: Toggle Clip Automation Write", true),
    TRANSPORT_SET_WRITE_MODE_LATCH(CommandCategory.TRANSPORT, "Transport: Set Write Mode: Latch", true),
    TRANSPORT_SET_WRITE_MODE_TOUCH(CommandCategory.TRANSPORT, "Transport: Set Write Mode: Touch", true),
    TRANSPORT_SET_WRITE_MODE_WRITE(CommandCategory.TRANSPORT, "Transport: Set Write Mode: Write", true),
    TRANSPORT_SET_TEMPO(CommandCategory.TRANSPORT, "Transport: Set Tempo", false),
    TRANSPORT_TAP_TEMPO(CommandCategory.TRANSPORT, "Transport: Tap Tempo", true),
    TRANSPORT_MOVE_PLAY_CURSOR(CommandCategory.TRANSPORT, "Transport: Move Play Cursor", false),

    LAYOUT_SET_ARRANGE_LAYOUT(CommandCategory.LAYOUT, "Layout: Set Arrange Layout", true),
    LAYOUT_SET_MIX_LAYOUT(CommandCategory.LAYOUT, "Layout: Set Mix Layout", true),
    LAYOUT_SET_EDIT_LAYOUT(CommandCategory.LAYOUT, "Layout: Set Edit Layout", true),
    LAYOUT_TOGGLE_NOTE_EDITOR(CommandCategory.LAYOUT, "Layout: Toggle Note Editor", true),
    LAYOUT_TOGGLE_AUTOMATION_EDITOR(CommandCategory.LAYOUT, "Layout: Toggle Automation Editor", true),
    LAYOUT_TOGGLE_DEVICES_PANEL(CommandCategory.LAYOUT, "Layout: Toggle Devices Panel", true),
    LAYOUT_TOGGLE_MIXER_PANEL(CommandCategory.LAYOUT, "Layout: Toggle Mixer Panel", true),
    LAYOUT_TOGGLE_FULLSCREEN(CommandCategory.LAYOUT, "Layout: Toggle Fullscreen", true),
    LAYOUT_TOGGLE_ARRANGER_CUE_MARKERS(CommandCategory.LAYOUT, "Layout: Toggle Arranger Cue Markers", true),
    LAYOUT_TOGGLE_ARRANGER_PLAYBACK_FOLLOW(CommandCategory.LAYOUT, "Layout: Toggle Arranger Playback Follow", true),
    LAYOUT_TOGGLE_ARRANGER_TRACK_ROW_HEIGHT(CommandCategory.LAYOUT, "Layout: Toggle Arranger Track Row Height", true),
    LAYOUT_TOGGLE_ARRANGER_CLIP_LAUNCHER_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Arranger Clip Launcher Section", true),
    LAYOUT_TOGGLE_ARRANGER_TIME_LINE(CommandCategory.LAYOUT, "Layout: Toggle Arranger Time Line", true),
    LAYOUT_TOGGLE_ARRANGER_IO_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Arranger IO Section", true),
    LAYOUT_TOGGLE_ARRANGER_EFFECT_TRACKS(CommandCategory.LAYOUT, "Layout: Toggle Arranger Effect Tracks", true),
    LAYOUT_TOGGLE_MIXER_CLIP_LAUNCHER_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Mixer Clip Launcher Section", true),
    LAYOUT_TOGGLE_MIXER_CROSS_FADE_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Mixer Cross Fade Section", true),
    LAYOUT_TOGGLE_MIXER_DEVICE_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Mixer Device Section", true),
    LAYOUT_TOGGLE_MIXER_SENDSSECTION(CommandCategory.LAYOUT, "Layout: Toggle Mixer sendsSection", true),
    LAYOUT_TOGGLE_MIXER_IO_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Mixer IO Section", true),
    LAYOUT_TOGGLE_MIXER_METER_SECTION(CommandCategory.LAYOUT, "Layout: Toggle Mixer Meter Section", true),

    TRACK_TOGGLE_TRACK_BANK(CommandCategory.TRACK, "Track: Toggle Trackbank", true),
    TRACK_ADD_AUDIO_TRACK(CommandCategory.TRACK, "Track: Add Audio Track", true),
    TRACK_ADD_EFFECT_TRACK(CommandCategory.TRACK, "Track: Add Effect Track", true),
    TRACK_ADD_INSTRUMENT_TRACK(CommandCategory.TRACK, "Track: Add Instrument Track", true),
    TRACK_SELECT_PREVIOUS_BANK_PAGE(CommandCategory.TRACK, "Track: Select Previous Bank Page", true),
    TRACK_SELECT_NEXT_BANK_PAGE(CommandCategory.TRACK, "Track: Select Next Bank Page", true),
    TRACK_SELECT_PREVIOUS_TRACK(CommandCategory.TRACK, "Track: Select Previous Track", true),
    TRACK_SELECT_NEXT_TRACK(CommandCategory.TRACK, "Track: Select Next Track", true),
    TRACK_SCROLL_TRACKS(CommandCategory.TRACK, "Track: Scroll Tracks", false),
    TRACK_1_SELECT(CommandCategory.TRACK, "Track 1: Select", true),
    TRACK_2_SELECT(CommandCategory.TRACK, "Track 2: Select", true),
    TRACK_3_SELECT(CommandCategory.TRACK, "Track 3: Select", true),
    TRACK_4_SELECT(CommandCategory.TRACK, "Track 4: Select", true),
    TRACK_5_SELECT(CommandCategory.TRACK, "Track 5: Select", true),
    TRACK_6_SELECT(CommandCategory.TRACK, "Track 6: Select", true),
    TRACK_7_SELECT(CommandCategory.TRACK, "Track 7: Select", true),
    TRACK_8_SELECT(CommandCategory.TRACK, "Track 8: Select", true),
    TRACK_1_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 1: Toggle Active", true),
    TRACK_2_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 2: Toggle Active", true),
    TRACK_3_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 3: Toggle Active", true),
    TRACK_4_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 4: Toggle Active", true),
    TRACK_5_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 5: Toggle Active", true),
    TRACK_6_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 6: Toggle Active", true),
    TRACK_7_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 7: Toggle Active", true),
    TRACK_8_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track 8: Toggle Active", true),
    TRACK_SELECTED_TOGGLE_ACTIVE(CommandCategory.TRACK, "Track Selected: Toggle Active", true),
    TRACK_1_SET_VOLUME(CommandCategory.TRACK, "Track 1: Set Volume", false),
    TRACK_2_SET_VOLUME(CommandCategory.TRACK, "Track 2: Set Volume", false),
    TRACK_3_SET_VOLUME(CommandCategory.TRACK, "Track 3: Set Volume", false),
    TRACK_4_SET_VOLUME(CommandCategory.TRACK, "Track 4: Set Volume", false),
    TRACK_5_SET_VOLUME(CommandCategory.TRACK, "Track 5: Set Volume", false),
    TRACK_6_SET_VOLUME(CommandCategory.TRACK, "Track 6: Set Volume", false),
    TRACK_7_SET_VOLUME(CommandCategory.TRACK, "Track 7: Set Volume", false),
    TRACK_8_SET_VOLUME(CommandCategory.TRACK, "Track 8: Set Volume", false),
    TRACK_SELECTED_SET_VOLUME_TRACK(CommandCategory.TRACK, "Track Selected: Set Volume Track", false),
    TRACK_1_SET_PANORAMA(CommandCategory.TRACK, "Track 1: Set Panorama", false),
    TRACK_2_SET_PANORAMA(CommandCategory.TRACK, "Track 2: Set Panorama", false),
    TRACK_3_SET_PANORAMA(CommandCategory.TRACK, "Track 3: Set Panorama", false),
    TRACK_4_SET_PANORAMA(CommandCategory.TRACK, "Track 4: Set Panorama", false),
    TRACK_5_SET_PANORAMA(CommandCategory.TRACK, "Track 5: Set Panorama", false),
    TRACK_6_SET_PANORAMA(CommandCategory.TRACK, "Track 6: Set Panorama", false),
    TRACK_7_SET_PANORAMA(CommandCategory.TRACK, "Track 7: Set Panorama", false),
    TRACK_8_SET_PANORAMA(CommandCategory.TRACK, "Track 8: Set Panorama", false),
    TRACK_SELECTED_SET_PANORAMA(CommandCategory.TRACK, "Track Selected: Set Panorama", false),
    TRACK_1_TOGGLE_MUTE(CommandCategory.TRACK, "Track 1: Toggle Mute", true),
    TRACK_2_TOGGLE_MUTE(CommandCategory.TRACK, "Track 2: Toggle Mute", true),
    TRACK_3_TOGGLE_MUTE(CommandCategory.TRACK, "Track 3: Toggle Mute", true),
    TRACK_4_TOGGLE_MUTE(CommandCategory.TRACK, "Track 4: Toggle Mute", true),
    TRACK_5_TOGGLE_MUTE(CommandCategory.TRACK, "Track 5: Toggle Mute", true),
    TRACK_6_TOGGLE_MUTE(CommandCategory.TRACK, "Track 6: Toggle Mute", true),
    TRACK_7_TOGGLE_MUTE(CommandCategory.TRACK, "Track 7: Toggle Mute", true),
    TRACK_8_TOGGLE_MUTE(CommandCategory.TRACK, "Track 8: Toggle Mute", true),
    TRACK_SELECTED_TOGGLE_MUTE(CommandCategory.TRACK, "Track Selected: Toggle Mute", true),
    TRACK_1_TOGGLE_SOLO(CommandCategory.TRACK, "Track 1: Toggle Solo", true),
    TRACK_2_TOGGLE_SOLO(CommandCategory.TRACK, "Track 2: Toggle Solo", true),
    TRACK_3_TOGGLE_SOLO(CommandCategory.TRACK, "Track 3: Toggle Solo", true),
    TRACK_4_TOGGLE_SOLO(CommandCategory.TRACK, "Track 4: Toggle Solo", true),
    TRACK_5_TOGGLE_SOLO(CommandCategory.TRACK, "Track 5: Toggle Solo", true),
    TRACK_6_TOGGLE_SOLO(CommandCategory.TRACK, "Track 6: Toggle Solo", true),
    TRACK_7_TOGGLE_SOLO(CommandCategory.TRACK, "Track 7: Toggle Solo", true),
    TRACK_8_TOGGLE_SOLO(CommandCategory.TRACK, "Track 8: Toggle Solo", true),
    TRACK_SELECTED_TOGGLE_SOLO(CommandCategory.TRACK, "Track Selected: Toggle Solo", true),
    TRACK_1_TOGGLE_ARM(CommandCategory.TRACK, "Track 1: Toggle Arm", true),
    TRACK_2_TOGGLE_ARM(CommandCategory.TRACK, "Track 2: Toggle Arm", true),
    TRACK_3_TOGGLE_ARM(CommandCategory.TRACK, "Track 3: Toggle Arm", true),
    TRACK_4_TOGGLE_ARM(CommandCategory.TRACK, "Track 4: Toggle Arm", true),
    TRACK_5_TOGGLE_ARM(CommandCategory.TRACK, "Track 5: Toggle Arm", true),
    TRACK_6_TOGGLE_ARM(CommandCategory.TRACK, "Track 6: Toggle Arm", true),
    TRACK_7_TOGGLE_ARM(CommandCategory.TRACK, "Track 7: Toggle Arm", true),
    TRACK_8_TOGGLE_ARM(CommandCategory.TRACK, "Track 8: Toggle Arm", true),
    TRACK_SELECTED_TOGGLE_ARM(CommandCategory.TRACK, "Track Selected: Toggle Arm", true),
    TRACK_1_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 1: Toggle Monitor", true),
    TRACK_2_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 2: Toggle Monitor", true),
    TRACK_3_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 3: Toggle Monitor", true),
    TRACK_4_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 4: Toggle Monitor", true),
    TRACK_5_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 5: Toggle Monitor", true),
    TRACK_6_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 6: Toggle Monitor", true),
    TRACK_7_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 7: Toggle Monitor", true),
    TRACK_8_TOGGLE_MONITOR(CommandCategory.TRACK, "Track 8: Toggle Monitor", true),
    TRACK_SELECTED_TOGGLE_MONITOR(CommandCategory.TRACK, "Track Selected: Toggle Monitor", true),
    TRACK_1_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 1: Toggle Auto Monitor", true),
    TRACK_2_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 2: Toggle Auto Monitor", true),
    TRACK_3_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 3: Toggle Auto Monitor", true),
    TRACK_4_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 4: Toggle Auto Monitor", true),
    TRACK_5_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 5: Toggle Auto Monitor", true),
    TRACK_6_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 6: Toggle Auto Monitor", true),
    TRACK_7_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 7: Toggle Auto Monitor", true),
    TRACK_8_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track 8: Toggle Auto Monitor", true),
    TRACK_SELECTED_TOGGLE_AUTO_MONITOR(CommandCategory.TRACK, "Track Selected: Toggle Auto Monitor", true),
    TRACK_1_SET_SEND_1(CommandCategory.TRACK, "Track 1: Set Send 1", false),
    TRACK_2_SET_SEND_1(CommandCategory.TRACK, "Track 2: Set Send 1", false),
    TRACK_3_SET_SEND_1(CommandCategory.TRACK, "Track 3: Set Send 1", false),
    TRACK_4_SET_SEND_1(CommandCategory.TRACK, "Track 4: Set Send 1", false),
    TRACK_5_SET_SEND_1(CommandCategory.TRACK, "Track 5: Set Send 1", false),
    TRACK_6_SET_SEND_1(CommandCategory.TRACK, "Track 6: Set Send 1", false),
    TRACK_7_SET_SEND_1(CommandCategory.TRACK, "Track 7: Set Send 1", false),
    TRACK_8_SET_SEND_1(CommandCategory.TRACK, "Track 8: Set Send 1", false),
    TRACK_1_SET_SEND_2(CommandCategory.TRACK, "Track 1: Set Send 2", false),
    TRACK_2_SET_SEND_2(CommandCategory.TRACK, "Track 2: Set Send 2", false),
    TRACK_3_SET_SEND_2(CommandCategory.TRACK, "Track 3: Set Send 2", false),
    TRACK_4_SET_SEND_2(CommandCategory.TRACK, "Track 4: Set Send 2", false),
    TRACK_5_SET_SEND_2(CommandCategory.TRACK, "Track 5: Set Send 2", false),
    TRACK_6_SET_SEND_2(CommandCategory.TRACK, "Track 6: Set Send 2", false),
    TRACK_7_SET_SEND_2(CommandCategory.TRACK, "Track 7: Set Send 2", false),
    TRACK_8_SET_SEND_2(CommandCategory.TRACK, "Track 8: Set Send 2", false),
    TRACK_1_SET_SEND_3(CommandCategory.TRACK, "Track 1: Set Send 3", false),
    TRACK_2_SET_SEND_3(CommandCategory.TRACK, "Track 2: Set Send 3", false),
    TRACK_3_SET_SEND_3(CommandCategory.TRACK, "Track 3: Set Send 3", false),
    TRACK_4_SET_SEND_3(CommandCategory.TRACK, "Track 4: Set Send 3", false),
    TRACK_5_SET_SEND_3(CommandCategory.TRACK, "Track 5: Set Send 3", false),
    TRACK_6_SET_SEND_3(CommandCategory.TRACK, "Track 6: Set Send 3", false),
    TRACK_7_SET_SEND_3(CommandCategory.TRACK, "Track 7: Set Send 3", false),
    TRACK_8_SET_SEND_3(CommandCategory.TRACK, "Track 8: Set Send 3", false),
    TRACK_1_SET_SEND_4(CommandCategory.TRACK, "Track 1: Set Send 4", false),
    TRACK_2_SET_SEND_4(CommandCategory.TRACK, "Track 2: Set Send 4", false),
    TRACK_3_SET_SEND_4(CommandCategory.TRACK, "Track 3: Set Send 4", false),
    TRACK_4_SET_SEND_4(CommandCategory.TRACK, "Track 4: Set Send 4", false),
    TRACK_5_SET_SEND_4(CommandCategory.TRACK, "Track 5: Set Send 4", false),
    TRACK_6_SET_SEND_4(CommandCategory.TRACK, "Track 6: Set Send 4", false),
    TRACK_7_SET_SEND_4(CommandCategory.TRACK, "Track 7: Set Send 4", false),
    TRACK_8_SET_SEND_4(CommandCategory.TRACK, "Track 8: Set Send 4", false),
    TRACK_1_SET_SEND_5(CommandCategory.TRACK, "Track 1: Set Send 5", false),
    TRACK_2_SET_SEND_5(CommandCategory.TRACK, "Track 2: Set Send 5", false),
    TRACK_3_SET_SEND_5(CommandCategory.TRACK, "Track 3: Set Send 5", false),
    TRACK_4_SET_SEND_5(CommandCategory.TRACK, "Track 4: Set Send 5", false),
    TRACK_5_SET_SEND_5(CommandCategory.TRACK, "Track 5: Set Send 5", false),
    TRACK_6_SET_SEND_5(CommandCategory.TRACK, "Track 6: Set Send 5", false),
    TRACK_7_SET_SEND_5(CommandCategory.TRACK, "Track 7: Set Send 5", false),
    TRACK_8_SET_SEND_5(CommandCategory.TRACK, "Track 8: Set Send 5", false),
    TRACK_1_SET_SEND_6(CommandCategory.TRACK, "Track 1: Set Send 6", false),
    TRACK_2_SET_SEND_6(CommandCategory.TRACK, "Track 2: Set Send 6", false),
    TRACK_3_SET_SEND_6(CommandCategory.TRACK, "Track 3: Set Send 6", false),
    TRACK_4_SET_SEND_6(CommandCategory.TRACK, "Track 4: Set Send 6", false),
    TRACK_5_SET_SEND_6(CommandCategory.TRACK, "Track 5: Set Send 6", false),
    TRACK_6_SET_SEND_6(CommandCategory.TRACK, "Track 6: Set Send 6", false),
    TRACK_7_SET_SEND_6(CommandCategory.TRACK, "Track 7: Set Send 6", false),
    TRACK_8_SET_SEND_6(CommandCategory.TRACK, "Track 8: Set Send 6", false),
    TRACK_1_SET_SEND_7(CommandCategory.TRACK, "Track 1: Set Send 7", false),
    TRACK_2_SET_SEND_7(CommandCategory.TRACK, "Track 2: Set Send 7", false),
    TRACK_3_SET_SEND_7(CommandCategory.TRACK, "Track 3: Set Send 7", false),
    TRACK_4_SET_SEND_7(CommandCategory.TRACK, "Track 4: Set Send 7", false),
    TRACK_5_SET_SEND_7(CommandCategory.TRACK, "Track 5: Set Send 7", false),
    TRACK_6_SET_SEND_7(CommandCategory.TRACK, "Track 6: Set Send 7", false),
    TRACK_7_SET_SEND_7(CommandCategory.TRACK, "Track 7: Set Send 7", false),
    TRACK_8_SET_SEND_7(CommandCategory.TRACK, "Track 8: Set Send 7", false),
    TRACK_1_SET_SEND_8(CommandCategory.TRACK, "Track 1: Set Send 8", false),
    TRACK_2_SET_SEND_8(CommandCategory.TRACK, "Track 2: Set Send 8", false),
    TRACK_3_SET_SEND_8(CommandCategory.TRACK, "Track 3: Set Send 8", false),
    TRACK_4_SET_SEND_8(CommandCategory.TRACK, "Track 4: Set Send 8", false),
    TRACK_5_SET_SEND_8(CommandCategory.TRACK, "Track 5: Set Send 8", false),
    TRACK_6_SET_SEND_8(CommandCategory.TRACK, "Track 6: Set Send 8", false),
    TRACK_7_SET_SEND_8(CommandCategory.TRACK, "Track 7: Set Send 8", false),
    TRACK_8_SET_SEND_8(CommandCategory.TRACK, "Track 8: Set Send 8", false),
    TRACK_SELECTED_SET_SEND_1(CommandCategory.TRACK, "Track Selected: Set Send 1", false),
    TRACK_SELECTED_SET_SEND_2(CommandCategory.TRACK, "Track Selected: Set Send 2", false),
    TRACK_SELECTED_SET_SEND_3(CommandCategory.TRACK, "Track Selected: Set Send 3", false),
    TRACK_SELECTED_SET_SEND_4(CommandCategory.TRACK, "Track Selected: Set Send 4", false),
    TRACK_SELECTED_SET_SEND_5(CommandCategory.TRACK, "Track Selected: Set Send 5", false),
    TRACK_SELECTED_SET_SEND_6(CommandCategory.TRACK, "Track Selected: Set Send 6", false),
    TRACK_SELECTED_SET_SEND_7(CommandCategory.TRACK, "Track Selected: Set Send 7", false),
    TRACK_SELECTED_SET_SEND_8(CommandCategory.TRACK, "Track Selected: Set Send 8", false),

    MASTER_SET_VOLUME(CommandCategory.MASTER, "Master: Set Volume", false),
    MASTER_SET_PANORAMA(CommandCategory.MASTER, "Master: Set Panorama", false),
    MASTER_TOGGLE_MUTE(CommandCategory.MASTER, "Master: Toggle Mute", true),
    MASTER_TOGGLE_SOLO(CommandCategory.MASTER, "Master: Toggle Solo", true),
    MASTER_TOGGLE_ARM(CommandCategory.MASTER, "Master: Toggle Arm", true),
    MASTER_CROSSFADER(CommandCategory.MASTER, "Master: Crossfader", false),

    DEVICE_TOGGLE_WINDOW(CommandCategory.DEVICE, "Device: Toggle Window", true),
    DEVICE_BYPASS(CommandCategory.DEVICE, "Device: Bypass", true),
    DEVICE_EXPAND(CommandCategory.DEVICE, "Device: Expand", true),
    DEVICE_TOGGLE_PARAMETERS(CommandCategory.DEVICE, "Device: Parameters", true),
    DEVICE_SELECT_PREVIOUS(CommandCategory.DEVICE, "Device: Select Previous", true),
    DEVICE_SELECT_NEXT(CommandCategory.DEVICE, "Device: Select Next", true),
    DEVICE_SCROLL_DEVICES(CommandCategory.DEVICE, "Device: Scroll devices", false),
    DEVICE_SELECT_PREVIOUS_PARAMETER_PAGE(CommandCategory.DEVICE, "Device: Select Previous Parameter Page", true),
    DEVICE_SELECT_NEXT_PARAMETER_PAGE(CommandCategory.DEVICE, "Device: Select Next Parameter Page", true),
    DEVICE_SCROLL_PARAMETER_PAGES(CommandCategory.DEVICE, "Device: Scroll Parameter Pages", false),
    DEVICE_SELECT_PREVIOUS_PARAMETER_BANK(CommandCategory.DEVICE, "Device: Select Previous Parameter Bank", true),
    DEVICE_SELECT_NEXT_PARAMETER_BANK(CommandCategory.DEVICE, "Device: Select Next Parameter Bank", true),
    DEVICE_SCROLL_PARAMETER_BANKS(CommandCategory.DEVICE, "Device: Scroll Parameter Banks", false),
    DEVICE_SET_PARAMETER_1(CommandCategory.DEVICE, "Device: Set Parameter 1", false),
    DEVICE_SET_PARAMETER_2(CommandCategory.DEVICE, "Device: Set Parameter 2", false),
    DEVICE_SET_PARAMETER_3(CommandCategory.DEVICE, "Device: Set Parameter 3", false),
    DEVICE_SET_PARAMETER_4(CommandCategory.DEVICE, "Device: Set Parameter 4", false),
    DEVICE_SET_PARAMETER_5(CommandCategory.DEVICE, "Device: Set Parameter 5", false),
    DEVICE_SET_PARAMETER_6(CommandCategory.DEVICE, "Device: Set Parameter 6", false),
    DEVICE_SET_PARAMETER_7(CommandCategory.DEVICE, "Device: Set Parameter 7", false),
    DEVICE_SET_PARAMETER_8(CommandCategory.DEVICE, "Device: Set Parameter 8", false),

    BROWSER_BROWSE_PRESETS(CommandCategory.BROWSER, "Browser: Browse Presets", true),
    BROWSER_INSERT_DEVICE_BEFORE_CURRENT(CommandCategory.BROWSER, "Browser: Insert Device before current", true),
    BROWSER_INSERT_DEVICE_AFTER_CURRENT(CommandCategory.BROWSER, "Browser: Insert Device after current", true),
    BROWSER_COMMIT_SELECTION(CommandCategory.BROWSER, "Browser: Commit Selection", true),
    BROWSER_CANCEL_SELECTION(CommandCategory.BROWSER, "Browser: Cancel Selection", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_1(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 1", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_2(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 2", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_3(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 3", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_4(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 4", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_5(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 5", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_6(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 6", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_7(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 7", true),
    BROWSER_SELECT_PREVIOUS_FILTER_IN_COLUMN_8(CommandCategory.BROWSER, "Browser: Select Previous Filter in Column 8", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_1(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 1", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_2(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 2", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_3(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 3", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_4(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 4", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_5(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 5", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_6(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 6", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_7(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 7", true),
    BROWSER_SELECT_NEXT_FILTER_IN_COLUMN_8(CommandCategory.BROWSER, "Browser: Select Next Filter in Column 8", true),
    BROWSER_SCROLL_FILTER_IN_COLUMN_1(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 1", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_2(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 2", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_3(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 3", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_4(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 4", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_5(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 5", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_6(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 6", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_7(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 7", false),
    BROWSER_SCROLL_FILTER_IN_COLUMN_8(CommandCategory.BROWSER, "Browser: Scroll Filter in Column 8", false),
    BROWSER_RESET_FILTER_COLUMN_1(CommandCategory.BROWSER, "Browser: Reset Filter Column 1", true),
    BROWSER_RESET_FILTER_COLUMN_2(CommandCategory.BROWSER, "Browser: Reset Filter Column 2", true),
    BROWSER_RESET_FILTER_COLUMN_3(CommandCategory.BROWSER, "Browser: Reset Filter Column 3", true),
    BROWSER_RESET_FILTER_COLUMN_4(CommandCategory.BROWSER, "Browser: Reset Filter Column 4", true),
    BROWSER_RESET_FILTER_COLUMN_5(CommandCategory.BROWSER, "Browser: Reset Filter Column 5", true),
    BROWSER_RESET_FILTER_COLUMN_6(CommandCategory.BROWSER, "Browser: Reset Filter Column 6", true),
    BROWSER_RESET_FILTER_COLUMN_7(CommandCategory.BROWSER, "Browser: Reset Filter Column 7", true),
    BROWSER_RESET_FILTER_COLUMN_8(CommandCategory.BROWSER, "Browser: Reset Filter Column 8", true),
    BROWSER_SELECT_THE_PREVIOUS_PRESET(CommandCategory.BROWSER, "Browser: Select the previous preset", true),
    BROWSER_SELECT_THE_NEXT_PRESET(CommandCategory.BROWSER, "Browser: Select the next preset", true),
    BROWSER_SCROLL_PRESETS(CommandCategory.BROWSER, "Browser: Scroll presets", false),
    BROWSER_SELECT_THE_PREVIOUS_TAB(CommandCategory.BROWSER, "Browser: Select the previous tab", true),
    BROWSER_SELECT_THE_NEXT_TAB(CommandCategory.BROWSER, "Browser: Select the next tab", true),
    BROWSER_SCROLL_TABS(CommandCategory.BROWSER, "Browser: Scroll tabs", false),

    SCENE_1_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 1: Launch Scene", true),
    SCENE_2_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 2: Launch Scene", true),
    SCENE_3_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 3: Launch Scene", true),
    SCENE_4_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 4: Launch Scene", true),
    SCENE_5_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 5: Launch Scene", true),
    SCENE_6_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 6: Launch Scene", true),
    SCENE_7_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 7: Launch Scene", true),
    SCENE_8_LAUNCH_SCENE(CommandCategory.SCENE, "Scene 8: Launch Scene", true),
    SCENE_SELECT_PREVIOUS_BANK(CommandCategory.SCENE, "Scene: Select Previous Bank", true),
    SCENE_SELECT_NEXT_BANK(CommandCategory.SCENE, "Scene: Select Next Bank", true),
    SCENE_CREATE_SCENE_FROM_PLAYING_CLIPS(CommandCategory.SCENE, "Scene: Create Scene from playing Clips", true),

    CLIP_PREVIOUS(CommandCategory.CLIP, "Clip: Select previous", true),
    CLIP_NEXT(CommandCategory.CLIP, "Clip: Select next", true),
    CLIP_SCROLL(CommandCategory.CLIP, "Clip: Scroll clips", false),
    CLIP_PLAY(CommandCategory.CLIP, "Clip: Play", true),
    CLIP_STOP(CommandCategory.CLIP, "Clip: Stop", true),
    CLIP_RECORD(CommandCategory.CLIP, "Clip: Record", true),
    CLIP_NEW(CommandCategory.CLIP, "Clip: New", true),

    MARKER_1_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 1: Launch Marker", true),
    MARKER_2_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 2: Launch Marker", true),
    MARKER_3_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 3: Launch Marker", true),
    MARKER_4_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 4: Launch Marker", true),
    MARKER_5_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 5: Launch Marker", true),
    MARKER_6_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 6: Launch Marker", true),
    MARKER_7_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 7: Launch Marker", true),
    MARKER_8_LAUNCH_MARKER(CommandCategory.MARKER, "Marker 8: Launch Marker", true),
    MARKER_SELECT_PREVIOUS_BANK(CommandCategory.MARKER, "Marker: Select Previous Bank", true),
    MARKER_SELECT_NEXT_BANK(CommandCategory.MARKER, "Marker: Select Next Bank", true),

    MODES_KNOB1(CommandCategory.MODES, "Modes: Item 1: Set value", false),
    MODES_KNOB2(CommandCategory.MODES, "Modes: Item 2: Set value", false),
    MODES_KNOB3(CommandCategory.MODES, "Modes: Item 3: Set value", false),
    MODES_KNOB4(CommandCategory.MODES, "Modes: Item 4: Set value", false),
    MODES_KNOB5(CommandCategory.MODES, "Modes: Item 5: Set value", false),
    MODES_KNOB6(CommandCategory.MODES, "Modes: Item 6: Set value", false),
    MODES_KNOB7(CommandCategory.MODES, "Modes: Item 7: Set value", false),
    MODES_KNOB8(CommandCategory.MODES, "Modes: Item 8: Set value", false),
    MODES_BUTTON1(CommandCategory.MODES, "Modes: Item 1: Select", true),
    MODES_BUTTON2(CommandCategory.MODES, "Modes: Item 2: Select", true),
    MODES_BUTTON3(CommandCategory.MODES, "Modes: Item 3: Select", true),
    MODES_BUTTON4(CommandCategory.MODES, "Modes: Item 4: Select", true),
    MODES_BUTTON5(CommandCategory.MODES, "Modes: Item 5: Select", true),
    MODES_BUTTON6(CommandCategory.MODES, "Modes: Item 6: Select", true),
    MODES_BUTTON7(CommandCategory.MODES, "Modes: Item 7: Select", true),
    MODES_BUTTON8(CommandCategory.MODES, "Modes: Item 8: Select", true),
    MODES_NEXT_ITEM(CommandCategory.MODES, "Modes: Select Next Item", true),
    MODES_PREV_ITEM(CommandCategory.MODES, "Modes: Select Previous Item", true),
    MODES_NEXT_PAGE(CommandCategory.MODES, "Modes: Select Next Item Page", true),
    MODES_PREV_PAGE(CommandCategory.MODES, "Modes: Select Previous Item Page", true),
    MODES_SELECT_MODE_TRACK(CommandCategory.MODES, "Modes: Select Track Mode", true),
    MODES_SELECT_MODE_VOLUME(CommandCategory.MODES, "Modes: Select Volume Mode", true),
    MODES_SELECT_MODE_PAN(CommandCategory.MODES, "Modes: Select Panorama Mode", true),
    MODES_SELECT_MODE_SEND1(CommandCategory.MODES, "Modes: Select Send 1 Mode", true),
    MODES_SELECT_MODE_SEND2(CommandCategory.MODES, "Modes: Select Send 2 Mode", true),
    MODES_SELECT_MODE_SEND3(CommandCategory.MODES, "Modes: Select Send 3 Mode", true),
    MODES_SELECT_MODE_SEND4(CommandCategory.MODES, "Modes: Select Send 4 Mode", true),
    MODES_SELECT_MODE_SEND5(CommandCategory.MODES, "Modes: Select Send 5 Mode", true),
    MODES_SELECT_MODE_SEND6(CommandCategory.MODES, "Modes: Select Send 6 Mode", true),
    MODES_SELECT_MODE_SEND7(CommandCategory.MODES, "Modes: Select Send 7 Mode", true),
    MODES_SELECT_MODE_SEND8(CommandCategory.MODES, "Modes: Select Send 8 Mode", true),
    MODES_SELECT_MODE_DEVICE(CommandCategory.MODES, "Modes: Select Device Mode", true),
    MODES_SELECT_MODE_NEXT(CommandCategory.MODES, "Modes: Select Next Mode", true),
    MODES_SELECT_MODE_PREV(CommandCategory.MODES, "Modes: Select Previous Mode", true),
    MODES_BROWSE_PRESETS(CommandCategory.MODES, "Modes: Start Browser: Browse Presets", true);

    private static final String []                 NAMES            = new String [FlexiCommand.values ().length];
    private static final Map<String, FlexiCommand> NAME_COMMAND_MAP = new HashMap<> (FlexiCommand.values ().length);

    static
    {
        final FlexiCommand [] values = FlexiCommand.values ();
        for (int i = 0; i < values.length; i++)
        {
            NAMES[i] = values[i].getName ();
            NAME_COMMAND_MAP.put (NAMES[i], values[i]);
        }
    }

    private final CommandCategory category;
    private final String          name;
    private final boolean         isTrigger;


    /**
     * Constructor.
     *
     * @param name The name of the command
     * @param isTrigger True if it is a trigger command (push button)
     */
    private FlexiCommand (final CommandCategory category, final String name, final boolean isTrigger)
    {
        this.category = category;
        this.name = name;
        this.isTrigger = isTrigger;
    }


    /**
     * Get the category of the command.
     *
     * @return The category
     */
    public CommandCategory getCategory ()
    {
        return this.category;
    }


    /**
     * Get the name of the command.
     *
     * @return The name
     */
    public String getName ()
    {
        return this.name;
    }


    /**
     * Get if the command is a trigger command.
     *
     * @return True if can be triggered
     */
    public boolean isTrigger ()
    {
        return this.isTrigger;
    }


    /**
     * Get the names of all commands.
     *
     * @return The names
     */
    public static String [] getNames ()
    {
        return NAMES;
    }


    /**
     * Lookup the command by its name.
     *
     * @param name The name
     * @return The command or if not found the OFF command
     */
    public static FlexiCommand lookupByName (final String name)
    {
        final FlexiCommand c = NAME_COMMAND_MAP.get (name);
        return c == null ? FlexiCommand.OFF : c;
    }
}
