// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.bitwig;

import de.mossgrabers.framework.daw.IProject;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Project;


/**
 * Encapsulates the Project instance.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ProjectProxy implements IProject
{
    private Project     project;
    private Application application;


    /**
     * Constructor.
     *
     * @param project The project
     * @param application The application
     */
    public ProjectProxy (final Project project, final Application application)
    {
        this.project = project;
        this.application = application;

        this.application.projectName ().markInterested ();
    }


    /** {@inheritDoc} */
    @Override
    public void enableObservers (final boolean enable)
    {
        this.application.projectName ().setIsSubscribed (enable);
    }


    /** {@inheritDoc} */
    @Override
    public String getName ()
    {
        return this.application.projectName ().get ();
    }


    /** {@inheritDoc} */
    @Override
    public void previous ()
    {
        this.application.previousProject ();
    }


    /** {@inheritDoc} */
    @Override
    public void next ()
    {
        this.application.nextProject ();
    }


    /** {@inheritDoc} */
    @Override
    public void createSceneFromPlayingLauncherClips ()
    {
        this.project.createSceneFromPlayingLauncherClips ();
    }
}
