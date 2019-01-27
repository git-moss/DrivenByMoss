// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities;

import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;

import java.util.Arrays;
import java.util.regex.Pattern;


/**
 * Extension for auto coloring tracks. In the settings of the script you can set a search string for
 * each color. E.g. if you write 'Bass' after the color red all tracks, which contain the word
 * 'Bass' in their name will automatically be colored in red. For multiple search strings use a
 * comma, e.g. I put 'Drum,BD,Snare,HiHat,Crash' after blue.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AutoColor
{
    private static final int MAX_TRACKS = 100;

    private final String []  colorRegex = new String [NamedColor.values ().length];
    private ITrackBank       trackBank;


    /**
     * Constructor.
     */
    protected AutoColor ()
    {
        Arrays.fill (this.colorRegex, "");
    }


    /**
     * Handle the change of a substring for a color.
     *
     * @param index The index of the color
     * @param color The color
     * @param substring The substring
     */
    public void handleRegExChange (final int index, final NamedColor color, final String substring)
    {
        this.colorRegex[index] = substring;
        if (this.colorRegex[index].length () == 0)
            return;
        // Split the substrings
        final String [] parts = this.colorRegex[index].split (",");
        // Apply all substring patterns to all tracks
        for (final String part: parts)
        {
            final Pattern pattern = Pattern.compile (".*" + part.trim () + ".*");
            for (int i1 = 0; i1 < MAX_TRACKS; i1++)
                this.setColorOnMatch (i1, color, pattern);
        }
    }


    /**
     * Handle the change of a track name. Check the new track name against all substrings.
     *
     * @param channelIndex The index of the channel to test
     * @param name The new track name to test
     */
    public void handleTrackNameChange (final int channelIndex, final String name)
    {
        if (name.length () == 0)
            return;
        // Match the new track name against all substring settings
        final NamedColor [] colors = NamedColor.values ();
        for (int i = 0; i < this.colorRegex.length; i++)
        {
            if (this.colorRegex[i] == null || this.colorRegex[i].length () == 0)
                continue;
            for (final String part: this.colorRegex[i].split (","))
                this.setColorOnMatch (channelIndex, colors[i], Pattern.compile (".*" + part + ".*"));
        }
    }


    /**
     * Tests a channel for a given pattern and assigns the given color if it matches the pattern.
     *
     * @param channelIndex The index of the channel to test
     * @param color The color to apply
     * @param pattern The pattern for testing
     */
    public void setColorOnMatch (final int channelIndex, final NamedColor color, final Pattern pattern)
    {
        final ITrack track = this.trackBank.getItem (channelIndex);
        if (pattern.matcher (track.getName ()).matches ())
            track.setColor (color.getRed (), color.getGreen (), color.getBlue ());
    }


    /**
     * Set the track bank for which the auto coloring should be applied.
     *
     * @param trackBank The track bank
     */
    public void setTrackBank (final ITrackBank trackBank)
    {
        this.trackBank = trackBank;
    }
}
