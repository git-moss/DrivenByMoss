package de.mossgrabers.controller.novation.launchcontrol.mode.main;

import de.mossgrabers.controller.novation.launchcontrol.controller.LaunchControlXLControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.IEqualizerDevice;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Equalizer mode for the LauchControl XL to control 8 EQ bands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class XLEqMode extends XLAbstractTrackMode
{
    private final IEqualizerDevice eqDevice;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public XLEqMode (final LaunchControlXLControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Equalizer", surface, model, controls);

        this.eqDevice = (IEqualizerDevice) model.getSpecificDevice (DeviceID.EQ);

        final List<IParameter> parameters = new ArrayList<> (24);
        for (int i = 0; i < 8; i++)
            parameters.add (this.eqDevice.getType (i));
        for (int i = 0; i < 8; i++)
            parameters.add (this.eqDevice.getQ (i));
        for (int i = 0; i < 8; i++)
            parameters.add (this.eqDevice.getFrequency (i));

        this.setParameterProvider (new FixedParameterProvider (parameters));

        this.model.getTrackBank ().addSelectionObserver (this::trackSelectionChanged);
        final ITrackBank effectTrackBank = this.model.getEffectTrackBank ();
        if (effectTrackBank != null)
            effectTrackBank.addSelectionObserver (this::trackSelectionChanged);

    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        final int row = index / 8;
        final int column = index % 8;
        switch (row)
        {
            case 0:
                return this.eqDevice.getType (column).getValue ();
            case 1:
                return this.eqDevice.getQ (column).getValue ();
            case 2:
                return this.eqDevice.getFrequency (column).getValue ();
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
            // EQ type: Center is Bell (green). Left side chooses between different low-cuts (red)
            // and right side different high-cuts (yellow). Fully left turns the band off (black).
            // Fully right sets it to notch (amber).
            case 0:
                switch (this.eqDevice.getTypeID (column))
                {
                    case BELL:
                        green = 3;
                        break;
                    case HIGHCUT:
                        red = 1;
                        green = 3;
                        break;
                    case HIGHSHELF:
                        red = 1;
                        green = 2;
                        break;
                    case LOWSHELF:
                        red = 3;
                        break;
                    case LOWCUT:
                        red = 2;
                        break;
                    case NOTCH:
                        break;
                    default:
                    case OFF:
                        // Already set to black
                        break;
                }
                break;

            // EQ Q in green
            case 1:
                green = value == 0 ? 0 : value / 42 + 1;
                break;

            // EQ frequency in amber
            case 2:
                green = value == 0 ? 0 : value / 42 + 1;
                red = green;
                break;

            default:
                // Not used
                return;
        }
        this.surface.setKnobLEDColor (row, column, green, red);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        this.trackSelectionChanged (-1, true);
    }


    /**
     * Add a equalizer device to a track which does not already contain one.
     *
     * @param index The index of the selected or de-selected track
     * @param isSelected Is the track selected or de-selected?
     */
    private void trackSelectionChanged (final int index, final boolean isSelected)
    {
        if (!(isSelected && this.isActive))
            return;

        // Add an equalizer if not present
        if (!this.eqDevice.doesExist ())
        {
            final Optional<ITrack> selectedTrack = this.model.getCurrentTrackBank ().getSelectedItem ();
            if (selectedTrack.isPresent ())
                selectedTrack.get ().addEqualizerDevice ();
        }
    }
}
