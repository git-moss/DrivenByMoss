// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.utils;

/**
 * Interface for a combination of integer supplier and consumer.
 *
 * @author Jürgen Moßgraber
 */
@FunctionalInterface
public interface IntConsumerSupplier
{
    /**
     * Processes an integer value and returns an integer as the result.
     *
     * @param value The input value
     * @return The result value
     */
    int process (int value);
}
