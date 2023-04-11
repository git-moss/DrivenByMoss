// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw;

import java.nio.ByteBuffer;


/**
 * Interface to a block of memory.
 *
 * @author Jürgen Moßgraber
 */
public interface IMemoryBlock
{
    /**
     * Creates a ByteBuffer that can be used to read/write the data at this memory block. .
     *
     * @return The byte buffer
     */
    ByteBuffer createByteBuffer ();
}