package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.PanParameterProvider;
import de.mossgrabers.framework.parameterprovider.track.SendParameterProvider;

import java.util.List;


/**
 * Mix mode for the LauchControl XL to control 2 sends and panorama.
 *
 * @author Jürgen Moßgraber
 */
public class XLTrackMixMode extends XLAbstractTrackMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLTrackMixMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Send A, B & Panorama", surface, model, controls);

        final IParameterProvider sendParameterProvider1 = new SendParameterProvider (model, 0, 0);
        final IParameterProvider sendParameterProvider2 = new SendParameterProvider (model, 1, 0);
        final IParameterProvider panParameterProvider = new PanParameterProvider (model);
        final IParameterProvider deviceParameterProvider = new BankParameterProvider (this.model.getCursorDevice ().getParameterBank ());

        this.setParameterProviders (
                // Control sends and pan
                new CombinedParameterProvider (sendParameterProvider1, sendParameterProvider2, panParameterProvider),
                // Control sends and device parameters
                new CombinedParameterProvider (sendParameterProvider1, sendParameterProvider2, deviceParameterProvider));
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        final ITrack track = this.model.getTrackBank ().getItem (column);
        switch (row)
        {
            case 0:
                return track.getSendBank ().getItem (0).getValue ();
            case 1:
                return track.getSendBank ().getItem (1).getValue ();
            case 2:
                if (this.configuration.isDeviceActive ())
                    return this.model.getCursorDevice ().getParameterBank ().getItem (column).getValue ();
                return track.getPan ();
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setKnobColor (final int row, final int column, final int value)
    {
        int green = 0;
        int red = 0;
        switch (row)
        {
            // Send A intensity in green
            case 0:
                green = value == 0 ? 0 : value / 42 + 1;
                break;

            // Send B intensity in red
            case 1:
                red = value == 0 ? 0 : value / 42 + 1;
                break;

            // Panorama in amber or Device parameters yellowish intensity in red
            case 2:
                green = value == 0 ? 0 : value / 42 + 1;
                if (this.configuration.isDeviceActive ())
                    red = green == 0 ? 0 : 1;
                else
                    red = green;
                break;

            default:
                // Not used
                return;
        }
        this.surface.setKnobLEDColor (row, column, green, red);
    }
}
