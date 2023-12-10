// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
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
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The 12 mode. The knobs control either the volume, panorama or a send of 12 tracks.
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
        final List<String> totalDisplayInfo = new ArrayList<> ();
        final ITextDisplay display = this.surface.getTextDisplay ().clear ();

        final ITrackBank trackBank = this.model.getTrackBank ();

        for (int i = 0; i < 12; i++)
        {
            final ITrack track = trackBank.getItem (i);
            final ISendBank sendBank = track.getSendBank ();

            String text = "    ";
            if (track.doesExist ())
            {
                switch (this.selectedParam)
                {
                    case 0:
                        text = track.getVolumeStr (4);
                        updateCache (i, track.getVolume (), "Volume: " + track.getVolumeStr (), totalDisplayInfo);
                        break;
                    case 1:
                        text = track.getPanStr (4);
                        updateCache (i, track.getPan (), "Pan: " + track.getPanStr (), totalDisplayInfo);
                        break;
                    case 2:
                        text = track.isMute () ? "Mute" : "   ";
                        updateCache (i, track.isMute () ? 127 : 0, "Mute: " + (track.isMute () ? "on" : "off"), totalDisplayInfo);
                        break;
                    case 3:
                        text = track.isSolo () ? "Solo" : "   ";
                        updateCache (i, track.isSolo () ? 127 : 0, "Solo: " + (track.isSolo () ? "on" : "off"), totalDisplayInfo);
                        break;
                    default:
                        final ISend send = sendBank.getItem (this.selectedParam - 4);
                        if (send.doesExist ())
                        {
                            text = send.getDisplayedValue (4);
                            updateCache (i, send.getValue (), (i + 1) + ":" + send.getName () + ": " + send.getDisplayedValue (), totalDisplayInfo);
                        }
                        break;
                }

                if (text.endsWith ("."))
                    text = text.substring (0, 3);

                display.setCell (i / 4, i % 4, text);
            }
        }

        super.updateDisplayRow4 (display, totalDisplayInfo, SUB_MODES[this.selectedParam]);

        display.allDone ();

        if (!totalDisplayInfo.isEmpty ())

        {
            final ITextDisplay totalDisplay = this.surface.getTextDisplay (1).clear ();
            // TODO
            // totalDisplay.setRow (0, StringUtils.pad (trackName == null ? "Select a track" :
            // (track.getPosition () + 1) + ": " + StringUtils.fixASCII (trackName), 20));
            totalDisplay.setRow (2, StringUtils.pad (StringUtils.fixASCII (totalDisplayInfo.get (0)), 20));
            totalDisplay.allDone ();
            this.surface.showTotalDisplay ();
        }
    }
}