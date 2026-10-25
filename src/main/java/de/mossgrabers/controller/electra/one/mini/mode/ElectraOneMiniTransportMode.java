// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mini.mode;

import java.util.Optional;

import de.mossgrabers.controller.electra.one.mini.ElectraOneMiniConfiguration;
import de.mossgrabers.controller.electra.one.mini.controller.ElectraOneMiniControlSurface;
import de.mossgrabers.framework.command.trigger.clip.NewCommand;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.GrooveParameterID;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.parameter.IFocusedParameter;
import de.mossgrabers.framework.parameter.IParameter;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameter.TempoParameter;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The transport mode.
 *
 * @author Jürgen Moßgraber
 */
public class ElectraOneMiniTransportMode extends AbstractElectraOneMiniMode
{
    private final ITransport                                                             transport;
    private final IMasterTrack                                                           masterTrack;
    private final IApplication                                                           application;
    private final IProject                                                               project;
    private final IParameter                                                             amountParameter;
    private final NewCommand<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration>  newCommand;
    private final PlayCommand<ElectraOneMiniControlSurface, ElectraOneMiniConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public ElectraOneMiniTransportMode (final ElectraOneMiniControlSurface surface, final IModel model)
    {
        super (2, "Transport", surface, model);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();
        this.project = this.model.getProject ();
        this.application = this.model.getApplication ();
        this.amountParameter = model.getGroove ().getParameter (GrooveParameterID.SHUFFLE_AMOUNT);

        this.newCommand = new NewCommand<> (model, surface);
        this.playCommand = new PlayCommand<> (model, surface);

        final Optional<IFocusedParameter> focusedParameter = this.model.getFocusedParameter ();
        if (focusedParameter.isEmpty ())
            throw new IllegalArgumentException ("Focused Parameter is not configured (in MasterVolumeMode)!");

        final EmptyParameterProvider emptyParameterProvider = new EmptyParameterProvider (1);

        this.setParameterProvider (new CombinedParameterProvider (
                // Slot 1: Position
                new FixedParameterProvider (new PlayPositionParameter (model.getValueChanger (), this.transport, surface)),
                // Slot 2: -
                emptyParameterProvider,
                // Slot 3: Swing
                new FixedParameterProvider (this.amountParameter),
                // Slot 4: Tempo
                new FixedParameterProvider (new TempoParameter (model.getValueChanger (), this.transport, surface)),
                // Slot 5: Last Parameter
                new FixedParameterProvider (focusedParameter.get ()),
                // Slot 6: Metronome volume
                new FixedParameterProvider (this.transport.getMetronomeVolumeParameter ()),
                // Slot 7: Cue volume
                new FixedParameterProvider (this.project.getCueVolumeParameter ()),
                // Slot 8: Master volume
                new FixedParameterProvider (this.masterTrack.getVolumeParameter ())));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int column, final ButtonEvent event)
    {
        // The 4 function buttons of the Mini
        if (row == 1)
        {
            this.handleFunctionButtons (column, event);
            return;
        }

        // The push buttons of the knobs

        if (event != ButtonEvent.DOWN)
            return;

        switch (column)
        {
            case 0:
                this.application.redo ();
                break;

            case 1:
                this.application.undo ();
                break;

            case 2:
                this.transport.toggleWriteArrangerAutomation ();
                break;

            case 3:
                this.transport.toggleOverdub ();
                break;

            case 4:
                this.newCommand.execute ();
                break;

            case 5:
                final Optional<ISlot> slot = this.model.getSelectedSlot ();
                if (slot.isEmpty ())
                    return;
                final ISlot s = slot.get ();
                if (!s.isRecording ())
                    s.startRecording ();
                s.launch (true, false);
                break;

            case 6:
                this.model.getCursorClip ().quantize (1);
                break;

            case 7:
                this.transport.toggleLauncherOverdub ();
                break;

            // Not used
            default:
                break;
        }
    }


    private void handleFunctionButtons (final int column, final ButtonEvent event)
    {
        switch (column)
        {
            case 0:
                if (event == ButtonEvent.DOWN)
                    this.transport.toggleMetronome ();
                break;

            case 1:
                if (event == ButtonEvent.DOWN)
                    this.transport.tapTempo ();
                break;

            case 2:
                if (event == ButtonEvent.DOWN)
                {
                    if (this.surface.isShiftPressed ())
                        this.transport.startRecording ();
                    else
                        this.playCommand.execute (ButtonEvent.UP, 127);
                }
                break;

            case 3:
                if (event != ButtonEvent.LONG)
                    this.surface.updateShift (event == ButtonEvent.DOWN);
                break;

            // There are no more
            default:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        this.pageCache.updateValue (0, 0, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
        this.pageCache.updateValue (0, 2, this.amountParameter.getValue (), this.amountParameter.getDisplayedValue ());
        this.pageCache.updateValue (0, 3, 0, this.transport.formatTempo (this.transport.getTempo ()));

        final Optional<IFocusedParameter> focusedParameter = this.model.getFocusedParameter ();
        final int paramValue = focusedParameter.isPresent () ? focusedParameter.get ().getValue () : 0;
        final String paramStr = focusedParameter.isPresent () ? focusedParameter.get ().getDisplayedValue () : " ";
        this.pageCache.updateValue (0, 4, paramValue, StringUtils.optimizeName (StringUtils.fixASCII (paramStr), 15));

        this.pageCache.updateValue (0, 5, this.transport.getMetronomeVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getMetronomeVolumeStr ()), 15));
        this.pageCache.updateValue (0, 6, this.project.getCueVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.project.getCueVolumeStr ()), 15));
        this.pageCache.updateValue (0, 7, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));

        final boolean isShifted = this.surface.isShiftPressed ();
        this.pageCache.updateElement (1, 2, isShifted ? "RECORD" : "PLAY", isShifted ? ColorEx.RED : ColorEx.GREEN, Boolean.TRUE);

        this.pageCache.flush ();
    }
}