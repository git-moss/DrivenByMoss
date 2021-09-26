package de.mossgrabers.bitwig.framework.daw;

import de.mossgrabers.framework.daw.IMemoryBlock;

import com.bitwig.extension.api.MemoryBlock;

import java.nio.ByteBuffer;


/**
 * Wrapper to a block of memory in Bitwig API.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MemoryBlockImpl implements IMemoryBlock
{
    private final MemoryBlock memoryBlock;


    /**
     * Constructor.
     *
     * @param memoryBlock The memory block to wrap
     */
    public MemoryBlockImpl (final MemoryBlock memoryBlock)
    {
        this.memoryBlock = memoryBlock;
    }


    /**
     * Get the Bitwig memory block.
     *
     * @return The memory block
     */
    public MemoryBlock getMemoryBlock ()
    {
        return this.memoryBlock;
    }


    /** {@inheritDoc} */
    @Override
    public ByteBuffer createByteBuffer ()
    {
        return this.memoryBlock.createByteBuffer ();
    }
}
