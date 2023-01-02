// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.slmkiii.controller;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.grid.LightGuideImpl;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.utils.KeyManager;
import de.mossgrabers.framework.view.AbstractPlayView;


/**
 * Implementation of the SLMkIII light guide.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SLMkIIILightGuide extends LightGuideImpl
{
    private final IModel     model;
    private final Scales     keyboardScales;
    private final KeyManager keyboardManager;


    /**
     * Constructor.
     *
     * @param model The model
     * @param colorManager The color manager for accessing specific colors to use
     * @param output The MIDI output which can address the pad states
     */
    public SLMkIIILightGuide (final IModel model, final ColorManager colorManager, final IMidiOutput output)
    {
        super (0, 61, colorManager, output);

        this.model = model;

        this.keyboardScales = new Scales (model.getValueChanger (), 36, 36 + 61, 61, 1);
        this.keyboardScales.setChromatic (true);
        this.keyboardManager = new KeyManager (this.model, this.keyboardScales, this);
        this.keyboardManager.setNoteMatrix (this.keyboardScales.getNoteMatrix ());

        final ITrackBank tb = model.getTrackBank ();
        tb.addSelectionObserver ( (index, isSelected) -> this.keyboardManager.clearPressedKeys ());
        tb.addNoteObserver (this.keyboardManager::call);
    }


    /** {@inheritDoc} */
    @Override
    protected void sendNoteState (final int channel, final int note, final int color)
    {
        this.output.sendNoteEx (15, note, color);
    }


    /**
     * Disable/enable the light guide.
     *
     * @param enable True to enable
     */
    public void setActive (final boolean enable)
    {
        this.output.sendSysex (new StringBuilder ("F0 00 20 29 02 0A 01 05 0").append (enable ? '1' : '0').append (" F7").toString ());
    }


    /**
     * Draw the light guide.
     *
     * @param isEnabled True if enabled
     */
    public void draw (final boolean isEnabled)
    {
        final boolean isRecording = this.model.hasRecordingState ();

        final Scales scales = this.model.getScales ();

        this.keyboardScales.setScaleOffsetByIndex (scales.getScaleOffsetIndex ());
        this.keyboardScales.setScale (scales.getScale ());

        final ITrack cursorTrack = this.model.getCursorTrack ();
        for (int i = this.keyboardScales.getStartNote (); i < this.keyboardScales.getEndNote (); i++)
            this.light (i - 36, this.getGridColor (isEnabled, isRecording, cursorTrack, i));
    }


    /**
     * Mark selected notes immediately for better performance.
     *
     * @param key The pressed/released key
     * @param velocity The velocity
     */
    public void updateKeyboardNote (final int key, final int velocity)
    {
        final int note = this.keyboardManager.map (key);
        if (note != -1)
            this.keyboardManager.setAllKeysPressed (note, velocity);
    }


    private String getGridColor (final boolean isEnabled, final boolean isRecording, final ITrack track, final int note)
    {
        if (!isEnabled)
            return AbstractPlayView.COLOR_OFF;

        if (this.keyboardManager.isKeyPressed (note))
            return isRecording ? AbstractPlayView.COLOR_RECORD : AbstractPlayView.COLOR_PLAY;

        return AbstractView.replaceOctaveColorWithTrackColor (track, this.keyboardManager.getColor (note));
    }
}
