// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.resource;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


/**
 * Stores information about the device types.
 *
 * @author Jürgen Moßgraber
 */
public class DeviceTypes
{
    private enum TypeIcon
    {
        ICON_WAVE,
        ICON_NOTE,
        ICON_FOLDER,
        ICON_DRUM_MACHINE,
        ICON_DRUM,
        ICON_INSTRUMENT,
        ICON_IO,
        ICON_ANALYSIS,
        ICON_GENERIC,
        ICON_PLUGIN,
        ICON_USER,
        ICON_PROJECT,
        ICON_TRACK
    }


    private static final Map<String, TypeIcon>     DEVICES      = new HashMap<> ();
    private static final EnumMap<TypeIcon, String> DEVICE_ICONS = new EnumMap<> (TypeIcon.class);

    static
    {
        DEVICES.put ("Amp", TypeIcon.ICON_WAVE);
        DEVICES.put ("Arpeggiator", TypeIcon.ICON_NOTE);
        DEVICES.put ("Audio MOD", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Audio Receiver", TypeIcon.ICON_WAVE);
        DEVICES.put ("Bit-8", TypeIcon.ICON_WAVE);
        DEVICES.put ("Blur", TypeIcon.ICON_WAVE);
        DEVICES.put ("Chain", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Channel Filter", TypeIcon.ICON_NOTE);
        DEVICES.put ("Channel Map", TypeIcon.ICON_NOTE);
        DEVICES.put ("Chorus", TypeIcon.ICON_WAVE);
        DEVICES.put ("Chorus+", TypeIcon.ICON_WAVE);
        DEVICES.put ("Comb", TypeIcon.ICON_WAVE);
        DEVICES.put ("Compressor", TypeIcon.ICON_WAVE);
        DEVICES.put ("DC Offset", TypeIcon.ICON_WAVE);
        DEVICES.put ("De-Esser", TypeIcon.ICON_WAVE);
        DEVICES.put ("Delay-1", TypeIcon.ICON_WAVE);
        DEVICES.put ("Delay-2", TypeIcon.ICON_WAVE);
        DEVICES.put ("Delay-4", TypeIcon.ICON_WAVE);
        DEVICES.put ("Diatonic Transposer", TypeIcon.ICON_NOTE);
        DEVICES.put ("Distortion", TypeIcon.ICON_WAVE);
        DEVICES.put ("Drum Machine", TypeIcon.ICON_DRUM_MACHINE);
        DEVICES.put ("Dual Pan", TypeIcon.ICON_WAVE);
        DEVICES.put ("Dynamics", TypeIcon.ICON_WAVE);
        DEVICES.put ("E-Clap", TypeIcon.ICON_DRUM);
        DEVICES.put ("E-Cowbell", TypeIcon.ICON_DRUM);
        DEVICES.put ("E-Hat", TypeIcon.ICON_DRUM);
        DEVICES.put ("E-Kick", TypeIcon.ICON_DRUM);
        DEVICES.put ("E-Snare", TypeIcon.ICON_DRUM);
        DEVICES.put ("E-Tom", TypeIcon.ICON_DRUM);
        DEVICES.put ("EQ+", TypeIcon.ICON_WAVE);
        DEVICES.put ("EQ-2", TypeIcon.ICON_WAVE);
        DEVICES.put ("EQ-5", TypeIcon.ICON_WAVE);
        DEVICES.put ("EQ-DJ", TypeIcon.ICON_WAVE);
        DEVICES.put ("Filter", TypeIcon.ICON_WAVE);
        DEVICES.put ("Flanger", TypeIcon.ICON_WAVE);
        DEVICES.put ("Flanger+", TypeIcon.ICON_WAVE);
        DEVICES.put ("FM-4", TypeIcon.ICON_INSTRUMENT);
        DEVICES.put ("Freq Shifter", TypeIcon.ICON_WAVE);
        DEVICES.put ("FX Grid", TypeIcon.ICON_WAVE);
        DEVICES.put ("FX Layer", TypeIcon.ICON_FOLDER);
        DEVICES.put ("FX Selector", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Gate", TypeIcon.ICON_WAVE);
        DEVICES.put ("HW Clock Out", TypeIcon.ICON_IO);
        DEVICES.put ("HW CV Instrument", TypeIcon.ICON_IO);
        DEVICES.put ("HW CV Out", TypeIcon.ICON_IO);
        DEVICES.put ("HW FX", TypeIcon.ICON_IO);
        DEVICES.put ("HW Instrument", TypeIcon.ICON_IO);
        DEVICES.put ("Instrument Layer", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Instrument Selector", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Ladder", TypeIcon.ICON_WAVE);
        DEVICES.put ("LFO MOD", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Mid-Side Split", TypeIcon.ICON_FOLDER);
        DEVICES.put ("MIDI CC", TypeIcon.ICON_NOTE);
        DEVICES.put ("MIDI Program Change", TypeIcon.ICON_NOTE);
        DEVICES.put ("MIDI Song Select", TypeIcon.ICON_NOTE);
        DEVICES.put ("Multi-Note", TypeIcon.ICON_NOTE);
        DEVICES.put ("Multiband FX-2", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Multiband FX-3", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Note Echo", TypeIcon.ICON_NOTE);
        DEVICES.put ("Note Filter", TypeIcon.ICON_NOTE);
        DEVICES.put ("Note FX Layer", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Note FX Selector", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Note Harmonizer", TypeIcon.ICON_NOTE);
        DEVICES.put ("Note Latch", TypeIcon.ICON_NOTE);
        DEVICES.put ("Note Length", TypeIcon.ICON_NOTE);
        DEVICES.put ("Note MOD", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Note Pitch Shifter", TypeIcon.ICON_WAVE);
        DEVICES.put ("Note Receiver", TypeIcon.ICON_NOTE);
        DEVICES.put ("Note Velocity", TypeIcon.ICON_NOTE);
        DEVICES.put ("Organ", TypeIcon.ICON_INSTRUMENT);
        DEVICES.put ("Oscilloscope", TypeIcon.ICON_ANALYSIS);
        DEVICES.put ("Peak Limiter", TypeIcon.ICON_WAVE);
        DEVICES.put ("Phase-4", TypeIcon.ICON_INSTRUMENT);
        DEVICES.put ("Phaser", TypeIcon.ICON_WAVE);
        DEVICES.put ("Phaser+", TypeIcon.ICON_WAVE);
        DEVICES.put ("Pitch Shifter", TypeIcon.ICON_WAVE);
        DEVICES.put ("Poly Grid", TypeIcon.ICON_INSTRUMENT);
        DEVICES.put ("Polysynth", TypeIcon.ICON_INSTRUMENT);
        DEVICES.put ("Replacer", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Resonator Bank", TypeIcon.ICON_WAVE);
        DEVICES.put ("Reverb", TypeIcon.ICON_WAVE);
        DEVICES.put ("Ring-Mod", TypeIcon.ICON_WAVE);
        DEVICES.put ("Rotary", TypeIcon.ICON_WAVE);
        DEVICES.put ("Sampler", TypeIcon.ICON_INSTRUMENT);
        DEVICES.put ("Spectrum", TypeIcon.ICON_ANALYSIS);
        DEVICES.put ("Step MOD", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Stereo Split", TypeIcon.ICON_FOLDER);
        DEVICES.put ("Test Tone", TypeIcon.ICON_WAVE);
        DEVICES.put ("Time Shift", TypeIcon.ICON_WAVE);
        DEVICES.put ("Tool", TypeIcon.ICON_WAVE);
        DEVICES.put ("Transient Control", TypeIcon.ICON_WAVE);
        DEVICES.put ("Transposition Map", TypeIcon.ICON_NOTE);
        DEVICES.put ("Treemonster", TypeIcon.ICON_WAVE);
        DEVICES.put ("Tremolo", TypeIcon.ICON_WAVE);
        DEVICES.put ("Vocoder", TypeIcon.ICON_WAVE);
        DEVICES.put ("XY Effect", TypeIcon.ICON_FOLDER);
        DEVICES.put ("XY Instrument", TypeIcon.ICON_FOLDER);
        DEVICES.put ("USER", TypeIcon.ICON_USER);
        DEVICES.put ("PROJECT", TypeIcon.ICON_PROJECT);
        DEVICES.put ("TRACK", TypeIcon.ICON_TRACK);

        DEVICE_ICONS.put (TypeIcon.ICON_ANALYSIS, "device/device_analysis.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_WAVE, "device/device_audio.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_FOLDER, "device/device_container.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_DRUM_MACHINE, "device/device_drum_machine.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_DRUM, "device/device_drum_module.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_GENERIC, "device/device_generic.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_INSTRUMENT, "device/device_instrument.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_IO, "device/device_io.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_NOTE, "device/device_note.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_PLUGIN, "device/device_plugin.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_USER, "user.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_PROJECT, "track/master_track.svg");
        DEVICE_ICONS.put (TypeIcon.ICON_TRACK, "device/device_plugin.svg");
    }


    /**
     * Get the ID of an icon to use for the given device.
     *
     * @param deviceName The name of the device
     * @return The icon
     */
    public static String getIconId (final String deviceName)
    {
        final TypeIcon typeIcon = DEVICES.get (deviceName);
        if (typeIcon == null)
            return DEVICE_ICONS.get (TypeIcon.ICON_PLUGIN);
        return DEVICE_ICONS.get (typeIcon);
    }
}
