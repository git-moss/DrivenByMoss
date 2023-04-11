// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchpad.view;

import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadColorManager;
import de.mossgrabers.controller.novation.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * 8 send faders.
 *
 * @author Jürgen Moßgraber
 */
public class SendsView extends AbstractFaderView
{
    private int selectedSend;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SendsView (final LaunchpadControlSurface surface, final IModel model)
    {
        super ("Sends", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        if (!this.model.isEffectTrackBankActive ())
            this.model.getTrackBank ().getItem (index).getSendBank ().getItem (this.selectedSend).setValue (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        if (this.model.isEffectTrackBankActive ())
            return 0;
        return this.model.getTrackBank ().getItem (index).getSendBank ().getItem (this.selectedSend).getValue ();
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final ButtonID buttonID, final ButtonEvent event, final int velocity)
    {
        if (!ButtonID.isSceneButton (buttonID) || event != ButtonEvent.DOWN)
            return;

        this.selectedSend = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();

        final ITrackBank tb = this.model.getTrackBank ();
        final String sendName = tb.getEditSendName (this.selectedSend);
        final String message = "Send " + (this.selectedSend + 1) + ": " + (sendName.isEmpty () ? "None" : sendName);
        this.surface.getTextDisplay ().notify (message);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        for (int i = 0; i < 8; i++)
            this.setupFader (i);
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int ordinal = buttonID.ordinal ();
        if (ordinal < ButtonID.SCENE1.ordinal () || ordinal > ButtonID.SCENE8.ordinal ())
            return 0;

        final int scene = buttonID.ordinal () - ButtonID.SCENE1.ordinal ();

        if (this.selectedSend == scene)
            return LaunchpadColorManager.LAUNCHPAD_COLOR_ORCHID;

        final ITrackBank tb = this.model.getTrackBank ();
        if (tb.canEditSend (scene))
            return LaunchpadColorManager.LAUNCHPAD_COLOR_GREY_LO;

        return LaunchpadColorManager.LAUNCHPAD_COLOR_BLACK;
    }


    /**
     * Get the selected send channel.
     *
     * @return The number of the channel
     */
    public int getSelectedSend ()
    {
        return this.selectedSend;
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        final int color = this.colorManager.getColorIndex (DAWColor.getColorID (track.getColor ()));
        this.surface.setupFader (index, color, false);

        final ISend send = track.getSendBank ().getItem (this.selectedSend);
        this.surface.setFaderValue (index, send.getValue ());
    }
}