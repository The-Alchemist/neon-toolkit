/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.io.command;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.io.util.ImportExportUtils;


public class GetOntologyFileFormat extends DatamodelCommand {
	
	private String _ontologyFileFormat;


	public GetOntologyFileFormat(String project, String physicalUri) {
		super(project, physicalUri);
	}

	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
	 */
	@Override
	public void perform() throws CommandException {
    	try {
    		String physicalUri = (String) super.getArgument(1);
    		_ontologyFileFormat = ImportExportUtils.getOntologyFileFormat(getOntologyProject().getName(), physicalUri);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommandException(e);
        }
	}
	
    public String getOntologyFileFormat() throws CommandException {
    	if(_ontologyFileFormat == null) {
    		run();
    	}
    	return _ontologyFileFormat;
    }
}
