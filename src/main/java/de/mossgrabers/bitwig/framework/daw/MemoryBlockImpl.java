package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.IMemoryBlock;

import com.bitwig.extension.api.MemoryBlock;

import java.nio.ByteBuffer;


/**
 * Wrapper to a block of memory in Bitwig API.
 *
 * @author Jürgen Moßgraber
 *
 * @param memoryBlock The memory block to wrap
 */
public record MemoryBlockImpl (MemoryBlock memoryBlock) implements IMemoryBlock
{
    /** {@inheritDoc} */
    @Override
    public ByteBuffer createByteBuffer ()
    {
        return this.memoryBlock.createByteBuffer ();
    }
}
