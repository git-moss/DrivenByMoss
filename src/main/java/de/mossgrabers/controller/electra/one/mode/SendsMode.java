// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.ElectraOnePlayPositionParameter;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * The sends mode. The knobs control the sends 1-6 of the tracks on the current track page.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SendsMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    // TODO
    private static final List<ContinuousID> KNOB_IDS = ContinuousID.createSequentialList (ContinuousID.SEND1_KNOB1, 6);
    static
    {
        KNOB_IDS.addAll (ContinuousID.createSequentialList (ContinuousID.SEND2_KNOB1, 6));
    }

    private static final int [] SEND_IDS     =
    {
        ElectraOneControlSurface.ELECTRA_ONE_SEND1,
        ElectraOneControlSurface.ELECTRA_ONE_SEND2,
        ElectraOneControlSurface.ELECTRA_ONE_SEND3,
        ElectraOneControlSurface.ELECTRA_ONE_SEND4,
        ElectraOneControlSurface.ELECTRA_ONE_SEND5,
        ElectraOneControlSurface.ELECTRA_ONE_SEND6
    };

    private final int []        valueCache   = new int [128];
    private final String []     elementCache = new String [37];
    private final String []     groupCache   = new String [37];


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SendsMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (Modes.NAME_SENDS, surface, model, true, KNOB_IDS);

        final IProject project = this.model.getProject ();
        this.setParameterProvider (new CombinedParameterProvider (
                // First row
                new SendParameterProvider (model, 0, 0), new FixedParameterProvider (project.getCueVolumeParameter ()),
                // Second row
                new SendParameterProvider (model, 1, 0), new FixedParameterProvider (new ElectraOnePlayPositionParameter (model.getValueChanger (), model.getTransport (), surface))));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        // These are all toggle buttons sending 127 for on and 0 for off state
        if (event == ButtonEvent.LONG || index != 5)
            return;

        switch (row)
        {
            // Next track page
            case 0:
                this.model.getCurrentTrackBank ().selectNextPage ();
                break;
            // Previous track page
            case 1:
                this.model.getCurrentTrackBank ().selectPreviousPage ();
                break;
            // Record
            case 2:
                this.model.getTransport ().startRecording ();
                break;
            // Play
            case 3:
                this.model.getTransport ().play ();
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        final int row = this.getButtonRow (buttonID);
        if (row == -1)
            return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

        final int column = this.isButtonRow (row, buttonID);
        if (column != 5)
            return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

        switch (row)
        {
            // Next track page
            case 0:
                return this.model.getCurrentTrackBank ().canScrollPageForwards () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            // Previous track page
            case 1:
                return this.model.getCurrentTrackBank ().canScrollPageBackwards () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            // Record
            case 2:
                return this.model.getTransport ().isRecording () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
            // Play
            case 3:
                return this.model.getTransport ().isPlaying () ? ElectraOneColorManager.COLOR_BUTTON_STATE_ON : ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;

            default:
                // Not used
                return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IMidiOutput output = this.surface.getMidiOutput ();

        for (int i = 0; i < 5; i++)
        {
            final int [] sendsVolumes = new int [6];
            Arrays.fill (sendsVolumes, 0);
            String title = "";
            final String [] sendNames = new String [6];
            Arrays.fill (sendNames, "");
            final boolean [] sendExists = new boolean [6];
            Arrays.fill (sendExists, false);
            ColorEx color = ColorEx.BLACK;

            final Optional<ITrack> trackOpt = this.getTrack (i);
            if (trackOpt.isPresent ())
            {
                final ITrack track = trackOpt.get ();
                if (track.doesExist ())
                {
                    final ISendBank sendBank = track.getSendBank ();
                    for (int s = 0; s < 6; s++)
                    {
                        final ISend send = sendBank.getItem (s);
                        sendsVolumes[s] = send.getValue ();
                        sendNames[s] = send.getName ();
                        sendExists[s] = send.doesExist ();
                    }
                    title = track.getPosition () + 1 + ": " + track.getName ();
                    color = ElectraOneColorManager.getClosestPaletteColor (track.getColor ());
                }
            }

            // TODO How to find out about groupIDs?
            this.surface.updateGroupTitle (72 + i, this.groupCache, title);

            // for (int sendIndex = 0; sendIndex < 6; sendIndex++)
            // {
            // if (this.valueCache[SEND_IDS[sendIndex] + i] != sendsVolumes[sendIndex])
            // {
            // output.sendCCEx (1, SEND_IDS[sendIndex] + i, sendsVolumes[sendIndex]);
            // this.valueCache[SEND_IDS[sendIndex] + i] = sendsVolumes[sendIndex];
            // }
            //
            // this.surface.updateElement (1 + 6 * sendIndex, this.elementCache,
            // sendNames[sendIndex], color, Boolean.valueOf (sendExists[sendIndex]));
            // }
        }

        // final IProject project = this.model.getProject ();
        // final int cueVolume = project.getCueVolume ();
        // if (this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_CUE_VOLUME] != cueVolume)
        // {
        // output.sendCCEx (1, ElectraOneControlSurface.ELECTRA_ONE_CUE_VOLUME, cueVolume);
        // this.valueCache[ElectraOneControlSurface.ELECTRA_ONE_CUE_VOLUME] = cueVolume;
        // }
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        Arrays.fill (this.valueCache, -1);
        Arrays.fill (this.elementCache, null);
        Arrays.fill (this.groupCache, null);

        super.onActivate ();
    }
}