// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.view;

import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.IControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.MidiConstants;


/**
 * Abstract implementation for a chord grid. The implementation is targeted on a 8x8 matrix but is
 * working with other sizes too.
 * <ul>
 * <li>Row 8: Add eleventh
 * <li>Row 7: Major seventh chord (maj7)
 * <li>Row 6: Dominant seventh chord (7)
 * <li>Row 5: Add sixth (6)
 * <li>Row 4: Suspended fourth (Sus4)
 * <li>Row 3: Suspended second (Sus2)
 * <li>Row 2: Dyad - Powerchord
 * <li>Row 1: Triad
 * </ul>
 *
 * @param <S> The type of the control surface
 * @param <C> The type of the configuration
 *
 * @author Jürgen Moßgraber
 */
public class AbstractChordView<S extends IControlSurface<C>, C extends Configuration> extends AbstractPlayView<S, C>
{
    // @formatter:off
    private static final int [][] CHORD_INTERVALS =
    {
        { 3, 5 },       // Triad
        { 5, 8 },       // Dyad - Powerchord
        { 2, 5 },       // Suspended second (Sus2)
        { 4, 5 },       // Suspended fourth (Sus4)
        { 3, 5, 6 },    // Add sixth (6)
        { 3, 5, 7 },    // Major seventh chord (maj7)
        { 3, 5, 9 },    // Add ninth (9)
        { 3, 5, 11 },   // Add eleventh (11)
    };
    // @formatter:on


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     * @param useTrackColor True to use the color of the current track for coloring the octaves
     */
    public AbstractChordView (final String name, final S surface, final IModel model, final boolean useTrackColor)
    {
        super (name, surface, model, useTrackColor);
    }


    /** {@inheritDoc} */
    @Override
    protected int [] getScaleMatrix ()
    {
        return this.scales.getNoteMatrix (this.scales.getActiveChordMatrix ());
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int key, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes ())
            return;

        super.onGridNote (key, velocity);

        final int index = key - 36;
        final int row = index / this.surface.getPadGrid ().getCols ();

        final int note = this.keyManager.map (key);

        final int [] chord = this.scales.getChord (note, CHORD_INTERVALS[row]);
        // Send additional chord notes to the DAW
        final IMidiInput input = this.surface.getMidiInput ();
        final C config = this.surface.getConfiguration ();
        final int channel = config.getMidiEditChannel ();
        int vel = 0;
        if (velocity > 0)
            vel = config.isAccentActive () ? config.getFixedAccentValue () : velocity;
        for (final int element: chord)
            input.sendRawMidiEvent (MidiConstants.CMD_NOTE_ON + channel, element, vel);
    }
}
