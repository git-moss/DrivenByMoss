// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.controller;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.framework.controller.AbstractControlSurface;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;

import java.util.List;


/**
 * A control surface which supports the Electra.One controller.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
@SuppressWarnings("javadoc")
public class ElectraOneControlSurface extends AbstractControlSurface<ElectraOneConfiguration>
{
    public static final int    ELECTRA_ONE_VOLUME1         = 0x10;
    public static final int    ELECTRA_ONE_VOLUME2         = 0x11;
    public static final int    ELECTRA_ONE_VOLUME3         = 0x12;
    public static final int    ELECTRA_ONE_VOLUME4         = 0x13;
    public static final int    ELECTRA_ONE_VOLUME5         = 0x14;
    public static final int    ELECTRA_ONE_MASTER_VOLUME   = 0x15;

    public static final int    ELECTRA_ONE_PAN1            = 0x20;
    public static final int    ELECTRA_ONE_PAN2            = 0x21;
    public static final int    ELECTRA_ONE_PAN3            = 0x22;
    public static final int    ELECTRA_ONE_PAN4            = 0x23;
    public static final int    ELECTRA_ONE_PAN5            = 0x24;
    public static final int    ELECTRA_ONE_PLAY_POSITION   = 0x25;

    public static final int    ELECTRA_ONE_ARM1            = 0x30;
    public static final int    ELECTRA_ONE_ARM2            = 0x31;
    public static final int    ELECTRA_ONE_ARM3            = 0x32;
    public static final int    ELECTRA_ONE_ARM4            = 0x33;
    public static final int    ELECTRA_ONE_ARM5            = 0x34;
    public static final int    ELECTRA_ONE_NEXT_TRACK_PAGE = 0x35;

    public static final int    ELECTRA_ONE_MUTE1           = 0x40;
    public static final int    ELECTRA_ONE_MUTE2           = 0x41;
    public static final int    ELECTRA_ONE_MUTE3           = 0x42;
    public static final int    ELECTRA_ONE_MUTE4           = 0x43;
    public static final int    ELECTRA_ONE_MUTE5           = 0x44;
    public static final int    ELECTRA_ONE_PREV_TRACK_PAGE = 0x45;

    public static final int    ELECTRA_ONE_SOLO1           = 0x50;
    public static final int    ELECTRA_ONE_SOLO2           = 0x51;
    public static final int    ELECTRA_ONE_SOLO3           = 0x52;
    public static final int    ELECTRA_ONE_SOLO4           = 0x53;
    public static final int    ELECTRA_ONE_SOLO5           = 0x54;
    public static final int    ELECTRA_ONE_RECORD          = 0x55;

    public static final int    ELECTRA_ONE_SELECT1         = 0x60;
    public static final int    ELECTRA_ONE_SELECT2         = 0x61;
    public static final int    ELECTRA_ONE_SELECT3         = 0x62;
    public static final int    ELECTRA_ONE_SELECT4         = 0x63;
    public static final int    ELECTRA_ONE_SELECT5         = 0x64;
    public static final int    ELECTRA_ONE_PLAY            = 0x65;

    // Sysex

    public static final String SYSEX_HDR                   = "F0 00 21 45 ";
    // TODO public static final int ELECTRA_ONE_SYSEX_CMD_DISPLAY = 0x12;


    /**
     * Constructor.
     *
     * @param surfaces All surfaces to be able to check for status keys like Shift.
     * @param host The host
     * @param colorManager The color manager
     * @param configuration The configuration
     * @param output The MIDI output
     * @param input The MIDI input
     * @param extenderOffset The channel/bank offset if multiple extenders are used
     * @param isMainDevice True if it is the main MCU controller (and not an extender)
     */
    public ElectraOneControlSurface (final List<ElectraOneControlSurface> surfaces, final IHost host, final ColorManager colorManager, final ElectraOneConfiguration configuration, final IMidiOutput output, final IMidiInput input)
    {
        super (surfaces.size (), host, configuration, colorManager, output, input, null, 1000, 1000);
    }


    /** {@inheritDoc} */
    @Override
    public void setTrigger (final int channel, final int cc, final int value)
    {
        // TODO
        this.output.sendNoteEx (channel, cc, value);
    }
}