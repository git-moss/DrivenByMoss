// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.ni.maschine.jam.controller;

import de.mossgrabers.controller.ni.maschine.jam.MaschineJamConfiguration;
import de.mossgrabers.controller.ni.maschine.jam.command.trigger.EncoderMode;
import de.mossgrabers.controller.ni.maschine.jam.view.IMaschineJamView;
import de.mossgrabers.framework.MVHelper;
import de.mossgrabers.framework.command.continuous.PlayPositionCommand;
import de.mossgrabers.framework.command.continuous.SwingCommand;
import de.mossgrabers.framework.command.continuous.TempoCommand;
import de.mossgrabers.framework.command.core.ContinuousCommand;
import de.mossgrabers.framework.controller.hardware.IHwRelativeKnob;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IParameter;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.featuregroup.IView;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;


/**
 * Manages the active encoder mode.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EncoderModeManager implements ContinuousCommand
{
    private static final Map<EncoderMode, String> MODE_LABELS         = new EnumMap<> (EncoderMode.class);
    private static final Map<EncoderMode, String> TOGGLED_MODE_LABELS = new EnumMap<> (EncoderMode.class);
    static
    {
        MODE_LABELS.put (EncoderMode.MASTER_VOLUME, "Master: Volume");
        MODE_LABELS.put (EncoderMode.SELECTED_TRACK_VOLUME, "Selected track: Volume");
        MODE_LABELS.put (EncoderMode.METRONOME_VOLUME, "Metronome: Volume");
        MODE_LABELS.put (EncoderMode.CUE_VOLUME, "Cue: Volume");
        MODE_LABELS.put (EncoderMode.TEMPORARY_TEMPO, "Tempo");
        MODE_LABELS.put (EncoderMode.TEMPORARY_SWING, "Swing");
        MODE_LABELS.put (EncoderMode.TEMPORARY_PLAY_POSITION, "Play Position");

        TOGGLED_MODE_LABELS.put (EncoderMode.MASTER_VOLUME, "Master: Panorama");
        TOGGLED_MODE_LABELS.put (EncoderMode.SELECTED_TRACK_VOLUME, "Selected track: Panorama");
        TOGGLED_MODE_LABELS.put (EncoderMode.METRONOME_VOLUME, "Metronome: Volume");
        TOGGLED_MODE_LABELS.put (EncoderMode.CUE_VOLUME, "Cue: Mix");
        TOGGLED_MODE_LABELS.put (EncoderMode.TEMPORARY_TEMPO, "Tempo");
        TOGGLED_MODE_LABELS.put (EncoderMode.TEMPORARY_SWING, "Swing");
        TOGGLED_MODE_LABELS.put (EncoderMode.TEMPORARY_PLAY_POSITION, "Play Position");
    }

    private final IHwRelativeKnob                                                          encoder;
    private final IModel                                                                   model;
    private final MaschineJamControlSurface                                                surface;
    private final MVHelper<MaschineJamControlSurface, MaschineJamConfiguration>            mvHelper;

    private EncoderMode                                                                    activeEncoderMode    = null;
    private EncoderMode                                                                    temporaryEncoderMode = null;
    private boolean                                                                        isFunctionToggled    = false;

    private final TempoCommand<MaschineJamControlSurface, MaschineJamConfiguration>        tempoCommand;
    private final SwingCommand<MaschineJamControlSurface, MaschineJamConfiguration>        swingCommand;
    private final PlayPositionCommand<MaschineJamControlSurface, MaschineJamConfiguration> playPositionCommand;


    /**
     * Constructor.
     *
     * @param encoder The encoder knob
     * @param model The model
     * @param surface The controller surface
     */
    public EncoderModeManager (final IHwRelativeKnob encoder, final IModel model, final MaschineJamControlSurface surface)
    {
        this.encoder = encoder;
        this.model = model;
        this.surface = surface;
        this.mvHelper = new MVHelper<> (model, surface);

        this.tempoCommand = new TempoCommand<> (model, surface);
        this.swingCommand = new SwingCommand<> (model, surface);
        this.playPositionCommand = new PlayPositionCommand<> (model, surface);

        this.model.getTrackBank ().addSelectionObserver ( (index, isSelected) -> this.handleTrackChange (isSelected));
    }


    /**
     * Get the active encoder mode.
     *
     * @return The active encoder mode
     */
    public EncoderMode getActiveEncoderMode ()
    {
        return this.activeEncoderMode;
    }


    /**
     * Set the active encoder mode.
     *
     * @param encoderMode The mode to activate
     */
    public void setActiveEncoderMode (final EncoderMode encoderMode)
    {
        if (this.activeEncoderMode == encoderMode)
            return;
        this.activeEncoderMode = encoderMode;
        this.updateBinding ();
    }


    /**
     * Toggles between the first and second order function that can be controlled.
     */
    public void toggleFunction ()
    {
        this.isFunctionToggled = !this.isFunctionToggled;
        this.updateBinding ();
    }


    /**
     * Is the given mode the active one?
     *
     * @param encoderMode The mode to test
     * @return True if the given mode is the active one
     */
    public boolean isActiveEncoderMode (final EncoderMode encoderMode)
    {
        return this.activeEncoderMode == encoderMode;
    }


    /**
     * Enable a temporary function like tempo, playback position, etc.
     *
     * @param encoderMode The temporary mode to activate
     */
    public void enableTemporaryEncodeMode (final EncoderMode encoderMode)
    {
        this.temporaryEncoderMode = encoderMode;
        this.encoder.bind ((IParameter) null);
        this.mvHelper.delayDisplay ( () -> MODE_LABELS.get (this.temporaryEncoderMode));
    }


    /**
     * Disable the active temporary function and activate the previous mode, if any.
     */
    public void disableTemporaryEncodeMode ()
    {
        this.temporaryEncoderMode = null;
        this.updateBinding ();
    }


    /**
     * Update the function binding depending on the set mode and toggle state.
     */
    private void updateBinding ()
    {
        if (this.temporaryEncoderMode != null)
            return;

        final IParameter parameter;
        switch (this.activeEncoderMode)
        {
            case SELECTED_TRACK_VOLUME:
                final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
                if (selectedTrack.isEmpty ())
                    return;
                final ITrack t = selectedTrack.get ();
                parameter = this.isFunctionToggled ? t.getPanParameter () : t.getVolumeParameter ();
                break;

            case METRONOME_VOLUME:
                parameter = this.model.getTransport ().getMetronomeVolumeParameter ();
                break;

            case CUE_VOLUME:
                final IProject project = this.model.getProject ();
                parameter = this.isFunctionToggled ? project.getCueMixParameter () : project.getCueVolumeParameter ();
                break;

            default:
            case MASTER_VOLUME:
                final IMasterTrack masterTrack = this.model.getMasterTrack ();
                parameter = this.isFunctionToggled ? masterTrack.getPanParameter () : masterTrack.getVolumeParameter ();
                break;
        }

        this.encoder.bind (parameter);
        final Map<EncoderMode, String> labels = this.isFunctionToggled ? TOGGLED_MODE_LABELS : MODE_LABELS;
        this.mvHelper.delayDisplay ( () -> labels.get (this.activeEncoderMode));
    }


    /**
     * Bind the selected tracks' volume parameter.
     *
     * @param isSelected True if the track got selected
     */
    private void handleTrackChange (final boolean isSelected)
    {
        if (!isSelected || this.activeEncoderMode != EncoderMode.SELECTED_TRACK_VOLUME)
            return;
        final Optional<ITrack> selectedTrack = this.model.getTrackBank ().getSelectedItem ();
        if (selectedTrack.isPresent ())
            this.encoder.bind (selectedTrack.get ().getVolumeParameter ());
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final int value)
    {
        if (this.temporaryEncoderMode == null)
            return;

        switch (this.temporaryEncoderMode)
        {
            case TEMPORARY_TEMPO:
                this.tempoCommand.execute (value);
                break;

            case TEMPORARY_SWING:
                this.swingCommand.execute (value);
                break;

            case TEMPORARY_PLAY_POSITION:
                this.playPositionCommand.execute (value);
                break;

            case TEMPORARY_PERFORM:
            case TEMPORARY_NOTES:
            case TEMPORARY_LOCK:
            case TEMPORARY_TUNE:
                final IView activeView = this.surface.getViewManager ().getActive ();
                if (activeView instanceof IMaschineJamView)
                    ((IMaschineJamView) activeView).changeOption (this.temporaryEncoderMode, value);
                break;

            case TEMPORARY_BROWSER:
                this.handleBrowser (value);
                break;

            default:
                // Not used
                break;
        }
    }


    private void handleBrowser (final int value)
    {
        final IBrowser browser = this.model.getBrowser ();
        final boolean increase = this.model.getValueChanger ().isIncrease (value);

        if (this.surface.isShiftPressed ())
        {
            if (increase)
                browser.nextContentType ();
            else
                browser.previousContentType ();
            return;
        }

        if (this.surface.isSelectPressed ())
        {
            final ICursorDevice cursorDevice = this.model.getCursorDevice ();
            if (!cursorDevice.doesExist ())
                return;
            if (increase)
                browser.insertAfterCursorDevice ();
            else
                browser.insertBeforeCursorDevice ();
            return;
        }

        if (increase)
            browser.selectNextResult ();
        else
            browser.selectPreviousResult ();
    }
}
