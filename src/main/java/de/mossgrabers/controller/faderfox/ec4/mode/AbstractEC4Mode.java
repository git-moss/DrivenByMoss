// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.faderfox.ec4.mode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.mossgrabers.controller.faderfox.ec4.EC4Configuration;
import de.mossgrabers.controller.faderfox.ec4.controller.EC4ControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.IProject;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IItem;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.bank.IBank;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.AbstractParameterMode;
import de.mossgrabers.framework.featuregroup.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.parameter.TempoParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;


/**
 * Abstract base mode for all EC4 modes.
 *
 * @param <B> The specific item contained in the bank
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractEC4Mode<B extends IItem> extends AbstractParameterMode<EC4ControlSurface, EC4Configuration, B>
{
    protected final IParameterProvider                             bottomRowProvider;

    private final PlayCommand<EC4ControlSurface, EC4Configuration> playCommand;
    protected int []                                               valueCache     = new int [16];
    protected boolean                                              isSession      = false;
    protected boolean                                              wasSessionUsed = false;


    /**
     * Constructor.
     *
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     * @param bank A bank from which to take the parameters
     */
    protected AbstractEC4Mode (final String name, final EC4ControlSurface surface, final IModel model, final IBank<B> bank)
    {
        super (name, surface, model, false, bank, EC4ControlSurface.KNOB_IDS);

        this.playCommand = new PlayCommand<> (model, surface);

        final ITransport transport = model.getTransport ();
        this.bottomRowProvider = new CombinedParameterProvider (
                // Column 1
                new FixedParameterProvider (new TempoParameter (model.getValueChanger (), transport, surface)),
                // Column 2
                new FixedParameterProvider (transport.getCrossfadeParameter ()),
                // Column 3
                new FixedParameterProvider (model.getProject ().getCueVolumeParameter ()),
                // Column 4
                new FixedParameterProvider (model.getMasterTrack ().getVolumeParameter ()));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (row == 3)
        {
            final ModeManager modeManager = this.surface.getModeManager ();

            switch (index)
            {
                case 0:
                    if (event == ButtonEvent.DOWN)
                    {
                        if (this.surface.isShiftPressed ())
                        {
                            this.model.getTransport ().setTempo (120);
                            return;
                        }
                        this.wasSessionUsed = false;
                        this.isSession = !this.isSession;
                    }
                    else if (event == ButtonEvent.UP)
                    {
                        if (this.isSession && this.wasSessionUsed)
                            this.isSession = false;
                        else
                            this.notifyTotalDisplay ("Scene: " + (this.isSession ? "on" : "off"));
                    }
                    break;

                case 1:
                    if (event == ButtonEvent.DOWN)
                    {
                        if (this.surface.isShiftPressed ())
                            this.model.getTransport ().getCrossfadeParameter ().resetValue ();
                        else
                            modeManager.setActive (modeManager.isActive (Modes.TRACK) ? Modes.DEVICE_PARAMS : Modes.TRACK);
                    }
                    break;

                case 2:
                    if (event != ButtonEvent.DOWN)
                        return;
                    if (this.surface.isShiftPressed ())
                    {
                        this.model.getProject ().resetCueVolume ();
                        return;
                    }
                    if (modeManager.isActive (Modes.TRACK_DETAILS))
                    {
                        modeManager.setActive (Modes.TRACK);
                        this.notifyTotalDisplay ("Selected Track");
                    }
                    else
                    {
                        modeManager.setActive (Modes.TRACK_DETAILS);
                        this.notifyTotalDisplay ("12 Mode");
                    }
                    break;

                case 3:
                    if (this.surface.isShiftPressed ())
                        this.model.getMasterTrack ().resetVolume ();
                    else
                        this.playCommand.execute (event, event == ButtonEvent.DOWN ? 127 : 0);
                    break;

                default:
                    // There are no more
                    break;
            }
        }
        else if (this.isSession)
        {
            this.wasSessionUsed = true;
            final IScene scene = this.model.getSceneBank ().getItem (row * 4 + index);
            if (scene.doesExist ())
                scene.launch (event == ButtonEvent.DOWN, this.surface.isShiftPressed ());
        }
    }


    protected void updateDisplayRow4 (final ITextDisplay display, final List<String []> totalDisplayInfo, final String lastItem)
    {
        display.setCell (3, 0, "Tmpo").setCell (3, 1, "Xfde").setCell (3, 2, "Cue ").setCell (3, 3, lastItem);

        final IMidiOutput output = this.surface.getMidiOutput ();
        final ITransport transport = this.model.getTransport ();
        final IProject project = this.model.getProject ();
        final IMasterTrack masterTrack = this.model.getMasterTrack ();

        final double tempo = transport.getTempo ();
        final int tempoAsInt = (int) (tempo * 100.0);
        if (this.valueCache[12] != tempoAsInt && tempoAsInt >= 2000)
        {
            this.valueCache[12] = tempoAsInt;
            output.sendCCEx (15, EC4ControlSurface.EC4_KNOB_1 + 12, (int) ((tempoAsInt - 2000.0) / 64600.0 * 127.0));
            this.storeLines (totalDisplayInfo, "Tempo: " + transport.formatTempo (tempo));
        }

        this.updateCache (13, transport.getCrossfade (), totalDisplayInfo, "Crossfade: " + transport.getCrossfadeParameter ().getDisplayedValue ());
        this.updateCache (14, project.getCueVolume (), totalDisplayInfo, "Cue Vol.: " + project.getCueVolumeStr ());
        this.updateCache (15, masterTrack.getVolume (), totalDisplayInfo, "Master: " + masterTrack.getVolumeStr ());
    }


    protected void updateCache (final int index, final int value, final List<String []> totalDisplayInfo, final String... lines)
    {
        if (this.valueCache[index] == value)
            return;

        this.valueCache[index] = value;
        final IMidiOutput output = this.surface.getMidiOutput ();
        output.sendCCEx (15, EC4ControlSurface.EC4_KNOB_1 + index, (int) (value * 127.0 / 1024.0));

        this.storeLines (totalDisplayInfo, lines);
    }


    protected void storeLines (final List<String []> totalDisplayInfo, final String... lines)
    {
        if (lines.length == 4)
            totalDisplayInfo.add (lines);
        else
        {
            final String [] linesNew = new String [4];
            Arrays.fill (linesNew, "");
            System.arraycopy (lines, 0, linesNew, 0, Math.min (3, lines.length));
            totalDisplayInfo.add (lines);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();

        Arrays.fill (this.valueCache, -1);
    }


    protected void notifyTotalDisplay (final String message)
    {
        this.surface.fillTotalDisplay (Collections.singletonList (new String []
        {
            message,
            "",
            "",
            ""
        }));
    }
}
