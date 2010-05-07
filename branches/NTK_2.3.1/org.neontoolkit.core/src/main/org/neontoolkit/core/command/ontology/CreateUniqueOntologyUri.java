/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command.ontology;

import org.neontoolkit.core.Messages;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;

/*
 * Created on 12.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class CreateUniqueOntologyUri extends DatamodelCommand {
	private String _uniqueId;
	
	/**
	 * 
	 */
	public CreateUniqueOntologyUri(String project) {
		super(project);
	}
	
	/* (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
	 */
	@Override
	protected void perform() throws CommandException {
		if (getProjectName().equals("")) { //$NON-NLS-1$
			_uniqueId = ""; //$NON-NLS-1$
			return;
		}
        String tmp = "http://www.NewOnto"; //$NON-NLS-1$
        String tmp2 = ".org/ontology"; //$NON-NLS-1$
        int cnt = 1;
        String newOntoUri = tmp + cnt + tmp2 + cnt;
        try {
        	IOntologyProject ontoProject = getOntologyProject();
            while (ontoProject.getAvailableOntologyURIs().contains(newOntoUri)) {
                if (cnt > 100) {
                	_uniqueId = ""; //$NON-NLS-1$
                    return;
                }
                cnt++;
                newOntoUri = tmp + cnt + tmp2 + cnt;
            }
            _uniqueId = newOntoUri;
        } catch (NeOnCoreException e) {
        	NeOnCorePlugin.getDefault().logError(Messages.CreateUniqueOntologyUri_1, e); 
        }
	}
	
	public String getOntologyUri() {
		try {
			return getUniqueId();
		} catch (CommandException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getUniqueId() throws CommandException {
		if (_uniqueId == null) {
			run();
		}
		return _uniqueId;
	}

	/* (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#getLoggedArguments()
	 */
	@Override
	protected Object[] getLoggedArguments() {
		return new Object[]{getProjectName()};
	}
}
