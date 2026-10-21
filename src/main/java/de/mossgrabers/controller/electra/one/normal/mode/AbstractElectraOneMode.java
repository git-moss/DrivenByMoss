// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2026
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.normal.mode;

import de.mossgrabers.controller.electra.one.base.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.base.mode.AbstractElectraOneBaseMode;
import de.mossgrabers.controller.electra.one.normal.ElectraOneConfiguration;
import de.mossgrabers.controller.electra.one.normal.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * Abstract base mode for all Electra One modes.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractElectraOneMode extends AbstractElectraOneBaseMode<ElectraOneControlSurface, ElectraOneConfiguration>
{
    private static final int                                                       GROUP_OFFSET = 500;

    protected final ITransport                                                     transport;
    protected final IMasterTrack                                                   masterTrack;
    protected final PlayCommand<ElectraOneControlSurface, ElectraOneConfiguration> playCommand;

    private int                                                                    timerCounter = 0;


    /**
     * Constructor.
     *
     * @param pageIndex The index of the preset page where the mode is located
     * @param name The name of the mode
     * @param surface The control surface
     * @param model The model
     */
    protected AbstractElectraOneMode (final int pageIndex, final String name, final ElectraOneControlSurface surface, final IModel model)
    {
        super (pageIndex, name, surface, model, 6, 6, ElectraOneControlSurface.KNOB_IDS, GROUP_OFFSET);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        this.playCommand = new PlayCommand<> (model, surface);
    }


    /**
     * Update the master volume, transport position and play/record state.
     */
    protected void updateMasterColumn ()
    {
        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));

        if (this.timerCounter > 10)
        {
            this.pageCache.updateValue (1, 5, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
            this.pageCache.updateElement (1, 5, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getPositionText ()), 15), null, null);
            this.timerCounter = 0;
        }
        this.timerCounter++;

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);
    }
}
