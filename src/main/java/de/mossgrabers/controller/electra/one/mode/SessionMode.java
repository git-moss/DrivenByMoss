// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.electra.one.mode;

import de.mossgrabers.controller.electra.one.controller.ElectraOneColorManager;
import de.mossgrabers.controller.electra.one.controller.ElectraOneControlSurface;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.daw.data.ISlot;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ISceneBank;
import de.mossgrabers.framework.daw.data.bank.ISlotBank;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.parameter.PlayPositionParameter;
import de.mossgrabers.framework.parameterprovider.IParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.CombinedParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.EmptyParameterProvider;
import de.mossgrabers.framework.parameterprovider.special.FixedParameterProvider;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.utils.StringUtils;


/**
 * The session mode. Provides 5 scenes and 5x5 clips.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionMode extends AbstractElectraOneMode
{
    private static final int   FIRST_TRACK_GROUP    = 550;

    /** The color for a scene. */
    public static final String COLOR_SCENE          = "COLOR_SCENE";
    /** The color for a selected scene. */
    public static final String COLOR_SELECTED_SCENE = "COLOR_SELECTED_SCENE";
    /** The color for no scene. */
    public static final String COLOR_SCENE_OFF      = "COLOR_SELECTED_OFF";

    private final ITransport   transport;
    private final IMasterTrack masterTrack;


    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public SessionMode (final ElectraOneControlSurface surface, final IModel model)
    {
        super (5, "Session", surface, model);

        this.transport = this.model.getTransport ();
        this.masterTrack = this.model.getMasterTrack ();

        final IParameterProvider emptyParameterProvider = new EmptyParameterProvider (5);
        final IParameterProvider emptyParameterProvider2 = new EmptyParameterProvider (1);

        this.setParameterProvider (new CombinedParameterProvider (
                // Row 1
                emptyParameterProvider, new FixedParameterProvider (this.masterTrack.getVolumeParameter ()),
                // Row 2
                emptyParameterProvider, new FixedParameterProvider (new PlayPositionParameter (model.getValueChanger (), this.transport, surface)),
                // Row 3
                emptyParameterProvider, emptyParameterProvider2,
                // Row 4
                emptyParameterProvider, emptyParameterProvider2,
                // Row 5
                emptyParameterProvider, emptyParameterProvider2,
                // Row 6
                emptyParameterProvider, emptyParameterProvider2));
    }


    /** {@inheritDoc} */
    @Override
    public void onButton (final int row, final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;

        if (index == 5)
        {
            switch (row)
            {
                case 2:
                    // TODO
                    break;
                case 3:
                    // TODO
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
        }

        // Scenes
        if (row == 0)
        {
            this.model.getSceneBank ().getItem (index).launch ();
            return;
        }

        final ITrack track = this.model.getTrackBank ().getItem (row - 1);
        if (!track.doesExist ())
            return;
        track.getSlotBank ().getItem (index).launch ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final ISceneBank sceneBank = this.model.getSceneBank ();

        for (int column = 0; column < 5; column++)
        {
            final IScene scene = sceneBank.getItem (column);
            this.pageCache.updateGroupLabel (FIRST_TRACK_GROUP + column, scene.doesExist () ? scene.getPosition () + 1 + ": " + scene.getName () : "");
            this.pageCache.updateElement (0, column, scene.getName (), scene.getColor (), Boolean.valueOf (scene.doesExist ()));

            for (int row = 1; row < 6; row++)
            {
                final ITrack t = tb.getItem (row - 1);
                final boolean isArmed = t.isRecArm ();

                final ISlotBank slotBank = t.getSlotBank ();
                final ISlot slot = slotBank.getItem (column);
                this.pageCache.updateElement (row, column, slot.getName (), this.getPadColor (slot, isArmed), Boolean.valueOf (slot.doesExist ()));
            }
        }

        // Master
        this.pageCache.updateColor (0, 5, this.masterTrack.getColor ());
        this.pageCache.updateValue (0, 5, this.masterTrack.getVolume (), StringUtils.optimizeName (StringUtils.fixASCII (this.masterTrack.getVolumeStr ()), 15));
        this.pageCache.updateValue (1, 5, 0, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getBeatText ()), 15));
        this.pageCache.updateElement (1, 5, StringUtils.optimizeName (StringUtils.fixASCII (this.transport.getPositionText ()), 15), null, null);

        // Transport
        this.pageCache.updateColor (4, 5, this.transport.isRecording () ? ElectraOneColorManager.RECORD_ON : ElectraOneColorManager.RECORD_OFF);
        this.pageCache.updateColor (5, 5, this.transport.isPlaying () ? ElectraOneColorManager.PLAY_ON : ElectraOneColorManager.PLAY_OFF);

        this.pageCache.flush ();
    }


    /**
     * Get the pad color for a slot.
     *
     * @param slot The slot
     * @param isArmed True if armed
     * @return The light info
     */
    public ColorEx getPadColor (final ISlot slot, final boolean isArmed)
    {
        if (slot.isRecordingQueued ())
            return ColorEx.DARK_RED;

        if (slot.isRecording ())
            return ColorEx.RED;

        if (slot.isPlayingQueued ())
            return ColorEx.DARK_GREEN;

        if (slot.isPlaying ())
            return ColorEx.GREEN;

        if (slot.hasContent ())
            return slot.getColor ();

        return slot.doesExist () && isArmed && this.surface.getConfiguration ().isDrawRecordStripe () ? ColorEx.RED_WINE : ColorEx.BLACK;
    }
}