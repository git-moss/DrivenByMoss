// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.mode;

import java.util.ArrayList;
import java.util.List;

import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ISend;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISendBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.MuteParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SoloParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.VolumeParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * The 12 mode. The knobs control either the volume, panning or a send of 12 tracks.
 *
 * @author Jürgen Moßgraber
 */
public class EC4TwelveMode extends AbstractEC4Mode<ITrack>
{
    private static final String []      SUB_MODES     =
    {
        "Mn V",
        "Mn P",
        "Mn M",
        "Mn S",
        "MnS1",
        "MnS2",
        "MnS3",
        "MnS4",
        "MnS5",
        "MnS6",
        "MnS7",
        "MnS8"
    };

    private final IParameterProvider [] providers     = new IParameterProvider [12];
    private int                         selectedParam = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public EC4TwelveMode (final EC4ControlSurface surface, final IModel model)
    {
        super (Modes.NAME_VOLUME, surface, model, model.getTrackBank ());

        this.providers[0] = new CombinedParameterProvider (new VolumeParameterProvider (model), this.bottomRowProvider);
        this.providers[1] = new CombinedParameterProvider (new PanParameterProvider (model), this.bottomRowProvider);
        this.providers[2] = new CombinedParameterProvider (new MuteParameterProvider (model), this.bottomRowProvider);
        this.providers[3] = new CombinedParameterProvider (new SoloParameterProvider (model), this.bottomRowProvider);
        for (int i = 0; i < 8; i++)
            this.providers[4 + i] = new CombinedParameterProvider (new SendParameterProvider (model, i, 0), this.bottomRowProvider);

        this.setParameterProvider (this.providers[0]);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (!this.isSession && row < 3)
        {
            if (event == ButtonEvent.DOWN)
            {
                if (this.surface.isShiftPressed ())
                {
                    final ITrack track = this.model.getTrackBank ().getItem (row * 4 + column);
                    switch (this.selectedParam)
                    {
                        case 0:
                            track.resetVolume ();
                            break;
                        case 1:
                            track.resetPan ();
                            break;
                        case 2:
                            track.setMute (false);
                            break;
                        case 3:
                            track.setSolo (false);
                            break;
                        default:
                            track.getSendBank ().getItem (this.selectedParam - 4).resetValue ();
                            break;
                    }
                    return;
                }

                this.selectedParam = row * 4 + column;
                this.setParameterProvider (this.providers[this.selectedParam]);
                this.bindControls ();
            }
            return;
        }

        super.onButton (row, column, event);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final List<String []> totalDisplayInfo = new ArrayList<> ();
        final ITextDisplay display = this.surface.getTextDisplay ().clear ();

        final ITrackBank trackBank = this.model.getTrackBank ();

        for (int i = 0; i < 12; i++)
        {
            final ITrack track = trackBank.getItem (i);
            final ISendBank sendBank = track.getSendBank ();
            final String trackName = track.getPosition () + 1 + ": " + track.getName ();

            String text = "    ";
            if (track.doesExist ())
            {
                switch (this.selectedParam)
                {
                    case 0:
                        text = track.getVolumeStr (4);
                        this.updateCache (i, track.getVolume (), totalDisplayInfo, trackName, "", "Volume: " + track.getVolumeStr ());
                        break;
                    case 1:
                        text = track.getPanStr (4);
                        this.updateCache (i, track.getPan (), totalDisplayInfo, trackName, "", "Pan: " + track.getPanStr ());
                        break;
                    case 2:
                        text = track.isMute () ? "Mute" : "   ";
                        this.updateCache (i, track.isMute () ? 127 : 0, totalDisplayInfo, trackName, "", "Mute: " + (track.isMute () ? "on" : "off"));
                        break;
                    case 3:
                        text = track.isSolo () ? "Solo" : "   ";
                        this.updateCache (i, track.isSolo () ? 127 : 0, totalDisplayInfo, trackName, "", "Solo: " + (track.isSolo () ? "on" : "off"));
                        break;
                    default:
                        final ISend send = sendBank.getItem (this.selectedParam - 4);
                        if (send.doesExist ())
                        {
                            text = send.getDisplayedValue (4);
                            this.updateCache (i, send.getValue (), totalDisplayInfo, trackName, "", i + 1 + ": " + send.getName (7) + ": " + send.getDisplayedValue ());
                        }
                        break;
                }

                if (text.endsWith ("."))
                    text = text.substring (0, 3);
                if (text.length () == 3)
                    text = " " + text;

                display.setCell (i / 4, i % 4, text);
            }
        }

        super.updateDisplayRow4 (display, totalDisplayInfo, SUB_MODES[this.selectedParam]);
        display.allDone ();
        this.surface.fillTotalDisplay (totalDisplayInfo);
    }
}
