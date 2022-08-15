package de.mossgrabers.controller.novation.launchcontrol.mode;

import de.mossgrabers.controller.novation.launchcontrol.LaunchControlXLConfiguration;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLColorManager;
import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * Mix mode for the LauchControl XL to control 2 sends and panorama.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLMixMode extends AbstractMode<LaunchControlXLControlSurface, LaunchControlXLConfiguration, ITrack>
{
    private static final List<ContinuousID> KNOB_CONTROLS = new ArrayList<> (24);
    static
    {
        KNOB_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND1_KNOB1, 8));
        KNOB_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND2_KNOB1, 8));
        KNOB_CONTROLS.addAll (ContinuousID.createSequentialList (ContinuousID.PAN_KNOB1, 8));
    }


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public XLMixMode (final LaunchControlXLControlSurface surface, final IModel model)
    {
        super ("Send A, B & Panorama", surface, model, true, model.getTrackBank (), KNOB_CONTROLS);

        this.setParameterProvider (new CombinedParameterProvider (new SendParameterProvider (model, 0, 0), new SendParameterProvider (model, 1, 0), new PanParameterProvider (model)));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP || row != 0)
            return;

        final ITrack track = this.model.getTrackBank ().getItem (index);
        if (track.doesExist ())
        {
            if (track.isSelected () && track.isGroup ())
            {
                track.toggleGroupExpanded ();
                return;
            }

            track.select ();
            this.mvHelper.notifySelectedTrack ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final ITrack track = this.model.getTrackBank ().getItem (index % 8);
        switch (row)
        {
            case 0:
                return track.getSendBank ().getItem (0).getValue ();
            case 1:
                return track.getSendBank ().getItem (1).getValue ();
            case 2:
                return track.getPan ();
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int index = buttonID.ordinal () - ButtonID.ROW1_1.ordinal ();
        if (index >= 0 && index < 8)
        {
            final ITrack track = this.model.getTrackBank ().getItem (index);
            if (track.doesExist ())
                return track.isSelected () ? LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW : LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_YELLOW_LO;
        }
        return LaunchControlXLColorManager.LAUNCHCONTROL_COLOR_BLACK;
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
            trackBank.getItem (i).getSendBank ().selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        for (int i = 0; i < 8; i++)
            trackBank.getItem (i).getSendBank ().selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasPreviousItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageBackwards ();
        return canScroll;
    }


    /** {@inheritDoc} */
    @Override
    public boolean hasNextItem ()
    {
        final ITrackBank trackBank = this.model.getTrackBank ();
        boolean canScroll = false;
        for (int i = 0; i < 8; i++)
            canScroll |= trackBank.getItem (i).getSendBank ().canScrollPageForwards ();
        return canScroll;
    }
}
