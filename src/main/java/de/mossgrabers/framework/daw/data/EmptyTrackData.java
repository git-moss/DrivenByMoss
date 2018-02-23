// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
package de.mossgrabers.framework.daw.data;

/**
 * Default data for an empty track.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EmptyTrackData implements ITrack
{
    /** {@inheritDoc} */
    @Override
    public boolean doesExist ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getType ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getPosition ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isGroup ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isRecArm ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMonitor ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isAutoMonitor ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldNotes ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean canHoldAudioData ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getCrossfadeMode ()
    {
        return "AB";
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSelected ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isActivated ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getVolume ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedVolume ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr ()
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public int getPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public int getModulatedPan ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public double [] getColor ()
    {
        return new double []
        {
            0.0,
            0.0,
            0.0
        };
    }


    /** {@inheritDoc} */
    @Override
    public boolean isMute ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isSolo ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public int getVu ()
    {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public ISend [] getSends ()
    {
        return new ISend [0];
    }


    /** {@inheritDoc} */
    @Override
    public ISlot [] getSlots ()
    {
        return new ISlot [0];
    }


    /** {@inheritDoc} */
    @Override
    public int getIndex ()
    {
        return -1;
    }


    /** {@inheritDoc} */
    @Override
    public void setSelected (final boolean isSelected)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public String getVolumeStr (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public String getPanStr (final int limit)
    {
        return "";
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public boolean isPlaying ()
    {
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void changeVolume (int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setVolume (double value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetVolume ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchVolume (boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setVolumeIndication (boolean indicate)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void changePan (int control)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setPan (double value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void resetPan ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void touchPan (boolean isBeingTouched)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setPanIndication (boolean indicate)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setColor (double red, double green, double blue)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setIsActivated (boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleIsActivated ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setMute (boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMute ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setSolo (boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleSolo ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setRecArm (boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleRecArm ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setMonitor (boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleMonitor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void setAutoMonitor (boolean value)
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void toggleAutoMonitor ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void select ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void makeVisible ()
    {
        // Intentionally empty
    }
}
