// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.utilities.autocolor;

import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;


/**
 * Extension for auto coloring tracks. In the settings of the script you can set a search string for
 * each color. E.g. if you write 'Bass' after the color red all tracks, which contain the word
 * 'Bass' in their name will automatically be colored in red. For multiple search strings use a
 * comma, e.g. I put 'Drum,BD,Snare,HiHat,Crash' after blue.
 *
 * @author Jürgen Moßgraber
 */
public class AutoColor
{
    private final EnumMap<DAWColor, List<Pattern>> colorRegex = new EnumMap<> (DAWColor.class);
    private final AutoColorConfiguration           configuration;
    private ITrackBank                             trackBank;


    /**
     * Constructor.
     *
     * @param configuration The configuration
     */
    protected AutoColor (final AutoColorConfiguration configuration)
    {
        this.configuration = configuration;
    }


    /**
     * Handle the change of a substring for a color.
     *
     * @param color The color
     * @param filter The substring
     */
    public void handleRegExChange (final DAWColor color, final String filter)
    {
        if (!this.configuration.isEnableAutoColor ())
            return;

        final List<Pattern> patterns = new ArrayList<> ();
        if (filter != null && !filter.trim ().isEmpty ())
        {
            for (final String part: filter.split (","))
                patterns.add (Pattern.compile (".*" + Pattern.quote (part.trim ()) + ".*"));
        }
        synchronized (this.colorRegex)
        {
            this.colorRegex.put (color, patterns);
            if (!patterns.isEmpty ())
                this.updateTracks (color, patterns);
        }
    }


    /**
     * Handle the change of a track name. Check the new track name against all substrings.
     *
     * @param channelIndex The index of the channel to test
     * @param trackName The new track name to test
     */
    public void matchTrackName (final int channelIndex, final String trackName)
    {
        if (this.configuration.isEnableAutoColor () && !trackName.trim ().isEmpty ())
            this.matchColorsToTrack (this.trackBank.getItem (channelIndex), trackName);
    }


    /**
     * Updates all tracks (in the page) for a color.
     *
     * @param color The color to match for
     * @param patterns The patterns to match
     */
    private void updateTracks (final DAWColor color, final List<Pattern> patterns)
    {
        for (int i = 0; i < this.trackBank.getPageSize (); i++)
        {
            final ITrack track = this.trackBank.getItem (i);
            matchColorToTrack (track, track.getName (), color, patterns);
        }
    }


    /**
     * Tests a track against all color patterns.
     *
     * @param track The track to test
     * @param trackName The name of the track (the track name of the track might not yet beend
     *            updated)
     */
    private void matchColorsToTrack (final ITrack track, final String trackName)
    {
        synchronized (this.colorRegex)
        {
            for (final Entry<DAWColor, List<Pattern>> e: this.colorRegex.entrySet ())
                matchColorToTrack (track, trackName, e.getKey (), e.getValue ());
        }
    }


    /**
     * Tests a track for all given patterns and assigns the given color if it matches one of the
     * patterns.
     *
     * @param track The track to test
     * @param trackName The name of the track (the track name of the track might not yet beend
     *            updated)
     * @param color The color to apply
     * @param patterns The pattern to test against
     */
    private static void matchColorToTrack (final ITrack track, final String trackName, final DAWColor color, final List<Pattern> patterns)
    {
        for (final Pattern pattern: patterns)
        {
            if (pattern.matcher (trackName).matches ())
            {
                track.setColor (color.getColor ());
                break;
            }
        }
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
