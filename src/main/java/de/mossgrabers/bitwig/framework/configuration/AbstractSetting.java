// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2023
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.bitwig.framework.configuration;

import de.mossgrabers.framework.configuration.ISetting;

import com.bitwig.extension.controller.api.Setting;


/**
 * Abstract base class for all Bitwig settings.
 *
 * @author Jürgen Moßgraber
 */
public abstract class AbstractSetting implements ISetting
{
    protected Setting setting;


    /**
     * Constructor.
     *
     * @param setting The Bitwig setting
     */
    protected AbstractSetting (final Setting setting)
    {
        this.setting = setting;
    }


    /** {@inheritDoc} */
    @Override
    public void setEnabled (final boolean enable)
    {
        if (enable)
            this.setting.enable ();
        else
            this.setting.disable ();
    }


    /** {@inheritDoc} */
    @Override
    public void setVisible (final boolean visible)
    {
        if (visible)
            this.setting.show ();
        else
            this.setting.hide ();
    }
}
