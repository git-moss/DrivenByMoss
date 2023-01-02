// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.daw.data.bank;

import de.mossgrabers.bitwig.framework.daw.data.SlotImpl;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;

import com.bitwig.extension.controller.api.ClipLauncherSlotBank;

import java.util.Optional;


/**
 * Encapsulates the data of a slot bank.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SlotBankImpl extends AbstractItemBankImpl<ClipLauncherSlotBank, ISlot> implements ISlotBank
{
    private final ITrack track;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param track The track, which contains the slot bank
     * @param clipLauncherSlotBank The slot bank
     * @param numSlots The number of slots in the page of the bank
     */
    public SlotBankImpl (final IHost host, final IValueChanger valueChanger, final ITrack track, final ClipLauncherSlotBank clipLauncherSlotBank, final int numSlots)
    {
        super (host, valueChanger, clipLauncherSlotBank, numSlots);

        this.track = track;

        if (this.bank.isEmpty ())
            return;

        final ClipLauncherSlotBank clsb = this.bank.get ();
        for (int i = 0; i < this.getPageSize (); i++)
            this.items.add (new SlotImpl (this.track, clsb.getItemAt (i), i));
    }


    /** {@inheritDoc} */
    @Override
    public Optional<ISlot> getEmptySlot (final int startFrom)
    {
        final int start = startFrom >= 0 ? startFrom : 0;
        final int size = this.items.size ();
        for (int i = 0; i < size; i++)
        {
            final ISlot item = this.items.get ((start + i) % size);
            if (!item.hasContent ())
                return Optional.of (item);
        }
        return Optional.empty ();
    }
}