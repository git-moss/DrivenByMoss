// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * Some helper functions for files.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class FileEx extends File
{
    private static final long serialVersionUID = 2670415170924953154L;


    /**
     * Creates a new FileEx instance by converting the given pathname string into an abstract
     * pathname. If the given string is the empty string, then the result is the empty abstract
     * pathname.
     *
     * @param filename A filename string
     */
    public FileEx (final String filename)
    {
        super (filename);
    }


    /**
     * Creates a new FileEx instance from a parent pathname string and a child pathname string.<br>
     * If parent is null then the new File instance is created as if by invoking the single-argument
     * File constructor on the given child pathname string. Otherwise the parent pathname string is
     * taken to denote a directory, and the child pathname string is taken to denote either a
     * directory or a file. If the child pathname string is absolute then it is converted into a
     * relative pathname in a system-dependent way. If parent is the empty string then the new File
     * instance is created by converting child into an abstract pathname and resolving the result
     * against a system-dependent default directory. Otherwise each pathname string is converted
     * into an abstract pathname and the child abstract pathname is resolved against the parent.
     *
     * @param parent The parent pathname string
     * @param child The child pathname string
     */
    public FileEx (final String parent, final String child)
    {
        super (parent, child);
    }


    /**
     * Gets the name of the file without the ending. E.g. the filename 'aFile.jpeg' will return
     * 'aFile'.
     *
     * @return The name of the file without the ending
     */
    public String getNameWithoutType ()
    {
        final String filename = this.getName ();
        final int pos = filename.lastIndexOf ('.');
        return pos == -1 ? filename : filename.substring (0, pos);
    }


    /**
     * Reads a text file in UTF8 encoding into a string.
     *
     * @return The content of the file
     * @throws IOException Something crashed
     */
    public String readUTF8 () throws IOException
    {
        final String text = Files.readString (this.toPath ());

        // UTF-8 BOM might not be automatically removed
        return text.length () > 0 && text.charAt (0) == '\uFEFF' ? text.substring (1) : text;
    }
}
