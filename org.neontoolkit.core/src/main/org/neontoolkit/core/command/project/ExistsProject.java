/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.core.command.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.LoggedCommand;
import org.neontoolkit.core.command.CommandException;

/*
 * Created on 13.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class ExistsProject extends LoggedCommand {
	private Boolean _exists;
	
	public ExistsProject(String project) {
		super(project);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.core.command.LoggedCommand#perform()
	 */
	@Override
	protected void perform() {
		String projectName = (String)getArgument(0);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject[] projects = root.getProjects();
        for (int i = 0; i < projects.length; i++) {
            if (projects[i].isOpen() && projectName.equalsIgnoreCase(projects[i].getName())) {
                _exists = true;
                return;
            }
        }
        _exists = false;
	}
	
	public boolean exists() {
		if (_exists == null) {
			try {
				run();
			} catch (CommandException e) {
				// Should never occur
				NeOnCorePlugin.getDefault().logError("", e); //$NON-NLS-1$
			}
		}
		return _exists;
	}

}
