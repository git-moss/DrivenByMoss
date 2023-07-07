// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.Optional;


/**
 * The sends mode. The knobs control the sends 1-6 of the tracks on the current track page.
 *
 * @author Jürgen Moßgraber
 */
public class SendsMode extends AbstractElectraOneMode
{
    private static final int   FIRST_TRACK_GROUP = 510;

    private final ITransport   transport;
    private final IMasterTrack masterTrack;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SendsMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (1, Modes.NAME_SENDS, surface, model);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                new SendParameterProvider (model, 0, 0), new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                new SendParameterProvider (model, 1, 0), new FixedParameterProvider (new PlayPositionParameter (model.getValueChanger (), this.transport, surface)),
                // Row 3
                new SendParameterProvider (model, 2, 0), new EmptyParameterProvider (1),
                // Row 4
                new SendParameterProvider (model, 3, 0), new EmptyParameterProvider (1),
                // Row 5
                new SendParameterProvider (model, 4, 0), new EmptyParameterProvider (1),
                // Row 6
                new SendParameterProvider (model, 5, 0), new EmptyParameterProvider (1)));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN || index != 5)
            return;

        switch (row)
        {
            // Next track page
            case 2:
                this.model.getCurrentTrackBank ().selectNextPage ();
                break;
            // Previous track page
            case 3:
                this.model.getCurrentTrackBank ().selectPreviousPage ();
                break;
            // Record
            case 4:
                this.transport.startRecording ();
                break;
            // Play
            case 5:
                this.transport.play ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        for (int column = 0; column < 5; column++)
        {
            final Optional<ITrack> trackOpt = this.getTrack (column);
            final ITrack track = trackOpt.isPresent () ? trackOpt.get () : EmptyTrack.getInstance (this.model.getTrackBank ().getItem (0).getSendBank ().getPageSize ());
            final ColorEx color = track.getColor ();

            this.pageCache.updateGroupLabel (FIRST_TRACK_GROUP + column, track.doesExist () ? track.getPosition () + 1 + ": " + track.getName () : "");

            for (int row = 0; row < 6; row++)
            {
                final ISendBank sendBank = track.getSendBank ();
                final ISend send = sendBank.getItem (row);

                final boolean doesExist = send.doesExist ();

                this.pageCache.updateValue (row, column, send.getValue (), doesExist ? StringUtils.optimizeName (StringUtils.fixASCII (send.getDisplayedValue ()), 15) : " ");
                this.pageCache.updateElement (row, column, send.getName (), doesExist ? color : ColorEx.BLACK, Boolean.TRUE);
            }
        }

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));
        this.pageCache.updateValue (1, 5, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
        this.pageCache.updateElement (1, 5, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getPositionText ()), 15), null, null);

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }
}