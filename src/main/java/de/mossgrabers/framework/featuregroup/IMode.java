// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.featuregroup;

import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * Interface to a mode. A mode has a number of knobs, one or two rows of buttons to navigate it and
 * optionally a display. Furthermore, a mode (normally) edits N items (e.g. track volume, pan,
 * device parameter). Items are organized in pages. The number of items on a page should be
 * identical to the number of knobs and buttons of a row.
 *
 * @author Jürgen Moßgraber
 */
public interface IMode extends IFeatureGroup
{
    /**
     * Update the display.
     */
    void updateDisplay ();


    /**
     * A knob has been used.
     *
     * @param index The index of the knob
     * @param value The value the knob sent
     */
    void onKnobValue (int index, int value);


    /**
     * Get the value of the parameter that is controlled by the knob.
     *
     * @param index The index of the knob
     * @return The value or -1
     */
    int getKnobValue (int index);


    /**
     * Get the color for a knob or fader, which is controlled by the mode.
     *
     * @param index The index of the knob
     * @return A color index
     */
    int getKnobColor (int index);


    /**
     * A knob has been touched.
     *
     * @param index The index of the knob
     * @param isTouched True if the knob has been touched
     */
    void onKnobTouch (final int index, final boolean isTouched);


    /**
     * Check if a knob is touched.
     *
     * @return True if at least 1 knob is touched
     */
    boolean isAnyKnobTouched ();


    /**
     * Set the index of the touched knob.
     *
     * @param knobIndex The index
     * @param isTouched True if touched otherwise false
     */
    void setTouchedKnob (int knobIndex, boolean isTouched);


    /**
     * Get the index of the touched knob, if any.
     *
     * @return The index or -1 if none is touched
     */
    int getTouchedKnob ();


    /**
     * Get the index of the last touched knob, if any.
     *
     * @return The index or -1 if none was touched in this mode so far
     */
    int getLastTouchedKnob ();


    /**
     * Check if a knob is touched.
     *
     * @param index The index of the knob
     * @return True if the knob is touched
     */
    boolean isKnobTouched (int index);


    /**
     * A row button has been pressed.
     *
     * @param row The number of the button row
     * @param index The index of the button
     * @param event The button event
     */
    void onButton (int row, int index, ButtonEvent event);


    /**
     * Select an item.
     *
     * @param index The items index
     */
    void selectItem (int index);


    /**
     * Get the selected item if any.
     *
     * @return The selected item or null
     */
    Optional<String> getSelectedItemName ();


    /**
     * Selects the previous item in the page. Scrolls the page to the previous page if the first
     * item was selected (and if there is a previous page).
     */
    void selectPreviousItem ();


    /**
     * Selects the next item in the page. Scrolls the page to the next page if the last item on the
     * page was selected (if there is a next page).
     */
    void selectNextItem ();


    /**
     * Selects the previous item page and selects the last item of the page.
     */
    void selectPreviousItemPage ();


    /**
     * Selects the next item page and selects the first item of the page.
     */
    void selectNextItemPage ();


    /**
     * Select an item page.
     *
     * @param page The index of the page
     */
    void selectItemPage (int page);


    /**
     * Is there a previous item that can be selected?
     *
     * @return True if there is an item
     */
    boolean hasPreviousItem ();


    /**
     * Is there a next item that can be selected?
     *
     * @return True if there is an item
     */
    boolean hasNextItem ();


    /**
     * Is there a previous item page that can be selected?
     *
     * @return True if there is an item
     */
    boolean hasPreviousItemPage ();


    /**
     * Is there a next item page that can be selected?
     *
     * @return True if there is an item
     */
    boolean hasNextItemPage ();


    /**
     * Formats the position range of the currently active page of the bank. If no bank is set or
     * there is no page an empty string is returned.
     *
     * @param format The format to use (e.g. "%d - %d")
     * @return The formatted range
     */
    String formatPageRange (final String format);


    /**
     * Get the currently active parameter provider, depending on pressed buttons.
     *
     * @return The active parameter provider, might be null if none is set
     */
    IParameterProvider getParameterProvider ();
}
