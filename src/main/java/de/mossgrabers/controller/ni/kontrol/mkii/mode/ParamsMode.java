// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2025
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.kontrol.mkii.mode;

import java.util.List;
import java.util.Optional;

import de.mossgrabers.controller.ni.kontrol.mkii.KontrolProtocolConfiguration;
import de.mossgrabers.controller.ni.kontrol.mkii.TrackType;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocol;
import de.mossgrabers.controller.ni.kontrol.mkii.controller.KontrolProtocolControlSurface;
import de.mossgrabers.framework.controller.ContinuousID;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.bank.IDeviceBank;
import de.mossgrabers.framework.daw.data.bank.IParameterBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.device.BankParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The parameters mode.
 *
 * @author Jürgen Moßgraber
 */
public class ParamsMode extends AbstractParameterMode<KontrolProtocolControlSurface, KontrolProtocolConfiguration, IParameter>
{
    private static final String []      BANK_NAMES          =
    {
        "Cursor Device ",
        "Track ",
        "Project "
    };

    private final IParameterBank []     banks;
    private final IParameterProvider [] providers           = new IParameterProvider [3];
    private int                         activeProviderIndex = 0;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     * @param controls The IDs of the knobs or faders to control this mode
     */
    public ParamsMode (final KontrolProtocolControlSurface surface, final IModel model, final List<ContinuousID> controls)
    {
        super ("Parameters", surface, model, false);

        this.setControls (controls);

        this.banks = new IParameterBank []
        {
            model.getCursorDevice ().getParameterBank (),
            model.getCursorTrack ().getParameterBank (),
            model.getProject ().getParameterBank ()
        };

        for (int i = 0; i < this.banks.length; i++)
        {
            final IParameterProvider cursorDeviceProvider = new BankParameterProvider (this.banks[i]);
            this.providers[i] = new CombinedParameterProvider (cursorDeviceProvider, cursorDeviceProvider);
        }

        this.selectProvider (0);
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

        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final IDeviceBank deviceBank = cursorDevice.getDeviceBank ();

        switch (index)
        {
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_BANKS:
                return (this.bank.canScrollBackwards () ? 1 : 0) + (this.bank.canScrollForwards () ? 2 : 0);
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_TRACKS:
                return (deviceBank.canScrollBackwards () ? 1 : 0) + (deviceBank.canScrollForwards () ? 2 : 0);
            case KontrolProtocolControlSurface.KONTROL_NAVIGATE_CLIPS:
                return (this.activeProviderIndex > 0 ? 1 : 0) + (this.activeProviderIndex < 2 ? 2 : 0);
            default:
                return 0;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IValueChanger valueChanger = this.model.getValueChanger ();

        final IParameterPageBank parameterPageBank = this.banks[this.activeProviderIndex].getPageBank ();
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

            // Switch off all mutes and solos otherwise "tracks" will be darkened
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_MUTE, 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_SOLO, 0, i);
            this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_MUTED_BY_SOLO, 0, i);
            this.surface.sendCommand (KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_AVAILABLE, 0);
            this.surface.sendCommand (KontrolProtocolControlSurface.KONTROL_SELECTED_TRACK_MUTED_BY_SOLO, 0);
        }
        this.surface.sendKontrolTrackSysEx (KontrolProtocolControlSurface.KONTROL_TRACK_VU, 2, 0, vuData);
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItem ()
    {
        this.model.getCursorDevice ().selectPrevious ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItem ()
    {
        this.model.getCursorDevice ().selectNext ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectPreviousItemPage ()
    {
        this.bank.scrollBackwards ();
    }


    /** {@inheritDoc} */
    @Override
    public void selectNextItemPage ()
    {
        this.bank.scrollForwards ();
    }


    private String getLabel (final String selectedPage, final IParameter parameter)
    {
        final String n = parameter.doesExist () ? parameter.getName (16) : "None";

        if (this.surface.getProtocolVersion () == KontrolProtocol.VERSION_1)
            return n;

        final String deviceName;
        switch (this.activeProviderIndex)
        {
            default:
            case 0:
                deviceName = this.model.getCursorDevice ().getName (8);
                break;
            case 1:
                deviceName = "Track";
                break;
            case 2:
                deviceName = "Project";
                break;
        }
        return deviceName + "\n" + selectedPage + "\n" + n;
    }


    /**
     * Switch to the previous or next provider.
     *
     * @param isLeft Select the previous if true
     */
    public void switchProvider (final boolean isLeft)
    {
        this.selectProvider (isLeft ? this.activeProviderIndex - 1 : this.activeProviderIndex + 1);
    }


    private void selectProvider (final int index)
    {
        this.activeProviderIndex = Math.max (0, Math.min (2, index));

        this.switchBanks (this.banks[this.activeProviderIndex]);
        this.setParameterProvider (this.providers[this.activeProviderIndex]);
        this.bindControls ();

        this.mvHelper.notifySelectedParameterPage (this.banks[this.activeProviderIndex], BANK_NAMES[this.activeProviderIndex]);
    }
}
