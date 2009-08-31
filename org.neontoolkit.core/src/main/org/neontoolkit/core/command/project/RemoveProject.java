/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.core.command.project;

import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.LoggedCommand;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;

/*
 * Created on 13.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class RemoveProject extends LoggedCommand {
	
	public RemoveProject(String project, boolean removeFromDatamodel) {
		super(project, removeFromDatamodel);
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.core.command.LoggedCommand#perform()
	 */
	@Override
	protected void perform() throws CommandException {
	    try {
    		String projectName = (String)getArgument(0);
    		boolean removeFromDatamodel = (Boolean)getArgument(1);
    		IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(projectName);
    		project.dispose(removeFromDatamodel);
	    } catch (NeOnCoreException e) {
	        throw new CommandException(e);
	    }
	}
}
