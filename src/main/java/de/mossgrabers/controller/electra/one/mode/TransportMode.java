// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.constants.AutomationMode;
import de.mossgrabers.framework.daw.data.IMarker;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.bank.IMarkerBank;
import de.mossgrabers.framework.mode.track.DefaultTrackMode;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;

import java.util.Optional;


/**
 * The transport mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TransportMode extends DefaultTrackMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    private final PageCache                                                     pageCache;
    private final ITransport                                                    transport;
    private final IMasterTrack                                                  masterTrack;
    private final IProject                                                      project;
    private final NewCommand<ElectraOneControlSurface, ElectraOneConfiguration> newCommand;

    private boolean                                                             launchMarkers = false;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public TransportMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super ("Transport", surface, model, true, ElectraOneControlSurface.KNOB_IDS);

        this.pageCache = new PageCache (4, surface);

        this.newCommand = new NewCommand<> (model, surface);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();
        this.project = this.model.getProject ();

        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                new EmptyParameterProvider (5), new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                new EmptyParameterProvider (5), new FixedParameterProvider (this.project.getCueVolumeParameter ()),
                // Row 3-5 have only knobs
                new EmptyParameterProvider (3 * 6),
                // Row 6
                new EmptyParameterProvider (2), new FixedParameterProvider (this.transport.getMetronomeVolumeParameter ()), new EmptyParameterProvider (3)));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (column == 5)
        {
            switch (row)
            {
                // Add marker
                case 2:
                    this.model.getMarkerBank ().addMarker ();
                    break;
                // Toggle marker select/launch
                case 3:
                    this.launchMarkers = !this.launchMarkers;
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
            return;
        }

        switch (row)
        {
            case 0:
                switch (column)
                {
                    case 0:
                        this.newCommand.execute ();
                        break;
                    case 1:
                        this.model.getCursorClip ().quantize (1);
                        break;
                    case 2:
                        this.transport.toggleWriteArrangerAutomation ();
                        break;
                    case 3:
                        this.transport.setAutomationWriteMode (AutomationMode.READ);
                        break;
                    case 4:
                        this.transport.setAutomationWriteMode (AutomationMode.WRITE);
                        break;
                    default:
                        // Not used
                        break;
                }
                break;

            case 1:
                switch (column)
                {
                    case 0:
                        final Optional<ISlot> slot = this.model.getSelectedSlot ();
                        if (slot.isEmpty ())
                            return;
                        final ISlot s = slot.get ();
                        if (!s.isRecording ())
                            s.startRecording ();
                        s.launch ();
                        break;
                    case 1:
                        this.transport.toggleLauncherOverdub ();
                        break;
                    case 2:
                        this.transport.toggleWriteClipLauncherAutomation ();
                        break;
                    case 3:
                        this.transport.setAutomationWriteMode (AutomationMode.LATCH);
                        break;
                    case 4:
                        this.transport.setAutomationWriteMode (AutomationMode.TOUCH);
                        break;
                    default:
                        // Not used
                        break;
                }
                break;

            case 2, 3:
                final IMarkerBank markerBank = this.model.getMarkerBank ();
                if (column == 0)
                {
                    if (row == 2)
                        markerBank.selectNextPage ();
                    else
                        markerBank.selectPreviousPage ();
                    return;
                }
                final IMarker marker = markerBank.getItem (4 * (row - 2) + column - 1);
                if (this.launchMarkers)
                    marker.launch (true);
                else
                    marker.select ();
                break;

            case 4:
                switch (column)
                {
                    case 0:
                        this.model.getApplication ().redo ();
                        break;
                    case 1:
                        this.transport.tapTempo ();
                        break;
                    case 2:
                        this.transport.changeTempo (false, false);
                        break;
                    case 3:
                        this.transport.changeTempo (true, false);
                        break;
                    case 4:
                        this.transport.changePosition (false, false);
                        break;
                    default:
                        // Not used
                        break;
                }
                break;

            case 5:
                switch (column)
                {
                    case 0:
                        this.model.getApplication ().undo ();
                        break;
                    case 1:
                        this.transport.toggleMetronome ();
                        break;
                    case 2:
                        // Not used
                        break;
                    case 3:
                        this.transport.toggleOverdub ();
                        break;
                    case 4:
                        this.transport.changePosition (true, false);
                        break;
                    default:
                        // Not used
                        break;
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public int getButtonColor (final ButtonID buttonID)
    {
        return ElectraOneColorManager.COLOR_BUTTON_STATE_OFF;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final IMarkerBank markerBank = this.model.getMarkerBank ();
        for (int i = 0; i < 4; i++)
        {
            IMarker marker = markerBank.getItem (i);
            this.pageCache.updateElement (2, 1 + i, marker.getName (), marker.getColor (), Boolean.valueOf ((marker.doesExist ())));
            marker = markerBank.getItem (4 + i);
            this.pageCache.updateElement (3, 1 + i, marker.getName (), marker.getColor (), Boolean.valueOf ((marker.doesExist ())));
        }

        this.pageCache.updateColor (0, 2, this.transport.isWritingArrangerAutomation () ? ElectraOneColorManager.AUTO_ON : ElectraOneColorManager.AUTO_OFF);
        this.pageCache.updateColor (1, 2, this.transport.isWritingClipLauncherAutomation () ? ElectraOneColorManager.AUTO_ON : ElectraOneColorManager.AUTO_OFF);
        this.pageCache.updateColor (1, 1, this.transport.isLauncherOverdub () ? ElectraOneColorManager.AUTO_MODE_ON : ElectraOneColorManager.AUTO_MODE_OFF);

        final AutomationMode automationWriteMode = this.transport.getAutomationWriteMode ();
        this.pageCache.updateColor (0, 3, automationWriteMode == AutomationMode.READ ? ElectraOneColorManager.AUTO_MODE_ON : ElectraOneColorManager.AUTO_MODE_OFF);
        this.pageCache.updateColor (0, 4, automationWriteMode == AutomationMode.WRITE ? ElectraOneColorManager.AUTO_MODE_ON : ElectraOneColorManager.AUTO_MODE_OFF);
        this.pageCache.updateColor (1, 3, automationWriteMode == AutomationMode.LATCH ? ElectraOneColorManager.AUTO_MODE_ON : ElectraOneColorManager.AUTO_MODE_OFF);
        this.pageCache.updateColor (1, 4, automationWriteMode == AutomationMode.TOUCH ? ElectraOneColorManager.AUTO_MODE_ON : ElectraOneColorManager.AUTO_MODE_OFF);

        this.pageCache.updateColor (3, 5, this.launchMarkers ? ElectraOneColorManager.MARKER_LAUNCH_ON : ElectraOneColorManager.MARKER_LAUNCH_OFF);

        this.pageCache.updateColor (5, 1, this.transport.isMetronomeOn () ? ElectraOneColorManager.METRONOME_ON : ElectraOneColorManager.METRONOME_OFF);
        this.pageCache.updateValue (5, 2, this.transport.getMetronomeVolume ());
        this.pageCache.updateColor (5, 3, this.transport.isArrangerOverdub () ? ElectraOneColorManager.AUTO_MODE_ON : ElectraOneColorManager.AUTO_MODE_OFF);

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume ());
        this.pageCache.updateValue (1, 5, this.project.getCueVolume ());

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        this.pageCache.reset ();

        super.onActivate ();
    }
}