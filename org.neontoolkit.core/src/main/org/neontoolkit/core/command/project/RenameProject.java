/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.LoggedCommand;

public class RenameProject extends LoggedCommand {
	
	public RenameProject(String oldProjectName, String newProjectname) {
		super(oldProjectName, newProjectname);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
	 */
	@Override
	protected void perform() throws CommandException {
		String oldName = (String)getArgument(0);
		String newName = (String)getArgument(1);
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IProject project = workspace.getRoot().getProject(oldName);
        if (project == null) {
        	return;
        }
        IProjectDescription description;
        try {
            description = project.getDescription();
            description.setName(newName);
            project.move(description, true, new NullProgressMonitor());
        } catch (CoreException e) {
            throw new CommandException(e);
        }
    }
}
