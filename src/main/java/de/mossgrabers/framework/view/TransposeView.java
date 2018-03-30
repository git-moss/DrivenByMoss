package de.mossgrabers.framework.view;

import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Interface for transposing up and down.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface TransposeView
{
    /**
     * Trigger to display 1 octave lower.
     *
     * @param event The button event
     */
    void onOctaveDown (final ButtonEvent event);


    /**
     * Trigger to display 1 octave higher.
     *
     * @param event The button event
     */
    void onOctaveUp (final ButtonEvent event);
}
