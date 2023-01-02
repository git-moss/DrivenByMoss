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
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.mode.device.ParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;

import java.util.List;
import java.util.Optional;


/**
 * The parameters mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ParamsMode extends ParameterMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public ParamsMode (final KontrolProtocolControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super (surface, model, false);

        this.setControls (controls);
        final BankParameterProvider pp = new BankParameterProvider (this.cursorDevice.getParameterBank ());
        this.setParameterProvider (new CombinedParameterProvider (pp, pp));
    }


    /** {@inheritDoc} */
    @Override
    public int getKnobValue (final int index)
    {
        // Note: Since we need multiple value (more than 8), index is the MIDI CC of the knob

        final IValueChanger valueChanger = this.model.getValueChanger ();

        if (index >= KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME && index < KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME + 8)
        {
            final IParameter parameter = this.bank.getItem (index - KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME);
            return valueChanger.toMidiValue (parameter.getValue ());
        }

        if (index >= KontrolProtocolControlSurface.KONTROL_TRACK_PAN && index < KontrolProtocolControlSurface.KONTROL_TRACK_PAN + 8)
        {
            final IParameter parameter = this.bank.getItem (index - KontrolProtocolControlSurface.KONTROL_TRACK_PAN);
            return valueChanger.toMidiValue (parameter.getValue ());
        }

        final int scrollTracksState = (this.bank.canScrollBackwards () ? 1 : 0) + (this.bank.canScrollForwards () ? 2 : 0);

        final IDeviceBank deviceBank = this.cursorDevice.getDeviceBank ();
        final int scrollScenesState = (deviceBank.canScrollBackwards () ? 1 : 0) + (deviceBank.canScrollForwards () ? 2 : 0);

        final KontrolProtocolConfiguration configuration = this.surface.getConfiguration ();
        switch (index)
        {
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS:
                return (this.cursorDevice.canSelectPrevious () ? 1 : 0) + (this.cursorDevice.canSelectNext () ? 2 : 0);
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

        final IParameterPageBank parameterPageBank = this.cursorDevice.getParameterPageBank ();
        final Optional<String> selectedItem = parameterPageBank.getSelectedItem ();
        final String selectedPage = selectedItem.isPresent () ? StringUtils.optimizeName (selectedItem.get (), 8) : "";

        final int [] vuData = new int [16];
        for (int i = 0; i < 8; i++)
        {
            final IParameter parameter = this.bank.getItem (i);

            // Track Available
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_AVAILABLE, TrackType.GENERIC, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_SELECTED, parameter.isSelected () ? 1 : 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_RECARM, 0, i);
            final String info = parameter.doesExist () ? parameter.getDisplayedValue (8) : " ";
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VOLUME_TEXT, 0, i, info);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_PAN_TEXT, 0, i, info);
            final String name = this.getLabel (selectedPage, parameter);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_NAME, 0, i, name);

            final int j = 2 * i;
            vuData[j] = valueChanger.toMidiValue (parameter.getModulatedValue ());
            vuData[j + 1] = valueChanger.toMidiValue (parameter.getModulatedValue ());
        }
        this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VU, 2, 0, vuData);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.cursorDevice.getParameterBank ().scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.cursorDevice.getParameterBank ().scrollForwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.cursorDevice.selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.cursorDevice.selectNext ();
    }


    private String getLabel (final String selectedPage, final IParameter parameter)
    {
        final String n = parameter.doesExist () ? parameter.getName (16) : "None";

        if (this.surface.getProtocolVersion () == KontrolProtocol.VERSION_1)
            return n;

        return this.cursorDevice.getName (8) + "\n" + selectedPage + "\n" + n;
    }
}
