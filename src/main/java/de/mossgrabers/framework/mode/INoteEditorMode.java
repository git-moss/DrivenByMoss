// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.mode;

/**
 * Interface to a mode which can edit notes.
 *
 * @author Jürgen Moßgraber
 */
public interface INoteEditorMode
{
    /**
     * Get the note editor.
     *
     * @return The editor
     */
    INoteEditor getNoteEditor ();
}
