// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.daw.data.empty.EmptyTrack;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.MuteParameter;
import de.mossgrabers.framework.parameter.SoloParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.RangeFilterParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SelectedTrackParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * This mode allows to edit the parameters of either the selected device, the track or the project.
 *
 * @author Jürgen Moßgraber
 */
public class EC4ParametersMode extends AbstractEC4Mode<IParameter>
{
    private static final String []      PARAM_MODES   =
    {
        "Project",
        "Track",
        "Selected Device"
    };

    private final IParameterBank []     banks         = new IParameterBank [3];
    private final IParameterProvider [] providers     = new IParameterProvider [3];
    private int                         selectedParam = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public EC4ParametersMode (final EC4ControlSurface surface, final IModel model)
    {
        super (Modes.NAME_PARAMETERS, surface, model, null);

        this.banks[0] = this.model.getProject ().getParameterBank ();
        this.banks[1] = this.model.getCursorTrack ().getParameterBank ();
        this.banks[2] = this.model.getCursorDevice ().getParameterBank ();
        this.switchBanks (this.banks[0]);

        final IParameterProvider trackProvider = new SelectedTrackParameterProvider (model);
        final CombinedParameterProvider selTrackProvider = new CombinedParameterProvider (new RangeFilterParameterProvider (trackProvider, 0, 2), new FixedParameterProvider (new MuteParameter (model)), new FixedParameterProvider (new SoloParameter (model)));
        for (int i = 0; i < 3; i++)
            this.providers[i] = new CombinedParameterProvider (selTrackProvider, new BankParameterProvider (this.banks[i]), this.bottomRowProvider);
        this.setParameterProvider (this.providers[0]);
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (!this.isSession && row < 3)
        {
            if (event != ButtonEvent.DOWN)
                return;

            if (row == 0)
            {
                if (this.surface.isShiftPressed ())
                {
                    final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
                    if (selectedTrack.isEmpty ())
                        return;
                    switch (column)
                    {
                        case 0:
                            selectedTrack.get ().resetVolume ();
                            break;
                        case 1:
                            selectedTrack.get ().resetPan ();
                            break;
                        case 2:
                            selectedTrack.get ().setMute (false);
                            break;
                        case 3:
                            selectedTrack.get ().setSolo (false);
                            break;
                    }
                    return;
                }

                if (column < 3)
                {
                    this.selectedParam = column;
                    this.switchBanks (this.banks[this.selectedParam]);
                    this.setParameterProvider (this.providers[this.selectedParam]);
                    this.bindControls ();
                    this.model.getHost ().showNotification (PARAM_MODES[this.selectedParam]);
                }
                else
                    this.model.getCursorDevice ().toggleWindowOpen ();
            }
            else
            {
                final int index = (row - 1) * 4 + column;
                if (this.surface.isShiftPressed ())
                {
                    final IDevice device = this.model.getCursorDevice ().getDeviceBank ().getItem (index);
                    if (device.doesExist ())
                        device.select ();
                }
                else
                {
                    final IParameterPageBank pageBank = ((IParameterBank) this.getBank ()).getPageBank ();
                    pageBank.selectPage (index);
                    this.model.getHost ().showNotification (pageBank.getItem (index));
                }
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

        display.setCell (0, 0, "Vol ").setCell (0, 1, "Pan ").setCell (0, 2, "Mute").setCell (0, 3, "Solo");

        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        ITrack track = EmptyTrack.getInstance (8);
        String trackName = "";
        if (selectedTrack.isPresent ())
        {
            track = selectedTrack.get ();
            trackName = track.getPosition () + 1 + ": " + track.getName ();
            this.updateCache (0, track.getVolume (), totalDisplayInfo, trackName, "", "Volume: " + track.getVolumeStr ());
            this.updateCache (1, track.getPan (), totalDisplayInfo, trackName, "", "Pan: " + track.getPanStr ());
            this.updateCache (2, track.isMute () ? 127 : 0, totalDisplayInfo, trackName, "", "Mute: " + (track.isMute () ? "on" : "off"));
            this.updateCache (3, track.isSolo () ? 127 : 0, totalDisplayInfo, trackName, "", "Solo: " + (track.isSolo () ? "on" : "off"));
        }

        if (this.selectedParam == 0)
            trackName = "   Project";

        final IParameterBank paramBank = (IParameterBank) this.getBank ();
        final IParameterPageBank pageBank = paramBank.getPageBank ();
        final Optional<String> selectedPage = pageBank.getSelectedItem ();
        final String pageName = selectedPage.isPresent () ? pageBank.getSelectedItemIndex () + 1 + ": " + selectedPage.get () : "";

        for (int i = 0; i < 8; i++)
        {
            final IParameter param = paramBank.getItem (i);
            display.setCell (1 + i / 4, i % 4, param.getName (4));
            String label = param.doesExist () ? param.getName () + ": " : "None";
            final String valueStr = param.getDisplayedValue ();
            final int longer = label.length () + valueStr.length () - 17;
            if (longer > 0)
                label = StringUtils.shortenAndFixASCII (label, Math.max (2, label.length () - longer));
            this.updateCache (4 + i, param.getValue (), totalDisplayInfo, trackName, pageName, i + 1 + ": " + label + valueStr);
        }

        super.updateDisplayRow4 (display, totalDisplayInfo, "Main");
        display.allDone ();
        this.surface.fillTotalDisplay (totalDisplayInfo);
    }
}
