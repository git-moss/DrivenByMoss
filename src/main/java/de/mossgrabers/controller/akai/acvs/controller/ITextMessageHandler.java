// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

/**
 * Callback for text encoded values received via system exclusive messages.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface ITextMessageHandler
{
    /**
     * Callback to handle the message text.
     *
     * @param itemID The ID of the item to which the text belongs
     * @param text The text
     */
    void handleTextMessage (int itemID, String text);
}
