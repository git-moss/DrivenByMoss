// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.mode;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptySend;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;

import java.util.List;
import java.util.Optional;


/**
 * The send mode.
 *
 * @author Jürgen Moßgraber
 */
public class SendMode extends DefaultTrackMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public SendMode (final KontrolProtocolControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Send", surface, model, false);

        this.setControls (controls);
        final SendParameterProvider pp = new SendParameterProvider (model, -1, 0);
        this.setParameterProvider (new CombinedParameterProvider (pp, pp));

        model.getTrackBank ().addSelectionObserver ( (index, isSelected) -> this.parametersAdjusted ());
    }


    /** {@inheritDoc} */
    @Override
    public void onKnobValue (final int index, final int value)
    {
        final Optional<ITrack> track = this.model.getCurrentTrackBank ().getSelectedItem ();
        if (track.isEmpty ())
            return;
        final ISend send = track.get ().getSendBank ().getItem (index);
        if (this.isAbsolute)
            send.setValue (value);
        else
            send.changeValue (value);
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        // Note: Since we need multiple value (more than 8), index is the MIDI CC of the knob

        final IValueChanger valueChanger = this.model.getValueChanger ();
        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        final ISendBank sendBank = selectedTrack.isEmpty () ? null : selectedTrack.get ().getSendBank ();

        if (index >= KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME && index < KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME + 8)
        {
            final ISend send = sendBank == null ? EmptySend.INSTANCE : sendBank.getItem (index - KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME);
            return valueChanger.toMidiValue (send.getValue ());
        }

        if (index >= KontrolProtocolControlSurface.KONTROL_TRACK_PAN && index < KontrolProtocolControlSurface.KONTROL_TRACK_PAN + 8)
        {
            final ISend send = sendBank == null ? EmptySend.INSTANCE : sendBank.getItem (index - KontrolProtocolControlSurface.KONTROL_TRACK_PAN);
            return valueChanger.toMidiValue (send.getValue ());
        }

        final int scrollTracksState = (sendBank != null && sendBank.canScrollPageBackwards () ? 1 : 0) + (sendBank != null && sendBank.canScrollPageForwards () ? 2 : 0);
        final int scrollScenesState = 0;

        final KontrolProtocolConfiguration configuration = this.surface.getConfiguration ();
        switch (index)
        {
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS:
                return scrollTracksState;
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS:
                return configuration.isFlipTrackClipNavigation () ? scrollScenesState : scrollTracksState;
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS:
                return configuration.isFlipTrackClipNavigation () ? scrollTracksState : scrollScenesState;
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();
        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        final ISendBank sendBank = selectedTrack.isEmpty () ? null : selectedTrack.get ().getSendBank ();

        final int [] vuData = new int [16];
        for (int i = 0; i < 8; i++)
        {
            final ISend send = sendBank == null ? EmptySend.INSTANCE : sendBank.getItem (i);

            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_AVAILABLE, send.doesExist () ? TrackType.RETURN_BUS : TrackType.EMPTY, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_SELECTED, send.isSelected () ? 1 : 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_RECARM, 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME_TEXT, 0, i, send.getDisplayedValue (8));
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_PAN_TEXT, 0, i, send.getDisplayedValue (8));
            final String n = selectedTrack.isPresent () ? this.getLabel (selectedTrack.get (), send) : "";
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_NAME, 0, i, n);

            final int j = 2 * i;
            vuData[j] = valueChanger.toMidiValue (send.getModulatedValue ());
            vuData[j + 1] = valueChanger.toMidiValue (send.getModulatedValue ());
        }
        this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VU, 2, 0, vuData);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        if (selectedTrack.isPresent ())
            selectedTrack.get ().getSendBank ().selectPreviousPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        final Optional<ITrack> selectedTrack = this.bank.getSelectedItem ();
        if (selectedTrack.isPresent ())
            selectedTrack.get ().getSendBank ().selectNextPage ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.selectPreviousItem ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.selectNextItem ();
    }


    private String getLabel (final ITrack track, final ISend send)
    {
        final String n = send.doesExist () ? send.getName () : "None";

        if (this.surface.getProtocolVersion () == KontrolProtocol.VERSION_1)
            return "S" + (send.getPosition () + 1) + ": " + n;

        return "Track " + (track.getPosition () + 1) + "\nFX " + (send.getPosition () + 1) + "\n\n" + n;
    }
}